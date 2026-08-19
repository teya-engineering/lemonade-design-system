/**
 * The navigation, as data.
 *
 * Two levels of grouping:
 *
 *   SECTION      an inert heading — "Get started", "Build"
 *     link       a page sitting directly under the heading
 *     GROUP      a collapsible group of pages
 *       link
 *
 * Starlight only understands links and groups; it has no notion of a section.
 * So `sidebar` below flattens the sections away for `astro.config.mjs`, and
 * `src/components/Sidebar.astro` re-applies them when rendering. The two must
 * stay in step — `sidebar.test.ts` fails if they drift.
 *
 * Standards and Patterns sit above Foundations on purpose: the site is about
 * how to build with Lemonade, so the reference material is not what a reader
 * should meet first.
 */

export interface SidebarItem {
	label: string;
	slug: string;
}

export interface SidebarGroup {
	label: string;
	items: SidebarItem[];
}

export type SidebarEntry = SidebarItem | SidebarGroup;

export interface SidebarSection {
	label: string;
	entries: SidebarEntry[];
}

export function isGroup(entry: SidebarEntry): entry is SidebarGroup {
	return 'items' in entry;
}

export const sections: SidebarSection[] = [
	{
		label: 'Get started',
		entries: [
			{
				label: 'Engineering',
				items: [
					{ label: 'Kotlin Multiplatform', slug: 'get-started/kmp' },
					{ label: 'SwiftUI', slug: 'get-started/swiftui' },
				],
			},
		],
	},
	{
		label: 'Build',
		entries: [
			{ label: 'Prototyping with Lemonade', slug: 'prototyping' },
			{
				label: 'Standards',
				items: [
					{ label: 'Semantic tokens first', slug: 'standards/semantic-tokens' },
					{ label: 'Theming & dark mode', slug: 'standards/theming' },
					{ label: 'Layout & rhythm', slug: 'standards/layout-rhythm' },
					{ label: 'Accessibility', slug: 'standards/accessibility' },
				],
			},
			{
				label: 'Patterns',
				items: [
					{ label: 'Forms', slug: 'patterns/forms' },
					{ label: 'Lists', slug: 'patterns/lists' },
					{ label: 'Empty & loading', slug: 'patterns/empty-and-loading' },
					{ label: 'Errors & recovery', slug: 'patterns/errors' },
				],
			},
		],
	},
	{
		label: 'Foundations',
		entries: [
			{ label: 'Colour', slug: 'foundations/colour' },
			{ label: 'Typography', slug: 'foundations/typography' },
			{ label: 'Space & shape', slug: 'foundations/space-and-shape' },
			{ label: 'Elevation', slug: 'foundations/elevation' },
			{ label: 'Opacity & borders', slug: 'foundations/opacity-and-borders' },
		],
	},
];

/** Section-free view of the tree, which is all Starlight's config accepts. */
export const sidebar: SidebarEntry[] = sections.flatMap((section) => section.entries);

/** Every page named anywhere in the tree, in render order. */
export function slugs(): string[] {
	return sidebar.flatMap((entry) =>
		isGroup(entry) ? entry.items.map((item) => item.slug) : [entry.slug],
	);
}
