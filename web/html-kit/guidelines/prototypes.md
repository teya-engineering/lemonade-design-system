# Prototypes

Rules for building a page with the kit. They sit on top of the `LDS-` rules, which say what the
components are for; these say how a prototype is put together so it stays true to the app.

## `LHK-TOKENS-ONLY` · Every value is a token

**Kind.** Rule.

**Rule.** A page's own `<style>` and `style=""` attributes use `var(--lmnd-*)` tokens for every colour,
space, size, radius, border width, opacity and shadow. Text takes an `lmnd-text-*` class, never a
`font-size`, `font-weight`, `line-height` or `font-family`.

**Why.** The tokens are what the app is built from, so a token value is a value the app can actually
draw. A literal looks right until the design system moves, and in dark mode a literal colour is wrong
straight away. A `var()` that names nothing silently drops the whole declaration, so take every name
from `llms.txt`, which also says what each colour is for.

**Exceptions.** `0`, `100%`, `auto`, `1fr` and other values that aren't design decisions.

**Do / don't.** `gap: var(--lmnd-spacing-300)`. Not `gap: 12px`, and not `font-size: 14px` on a label.

## `LHK-KIT-MARKUP` · Copy the markup, never restyle it

**Kind.** Rule.

**Rule.** A component's markup comes from `gallery.html` or `examples/`, with every class it carries:
block, variant, treatment, size and the text style. A page never overrides a `lmnd-kit-*` or `lmnd-*` rule —
no local CSS that targets a kit class to change its padding, colour or radius — and never invents a class
with either prefix.

**Why.** The kit's classes reproduce the native components' measurements. A local override makes the
prototype show something the app can't build, and the developer who builds the screen from it either
copies the wrong value or argues it back out. HTML has no defaults, so a missing class is a missing
style, not the component's default.

**Exceptions.** Layout around components — the grid a pair of cards sits in, the gap between sections —
belongs to the page and goes in a page class of its own, using tokens (`LHK-TOKENS-ONLY`).

**Do / don't.** `<div class="currencies">` with its own grid rule around two `lmnd-kit-box-selection`s. Not
`.lmnd-kit-card { border-radius: 12px }` in the page's `<style>`.

## `LHK-SCREEN` · A screen is built like the app's

**Kind.** Rule.

**Rule.** Every screen:

- starts from the closest file in `examples/`;
- sits in a phone frame, `<div class="lmnd-kit-device">`, as the only thing in `<body>`. The frame is a 393 × 852
  phone with a status bar and home indicator on a wide window, and drops away on a phone;
- is one `lmnd-kit-screen` (`bg-subtle`), or `lmnd-kit-screen lmnd-kit-screen--default` (`bg-default`)
  when it holds no cards (`LDS-CARD-SURFACE`), holding a `lmnd-kit-top-bar`, a `lmnd-kit-content`, an
  `lmnd-kit-actions` block when it has fixed actions, and, on a section root such as Sales, an
  `lmnd-kit-tab-bar` last;
- is checked in the frame, in both light and dark, before it is handed over.

A top-level screen uses the compact top bar with no back button; a pushed screen uses the navigation top
bar with Back; a details screen uses the small one (`lmnd-kit-top-bar--small`, the centred title only) on an
`lmnd-kit-screen--details` (`APP-DETAILS-TOP-BAR`); a screen presented over another uses Close.

**Why.** The examples carry the app's page background, top bar and spacing. A screen that starts blank
gets those slightly wrong. A screen outside the frame stretches to the window and stops looking like a
phone, and a screen only checked in light hides every literal colour.

```html
<body>
  <div class="lmnd-kit-device">
    <div class="lmnd-kit-screen">…</div>
    <!-- overlays, each with an id -->
  </div>
</body>
```

**Exceptions.** A page that isn't a phone screen, such as a gallery or a side-by-side comparison of
screens.

## `LHK-SAFE-AREAS` · Nothing sits under the status bar or the home indicator

**Kind.** Rule.

**Rule.** The phone keeps two areas for itself: the status bar at the top and the home indicator at the
bottom. The kit's components keep clear of both, so a screen gets this by using them:

- **The status bar** sits over the top bar. `lmnd-kit-top-bar` reaches up behind it and carries its own
  background there, so the status bar always shows what the top bar shows — the screen's background at
  rest, the filled bar once a details screen scrolls. Every screen starts with a top bar for that reason;
  content never begins under the status bar.
- **The home indicator** sits over the bottom of the screen. Whatever is last — `lmnd-kit-content`, the
  `lmnd-kit-actions` block, the `lmnd-kit-tab-bar`, a sheet — keeps a safe area above it, and a toast sits
  above it too.

The areas are `--lmnd-kit-safe-top` and `--lmnd-kit-safe-bottom`: the phone frame's status bar and home
indicator, or the real phone's safe areas when the page is opened on one. A screen never adds its own
padding for them.

**Why.** Content under the status bar collides with the time and the battery, and the last row of a list
under the home indicator can't be read or tapped. A status bar that keeps the screen's colour while the top
bar fills draws a seam the app never shows.

**Exceptions.** None.

**Do / don't.** A screen whose last card ends above the home indicator, and whose status bar turns white
with its top bar. Not a `padding-bottom: 34px` on a page's own wrapper, and not a screen with no top bar.

## `LHK-ASSETS` · Icons, flags, logos and badges come from the kit

**Kind.** Rule.

