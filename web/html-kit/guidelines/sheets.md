# Sheets

## `LDS-SHEET-CHROME` · The scaffold owns the chrome

**Kind.** Rule.

**Rule.** A sheet uses the scaffold: `lmnd-kit-sheet__handle`, then `lmnd-kit-sheet__bar` with the close button on
the left and optional actions on the right, then `lmnd-kit-sheet__title`. A sheet closes with Close (`times`),
never Back.

```html
<!-- In the lmnd-kit-device, after the lmnd-kit-screen. A link to #filter opens it. -->
<div class="lmnd-kit-scrim" id="filter">
  <div class="lmnd-kit-sheet lmnd-kit-sheet--subtle" role="dialog" aria-labelledby="filter-title">
    <span class="lmnd-kit-sheet__handle"></span>
    <div class="lmnd-kit-sheet__bar">
      <a href="#" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Close"><span class="lmnd-icon" data-icon="times"></span></a>
      <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Reset"><span class="lmnd-icon" data-icon="arrow-rotate-ccw"></span></button>
    </div>
    <h2 class="lmnd-kit-sheet__title lmnd-text-heading-large" id="filter-title">Filter</h2>
    <div class="lmnd-kit-sheet__body">…</div>
    <div class="lmnd-kit-sheet__actions">…</div>
  </div>
</div>
```

**Why.** Hand-built chrome gets the title's typography and the close button's position slightly wrong,
and every sheet gets them wrong differently. Back on a sheet promises a screen underneath that the
gesture does not go to.

**Exceptions.** A sheet that is genuinely a step inside a sheet-hosted flow may carry Back
(`arrow-left`) to reach the previous step, as long as the flow's root still closes with Close.

**Do / don't.** The scaffold's parts. Not a heading and an icon button laid out at the top of
`lmnd-kit-sheet__body`, and not a second drag handle.

## `LDS-SHEET-BAR-ACTIONS` · A sheet's bar follows the top bar's button rules

**Kind.** Rule.

**Rule.** The buttons in `lmnd-kit-sheet__bar` follow `LDS-TOP-BAR-ACTIONS`, exactly as in an app's top bar:
circular, Small icon buttons, or one Small text button such as Reset or Done in its pill shape. The sheet's primary action,
if it has one in the bar, is Primary · Solid, a destructive one is Critical · Subtle (`LDS-TOP-BAR-CRITICAL`),
and every other action, Close included, is Neutral · Subtle.
Two or more trailing actions go in a `lmnd-kit-sheet__trailing` group.

```html
<div class="lmnd-kit-sheet__bar">
  <a href="#" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Close"><span class="lmnd-icon" data-icon="times"></span></a>
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--small lmnd-kit-button--full-shape lmnd-text-body-small-semibold"><span class="lmnd-button__label">Reset</span></button>
</div>
```

**Why.** A sheet's bar sits where a top bar sits and does the same job. Different buttons there make the
same action look different depending on whether it opened as a screen or a sheet.

**Exceptions.** None.

**Do / don't.** A Neutral · Subtle Reset button on the right of the bar. Not a bare text link, and not a
ghost button.
