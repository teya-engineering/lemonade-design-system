# Lemonade Web Support — Design

**Date:** 2026-08-21
**Status:** Approved design, pending implementation plan
**Scope:** v0 — design tokens and assets only. No components.

---

## 1. Problem

Lemonade ships to Android, iOS and JVM Desktop (KMP), to iOS/macOS (SwiftUI), and
nominally to Flutter. It has never shipped to the web.

Two populations pay for that gap every day:

- **Prototypers.** Teya builds a lot of mobile-web prototypes. With no web tokens,
  every prototype re-derives colour, type and spacing by eye from Figma, and the
  result never quite matches the product.
- **Web products.** The production web apps are React + Material UI. Because
  Lemonade offers them nothing, they have grown their own: `saltpay/financial-component-library`
  and `saltpay/shared-internal-components` are both MUI 5 + Emotion + Storybook
  component libraries maintained inside the org, neither aligned to Lemonade.
  This is the fragmentation the design system exists to prevent.

v0 closes the gap at the foundation layer — tokens, type, icons — which is where
the divergence starts and where the fix is framework-agnostic.

## 2. Goals and non-goals

**Goals**

1. Publish Lemonade's design tokens to npm, consumable by any web stack.
2. Generate them from the existing `tokens/*.tokens.json` source of truth, with the
   same drift protection the native platforms already have.
3. Make the package genuinely useful on day one: real type, real icons, real fonts —
   not a bag of hex codes.
4. Make correctness verifiable: WCAG contrast checked in CI, cross-platform
   typography parity checked in CI.
5. Make tokens discoverable, so teams stop reinventing them.

**Non-goals for v0**

React components; a Tailwind preset; a Material UI adapter; a CSS reset; motion
tokens (none exist in Figma). Each is deliberately deferred — see §15.

## 3. Decisions

| Decision | Choice | Rationale |
|---|---|---|
| v0 scope | Tokens + assets, components deferred | Let adoption reveal which components are actually needed, rather than guessing |
| Registry | **Public npm** | The tokens are already public via Maven Central and an Apache-2.0 repo, so nothing new is disclosed — see §3.1. Public npm is also the only registry AI prototyping tools can resolve |
| Scope | `@teya` if claimable, else `@teyaproduct` | `@teyaproduct` is the scope Teya demonstrably owns on public npm (`@teyaproduct/teya-blocks-*` resolve). That Teya chose it over `@teya` suggests `@teya` is taken. Verify before first publish |
| Package | `@teya/lemonade-ds`, single package | Subpath exports let components land later without a rename. npm has no rename operation, and the name will eventually be baked into Figma `codeSyntax`, so this is decided once |
| Publish gate | Nothing published until validated locally and signed off by the team | Public publication is effectively irreversible: npm unpublish is restricted and the name is burned either way |
| CSS delivery | Layered, individually importable entrypoints | The base layer is custom properties only — zero selectors — so it is safe to drop into any app, MUI included, with no possibility of conflict |
| Generator | Kotlin `.main.kts` in `scripts/`, like the other platforms | The DTCG loader is duplicated per platform and guarded by `check-loader-parity.py`. A TypeScript loader would be a fourth copy the guard cannot read — see §4.1. Style Dictionary was also rejected: the Figma export needs custom parsers regardless |
| Division of labour | `scripts/` generates source; `web/` builds and publishes | The line the repo already draws for KMP, SwiftUI and Flutter |
| Build tool | `tsup` | Matches the org's pattern for new JS packages (`teya-blocks-react`); the JS surface is small |
| Var prefix | `--lmnd-` | Short enough to type all day, distinct from `--mui-*` and `--tw-*` |
| Theming | `data-lmnd-theme` attribute + `prefers-color-scheme` | Zero-config follows the OS; the attribute always wins and works at any depth |
| Units | `rem` for proportional values, `px` for optical ones | See §5 |
| Docs | Storybook | Both existing internal component libraries use it, so Teya web teams already know it; it is also where components will live later |
| Release | Tag `lemonade-web-v*` | Matches `lemonade-kmp-v*` / `lemonade-swiftui-v*` |

### 3.1 Why public, and what it exposes

Verified, not assumed:

