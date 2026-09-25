// Build output: written by build/build-fonts.mjs, which npm run build and both storybook
// scripts' pre hooks run. Without it --lmnd-font-family-base falls through to the system
// sans and the gallery misreports how the type actually looks.
import '../dist/fonts.css'
import '../styles/tokens.css'
import '../styles/typography.css'
import '../styles/icon.css'
import type { Preview } from '@storybook/react'
import type { ReactElement } from 'react'
import { createElement } from 'react'
import { withThemeByDataAttribute } from '@storybook/addon-themes'
import { create } from '@storybook/theming/create'

export const decorators = [
  withThemeByDataAttribute({
    themes: { light: 'light', dark: 'dark' },
    defaultTheme: 'light',
    attributeName: 'data-lmnd-theme',
  }),
  // tokens.css only *declares* the custom properties — nothing in the package applies
  // --lmnd-color-bg-default/--lmnd-color-content-primary to a page by default (that's
  // a consuming app's job). Wrap every story so the canvas visibly repaints on toggle,
  // the same way a real app's root element would.
  (Story: () => ReactElement) =>
    createElement(
      'div',
      {
        style: {
          minHeight: '100vh',
          margin: '-1rem', // undo Storybook's default docs/canvas padding so the fill reaches the edges
          padding: 'var(--lmnd-spacing-600)',
          background: 'var(--lmnd-color-bg-default)',
          color: 'var(--lmnd-color-content-primary)',
          fontFamily: 'var(--lmnd-font-family-base)',
        },
      },
      createElement(Story),
    ),
]

const preview: Preview = {
  parameters: {
    // The tokens own the page background (--lmnd-color-bg-default etc., applied by
    // the decorator above); Storybook's own backgrounds addon would paint over it
    // and fight the theme toggle.
    backgrounds: { disable: true },
    docs: { theme: create({ base: 'light', fontBase: '"Figtree", sans-serif' }) },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
}

export default preview