**Rule.** Icons are `<span class="lmnd-icon" data-icon="…">`, flags `<span class="lmnd-kit-flag" data-flag="…">`,
brand logos `<span class="lmnd-kit-logo" data-logo="…">`, and a transaction's status badge
`<span class="lmnd-kit-symbol__status" data-badge="…">` (`APP-FEED-ROWS`), using names from `assets.txt`. No
inline SVG, icon font, emoji or image from elsewhere stands in for one.

**Why.** These are the app's assets: the icons, flags and logos generated from Lemonade's Figma file, the
badges copied from the app's own. `lemonade-kit.css` carries them, the build refuses a name that doesn't
exist, and logos and badges swap to their dark artwork in dark mode.
An asset from elsewhere gets none of that and won't be in the app.

**Exceptions.** Content imagery the app loads at runtime — a product photo, a merchant's logo, an avatar
— which stands in as an `<img>` inside a `lmnd-kit-symbol`, with a note in the hand-over that it is a
placeholder.

## `LHK-COPY` · Realistic copy in Teya's voice

**Kind.** Rule of thumb.

**Rule.** Copy is British English, plain and short. Sample data is realistic: real-looking names,
`£` amounts with pence, dates as `12 Oct 2026`, times as `14:32`. Use `•` (U+2022) as an inline
separator and `-` for a negative amount (`-£12.00`).

**Why.** A prototype is read as the screen it will become. Lorem ipsum hides how long real copy runs, and
placeholder numbers hide how amounts and dates line up in a row.

**Exceptions.** None for lorem ipsum. Copy still being written can be marked as a draft in the hand-over.

## `LHK-FLOWS` · A flow is linked screens

**Kind.** Rule.

**Rule.** A flow is one `.html` file per screen, linked with `<a href="next.html">`. A sheet, dialog,
dropdown, side menu or toast over a screen lives in that screen's file, in the `lmnd-kit-device` after the
`lmnd-kit-screen`, with an `id`. A link to `#that-id` opens it and a link to `#` closes it. Script follows
`LHK-SCRIPT`. A flow that has to travel as one file keeps its screens together instead (`LHK-SCREENS`).

**Why.** Linked pages and linked overlays click through on any device with nothing to install, the
browser's back button closes an open sheet, and the markup stays the same markup a reader copies.

**Exceptions.** A state that HTML can't reach without script — a timer, an animation sequence — is shown
as its own screen instead.

## `LHK-SCREENS` · A flow in one file is several screens in one device

**Kind.** Rule.

**Rule.** When a flow has to be one file — a Claude.ai artifact, a standalone page sent to someone — its
screens sit side by side in the one `lmnd-kit-device`, each a `lmnd-kit-screen` with an `id`, the first being
where the flow starts. Overlays follow the screens, as in `LHK-FLOWS`.

- A link to a screen's id moves to it: `<a href="#details">`.
- Back is a link to the screen it returns to, marked `data-lmnd-kit-back`: `<a href="#transactions"
  data-lmnd-kit-back …>`.
- A tab bar's links point at its tabs' screens.

```html
<div class="lmnd-kit-device">
  <div class="lmnd-kit-screen" id="transactions">… <a href="#details" class="lmnd-kit-list-item">…</a> …</div>
  <div class="lmnd-kit-screen lmnd-kit-screen--details" id="details">… <a href="#transactions" data-lmnd-kit-back class="lmnd-kit-icon-button …" aria-label="Back">…</a> …</div>
  <div class="lmnd-kit-scrim" id="store-sheet">…</div>
</div>
```

With the kit's script, a link pushes the next screen in from the right, Back and the browser's back button
slide back, a tab switches without sliding, and closing a sheet stays on its screen. Without it, the screen a
link targets shows, with no slide; a sheet over any screen but the first then shows over the first.
`examples/flow.html` is two screens in one file.

**Why.** A Claude.ai artifact or a standalone file can't link to another file, so a flow sent as one file
needs its screens inside it. Keeping them as screens, not pages stitched together with script, keeps each
one the same markup as a screen on its own.

**Exceptions.** None. A flow that travels as a folder keeps one file per screen (`LHK-FLOWS`).

## `LHK-SCRIPT` · Only the kit's script, and nothing depends on it

**Kind.** Rule.

**Rule.** A page may link the kit's own script after its stylesheet, and no other script:

```html
<link rel="stylesheet" href="../lemonade-kit.css">
<script src="../lemonade-kit.js" defer></script>
```

`lemonade-kit.js` only adds to markup that already works:

| Kit markup | What the script adds |
| --- | --- |
| Screens with `id`s in one device (`LHK-SCREENS`) | Slides between them: in from the right on a link, back on Back or the browser's back button, no slide on a tab |
| An overlay with an `id` (sheet, dialog, dropdown, side menu) | Animates it out as it closes, whichever way it closes — it animates in without the script. Esc and a tap on the scrim close it; focus moves into it and back to what opened it |
| A `lmnd-kit-segmented` or `lmnd-kit-tabs` | Slides one pill, or one underline, to the option picked, rather than each option lighting up |
| A tab or segmented option with `aria-controls="panel-id"` | Shows its panel and hides the others. The panels it doesn't show carry `hidden` in the markup, so without script the first stays |
| A navigation top bar with a large title under its row | Folds the large title into the bar's small one as the screen scrolls |
| The top bar of a details screen or of Home | Fills it once the screen scrolls, in browsers that can't do it in CSS |
| A `lmnd-kit-day-strip` with an `aria-current` day | Opens scrolled to that day |

Every page still works without it, in an email attachment, a preview that blocks script or a sandbox, so
nothing a reader needs may depend on it.

**Why.** The kit's script makes a demo feel like the app without making the page code. A page's own script
makes a prototype only its author can change, and one no review can check against these rules.

**Exceptions.** None.
