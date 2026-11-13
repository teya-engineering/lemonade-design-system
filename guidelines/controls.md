# Controls

## `LDS-CONTROL-CHOICE` · Pick the control by what the choice means

**Rule of thumb.** Each control states its job in its own documentation. Reach for the one whose job
matches:

| The choice | Control |
| --- | --- |
| A setting the thing will have, on or off, whether it applies at once or on submit | `Switch`, or a Toggle `SelectListItem` in a list |
| A statement the user agrees to, or several options picked together | `Checkbox` |
| One option from a short group shown in full | `RadioButton`, or a Single `SelectListItem` |
| One or more options from a list shown in full | A Multiple `SelectListItem`, or `BoxSelection` for options that need their own layout |
| A value from a list too long to show, or one the field only summarises | `SelectField`, opening a `Dropdown` or a picker |
| Which view or filter the content below shows | `SegmentedControl` |
| Which of several sections at the same level is on screen | `Tabs` |
| A filter or a choice among many, in a dense layout | `Chip` |

**Why.** People read a control by what it says, not by when it acts. A switch reads as a state: this is
on. A checkbox reads as agreeing or choosing: I accept this, these ones. A setting shown as a checkbox
reads as a question to agree to, and a consent shown as a switch reads as a setting. When a switch only
applies on submit, the submit button already says so.

**`SegmentedControl` and `Tabs` overlap.** Both switch what the screen shows, and the design system
does not yet draw the line between them. Until it does, pick one per kind of screen and keep it.

**Exceptions.** None. The table has no option-count thresholds because the design system sets none.

**Example.** An Instant settlements setting in a settings list, applied the moment it is toggled. A
Repeat this payment setting on a payment's review screen is a switch too, though it applies only when
the payment is sent; the review's "I agree to the terms" is a checkbox.

Compose.

```kotlin
LemonadeUi.SelectListItem(
    label = "Instant settlements",
    type = SelectListItemType.Toggle,
    checked = instantSettlements,
    onItemClicked = { onInstantSettlementsChange(!instantSettlements) },
)
```

SwiftUI.

```swift
LemonadeUi.SelectListItem(
    label: "Instant settlements",
    type: .toggle,
    checked: instantSettlements,
    onItemClicked: { onInstantSettlementsChange(!instantSettlements) }
)
```

HTML kit. A switch row is a `lmnd-kit-list-item` label with the switch in its trailing slot.

```html
<div class="lmnd-kit-card">
  <label class="lmnd-kit-list-item">
    <span class="lmnd-kit-list-item__row">
      <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Instant settlements</span></span>
      <span class="lmnd-kit-list-item__trailing"><input type="checkbox" class="lmnd-kit-switch" role="switch" checked></span>
    </span>
  </label>
</div>
```

**Don't.** A checkbox for an on/off setting, a switch (`Switch`, a Toggle `SelectListItem`, `lmnd-kit-switch`)
for agreeing to terms, or a `SegmentedControl` (`lmnd-kit-segmented`) as a form field choosing a value.
