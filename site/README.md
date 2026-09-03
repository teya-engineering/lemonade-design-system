# Lemonade documentation site

The docs site for the Lemonade design system: getting started on KMP and SwiftUI, the
standards behind the components, and the design tokens that hold it together. Built
with [Astro](https://astro.build) and [Starlight](https://starlight.astro.build).

Foundations pages (Colour, Space & shape, Typography, Opacity & borders) are not
hand-written — they're generated at build time from the Figma exports in `tokens/*.json`
at the repository root. Edit the token export, not the page, to change what they show.

## Requirements

Node 22 (see `.nvmrc`).

## Commands

Run from this directory (`site/`):

| Command          | Action                                          |
| :--------------- | :----------------------------------------------- |
| `npm install`    | Install dependencies                              |
| `npm run dev`    | Start the local dev server at `localhost:4321`    |
| `npm run build`  | Build the production site to `./dist/`            |
| `npx vitest run` | Run the unit tests for the token-reading logic    |

## Links

Internal content links must be base-absolute (`/lemonade-design-system/...`), not
relative — the site is hosted under a base path. `npm run build` runs
`starlight-links-validator` and fails on any dead internal link, so a broken link never
reaches production.
