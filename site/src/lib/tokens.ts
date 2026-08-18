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
export interface RawResolved {
	resolvedValue: number | string | { r: number; g: number; b: number; a?: number };
}

interface RawVariable {
	id: string;
	name: string;
	description: string;
	type: 'COLOR' | 'FLOAT' | 'STRING';
	resolvedValuesByMode: Record<string, RawResolved>;
	codeSyntax: { ANDROID?: string; iOS?: string };
	hiddenFromPublishing: boolean;
}

export interface RawCollection {
	id: string;
	name: string;
	modes: Record<string, string>;
	variables: RawVariable[];
}

const exports = import.meta.glob<RawCollection>('../../../tokens/*.json', {
	eager: true,
	import: 'default',
});

/** Keyed by bare filename, e.g. "spacing.json". */
const collections = new Map<string, RawCollection>(
	Object.entries(exports).map(([path, collection]) => [path.split('/').pop()!, collection]),
);

function load(file: string): RawCollection {
	const collection = collections.get(file);
	if (!collection) {
		throw new Error(
			`No token export named "${file}". Available: ${[...collections.keys()].join(', ')}`,
		);
	}
	return collection;
}

/**
 * Figma re-exports have been known to carry stray modes, so modes are looked up
 * by their human name rather than by a hard-coded id.
 */
function modeId(collection: RawCollection, name: string): string {
	const found = Object.entries(collection.modes).find(
		([, label]) => label.toLowerCase() === name.toLowerCase(),
	);
	if (!found) {
		throw new Error(
			`Mode "${name}" not found in collection "${collection.name}". ` +
				`Available: ${Object.values(collection.modes).join(', ')}`,
		);
	}
	return found[0];
}

/**
 * The single mode of a collection that only has one. Figma re-exports have
 * carried stray extra modes before (see `theme-colors strip 3932:0`); silently
 * taking the first key would read one mode's values while claiming to read
 * the collection's only one, so this fails loud instead.
 */
export function soleModeId(collection: RawCollection): string {
	const ids = Object.keys(collection.modes);
	if (ids.length !== 1) {
		throw new Error(
			`Expected exactly one mode in collection "${collection.name}", found ${ids.length}: ` +
				`${Object.values(collection.modes).join(', ')}.`,
		);
	}
	return ids[0]!;
}

function toCssColor(value: RawResolved['resolvedValue']): string {
	if (typeof value !== 'object') return String(value);
	const channel = (n: number) =>
		Math.round(n * 255)
			.toString(16)
			.padStart(2, '0');
	const alpha = value.a ?? 1;
	const base = `#${channel(value.r)}${channel(value.g)}${channel(value.b)}`;
	return alpha < 0.999 ? `${base}${channel(alpha)}` : base;
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
	android?: string;
	ios?: string;
}

export interface ColorGroup {
	label: string;
	subgroups: Array<{ label: string | null; tokens: ColorToken[] }>;
}

export function themeColors(): ColorGroup[] {
	const collection = load('theme-colors.json');
	const light = modeId(collection, 'Light');
	const dark = modeId(collection, 'Dark');

	const tokens: ColorToken[] = collection.variables
		.filter((v) => !v.hiddenFromPublishing)
		.map((v) => {
			const segments = v.name.split('/');
			const name = segments.pop()!;
			const lightValue = toCssColor(v.resolvedValuesByMode[light]!.resolvedValue);
			const darkValue = toCssColor(v.resolvedValuesByMode[dark]!.resolvedValue);
			return {
				name,
				path: segments,
				description: v.description,
				light: lightValue,
				dark: darkValue,
				fixed: lightValue === darkValue,
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
			`Token(s) not found in theme-colors.json: ${missing.join(', ')}. They may have ` +
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
const RENDERED_COLOR_GROUPS = ['Background', 'Border', 'Content', 'Interaction', 'Scoped', 'Shadow'];

/**
 * Pure comparison step, split out from `assertAllColorGroupsRendered()` so it
 * can be exercised in tests without needing a malformed `theme-colors.json`
 * on disk (the real export is well-formed, so it can never trigger the throw
 * itself).
 */
export function unrenderedColorGroups(exportedLabels: string[]): string[] {
	return exportedLabels.filter((label) => !RENDERED_COLOR_GROUPS.includes(label));
}

/**
 * Fails the build if `theme-colors.json` contains a top-level group the
 * Colour page does not render. The project rule is that a token schema
 * change must fail the build rather than silently empty a page — without
 * this, a new Figma group ships with a green build and no signal.
 */
export function assertAllColorGroupsRendered(): void {
	const exportedLabels = themeColors().map((g) => g.label);
	const missing = unrenderedColorGroups(exportedLabels);
	if (missing.length > 0) {
		throw new Error(
			`theme-colors.json contains colour group(s) the Colour page does not render: ` +
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
 * malformed `theme-colors.json` on disk (the real export is well-formed, so
 * it can never trigger this throw itself).
 */
export function findFixedLightSurface(
	variables: Array<{ name: string; resolvedValuesByMode: Record<string, RawResolved> }>,
	light: string,
): string {
	const variable = variables.find((v) => v.name.endsWith('/bg-always-light'));
	if (!variable) {
		throw new Error('theme-colors.json is missing the "bg-always-light" token.');
	}
	return toCssColor(variable.resolvedValuesByMode[light]!.resolvedValue);
}

export function fixedLightSurface(): string {
	const collection = load('theme-colors.json');
	const light = modeId(collection, 'Light');
	return findFixedLightSurface(collection.variables, light);
}

export interface ScaleToken {
	name: string;
	value: number;
	android?: string;
	ios?: string;
}

/** Reads a single-mode numeric collection such as spacing or radius. */
export function scale(file: string, options: { prefix?: string } = {}): ScaleToken[] {
	const collection = load(file);
	const mode = soleModeId(collection);

	return collection.variables
		.filter((v) => !v.hiddenFromPublishing && v.type === 'FLOAT')
		.filter((v) => (options.prefix ? v.name.startsWith(options.prefix) : true))
		.map((v) => ({
			name: v.name.split('/').pop()!,
			value: Number(v.resolvedValuesByMode[mode]!.resolvedValue),
			android: v.codeSyntax.ANDROID,
			ios: v.codeSyntax.iOS,
		}))
		.sort((a, b) => a.value - b.value);
}

export function fontSizes(): ScaleToken[] {
	return scale('typography.json', { prefix: 'font-size/' });
}

export function lineHeights(): ScaleToken[] {
	return scale('typography.json', { prefix: 'line-height/' });
}

export function fontWeights(): ScaleToken[] {
	return scale('typography.json', { prefix: 'font-weight/' });
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
	const collection = load('shadow.json');
	const mode = soleModeId(collection);

	// name is shadow/<size>/<level>/sd-<abbr>-lv<n>-<prop>
	const parts = new Map<string, Map<string, Map<string, number | string>>>();
	for (const variable of collection.variables) {
		if (variable.hiddenFromPublishing) continue;
		const [, size, level, leaf] = variable.name.split('/');
		if (!size || !level || !leaf) continue;

		const property = leaf.replace(/^sd-[a-z]+-lv\d-/, '');
		const raw = variable.resolvedValuesByMode[mode]!.resolvedValue;
		const value = variable.type === 'COLOR' ? toCssColor(raw) : Number(raw);

		if (!parts.has(size)) parts.set(size, new Map());
		const levels = parts.get(size)!;
		if (!levels.has(level)) levels.set(level, new Map());
		levels.get(level)!.set(property, value);
	}

	return composeShadowSizes(parts);
}
