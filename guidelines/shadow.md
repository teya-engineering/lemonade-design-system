# Shadow

Five levels, from Xsmall to Xlarge. Compose applies one with `Modifier.lemonadeShadow(shadow = …,
shape = …)`, SwiftUI with `.lemonadeShadow(_:)`, and the HTML kit with `box-shadow` and
`--lmnd-shadow-xs` to `--lmnd-shadow-xl`.

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

**Example.** A custom draggable handle, raised to be touched, so `Small`.

Compose.

```kotlin
Box(
    modifier = Modifier
        .size(LemonadeTheme.sizes.size800)
        .lemonadeShadow(shadow = LemonadeShadow.Small, shape = LemonadeTheme.shapes.radiusFull)
        .background(color = LemonadeTheme.colors.background.bgDefault, shape = LemonadeTheme.shapes.radiusFull)
        .draggable(state = dragState, orientation = Orientation.Horizontal),
)
```

SwiftUI. `lemonadeShadow` takes the outline from the view it is applied to, so apply it after the fill.

```swift
Circle()
    .fill(LemonadeTheme.colors.background.bgDefault)
    .frame(width: LemonadeTheme.sizes.size800, height: LemonadeTheme.sizes.size800)
    .lemonadeShadow(.small)
    .gesture(dragGesture)
```

HTML kit. The levels are `--lmnd-shadow-xs`, `-sm`, `-md`, `-lg` and `-xl`.

```html
<div aria-hidden="true" style="width: var(--lmnd-size-800); height: var(--lmnd-size-800); border-radius: var(--lmnd-radius-full); background: var(--lmnd-color-bg-default); box-shadow: var(--lmnd-shadow-sm)"></div>
```

**Don't.** `Large`, `.large` or `--lmnd-shadow-lg` on something that sits in the page.

## `LDS-SHADOW-NOT-A-BORDER` · A shadow shows elevation and nothing else

**Rule.** A shadow never marks selection, never stands in for a border or a divider, and never adds
depth to a surface the design system draws flat. One element has one shadow level.

**Why.** Selection is a colour and a border (`borderSelected`), and separation on the same surface is
a background step or a divider. A shadow used for either reads as the element lifting off the page.

**Exceptions.** None. `LemonadeUi.Card` has no shadow, and adding one is a hand-rolled card under
`LDS-CARD-NO-HANDROLL`.

**Example.** A payout frequency choice: the chosen option shows the `borderSelected` outline and no
shadow.

Compose.

```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200)) {
    LemonadeUi.BoxSelection(
        modifier = Modifier.weight(1f),
        variant = LemonadeBoxSelectionVariant.Outlined,
        isSelected = frequency == PayoutFrequency.Weekly,
        onClick = { onFrequency(PayoutFrequency.Weekly) },
    ) {
        LemonadeUi.Text(text = "Weekly")
    }
    LemonadeUi.BoxSelection(
        modifier = Modifier.weight(1f),
        variant = LemonadeBoxSelectionVariant.Outlined,
        isSelected = frequency == PayoutFrequency.Monthly,
        onClick = { onFrequency(PayoutFrequency.Monthly) },
    ) {
        LemonadeUi.Text(text = "Monthly")
    }
}
```

SwiftUI.

```swift
HStack(spacing: LemonadeTheme.spaces.spacing200) {
    LemonadeUi.BoxSelection(
        variant: .outlined,
        isSelected: frequency == .weekly,
        stretched: true,
        onClick: { frequency = .weekly }
    ) {
        LemonadeUi.Text("Weekly")
    }
    LemonadeUi.BoxSelection(
        variant: .outlined,
        isSelected: frequency == .monthly,
        stretched: true,
        onClick: { frequency = .monthly }
    ) {
        LemonadeUi.Text("Monthly")
    }
}
```

HTML kit. A checked radio inside `lmnd-kit-box-selection` draws the outline.

```html
<div class="lmnd-kit-row" style="flex-wrap: nowrap">
  <label class="lmnd-kit-box-selection lmnd-kit-box-selection--outlined" style="flex: 1; align-items: center"><input type="radio" name="frequency" checked><span class="lmnd-text-body-small-medium">Weekly</span></label>
  <label class="lmnd-kit-box-selection lmnd-kit-box-selection--outlined" style="flex: 1; align-items: center"><input type="radio" name="frequency"><span class="lmnd-text-body-small-medium">Monthly</span></label>
</div>
```

**Don't.** `Medium` or `--lmnd-shadow-md` on the chosen option, or `Xsmall` or `--lmnd-shadow-xs` on a
card to separate it from the screen.
