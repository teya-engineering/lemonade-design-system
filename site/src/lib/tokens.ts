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
interface RawResolved {
	resolvedValue: number | string | { r: number; g: number; b: number; a?: number };
	alias: string | null;
	aliasName?: string;
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

interface RawCollection {
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

/** The single mode of a collection that only has one. */
function soleModeId(collection: RawCollection): string {
	const ids = Object.keys(collection.modes);
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

export interface ScaleToken {
	name: string;
	description: string;
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
			description: v.description,
			value: Number(v.resolvedValuesByMode[mode]!.resolvedValue),
			android: v.codeSyntax.ANDROID,
			ios: v.codeSyntax.iOS,
		}))
		.sort((a, b) => a.value - b.value);
}

export interface TypeToken extends ScaleToken {}

export function fontSizes(): TypeToken[] {
	return scale('typography.json', { prefix: 'font-size/' });
}

export function lineHeights(): TypeToken[] {
	return scale('typography.json', { prefix: 'line-height/' });
}

export function fontWeights(): TypeToken[] {
	return scale('typography.json', { prefix: 'font-weight/' });
}
