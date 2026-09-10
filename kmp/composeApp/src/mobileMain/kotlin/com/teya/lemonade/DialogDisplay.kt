package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant

@Composable
internal fun DialogSampleDisplay() {
    var showBasicDialog by remember { mutableStateOf(false) }
    var showNonDismissableDialog by remember { mutableStateOf(false) }

    SampleScreenDisplayLazyColumn(title = "Dialog") {
        item(key = "Basic Dialog") {
            DialogSection(title = "Basic Dialog") {
                LemonadeUi.Button(
                    label = "Open Dialog",
                    onClick = { showBasicDialog = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }

        item(key = "Non-Dismissable Dialog") {
            DialogSection(title = "Non-Dismissable Dialog") {
                LemonadeUi.Text(
                    text = "This dialog cannot be dismissed by tapping outside or pressing back",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
                LemonadeUi.Button(
                    label = "Open Non-Dismissable Dialog",
                    onClick = { showNonDismissableDialog = true },
                    variant = LemonadeButtonVariant.Secondary,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }
    }

    LemonadeUi.Dialog(
        expanded = showBasicDialog,
        onDismissRequest = { showBasicDialog = false },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Dialog Title",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "This is an example dialog with free-form content. You can place any composable content here.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                    alignment = Alignment.End,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                LemonadeUi.Button(
                    label = "Cancel",
                    onClick = { showBasicDialog = false },
                    variant = LemonadeButtonVariant.Neutral,
                    size = LemonadeButtonSize.Small,
                )
                LemonadeUi.Button(
                    label = "Confirm",
                    onClick = { showBasicDialog = false },
                    variant = LemonadeButtonVariant.Primary,
                    size = LemonadeButtonSize.Small,
                )
            }
        }
    }

    LemonadeUi.Dialog(
        expanded = showNonDismissableDialog,
        onDismissRequest = { showNonDismissableDialog = false },
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Non-Dismissable Dialog",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "You must use the button below to close this dialog. " +
                    "Tapping outside or pressing back will not dismiss it.",
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                    alignment = Alignment.End,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                LemonadeUi.Button(
                    label = "Close",
                    onClick = { showNonDismissableDialog = false },
                    variant = LemonadeButtonVariant.Primary,
                    size = LemonadeButtonSize.Small,
                )
            }
        }
    }
}

@Composable
private fun DialogSection(
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
