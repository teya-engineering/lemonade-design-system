package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.HistoryItemVoice
import com.teya.lemonade.core.HistoryTimelineVariant
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeCardPadding
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TabButtonProperties
import com.teya.lemonade.core.TagVoice

@Composable
internal fun HistoryTimelineDisplay() {
    var variant by remember { mutableStateOf(HistoryTimelineVariant.Steps) }
    SampleScreenDisplayLazyColumn(title = "HistoryTimeline") {
        item(key = "Steps — variant") {
            LemonadeUi.SegmentedControl(
                selectedTab = HistoryTimelineVariant.entries.indexOf(variant),
                onTabSelected = { index -> variant = HistoryTimelineVariant.entries[index] },
                properties = HistoryTimelineVariant.entries.map { TabButtonProperties.label(label = it.name) },
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
            )
        }

        stepsSamples.forEach { sample ->
            item(key = sample.title) {
                LemonadeUi.Card(
                    modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                    contentPadding = LemonadeCardPadding.Medium,
                    header = CardHeaderConfig(title = sample.title),
                ) {
                    LemonadeUi.HistoryTimeline(
                        items = sample.items(),
                        currentIndex = sample.currentIndex,
                        variant = variant,
                    )
                }
            }
        }

        item(key = "Timeline — positive first") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                contentPadding = LemonadeCardPadding.Medium,
                header = CardHeaderConfig(title = "Timeline — positive first"),
            ) {
                LemonadeUi.HistoryTimeline(
                    items = listOf(
                        HistoryTimelineItem(
                            label = "Payment sent",
                            subheading = "10:24",
                            description = "Your transfer has been initiated successfully.",
                            voice = HistoryItemVoice.Positive,
                        ),
                        HistoryTimelineItem(
                            label = "Processing",
                            subheading = "10:23",
                            description = "Bank is reviewing the transaction.",
                        ),
                        HistoryTimelineItem(
                            label = "Initiated",
                            subheading = "10:22",
                        ),
                    ),
                    currentIndex = 0,
                )
            }
        }

        item(key = "How it works — with content slot") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                contentPadding = LemonadeCardPadding.Medium,
                header = CardHeaderConfig(title = "How it works — with content slot"),
            ) {
                LemonadeUi.HistoryTimeline(
                    items = listOf(
                        HistoryTimelineItem(
                            label = "Find a Visa PayPoint next to you",
                            description = "PayPoint locations collect cash deposits on our behalf.",
                            voice = HistoryItemVoice.Positive,
                            contentSlot = {
                                LemonadeUi.Button(
                                    label = "Find a PayPoint",
                                    onClick = {},
                                    leadingIcon = LemonadeIcons.MapPin,
                                    variant = LemonadeButtonVariant.Neutral,
                                    size = LemonadeButtonSize.Medium,
                                )
                            },
                        ),
                        HistoryTimelineItem(
                            label = "Enter the amount and generate a barcode",
                            description = "Show the barcode to the shopkeeper and deposit the funds.",
                        ),
                        HistoryTimelineItem(
                            label = "Your money will be available in 10 minutes",
                            description = "We'll notify you once the funds are in your account.",
                        ),
                    ),
                    currentIndex = 0,
                )
            }
        }

        item(key = "Timeline — critical current") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                contentPadding = LemonadeCardPadding.Medium,
                header = CardHeaderConfig(title = "Timeline — critical current"),
            ) {
                LemonadeUi.HistoryTimeline(
                    items = listOf(
                        HistoryTimelineItem(
                            label = "Payment failed",
                            subheading = "Just now",
                            description = "Your card was declined by the issuer.",
                            voice = HistoryItemVoice.Critical,
                            contentSlot = {
                                LemonadeUi.Tag(
                                    label = "Declined",
                                    voice = TagVoice.Critical,
                                )
                            },
                        ),
                        HistoryTimelineItem(
                            label = "Processing",
                            subheading = "09:58",
                        ),
                        HistoryTimelineItem(
                            label = "Initiated",
                            subheading = "09:57",
                        ),
                    ),
                    currentIndex = 0,
                )
            }
        }

        item(key = "Timeline — neutral current") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                contentPadding = LemonadeCardPadding.Medium,
                header = CardHeaderConfig(title = "Timeline — neutral current"),
            ) {
                LemonadeUi.HistoryTimeline(
                    items = listOf(
                        HistoryTimelineItem(
                            label = "Current item",
                            subheading = "Subheading",
                            description = "Timeline row rendered as the current step.",
                            voice = HistoryItemVoice.Neutral,
                        ),
                        HistoryTimelineItem(
                            label = "Past item",
                            subheading = "Subheading",
                            voice = HistoryItemVoice.Neutral,
                        ),
                    ),
                    currentIndex = 0,
                )
            }
        }
    }
}

private class StepsSample(
    val title: String,
    val currentIndex: Int?,
    val items: () -> List<HistoryTimelineItem>,
)

private fun steps(
    completed: Int,
    currentSubheading: String? = null,
    currentVoice: HistoryItemVoice = HistoryItemVoice.Neutral,
): List<HistoryTimelineItem> {
    val labels = listOf("Invitation accepted", "Logs into the Teya App", "Card activation")
    return labels.mapIndexed { index, label ->
        when {
            index < completed -> HistoryTimelineItem(label = label, voice = HistoryItemVoice.Positive)
            index == completed -> HistoryTimelineItem(
                label = label,
                subheading = currentSubheading,
                voice = currentVoice,
            )
            else -> HistoryTimelineItem(label = label)
        }
    }
}

private val stepsSamples = listOf(
    StepsSample(title = "Steps — first step", currentIndex = 0) {
        steps(completed = 0, currentSubheading = "An activation email was sent to sofia@teya.com")
    },
    StepsSample(title = "Steps — second step", currentIndex = 1) {
        steps(completed = 1, currentSubheading = "Sofia needs to download the Teya app and log in.")
    },
    StepsSample(title = "Steps — last step", currentIndex = 2) {
        steps(completed = 2, currentSubheading = "The card was delivered. Sofia can now activate it.")
    },
    StepsSample(title = "Steps — all steps completed", currentIndex = 3) {
        steps(completed = 3)
    },
    StepsSample(title = "Steps — critical current step", currentIndex = 1) {
        steps(
            completed = 1,
            currentSubheading = "We couldn't verify Sofia's phone number.",
            currentVoice = HistoryItemVoice.Critical,
        )
    },
    StepsSample(title = "Steps — current step with an action", currentIndex = 1) {
        steps(completed = 1).mapIndexed { index, item ->
            if (index == 1) {
                item.copy(
                    description = "Remind Sofia to log in so the card can be activated.",
                    contentSlot = {
                        LemonadeUi.Button(
                            label = "Resend invite",
                            onClick = {},
                            variant = LemonadeButtonVariant.Neutral,
                            size = LemonadeButtonSize.Medium,
                        )
                    },
                )
            } else {
                item
            }
        }
    },
    StepsSample(title = "Steps — nothing reached", currentIndex = null) {
        steps(completed = 0)
    },
)
