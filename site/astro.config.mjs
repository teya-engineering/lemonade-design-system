// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import starlightLinksValidator from 'starlight-links-validator';

// Project-path hosting on GitHub Pages: https://saltpay.github.io/lemonade-design-system/
export default defineConfig({
	site: 'https://saltpay.github.io',
	base: '/lemonade-design-system',
	// The token exports live at the repository root, outside the Astro project.
	vite: { server: { fs: { allow: ['..'] } } },
	integrations: [
		starlight({
			title: 'Lemonade',
			description:
				"Teya's design system — how to build with it, the standards behind it, and the tokens that hold it together.",
			customCss: ['./src/styles/lemonade.css'],
			plugins: [starlightLinksValidator()],
			social: [
				{
					icon: 'github',
					label: 'GitHub',
					href: 'https://github.com/saltpay/lemonade-design-system',
				},
			],
			// The landing page is a standalone Astro page at src/pages/index.astro,
			// so Starlight owns everything except "/".
			sidebar: [
				{
					label: 'Get started',
					items: [{ label: 'Installing Lemonade', slug: 'get-started' }],
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
					],
				},
			],
		}),
	],
});
