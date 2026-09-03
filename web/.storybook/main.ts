import type { StorybookConfig } from '@storybook/react-vite'

const config: StorybookConfig = {
  stories: ['../stories/**/*.mdx', '../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-essentials', '@storybook/addon-interactions', '@storybook/addon-themes'],
  framework: {
    name: '@storybook/react-vite',
    options: {},
  },
  // web/assets/{icons,flags,brand-logos} are consumed at runtime via the .lmnd-icon
  // mask class (url('/assets/icons/foo.svg')). Storybook doesn't serve arbitrary
  // project directories by default, so this maps them onto the dev server and the
  // static build under the same /assets/ path the published package expects.
  staticDirs: [{ from: '../assets', to: '/assets' }],
}
export default config
