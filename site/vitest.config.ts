/// <reference types="vitest" />
import { getViteConfig } from 'astro/config';

// getViteConfig gives the tests the same resolution rules as the build, which is
// what lets import.meta.glob reach the token exports outside site/.
export default getViteConfig({
	test: {
		include: ['src/**/*.test.ts'],
	},
});
