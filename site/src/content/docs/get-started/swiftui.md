---
title: SwiftUI
description: Add Lemonade to a SwiftUI project and render your first component.
---

:::caution[Unverified]
These installation steps have not been walked through end to end. If you follow
them and something is wrong, please correct this page.
:::

Add the package in Xcode via **File → Add Package Dependencies**, pointing at
`github.com/saltpay/lemonade-design-system`, and pick the most recent
`lemonade-swiftui-*` tag (no `v` prefix — e.g. `lemonade-swiftui-0.37.1`).

The same `LemonadeUi` namespace applies:

```swift
import Lemonade

struct MyScreen: View {
    @State private var checked = false

    var body: some View {
        VStack {
            LemonadeUi.Text("Notifications")
            LemonadeUi.Switch(checked: checked, onCheckedChange: { checked = $0 })
        }
    }
}
```

## Using the tokens

There is no theme wrapper to add. `LemonadeTheme` is a namespace of statics, and the
colours resolve through the asset catalog, so they follow light and dark on their own.

```swift
VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
    LemonadeUi.Text(
        "Payment received",
        textStyle: LemonadeTypography.shared.bodyMediumSemiBold,
        color: LemonadeTheme.colors.content.contentPositive
    )
}
.padding(LemonadeTheme.spaces.spacing400)
.background(LemonadeTheme.colors.background.bgDefault)
```

| Tokens | Accessor | Example |
|---|---|---|
| Colour | `LemonadeTheme.colors` | `colors.content.contentPositive` |
| Typography | `LemonadeTypography.shared` | `shared.bodyMediumSemiBold` |
| Spacing | `LemonadeTheme.spaces` | `spaces.spacing400` |
| Radius | `LemonadeTheme.radius` | `radius.radius600` |
| Shape | `LemonadeTheme.shapes` | `shapes.radiusContainerDefault` |
| Size | `LemonadeTheme.sizes` | `sizes.size500` |
| Opacity | `LemonadeTheme.opacity` | `opacity.opacity5` |
| Border width | `LemonadeTheme.borderWidth` | `borderWidth.borderWidth100` |

Colour is grouped the same way [Foundations](/lemonade-design-system/foundations/colour/)
lists it: `background`, `border`, `content`, `interaction`, `scoped` and `shadow`.

Two shorthands are worth knowing. Colours work anywhere SwiftUI takes a `ShapeStyle`:

```swift
Text("Total").foregroundStyle(.content.contentPrimary)
Rectangle().fill(.bg.bgSubtle)
```

And every text style is on `Font`, so it drops straight into `.font(_:)`:

```swift
Text("Total").font(.bodyMediumSemiBold)
```

## Where to go next

- [Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/) — the one
  rule worth reading before you write any styling
- [Lists](/lemonade-design-system/patterns/lists/) — a list screen, done properly
- [Colour](/lemonade-design-system/foundations/colour/) — every token, with its usage
