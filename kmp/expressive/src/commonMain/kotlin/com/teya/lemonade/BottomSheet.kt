package com.teya.lemonade

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeBottomSheetVariant

/**
 * A bottom sheet overlay following the Lemonade Design System.
 *
 * Slides a modal sheet up from the bottom of the screen, styled with Lemonade design tokens for
 * shape, color, and elevation. [expanded] controls visibility, the same pattern used by
 * [LemonadeUi.Dialog] and [LemonadeUi.Dropdown].
 *
 * Exit animations run automatically: when [expanded] flips from `true` to `false` — from a button
 * click inside the sheet, say — the sheet animates out before leaving the composition.
 * Drag-to-dismiss and scrim taps animate the same way.
 *
 * ## Usage
 *
 * ```kotlin
 * var showSheet by remember { mutableStateOf(false) }
 *
 * LemonadeUi.Button(
 *     label = "Open Bottom Sheet",
 *     onClick = { showSheet = true },
 * )
 *
 * LemonadeUi.BottomSheet(
 *     expanded = showSheet,
 *     onDismissRequest = { showSheet = false },
 * ) {
 *     Column(modifier = Modifier.padding(LemonadeTheme.spaces.spacing400)) {
 *         LemonadeUi.Text(text = "Bottom Sheet Title", textStyle = LemonadeTheme.typography.headingSmall)
 *         LemonadeUi.Text(text = "Sheet body content goes here.")
 *     }
 * }
 * ```
 *
 * ## Design Notes
 *
 * - The sheet uses [LemonadeTheme.radius.radius500] for the top corners.
 * - [background] resolves the background color: [LemonadeBottomSheetVariant.Default] maps to
 *   [LemonadeTheme.colors.background.bgDefault] and [LemonadeBottomSheetVariant.Subtle] maps to
 *   [LemonadeTheme.colors.background.bgSubtle].
 * - Tonal elevation is 0.dp; the sheet relies on Lemonade color tokens for visual hierarchy.
 * - The drag handle uses the default [BottomSheetDefaults.DragHandle] styling.
 * - The sheet keeps whichever system bars the host window hides, never shows one the host hides.
 *
 * @param expanded whether the bottom sheet is currently visible. When `false` the sheet animates
 *   out, then leaves the composition
 * @param onDismissRequest called when the user requests dismissal, by swiping down, tapping the
 *   scrim, or pressing back
 * @param showDragHandle whether to show the drag handle at the top of the sheet, `true` by default
 * @param skipPartiallyExpanded whether to skip the half-expanded state and always open at full
 *   height, `false` by default
 * @param gesturesEnabled whether the sheet responds to swipe and drag gestures. When `false` the
 *   drag handle is hidden, overriding [showDragHandle], and the sheet cannot be dragged. `true` by
 *   default
 * @param background background variant of the sheet, [LemonadeBottomSheetVariant.Default] by default
 * @param properties dismissal behaviour for back press and scrim tap, both enabled by default
 * @param content sheet body, composed in a [ColumnScope]
 *
 * @see LemonadeUi.Dialog for a dialog overlay with the same visibility pattern
 * @see LemonadeUi.Dropdown for a dropdown menu overlay with the same visibility pattern
 * @see ModalBottomSheet the underlying component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    gesturesEnabled: Boolean = true,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    properties: LemonadeBottomSheetProperties = LemonadeBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    CoreBottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        showDragHandle = showDragHandle,
        skipPartiallyExpanded = skipPartiallyExpanded,
        gesturesEnabled = gesturesEnabled,
        background = background,
        properties = properties,
        content = content,
    )
}

@Deprecated(
    message = "Use the overload with a gesturesEnabled parameter.",
    replaceWith = ReplaceWith(
        expression = "BottomSheet(expanded, onDismissRequest, showDragHandle, skipPartiallyExpanded, " +
            "true, background, properties, content)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    properties: LemonadeBottomSheetProperties = LemonadeBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        showDragHandle = showDragHandle,
        skipPartiallyExpanded = skipPartiallyExpanded,
        gesturesEnabled = true,
        background = background,
        properties = properties,
        content = content,
    )
}

@Deprecated(
    message = "Use the overload with a properties parameter.",
    replaceWith = ReplaceWith(
        expression = "BottomSheet(expanded, onDismissRequest, showDragHandle, skipPartiallyExpanded, " +
            "true, background, LemonadeBottomSheetProperties(), content)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    content: @Composable ColumnScope.() -> Unit,
) {
    BottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        showDragHandle = showDragHandle,
        skipPartiallyExpanded = skipPartiallyExpanded,
        background = background,
        properties = LemonadeBottomSheetProperties(),
        content = content,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CoreBottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    gesturesEnabled: Boolean = true,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    properties: LemonadeBottomSheetProperties = LemonadeBottomSheetProperties(),
    forceHideNavigationBar: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
    )
    LaunchedEffect(expanded) {
        if (!expanded && sheetState.isVisible) {
            sheetState.hide()
        }
    }

    val containerColor = when (background) {
        LemonadeBottomSheetVariant.Default -> LemonadeTheme.colors.background.bgDefault
        LemonadeBottomSheetVariant.Subtle -> LemonadeTheme.colors.background.bgSubtle
    }

    val mirrorSystemBars = systemBarsMirror(forceHideNavigationBar = forceHideNavigationBar)

    if (expanded || sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            sheetGesturesEnabled = gesturesEnabled,
            shape = RoundedCornerShape(
                topStart = LemonadeTheme.radius.radius500,
                topEnd = LemonadeTheme.radius.radius500,
            ),
            containerColor = containerColor,
            tonalElevation = 0.dp,
            dragHandle = if (showDragHandle && gesturesEnabled) {
                { BottomSheetDefaults.DragHandle() }
            } else {
                null
            },
            properties = properties.toMaterial(),
            content = {
                mirrorSystemBars()
                content()
            },
        )
    }
}
