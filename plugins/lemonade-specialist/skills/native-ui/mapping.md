<!-- DO NOT MODIFY THIS FILE MANUALLY. Copied from web/html-kit/mapping.md by scripts/sync-lemonade-specialist.sh. -->

# HTML kit to Compose and SwiftUI

What each `lmnd-kit-` block in a prototype becomes in Jetpack Compose and SwiftUI. Each row is one
block. Its `--modifier` classes are listed in the same row with the parameter value they become. Its
`__element` classes are the component's own parts: the label, the support text, a slot. Pass them as
that component's parameters or slots.

A row marked **No Lemonade component** is a block the kit draws for a product. Build it from the
Lemonade parts the row names, or use the product's own component.

Names are where to look, not a full signature. Check a component's parameters in its source in
`kmp/` or `swiftui/` before calling it. Compose components are `LemonadeUi.<Name>` from
`com.teya.lemonade`; SwiftUI components are `LemonadeUi.<Name>` from the `Lemonade` module.

## Web classes

The kit's markup also uses classes from `@teya/lemonade-mobile-ds`. They map one to one:

| Web class | Compose and SwiftUI |
| --- | --- |
| `lmnd-button` with `--primary`, `--secondary`, `--neutral`, `--critical`, `--on-brand`, `--on-color`, `--solid`, `--subtle`, `--ghost` and a size | `LemonadeUi.Button`: `variant`, `type` and `size` of the same names |
| `lmnd-icon` with `data-icon` | `LemonadeUi.Icon`: the `data-icon` name in camel case (`LemonadeIcons.ChevronRight`, `.chevronRight`) |
| `lmnd-text-<style>` | `LemonadeUi.Text` with the typography token of the same name (`textStyle = LemonadeTheme.typography.bodyMediumRegular`, `textStyle: LemonadeTypography.shared.bodyMediumRegular`) |
| `lmnd-spinner` | `LemonadeUi.Spinner` |

## Prototype chrome

Nothing to build: these exist so a prototype runs in a browser.

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-device`, `lmnd-kit-source-notice` | Nothing | Nothing | |
| `lmnd-kit-overlay`, `lmnd-kit-scrim` | Nothing: the sheet or dialog draws its own scrim | Nothing | `--closing` is set by `kit.js` while an overlay closes. `--center` centres a dialog. |

## Screens and layout

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-screen` | The screen's root, on `LemonadeTheme.colors.background.bgSubtle` | The screen's root view, on `LemonadeTheme.colors.background.bgSubtle` | `--default`: `bgDefault` instead. `--wash-critical`, `--wash-featured`, `--wash-info`, `--wash-neutral`, `--wash-positive`, `--wash-warning`: a wash of `bgCritical`, `bgFeatured`, `bgInfo`, `bgNeutral`, `bgPositive` or `bgCaution` fading down from the top of the screen. `--details`, `--home`, `--restricted`: product screen layouts, **No Lemonade component**. `--current`, `--leaving`, `--push-in`, `--push-out`, `--pop-in`, `--pop-out` are set by `kit.js` for navigation; nothing to build. |
| `lmnd-kit-content` | A `Column` in a `verticalScroll`, `spacing400` at the sides | A `ScrollView` holding a `VStack`, `spacing400` at the sides | |
| `lmnd-kit-stack`, `lmnd-kit-row`, `lmnd-kit-grid` | `Column`, `Row`, a grid of `Row`s; the gaps as spacing tokens | `VStack`, `HStack`, `Grid`; the gaps as spacing tokens | |
| `lmnd-kit-section` | A heading `LemonadeUi.Text` over the section's content | The same | |
| `lmnd-kit-actions` | The screen's buttons at its foot, outside the scroll: full-width `LemonadeUi.Button`s, primary above secondary | The same, in a `VStack` | |
| `lmnd-kit-divider` | `LemonadeUi.HorizontalDivider` | `LemonadeUi.HorizontalDivider` | `--dashed`: `variant = DividerVariant.Dashed` / `.dashed`. `--vertical`: `LemonadeUi.VerticalDivider`. `--inset`: `spacing400` of horizontal padding. |
| `lmnd-kit-divider-label` | `LemonadeUi.HorizontalDivider(label = …)` | `LemonadeUi.HorizontalDivider(label: …)` | `--dashed`: `variant = DividerVariant.Dashed` / `.dashed` |
| `lmnd-kit-chips` | `LemonadeUi.Chip`s in a horizontally scrolling `Row` | `LemonadeUi.Chip`s in a horizontal `ScrollView` | |

