# Shadow

Five levels, from `LemonadeShadow.Xsmall` to `LemonadeShadow.Xlarge`, applied with
`Modifier.lemonadeShadow(shadow = …, shape = …)`.

## `LDS-SHADOW-LEVEL` · The level follows how far the element floats

**Rule of thumb.** Take the lowest level that separates the element:

| Level | For |
| --- | --- |
| `Xsmall` | Something resting on its surface — a segmented control's thumb |
| `Small` | Something raised to be touched — a switch's thumb, a dropdown |
| `Medium` | Something floating over the page — a popover, a tooltip, a toast |
| `Large` | The highest overlays — a dialog, a side panel |

`Xlarge` has no use the system has named.

**Why.** A shadow says how far an element is from the surface under it. A level higher than the
element needs makes the page look stacked, and two neighbours at different levels look like they
belong to different layers.

**Exceptions.** None.

**Deviations.** `Tooltip` uses `Xlarge` and `Toast` uses `Large`, where the table says `Medium`.

**Do / don't.** `Small` on a custom draggable handle. Not `Large` on something that sits in the page.

## `LDS-SHADOW-NOT-A-BORDER` · A shadow shows elevation and nothing else

**Rule.** A shadow never marks selection, never stands in for a border or a divider, and never adds
depth to a surface the design system draws flat. One element has one shadow level.

**Why.** Selection is a colour and a border (`borderSelected`), and separation on the same surface is
a background step or a divider. A shadow used for either reads as the element lifting off the page.

**Exceptions.** None. `LemonadeUi.Card` has no shadow, and adding one is a hand-rolled card under
`LDS-CARD-NO-HANDROLL`.

**Do / don't.** A `borderSelected` outline on the chosen option. Not `Medium` on the chosen option, and
not `Xsmall` on a card to separate it from the screen.
