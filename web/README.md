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

The CSS carries the component and React only composes class names, so the classes are the API:

```jsx
import '@teya/lemonade-mobile-ds/components.css'
import { Button } from '@teya/lemonade-mobile-ds/react'

<Button label="Add item" variant="primary" size="large" onClick={save} />
```

Without React, write the markup against the same classes — `buttonClasses` from the root
export builds the list, and `llms.txt` documents the markup per component:

```html
<button
  type="button"
  class="lmnd-button lmnd-button--primary lmnd-button--solid lmnd-button--large lmnd-text-body-medium-semibold"
>
  <span class="lmnd-button__label">Add item</span>
</button>
```

React and `react-dom` are **optional** peer dependencies, so installing the package for the
tokens alone pulls in neither.

## What is here

| Import | Contents |
|---|---|
| `@teya/lemonade-mobile-ds` | Typed tokens, text styles, asset manifests and the class builders |
| `@teya/lemonade-mobile-ds/react` | The React components. Needs React, which is an optional peer |
| `@teya/lemonade-mobile-ds/components.css` | Component styles. Nothing imports it for you |
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
| **Hand-written** | `src/index.ts`, `src/react/**`, `src/components/**` — components, their CSS, types and tests | **yes** | people |
| **Built** | `dist/**` — bundled JS, type declarations, `fonts.css`, `components.css`, optimized `assets/**` | no (gitignored) | `npm run build` |

Generated files are committed on purpose: `token_drift.yml` regenerates them and fails
if the tree differs, which is what stops a Figma export landing without the platform
code that matches it. **Do not hand-edit them** — change the converter and regenerate.

A component's stylesheet is hand-written but feeds a generated file: `web-css-bundle`
discovers `src/components/**/*.css` and concatenates it into `styles/lemonade.css`, the
single file a prototype can paste. So editing one and not regenerating leaves that bundle
stale, which `token_drift.yml` catches.

`npm run build` writes only into `dist/`. It never modifies the committed sources, so a
build never leaves your working tree dirty.

Regenerate the committed output with:

```sh
.claude/skills/generate-tokens/scripts/run-converters.sh --all
```
