package com.teya.lemonade.app

import androidx.compose.runtime.Composable
import com.teya.lemonade.ActionListItemDisplay
import com.teya.lemonade.BadgeDisplay
import com.teya.lemonade.BorderWidthDisplay
import com.teya.lemonade.BottomTabBarDisplay
import com.teya.lemonade.BoxSelectionDisplay
import com.teya.lemonade.BrandLogoDisplay
import com.teya.lemonade.ButtonDisplay
import com.teya.lemonade.CardDisplay
import com.teya.lemonade.CheckboxDisplay
import com.teya.lemonade.ChipDisplay
import com.teya.lemonade.ColorsDisplay
import com.teya.lemonade.ContentListItemDisplay
import com.teya.lemonade.CountryFlagDisplay
import com.teya.lemonade.DatePickerDisplay
import com.teya.lemonade.Displays
import com.teya.lemonade.DividerDisplay
import com.teya.lemonade.HistoryTimelineDisplay
import com.teya.lemonade.HomeDisplay
import com.teya.lemonade.IconButtonDisplay
import com.teya.lemonade.IconsDisplay
import com.teya.lemonade.InlineCalendarDisplay
import com.teya.lemonade.LinkDisplay
import com.teya.lemonade.MarkdownDisplay
import com.teya.lemonade.NoticeDisplay
import com.teya.lemonade.OpacityDisplay
import com.teya.lemonade.PinCodeDisplay
import com.teya.lemonade.RadioButtonDisplay
import com.teya.lemonade.RadiusDisplay
import com.teya.lemonade.ResourceListItemDisplay
import com.teya.lemonade.SearchFieldDisplay
import com.teya.lemonade.SegmentedControlDisplay
import com.teya.lemonade.SelectFieldDisplay
import com.teya.lemonade.SelectListItemDisplay
import com.teya.lemonade.ShadowDisplay
import com.teya.lemonade.SizesDisplay
import com.teya.lemonade.SkeletonDisplay
import com.teya.lemonade.SpacingDisplay
import com.teya.lemonade.SpinnerDisplay
import com.teya.lemonade.SwitchDisplay
import com.teya.lemonade.SymbolContainerDisplay
import com.teya.lemonade.TabsDisplay
import com.teya.lemonade.TagDisplay
import com.teya.lemonade.TextDisplay
import com.teya.lemonade.TextFieldDisplay
import com.teya.lemonade.TileDisplay
import com.teya.lemonade.TimePickerDisplay
import com.teya.lemonade.ToastDisplay
import com.teya.lemonade.TooltipDisplay

@Composable
internal expect fun App()

internal expect val platformScreens: Map<Displays, @Composable (onNavigate: (Displays) -> Unit) -> Unit>

internal val screens: Map<Displays, @Composable (onNavigate: (Displays) -> Unit) -> Unit> = platformScreens + mapOf(
    Displays.Home to { onNavigate -> HomeDisplay(onNavigate = onNavigate) },
    Displays.Colors to { _ -> ColorsDisplay() },
    Displays.Icons to { _ -> IconsDisplay() },
    Displays.CountryFlag to { _ -> CountryFlagDisplay() },
    Displays.BrandLogo to { _ -> BrandLogoDisplay() },
    Displays.Badge to { _ -> BadgeDisplay() },
    Displays.Switch to { _ -> SwitchDisplay() },
    Displays.Checkbox to { _ -> CheckboxDisplay() },
    Displays.RadioButton to { _ -> RadioButtonDisplay() },
    Displays.SelectListItem to { _ -> SelectListItemDisplay() },
    Displays.ActionListItem to { _ -> ActionListItemDisplay() },
    Displays.ResourceListItem to { _ -> ResourceListItemDisplay() },
    Displays.ContentListItem to { _ -> ContentListItemDisplay() },
    Displays.Chip to { _ -> ChipDisplay() },
    Displays.SegmentedControl to { _ -> SegmentedControlDisplay() },
    Displays.BoxSelection to { _ -> BoxSelectionDisplay() },
    Displays.PinCode to { _ -> PinCodeDisplay() },
    Displays.Text to { _ -> TextDisplay() },
    Displays.SymbolContainer to { _ -> SymbolContainerDisplay() },
    Displays.Tag to { _ -> TagDisplay() },
    Displays.TextField to { _ -> TextFieldDisplay() },
    Displays.SearchField to { _ -> SearchFieldDisplay() },
    Displays.SelectField to { _ -> SelectFieldDisplay() },
    Displays.Card to { _ -> CardDisplay() },
    Displays.Button to { _ -> ButtonDisplay() },
    Displays.IconButton to { _ -> IconButtonDisplay() },
    Displays.Link to { _ -> LinkDisplay() },
    Displays.Shadows to { _ -> ShadowDisplay() },
    Displays.Skeleton to { _ -> SkeletonDisplay() },
    Displays.Tile to { _ -> TileDisplay() },
    Displays.Spacing to { _ -> SpacingDisplay() },
    Displays.Radius to { _ -> RadiusDisplay() },
    Displays.Sizes to { _ -> SizesDisplay() },
    Displays.Opacity to { _ -> OpacityDisplay() },
    Displays.BorderWidth to { _ -> BorderWidthDisplay() },
    Displays.Spinner to { _ -> SpinnerDisplay() },
    Displays.Divider to { _ -> DividerDisplay() },
    Displays.Tabs to { _ -> TabsDisplay() },
    Displays.BottomTabBar to { _ -> BottomTabBarDisplay() },
    Displays.DatePicker to { _ -> DatePickerDisplay() },
    Displays.InlineCalendar to { _ -> InlineCalendarDisplay() },
    Displays.TimePicker to { _ -> TimePickerDisplay() },
    Displays.Notice to { _ -> NoticeDisplay() },
    Displays.Toast to { _ -> ToastDisplay() },
    Displays.Tooltip to { _ -> TooltipDisplay() },
    Displays.HistoryTimeline to { _ -> HistoryTimelineDisplay() },
    Displays.Markdown to { _ -> MarkdownDisplay() },
)