- `saltpay/lemonade-design-system` is **public** and Apache-2.0 licensed.
- KMP artifacts publish to **Maven Central** under group `com.teya.foundation` —
  `lemonade-core`, `lemonade-tokens`, `lemonade-ui`, `lemonade-expressive`,
  `lemonade-calendar` across Android, desktop, JVM and iOS targets, versions through
  0.9.0, resolvable by anyone with no authentication. `lemonade-tokens` means **the
  design tokens already ship as a public artifact**.
- `tokens/*.tokens.json`, all 587 SVGs and the Figtree TTFs are readable in the repo
  by anyone today.

A CSS file of custom properties is therefore a re-encoding of already-public data.
There is **no disclosure delta**. What changes is not secrecy but three other things,
which are the real subject of the decision:

1. **Discoverability** — npm is searchable in a way Maven Central and a GitHub repo
   are not. This is also precisely why AI tooling can resolve it.
2. **An implicit support commitment** — a versioned public package invites issues and
   an expectation of semver and continuity. This is the genuine cost.
3. **Public download telemetry** — npm shows weekly download counts to everyone.

Nothing internal is exposed either way: no product code, no infrastructure, no
customer data. It is colours, spacing, type and icons.

**JFrog is dropped for v0.** With a public package there is no auth to arrange, so
internal apps install it directly and no `.npmrc` entry is required. If Teya later
wants an internal mirror, JFrog's virtual registry proxies public npm already.

## 4. Architecture

Web lives in the monorepo alongside the other platforms, and the token pipeline
stays where the token pipeline already lives.

**`scripts/` reads the design source of truth and emits platform source.**
**`web/` builds and publishes the npm package.** That is the same division the
repo already applies to KMP, SwiftUI and Flutter; web does not get a special case.

```
scripts/                                    Kotlin .main.kts, alongside kmp-* / swiftui-* / flutter-*
  web-resource-file-loading.main.kts        DTCG loader — REGISTERED in check-loader-parity.py
  web-color-token-converter.main.kts
  web-theme-token-converter.main.kts
  web-spacing-token-converter.main.kts
  web-radius-token-converter.main.kts
  web-size-token-converter.main.kts
  web-border-token-converter.main.kts
  web-opacity-token-converter.main.kts
  web-shadow-token-converter.main.kts
  web-typography-token-converter.main.kts
  web-text-style-converter.main.kts         text-styles.json -> .lmnd-text-* classes
  web-svg-converter.main.kts                currentColor rewrite, inline-style strip
  web-llms-txt-converter.main.kts           llms.txt token reference for AI tools — §12
  web-contrast-check.main.kts               WCAG 2.2 AA validation
  web-text-style-parity-check.main.kts      web table vs SwiftUI table

web/
  package.json                              @teya/lemonade-ds
  tsup.config.ts
  src/
    index.ts                                public TS surface
    tokens.generated.ts                     GENERATED — DO NOT MODIFY
    icons.generated.ts                      GENERATED — typed icon-name manifest
  styles/                                   GENERATED + COMMITTED
    tokens.css  fonts.css  typography.css  styles.css
    lemonade.css                            self-contained single file — §12
  llms.txt                                  GENERATED + COMMITTED — §12
  assets/                                   GENERATED + COMMITTED (source SVG)
    icons/*.svg  flags/*.svg  brand-logos/*.svg
  build/
    optimize-svg.mjs                        svgo   (no Kotlin equivalent exists)
    build-fonts.mjs                         .ttf -> .woff2 + @font-face
  dist/                                     BUILT AT PUBLISH — not committed
    *.woff2, minified SVG, bundled JS
  .storybook/  stories/  tests/

text-styles.json                            repo root — hand-authored, see §7
```

Only two things live in `web/` that touch design assets, and both are there because
no Kotlin path exists: `svgo` and `woff2` are Node binaries. They are asset
*optimization* steps inside the package build, not token generation, so the pipeline
is not fragmented — `scripts/web-svg-converter.main.kts` decides what an icon *is*
(colour behaviour, naming, manifest), and `optimize-svg.mjs` only makes the bytes
smaller.

