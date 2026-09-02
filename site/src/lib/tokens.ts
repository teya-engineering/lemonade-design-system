/**
 * Reads the Figma variable exports in `/tokens` at build time and reshapes them
 * into something the documentation pages can render.
 *
 * The exports are pulled in with `import.meta.glob` so Vite inlines them during
 * the build. Reading them with `node:fs` instead does not survive bundling — the
 * compiled module is emitted into `dist/`, and any path relative to
 * `import.meta.url` resolves from there rather than from `src/`.
 *
 * Reaching outside `site/` requires `vite.server.fs.allow` in astro.config.mjs
 * for the dev server.
 */
/**
 * A single token, normalised out of the Figma native export.
 *
 * That export is DTCG-shaped: a nested object per collection, keyed by token
 * name, with `$value`/`$type` on the leaves and Figma's own metadata under
 * `$extensions`. Modes are no longer inside one file — light and dark are
 * separate files that name themselves via `com.figma.modeName`.
 */
export interface RawVariable {
	/** Slash-delimited path, e.g. "Background/Voice/bg-critical". */
	name: string;
	description: string;
	type: 'COLOR' | 'FLOAT' | 'STRING';
	value: number | string | RawColor;
	codeSyntax: { ANDROID?: string; iOS?: string };
	hiddenFromPublishing: boolean;
	/** The primitive this token points at, e.g. "neutral-alpha-900". */
	alias?: string;
}

export interface RawColor {
	hex: string;
	alpha: number;
}

interface RawLeaf {
	$type?: string;
	$value?: unknown;
	$description?: string;
	$extensions?: Record<string, unknown>;
}

const TOKEN_TYPES: Record<string, RawVariable['type']> = {
	color: 'COLOR',
	number: 'FLOAT',
	string: 'STRING',
};

const exports = import.meta.glob<Record<string, unknown>>('../../../tokens/*.tokens.json', {
	eager: true,
	import: 'default',
});

/** Keyed by bare filename, e.g. "spacing.tokens.json". */
const files = new Map<string, Record<string, unknown>>(
	Object.entries(exports).map(([path, contents]) => [path.split('/').pop()!, contents]),
);

/**
 * Pure walk of one export's tree into a flat list. Split out from `load()` so
 * the shape can be exercised in tests without a file on disk.
 */
