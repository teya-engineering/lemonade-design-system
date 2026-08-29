package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonVariant

private const val SAMPLE_HOUR = 9
private const val SAMPLE_MINUTE = 30
private const val TIME_FIELD_DIGITS = 2

@Composable
internal fun TimePickerDisplay() {
    // The picker states are hoisted above the lazy list so a selection survives the item being
    // scrolled out of the viewport and disposed.
    val dial24State = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = true)
    val dial12State = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = false)
    val input24State = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = true)
    val input12State = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = false)

    val dialogState = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = true)
    val inputDialogState = rememberLemonadeTimePickerState(SAMPLE_HOUR, SAMPLE_MINUTE, is24Hour = false)

    var expandedDialog by remember { mutableStateOf<LemonadeTimePickerDisplayMode?>(value = null) }
    var confirmedTime by remember { mutableStateOf(value = "—") }

    SampleScreenDisplayLazyColumn(title = "TimePicker") {
        item(key = "dial-24h") {
            TimePickerSample(title = "Dial — 24-hour", state = dial24State) {
                LemonadeUi.TimePicker(state = dial24State)
            }
        }

        item(key = "dial-12h") {
            TimePickerSample(title = "Dial — 12-hour (AM/PM)", state = dial12State) {
                LemonadeUi.TimePicker(state = dial12State)
            }
        }

        item(key = "input-24h") {
            TimePickerSample(title = "Input — 24-hour", state = input24State) {
                LemonadeUi.TimeInput(state = input24State)
            }
        }

        item(key = "input-12h") {
            TimePickerSample(title = "Input — 12-hour (AM/PM)", state = input12State) {
                LemonadeUi.TimeInput(state = input12State)
            }
        }

        item(key = "dialog") {
            TimePickerSection(title = "Dialog") {
                LemonadeUi.Button(
                    label = "Open dial dialog",
                    onClick = { expandedDialog = LemonadeTimePickerDisplayMode.Dial },
                )
                LemonadeUi.Button(
                    label = "Open input dialog",
                    onClick = { expandedDialog = LemonadeTimePickerDisplayMode.Input },
                    variant = LemonadeButtonVariant.Neutral,
                )
                LemonadeUi.Text(
                    text = "Last confirmed: $confirmedTime",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
            }
        }
    }

    LemonadeUi.TimePickerDialog(
        expanded = expandedDialog == LemonadeTimePickerDisplayMode.Dial,
        title = "Expiration time",
        confirmLabel = "Confirm",
        cancelLabel = "Cancel",
        switchToInputLabel = "Switch to text input",
        switchToDialLabel = "Switch to clock dial",
        state = dialogState,
        onDismissRequest = { expandedDialog = null },
        onConfirm = { hour, minute ->
            confirmedTime = formatTime(hour, minute)
            expandedDialog = null
        },
    )

    LemonadeUi.TimePickerDialog(
        expanded = expandedDialog == LemonadeTimePickerDisplayMode.Input,
        title = "Expiration time",
        confirmLabel = "Confirm",
        cancelLabel = "Cancel",
        switchToInputLabel = "Switch to text input",
        switchToDialLabel = "Switch to clock dial",
        state = inputDialogState,
        onDismissRequest = { expandedDialog = null },
        onConfirm = { hour, minute ->
            confirmedTime = formatTime(hour, minute)
            expandedDialog = null
        },
        initialDisplayMode = LemonadeTimePickerDisplayMode.Input,
    )
}

@Composable
private fun TimePickerSample(
    title: String,
    state: LemonadeTimePickerState,
    content: @Composable () -> Unit,
) {
    TimePickerSection(title = title) {
        content()
        LemonadeUi.Text(
            text = "Selected: ${formatTime(state.hour, state.minute)}",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun TimePickerSection(
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

private fun formatTime(
    hour: Int,
    minute: Int,
): String = "${hour.toString().padStart(TIME_FIELD_DIGITS, '0')}:${minute.toString().padStart(TIME_FIELD_DIGITS, '0')}"
