import { describe, expect, it } from 'vitest';
import { themeColors, scale, fontSizes, shadowSets, composeShadowSizes } from './tokens';

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
	it('returns only font-size tokens', () => {
		expect(fontSizes().every((t) => !t.name.startsWith('line-height'))).toBe(true);
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