export function flattenTokens(tree: Record<string, unknown>): RawVariable[] {
	const out: RawVariable[] = [];

	const visit = (node: unknown, path: string[]): void => {
		if (typeof node !== 'object' || node === null) return;
		const leaf = node as RawLeaf;

		if ('$value' in leaf) {
			const extensions = (leaf.$extensions ?? {}) as Record<string, never>;
			const type = TOKEN_TYPES[leaf.$type ?? ''];
			if (!type) {
				throw new Error(
					`Token "${path.join('/')}" has unsupported $type "${leaf.$type}". ` +
						`Known: ${Object.keys(TOKEN_TYPES).join(', ')}.`,
				);
			}
			const raw = leaf.$value as Record<string, unknown>;
			const alias = extensions['com.figma.aliasData'] as
				{ targetVariableName?: string } | undefined;
			out.push({
				name: path.join('/'),
				description: leaf.$description ?? '',
				type,
				value:
					type === 'COLOR'
						? { hex: String(raw.hex), alpha: Number(raw.alpha ?? 1) }
						: (leaf.$value as number | string),
				codeSyntax: (extensions['com.figma.codeSyntax'] ?? {}) as RawVariable['codeSyntax'],
				hiddenFromPublishing: extensions['com.figma.hiddenFromPublishing'] === true,
				alias: alias?.targetVariableName?.replace(/\//g, '-'),
			});
			return;
		}

		for (const [key, child] of Object.entries(node)) {
			// `$extensions` at group level carries the mode name, not a token.
			if (key.startsWith('$')) continue;
			visit(child, [...path, key]);
		}
	};

	visit(tree, []);
	return out;
}

function load(file: string): RawVariable[] {
	const contents = files.get(file);
	if (!contents) {
		throw new Error(`No token export named "${file}". Available: ${[...files.keys()].join(', ')}`);
	}
	return flattenTokens(contents);
}

function toCssColor(value: RawVariable['value']): string {
	if (typeof value !== 'object') return String(value);
	const alpha = value.alpha;
	if (alpha >= 0.999) return value.hex.toLowerCase();
	const byte = Math.round(alpha * 255)
		.toString(16)
		.padStart(2, '0');
	return `${value.hex.toLowerCase()}${byte}`;
}

export interface ColorToken {
	/** Leaf name, e.g. `bg-neutral-subtle`. */
	name: string;
	/** Ancestry above the leaf, e.g. `['Background', 'Voice']`. */
	path: string[];
	description: string;
	light: string;
	dark: string;
	/** True when light and dark resolve to the same value. */
	fixed: boolean;
	/** The primitive this aliases in the light theme, e.g. "neutral-50". */
	alias?: string;
	/** The primitive it aliases in dark, when that differs. */
	darkAlias?: string;
	android?: string;
	ios?: string;
}

export interface ColorGroup {
	label: string;
	subgroups: Array<{ label: string | null; tokens: ColorToken[] }>;
}

export function themeColors(): ColorGroup[] {
	// Light and dark are separate exports now, so they are paired by token path
	// rather than by mode id.
	const lightTokens = load('theme-colors.light.tokens.json');
	const darkByName = new Map(load('theme-colors.dark.tokens.json').map((v) => [v.name, v]));

	const tokens: ColorToken[] = lightTokens
		.filter((v) => !v.hiddenFromPublishing)
		.map((v) => {
			const segments = v.name.split('/');
			const name = segments.pop()!;
			const dark = darkByName.get(v.name);
			if (!dark) {
				throw new Error(
					`"${v.name}" is in the light theme export but not the dark one. The two ` +
						'files have drifted; re-export both from Figma.',
				);
			}
			const lightValue = toCssColor(v.value);
			const darkValue = toCssColor(dark.value);
			return {
				name,
				path: segments,
				description: v.description,
				light: lightValue,
				dark: darkValue,
				fixed: lightValue === darkValue,
				alias: v.alias,
				darkAlias: dark.alias,
				android: v.codeSyntax.ANDROID,
				ios: v.codeSyntax.iOS,
			};
		});

	const groups = new Map<string, Map<string | null, ColorToken[]>>();
	for (const token of tokens) {
		const [group = 'Other', subgroup = null] = token.path;
		if (!groups.has(group)) groups.set(group, new Map());
		const subgroups = groups.get(group)!;
		if (!subgroups.has(subgroup)) subgroups.set(subgroup, []);
		subgroups.get(subgroup)!.push(token);
	}

	// Ungrouped tokens first within a group, then named subgroups alphabetically.
	return [...groups.entries()]
		.sort(([a], [b]) => a.localeCompare(b))
		.map(([label, subgroups]) => ({
			label,
			subgroups: [...subgroups.entries()]
				.sort(([a], [b]) => (a === null ? -1 : b === null ? 1 : a.localeCompare(b)))
				.map(([subLabel, tokens]) => ({
					label: subLabel,
					tokens: tokens.sort((x, y) => x.name.localeCompare(y.name)),
				})),
		}));
}

/**
 * Resolves a list of leaf token names against an already-flattened token
 * list, in the order given. Used anywhere (e.g. the landing page's palette
 * sample) that hard-codes a handful of token names rather than rendering a
 * whole group — a plain `.find()` per name would silently drop a renamed or
 * unpublished token and never notice. Matches the project's fail-loud policy
 * for token schema changes: throws naming every name that did not resolve,
 * so a rename or a token going `hiddenFromPublishing` fails the build
 * instead of quietly shrinking the caller's output.
 */
export function resolveColorTokens(names: string[], tokens: ColorToken[]): ColorToken[] {
	const resolved = names.map((name) => tokens.find((t) => t.name === name));
	const missing = names.filter((_, i) => !resolved[i]);
	if (missing.length > 0) {
		throw new Error(
			`Token(s) not found in theme-colors.light.tokens.json: ${missing.join(', ')}. They may have ` +
				'been renamed or marked hiddenFromPublishing — update the caller to match.',
		);
	}
	return resolved as ColorToken[];
}

/**
 * Every top-level colour group the Colour page is expected to render, kept in
 * sync with the `<ColorTokens group="..." />` sections in colour.mdx. Mirrors
 * SHADOW_ORDER below: a hard-coded expectation the export is checked against,
 * so a newly exported Figma group fails the build instead of silently never
 * appearing on the page.
 */
const RENDERED_COLOR_GROUPS = [
	'Background',
	'Border',
	'Content',
	'Interaction',
	'Scoped',
	'Shadow',
];

/**
 * Pure comparison step, split out from `assertAllColorGroupsRendered()` so it
 * can be exercised in tests without needing a malformed `theme-colors.light.tokens.json`
 * on disk (the real export is well-formed, so it can never trigger the throw
 * itself).
 */
export function unrenderedColorGroups(exportedLabels: string[]): string[] {
	return exportedLabels.filter((label) => !RENDERED_COLOR_GROUPS.includes(label));
}

/**
 * Fails the build if `theme-colors.light.tokens.json` contains a top-level group the
 * Colour page does not render. The project rule is that a token schema
 * change must fail the build rather than silently empty a page — without
 * this, a new Figma group ships with a green build and no signal.
 */
export function assertAllColorGroupsRendered(): void {
	const exportedLabels = themeColors().map((g) => g.label);
	const missing = unrenderedColorGroups(exportedLabels);
	if (missing.length > 0) {
		throw new Error(
			`theme-colors.light.tokens.json contains colour group(s) the Colour page does not render: ` +
				`${missing.join(', ')}. Add a <ColorTokens group="..."/> section for each in ` +
				'colour.mdx, and add the group name to RENDERED_COLOR_GROUPS in tokens.ts.',
		);
	}
}

/**
 * Looks up the theme-colors token reserved for surfaces that must read as
 * light in either theme (`Background/Fixed/bg-always-light`). Components
 * that need a guaranteed-light surface — e.g. rendering a shadow sample
 * composed from literal low-alpha black, which has no dark-theme counterpart
 * — should use this instead of a raw hex value, so the choice stays
 * traceable to a real design token rather than a magic constant.
 *
 * Pure lookup step, split out from `fixedLightSurface()` so the fail-loud
 * validation below can be exercised directly in tests without needing a
 * malformed `theme-colors.light.tokens.json` on disk (the real export is well-formed, so
 * it can never trigger this throw itself).
 */
export function findFixedLightSurface(
	variables: Array<Pick<RawVariable, 'name' | 'value'>>,
): string {
	const variable = variables.find((v) => v.name.endsWith('/bg-always-light'));
	if (!variable) {
		throw new Error('theme-colors.light.tokens.json is missing the "bg-always-light" token.');
	}
	return toCssColor(variable.value);
}

export function fixedLightSurface(): string {
	return findFixedLightSurface(load('theme-colors.light.tokens.json'));
}

export interface ScaleToken {
	name: string;
	value: number;
	android?: string;
	ios?: string;
}

/** Reads a single-mode numeric collection such as spacing or radius. */
export function scale(file: string, options: { prefix?: string } = {}): ScaleToken[] {
	return load(file)
		.filter((v) => !v.hiddenFromPublishing && v.type === 'FLOAT')
		.filter((v) => (options.prefix ? v.name.startsWith(options.prefix) : true))
		.map((v) => ({
			name: v.name.split('/').pop()!,
			value: Number(v.value),
			android: v.codeSyntax.ANDROID,
			ios: v.codeSyntax.iOS,
		}))
		.sort((a, b) => a.value - b.value);
}

export function fontSizes(): ScaleToken[] {
	return scale('typography.tokens.json', { prefix: 'font-size/' });
}

export function lineHeights(): ScaleToken[] {
	return scale('typography.tokens.json', { prefix: 'line-height/' });
}

/**
 * Figma stores a font weight as the typeface's style name, not a number, so
 * the export says "SemiBold" where CSS wants 600. This map is the only place
 * that translation lives.
 *
 * A style with no entry here fails the build rather than being guessed at or
 * dropped: a specimen rendered at the wrong weight looks deliberate, so no
 * one would catch it.
 */
const FONT_WEIGHT_SCALE: Record<string, number> = {
	Thin: 100,
	ExtraLight: 200,
	Light: 300,
	Regular: 400,
	Medium: 500,
	SemiBold: 600,
	Bold: 700,
	ExtraBold: 800,
	Black: 900,
};

export interface WeightToken {
	/** Leaf token name as exported, e.g. "bold". */
	name: string;
	/** The style name Figma and the platforms use, e.g. "Bold". */
	style: string;
	/** The CSS numeric weight, e.g. 700. */
	value: number;
}

/**
 * Pure mapping step, split out from `fontWeights()` so the fail-loud
 * validation can be exercised in tests without needing a malformed
 * `typography.json` on disk (the real export is well-formed, so it can never
 * trigger these throws itself).
 */
export function composeFontWeights(entries: Array<{ name: string; style: string }>): WeightToken[] {
	if (entries.length === 0) {
		throw new Error(
			'typography.json exported no font-weight tokens. The Typography page builds its ' +
				'Weights section from them, so an empty list renders an empty section with no ' +
				'other signal — which is exactly what this throw exists to prevent.',
		);
	}

	const unknown = entries.filter((entry) => !(entry.style in FONT_WEIGHT_SCALE));
	if (unknown.length > 0) {
		throw new Error(
			'typography.json uses font-weight style(s) with no CSS equivalent: ' +
				`${unknown.map((entry) => `${entry.name} = "${entry.style}"`).join(', ')}. ` +
				'Add them to FONT_WEIGHT_SCALE in tokens.ts.',
		);
	}

	return entries
		.map((entry) => ({ ...entry, value: FONT_WEIGHT_SCALE[entry.style]! }))
		.sort((a, b) => a.value - b.value);
}

/**
 * Font weights are STRING variables, so they cannot go through `scale()`,
 * which reads FLOATs. That mismatch is why this section rendered empty: the
 * type filter dropped all four tokens and nothing complained.
 */
export function fontWeights(): WeightToken[] {
	return composeFontWeights(
		load('typography.tokens.json')
			.filter((v) => !v.hiddenFromPublishing && v.type === 'STRING')
			.filter((v) => v.name.startsWith('font-weight/'))
			.map((v) => ({
				name: v.name.split('/').pop()!,
				style: String(v.value),
			})),
	);
}

export interface ShadowLevel {
	offsetX: number;
	offsetY: number;
	blur: number;
	spread: number;
	color: string;
}

export interface ShadowSet {
	name: string;
	levels: ShadowLevel[];
	/** Ready-to-use CSS box-shadow value. */
	css: string;
}

const SHADOW_ORDER = ['xsmall', 'small', 'medium', 'large', 'xlarge'];

/** The five properties every shadow level must resolve. No defaults — a
 *  partial export must fail loudly rather than render a plausible-looking
 *  but wrong shadow. */
const REQUIRED_SHADOW_PROPERTIES = ['offset-x', 'offset-y', 'blur', 'spread', 'color'];

/**
 * Pure composition step, split out from `shadowSets()` so the fail-loud
 * validation below can be exercised directly in tests without needing a
 * malformed `shadow.json` on disk (the real export is well-formed, so it
 * can never trigger these throws itself).
 */
export function composeShadowSizes(
	parts: Map<string, Map<string, Map<string, number | string>>>,
): ShadowSet[] {
	const unknownSizes = [...parts.keys()].filter((size) => !SHADOW_ORDER.includes(size));
	if (unknownSizes.length > 0) {
		throw new Error(
			`shadow.json contains size(s) not in SHADOW_ORDER: ${unknownSizes.join(', ')}. ` +
				'Update SHADOW_ORDER in tokens.ts to include the new size(s).',
		);
	}

	return SHADOW_ORDER.filter((size) => parts.has(size)).map((size) => {
		const levels = [...parts.get(size)!.entries()]
			.sort(([a], [b]) => a.localeCompare(b))
			.map(([level, props]) => {
				for (const property of REQUIRED_SHADOW_PROPERTIES) {
					if (!props.has(property)) {
						throw new Error(
							`shadow.json is missing "${property}" for "${size}" ${level}. ` +
								'Every shadow level needs all five properties (offset-x, ' +
								'offset-y, blur, spread, color) — check for a partial export.',
						);
					}
				}
				return {
					offsetX: Number(props.get('offset-x')),
					offsetY: Number(props.get('offset-y')),
					blur: Number(props.get('blur')),
					spread: Number(props.get('spread')),
					color: String(props.get('color')),
				};
			});

		const css = levels
			.map((l) => `${l.offsetX}px ${l.offsetY}px ${l.blur}px ${l.spread}px ${l.color}`)
			.join(', ');

		return { name: size, levels, css };
	});
}

export function shadowSets(): ShadowSet[] {
	// name is shadow/<size>/<level>/sd-<abbr>-lv<n>-<prop>
	const parts = new Map<string, Map<string, Map<string, number | string>>>();
	for (const variable of load('shadow.tokens.json')) {
		if (variable.hiddenFromPublishing) continue;
		const [, size, level, leaf] = variable.name.split('/');
		if (!size || !level || !leaf) continue;

		const property = leaf.replace(/^sd-[a-z]+-lv\d-/, '');
		const value = variable.type === 'COLOR' ? toCssColor(variable.value) : Number(variable.value);

		if (!parts.has(size)) parts.set(size, new Map());
		const levels = parts.get(size)!;
		if (!levels.has(level)) levels.set(level, new Map());
		levels.get(level)!.set(property, value);
	}

	return composeShadowSizes(parts);
}

/* ==========================================================================
   Semantic text styles

   These are the styles product code actually uses — `BodyMediumRegular`, not
   `font-size-400`. They are not in the Figma variable export: the export
   carries the scales, and the composite styles that combine them live in
   Kotlin, in the enum below, which is what both platforms ship.

   So this reads that enum at build time rather than restating it. The numbers
   still come from `typography.json` — the enum references scale tokens by
   name and they are resolved here — which means a page cannot disagree with
   either source.
   ========================================================================== */

const TEXT_STYLE_SOURCE =
	'../../../kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeTextStyle.kt';

const textStyleSources = import.meta.glob<string>(
	'../../../kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeTextStyle.kt',
	{ eager: true, query: '?raw', import: 'default' },
);

/** Longest-first, so `XSmall` cannot win against `XXSmall`. */
const STYLE_SIZES = [
	'3XLarge',
	'2XLarge',
	'XXSmall',
	'XLarge',
	'XSmall',
	'Medium',
	'Large',
	'Small',
];

const STYLE_WEIGHTS = ['Overline', 'Regular', 'SemiBold', 'Medium', 'Bold'];

const STYLE_GROUPS = ['Display', 'Heading', 'Body'];

export interface TextStyle {
	/** Enum entry name, e.g. "BodyMediumRegular". */
	name: string;
	/** Human reading of it, e.g. "Body Medium Regular". */
	label: string;
	/** "Display", "Heading", "Body" or "Overline". */
	group: string;
	/** Size step within the group, e.g. "Medium". */
	size: string;
	fontSize: number;
	lineHeight: number;
	fontWeight: number;
	letterSpacing?: number;
	/** The scale tokens this style is built from. */
	sizeToken: string;
	lineHeightToken: string;
	weightToken: string;
}

export interface TextStyleSubgroup {
	label?: string;
	styles: TextStyle[];
}

export interface TextStyleGroup {
	label: string;
	subgroups: TextStyleSubgroup[];
}

/**
 * Splits an enum entry name into its parts. Generic PascalCase splitting gets
 * `SemiBold` wrong, so the parts are matched against known vocabularies and an
 * unrecognised name throws rather than producing a plausible mislabel.
 */
export function parseTextStyleName(name: string): { group: string; size: string; weight: string } {
	const group = STYLE_GROUPS.find((g) => name.startsWith(g));
	if (!group) {
		throw new Error(
			`LemonadeTypography.${name} does not start with a known group ` +
				`(${STYLE_GROUPS.join(', ')}). Add it to STYLE_GROUPS in tokens.ts.`,
		);
	}

	let rest = name.slice(group.length);
	const size = STYLE_SIZES.find((s) => rest.startsWith(s));
	if (!size) {
		throw new Error(
			`LemonadeTypography.${name} has no known size step after "${group}". ` +
				`Known: ${STYLE_SIZES.join(', ')}. Add it to STYLE_SIZES in tokens.ts.`,
		);
	}

	rest = rest.slice(size.length);
	// Display and Heading carry no weight suffix; their weight is implied.
	const weight = rest === '' ? '' : STYLE_WEIGHTS.find((w) => w === rest);
	if (weight === undefined) {
		throw new Error(
			`LemonadeTypography.${name} ends in "${rest}", which is not a known weight. ` +
				`Known: ${STYLE_WEIGHTS.join(', ')}. Add it to STYLE_WEIGHTS in tokens.ts.`,
		);
	}

	return { group, size, weight };
}

interface RawTextStyle {
	name: string;
	sizeToken: string;
	lineHeightToken: string;
	weightToken: string;
	letterSpacing?: number;
}

/**
 * Pure parse step, split out so the fail-loud paths can be exercised in tests
 * without a malformed Kotlin file on disk.
 */
export function parseTextStyleSource(source: string): RawTextStyle[] {
	const entry =
		/^\s{4}(\w+)\(\s*LemonadeTextStyle\(\s*fontSize = LemonadeFontSizes\.(\w+)\.value,\s*lineHeight = LemonadeLineHeights\.(\w+)\.value,\s*fontWeight = LemonadeFontWeights\.(\w+)\.weight,\s*(?:letterSpacing = (-?[\d.]+)f,\s*)?\),\s*\),/gm;

	const styles: RawTextStyle[] = [];
	for (const match of source.matchAll(entry)) {
		styles.push({
			name: match[1]!,
			sizeToken: match[2]!,
			lineHeightToken: match[3]!,
			weightToken: match[4]!,
			letterSpacing: match[5] === undefined ? undefined : Number(match[5]),
		});
	}

	if (styles.length === 0) {
		throw new Error(
			`No LemonadeTypography entries parsed from ${TEXT_STYLE_SOURCE}. The enum's shape ` +
				'has changed, and the Typography page builds its semantic styles from it, so an ' +
				'empty list would silently blank that section. Update the parser in tokens.ts.',
		);
	}

	return styles;
}

/** `FontSize600` -> `font-size-600`, `LineHeight1200` -> `line-height-1200`. */
function scaleTokenName(reference: string): string {
	return reference
		.replace(/([a-z])([A-Z])/g, '$1-$2')
		.replace(/([A-Za-z])(\d)/g, '$1-$2')
		.toLowerCase();
}

/**
 * The semantic text styles, grouped the way the Foundations frame in Figma
 * presents them: Display, Heading, Body (split by size step) and Overline.
 */
export function semanticTextStyles(): TextStyleGroup[] {
	const path = Object.keys(textStyleSources)[0];
	if (!path) {
		throw new Error(
			`Could not read ${TEXT_STYLE_SOURCE}. The Typography page builds its semantic ` +
				'styles from that file; if it moved, update the glob in tokens.ts.',
		);
	}

	const sizes = new Map(fontSizes().map((t) => [t.name, t.value]));
	const heights = new Map(lineHeights().map((t) => [t.name, t.value]));
	const weights = new Map(fontWeights().map((t) => [t.name.toLowerCase(), t.value]));

	const styles = parseTextStyleSource(textStyleSources[path]!).map((raw) => {
		const { group, size, weight } = parseTextStyleName(raw.name);
		const sizeToken = scaleTokenName(raw.sizeToken);
		const lineHeightToken = scaleTokenName(raw.lineHeightToken);
		const weightToken = raw.weightToken.toLowerCase();

		const fontSize = sizes.get(sizeToken);
		const lineHeight = heights.get(lineHeightToken);
		const fontWeight = weights.get(weightToken);
		const missing = [
			fontSize === undefined ? sizeToken : null,
			lineHeight === undefined ? lineHeightToken : null,
			fontWeight === undefined ? `font-weight/${weightToken}` : null,
		].filter(Boolean);

		if (missing.length > 0) {
			throw new Error(
				`LemonadeTypography.${raw.name} references token(s) that typography.json does ` +
					`not export: ${missing.join(', ')}. The enum and the export have drifted.`,
			);
		}

		return {
			name: raw.name,
			label: [group, size, weight].filter(Boolean).join(' '),
			// Overline is its own group in Figma even though the enum files it under Body.
			group: weight === 'Overline' ? 'Overline' : group,
			size,
			fontSize: fontSize!,
			lineHeight: lineHeight!,
			fontWeight: fontWeight!,
			letterSpacing: raw.letterSpacing,
			sizeToken,
			lineHeightToken,
			weightToken,
		} satisfies TextStyle;
	});

	const order = ['Display', 'Heading', 'Body', 'Overline'];
	const bySize = (a: TextStyle, b: TextStyle) =>
		a.fontSize - b.fontSize || a.fontWeight - b.fontWeight;

	return order
		.map((label) => {
			const inGroup = styles.filter((s) => s.group === label).sort(bySize);
			if (inGroup.length === 0) return null;

			// Body is the only group deep enough to need a second level; the rest
			// read better as one grid.
			if (label !== 'Body') return { label, subgroups: [{ styles: inGroup }] };

			const steps = [...new Set(inGroup.map((s) => s.size))];
			return {
				label,
				subgroups: steps.map((step) => ({
					label: `Body ${step}`,
					styles: inGroup.filter((s) => s.size === step),
				})),
			};
		})
		.filter((g): g is TextStyleGroup => g !== null);
}
