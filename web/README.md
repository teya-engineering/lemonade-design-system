# Lemonade Design System — Web

Design tokens, typography, fonts and icons for the web.

## Install

```sh
npm install @teya/lemonade-mobile-ds
```

## Use

```js
import '@teya/lemonade-mobile-ds/tokens.css'      // custom properties only — safe anywhere
import '@teya/lemonade-mobile-ds/typography.css'  // .lmnd-text-* classes
import '@teya/lemonade-mobile-ds/fonts.css'       // Figtree, self-hosted (opt-in)
```

Or take the first two together, and add fonts if you want the bundled typeface:

```js
import '@teya/lemonade-mobile-ds/styles.css'      // tokens + typography
import '@teya/lemonade-mobile-ds/fonts.css'
```

```html
<html data-lmnd-theme="dark">  <!-- explicit; omit to follow the OS -->
```

`tokens.css` declares custom properties and nothing else — no element selectors — so it
can be added to an existing app without affecting any current component.

## Components

The CSS carries the component and React only applies the class names, so the classes are
the API:

```jsx
import { Icon, Text } from '@teya/lemonade-mobile-ds/react'

<Text textStyle="bodyMediumRegular" as="p">Account balance</Text>
<Icon use="heart" size="large" label="Favourite" />
```

Icons are CSS masks, so they take their colour from `currentColor` and need the SVGs
served: copy `dist/assets/icons` to `/assets/icons`, or point `basePath` elsewhere.

Without React, write the markup against the same classes — the `textStyles` manifest on
the root export maps every style name to its class, and `llms.txt` documents the markup
per component:

```html
<p class="lmnd-text-body-medium-regular">Account balance</p>
```

React and `react-dom` are **optional** peer dependencies, so installing the package for
the tokens alone pulls in neither.

## What is here

| Import | Contents |
|---|---|
| `@teya/lemonade-mobile-ds` | Typed tokens, text styles and asset manifests |
| `@teya/lemonade-mobile-ds/react` | The React components. Needs React, which is an optional peer |
| `@teya/lemonade-mobile-ds/styles.css` | Barrel: tokens + typography |
| `@teya/lemonade-mobile-ds/fonts.css` | Figtree `@font-face` declarations |
| `@teya/lemonade-mobile-ds/icon.css` | The `.lmnd-icon` mask utility |
| `@teya/lemonade-mobile-ds/lemonade.css` | Everything in one self-contained file, for prototypes |
| `@teya/lemonade-mobile-ds/llms.txt` | Token reference for AI tools |
| `@teya/lemonade-mobile-ds/icons/*.svg` | 295 icons, `currentColor` |
| `@teya/lemonade-mobile-ds/flags/*.svg` | 265 flags |
| `@teya/lemonade-mobile-ds/brand-logos/*.svg` | 39 brand logos |

## Repository layout — generated vs built

Two different kinds of output live here, and the distinction matters:

| | Where | Committed? | Written by |
|---|---|---|---|
| **Generated** | `styles/*.css`, `src/*.generated.ts`, `assets/**`, `llms.txt`, `tokens.json` | **yes** | `scripts/web-*.main.kts` (Kotlin) |
| **Hand-written** | `src/index.ts`, `src/react/**`, `src/components/**` — components, their types and tests | **yes** | people |
| **Built** | `dist/**` — bundled JS, type declarations, `fonts.css`, optimized `assets/**` | no (gitignored) | `npm run build` |

Generated files are committed on purpose: `token_drift.yml` regenerates them and fails
if the tree differs, which is what stops a Figma export landing without the platform
code that matches it. **Do not hand-edit them** — change the converter and regenerate.

`npm run build` writes only into `dist/`. It never modifies the committed sources, so a
build never leaves your working tree dirty.

Regenerate the committed output with:

```sh
.claude/skills/generate-tokens/scripts/run-converters.sh --all
```