**What is committed, and what is built.** Converter output — the CSS, the TS, the
JSON, and the `currentColor`-rewritten SVG source — is **committed**, which is what
lets `token_drift.yml` diff it and what keeps that job Kotlin-only. Optimization
output — `.woff2` files, `svgo`-minified SVG, the bundled JS — is **built during
`web_ci.yml` and `web_release.yml` and never committed**. Binary artifacts in git
would bloat the repo and produce meaningless diffs, and they are deterministic
enough to rebuild. This is the line that keeps the drift job free of Node.

### 4.1 Why Kotlin, not TypeScript

This reverses an earlier draft of this spec, for a reason worth recording.

The three existing converters do not share a DTCG parser. Each carries its own copy —
`kmp-resource-file-loading.main.kts` (332 lines),
`swiftui-resource-file-loading.main.kts` (363),
`flutter-resource-file-loading.main.kts` (325). The duplication is deliberate: a
shared module would rewire the `@file:Import` graph of 20+ scripts. Nothing else
keeps the copies in step, so `check-loader-parity.py` does, and it runs in
`token_drift.yml`. Its own docstring states the failure it guards:

> A silent divergence is the failure this guards: the same token would produce
> different names, values or ordering depending on the platform, and each
> platform's own verification would still pass.

A TypeScript loader would be a **fourth copy of that parser in a language the guard
cannot read** — the check is a regex comparison over Kotlin `fun` declarations. Web
would become the one platform able to silently disagree with the other three about a
token's name or value, with every platform's own tests still green. Writing the
loader in Kotlin puts web *inside* the existing protection instead of outside it.

Secondary reasons, which only matter once that one holds: the whole token pipeline
stays in one directory; `token_drift.yml` needs no Node, since it already has a JDK;
and SVG conversion has Kotlin precedent in `scripts/svg-asset-converter.main.kts`.

The cost that argued for TypeScript — "web engineers would have to edit Kotlin" — is
smaller than it looks. Token *values* change with every Figma export; the *generator*
changes a few times a year. It is not a daily-friction surface.

**Accepted cost:** the Kotlin scripts are pinned to Kotlin 2.3.20 and crash on 2.4.0.
Adding ~13 scripts deepens an existing, contained dependency that already applies to
the whole pipeline.

### Published exports

```jsonc
{
  ".":                  "./dist/index.js",           // tokens, textStyles, iconNames
  "./tokens.css":       "./styles/tokens.css",       // --lmnd-* only, zero selectors
  "./typography.css":   "./styles/typography.css",
  "./icon.css":         "./styles/icon.css",         // .lmnd-icon mask utility
  "./styles.css":       "./styles/styles.css",       // barrel: tokens + typography
  "./lemonade.css":     "./styles/lemonade.css",     // self-contained, pasteable
  "./llms.txt":         "./llms.txt",                // AI token reference
  "./tokens.json":      "./tokens.json",             // non-JS consumers
  "./fonts.css":        "./dist/fonts.css",          // BUILT
  "./icons/*":          "./dist/assets/icons/*",     // BUILT (svgo-optimized)
  "./flags/*":          "./dist/assets/flags/*",     // BUILT
  "./brand-logos/*":    "./dist/assets/brand-logos/*"// BUILT
}
```

**Generated vs built — the rule the paths above follow.** Two kinds of output live in
`web/`, and conflating them is what made an earlier draft of this spec incoherent:

| | Location | Committed? | Written by |
|---|---|---|---|
| **Generated** | `styles/*.css`, `src/*.generated.ts`, `assets/**`, `llms.txt`, `tokens.json` | **yes** | `scripts/web-*.main.kts` |
| **Built** | `dist/**` — bundled JS, declarations, `fonts.css`, optimized `assets/**` | no | `npm run build` |

Generated output is committed precisely so `token_drift.yml` can diff it; putting it in
a gitignored `dist/` would delete the drift guarantee that justifies §4.1's whole
argument. Built output goes to `dist/` and nowhere else, so a build never modifies a
committed file and never leaves the working tree dirty.

The published tarball ships `dist/assets/**` (optimized) and **not** `assets/**`
(the unoptimized converter output), which stays in the repo for drift-checking only.
The `styles.css` barrel deliberately omits `fonts.css`: fonts are build output, so a
barrel importing them would reference a file absent from a source checkout.

