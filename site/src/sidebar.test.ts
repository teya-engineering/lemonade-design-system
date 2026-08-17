import { readdirSync, existsSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { sidebar } from './sidebar';

const DOCS = new URL('./content/docs/', import.meta.url);

function contentFiles(dir = DOCS, prefix = ''): string[] {
	return readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
		if (entry.isDirectory()) {
			return contentFiles(new URL(`${entry.name}/`, dir), `${prefix}${entry.name}/`);
		}
		const slug = `${prefix}${entry.name.replace(/\.mdx?$/, '')}`;
		return entry.name.endsWith('.md') || entry.name.endsWith('.mdx') ? [slug] : [];
	});
}

describe('sidebar', () => {
	it('points every entry at a page that exists', () => {
		for (const group of sidebar) {
			for (const item of group.items) {
				const md = new URL(`./content/docs/${item.slug}.md`, import.meta.url);
				const mdx = new URL(`./content/docs/${item.slug}.mdx`, import.meta.url);
				expect(
					existsSync(md) || existsSync(mdx),
					`sidebar entry "${item.label}" points at missing page "${item.slug}"`,
				).toBe(true);
			}
		}
	});

	it('lists every page except the 404', () => {
		const listed = new Set(sidebar.flatMap((g) => g.items.map((i) => i.slug)));
		const orphans = contentFiles().filter((slug) => slug !== '404' && !listed.has(slug));

		expect(orphans, `pages missing from the sidebar: ${orphans.join(', ')}`).toEqual([]);
	});
});
