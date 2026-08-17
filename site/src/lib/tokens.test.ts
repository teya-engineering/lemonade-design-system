import { describe, expect, it } from 'vitest';
import { themeColors, scale, fontSizes } from './tokens';

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