React is not a dependency of any kind in v0. When components land they become
`./react`, with React as an optional peer dependency.

### Consumer usage

```js
import '@teya/lemonade-ds/tokens.css'      // always safe, anywhere
import '@teya/lemonade-ds/fonts.css'       // opt-in
import '@teya/lemonade-ds/typography.css'  // opt-in
```

```html
<html data-lmnd-theme="dark">   <!-- explicit -->
<html>                          <!-- follows prefers-color-scheme -->
```

## 5. Token generation

The `scripts/web-*-token-converter.main.kts` set reads the same
`tokens/*.tokens.json` the KMP and SwiftUI converters read, through
`web-resource-file-loading.main.kts`, and emits `tokens.css`,
`tokens.generated.ts` and `tokens.json` into `web/`.

They are registered in `run-converters.sh` and covered by `token_drift.yml`, so a
Figma export that was not regenerated for web fails CI exactly as it does for KMP
and SwiftUI. `web-resource-file-loading.main.kts` is added to the `LOADERS` map in
`check-loader-parity.py`, so web's DTCG parsing is held identical to the other
three platforms' rather than being allowed to drift.

### Colour: alpha is not in the hex

DTCG colour objects carry `components` (0–1 floats), `alpha`, and a `hex` string —
and **the hex discards alpha**. `content-primary` is `#090806` at `alpha: 0.925`.
Emitting the hex alone would silently produce the wrong colour. Colours are
therefore emitted as space-separated `rgb()`:

```css
--lmnd-color-content-primary: rgb(9 8 6 / 0.925);
--lmnd-color-bg-default: rgb(255 255 255);        /* alpha 1 -> omitted */
```

### Units

`px` scales under browser *zoom*, but only `rem` scales when a user raises their
browser's **default font size** — a common low-vision accommodation. If type grows
and padding does not, text crowds its container and eventually clips. Proportion
must scale as a unit. This also matches what the web ecosystem does (Tailwind,
Primer and Polaris all express spacing in rem), which makes a future Tailwind
mapping direct rather than a conversion.

The `html { font-size: 62.5% }` hack, which is what makes rem-everywhere fragile in
legacy apps, was searched for across the `saltpay` org and **does not appear
anywhere**.

| Category | Unit | Reason |
|---|---|---|
| font-size, line-height | `rem` | Must follow the user's font-size preference (WCAG 2.2 SC 1.4.4) |
| spacing | `rem` | Padding and gaps must grow with the text they surround |
| size | `rem` | Control heights and touch targets scale with content (helps SC 2.5.8) |
| radius | `rem` | A scaled-up surface keeps proportional corners |
| border-width | `px` | Hairlines are optical constants. `0.0625rem` at a 20px root is 1.25px, which renders blurry and inconsistently |
| shadow offset/blur/spread | `px` | Elevation is a depth cue tied to the surface, not to text size |
| opacity | unitless | n/a |

Pixel parity with the native platforms is preserved where it matters — in the TS
export, not in the stylesheet:

```ts
tokens.spacing[200]        // 8        raw, matches KMP/SwiftUI
tokens.spacing.css[200]    // "0.5rem" what the CSS var holds
```

### Font weights

The tokens store weights as strings (`"Regular" | "Medium" | "SemiBold" | "Bold"`).
The generator maps them to `400 | 500 | 600 | 700`.

### Naming scheme

Leaf names in the Figma export are already self-describing, so the scheme is
`--lmnd-<category>-<leaf>`, dropping the category word when the leaf repeats it.

| Source | Token | CSS custom property |
|---|---|---|
| theme-colors | `content-primary` | `--lmnd-color-content-primary` |
| theme-colors | `bg-default` | `--lmnd-color-bg-default` |
| theme-colors | `border-neutral-low` | `--lmnd-color-border-neutral-low` |
| spacing | `spacing-100` | `--lmnd-spacing-100` |
| radius | `radius-200` | `--lmnd-radius-200` |
| size | `size-400` | `--lmnd-size-400` |
| border-width | `border-25` | `--lmnd-border-width-25` |
| border-width | `border-selected` | `--lmnd-border-width-selected` |
| opacity | `opacity-disabled` | `--lmnd-opacity-disabled` |
| typography | `font-size-400` | `--lmnd-font-size-400` |
| typography | `base` | `--lmnd-font-family-base` |
| typography | `semibold` | `--lmnd-font-weight-semibold` |

