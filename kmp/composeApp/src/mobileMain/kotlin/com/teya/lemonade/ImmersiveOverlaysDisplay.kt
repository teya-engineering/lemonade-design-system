package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons

private const val IMMERSIVE_TOOLTIP_ANCHOR = "immersive-overlays-tooltip"

/**
 * Reproduction surface for overlays opened by an app that runs fully immersive. Switching the host
 * bars back on is what tells the two behaviours apart: an inherited overlay follows the host, while
 * `hideNavigationBar = true` hides the navigation bar either way.
 */
@Composable
internal fun ImmersiveOverlaysSampleDisplay() {
    var hideHostSystemBars by remember { mutableStateOf(true) }
    HideSystemBarsEffect(enabled = hideHostSystemBars)

    val toasts = LocalLemonadeToastState.current
    val tooltips = LocalLemonadeTooltipState.current

    var showInheritedSheet by remember { mutableStateOf(false) }
    var showForcedSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing600),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(all = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = "Immersive Overlays",
            textStyle = LemonadeTheme.typography.headingSmall,
        )
        LemonadeUi.Text(
            text = "The system bars are hidden for this screen. Every overlay below should keep " +
                "them hidden, with no flash when it opens or closes.",
            color = LemonadeTheme.colors.content.contentSecondary,
        )

        LemonadeUi.Switch(
            checked = hideHostSystemBars,
            onCheckedChange = { checked -> hideHostSystemBars = checked },
            label = "Hide host system bars",
            supportText = "Turn off to check that overlays leave visible bars alone.",
        )

        ImmersiveOverlaySection(title = "Bottom Sheet — inherited") {
            LemonadeUi.Button(
                label = "Open Bottom Sheet",
                onClick = { showInheritedSheet = true },
                variant = LemonadeButtonVariant.Secondary,
                size = LemonadeButtonSize.Medium,
            )
        }

        ImmersiveOverlaySection(title = "Bottom Sheet — hideNavigationBar = true") {
            LemonadeUi.Button(
                label = "Open Forced Bottom Sheet",
                onClick = { showForcedSheet = true },
                variant = LemonadeButtonVariant.Secondary,
                size = LemonadeButtonSize.Medium,
            )
        }

        ImmersiveOverlaySection(title = "Dialog") {
            LemonadeUi.Button(
                label = "Open Dialog",
                onClick = { showDialog = true },
                variant = LemonadeButtonVariant.Secondary,
                size = LemonadeButtonSize.Medium,
            )
        }

        ImmersiveOverlaySection(title = "Dropdown") {
            Box {
                LemonadeUi.Button(
                    label = "Open Dropdown",
                    onClick = { showDropdown = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
                LemonadeUi.Dropdown(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                ) {
                    LemonadeUi.DropdownItem(
                        text = "First option",
                        onClick = { showDropdown = false },
                        leadingIcon = LemonadeIcons.Gear,
                    )
                    LemonadeUi.DropdownItem(
                        text = "Second option",
                        onClick = { showDropdown = false },
                        leadingIcon = LemonadeIcons.Link,
                    )
                }
            }
        }

        ImmersiveOverlaySection(title = "Toast and Tooltip") {
            LemonadeUi.Button(
                label = "Show Toast",
                onClick = { toasts.show(label = "Toast over immersive content") },
                variant = LemonadeButtonVariant.Secondary,
                size = LemonadeButtonSize.Medium,
                modifier = Modifier.lemonadeTooltipAnchor(key = IMMERSIVE_TOOLTIP_ANCHOR),
            )
            LemonadeUi.Button(
                label = "Show Tooltip",
                onClick = {
                    tooltips.show(
                        anchor = IMMERSIVE_TOOLTIP_ANCHOR,
                        title = "Tooltip",
                        content = "Anchored to the toast button, drawn inside the host window.",
                    )
                },
                variant = LemonadeButtonVariant.Secondary,
                size = LemonadeButtonSize.Medium,
            )
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showInheritedSheet,
        onDismissRequest = { showInheritedSheet = false },
    ) {
        ImmersiveOverlayContent(
            title = "Inherited",
            body = "This sheet mirrors whatever system bars the host window hides.",
            onClose = { showInheritedSheet = false },
        )
    }

    ForcedHiddenNavBarBottomSheet(
        expanded = showForcedSheet,
        onDismissRequest = { showForcedSheet = false },
    )

    LemonadeUi.Dialog(
        expanded = showDialog,
        onDismissRequest = { showDialog = false },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Dialog",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "The system bars should stay hidden behind this dialog.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Close",
                onClick = { showDialog = false },
                variant = LemonadeButtonVariant.Primary,
                size = LemonadeButtonSize.Medium,
            )
        }
    }
}

@Composable
internal fun ImmersiveOverlayContent(
    title: String,
    body: String,
    onClose: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing400),
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingSmall,
        )
        LemonadeUi.Text(
            text = body,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        LemonadeUi.Button(
            label = "Close",
            onClick = onClose,
            variant = LemonadeButtonVariant.Primary,
            size = LemonadeButtonSize.Medium,
        )
        Spacer(
            modifier = Modifier.height(height = LemonadeTheme.spaces.spacing400),
        )
    }
}

@Composable
private fun ImmersiveOverlaySection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )

        content()
    }
}
