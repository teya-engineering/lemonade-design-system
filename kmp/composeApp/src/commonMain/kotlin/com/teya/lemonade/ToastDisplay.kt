package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeIcons

@Composable
internal fun ToastDisplay() {
    val toastState = LocalLemonadeToastState.current
    var showSheet by remember { mutableStateOf(false) }

    SampleScreenDisplayLazyColumn(title = "Toast") {
        item(key = "Voice Variants") {
            ToastSection("Voice Variants") {
                LemonadeUi.Button(
                    label = "Success Toast",
                    onClick = {
                        toastState.show(
                            label = "Changes saved successfully",
                            voice = ToastVoice.Success,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Error Toast",
                    onClick = {
                        toastState.show(
                            label = "Something went wrong",
                            voice = ToastVoice.Error,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Neutral Toast",
                    onClick = {
                        toastState.show(
                            label = "Copied to clipboard",
                            voice = ToastVoice.Neutral,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Neutral with Icon",
                    onClick = {
                        toastState.show(
                            label = "Link copied",
                            voice = ToastVoice.Neutral,
                            icon = LemonadeIcons.Link,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Long Label",
                    onClick = {
                        toastState.show(
                            label = "Something went wrong. Please try again, or contact support if the error persists.",
                            voice = ToastVoice.Error,
                        )
                    },
                )
            }
        }

        item(key = "Loading") {
            ToastSection("Loading") {
                LemonadeUi.Button(
                    label = "Loading Toast",
                    onClick = {
                        toastState.show(
                            label = "Downloading your document…",
                            voice = ToastVoice.Loading,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Complete Loading (replace)",
                    onClick = {
                        toastState.show(
                            label = "Download complete",
                            voice = ToastVoice.Success,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Dismiss Loading",
                    onClick = { toastState.dismiss() },
                )
            }
        }

        item(key = "With Action") {
            ToastSection("With Action") {
                LemonadeUi.Button(
                    label = "Success Toast with Action",
                    onClick = {
                        toastState.show(
                            label = "Changes saved",
                            voice = ToastVoice.Success,
                            actionLabel = "Undo",
                            onAction = {
                                toastState.show(
                                    label = "Change undone",
                                    voice = ToastVoice.Neutral,
                                )
                            },
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Error Toast with Action",
                    onClick = {
                        toastState.show(
                            label = "Something went wrong",
                            voice = ToastVoice.Error,
                            actionLabel = "Retry",
                            onAction = {
                                toastState.show(
                                    label = "Retrying…",
                                    voice = ToastVoice.Neutral,
                                )
                            },
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Neutral Toast with Action",
                    onClick = {
                        toastState.show(
                            label = "Added to favorites",
                            voice = ToastVoice.Neutral,
                            icon = LemonadeIcons.Heart,
                            actionLabel = "View",
                            onAction = {
                                toastState.show(
                                    label = "Opening favorites",
                                    voice = ToastVoice.Neutral,
                                )
                            },
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Long Label with Action",
                    onClick = {
                        toastState.show(
                            label = "We couldn't sync your latest changes because the connection " +
                                "dropped partway through",
                            voice = ToastVoice.Error,
                            actionLabel = "Try again",
                            onAction = {
                                toastState.show(
                                    label = "Retrying…",
                                    voice = ToastVoice.Neutral,
                                )
                            },
                        )
                    },
                )
            }
        }

        item(key = "Durations") {
            ToastSection("Durations") {
                LemonadeUi.Button(
                    label = "Short (3s)",
                    onClick = {
                        toastState.show(
                            label = "Short duration toast",
                            voice = ToastVoice.Neutral,
                            duration = ToastDuration.Short,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Medium (6s)",
                    onClick = {
                        toastState.show(
                            label = "Medium duration toast",
                            voice = ToastVoice.Neutral,
                            duration = ToastDuration.Medium,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Long (9s)",
                    onClick = {
                        toastState.show(
                            label = "Long duration toast",
                            voice = ToastVoice.Neutral,
                            duration = ToastDuration.Long,
                        )
                    },
                )
            }
        }

        item(key = "Behaviors") {
            ToastSection("Behaviors") {
                LemonadeUi.Button(
                    label = "Non-dismissible",
                    onClick = {
                        toastState.show(
                            label = "You can't swipe this away",
                            voice = ToastVoice.Neutral,
                            dismissible = false,
                            duration = ToastDuration.Short,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Tap while visible to replace",
                    onClick = {
                        toastState.show(
                            label = "New toast replaced the old one",
                            voice = ToastVoice.Success,
                        )
                    },
                )
            }
        }

        item(key = "Padding") {
            ToastSection("Padding") {
                LemonadeUi.Button(
                    label = "Bottom (default)",
                    onClick = {
                        toastState.show(
                            label = "Default bottom position",
                            voice = ToastVoice.Neutral,
                        )
                    },
                )
                LemonadeUi.Button(
                    label = "Above Bottom Action Button",
                    onClick = {
                        toastState.show(
                            label = "Above Bottom Action Button",
                            voice = ToastVoice.Success,
                            paddingValues = PaddingValues(bottom = 112.dp),
                        )
                    },
                )
            }
        }

        item(key = "Over Bottom Sheet") {
            ToastSection("Over Bottom Sheet") {
                LemonadeUi.Button(
                    label = "Open bottom sheet",
                    onClick = { showSheet = true },
                )
            }
        }
    }

    LemonadeUi.BottomSheet(
        expanded = showSheet,
        onDismissRequest = { showSheet = false },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Show a toast while this sheet is open — it appears on top of the sheet, " +
                    "not behind it.",
                textStyle = LemonadeTheme.typography.bodyMediumRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            LemonadeUi.Button(
                label = "Show toast on top",
                onClick = {
                    toastState.show(
                        label = "This toast is above the bottom sheet",
                        voice = ToastVoice.Success,
                    )
                },
            )
        }
    }
}

@Composable
private fun ToastSection(
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
