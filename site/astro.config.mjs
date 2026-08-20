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
			// Dates come from the newest commit touching each file, so the CI
			// checkout needs full history — see .github/workflows/site.yml.
			lastUpdated: true,
			components: {
				// Adds inert section headings above the groups; see the component.
				Sidebar: './src/components/Sidebar.astro',
				// Moves the last-updated date from the footer to under the title.
				PageTitle: './src/components/PageTitle.astro',
				LastUpdated: './src/components/LastUpdated.astro',
			},
		}),
	],
});