**Collision resolved.** `border-selected` exists twice in the token set — once as a
border *width* and once as a semantic *colour*. A flat `--lmnd-<leaf>` scheme would
have silently collapsed them. Category namespacing keeps both:
`--lmnd-border-width-selected` and `--lmnd-color-border-selected`.

### Shadows are composed

The raw tokens are 45 scalar parts across 9 sets (`sd-md-lv1-offset-y`,
`sd-md-lv2-blur`, …), which is unusable directly. The generator composes each
`lv1`+`lv2` pair into one ready-to-use value and still emits the parts:

```css
--lmnd-shadow-md: 0 1px 2px 0 rgb(9 8 6 / .06), 0 4px 8px 0 rgb(9 8 6 / .08);
```

Note: KMP currently renders shadows roughly 2x too dark because Compose interprets
the blur value differently. CSS `box-shadow` blur matches Figma's definition
directly, so **web is correct without adjustment**, as SwiftUI is. Do not "fix" web
to match the KMP bug.

## 6. Theming

```css
:root {
  --lmnd-color-bg-default: rgb(255 255 255);   /* ...151 colour tokens... */
  color-scheme: light;
}

@media (prefers-color-scheme: dark) {
  :root:not([data-lmnd-theme="light"]) { /* dark values */ color-scheme: dark; }
}

[data-lmnd-theme="dark"]  { /* dark values */  color-scheme: dark; }
[data-lmnd-theme="light"] { /* light values */ color-scheme: light; }
```

Three consequences:

- **Zero-config is correct.** Import `tokens.css`, set nothing, and the page follows
  the user's OS preference.
- **The attribute works at any depth**, not only on `<html>`. A dark card inside a
  light page is `<div data-lmnd-theme="dark">`. Native platforms cannot do this
  cheaply; on web it is free.
- **`color-scheme` is set**, so native scrollbars, form controls and browser UI
  follow the theme.

## 7. Typography classes — and a drift risk

The 27 named text styles (`displayXSmall` … `bodyXSmallOverline`) are **not in the
token JSON**. They are a hand-authored composition table written twice — in
`swiftui/Sources/Lemonade/LemonadeTypography.swift` and again in KMP. Nothing
generates or verifies them.

A third hand-maintained copy would make drift near-certain. Therefore:

- The table lives at repo root as `text-styles.json` — **data, not code** — and
  `scripts/web-text-style-converter.main.kts` emits the CSS classes from it.
- `scripts/web-text-style-parity-check.main.kts` parses the SwiftUI table and
  asserts web matches it field for field, in CI.

```css
.lmnd-text-heading-large {
  font-family: var(--lmnd-font-family-base);
  font-size: var(--lmnd-font-size-800);
  line-height: var(--lmnd-line-height-1000);
  font-weight: var(--lmnd-font-weight-semibold);
}
```

Placing the file at the repo root rather than under `web/` is deliberate: the
longer-term fix is for all three platforms to generate from it, and web claiming it
as a private file would make that harder later. That change touches KMP and SwiftUI
generated code and is **out of scope for v0** — but the parity test means drift is caught in CI rather
than discovered in a screenshot months later.

## 8. Fonts

Figtree is OFL-licensed, so self-hosting is fine. The repo already contains the
TTFs. `web/build/build-fonts.mjs` converts them to `.woff2` (roughly 40% smaller)
and emits `fonts.css`. This one is Node because no Kotlin `woff2` encoder exists:

```css
@font-face {
  font-family: 'Figtree';
  src: url('../assets/fonts/Figtree-Regular.woff2') format('woff2');
  font-weight: 400;
  font-display: swap;
}
```

Ship **Regular / Medium / SemiBold** — exactly the three weights KMP and SwiftUI
ship, so web cannot render a weight the native apps cannot. `font-weight: 700` maps
to SemiBold, matching what SwiftUI already does internally. `Figtree-Bold` and
`-Italic` exist under `flutter/`; adding them web-only would break cross-platform
parity.

## 9. Icons — 587 assets

