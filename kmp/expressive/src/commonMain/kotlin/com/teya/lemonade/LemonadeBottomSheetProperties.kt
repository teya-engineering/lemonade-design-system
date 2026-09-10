package com.teya.lemonade

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Stable

/**
 * Configuration of dismissal behaviours for [LemonadeUi.BottomSheet].
 *
 * Mirrors [androidx.compose.material3.ModalBottomSheetProperties] but keeps the public surface of
 * the Lemonade Design System free of Material 3 types, matching the naming used by
 * [LemonadeUi.Dialog].
 *
 * @param dismissOnBackPress whether pressing the back button requests dismissal, `true` by default
 * @param dismissOnClickOutside whether tapping the scrim requests dismissal, `true` by default
 */
@Stable
public class LemonadeBottomSheetProperties(
    public val dismissOnBackPress: Boolean = true,
    public val dismissOnClickOutside: Boolean = true,
)

@OptIn(ExperimentalMaterial3Api::class)
internal fun LemonadeBottomSheetProperties.toMaterial(): ModalBottomSheetProperties =
    ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress,
        shouldDismissOnClickOutside = dismissOnClickOutside,
    )
