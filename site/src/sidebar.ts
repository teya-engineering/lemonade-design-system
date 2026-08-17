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
		label: 'Standards',
		items: [{ label: 'Semantic tokens first', slug: 'standards/semantic-tokens' }],
	},
	{
		label: 'Patterns',
		items: [{ label: 'Forms', slug: 'patterns/forms' }],
	},
	{
		label: 'Foundations',
		items: [
			{ label: 'Colour', slug: 'foundations/colour' },
			{ label: 'Typography', slug: 'foundations/typography' },
			{ label: 'Space & shape', slug: 'foundations/space-and-shape' },
			{ label: 'Elevation', slug: 'foundations/elevation' },
		],
	},
];
