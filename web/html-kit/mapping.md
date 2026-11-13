# HTML kit to Compose and SwiftUI

What each `lmnd-kit-` block in a prototype becomes in Jetpack Compose and SwiftUI. Each row is one
block. Its `--modifier` classes are listed in the same row with the parameter value they become, and
so are its `__element` parts that a component takes as a parameter or slot: the label, the support
text, a leading slot. Parts that only lay a block out are listed once, under
[Layout-only parts](#layout-only-parts); the component draws its own layout.

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

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-device`, `lmnd-kit-source-notice` | Nothing | Nothing | |
| `lmnd-kit-overlay`, `lmnd-kit-scrim` | Nothing: the sheet or dialog draws its own scrim | Nothing | `--closing` is set by `kit.js` while an overlay closes. `--center` centres a dialog. |

## Screens and layout

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-screen` | The screen's root, on `LemonadeTheme.colors.background.bgSubtle` | The screen's root view, on `LemonadeTheme.colors.background.bgSubtle` | `--default`: `bgDefault` instead. `--wash-critical`, `--wash-featured`, `--wash-info`, `--wash-neutral`, `--wash-positive`, `--wash-warning`: a wash of `bgCritical`, `bgFeatured`, `bgInfo`, `bgNeutral`, `bgPositive` or `bgCaution` fading down from the top of the screen. `--details`, `--home`, `--restricted`: product screen layouts, **No Lemonade component**. `--current`, `--leaving`, `--push-in`, `--push-out`, `--pop-in`, `--pop-out` are set by `kit.js` for navigation; nothing to build. |
| `lmnd-kit-content` | A `Column` in a `verticalScroll`, `spacing400` at the sides | A `ScrollView` holding a `VStack`, `spacing400` at the sides | |
| `lmnd-kit-stack`, `lmnd-kit-row`, `lmnd-kit-grid` | `Column`, `Row`, a grid of `Row`s; the gaps as spacing tokens | `VStack`, `HStack`, `Grid`; the gaps as spacing tokens | |
| `lmnd-kit-section` | A heading `LemonadeUi.Text` over the section's content | The same | Parts: `__title`: the section's heading `LemonadeUi.Text`. |
| `lmnd-kit-actions` | The screen's buttons at its foot, outside the scroll: full-width `LemonadeUi.Button`s, primary above secondary | The same, in a `VStack` | |
| `lmnd-kit-divider` | `LemonadeUi.HorizontalDivider` | `LemonadeUi.HorizontalDivider` | `--dashed`: `variant = DividerVariant.Dashed` / `.dashed`. `--vertical`: `LemonadeUi.VerticalDivider`. `--inset`: `spacing400` of horizontal padding. |
| `lmnd-kit-divider-label` | `LemonadeUi.HorizontalDivider(label = …)` | `LemonadeUi.HorizontalDivider(label: …)` | `--dashed`: `variant = DividerVariant.Dashed` / `.dashed` |
| `lmnd-kit-chips` | `LemonadeUi.Chip`s in a horizontally scrolling `Row` | `LemonadeUi.Chip`s in a horizontal `ScrollView` | |

## Top bar and navigation

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-top-bar`, `lmnd-kit-large-title` | `LemonadeUi.TopBar` with `rememberTopBarState()`; the large title's description is `subtitle` | `.lemonadeTopBar(label:subheading:titleDisplayMode:navigationAction:)` | `--small`: a bar that starts collapsed and never expands (`titleDisplayMode: .inline`). `--scrolled` and `--collapsed` are set by `kit.js` as the screen scrolls; the component does this itself. Parts: `__title`, `__large-title`: `label`. `__subtitle`, `__large-subtitle`: `subtitle` / `subheading`. `__leading`: `navigationAction`. `__trailing`: `trailingSlot` on Compose; `ToolbarItem`s on SwiftUI. `__bottom`: `bottomSlot` / `.lemonadeTopBarBottomSlot`. `__compact`: a top-level bar with the large title and actions on one row and no navigation action. `__home`: a product's home bar, **No Lemonade component**. A large title's `__description`: `subtitle` / `subheading`. |
| `lmnd-kit-tabs` | `LemonadeUi.Tabs` | `LemonadeUi.Tabs` | `--stretch`: `itemsSize = TabsItemSize.Stretch` / `.stretch` Parts: `__item`: one `TabItem` in `tabs`, with `__label` its `label`. `__item--selected`: `selectedIndex`. `__pill`: the selection indicator; the component draws it. |
| `lmnd-kit-segmented` | `LemonadeUi.SegmentedControl` | `LemonadeUi.SegmentedControl` | `--small`, `--medium`: `size = LemonadeSegmentedControlSize.Small` / `.Medium`; without either, `Large` Parts: `__item`: one entry in `properties`. `__item--selected`: `selectedTab`. |
| `lmnd-kit-tab-bar` | `LemonadeUi.BottomTabBar` | **No Lemonade component**: the app's `TabView` | Parts: `__item`: one `BottomTabBarItem` in `items`. `__item--selected`: `selectedIndex`. |
| `lmnd-kit-dropdown`, `lmnd-kit-dropdown-layer` | `LemonadeUi.Dropdown` holding `LemonadeUi.DropdownItem`s | In a top bar, `LemonadeUi.ToolbarIconMenu`. Elsewhere, **No Lemonade component** | `--start`: the menu opens from the leading edge instead of the trailing one Parts: `__item`: a `LemonadeUi.DropdownItem`, with `__label` its `text`. `__item--disabled`: `enabled = false`. `__item--critical`: a critical item; `DropdownItem` has no voice, so it stays neutral. `__title`: a heading `LemonadeUi.Text` above the items. |
| `lmnd-kit-filter-bar` | `LemonadeUi.Chip`s in a horizontally scrolling `Row`, ending in a `LemonadeUi.Button` that clears them | The same, in a horizontal `ScrollView` | |
| `lmnd-kit-identifier-button` | **No Lemonade component** | **No Lemonade component** | `--circle`, `--icon`, `--text`: its shapes |
| `lmnd-kit-page-indicator` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-month-nav`, `lmnd-kit-day-strip`, `lmnd-kit-day-chip` | **No Lemonade component**: build from `LemonadeUi.IconButton`, `LemonadeUi.Text` and tokens | The same | `--selected`: the chosen day. `--glow-critical`, `--glow-info`, `--glow-positive`, `--glow-warning`: a glow in that voice's colour. |
| `lmnd-kit-drawer`, `lmnd-kit-drawer-layer` | **No Lemonade component** | **No Lemonade component** | |

## Actions

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-button` | `LemonadeUi.Button` (the `lmnd-button` web class) | `LemonadeUi.Button` | `--full-shape`: the pill shape. Nothing on Compose; `.fullShape()` on SwiftUI. |
| `lmnd-kit-icon-button` | `LemonadeUi.IconButton`; without modifiers, `Neutral`, `Subtle`, `Medium` | `LemonadeUi.IconButton`; in a top bar, `LemonadeUi.ToolbarIconButton` | `--primary`, `--secondary`, `--critical`, `--on-brand`, `--on-color`: `variant = LemonadeButtonVariant.Primary`, `Secondary`, `Critical`, `OnBrand`, `OnColor`. `--solid`, `--ghost`: `type = LemonadeButtonType.Solid`, `Ghost`. `--x-small`, `--small`, `--large`: `size = LemonadeButtonSize.XSmall`, `Small`, `Large`. `--circular`: `shape = LemonadeIconButtonShape.Circular` / `.circular`. |
| `lmnd-kit-link` | `LemonadeUi.Link` | `LemonadeUi.Link` | |
| `lmnd-kit-inline-action` | A `LemonadeUi.IconButton` in the row's trailing slot | The same | |
| `lmnd-kit-chip` | `LemonadeUi.Chip` | `LemonadeUi.Chip` | `--selected`: `selected = true`. `--disabled`: `enabled = false`. `--error`: `error = true`. Parts: `__label`: `label`. `__leading`: `leadingPainter` / `leadingIcon`. `__counter`: `counter`. |
| `lmnd-kit-tile` | `LemonadeUi.Tile` | `LemonadeUi.Tile` | `--horizontal`: `orientation = LemonadeTileOrientation.Horizontal` / `.horizontal`. `--outlined`: `variant = LemonadeTileVariant.Outlined` / `.outlined`. `--selected`: `isSelected = true`. Parts: `__label`: `label`. `__support`: `supportText`. `__top`: `topAccessory` on Compose; SwiftUI has none. |
| `lmnd-kit-box-selection` | `LemonadeUi.BoxSelection` | `LemonadeUi.BoxSelection` | `--outlined`: `variant = LemonadeBoxSelectionVariant.Outlined` / `.outlined`. `--elevated`: `background = LemonadeBoxSelectionBackground.Elevated` / `.elevated`. `--selected`: `isSelected = true`. |

## Text and display

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-primary`, `lmnd-kit-secondary`, `lmnd-kit-tertiary`, `lmnd-kit-brand`, `lmnd-kit-positive`, `lmnd-kit-critical`, `lmnd-kit-caution`, `lmnd-kit-info` | The `color` of a `LemonadeUi.Text`: `LemonadeTheme.colors.content.contentPrimary`, `contentSecondary`, `contentTertiary`, `contentBrand`, `contentPositive`, `contentCritical`, `contentCaution`, `contentInfo` | The same tokens | |
| `lmnd-kit-intro` | A `LemonadeUi.Text` in `contentSecondary` | The same | |
| `lmnd-kit-symbol` | `LemonadeUi.SymbolContainer`; without modifiers, `Neutral`, `Medium`, `Circle` | `LemonadeUi.SymbolContainer` | `--brand`, `--brand-subtle`, `--critical`, `--info`, `--positive`, `--warning`: `voice = SymbolContainerVoice.Brand`, `BrandSubtle`, `Critical`, `Info`, `Positive`, `Warning`. `--x-small`, `--small`, `--large`, `--x-large`, `--xx-large`: `size = SymbolContainerSize.XSmall` to `XXLarge`. `--rounded`: `shape = SymbolContainerShape.Rounded`. `--themed` with `data-hue`: `theme = ThemedStyle` of that hue; with `--subtle`, its subtle palette (`.blue.subtle`). Parts: `__status`: `badgeSlot`, holding the product's status artwork. `__frequency` with `__frequency--business-days`, `__frequency--everyday`, `__frequency--instant`, `__frequency--scheduled`: `badgeSlot`, holding a product's frequency disc; **No Lemonade component**. |
| `lmnd-kit-tag` | `LemonadeUi.Tag` | `LemonadeUi.Tag` | `--critical`, `--featured`, `--info`, `--positive`, `--warning`: `voice = TagVoice.Critical`, `Featured`, `Info`, `Positive`, `Warning`. `--on-color`: `TagVoice.NeutralOnColor`. Parts: `__label`: `label`. |
| `lmnd-kit-badge` | `LemonadeUi.Badge` | `LemonadeUi.Badge` | `--x-small`: `size = LemonadeBadgeSize.XSmall` / `.xSmall`. Parts: `__label`: `text`. |
| `lmnd-kit-notice` | `LemonadeUi.Notice`; without a modifier, `NoticeVoice.Info` | `LemonadeUi.Notice` | `--critical`, `--neutral`, `--positive`, `--warning`: `voice = NoticeVoice.Critical`, `Neutral`, `Positive`, `Warning` Parts: `__title`: `title`. `__text`: `content`. `__action`: `actionLabel` and `onActionClick`. |
| `lmnd-kit-skeleton` | `LemonadeUi.LineSkeleton`, `CircleSkeleton` or `BlockSkeleton` | The same | `--line`, `--circle`, `--block`: which of the three. `--symbol`: a `BlockSkeleton` the size of a medium symbol container. `--small`, `--large`, `--x-large`, `--xx-large`: `size = LemonadeSkeletonSize.Small` to `XXLarge`. |
| `lmnd-kit-flag` | `LemonadeUi.CountryFlag` | `LemonadeUi.CountryFlag` | `--small`, `--large`, `--x-large`, `--xx-large`, `--xxx-large`: `size = LemonadeAssetSize.Small` to `XXXLarge` / `LemonadeCountryFlagSize.small` to `.xxxLarge`. `--rounded`: `shape = CountryFlagShape.Rounded` / `.rounded`. |
| `lmnd-kit-logo` | `LemonadeUi.BrandLogo` | `LemonadeUi.BrandLogo` | `--small`, `--large`, `--x-large`, `--xx-large`, `--xxx-large`: `size = LemonadeAssetSize.Small` to `XXXLarge` / `LemonadeBrandLogoSize.small` to `.xxxLarge` |
| `lmnd-kit-load-more` | A `LemonadeUi.Spinner` centred under the list | The same | |
| `lmnd-kit-tooltip` | `LemonadeUi.Tooltip` | `.lemonadeTooltipContainer()` on the screen, `.lemonadeTooltipAnchor(_:)` on the target, shown through the tooltip manager | `--top`, `--bottom`: `indicatorPlacement`, the edge the indicator sits on Parts: `__content` and `__text`: `content`. `__close`: `onCloseClick` / `showCloseButton`. `__footer`: `footer`, with `__action` a `TooltipFooterActionVariant.Primary` action and `__action--secondary` a `Secondary` one. `__steps`: a step count such as "1 of 3", as `LemonadeUi.Text` in the footer. |
| `lmnd-kit-empty` | **No Lemonade component**: a `LemonadeUi.SymbolContainer`, a title and a line of `LemonadeUi.Text`, and an optional `LemonadeUi.Button`, centred | The same | |
| `lmnd-kit-progress` | **No Lemonade component**: a bar in `bgBrand` on `bgSubtle` | The same | |
| `lmnd-kit-table` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-hero`, `lmnd-kit-balance`, `lmnd-kit-delta`, `lmnd-kit-stats`, `lmnd-kit-avatars`, `lmnd-kit-role-strip` | **No Lemonade component** | **No Lemonade component** | `--down` on a delta: a fall, in `contentCritical` |
| `lmnd-kit-bars`, `lmnd-kit-sparkline`, `lmnd-kit-legend` | **No Lemonade component**: Lemonade has no charts | The same | |

## Cards and lists

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-card` | `LemonadeUi.Card`; the header elements are its `header`, the footer its `footerAction` | `LemonadeUi.Card` | `--subtle`, `--elevated`: `background = LemonadeCardBackground.Subtle`, `Elevated`. `--pad-x-small`, `--pad-small`, `--pad-medium`: `contentPadding = LemonadeCardPadding.XSmall`, `Small`, `Medium`. `--raised`: `Modifier.lemonadeShadow(LemonadeShadow.Large, shape)` / `.lemonadeShadow(.large)`. `--brand`: a card on `bgBrand`, **No Lemonade component**. Parts: `__header` is `header` (a `CardHeaderConfig`), with `__title` its `title`, `__subtitle` its `subtitle`, `__trailing` its `trailingSlot` and `__chevron` its `showNavigationIndicator`. `__body` is `content`. `__footer` is `footerAction`. |
| `lmnd-kit-gradient-card` | **No Lemonade component** | **No Lemonade component** | `--critical`, `--info`, `--neutral`, `--positive`, `--warning`: the voice of its gradient |
| `lmnd-kit-list-group` | A heading `LemonadeUi.Text` over a `LemonadeUi.Card` holding the group's rows | The same | Parts: `__header` holds `__heading` and `__total`, the `LemonadeUi.Text`s of the heading row. |
| `lmnd-kit-list-item` | `LemonadeUi.ActionListItem` when the row opens something, `LemonadeUi.ResourceListItem` when it shows a thing with a value, `LemonadeUi.ListItem` otherwise (`LDS-LIST-NO-HANDROLL`) | The same | `--divider`: `showDivider = true`. `--disabled`: `enabled = false`. `--critical`: `voice = LemonadeListItemVoice.Critical` / `.critical`. Parts: `__label`: `label`. `__overline`: `topLabel`. `__support`: `supportText`; `__support--one-line`: `supportTextMaxLines = 1`. `__leading`: `leadingSlot`. `__trailing`: `trailingSlot`. `__chevron`: `showNavigationIndicator = true`. `__value`: a `ResourceListItem`'s `value`. `__value--dimmed`, `__value--positive`, `__value--strikethrough`: no parameter; a `LemonadeUi.Text` in `contentTertiary`, `contentPositive` or struck through, in the trailing slot. |
| `lmnd-kit-content-item` | `LemonadeUi.ContentListItem` | `LemonadeUi.ContentListItem` | `--vertical`: `layout = LemonadeContentListItemLayout.Vertical` / `.vertical`. `--compact`: `density = LemonadeContentListItemDensity.Compact` / `.compact`. Parts: `__label`: `label`. `__value`: `value`. |
| `lmnd-kit-row-data` | `LemonadeUi.ContentListItem`s in a `Column`, without dividers | The same, in a `VStack` | Parts: `__row`: one `ContentListItem`. `__group`: a row with its sub-rows under it, a size smaller. |
| `lmnd-kit-select-item` | `LemonadeUi.SelectListItem` | `LemonadeUi.SelectListItem` | Parts: `__leading`: `leadingSlot`. `__support`: `supportText`. |
| `lmnd-kit-timeline` | `LemonadeUi.HistoryTimeline`; without a modifier, `HistoryTimelineVariant.Events` | `LemonadeUi.HistoryTimeline` | `--steps`: `variant = HistoryTimelineVariant.Steps` / `.steps` Parts: `__item`: one `HistoryTimelineItem` in `items`, with `__label` its `label`, `__subheading` its `subheading` and `__description` its `description`. `__item--current`, `__item--active`: `currentIndex`. `__item--completed`, `__item--next`: rows before and after `currentIndex`. `__item--positive`, `__item--critical`, `__item--error`: `voice = HistoryItemVoice.Positive`, `Critical`. `__indicator`: the dot or step mark; the component draws it. |
| `lmnd-kit-swipe-row` | `LemonadeUi.SwipeActionRow` | `LemonadeUi.SwipeActionRow` | `--two`: two trailing `SwipeAction`s instead of one Parts: `__content`: `content`. `__actions`: `trailingActions`, one `SwipeAction` each. |
| `lmnd-kit-content-card` | **No Lemonade component** | **No Lemonade component** | |
| `lmnd-kit-menu-footer` | **No Lemonade component**: centred `LemonadeUi.Text` in `contentTertiary` under the last card | The same | |
| `lmnd-kit-widget`, `lmnd-kit-widgets` | **No Lemonade component** | **No Lemonade component** | `--caution`, `--critical`, `--positive`: the widget's status colour. `--full`: the widget spans the whole row. |

## Inputs

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-field` | `LemonadeUi.TextField`; with a selector, `LemonadeUi.TextFieldWithSelector` | The same | `--textarea`: `LemonadeUi.TextArea`. `--error`: `error = true` with an `errorMessage`. `--disabled`: `enabled = false`. `--focused`: the focused state; the component draws it while it has focus. Parts: `__label`: `label`. `__optional`: `optionalIndicator`. `__input`: `input`. `__value`, `__value--placeholder`: a select field's `selectedValue` and `placeholderText`. `__helper`: `supportText`, or `errorMessage` with `--error`. `__counter`: `maxLength` on a `TextArea`, which draws the count; `__counter--over` is set by `kit.js` when the text runs over. `__selector`: a `TextFieldWithSelector`'s `leadingContent`. `__select`: a native picker behind the box; on the platforms, `LemonadeUi.SelectField` instead. |
| `lmnd-kit-select` | `LemonadeUi.SelectField`, opening a sheet or dropdown of options | `LemonadeUi.SelectField` | |
| `lmnd-kit-search` | `LemonadeUi.SearchField` | `LemonadeUi.SearchField` | Parts: `__input`: `input`. `__clear`: `onInputClear`. |
| `lmnd-kit-pin` | `LemonadeUi.PinCode` | `LemonadeUi.PinCode` | `--error`: `error = true`. `--disabled`: `submitting = true` while the code is checked. Parts: `__box`: one cell per digit of `length`. `__box--active`: the cell taking input; the component draws it. |
| `lmnd-kit-switch`, `lmnd-kit-checkbox`, `lmnd-kit-radio` | `LemonadeUi.Switch`, `LemonadeUi.Checkbox`, `LemonadeUi.RadioButton` | The same | |
| `lmnd-kit-control` | The label and support text beside a control: `label` and `supportText` of a `Checkbox` or `RadioButton`; a `Switch` goes in a list item's trailing slot | The same | Parts: `__text`: `label`. `__support`: `supportText`. |

## Overlays

| Kit block | Compose | SwiftUI | Modifiers and parts |
| --- | --- | --- | --- |
| `lmnd-kit-sheet` | `LemonadeUi.BottomSheet`, with a `LemonadeUi.TopBar` as its first child (`LDS-SHEET-CHROME`) | `.sheet` holding a `NavigationStack`, with `.lemonadeTopBar(…)` (`LDS-SHEET-CHROME`) | `--subtle`: `background = LemonadeBottomSheetVariant.Subtle`; on SwiftUI, the sheet's content on `bgSubtle` Parts: `__bar` is the sheet's top bar, with `__title` its `label` and `__trailing` its trailing action. `__handle`: `showDragHandle` on Compose; the system drag indicator on SwiftUI. `__intro`: a `LemonadeUi.Text` in `contentSecondary` at the top of the content. `__body` is the content. `__actions`: the buttons at the foot of the content. |
| `lmnd-kit-dialog` | `LemonadeUi.Dialog` | **No Lemonade component** | Parts: `__body` and `__actions` are the dialog's `content`, with the buttons last. |
| `lmnd-kit-toast`, `lmnd-kit-toast-host` | `LemonadeToastHost`, shown through `LemonadeToastState.show(…)` | `.lemonadeToastContainer()` on the screen, shown through the toast manager's `show(…)` | `--success`, `--error`: `voice = ToastVoice.Success`, `Error` / `LemonadeToastVoice.success`, `.error`. `--medium`, `--long`: `duration = ToastDuration.Medium`, `Long` / `LemonadeToastDuration.medium`, `.long`. Parts: `__label`: `label`. `__action`: `actionLabel` and `onAction`. |

## Layout-only parts

These parts arrange a block's other parts. The component lays itself out, so they become nothing.

- `lmnd-kit-card__heading`
- `lmnd-kit-content-item__column`
- `lmnd-kit-dropdown-layer__dismiss`
- `lmnd-kit-field__box`
- `lmnd-kit-field__footer`
- `lmnd-kit-field__label-row`
- `lmnd-kit-list-item__content`
- `lmnd-kit-list-item__row`
- `lmnd-kit-search__box`
- `lmnd-kit-select-item__content`
- `lmnd-kit-tab-bar__bar`
- `lmnd-kit-tile__text`
- `lmnd-kit-timeline__body`
- `lmnd-kit-timeline__head`
- `lmnd-kit-tooltip__body`
- `lmnd-kit-top-bar__bar`
- `lmnd-kit-top-bar__center`
- `lmnd-kit-top-bar__large`
