import { readdirSync, existsSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { isGroup, sections, sidebar, slugs } from './sidebar';

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
		for (const slug of slugs()) {
			const md = new URL(`./content/docs/${slug}.md`, import.meta.url);
			const mdx = new URL(`./content/docs/${slug}.mdx`, import.meta.url);
			expect(existsSync(md) || existsSync(mdx), `sidebar points at missing page "${slug}"`).toBe(
				true,
			);
		}
	});

	it('lists every page except the 404', () => {
		const listed = new Set(slugs());
		const orphans = contentFiles().filter((slug) => slug !== '404' && !listed.has(slug));

		expect(orphans, `pages missing from the sidebar: ${orphans.join(', ')}`).toEqual([]);
	});

	// The Sidebar override slices Starlight's flat list back into sections by
	// counting entries, so the flattening has to stay a plain concatenation in
	// order. If someone hand-edits `sidebar`, sections silently mis-slice and
	// pages disappear from the nav.
	it('flattens sections in order, losing nothing', () => {
		expect(sidebar).toEqual(sections.flatMap((section) => section.entries));
		expect(sidebar).toHaveLength(sections.reduce((total, s) => total + s.entries.length, 0));
	});

	it('gives every section and group a label', () => {
		for (const section of sections) {
			expect(section.label, 'section without a label').toBeTruthy();
			expect(section.entries.length, `empty section "${section.label}"`).toBeGreaterThan(0);

			for (const entry of section.entries) {
				expect(entry.label, `entry without a label in "${section.label}"`).toBeTruthy();
				if (isGroup(entry)) {
					expect(entry.items.length, `empty group "${entry.label}"`).toBeGreaterThan(0);
				}
			}
		}
	});
});
