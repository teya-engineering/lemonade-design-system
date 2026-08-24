# Lemonade Design System — Web

Design tokens, typography, fonts and icons for the web.

## Install

```sh
npm install @teya/lemonade-ds
```

## Use

```js
import '@teya/lemonade-ds/tokens.css'      // custom properties only — safe anywhere
import '@teya/lemonade-ds/fonts.css'       // Figtree, self-hosted
import '@teya/lemonade-ds/typography.css'  // .lmnd-text-* classes
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
| `@teya/lemonade-ds/lemonade.css` | Everything in one self-contained file, for prototypes |
| `@teya/lemonade-ds/llms.txt` | Token reference for AI tools |
| `@teya/lemonade-ds/icons/*.svg` | 283 icons, `currentColor` |
| `@teya/lemonade-ds/flags/*.svg` | 265 flags |
| `@teya/lemonade-ds/brand-logos/*.svg` | 39 brand logos |

Tokens are generated from `tokens/*.tokens.json` by `scripts/web-*.main.kts`.
Do not edit anything under `web/styles/` or `web/src/*.generated.ts` by hand.

> **`npm run build` rewrites `web/assets/**` in place** (the SVG optimizer step) and
> those files are committed. Do not commit changes it makes there. If
> `web/assets/**` shows up dirty after a build, regenerate it from the source of
> truth instead: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-svg-converter.main.kts`,
> then confirm `git status --porcelain -- web/assets` is empty before committing.
