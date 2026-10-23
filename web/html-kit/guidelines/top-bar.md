# Top bar

## `LDS-TOP-BAR-ACTIONS` · Top bar buttons are Solid or Subtle

**Kind.** Rule.

**Rule.** A top bar's buttons are circular, Small icon buttons, or a single Small text button such as
Reset or Done. Either way, the type says what the action is:

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
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--small lmnd-text-body-small-semibold"><span class="lmnd-button__label">Reset</span></button>
</div>
```

**Why.** A filled button reads as something to press. A ghost icon on the top bar's background looks like
a label rather than a control, and two treatments for the same weight of action make the reader work
out which one matters. Solid marks the one action the screen wants; Subtle keeps every other action
visible without competing with it.

**Exceptions.** None. Ghost is never used in a top bar.

**Do / don't.** A Primary · Solid add button next to a Subtle settings button. Not a ghost settings
button, and not a Neutral · Solid one.
