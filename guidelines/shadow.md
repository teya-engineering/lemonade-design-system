# Shadow

Five levels, from `LemonadeShadow.Xsmall` to `LemonadeShadow.Xlarge`, applied with
`Modifier.lemonadeShadow(shadow = …, shape = …)`.

## `LDS-SHADOW-LEVEL` · The level follows how far the element floats

**Rule of thumb.** Take the level the components use for the same kind of element:

| Level | For | In Lemonade |
| --- | --- | --- |
| `Xsmall` | Something resting on its surface | `SegmentedControl`'s thumb |
| `Small` | Something raised to be touched | `Switch`'s thumb |
| `Large` | Something floating over the page for a moment | `Toast` |
| `Xlarge` | Something floating over everything else | `Tooltip`, `BottomTabBar` |

No component uses `Medium`.

**Why.** A shadow says how far an element is from the surface under it. A level higher than the
element needs makes the page look stacked, and two neighbours at different levels look like they
belong to different layers.

**Exceptions.** None.

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
