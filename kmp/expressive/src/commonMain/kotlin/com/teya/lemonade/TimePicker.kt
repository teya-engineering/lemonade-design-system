package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons

/**
 * Whether a time is entered on the clock dial or typed into hour / minute fields.
 *
 * @property Dial The analog clock face — tap or drag to set the hour, then the minute.
 * @property Input Two numeric fields the merchant types into, plus the AM/PM toggle in 12-hour mode.
 */
public enum class LemonadeTimePickerDisplayMode {
    Dial,
    Input,
}

/**
 * State holder for [LemonadeUi.TimePicker], [LemonadeUi.TimeInput] and
 * [LemonadeUi.TimePickerDialog].
 *
 * Holds the currently selected [hour] and [minute] as observable state. Both are read-only: the
 * merchant's interaction writes them, and callers seed a new time by re-keying the state, which
 * [rememberLemonadeTimePickerState] does automatically when any of its arguments change.
 *
 * @param initialHour The initially selected hour, in the 0..23 range regardless of [is24Hour].
 * @param initialMinute The initially selected minute, in the 0..59 range.
 * @param is24Hour Whether the picker uses a 24-hour clock. The Lemonade Design System never reads
 * the host platform's clock setting — pass the merchant's own preference (on Android that is
 * `DateFormat.is24HourFormat(context)`), so the same component behaves correctly on every target.
 * @see rememberLemonadeTimePickerState
 */
@Stable
public class LemonadeTimePickerState internal constructor(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    internal val delegate: TimePickerState = TimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    /** Whether the picker uses a 24-hour clock, as passed at construction. */
    @OptIn(ExperimentalMaterial3Api::class)
    public val is24Hour: Boolean get() = delegate.is24hour

    /** The selected hour, always in the 0..23 range even when [is24Hour] is `false`. */
    @OptIn(ExperimentalMaterial3Api::class)
    public val hour: Int get() = delegate.hour

    /** The selected minute, in the 0..59 range. */
    @OptIn(ExperimentalMaterial3Api::class)
    public val minute: Int get() = delegate.minute
}

/**
 * Creates and remembers a [LemonadeTimePickerState].
 *
 * The state is re-created whenever any argument changes, so hoisting it above a dialog and passing
 * a freshly loaded time re-seeds the picker without any extra plumbing.
 *
 * @param initialHour The initially selected hour, in the 0..23 range.
 * @param initialMinute The initially selected minute, in the 0..59 range.
 * @param is24Hour Whether the picker uses a 24-hour clock — supplied by the caller, never read
 * from the platform.
 */
@Composable
public fun rememberLemonadeTimePickerState(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
): LemonadeTimePickerState =
    remember(initialHour, initialMinute, is24Hour) {
        LemonadeTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = is24Hour,
        )
    }