## Top bar and navigation

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-top-bar`, `lmnd-kit-large-title` | `LemonadeUi.TopBar` with `rememberTopBarState()`; the large title's description is `subtitle` | `.lemonadeTopBar(label:subheading:titleDisplayMode:navigationAction:)` | `--small`: a bar that starts collapsed and never expands (`titleDisplayMode: .inline`). `--scrolled` and `--collapsed` are set by `kit.js` as the screen scrolls; the component does this itself. |
| `lmnd-kit-tabs` | `LemonadeUi.Tabs` | `LemonadeUi.Tabs` | `--stretch`: `itemsSize = TabsItemSize.Stretch` / `.stretch` |
| `lmnd-kit-segmented` | `LemonadeUi.SegmentedControl` | `LemonadeUi.SegmentedControl` | `--small`, `--medium`: `size = LemonadeSegmentedControlSize.Small` / `.Medium`; without either, `Large` |
| `lmnd-kit-tab-bar` | `LemonadeUi.BottomTabBar` | **No Lemonade component**: the app's `TabView` | |
| `lmnd-kit-dropdown`, `lmnd-kit-dropdown-layer` | `LemonadeUi.Dropdown` holding `LemonadeUi.DropdownItem`s | In a top bar, `LemonadeUi.ToolbarIconMenu`. Elsewhere, **No Lemonade component** | `--start`: the menu opens from the leading edge instead of the trailing one |
| `lmnd-kit-filter-bar` | `LemonadeUi.Chip`s in a horizontally scrolling `Row`, ending in a `LemonadeUi.Button` that clears them | The same, in a horizontal `ScrollView` | |
| `lmnd-kit-identifier-button` | **No Lemonade component** | **No Lemonade component** | `--circle`, `--icon`, `--text`: its shapes |
| `lmnd-kit-page-indicator` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-month-nav`, `lmnd-kit-day-strip`, `lmnd-kit-day-chip` | **No Lemonade component**: build from `LemonadeUi.IconButton`, `LemonadeUi.Text` and tokens | The same | `--selected`: the chosen day. `--glow-critical`, `--glow-info`, `--glow-positive`, `--glow-warning`: a glow in that voice's colour. |
| `lmnd-kit-drawer`, `lmnd-kit-drawer-layer` | **No Lemonade component** | **No Lemonade component** | |

