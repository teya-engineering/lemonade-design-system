package com.teya.lemonade

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

/**
 * A dropdown menu overlay following the Lemonade Design System.
 *
 * Anchors a popup menu to its parent layout, styled with Lemonade design tokens for shape, color,
 * and spacing. [expanded] controls visibility, the same pattern used by [LemonadeUi.Dialog] and
 * [LemonadeUi.BottomSheet].
 *
 * ## Usage
 *
 * ```kotlin
 * var expanded by remember { mutableStateOf(false) }
 *
 * Box {
 *     LemonadeUi.Button(
 *         label = "Open Menu",
 *         onClick = { expanded = true },
 *     )
 *
 *     LemonadeUi.Dropdown(
 *         expanded = expanded,
 *         onDismissRequest = { expanded = false },
 *     ) {
 *         LemonadeUi.DropdownItem(
 *             text = "Option 1",
 *             onClick = { expanded = false },
 *         )
 *     }
 * }
 * ```
 *
 * ## Design Notes
 *
 * - The dropdown enforces a minimum width of 248.dp.
 * - Uses [LemonadeTheme.shapes.radius500] for rounded corners.
 * - Background color is [LemonadeTheme.colors.background.bgDefault].
 * - Vertical offset is [LemonadeTheme.spaces.spacing200] from the anchor.
 * - Items are inset [LemonadeTheme.spaces.spacing100] from the panel and stack flush against one
 *   another.
 * - The dropdown keeps whichever system bars the host window hides, never shows one the host hides.
 *
 * @param expanded whether the dropdown menu is currently visible
 * @param onDismissRequest called when the user requests dismissal, by tapping outside or pressing
 *   back, depending on [dismissOnBackPress] and [dismissOnClickOutside]
 * @param dismissOnBackPress whether pressing the back button dismisses the dropdown, `true` by
 *   default
 * @param dismissOnClickOutside whether tapping outside the dropdown dismisses it, `true` by default
 * @param content menu items, composed in a [ColumnScope]. Typically a series of
 *   [LemonadeUi.DropdownItem] calls
 *
 * @see LemonadeUi.DropdownItem for individual menu items within this dropdown
 * @see LemonadeUi.Dialog for a dialog overlay with the same visibility pattern
 * @see LemonadeUi.BottomSheet for a bottom sheet overlay with the same visibility pattern
 * @see DropdownMenu the underlying component
 */
@Composable
public fun LemonadeUi.Dropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val dropdownMinWidth = 248.dp
    val mirrorSystemBars = systemBarsMirror()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        // The inset belongs on the menu: padding each item opens a gap between rows.
        modifier = Modifier
            .defaultMinSize(minWidth = dropdownMinWidth)
            .padding(horizontal = LemonadeTheme.spaces.spacing100),
        offset = DpOffset(
            y = LemonadeTheme.spaces.spacing200,
            x = LemonadeTheme.spaces.spacing0,
        ),
        containerColor = LemonadeTheme.colors.background.bgDefault,
        shape = LemonadeTheme.shapes.radius500,
        properties = PopupProperties(
            focusable = true,
            clippingEnabled = true,
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
        content = {
            mirrorSystemBars()
            content()
        },
    )
}