283 icons, 265 flags, 39 brand logos. `scripts/web-svg-converter.main.kts` decides
what an icon *is* — colour behaviour, naming, the typed manifest — matching the
existing `scripts/svg-asset-converter.main.kts`. `web/build/optimize-svg.mjs` then
runs `svgo` over the result, purely to shrink the bytes.

**No sprite.** Measured: all 283 icons are 393KB raw, 123KB gzipped — 123KB to
render one arrow. The sprite pattern solved HTTP/1.1 connection limits, a problem
that no longer exists under HTTP/2 and is moot entirely when a bundler inlines the
SVG.

Three first-class consumption paths:

```js
import ArrowRight from '@teya/lemonade-ds/icons/arrow-right.svg'  // bundler inlines; tree-shakes
```
```css
.lmnd-icon {                                   /* no-build, themeable */
  background-color: currentColor;
  mask: var(--lmnd-icon) center / contain no-repeat;
  width: var(--lmnd-size-500); height: var(--lmnd-size-500);
}
```
```html
<img src=".../icons/arrow-right.svg">          <!-- when theming is not needed -->
```

The `.lmnd-icon` mask class exists because `<img>` renders an opaque document that
CSS cannot reach into, so it cannot inherit `currentColor`. Masking gives no-build
pages themed icons without shipping a sprite.

**Gotcha the generator must handle.** Every icon is hardcoded black twice:

```html
<path d="…" fill="black" style="fill:black;fill-opacity:1;"/>
```

The inline `style` beats any stylesheet rule, so rewriting only the `fill`
attribute leaves the icon rendering black. `web-svg-converter.main.kts` must strip
the inline `style` *and* rewrite the attribute to `currentColor`.

`svgo` config is family-aware: aggressive for icons, conservative for flags and
brand logos, where path merging can visibly distort artwork.

## 10. Accessibility validation

`scripts/web-contrast-check.main.kts` walks the semantic pairs — every `content-*` against every
plausible `bg-*`, and `border-*` where used as a boundary — and asserts **WCAG 2.2
AA**: 4.5:1 for body text, 3:1 for large text and non-text boundaries.

It runs for **both light and dark**, and composites alpha correctly. This matters:
`content-primary` is 92.5% opaque, so comparing hex values alone reports a contrast
ratio the user never actually sees.

Known-failing pairs go in an explicit allowlist with a reason and an owner, so
exceptions are visible and reviewable rather than silently absent. Storybook shows
the computed ratio on every swatch.

## 11. Documentation — Storybook

- **Foundations** — Colours (151 swatches, light and dark side by side, contrast
  badges), Typography (live ramp), Spacing, Radius, Shadows, Icons (587,
  searchable, click-to-copy).
- **Guides** — Installing, Theming, Migrating from `financial-component-library`.

A "Using with Material UI" guide was written and then removed before release — MUI
integration is deferred rather than documented for now. The property it relied on
still holds and is worth keeping: `tokens.css` contains only custom property
declarations and zero element selectors, so it cannot disturb an existing app's
components. Nothing in the package depends on, or is tested against, MUI.

Static build deployed on merge to `main`.

## 12. AI and prototype consumption

A stated goal is that prototypes built with AI tools produce real Lemonade UI rather
than an approximation. Public npm solves most of it, but not all — the environments
differ in what they can do:

| Environment | `npm install` | Notes |
|---|---|---|
| Local dev, Claude Code, CI | yes | Ordinary public install |
| Figma Make, v0, Bolt, Lovable, StackBlitz, CodeSandbox | yes | Resolve from public npm; this is what going public unlocks |
| **Claude artifacts** | **no** | A strict CSP blocks every external host. No install, no CDN, no fetch. Everything must be inlined in the page |

Claude artifacts cannot be fixed by any registry choice, so v0 ships two extra
build outputs that make the system usable by paste rather than install. Both fall
out of the converters already being written.

**1. `lemonade.css` — a single self-contained file.** Every custom property for both
themes, the `.lmnd-text-*` classes and `.lmnd-icon`, with zero `@import`s, roughly
15KB. Pasteable into a `<style>` block. Published as a package export and attached to
each GitHub release so it can be linked without installing anything.

Figtree needs no bundling in this path: it is on Google Fonts, which is the one
external host Claude artifacts permit.

