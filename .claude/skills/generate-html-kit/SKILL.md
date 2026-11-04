---
name: generate-html-kit
description: Generate the Lemonade HTML kit — the shareable zip of HTML/CSS prototyping files built from web/html-kit/ and the Lemonade tokens. Use when asked to "generate", "build", "rebuild", "package" or "zip" the HTML kit, when someone needs the kit to share, or after tokens, icons, web/llms.txt, kit.css or the gallery change. Not for building a prototype screen with the kit, or for the npm package's components (`web-review`).
---

# Generate the Lemonade HTML kit

The HTML kit is plain HTML and CSS for prototyping Teya app screens. Its source lives in
`web/html-kit/`; one command packages it into a folder and a zip that anyone can open, edit and hand to
an AI session, with no repository, install or network. `web/html-kit/README.md` describes the source.

## Generate

From `web/`:

```sh
npm run html-kit
```

It writes `web/dist/lemonade-html-kit/` and `web/dist/lemonade-html-kit.zip`. It needs Node 20.12 or
later and no `npm install`. Pass `--out <dir>` (`npm run html-kit -- --out <dir>`) to write elsewhere.

## Verify before handing it over

1. `npx vitest run tests/html-kit.test.ts` in `web/` passes. The build needs no dependencies, but the
   test does: run `npm ci` once first if `web/node_modules/` is missing. The test checks that `kit.css`
   reads only real tokens and has no colour of its own, that every class a page uses has a rule, that
   every page names only real assets, and that the kit's `llms.txt` holds the token sections only.
2. `unzip -tq dist/lemonade-html-kit.zip` reports no errors.
3. Look at `web/dist/lemonade-html-kit/gallery.html` in light and dark: the theme switch at the top
   flips it, or render it headless with `data-lmnd-theme="light"` / `"dark"` on `<html>`. Expect styled
   components, coloured swatches and icons, all changing with the theme. Unstyled black-on-white text
   means `lemonade-kit.css` is missing or failed to generate. If you can't render pages, say so.

Then give the user the absolute path of the zip. `web/dist/` itself stays on this machine.

## What goes into the zip

| In the zip | Comes from |
| --- | --- |
| `lemonade-kit.css` | Generated: `web/styles/` tokens, type scale and components, `web/html-kit/kit.css`, Figtree from `swiftui/`, every asset in `web/assets/` |
| `gallery.html` | `web/html-kit/`, with the icon list filled in |
| `README.md` | `web/html-kit/share/README.md` |
| `llms.txt` | Generated: the token sections of `web/llms.txt` |
| `assets.txt` | Generated: every icon, flag, logo and badge name in the kit's markup |
| `classes.txt` | Generated: every `lmnd-kit-` class in `kit.css` |

## When it fails

| Message | Cause | Fix |
| --- | --- | --- |
| `web/llms.txt has no section for: …` | A token section in `web/llms.txt` was renamed or removed | Update `LLMS_SECTIONS` in `web/html-kit/build.mjs` to the new heading |
| A test names an unknown icon, flag or logo | A page uses an asset name that no longer exists | Pick a name from `assets.txt` and fix the page |
| A test names a class with no rule | A page uses a class that `kit.css` doesn't define | Fix the page, or add the rule to `kit.css` |
| A page opened from `web/html-kit/` shows a yellow notice | It was opened from the source, which has no `lemonade-kit.css` | Open it from `web/dist/lemonade-html-kit/` |

## Don't

- Commit anything under `web/dist/`. It is build output and gitignored; share the zip instead.
- Edit `lemonade-kit.css`, the kit's `llms.txt` or `assets.txt`. Change their sources and regenerate.