/**
 * A single item within a [LemonadeUi.Dropdown] menu, following the Lemonade Design System.
 *
 * Wraps [DropdownMenuItem] with Lemonade styling and restricts the icon parameters to
 * [LemonadeIcons] values. [LemonadeUi.Icon] renders them at [LemonadeAssetSize.Medium] for leading
 * icons and [LemonadeAssetSize.Small] for trailing icons. For arbitrary composable content in the
 * trailing position, use the [LemonadeUi.DropdownItem] overload that takes a `trailingSlot`.
 *
 * ## Usage
 *
 * ```kotlin
 * LemonadeUi.DropdownItem(
 *     text = "Settings",
 *     onClick = { /* handle click */ },
 *     leadingIcon = LemonadeIcons.Gear,
 * )
 *
 * LemonadeUi.DropdownItem(
 *     text = "Delete",
 *     onClick = { /* handle click */ },
 *     trailingIcon = LemonadeIcons.Trash,
 *     enabled = false,
 * )
 * ```
 *
 * @param text label text shown for this menu item
 * @param onClick called when the user clicks this menu item
 * @param leadingIcon optional [LemonadeIcons] shown before the text at [LemonadeAssetSize.Medium],
 *   `null` by default
 * @param trailingIcon optional [LemonadeIcons] shown after the text at [LemonadeAssetSize.Small],
 *   `null` by default
 * @param enabled whether this menu item is interactive, `true` by default. When `false` the item
 *   dims and stops responding to clicks
 *
 * @see LemonadeUi.Dropdown the parent dropdown menu component
 * @see LemonadeIcons for available icon options
 */
@Composable
public fun LemonadeUi.DropdownItem(
    text: String,
    onClick: () -> Unit,
    leadingIcon: LemonadeIcons? = null,
    trailingIcon: LemonadeIcons? = null,
    enabled: Boolean = true,
) {
    LemonadeUi.DropdownItem(
        text = text,
        onClick = onClick,
        leadingIcon = leadingIcon,
        trailingSlot = if (trailingIcon != null) {
            {
                LemonadeUi.Icon(
                    icon = trailingIcon,
                    contentDescription = null,
                    size = LemonadeAssetSize.Small,
                    tint = LemonadeTheme.colors.content.contentSecondary,
                )
            }
        } else {
            null
        },
        enabled = enabled,
    )
}

/**
 * A single item within a [LemonadeUi.Dropdown] menu with slot-based trailing content.
 *
 * Wraps [DropdownMenuItem] with Lemonade styling. The leading position takes [LemonadeIcons] values
 * only, which [LemonadeUi.Icon] renders at [LemonadeAssetSize.Medium]; the trailing position accepts
 * an arbitrary composable slot. Slot content renders exactly as given, so the caller sizes it for a
 * menu item.
 *
 * ## Usage
 *
 * ```kotlin
 * LemonadeUi.DropdownItem(
 *     text = "Notifications",
 *     onClick = { /* handle click */ },
 *     leadingIcon = LemonadeIcons.Bell,
 *     trailingSlot = {
 *         LemonadeUi.Badge(text = "12")
 *     },
 * )
 * ```
 *
 * @param text label text shown for this menu item
 * @param onClick called when the user clicks this menu item
 * @param modifier [Modifier] applied to this menu item
 * @param leadingIcon optional [LemonadeIcons] shown before the text at [LemonadeAssetSize.Medium],
 *   `null` by default
 * @param trailingSlot optional composable shown after the text, `null` by default
 * @param enabled whether this menu item is interactive, `true` by default. When `false` the item
 *   dims and stops responding to clicks
 *
 * @see LemonadeUi.Dropdown the parent dropdown menu component
 * @see LemonadeIcons for available icon options
 */
@Composable
public fun LemonadeUi.DropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: LemonadeIcons? = null,
    trailingSlot: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    CoreDropdownItem(
        text = text,
        onClick = onClick,
        leadingSlot = if (leadingIcon != null) {
            {
                LemonadeUi.Icon(
                    icon = leadingIcon,
                    contentDescription = null,
                    size = LemonadeAssetSize.Medium,
                    tint = LemonadeTheme.colors.content.contentSecondary,
                )
            }
        } else {
            null
        },
        trailingSlot = trailingSlot,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
private fun CoreDropdownItem(
    text: String,
    onClick: () -> Unit,
    leadingSlot: (@Composable () -> Unit)?,
    trailingSlot: (@Composable () -> Unit)?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        onClick = onClick,
        modifier = modifier
            .alpha(alpha = if (enabled) 1f else LemonadeTheme.opacities.state.opacityDisabled)
            .clip(LemonadeTheme.shapes.radius400),
        leadingIcon = leadingSlot?.let { slot ->
            {
                SpacedFromLabel(
                    padding = PaddingValues(end = LemonadeTheme.spaces.spacing100),
                    content = slot,
                )
            }
        },
        trailingIcon = trailingSlot?.let { slot ->
            {
                SpacedFromLabel(
                    padding = PaddingValues(start = LemonadeTheme.spaces.spacing100),
                    content = slot,
                )
            }
        },
        enabled = enabled,
        contentPadding = PaddingValues(
            horizontal = LemonadeTheme.spaces.spacing300,
            vertical = LemonadeTheme.spaces.spacing200,
        ),
        text = {
            LemonadeUi.Text(
                text = text,
                textStyle = LemonadeTheme.typography.bodyMediumRegular,
            )
        },
    )
}

@Composable
private fun SpacedFromLabel(
    padding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.padding(paddingValues = padding)) {
        content()
    }
}
