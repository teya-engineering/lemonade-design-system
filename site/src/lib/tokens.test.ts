import { describe, expect, it } from 'vitest';
import {
	themeColors,
	scale,
	fontSizes,
	fontWeights,
	composeFontWeights,
	shadowSets,
	composeShadowSizes,
	fixedLightSurface,
	findFixedLightSurface,
	unrenderedColorGroups,
	assertAllColorGroupsRendered,
	resolveColorTokens,
	soleModeId,
	type RawCollection,
	type ColorToken,
} from './tokens';

describe('themeColors', () => {
	it('reads tokens from the Figma export', () => {
		const groups = themeColors();
		const tokens = groups.flatMap((g) => g.subgroups.flatMap((s) => s.tokens));

		// Guards against a schema change that would leave the galleries empty
		// while the site still builds clean.
		expect(tokens.length).toBeGreaterThan(100);
	});

	it('resolves both themes for a known token', () => {
		const tokens = themeColors().flatMap((g) => g.subgroups.flatMap((s) => s.tokens));
		const bgDefault = tokens.find((t) => t.name === 'bg-default');

		expect(bgDefault).toBeDefined();
		expect(bgDefault!.light).toBe('#ffffff');
		expect(bgDefault!.dark).toBe('#201f1d');
	});

	it('carries every published Content token, not just some of them', () => {
		const content = themeColors().find((g) => g.label === 'Content')!;
		const contentTokens = content.subgroups.flatMap((s) => s.tokens);

		// Guards against #7: a schema change that silently drops a whole
		// top-level group would still pass a bare `> 100` check.
		//
		// A floor, not an exact count. Design adds semantic tokens routinely —
		// PR #326 added four across Content, Background, Border and Interaction —
		// and pinning the number turns every legitimate addition into a red
		// build, which trains people to bump the constant without reading it.
		// The failure this guards against is a group emptying out, not growing.
		expect(contentTokens.length).toBeGreaterThanOrEqual(28);
		expect(contentTokens.map((t) => t.name)).toEqual(
			expect.arrayContaining(['content-primary', 'content-secondary', 'content-tertiary']),
		);
	});

	it('renders every top-level group the export contains', () => {
		const groups = themeColors().map((g) => g.label).sort();

		expect(groups).toEqual(['Background', 'Border', 'Content', 'Interaction', 'Scoped', 'Shadow']);
	});
});

describe('unrenderedColorGroups', () => {
	it('is empty when every exported group is a rendered one', () => {
		expect(unrenderedColorGroups(['Background', 'Content'])).toEqual([]);
	});

	it('flags a group that is not in the rendered set', () => {
		expect(unrenderedColorGroups(['Background', 'Vault'])).toEqual(['Vault']);
	});
});

describe('assertAllColorGroupsRendered', () => {
	it('does not throw against the real theme-colors.json export', () => {
		expect(() => assertAllColorGroupsRendered()).not.toThrow();
	});
});

describe('resolveColorTokens', () => {
	const tokens: ColorToken[] = [
		{ name: 'bg-brand', path: [], description: '', light: '#fff', dark: '#000', fixed: false },
		{
			name: 'content-positive',
			path: [],
			description: '',
			light: '#fff',
			dark: '#000',
			fixed: false,
		},
	];

	it('resolves every name, in the order given', () => {
		expect(resolveColorTokens(['content-positive', 'bg-brand'], tokens)).toEqual([
			tokens[1],
			tokens[0],
		]);
	});

	it('throws naming every token that does not resolve', () => {
		expect(() =>
			resolveColorTokens(['bg-brand', 'bg-accent', 'bg-ghost'], tokens),
		).toThrow(/bg-accent, bg-ghost/);
	});

	it('does not throw against the real theme-colors.json export for the tokens the landing page uses', () => {
		const flat = themeColors().flatMap((g) => g.subgroups.flatMap((s) => s.tokens));
		expect(() =>
			resolveColorTokens(
				['bg-brand', 'content-positive', 'content-caution', 'content-critical'],
				flat,
			),
		).not.toThrow();
	});
});