/**
 * A clock-dial time picker following the Lemonade Design System.
 *
 * Shows the selected time above an analog clock face; the merchant taps or drags to set the hour,
 * then the minute. In 12-hour mode an AM/PM toggle sits beside the dial.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberLemonadeTimePickerState(
 *     initialHour = 9,
 *     initialMinute = 30,
 *     is24Hour = DateFormat.is24HourFormat(LocalContext.current),
 * )
 * LemonadeUi.TimePicker(state = state)
 * // Observe: state.hour, state.minute
 * ```
 *
 * @param state Configuration state created via [rememberLemonadeTimePickerState]. Observe
 * [LemonadeTimePickerState.hour] and [LemonadeTimePickerState.minute] to react to the selection.
 * @param modifier Optional [Modifier] for layout adjustments.
 * @see LemonadeUi.TimeInput For the typed-entry variant.
 * @see LemonadeUi.TimePickerDialog For the dial inside a confirm / cancel dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.TimePicker(
    state: LemonadeTimePickerState,
    modifier: Modifier = Modifier,
) {
    LemonadeTimePickerTheme {
        TimePicker(
            state = state.delegate,
            modifier = modifier,
            colors = lemonadeTimePickerColors(),
        )
    }
}

/**
 * A typed-entry time picker following the Lemonade Design System.
 *
 * Shows two numeric fields for the hour and the minute, plus an AM/PM toggle in 12-hour mode. It
 * is the compact counterpart to [LemonadeUi.TimePicker] and fits inline on a form or a filter
 * sheet, where a clock dial would dominate the screen.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberLemonadeTimePickerState(
 *     initialHour = 9,
 *     initialMinute = 30,
 *     is24Hour = DateFormat.is24HourFormat(LocalContext.current),
 * )
 * LemonadeUi.TimeInput(state = state)
 * // Observe: state.hour, state.minute
 * ```
 *
 * ## Design Notes
 *
 * - The focused field's outline and its cursor are pinned to the same token
 *   [LemonadeUi.TextField] uses when focused, so they stay Lemonade-coloured under any host theme
 *   rather than falling back to Material's purple.
 * - The "Hour" and "Minute" labels below the fields are Material's, and Material localizes them
 *   from its own resources; nothing here can restyle or re-word them.
 *
 * @param state Configuration state created via [rememberLemonadeTimePickerState]. Observe
 * [LemonadeTimePickerState.hour] and [LemonadeTimePickerState.minute] to react to the selection.
 * @param modifier Optional [Modifier] for layout adjustments.
 * @see LemonadeUi.TimePicker For the clock-dial variant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun LemonadeUi.TimeInput(
    state: LemonadeTimePickerState,
    modifier: Modifier = Modifier,
) {
    LemonadeTimePickerTheme {
        TimeInput(
            state = state.delegate,
            modifier = modifier,
            colors = lemonadeTimePickerColors(),
        )
    }
}

/**
 * A time picker inside a confirm / cancel dialog, following the Lemonade Design System.
 *
 * Wraps [LemonadeUi.TimePicker] or [LemonadeUi.TimeInput] — chosen with [displayMode] — in a
 * [LemonadeUi.Dialog] carrying a title and a Cancel / Confirm button pair. The selection is only
 * reported through [onConfirm]: dismissing the dialog leaves the caller's time untouched, so a
 * merchant who backs out keeps whatever was set before.
 *
 * ## Usage
 * ```kotlin
 * var showDialog by remember { mutableStateOf(false) }
 * val state = rememberLemonadeTimePickerState(
 *     initialHour = expiresAt.hour,
 *     initialMinute = expiresAt.minute,
 *     is24Hour = DateFormat.is24HourFormat(LocalContext.current),
 * )
 *
 * LemonadeUi.TimePickerDialog(
 *     expanded = showDialog,
 *     title = stringResource(R.string.expiration_time),
 *     confirmLabel = stringResource(R.string.confirm),
 *     cancelLabel = stringResource(R.string.cancel),
 *     switchToInputLabel = stringResource(R.string.switch_to_text_input),
 *     switchToDialLabel = stringResource(R.string.switch_to_clock_dial),
 *     state = state,
 *     onDismissRequest = { showDialog = false },
 *     onConfirm = { hour, minute ->
 *         expiresAt = expiresAt.withTime(hour, minute)
 *         showDialog = false
 *     },
 * )
 * ```
 *
 * ## Design Notes
 *
 * - Closing the dialog is the caller's job in both callbacks — the component never hides itself,
 *   matching the `expanded` contract of [LemonadeUi.Dialog] and [LemonadeUi.BottomSheet].
 * - A toggle in the button row switches between the dial and the typed fields, the way Material's
 *   own dialog does. Both read the same [LemonadeTimePickerState], so the time in progress
 *   survives the switch.
 * - Every label is a plain [String]: the Lemonade Design System carries no copy of its own, so the
 *   caller resolves and localizes them.
 * - On a short or wide window — a landscape phone or a tablet — Material lays the dial out
 *   side by side with the selected time, and the dialog takes the width that layout needs rather
 *   than the platform's default dialog width, which would clip the clock face.
 *
 * @param expanded Whether the dialog is currently visible. When `false`, nothing is composed.
 * @param title Heading shown above the picker, already localized.
 * @param confirmLabel Label of the confirming button, already localized.
 * @param cancelLabel Label of the dismissing button, already localized.
 * @param switchToInputLabel Accessibility label of the mode toggle while the dial is showing — it
 * switches to the typed fields. Already localized.
 * @param switchToDialLabel Accessibility label of the mode toggle while the typed fields are
 * showing — it switches to the dial. Already localized.
 * @param state Configuration state created via [rememberLemonadeTimePickerState].
 * @param onDismissRequest Called when the merchant cancels, taps outside, or presses back.
 * @param onConfirm Called with the chosen hour (0..23) and minute (0..59) when the merchant
 * confirms.
 * @param initialDisplayMode Which of the two the dialog opens on. The merchant can switch freely
 * afterwards, so this only seeds the first frame; passing a new value re-seeds it. Defaults to
 * [LemonadeTimePickerDisplayMode.Dial].
 * @see LemonadeUi.Dialog The underlying dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.TimePickerDialog(
    expanded: Boolean,
    title: String,
    confirmLabel: String,
    cancelLabel: String,
    switchToInputLabel: String,
    switchToDialLabel: String,
    state: LemonadeTimePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialDisplayMode: LemonadeTimePickerDisplayMode = LemonadeTimePickerDisplayMode.Dial,
) {
    var displayMode by remember(initialDisplayMode) { mutableStateOf(initialDisplayMode) }

    // Material reads the window from LocalWindowInfo and asks for its side-by-side layout on a
    // short or wide window — a landscape phone or a tablet. The dial picks that up on its own; the
    // dialog only needs to know so it can take the width that layout needs.
    val isHorizontal = TimePickerDefaults.layoutType() == TimePickerLayoutType.Horizontal &&
        displayMode == LemonadeTimePickerDisplayMode.Dial

    LemonadeUi.Dialog(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        sizeToContent = isHorizontal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LemonadeTheme.spaces.spacing500,
                        top = LemonadeTheme.spaces.spacing500,
                        end = LemonadeTheme.spaces.spacing500,
                    ),
                textStyle = LemonadeTheme.typography.bodySmallSemiBold,
            )

            val pickerModifier = Modifier.padding(
                horizontal = LemonadeTheme.spaces.spacing600,
                vertical = LemonadeTheme.spaces.spacing400,
            )

            when (displayMode) {
                LemonadeTimePickerDisplayMode.Dial -> LemonadeUi.TimePicker(
                    state = state,
                    modifier = pickerModifier,
                )

                LemonadeTimePickerDisplayMode.Input -> LemonadeUi.TimeInput(
                    state = state,
                    modifier = pickerModifier,
                )
            }

            val toggleIcon = when (displayMode) {
                LemonadeTimePickerDisplayMode.Dial -> LemonadeIcons.Keyboard
                LemonadeTimePickerDisplayMode.Input -> LemonadeIcons.Clock
            }
            val toggleLabel = when (displayMode) {
                LemonadeTimePickerDisplayMode.Dial -> switchToInputLabel
                LemonadeTimePickerDisplayMode.Input -> switchToDialLabel
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LemonadeTheme.spaces.spacing400,
                        end = LemonadeTheme.spaces.spacing400,
                        bottom = LemonadeTheme.spaces.spacing400,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LemonadeUi.IconButton(
                    icon = toggleIcon,
                    contentDescription = toggleLabel,
                    onClick = {
                        displayMode = when (displayMode) {
                            LemonadeTimePickerDisplayMode.Dial -> LemonadeTimePickerDisplayMode.Input
                            LemonadeTimePickerDisplayMode.Input -> LemonadeTimePickerDisplayMode.Dial
                        }
                    },
                    variant = LemonadeButtonVariant.Neutral,
                    type = LemonadeButtonType.Ghost,
                    size = LemonadeButtonSize.Small,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LemonadeTheme.spaces.spacing200,
                    ),
                ) {
                    LemonadeUi.Button(
                        label = cancelLabel,
                        onClick = onDismissRequest,
                        variant = LemonadeButtonVariant.Neutral,
                        size = LemonadeButtonSize.Small,
                    )
                    LemonadeUi.Button(
                        label = confirmLabel,
                        onClick = { onConfirm(state.hour, state.minute) },
                        size = LemonadeButtonSize.Small,
                    )
                }
            }
        }
    }
}

/**
 * Material reads three things from the ambient theme that [TimePickerColors] has no slot for and
 * that no parameter exposes, so they are re-pointed for the picker's subtree alone:
 *
 * - `typography` carries the typeface of every label the picker draws — the clock numerals, the
 *   hour / minute values, the AM/PM toggle and the field captions. Left alone it is Material's
 *   default typeface under a theme that does not remap the scale, so the Lemonade mapping is
 *   pinned here and the picker stays on Figtree wherever it is hosted. Two of its slots are then
 *   stepped down to 14sp, because the Lemonade scale runs larger than the sizes Material's layout
 *   was drawn for: `bodyLarge`, read only by the clock numerals, which at the scale's own 18sp
 *   spaces the dial noticeably wider than the Material original, and `titleMedium`, read only by
 *   the AM/PM toggle in both the dial and the typed-entry variants.
 * - `colorScheme.primary` sets the focused field's outline (Material's `FocusOutlineColor` token)
 *   and its blinking cursor — Material's purple, left alone. Pinned to the same token
 *   [LemonadeUi.TextField] uses when focused.
 * - `shapes.small` sets the hour / minute field corners and the AM/PM toggle's. Material maps it to
 *   radius200, but the Lemonade fields the picker sits beside ([LemonadeUi.TextField],
 *   [LemonadeUi.PinCode]) are radius400.
 *
 * Every other slot comes from the same Lemonade mapping [LemonadeExpressiveTheme] installs, not
 * from the surrounding theme, so the picker looks the same under a host that never set Material up.
 */
