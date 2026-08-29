package com.teya.lemonade

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * A free-content dialog following the Lemonade Design System.
 *
 * This composable provides a flexible dialog that displays custom content inside a styled [Surface]
 * with Lemonade design tokens for shape, color, and elevation. The dialog visibility is controlled
 * by the [expanded] flag, following the same pattern used by [LemonadeUi.Dropdown] and
 * [LemonadeUi.BottomSheet].
 *
 * @param expanded Whether the dialog is currently visible. When `false`, the dialog is not composed.
 * @param onDismissRequest Callback invoked when the user requests to dismiss the dialog
 *   (e.g., by tapping outside or pressing back, depending on [dismissOnClickOutside]
 *   and [dismissOnBackPress]).
 * @param dismissOnClickOutside Whether tapping outside the dialog dismisses it. Defaults to `true`.
 * @param dismissOnBackPress Whether pressing the back button dismisses the dialog. Defaults to `true`.
 * @param sizeToContent Whether the dialog takes the width of its content rather than the
 *   platform's default dialog width. Defaults to `false`, which is the right choice for the
 *   text-and-buttons dialogs that width was sized for, and keeps every dialog a familiar size.
 *   Pass `true` for content that carries a width of its own, such as a side-by-side picker on a
 *   landscape phone or a tablet — the content must have a width of its own rather than filling
 *   whatever it is given.
 *
 *   The content decides only between 280.dp and 560.dp: [BasicAlertDialog] clamps to that range
 *   whatever this is set to. Anything narrower is padded out to 280.dp, and anything wider is
 *   clipped at 560.dp rather than growing, so content approaching that ceiling has little room
 *   left for a larger font scale or a longer translation.
 *
 *   Sizing to the content means measuring its intrinsic width, which not every layout can answer:
 *   a `LazyColumn`, `LazyRow` or anything else built on `SubcomposeLayout` throws when asked. Keep
 *   this `false` for content that scrolls lazily.
 * @param content A composable lambda that defines the dialog's content.
 *
 * ## Usage Example
 *
 * ```kotlin
 * var showDialog by remember { mutableStateOf(false) }
 *
 * LemonadeUi.Button(
 *     label = "Open Dialog",
 *     onClick = { showDialog = true },
 * )
 *
 * LemonadeUi.Dialog(
 *     expanded = showDialog,
 *     onDismissRequest = { showDialog = false },
 * ) {
 *     Column(modifier = Modifier.padding(LemonadeTheme.spaces.spacing400)) {
 *         LemonadeUi.Text(text = "Dialog Title", textStyle = LemonadeTheme.typography.headingSmall)
 *         LemonadeUi.Text(text = "Dialog body content goes here.")
 *     }
 * }
 * ```
 *
 * ## Design Notes
 *
 * - The dialog surface uses [LemonadeTheme.radius.radius400] for rounded corners.
 * - Background color is [LemonadeTheme.colors.background.bgDefault].
 * - Tonal elevation is set to 0.dp; the dialog relies on Lemonade color tokens for visual hierarchy.
 * - The dialog keeps whichever system bars the host window hides, never shows one the host hides.
 * - For overlay components with a unified visibility API, see also [LemonadeUi.Dropdown] and
 *   [LemonadeUi.BottomSheet], which share the same `expanded` flag pattern.
 *
 * @see LemonadeUi.BottomSheet For a bottom sheet overlay with the same visibility pattern.
 * @see LemonadeUi.Dropdown For a dropdown menu overlay with the same visibility pattern.
 * @see BasicAlertDialog The underlying component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.Dialog(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    sizeToContent: Boolean = false,
    content: @Composable () -> Unit,
) {
    val mirrorSystemBars = systemBarsMirror()

    if (expanded) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnClickOutside = dismissOnClickOutside,
                dismissOnBackPress = dismissOnBackPress,
                usePlatformDefaultWidth = !sizeToContent,
            ),
        ) {
            mirrorSystemBars()
            Surface(
                // A platform-width dialog fills what it is granted. One sized by its content is
                // measured at its widest child instead — filling here, or leaving the width
                // unconstrained, would stretch it to the screen edges and the content would decide
                // nothing. Callers keep filling the width they are given either way.
                modifier = if (sizeToContent) {
                    Modifier.width(intrinsicSize = IntrinsicSize.Max)
                } else {
                    Modifier.fillMaxWidth()
                },
                shape = RoundedCornerShape(size = LemonadeTheme.radius.radius400),
                color = LemonadeTheme.colors.background.bgDefault,
                tonalElevation = 0.dp,
                content = content,
            )
        }
    }
}

/**
 * Binary-compatibility shim for the signature that shipped before [sizeToContent] was added. Kept
 * so the released symbol survives; see the binary-compatibility skill.
 */
@Deprecated(
    message = "Binary compatibility only.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.Dialog(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    content: @Composable () -> Unit,
) {
    Dialog(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        dismissOnClickOutside = dismissOnClickOutside,
        dismissOnBackPress = dismissOnBackPress,
        sizeToContent = false,
        content = content,
    )
}
