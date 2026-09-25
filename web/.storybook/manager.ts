import { addons } from '@storybook/manager-api'
import { create } from '@storybook/theming/create'

// The sidebar and toolbar live in the manager iframe, which never loads the preview's
// stylesheets. main.ts links dist/fonts.css there so this family resolves.
addons.setConfig({
  theme: create({ base: 'light', fontBase: '"Figtree", sans-serif' }),
})