## Actions

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-button` | `LemonadeUi.Button` (the `lmnd-button` web class) | `LemonadeUi.Button` | `--full-shape`: the pill shape. Nothing on Compose; `.fullShape()` on SwiftUI. |
| `lmnd-kit-icon-button` | `LemonadeUi.IconButton`; without modifiers, `Neutral`, `Subtle`, `Medium` | `LemonadeUi.IconButton`; in a top bar, `LemonadeUi.ToolbarIconButton` | `--primary`, `--secondary`, `--critical`, `--on-brand`, `--on-color`: `variant = LemonadeButtonVariant.Primary`, `Secondary`, `Critical`, `OnBrand`, `OnColor`. `--solid`, `--ghost`: `type = LemonadeButtonType.Solid`, `Ghost`. `--x-small`, `--small`, `--large`: `size = LemonadeButtonSize.XSmall`, `Small`, `Large`. `--circular`: `shape = LemonadeIconButtonShape.Circular` / `.circular`. |
| `lmnd-kit-link` | `LemonadeUi.Link` | `LemonadeUi.Link` | |
| `lmnd-kit-inline-action` | A `LemonadeUi.IconButton` in the row's trailing slot | The same | |
| `lmnd-kit-chip` | `LemonadeUi.Chip` | `LemonadeUi.Chip` | `--selected`: `selected = true`. `--disabled`: `enabled = false`. `--error`: `error = true`. |
| `lmnd-kit-tile` | `LemonadeUi.Tile` | `LemonadeUi.Tile` | `--horizontal`: `orientation = LemonadeTileOrientation.Horizontal` / `.horizontal`. `--outlined`: `variant = LemonadeTileVariant.Outlined` / `.outlined`. `--selected`: `isSelected = true`. |
| `lmnd-kit-box-selection` | `LemonadeUi.BoxSelection` | `LemonadeUi.BoxSelection` | `--outlined`: `variant = LemonadeBoxSelectionVariant.Outlined` / `.outlined`. `--elevated`: `background = LemonadeBoxSelectionBackground.Elevated` / `.elevated`. `--selected`: `isSelected = true`. |

## Text and display

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-primary`, `lmnd-kit-secondary`, `lmnd-kit-tertiary`, `lmnd-kit-brand`, `lmnd-kit-positive`, `lmnd-kit-critical`, `lmnd-kit-caution`, `lmnd-kit-info` | The `color` of a `LemonadeUi.Text`: `LemonadeTheme.colors.content.contentPrimary`, `contentSecondary`, `contentTertiary`, `contentBrand`, `contentPositive`, `contentCritical`, `contentCaution`, `contentInfo` | The same tokens | |
| `lmnd-kit-intro` | A `LemonadeUi.Text` in `contentSecondary` | The same | |
| `lmnd-kit-symbol` | `LemonadeUi.SymbolContainer`; without modifiers, `Neutral`, `Medium`, `Circle` | `LemonadeUi.SymbolContainer` | `--brand`, `--brand-subtle`, `--critical`, `--info`, `--positive`, `--warning`: `voice = SymbolContainerVoice.Brand`, `BrandSubtle`, `Critical`, `Info`, `Positive`, `Warning`. `--x-small`, `--small`, `--large`, `--x-large`, `--xx-large`: `size = SymbolContainerSize.XSmall` to `XXLarge`. `--rounded`: `shape = SymbolContainerShape.Rounded`. `--themed` with `data-hue`: `theme = ThemedStyle` of that hue; with `--subtle`, its subtle palette (`.blue.subtle`). |
| `lmnd-kit-tag` | `LemonadeUi.Tag` | `LemonadeUi.Tag` | `--critical`, `--featured`, `--info`, `--positive`, `--warning`: `voice = TagVoice.Critical`, `Featured`, `Info`, `Positive`, `Warning`. `--on-color`: `TagVoice.NeutralOnColor`. |
| `lmnd-kit-badge` | `LemonadeUi.Badge` | `LemonadeUi.Badge` | `--x-small`: `size = LemonadeBadgeSize.XSmall` / `.xSmall` |
| `lmnd-kit-notice` | `LemonadeUi.Notice`; without a modifier, `NoticeVoice.Info` | `LemonadeUi.Notice` | `--critical`, `--neutral`, `--positive`, `--warning`: `voice = NoticeVoice.Critical`, `Neutral`, `Positive`, `Warning` |
| `lmnd-kit-skeleton` | `LemonadeUi.LineSkeleton`, `CircleSkeleton` or `BlockSkeleton` | The same | `--line`, `--circle`, `--block`: which of the three. `--symbol`: a `BlockSkeleton` the size of a medium symbol container. `--small`, `--large`, `--x-large`, `--xx-large`: `size = LemonadeSkeletonSize.Small` to `XXLarge`. |
| `lmnd-kit-flag` | `LemonadeUi.CountryFlag` | `LemonadeUi.CountryFlag` | `--small`, `--large`, `--x-large`, `--xx-large`, `--xxx-large`: `size = LemonadeAssetSize.Small` to `XXXLarge` / `LemonadeCountryFlagSize.small` to `.xxxLarge`. `--rounded`: `shape = CountryFlagShape.Rounded` / `.rounded`. |
| `lmnd-kit-logo` | `LemonadeUi.BrandLogo` | `LemonadeUi.BrandLogo` | `--small`, `--large`, `--x-large`, `--xx-large`, `--xxx-large`: `size = LemonadeAssetSize.Small` to `XXXLarge` / `LemonadeBrandLogoSize.small` to `.xxxLarge` |
| `lmnd-kit-load-more` | A `LemonadeUi.Spinner` centred under the list | The same | |
| `lmnd-kit-tooltip` | `LemonadeUi.Tooltip` | `.lemonadeTooltipContainer()` on the screen, `.lemonadeTooltipAnchor(_:)` on the target, shown through the tooltip manager | `--top`, `--bottom`: `indicatorPlacement`, the edge the indicator sits on |
| `lmnd-kit-empty` | **No Lemonade component**: a `LemonadeUi.SymbolContainer`, a title and a line of `LemonadeUi.Text`, and an optional `LemonadeUi.Button`, centred | The same | |
| `lmnd-kit-progress` | **No Lemonade component**: a bar in `bgBrand` on `bgSubtle` | The same | |
| `lmnd-kit-table` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-hero`, `lmnd-kit-balance`, `lmnd-kit-delta`, `lmnd-kit-stats`, `lmnd-kit-avatars`, `lmnd-kit-role-strip` | **No Lemonade component** | **No Lemonade component** | `--down` on a delta: a fall, in `contentCritical` |
| `lmnd-kit-bars`, `lmnd-kit-sparkline`, `lmnd-kit-legend` | **No Lemonade component**: Lemonade has no charts | The same | |

## Cards and lists

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-card` | `LemonadeUi.Card`; the header elements are its `header`, the footer its `footerAction` | `LemonadeUi.Card` | `--subtle`, `--elevated`: `background = LemonadeCardBackground.Subtle`, `Elevated`. `--pad-x-small`, `--pad-small`, `--pad-medium`: `contentPadding = LemonadeCardPadding.XSmall`, `Small`, `Medium`. `--raised`: `Modifier.lemonadeShadow(LemonadeShadow.Large, shape)` / `.lemonadeShadow(.large)`. `--brand`: a card on `bgBrand`, **No Lemonade component**. |
| `lmnd-kit-gradient-card` | **No Lemonade component** | **No Lemonade component** | `--critical`, `--info`, `--neutral`, `--positive`, `--warning`: the voice of its gradient |
| `lmnd-kit-list-group` | A heading `LemonadeUi.Text` over a `LemonadeUi.Card` holding the group's rows | The same | |
| `lmnd-kit-list-item` | `LemonadeUi.ActionListItem` when the row opens something, `LemonadeUi.ResourceListItem` when it shows a thing with a value, `LemonadeUi.ListItem` otherwise (`LDS-LIST-NO-HANDROLL`) | The same | `--divider`: `showDivider = true`. `--disabled`: `enabled = false`. `--critical`: `voice = LemonadeListItemVoice.Critical` / `.critical`. |
| `lmnd-kit-content-item` | `LemonadeUi.ContentListItem` | `LemonadeUi.ContentListItem` | `--vertical`: `layout = LemonadeContentListItemLayout.Vertical` / `.vertical`. `--compact`: `density = LemonadeContentListItemDensity.Compact` / `.compact`. |
| `lmnd-kit-row-data` | `LemonadeUi.ContentListItem`s in a `Column`, without dividers | The same, in a `VStack` | |
| `lmnd-kit-select-item` | `LemonadeUi.SelectListItem` | `LemonadeUi.SelectListItem` | |
| `lmnd-kit-timeline` | `LemonadeUi.HistoryTimeline`; without a modifier, `HistoryTimelineVariant.Events` | `LemonadeUi.HistoryTimeline` | `--steps`: `variant = HistoryTimelineVariant.Steps` / `.steps` |
| `lmnd-kit-swipe-row` | `LemonadeUi.SwipeActionRow` | `LemonadeUi.SwipeActionRow` | `--two`: two trailing `SwipeAction`s instead of one |
| `lmnd-kit-content-card` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-menu-footer` | **No Lemonade component**: centred `LemonadeUi.Text` in `contentTertiary` under the last card | The same | |
| `lmnd-kit-widget`, `lmnd-kit-widgets` | **No Lemonade component** | **No Lemonade component** | `--caution`, `--critical`, `--positive`: the widget's status colour. `--full`: the widget spans the whole row. |

## Inputs

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-field` | `LemonadeUi.TextField`; with a selector, `LemonadeUi.TextFieldWithSelector` | The same | `--textarea`: `LemonadeUi.TextArea`. `--error`: `error = true` with an `errorMessage`. `--disabled`: `enabled = false`. `--focused`: the focused state; the component draws it while it has focus. |
| `lmnd-kit-select` | `LemonadeUi.SelectField`, opening a sheet or dropdown of options | `LemonadeUi.SelectField` | |
| `lmnd-kit-search` | `LemonadeUi.SearchField` | `LemonadeUi.SearchField` | |
| `lmnd-kit-pin` | `LemonadeUi.PinCode` | `LemonadeUi.PinCode` | `--error`: `error = true`. `--disabled`: `submitting = true` while the code is checked. |
| `lmnd-kit-switch`, `lmnd-kit-checkbox`, `lmnd-kit-radio` | `LemonadeUi.Switch`, `LemonadeUi.Checkbox`, `LemonadeUi.RadioButton` | The same | |
| `lmnd-kit-control` | The label and support text beside a control: `label` and `supportText` of a `Checkbox` or `RadioButton`; a `Switch` goes in a list item's trailing slot | The same | |

## Overlays

| Kit block | Compose | SwiftUI | Modifiers |
| --- | --- | --- | --- |
| `lmnd-kit-sheet` | `LemonadeUi.BottomSheet`, with a `LemonadeUi.TopBar` as its first child (`LDS-SHEET-CHROME`) | `.sheet` holding a `NavigationStack`, with `.lemonadeTopBar(…)` (`LDS-SHEET-CHROME`) | `--subtle`: `background = LemonadeBottomSheetVariant.Subtle`; on SwiftUI, the sheet's content on `bgSubtle` |
| `lmnd-kit-dialog` | `LemonadeUi.Dialog` | **No Lemonade component** | |
| `lmnd-kit-toast`, `lmnd-kit-toast-host` | `LemonadeToastHost`, shown through `LemonadeToastState.show(…)` | `.lemonadeToastContainer()` on the screen, shown through the toast manager's `show(…)` | `--success`, `--error`: `voice = ToastVoice.Success`, `Error` / `LemonadeToastVoice.success`, `.error`. `--medium`, `--long`: `duration = ToastDuration.Medium`, `Long` / `LemonadeToastDuration.medium`, `.long`. |
