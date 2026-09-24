import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import type { StorybookConfig } from '@storybook/react-vite'

const here = dirname(fileURLToPath(import.meta.url))

// The manager iframe loads none of the preview's stylesheets, so the faces have to be
// inlined into its document. Reading the generated file keeps one source for the
// declarations; only the paths change, from fonts.css's ./fonts/ to the URL the
// staticDirs mapping below serves. Relative, so a subpath deploy still resolves.
function managerFontFaces(): string {
  const generated = join(here, '../dist/fonts.css')
  try {
    return readFileSync(generated, 'utf8').replaceAll("url('./fonts/", "url('lmnd/fonts/")
  } catch {
    throw new Error(`${generated} is missing — run npm run build (or use npm run storybook, which builds it).`)
  }
}

const config: StorybookConfig = {
  // The MDX guides are left out until the package is published — stories/Installing.mdx
  // documents an npm install that does not resolve yet. Restore the '../stories/**/*.mdx'
  // entry once it does.
  stories: ['../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-essentials', '@storybook/addon-interactions', '@storybook/addon-themes'],
  framework: {
    name: '@storybook/react-vite',
    options: {},
  },
  // web/assets/{icons,flags,brand-logos} are consumed at runtime via the .lmnd-icon
  // mask class (url('/assets/icons/foo.svg')). Storybook doesn't serve arbitrary
  // project directories by default, so this maps them onto the dev server and the
  // static build under the same /assets/ path the published package expects.
  staticDirs: [
    { from: '../assets', to: '/assets' },
    { from: '../dist/fonts', to: '/lmnd/fonts' },
  ],
  managerHead: (head) => `${head}\n<style>\n${managerFontFaces()}</style>`,
}
export default config
