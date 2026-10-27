# Top bar

## `LDS-TOP-BAR-ACTIONS` · Top bar buttons are Solid or Subtle

**Kind.** Rule.

**Rule.** A top bar's buttons are always round: circular, Small icon buttons
(`lmnd-kit-icon-button--circular`), or a single Small text button such as Reset or Done in its pill shape
(`lmnd-kit-button--full-shape`, SwiftUI's `.fullShape()`). Either way, the type says what the action is:

- **The primary action** — the one thing the screen is for, such as adding, creating or saving — is
  Primary · Solid: `lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid`, or
  `lmnd-button--primary lmnd-button--solid lmnd-button--small` for text.
- **Every other action** is Neutral · Subtle: the icon button's default `lmnd-kit-icon-button`, or
  `lmnd-button--neutral lmnd-button--subtle lmnd-button--small` for text. Back and Close are secondary
  actions, so they are Subtle too.

How many actions a bar carries is the app's call (`APP-TOPBAR-TRAILING`).

```html
<div class="lmnd-kit-top-bar__trailing">
  <a href="settings.html" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Settings"><span class="lmnd-icon" data-icon="gear"></span></a>
  <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="New sale"><span class="lmnd-icon" data-icon="plus"></span></button>
</div>

<div class="lmnd-kit-top-bar__trailing">
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--small lmnd-kit-button--full-shape lmnd-text-body-small-semibold"><span class="lmnd-button__label">Reset</span></button>
</div>
```

**Why.** A filled button reads as something to press. A ghost icon on the top bar's background looks like
a label rather than a control, and two treatments for the same weight of action make the reader work
out which one matters. Solid marks the one action the screen wants; Subtle keeps every other action
visible without competing with it.

**Exceptions.** None. Ghost is never used in a top bar, and neither is a button with square corners.

**Do / don't.** A Primary · Solid add button next to a Subtle settings button. Not a ghost settings
button, and not a Neutral · Solid one.

## `LDS-TOP-BAR-DESCRIPTION` · A large title's description sits right under it

**Kind.** Rule.

**Rule.** A screen with a large title puts its description inside the title block, directly under the
title, with no gap: `lmnd-kit-large-title__description` in the screen's `lmnd-kit-large-title`, or
`lmnd-kit-top-bar__large-subtitle` in a top bar's `lmnd-kit-top-bar__large`. It is never a paragraph of
its own in the content. It follows `LDS-TEXT-INTRO`: at most two lines of `lmnd-text-body-small-regular`
in `content-secondary`.

```html
<main class="lmnd-kit-content">
  <div class="lmnd-kit-large-title">
    <h1 class="lmnd-text-heading-large">What can Dan do?</h1>
    <p class="lmnd-kit-large-title__description lmnd-text-body-small-regular">Tick what this role can do</p>
  </div>
  …
</main>
```

**Why.** The title and its description are one unit, and the native TopBar draws them as one: its
subtitle sits under the large title with nothing between. A description written as its own paragraph
takes the content's gap above it, drifts away from the title it explains and reads as the start of the
content.

**Exceptions.** A screen with an inline title (`lmnd-kit-top-bar--small`, `APP-TOPBAR-TITLE`) has no
title block to hold it; its intro is the content's first paragraph, `lmnd-kit-intro`.

**Do / don't.** "What can Dan do?" with "Tick what this role can do" directly under it. Not the
description as an `lmnd-kit-intro` after the `lmnd-kit-large-title`, a gap below the title.
