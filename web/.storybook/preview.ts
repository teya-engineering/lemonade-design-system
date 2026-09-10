import '../styles/tokens.css'
import '../styles/typography.css'
import '../styles/icon.css'
import type { Preview } from '@storybook/react'
import type { ReactElement } from 'react'
import { createElement } from 'react'
import { withThemeByDataAttribute } from '@storybook/addon-themes'

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
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
}

export default preview
