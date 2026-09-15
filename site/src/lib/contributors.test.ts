import { describe, expect, it } from 'vitest';
import { parseGitLog } from './contributors';

/** `git log` output is newest first; this helper keeps the fixtures readable. */
const log = (...lines: string[]) => lines.join('\n');

describe('parseGitLog', () => {
	it('takes the newest commit as the last edit and the oldest as the creation', () => {
		const entries = parseGitLog(
			log(
				'c:1700000300|Bea',
				'M\tsite/src/content/docs/a.md',
				'c:1700000200|Cal',
				'M\tsite/src/content/docs/a.md',
				'c:1700000100|Ada',
				'A\tsite/src/content/docs/a.md',
			),
		);

		const a = entries.get('site/src/content/docs/a.md')!;
		expect(a.lastEdited).toEqual({ date: 1700000300000, name: 'Bea' });
		expect(a.created).toEqual({ date: 1700000100000, name: 'Ada' });
	});

	// The page that moved from standards/ to layout/ would otherwise report its
	// creation as the day it was moved.
	it('follows a rename back to the original creation', () => {
		const entries = parseGitLog(
			log(
				'c:1700000200|Bea',
				'R100\tsite/src/content/docs/old.md\tsite/src/content/docs/new.md',
				'c:1700000100|Ada',
				'A\tsite/src/content/docs/old.md',
			),
		);

		expect(entries.has('site/src/content/docs/old.md')).toBe(false);
		const moved = entries.get('site/src/content/docs/new.md')!;
		expect(moved.created.name).toBe('Ada');
		expect(moved.created.date).toBe(1700000100000);
		expect(moved.lastEdited.name).toBe('Bea');
	});

	it('follows a chain of renames', () => {
		const entries = parseGitLog(
			log(
				'c:1700000300|Cal',
				'R100\tb.md\tc.md',
				'c:1700000200|Bea',
				'R100\ta.md\tb.md',
				'c:1700000100|Ada',
				'A\ta.md',
			),
		);

		expect([...entries.keys()]).toEqual(['c.md']);
		expect(entries.get('c.md')!.created.name).toBe('Ada');
	});

	it('keeps files independent of one another', () => {
		const entries = parseGitLog(
			log('c:1700000200|Bea', 'M\ta.md', 'A\tb.md', 'c:1700000100|Ada', 'A\ta.md'),
		);

		expect(entries.get('a.md')!.created.name).toBe('Ada');
		expect(entries.get('b.md')!.created.name).toBe('Bea');
	});

	it('returns nothing for empty output rather than inventing a history', () => {
		expect(parseGitLog('').size).toBe(0);
	});
});
