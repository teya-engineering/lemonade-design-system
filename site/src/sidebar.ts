export interface SidebarItem {
	label: string;
	slug: string;
}

export interface SidebarGroup {
	label: string;
	items: SidebarItem[];
}

/**
 * Standards and Patterns sit above Foundations on purpose: the site is about
 * how to build with Lemonade, so the reference material is not what a reader
 * should meet first.
 */
export const sidebar: SidebarGroup[] = [
	{
		label: 'Get started',
		items: [
			{ label: 'Kotlin Multiplatform', slug: 'get-started/kmp' },
			{ label: 'SwiftUI', slug: 'get-started/swiftui' },
		],
	},
	{
		label: 'Prototyping',
		items: [{ label: 'Prototyping with Lemonade', slug: 'prototyping' }],
	},
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
	{
		label: 'Foundations',
		items: [
			{ label: 'Colour', slug: 'foundations/colour' },
			{ label: 'Typography', slug: 'foundations/typography' },
			{ label: 'Space & shape', slug: 'foundations/space-and-shape' },
			{ label: 'Elevation', slug: 'foundations/elevation' },
			{ label: 'Opacity & borders', slug: 'foundations/opacity-and-borders' },
		],
	},
];
