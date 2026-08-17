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
			// errorOnRelativeLinks is off because content pages intentionally use
			// relative links instead of hard-coding the `/lemonade-design-system`
			// base path (see the 404 page for the one deliberate exception).
			plugins: [starlightLinksValidator({ errorOnRelativeLinks: false })],
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
		}),
	],
});