describe('scale', () => {
	it('reads spacing in ascending order', () => {
		const spacing = scale('spacing.json');
		const values = spacing.map((t) => t.value);

		expect(values).toEqual([...values].sort((a, b) => a - b));
		expect(spacing.find((t) => t.name === 'spacing-400')?.value).toBe(16);
	});
});

describe('fontSizes', () => {
	it('returns only font-size tokens, leaf-named and in ascending order', () => {
		const sizes = fontSizes();

		expect(sizes.length).toBe(15);
		expect(sizes.map((t) => t.name)).toContain('font-size-400');
		expect(sizes.every((t) => !t.name.startsWith('line-height'))).toBe(true);
		expect(sizes.map((t) => t.value)).toEqual([...sizes.map((t) => t.value)].sort((a, b) => a - b));
	});
});

describe('fontWeights', () => {
	// The regression this covers: font weights are STRING variables, and the
	// generic `scale()` reader keeps only FLOATs. That filter dropped all four
	// tokens, so the Typography page rendered an empty Weights section and the
	// build stayed green.
	it('reads the STRING weight tokens the FLOAT reader drops', () => {
		const weights = fontWeights();

		expect(weights.length).toBeGreaterThanOrEqual(4);
		expect(weights.map((t) => t.name)).toEqual(
			expect.arrayContaining(['regular', 'medium', 'semibold', 'bold']),
		);
	});

	it('maps each style name to its CSS numeric weight', () => {
		const byName = new Map(fontWeights().map((t) => [t.name, t]));

		expect(byName.get('regular')).toMatchObject({ style: 'Regular', value: 400 });
		expect(byName.get('medium')).toMatchObject({ style: 'Medium', value: 500 });
		expect(byName.get('semibold')).toMatchObject({ style: 'SemiBold', value: 600 });
		expect(byName.get('bold')).toMatchObject({ style: 'Bold', value: 700 });
	});

	it('returns them in ascending weight order', () => {
		const values = fontWeights().map((t) => t.value);

		expect(values).toEqual([...values].sort((a, b) => a - b));
	});
});

describe('composeFontWeights', () => {
	it('sorts by weight rather than by export order', () => {
		const weights = composeFontWeights([
			{ name: 'bold', style: 'Bold' },
			{ name: 'regular', style: 'Regular' },
			{ name: 'semibold', style: 'SemiBold' },
		]);

		expect(weights.map((t) => t.value)).toEqual([400, 600, 700]);
	});

	it('throws when the export has no weight tokens at all', () => {
		expect(() => composeFontWeights([])).toThrow(/no font-weight tokens/);
		expect(() => composeFontWeights([])).toThrow(/Weights section/);
	});

	it('throws on a style name with no CSS equivalent', () => {
		const entries = [
			{ name: 'regular', style: 'Regular' },
			{ name: 'ultra', style: 'UltraHeavy' },
		];

		expect(() => composeFontWeights(entries)).toThrow(/ultra/);
		expect(() => composeFontWeights(entries)).toThrow(/UltraHeavy/);
		expect(() => composeFontWeights(entries)).toThrow(/FONT_WEIGHT_SCALE/);
	});
});

describe('soleModeId', () => {
	const collection = (modes: Record<string, string>): RawCollection => ({
		id: 'c1',
		name: 'Test collection',
		modes,
		variables: [],
	});

	it('returns the only mode id', () => {
		expect(soleModeId(collection({ '1:0': 'Value' }))).toBe('1:0');
	});

	it('throws when a collection has more than one mode', () => {
		expect(() => soleModeId(collection({ '1:0': 'Value', '2:0': 'Stray' }))).toThrow(
			/exactly one mode/,
		);
		expect(() => soleModeId(collection({ '1:0': 'Value', '2:0': 'Stray' }))).toThrow(
			/Test collection/,
		);
	});

	it('throws when a collection has no modes', () => {
		expect(() => soleModeId(collection({}))).toThrow(/exactly one mode/);
	});
});