@Composable
private fun LemonadeTimePickerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lemonadeExpressiveColorScheme().copy(
            primary = LemonadeTheme.colors.border.borderSelected,
        ),
        shapes = lemonadeExpressiveShapes().copy(
            small = RoundedCornerShape(size = LemonadeTheme.radius.radius400),
        ),
        typography = lemonadeExpressiveTypography().copy(
            bodyLarge = LemonadeTheme.typography.bodySmallRegular.textStyle,
            titleMedium = LemonadeTheme.typography.bodySmallSemiBold.textStyle,
        ),
        content = content,
    )
}

/**
 * Material 3's time picker carries its own colour scheme, so every one of its fourteen slots is
 * remapped onto a Lemonade token here. Kept private: [TimePickerColors] is a Material type, and
 * the Lemonade public surface stays free of them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun lemonadeTimePickerColors(): TimePickerColors =
    TimePickerDefaults.colors(
        clockDialColor = LemonadeTheme.colors.background.bgElevated,
        selectorColor = LemonadeTheme.colors.background.bgBrand,
        containerColor = LemonadeTheme.colors.background.bgDefault,
        periodSelectorBorderColor = LemonadeTheme.colors.border.borderNeutralMedium,
        clockDialSelectedContentColor = LemonadeTheme.colors.content.contentOnBrandHigh,
        clockDialUnselectedContentColor = LemonadeTheme.colors.content.contentPrimary,
        periodSelectorSelectedContainerColor = LemonadeTheme.colors.background.bgBrandSubtle,
        periodSelectorUnselectedContainerColor = LemonadeTheme.colors.background.bgDefault,
        periodSelectorSelectedContentColor = LemonadeTheme.colors.content.contentBrand,
        periodSelectorUnselectedContentColor = LemonadeTheme.colors.content.contentSecondary,
        timeSelectorSelectedContainerColor = LemonadeTheme.colors.background.bgBrandSubtle,
        timeSelectorUnselectedContainerColor = LemonadeTheme.colors.background.bgElevated,
        timeSelectorSelectedContentColor = LemonadeTheme.colors.content.contentBrand,
        timeSelectorUnselectedContentColor = LemonadeTheme.colors.content.contentPrimary,
    )
