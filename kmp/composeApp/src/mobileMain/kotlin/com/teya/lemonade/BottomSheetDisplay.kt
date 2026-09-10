package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeBottomSheetVariant
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant

@Composable
internal fun BottomSheetSampleDisplay() {
    var showBasicSheet by remember { mutableStateOf(false) }
    var showNoDragHandleSheet by remember { mutableStateOf(false) }
    var showSubtleSheet by remember { mutableStateOf(false) }
    var showNonDismissibleSheet by remember { mutableStateOf(false) }

    SampleScreenDisplayLazyColumn(title = "BottomSheet") {
        item(key = "Basic Bottom Sheet") {
            BottomSheetSection(title = "Basic Bottom Sheet") {
                LemonadeUi.Button(
                    label = "Open Bottom Sheet",
                    onClick = { showBasicSheet = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }

        item(key = "Without Drag Handle") {
            BottomSheetSection(title = "Without Drag Handle") {
                LemonadeUi.Text(
                    text = "This bottom sheet has no drag handle",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
                LemonadeUi.Button(
                    label = "Open Without Drag Handle",
                    onClick = { showNoDragHandleSheet = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }

        item(key = "Subtle Background") {
            BottomSheetSection(title = "Subtle Background") {
                LemonadeUi.Text(
                    text = "This bottom sheet uses the Subtle background variant (bgSubtle)",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
                LemonadeUi.Button(
                    label = "Open Subtle Background",
                    onClick = { showSubtleSheet = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }

        item(key = "Non-dismissible") {
            BottomSheetSection(title = "Non-dismissible") {
                LemonadeUi.Text(
                    text = "Scrim taps, back presses, and swipe-down are all ignored; " +
                        "the sheet must be closed via the button.",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
                LemonadeUi.Button(
                    label = "Open Non-dismissible",
                    onClick = { showNonDismissibleSheet = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showBasicSheet,
        onDismissRequest = { showBasicSheet = false },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Bottom Sheet Title",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "This is an example bottom sheet with free-form content. " +
                    "Swipe down or tap the scrim to dismiss.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Close",
                onClick = { showBasicSheet = false },
                variant = LemonadeButtonVariant.Primary,
                size = LemonadeButtonSize.Medium,
            )
            Spacer(
                modifier = Modifier.height(LemonadeTheme.spaces.spacing400),
            )
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showNoDragHandleSheet,
        onDismissRequest = { showNoDragHandleSheet = false },
        showDragHandle = false,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "No Drag Handle",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "This bottom sheet has no drag handle visible. Tap the scrim or use the button to close.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Close",
                onClick = { showNoDragHandleSheet = false },
                variant = LemonadeButtonVariant.Primary,
                size = LemonadeButtonSize.Medium,
            )
            Spacer(
                modifier = Modifier.height(LemonadeTheme.spaces.spacing400),
            )
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showSubtleSheet,
        onDismissRequest = { showSubtleSheet = false },
        background = LemonadeBottomSheetVariant.Subtle,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Subtle Background",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "This bottom sheet uses the Subtle background variant, " +
                    "applying bgSubtle from the Lemonade tokens.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Close",
                onClick = { showSubtleSheet = false },
                variant = LemonadeButtonVariant.Primary,
                size = LemonadeButtonSize.Medium,
            )
            Spacer(
                modifier = Modifier.height(LemonadeTheme.spaces.spacing400),
            )
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showNonDismissibleSheet,
        onDismissRequest = { showNonDismissibleSheet = false },
        gesturesEnabled = false,
        properties = LemonadeBottomSheetProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Non-dismissible",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "Scrim taps, back presses, and swipe-down are ignored, and there's no drag handle. " +
                    "Use the button below to close.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Close",
                onClick = { showNonDismissibleSheet = false },
                variant = LemonadeButtonVariant.Primary,
                size = LemonadeButtonSize.Medium,
            )
            Spacer(
                modifier = Modifier.height(LemonadeTheme.spaces.spacing400),
            )
        }
    }
}

@Composable
private fun BottomSheetSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )

        content()
    }
}
