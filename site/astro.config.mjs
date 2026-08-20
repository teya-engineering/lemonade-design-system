// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import starlightLinksValidator from 'starlight-links-validator';
import { sidebar } from './src/sidebar';

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
			sidebar,
			components: {
				// Adds inert section headings above the groups; see the component.
				Sidebar: './src/components/Sidebar.astro',
				// Puts the page's authors and last-updated date under the title,
				// read from git by src/lib/contributors.ts. Starlight's own
				// `lastUpdated` is left off so there is one source, not two.
				PageTitle: './src/components/PageTitle.astro',
			},
		}),
	],
});
