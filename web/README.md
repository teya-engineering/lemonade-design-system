# Lemonade Design System — Web

Design tokens, typography, fonts and icons for the web.

## Install

```sh
npm install @teya/lemonade-ds
```

## Use

```js
import '@teya/lemonade-ds/tokens.css'      // custom properties only — safe anywhere
import '@teya/lemonade-ds/typography.css'  // .lmnd-text-* classes
import '@teya/lemonade-ds/fonts.css'       // Figtree, self-hosted (opt-in)
```

Or take the first two together, and add fonts if you want the bundled typeface:

```js
import '@teya/lemonade-ds/styles.css'      // tokens + typography
import '@teya/lemonade-ds/fonts.css'
```

```html
<html data-lmnd-theme="dark">  <!-- explicit; omit to follow the OS -->
```

`tokens.css` declares custom properties and nothing else, so it can be added to an
existing app — including a Material UI one — without affecting current components.

## What is here

| Import | Contents |
|---|---|
| `@teya/lemonade-ds` | Typed tokens, text styles and asset manifests |
| `@teya/lemonade-ds/styles.css` | Barrel: tokens + typography |
| `@teya/lemonade-ds/fonts.css` | Figtree `@font-face` declarations |
| `@teya/lemonade-ds/icon.css` | The `.lmnd-icon` mask utility |
| `@teya/lemonade-ds/lemonade.css` | Everything in one self-contained file, for prototypes |
| `@teya/lemonade-ds/llms.txt` | Token reference for AI tools |
| `@teya/lemonade-ds/icons/*.svg` | 283 icons, `currentColor` |
| `@teya/lemonade-ds/flags/*.svg` | 265 flags |
| `@teya/lemonade-ds/brand-logos/*.svg` | 39 brand logos |

## Repository layout — generated vs built

Two different kinds of output live here, and the distinction matters:

| | Where | Committed? | Written by |
|---|---|---|---|
| **Generated** | `styles/*.css`, `src/*.generated.ts`, `assets/**`, `llms.txt`, `tokens.json` | **yes** | `scripts/web-*.main.kts` (Kotlin) |
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