**2. `llms.txt` — a compact token reference for a model's context.** This is the
larger unlock, and the token export already contains the hard part:

```json
"content-primary":   { "$description": "Use for main text, titles, and essential content." }
"content-secondary": { "$description": "Use for secondary text, such as body copy or supporting content." }
```

Those `$description` fields are human-written usage guidance currently unused by any
platform. Emitted as a reference, they are what moves a model from guessing `#333` to
choosing `var(--lmnd-color-content-secondary)` for the stated reason. Generated by
`scripts/web-llms-txt-converter.main.kts` alongside the other converters, so it can
never drift from the tokens it describes.

Icons remain the one gap in no-install environments — 587 files cannot be pasted —
though a model can inline the few it needs from the public repo.

## 13. CI and release

- **`web_ci.yml`** — on PRs touching `web/**`: typecheck, unit tests, contrast
  check, typography parity check, Storybook build, and a check that the package
  installs and imports cleanly.
- **`web_release.yml`** — on tag `lemonade-web-v*`: build, publish to **public npm**
  with `NODE_AUTH_TOKEN` (`publishConfig.access: "public"`, as
  `teya-blocks-react` does), create a GitHub release with a changelog scoped to
  `web/`. Same shape as `kmp_release.yml`.
  The workflow is written and tested with `npm publish --dry-run` and `npm pack`,
  but **the first real publish happens only after local validation and team
  sign-off** — see §3.
- **`token_drift.yml`** — gains `scripts/web-*` and `web/` in its watched paths and
  its drift check. It needs **no Node**: the converters are Kotlin and the JDK is
  already set up. `check-loader-parity.py` gains web as a fourth loader.

## 14. Testing

**Converters** — `scripts/web-loader-dtcg-test.main.kts`, following the existing
`kmp-loader-dtcg-test.main.kts` pattern and run in the same CI step, covering:
colour to `rgb()` with alpha, number to `rem`, weight-string to numeric, shadow
composition, and name mapping including the `border-selected` collision.

**Generated output** — the CSS and TS are committed, so `token_drift.yml` already
functions as a snapshot test: any change to output shows up as a reviewable diff.

**Package** — Vitest in `web/` over the published TS surface (`tokens`,
`textStyles`, `iconNames`) plus an install-and-import smoke test.

The contrast, loader-parity and text-style-parity checks double as product
guarantees rather than only tests.

## 15. Explicitly out of scope for v0

| Deferred | Why, and what unblocks it |
|---|---|
| React components | The framework decision is deliberately left to adoption. Everything here is framework-agnostic, so no rework is implied |
| Material UI adapter | A hybrid of `var()` and literal values is unavoidable, because MUI computes derived states with `alpha()`/`darken()`, which cannot parse `var()`. Documenting the pattern is honest; shipping a half-solution creates a support burden |
| Tailwind preset | ~30 lines on top of this foundation. Tailwind v4's `@theme` consumes CSS variables natively. Should not gate the release |
| CSS reset | Would fight MUI's `CssBaseline`. Consumers own their reset |
| Motion tokens | None exist in Figma yet |
| Icon sprite | Measured at 123KB gzipped for one icon's worth of value |
| Shared `text-styles.json` across all platforms | Touches KMP and SwiftUI generated code. The CI parity check covers the risk in the meantime |

## 16. Success criteria

1. `npm pack` produces a tarball that installs cleanly into a scratch project from a
   local file path, with the correct `exports` map — verified **before** any publish.
2. A plain HTML file importing `tokens.css` renders Lemonade colours and follows OS
   dark mode with no configuration.
3. An existing MUI app can adopt `tokens.css` with no visual regression to its
   current components.
4. Changing a value in Figma, re-exporting, and running `run-converters.sh`
   regenerates web output; skipping it fails CI.
5. Contrast and typography-parity checks pass, with any exceptions explicitly
   allowlisted.
6. Storybook is deployed and a designer or engineer can find any token by browsing.
7. Pasting `lemonade.css` into a bare HTML page in a Claude artifact renders Lemonade
   colour and type, with Figtree loading from Google Fonts.
8. An AI given `llms.txt` produces markup using semantic token names rather than
   literal hex values.
