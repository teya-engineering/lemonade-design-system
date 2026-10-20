package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.HistoryItemVoice
import com.teya.lemonade.core.HistoryTimelineVariant
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeCardPadding
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TagVoice

@Composable
internal fun HistoryTimelineDisplay() {
    SampleScreenDisplayLazyColumn(title = "HistoryTimeline") {
        item(key = "Progress — completed steps") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                contentPadding = LemonadeCardPadding.Medium,
                header = CardHeaderConfig(title = "Progress — completed steps"),
            ) {
                LemonadeUi.HistoryTimeline(
                    items = listOf(
                        HistoryTimelineItem(label = "Invitation accepted", voice = HistoryItemVoice.Positive),
                        HistoryTimelineItem(label = "Logs into the Teya App", voice = HistoryItemVoice.Positive),
                        HistoryTimelineItem(
                            label = "Card activation",
                            subheading = "The card was delivered. Sofia can now activate it.",
                        ),
                        HistoryTimelineItem(label = "First payment"),
                    ),
                    currentIndex = 2,
                    variant = HistoryTimelineVariant.Progress,
                )
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
