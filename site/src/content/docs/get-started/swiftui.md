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

## Where to go next

- [Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/) — the one
  rule worth reading before you write any styling
- [Forms](/lemonade-design-system/patterns/forms/) — the most common screen, done properly
- [Colour](/lemonade-design-system/foundations/colour/) — every token, with its usage
