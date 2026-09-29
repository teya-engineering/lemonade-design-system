---
applyTo: "kmp/{ui,expressive,calendar}/src/**/*.kt,swiftui/Sources/Lemonade/**/*.swift"
---

# Text scaling across the design system

Status: proposal, not agreed yet. If we agree on it, it becomes the rule for any component
that puts text inside a fixed dimension.

Reference implementation: [#298](https://github.com/saltpay/lemonade-design-system/pull/298)
(`BottomTabBar`, MOP-342).

## Why this exists

While fixing `BottomTabBar` for MOP-342 I went looking for whether the same mistake lived
anywhere else. It does. `Button` clips its label on a stock Android phone at 200% text, and
`Button` is the most used component we ship.

This proposes a contract, the fix for each platform, and an order to work through them.

## The pattern

Someone sets a height in `dp` or `pt` around content that contains text. The text grows with
the user's text-size setting. The height does not. The budget is usually drawn for the default
scale with nothing to spare, and then Compose removes the overflow with a `clip`. SwiftUI
does not clip, so the text just draws outside its own background.

```kotlin
// The shape of the bug, in Compose
Row(
    modifier = Modifier
        .requiredHeight(height = 48.dp)   // fixed
        .clip(shape = pillShape)          // and clipped
) {
    LemonadeUi.Text(text = label)         // scales with fontScale
}
```

It is the same bug every time. The SwiftUI package is a port of the KMP one, so the same
components carry the same fixed heights, sometimes at the same line numbers.

### Why nobody caught it

The budget tends to close exactly at the default scale. `BottomTabBar` was 16dp padding +
20dp icon + 2dp spacer + 16sp line height, against a fixed `ItemHeight` of 54.dp. Adds up to
54 on the nose. Nothing looks wrong until a user touches their text size, and then it breaks
on the first step up.

## What I found

Checked on a Pixel 10a and a Pixel 7 emulator for Android, and an iPhone 17 simulator running
iOS 26.4 for SwiftUI. Screenshots are in the PR.

| component | platform | status | what happens |
|---|---|---|---|
| `BottomTabBar` | KMP | confirmed | Instrumented with `onTextLayout`. Every label reports `didOverflowHeight = true` at `fontScale 1.15`. At `2.0` the label gets `42px` of the `84px` it needs, so you see exactly half the glyph. Fixed in #298. |
| `Button` | KMP | confirmed | Labels clipped at `fontScale 2.0` on all four sizes and both variants. `Button.kt:428` does `.requiredHeight()` then `.clip()`. |
| `SegmentedControl` | KMP | confirmed | At `2.0` the `Large` size fills its container exactly. Not clipped, but nothing left over. `SegmentedControl.kt:210`. |
| `SegmentedControl` | SwiftUI | confirmed | At `AX5` the labels sit outside the pill. The descender on "Day" hangs below it. `LemonadeSegmentedControl.swift:167,182`. |
| `Badge` | SwiftUI | confirmed | At `AX5` the text runs above and below the capsule. `LemonadeBadge.swift:110`. The label grows 2.72× between `large` and `AX5` against a fixed `.frame(height:)`. |
| `Tabs` | SwiftUI | fine today | Uses `.frame(minHeight:)` instead of `.frame(height:)`, and grows properly at `AX5`. |
| `Button` | SwiftUI | partly checked | The default size survives `AX5`. I did not check the others. |
| `InlineCalendar` | KMP | different problem | Branches on `density.fontScale > 1.3`. `InlineCalendar.kt:57,194`. |

### Button break points, by arithmetic

Budget is `requiredHeight` minus twice the vertical padding, against `lineHeight × scale`.

| size | `requiredHeight` | vertical padding | available | line height | clips above |
|---|---|---|---|---|---|
| `XSmall` | `size800` 32dp | `spacing100` 4dp | 24dp | 20sp | 1.20 |
| `Small` | `size1000` 40dp | `spacing200` 8dp | 24dp | 20sp | 1.20 |
| `Medium` | `size1200` 48dp | `spacing300` 12dp | 24dp | 24sp | 1.00 |
| `Large` | `size1400` 56dp | `spacing300` 12dp | 32dp | 24sp | 1.33 |

That is arithmetic off the tokens, not measurement. The same arithmetic said `BottomTabBar`
would have zero headroom at 1.0 and the instrumented run agreed, so I trust the method. I
would still check each component rather than take the table on faith.

`Medium` having no headroom at all at the default scale is the number I would look at first.

## What the platforms say

Android 14 took the ceiling from 130% to 200% and made anything above 100% non-linear. Small
text can double; text that is already big barely moves. That is why `scaledDensity` is
deprecated, and why the docs now say `fontScale` is *"for informational purposes only, because
fonts are no longer scaled with a single scalar value."*

Apple has twelve Dynamic Type sizes. Seven standard ones, `xSmall` through `xxxLarge` with
`Large` as the default, plus five accessibility sizes `AX1` to `AX5` that only appear once the
user turns on Larger Accessibility Sizes. Body text lands around 310% at `AX5`. In code the
line is `DynamicTypeSize.isAccessibilitySize`, true from `AX1` up. Apple picked that threshold
so we do not have to.

On bars specifically, WWDC24 "Get started with Dynamic Type" says bar heights should not scale:
*"If the tab bar height were to increase when large text is enabled, it would occupy almost a
quarter of the screen."* The same talk says not to drop content either: *"ensure that
functionality and essential content are not lost."* Apple gets to have both because of the
Large Content Viewer, where pressing and holding a bar control brings up a large icon and label.

Android has no equivalent, so for bars we have to give one of them up. Material is comfortable
with that. `labelVisibilityMode` has an `UNLABELED` option, and its `AUTO` mode already hides
labels on unselected items once you pass four.

## The contract

1. Support 200% on Android and `AX5` on iOS. That covers both platform ceilings, and roughly
   what WCAG 1.4.4 asks for.
2. Nothing clips text inside that range. A fixed dimension around text becomes a floor.
3. Do not branch on `fontScale`. Measure. With non-linear scaling the same `fontScale` gives
   different rendered sizes depending on the base size, so any threshold is calibrated against
   a number Android tells us not to read.
4. Bars can opt out of rule 2. They may stay compact and drop labels, as long as the label
   survives as a content description.
5. Icons stay fixed. Both platforms agree there, so `LemonadeAssetSize` stays in `dp` and `pt`.
6. Text that is already big grows less, but it grows. Use the platform's own per-size curve to get
   that, never a hand-written opt-out. A style may stop growing only once it is rendering at least
   as large as `bodyMediumRegular` does at the same setting.

## Fix recipes

### Compose: make the cap a floor

```kotlin
// Before: fixed height, and the clip removes whatever spills
Modifier
    .requiredHeight(height = size.requiredHeight)
    .clip(shape = size.shape)

// After: a floor, so the component grows rather than cutting
Modifier
    .heightIn(min = size.requiredHeight)
    .clip(shape = size.shape)
```

Where a parent needs a real height to position a sibling against, a selection indicator say,
take the height from the content and let the sibling fill it:

```kotlin
Box(
    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
) {
    Box(modifier = Modifier.fillMaxHeight())   // indicator tracks the row
    Row { /* items, each .heightIn(min = ItemHeight) */ }
}
```

One caveat. `IntrinsicSize.Min` there is a height intrinsic. `HorizontalFloatingToolbar` asks
its content for width intrinsics, and `SubcomposeLayout` throws when you do that, which is why
`BoxWithConstraints` is off the table inside it.

### Compose: measure instead of guessing

If a component really has to change shape once the text stops fitting, measure the text against
the room it has:

```kotlin
@Composable
private fun rememberLabelsFit(items: List<Item>, rowWidthPx: Int): Boolean {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = LemonadeTheme.typography.bodyXSmallMedium.textStyle
    val gutterPx = with(LocalDensity.current) {
        (LemonadeTheme.spaces.spacing100 * 2).roundToPx()
    }

    return remember(rowWidthPx, items, labelStyle, textMeasurer, gutterPx) {
        if (rowWidthPx == 0 || items.isEmpty()) return@remember true
        val availableWidthPx = rowWidthPx / items.size - gutterPx
        items.all { item ->
            textMeasurer.measure(
                text = item.label,
                style = labelStyle,
                maxLines = 1,
            ).size.width <= availableWidthPx
        }
    }
}
```

`rememberTextMeasurer()` measures without laying anything out, so it works in the places
`SubcomposeLayout` cannot.

### SwiftUI: minHeight, or @ScaledMetric

```swift
// Before
.frame(height: size.containerHeight)

// After, which is what Tabs already does
.frame(minHeight: size.containerHeight)
```

For dimensions that have to track the text rather than sit still, an inset or a thumb, scale
them instead of pinning them:

```swift
@ScaledMetric(relativeTo: .body) private var containerHeight: CGFloat = 48
```

Right now nothing in `swiftui/Sources/Lemonade` uses `@ScaledMetric`.

### SwiftUI: switching layout at accessibility sizes

```swift
@Environment(\.dynamicTypeSize) private var dynamicTypeSize

var layout: AnyLayout {
    dynamicTypeSize.isAccessibilitySize
        ? AnyLayout(VStackLayout())
        : AnyLayout(HStackLayout())
}
```

This is the one threshold worth keeping, because Apple defines it and we do not.

## Text that is already big

Rule 2 is about text that gets cut off. This is the opposite case. A balance on a card is set in
`displayLarge`, 48 units. At `AX5` it does not need to be three times that. It needs to be somewhat
bigger and still fit on the card.

The instinct is to give those styles an opt-out. Both platforms already ship one, worked out per
size, and it is better than anything we would write.

### Android already does it

`LemonadeTypographyExtensions.kt:47-48` emits plain `.sp` for both `fontSize` and `lineHeight`, so
every style goes through the Android 14 non-linear converter. Measured on a Pixel 10a (Android 16)
at `font_scale 2.0`, the platform ceiling. `actual` is what Compose resolves; `linear` is what a
naive `sp * fontScale` would have produced.

| style | base | actual | linear | effective sp |
|---|---|---|---|---|
| `display3XLarge` | 72sp | **1.04x** | 2.00x | 72 → 75 |
| `displayLarge` | 48sp | **1.12x** | 2.00x | 48 → 54 |
| `headingLarge` | 32sp | 1.24x | 2.00x | 32 → 40 |
| `bodyXLarge` | 20sp | 1.70x | 2.00x | 20 → 34 |
| `bodyMedium` | 16sp | 1.75x | 2.00x | 16 → 28 |
| `bodySmall` | 14sp | 1.86x | 2.00x | 14 → 26 |
| `bodyXSmall` | 12sp | **2.00x** | 2.00x | 12 → 24 |

At `font_scale 1.0` every row is exactly 1.00x, so the design sizes are untouched and the whole
effect lives above the default.

The two platforms key the curve differently, and that difference is the whole reason iOS had a bug
and Android did not. Apple keys on a *named style*, so the curve is a parameter separate from the
size and you can ask for the wrong one — which we did, for every style. Android keys on the *sp
value itself*, so the size is the key and there is nothing to ask for. Passing `72.sp` already
determines the curve of 72.

Nothing to add here. A hand-written "display styles do not scale" rule would fight the platform, and
it would have to read the `fontScale` scalar rule 3 tells us not to read. The only way to break
scaling on Android is to leave the `sp` system — text in `dp`, or reading `fontScale` and doing the
arithmetic by hand. We do the second in exactly one place, the `InlineCalendar` threshold, which
rule 3 already covers.

**Assumed baseline: Android 14+.** `minSdk` is 23, and the non-linear converter arrived in 14. On an
older device Compose falls back to linear `sp * fontScale` — the `linear` column above, 2.00x for
everything including `display3XLarge`. So the problem the iOS mapping fixes does exist on pre-14
Android, and there is no API to fix it because the conversion tables are not on the device. We are
accepting that: it degrades to the old behaviour rather than breaking, and the fleet is moving off
those versions. Worth knowing rather than acting on.

### iOS asks for the wrong curve

Apple has the same idea, expressed as one curve per text style. We opt out of it. Every Lemonade
style resolves through `relativeTo: .body` — `LemonadeTypography.swift:51`, and
`LemonadeText.swift:322,374,376,382` — and `.body` is close to the steepest curve Apple ships.

| Apple text style | default | `AX5` | growth |
|---|---|---|---|
| `.caption` | 12pt | 43pt | 3.58 |
| `.body` | 17pt | 53pt | 3.12 |
| `.title` | 28pt | 58pt | 2.07 |
| `.largeTitle` | 34pt | 60pt | 1.76 |

So `display3XLarge` at 72pt renders somewhere near 224pt at `AX5`. On Apple's display curve it would
be around 127pt. Both are big. Only one of them was asked for.

The fix is a mapping, not an opt-out. Every tier picks the Apple style whose curve it wants:

| Lemonade style | size | `relativeTo:` |
|---|---|---|
| `displayXSmall` … `display3XLarge` | 24–72 | `.largeTitle` |
| `headingXLarge` | 40 | `.title` |
| `headingLarge`, `headingMedium` | 32, 28 | `.title2` |
| `headingSmall` | 24 | `.title3` |
| `headingXSmall`, `headingXXSmall` | 18, 16 | `.headline` |
| `bodyXLarge*`, `bodyLarge*`, `bodyMedium*` | 20, 18, 16 | `.body` |
| `bodySmall*` | 14 | `.footnote` |
| `bodyXSmall*` | 12 | `.caption` |

```swift
// LemonadeTextStyle
var relativeTextStyle: Font.TextStyle {
    switch fontSize {
    case 24...: return .largeTitle   // display tiers
    ...
    }
}

public var font: Font {
    .custom(fontName, size: fontSize, relativeTo: relativeTextStyle)
}
```

Switching on `fontSize` alone cannot separate `displayXSmall` from `headingSmall`, both 24, so the
mapping wants to hang off `LemonadeTypography` rather than off the raw size.

Two call sites drop the parameter entirely. `LemonadeText.swift:171` and `LemonadeBadge.swift:104`
build the font with `.custom(_:size:)` and no `relativeTo:`. That is not an opt-out: bare
`.custom(_:size:)` scales relative to `.body`, the same curve as every other call site. Only
`.custom(_:fixedSize:)` pins a font. So these two are not a separate problem, they are the same one
written more quietly, and they should go through the same mapping.

Measured rather than assumed, because the wording invites the opposite reading: the `Badge` label
grows 2.72× between `large` and `AX5` in the sample app, from a 25px glyph to a 68px one. The `Badge`
row in the table above stands as written.

### The mapping lands where Android already sits

The two ceilings differ — Android stops at 2.0x, iOS AX5 goes past 2.8x — so absolute growth does not
compare. The ratio between tiers does, and it is the number that says whether the platforms behave
alike.

| | `display3XLarge` | `bodyMedium` | display as % of body |
|---|---|---|---|
| Android at `font_scale 2.0` | 1.04x | 1.75x | **59%** |
| iOS at AX5, with the mapping | 1.71x | 2.81x | **61%** |
| iOS at AX5, before the mapping | 2.82x | 2.81x | 100% |

59% against 61%, arrived at independently: the iOS mapping was picked from Apple's own size tables
without looking at Android.

That reframes the change. It is not a taste call about how big a balance should be. Before it, the
same design token produced 12% growth on Android and 182% on iOS — the platforms disagreed with each
other. The mapping is convergence on behaviour Android already ships, and the 100% row is the
outlier that needed explaining.

### When the curve is not enough

Then cap the subtree. Do not pin the style, and do not pin the size.

```swift
.dynamicTypeSize(...DynamicTypeSize.xxxLarge)
```

Compose has no direct equivalent. The nearest thing is clamping the density for a subtree:

```kotlin
val density = LocalDensity.current
CompositionLocalProvider(
    LocalDensity provides Density(
        density = density.density,
        fontScale = min(density.fontScale, 1.3f),
    ),
) { /* content */ }
```

That one is a compromise, not a recipe. It reads the same `fontScale` scalar as rule 3, so it is
approximate under non-linear scaling, and it caps everything in the subtree rather than one style.
If we end up needing it often, that is a sign the mapping above is wrong somewhere.

Either way the ceiling is rule 6. A cap that leaves a number rendering smaller than the label under
it has taken away hierarchy as well as size, and a flat cap on a display style is a WCAG 1.4.4
failure with no story to tell. "Still at least body size, and still well past 200% of its own
default" has one.

### The failure is usually sideways

For a big number the thing that breaks is width. `R$ 1.234,56` runs out of card, it does not get its
descenders cut. Nothing in [Fix recipes](#fix-recipes) helps with that — `heightIn` and `minHeight`
are the wrong axis.

The options are to wrap to a second line, or to shrink to fit: `minimumScaleFactor(_:)` on iOS,
`TextAutoSize.StepBased(minFontSize, maxFontSize)` on Compose, which the Compose Multiplatform
version we are on has. Shrinking takes back the size the user asked for, so it needs a floor and it
is the last option rather than the first.

And the general shape of it: the container is what cannot absorb the growth, not the text. Widen the
container, wrap the line, then shrink the glyphs, in that order.

## What will move

On Android, components get taller as text grows. That is the point, but it shifts layouts. A
`Button` at `Medium` sits at exactly 48dp today and will pass that above the default scale, so
any consumer pinning a row height around one of our components needs a look. Going from
`requiredHeight` to `heightIn` also means the component starts respecting incoming constraints
instead of overriding them. Worth flagging per component, because a few lean on that override
to get out of a cramped parent.

On iOS the movement is less obvious, since `.frame(height:)` never clipped anything. The text
was already drawing outside its background. Moving to `minHeight` makes the background grow to
hold it, so things that looked wrong start looking right and take up more room. Expect shifts
in the sample app and in consumer screens at accessibility sizes, and close to nothing at
default Dynamic Type.

The `relativeTo:` mapping is the widest change of the lot, because it touches every string the
SwiftUI package draws. It cuts display and heading text down at accessibility sizes, which is the
point, but it also makes `bodySmall` and `bodyXSmall` grow *more* than they do today, since
`.footnote` and `.caption` are steeper curves than `.body`. It is not a one-way "make things
smaller" change, it is Apple's whole curve restored, and the small end of it lands in exactly the
dense components rule 2 is already about. Worth doing early for that reason, and worth screenshots
at `AX5` before and after rather than a code review alone.

No public API changes either way. The layout work is internal modifiers, and the mapping wants to be
an internal property on `LemonadeTextStyle` so the published surface stays as it is — only what
`font` returns changes. The `BottomTabBar` fix came out as `ADDITIONS_ONLY`, and the only baseline
movement was an internal Compose singleton re-hashing.

## Order of work

Most used first. Each one is its own PR, checked on device at the default scale, at 200% on
Android and at `AX5` on iOS.

1. The SwiftUI `relativeTo:` mapping. One file, and it changes the numbers every component below is
   then measured against, so doing it after the others means measuring twice.
2. `Button`. Confirmed broken on Android and used everywhere. KMP and SwiftUI in one go.
3. `SegmentedControl`. Confirmed broken on iOS, no headroom on Android.
4. `Badge`, `Tag`, `Chip`. Small text containers with the same problem, cheap to do together.
   `Badge` also needs its actual cause pinned down first.
5. `SearchField`, `TextField`, `PinCode`, `SelectField`. Input heights. These need more care,
   because the cursor and the platform text field bring their own metrics.
6. `InlineCalendar`. Swap the `fontScale > 1.3` branch for something measured.
7. The rest, from the audit below.

### Audit commands

Today these come back with 26 fixed dimensions in the published KMP modules, 2 `fontScale`
references (both the `InlineCalendar` branch), and 21 fixed frame heights in SwiftUI.

```bash
# KMP, fixed dimensions in published modules
grep -rn "\.height(height =\|\.requiredHeight(\|\.requiredSize(" \
  --include='*.kt' kmp/ui/src/commonMain kmp/expressive/src/commonMain kmp/calendar/src/commonMain \
  | grep -v "IntrinsicSize\|heightIn"

# KMP, fontScale branching
grep -rn "fontScale" --include='*.kt' kmp/*/src

# SwiftUI, fixed frame heights
grep -rn "\.frame(height:" swiftui/Sources/Lemonade/Components/ | grep -v "minHeight\|maxHeight"
```

Plenty of those hits are fine. `Icon`, `BrandLogo`, `CountryFlag`, `Checkbox`, `RadioButton`,
`SymbolContainer` and the `Tabs` indicator are fixed dimensions around things that are not
text, and they should stay fixed. The rule only covers dimensions that wrap text.

### Keeping it fixed

Hand checking on a device does not scale to a sweep this size. Part of agreeing this proposal
is deciding whether we add screenshot tests at default, 200% and `AX5`, so the contract holds
itself up instead of needing another afternoon of somebody poking at emulators.
[#199](https://github.com/saltpay/lemonade-design-system/pull/199) already adds Roborazzi
screenshot testing for `:ui` and may be the right place to hang this.

## Open questions

1. How far does the bar exception go? `BottomTabBar` drops labels once they stop fitting.
   Apple says do not lose functionality and pays for it with the Large Content Viewer, which
   Android does not have. Do we drop labels on both platforms so they match, or use the Large
   Content Viewer on iOS and only drop on Android?
2. Long labels at the default scale. The measured approach in #298 can go icon-only at
   `fontScale 1.0` if someone passes a long label on a narrow device, where before it
   ellipsised. That is deliberate, but it is still a behaviour change. Should it have a floor?
3. Screenshot tests. Part of this work, or its own track alongside #199?
4. Is the subtree cap allowed at all? Rule 6 says the platform curves are the answer for text that
   is already big, and I think they cover almost everything. The question is whether
   `.dynamicTypeSize(...)` and the Compose density clamp are a sanctioned last resort with rule 6 as
   the floor, or whether we ban them outright and treat every case that seems to need one as a
   layout problem instead. Banning them is cleaner to review and I would rather start there, but I
   have not tried it against the balance-on-a-card case that started this.

## References

- [Get started with Dynamic Type, WWDC24](https://developer.apple.com/videos/play/wwdc2024/10074/)
- [`DynamicTypeSize.isAccessibilitySize`, Apple Developer](https://developer.apple.com/documentation/swiftui/dynamictypesize/isaccessibilitysize)
- [Typography, Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/typography) — the Dynamic Type size tables
- [`Font.custom(_:size:relativeTo:)`, Apple Developer](https://developer.apple.com/documentation/swiftui/font/custom(_:size:relativeto:))
- [`dynamicTypeSize(_:)`, Apple Developer](https://developer.apple.com/documentation/swiftui/view/dynamictypesize(_:)-9brgv)
- [Android 14 features and APIs, non-linear font scaling](https://developer.android.com/about/versions/14/features)
- [Android 14 non-linear text scaling migration, Flutter docs](https://docs.flutter.dev/release/breaking-changes/android-14-nonlinear-text-scaling-migration)
- [Bottom navigation `labelVisibilityMode`, Material Components Android](https://github.com/material-components/material-components-android/blob/master/docs/components/BottomNavigation.md)
- [Navigation bar accessibility, Material Design 3](https://m3.material.io/components/navigation-bar/accessibility)
- [Supporting Dynamic Type, Create with Swift](https://www.createwithswift.com/supporting-dynamic-type-and-larger-text-in-your-app-to-enhance-accessibility/)
- WCAG 2.2 [1.4.4 Resize Text](https://www.w3.org/WAI/WCAG22/Understanding/resize-text.html)
