# Controls

## `LDS-CONTROL-CHOICE` · Pick the control by what the choice does

**Rule of thumb.** Each control states its job in its own documentation. Reach for the one whose job
matches:

| The choice | Control |
| --- | --- |
| An on/off that takes effect the moment it is changed | `Switch`, or a Toggle `SelectListItem` in a list |
| An on/off, or several, confirmed later by a submit | `Checkbox` |
| One option from a short group shown in full | `RadioButton`, or a Single `SelectListItem` |
| One or more options from a list shown in full | A Multiple `SelectListItem`, or `BoxSelection` for options that need their own layout |
| A value from a list too long to show, or one the field only summarises | `SelectField`, opening a `Dropdown` or a picker |
| Which view or filter the content below shows | `SegmentedControl` |
| Which of several sections at the same level is on screen | `Tabs` |
| A filter or a choice among many, in a dense layout | `Chip` |

**Why.** A control promises a behaviour before it is touched. A switch says the change is already
made, so a switch that waits for a Save button lies about it. A checkbox says nothing happens until
the form is sent.

**`SegmentedControl` and `Tabs` overlap.** Both switch what the screen shows, and the design system
does not yet draw the line between them. Until it does, pick one per kind of screen and keep it.

**Exceptions.** None. The table has no option-count thresholds because the design system sets none.

**Example.** An Instant settlements setting in a settings list, applied the moment it is toggled.

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

**Don't.** A switch (`Switch`, a Toggle `SelectListItem`, `lmnd-kit-switch`) in a form that only saves on
Submit, or a `SegmentedControl` (`lmnd-kit-segmented`) as a form field choosing a value.
