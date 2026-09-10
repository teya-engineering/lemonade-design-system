package com.teya.lemonade

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.teya.lemonade.core.LemonadeBottomSheetVariant

/**
 * Android [BottomSheet][LemonadeUi.BottomSheet] that hides the system navigation bar.
 *
 * Forces the back / home / recent buttons hidden inside the sheet's own dialog window. Every
 * [LemonadeUi.BottomSheet] already keeps whichever system bars the host window hides, so an app
 * running fully immersive needs nothing from this overload. Reach for it only to hide the
 * navigation bar while the host window still shows it.
 *
 * @param expanded whether the bottom sheet is currently visible
 * @param onDismissRequest called when the user requests dismissal of the bottom sheet
 * @param hideNavigationBar whether to hide the navigation bar even when the host window shows it
 * @param showDragHandle whether to show the drag handle at the top of the sheet
 * @param skipPartiallyExpanded whether to skip the half-expanded state and always open at full
 *   height
 * @param gesturesEnabled whether the sheet responds to swipe and drag gestures. When `false` the
 *   drag handle is hidden, overriding [showDragHandle], and the sheet cannot be dragged. `true` by
 *   default
 * @param background background variant of the sheet, [LemonadeBottomSheetVariant.Default] by default
 * @param properties dismissal behaviour for back press and scrim tap, both enabled by default
 * @param content sheet body, composed in a [ColumnScope]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    hideNavigationBar: Boolean,
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
        forceHideNavigationBar = hideNavigationBar,
        content = content,
    )
}

@Deprecated(
    message = "Use the overload with a gesturesEnabled parameter.",
    replaceWith = ReplaceWith(
        expression = "BottomSheet(expanded, onDismissRequest, hideNavigationBar, showDragHandle, " +
            "skipPartiallyExpanded, true, background, properties, content)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    hideNavigationBar: Boolean,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    properties: LemonadeBottomSheetProperties = LemonadeBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        hideNavigationBar = hideNavigationBar,
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
        expression = "BottomSheet(expanded, onDismissRequest, hideNavigationBar, showDragHandle, " +
            "skipPartiallyExpanded, true, background, LemonadeBottomSheetProperties(), content)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.BottomSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    hideNavigationBar: Boolean,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    background: LemonadeBottomSheetVariant = LemonadeBottomSheetVariant.Default,
    content: @Composable ColumnScope.() -> Unit,
) {
    BottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        hideNavigationBar = hideNavigationBar,
        showDragHandle = showDragHandle,
        skipPartiallyExpanded = skipPartiallyExpanded,
        background = background,
        properties = LemonadeBottomSheetProperties(),
        content = content,
    )
}