describe('shadowSets', () => {
	it('composes every shadow size', () => {
		const names = shadowSets().map((s) => s.name);
		expect(names).toEqual(['xsmall', 'small', 'medium', 'large', 'xlarge']);
	});

	it('gives xsmall one level and large two', () => {
		const sets = shadowSets();
		expect(sets.find((s) => s.name === 'xsmall')!.levels).toHaveLength(1);
		expect(sets.find((s) => s.name === 'large')!.levels).toHaveLength(2);
	});

	it('builds the box-shadow value for every size', () => {
		const css = Object.fromEntries(shadowSets().map((s) => [s.name, s.css]));
		expect(css).toEqual({
			xsmall: '0px 1px 2px 0px #0000000d',
			small: '0px 1px 3px 0px #0000000d, 0px 1px 2px -1px #0000000d',
			medium: '0px 2px 3px -2px #0000000d, 0px 4px 6px -2px #0000000d',
			large: '0px 4px 6px -4px #0000000d, 0px 10px 15px -3px #0000000d',
			xlarge: '0px 8px 10px -6px #0000000d, 0px 20px 25px -5px #0000000d',
		});
	});
});

describe('fixedLightSurface', () => {
	it('resolves bg-always-light to white', () => {
		expect(fixedLightSurface()).toBe('#ffffff');
	});
});

describe('findFixedLightSurface', () => {
	it('resolves the light value of bg-always-light', () => {
		const variables = [
			{
				name: 'Background/Fixed/bg-always-light',
				resolvedValuesByMode: {
					light: { resolvedValue: { r: 1, g: 1, b: 1 }, alias: null },
				},
			},
		];

		expect(findFixedLightSurface(variables, 'light')).toBe('#ffffff');
	});

	it('throws when the token is missing', () => {
		expect(() => findFixedLightSurface([], 'light')).toThrow(/bg-always-light/);
	});
});

describe('composeShadowSizes', () => {
	it('throws when a size is not in SHADOW_ORDER', () => {
		const parts = new Map([
			[
				'huge',
				new Map([
					[
						'level-1',
						new Map<string, number | string>([
							['offset-x', 0],
							['offset-y', 1],
							['blur', 2],
							['spread', 0],
							['color', '#0000000d'],
						]),
					],
				]),
			],
		]);

		expect(() => composeShadowSizes(parts)).toThrow(/huge/);
		expect(() => composeShadowSizes(parts)).toThrow(/SHADOW_ORDER/);
	});

	it('throws when a level is missing a required property', () => {
		const parts = new Map([
			[
				'xsmall',
				new Map([
					[
						'level-1',
						new Map<string, number | string>([
							['offset-x', 0],
							['offset-y', 1],
							['blur', 2],
							['spread', 0],
							// color omitted
						]),
					],
				]),
			],
		]);

		expect(() => composeShadowSizes(parts)).toThrow(/xsmall/);
		expect(() => composeShadowSizes(parts)).toThrow(/level-1/);
		expect(() => composeShadowSizes(parts)).toThrow(/color/);
	});

	it('does not throw when a size legitimately has only one level', () => {
		const parts = new Map([
			[
				'xsmall',
				new Map([
					[
						'level-1',
						new Map<string, number | string>([
							['offset-x', 0],
							['offset-y', 1],
							['blur', 2],
							['spread', 0],
							['color', '#0000000d'],
						]),
					],
				]),
			],
		]);

		expect(() => composeShadowSizes(parts)).not.toThrow();
		expect(composeShadowSizes(parts)[0]!.levels).toHaveLength(1);
	});
});
