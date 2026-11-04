# Controls

## `LDS-CONTROL-CHOICE` · Pick the control by what the choice does

**Rule of thumb.** Each control states its job in its own documentation. Reach for the one whose job
matches:

| The choice | Control |
| --- | --- |
| An on/off that takes effect the moment it is changed | `Switch`, or `SelectListItem` with `type = Toggle` in a list |
| An on/off, or several, confirmed later by a submit | `Checkbox` |
| One option from a short group shown in full | `RadioButton`, or `SelectListItem` with `type = Single` |
| One or more options from a list shown in full | `SelectListItem` with `type = Multiple`, or `BoxSelection` for options that need their own layout |
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

**Do / don't.** A `Switch` for "Instant settlements" that applies on toggle. Not a `Switch` in a form
that only saves on Submit, and not a `SegmentedControl` as a form field choosing a value.
