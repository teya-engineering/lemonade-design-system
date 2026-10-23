# Buttons

## `LDS-BUTTON-PAIR` · At most two actions, primary above secondary

**Kind.** Rule.

**Rule.** A screen offers at most two actions, the primary above the secondary, in `lmnd-kit-actions` (or
`lmnd-kit-sheet__actions` in a sheet).

The primary is Primary · Solid · Large. The secondary in the hierarchy is always Neutral · Subtle · Large.
A destructive primary is Critical · Solid over the same secondary.

```html
<div class="lmnd-kit-actions">
  <button type="button" class="lmnd-button lmnd-button--primary lmnd-button--solid lmnd-button--large lmnd-text-body-medium-semibold">
    <span class="lmnd-button__label">Confirm</span>
  </button>
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--large lmnd-text-body-medium-semibold">
    <span class="lmnd-button__label">Not now</span>
  </button>
</div>
```

**Why.** Two buttons of equal weight ask the user to decide which one the screen wants. A third action
means the screen is doing two jobs.

**`lmnd-button--secondary` is a branded treatment, not a position in the hierarchy.** It never means "the
second button".

**Exceptions.** A third action belongs in the top bar (`lmnd-kit-top-bar__trailing`) or in the content, not in
the action block.

**Do / don't.** A primary above a Neutral · Subtle button. Not two primaries stacked, and not a primary
above a `--secondary` one.

## `LDS-BUTTON-EXPLICIT-TYPE` · Only a primary relies on the defaults

**Kind.** Rule.

**Rule.** Any variant other than Primary states its fill treatment.

**Why.** In the app, `variant` and `type` are independent and `type` defaults to Solid, so a Neutral
button with no type renders a filled button where a subtle one was meant. A prototype is read as the spec
for that call, so it names the treatment the app has to ask for.

**Exceptions.** None. In the kit every button carries its treatment class anyway, because HTML has no
defaults (`LHK-KIT-MARKUP`); the rule is about which treatment, not whether the class is there.

**Do / don't.** `lmnd-button--neutral lmnd-button--subtle`. Not a Neutral button left Solid because
nobody chose.
