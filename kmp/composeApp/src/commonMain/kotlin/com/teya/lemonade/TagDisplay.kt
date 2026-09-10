package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TagVoice

@Composable
internal fun TagDisplay() {
    SampleScreenDisplayLazyColumn(title = "Tag") {
        item(key = "Voices") {
            TagSection(title = "Voices") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Tag(
                            label = "Neutral",
                            voice = TagVoice.Neutral,
                        )
                        LemonadeUi.Tag(
                            label = "Critical",
                            voice = TagVoice.Critical,
                        )
                        LemonadeUi.Tag(
                            label = "Warning",
                            voice = TagVoice.Warning,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Tag(
                            label = "Info",
                            voice = TagVoice.Info,
                        )
                        LemonadeUi.Tag(
                            label = "Positive",
                            voice = TagVoice.Positive,
                        )
                        LemonadeUi.Tag(
                            label = "Featured",
                            voice = TagVoice.Featured,
                        )
                    }

                    OnColorContainer {
                        LemonadeUi.Tag(
                            label = "Neutral On Color",
                            voice = TagVoice.NeutralOnColor,
                        )
                    }
                }
            }
        }

        item(key = "With Icons") {
            TagSection(title = "With Icons") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Tag(
                        label = "Neutral",
                        icon = LemonadeIcons.Heart,
                        voice = TagVoice.Neutral,
                    )
                    LemonadeUi.Tag(
                        label = "Error",
                        icon = LemonadeIcons.CircleX,
                        voice = TagVoice.Critical,
                    )
                    LemonadeUi.Tag(
                        label = "Warning",
                        icon = LemonadeIcons.TriangleAlert,
                        voice = TagVoice.Warning,
                    )
                    LemonadeUi.Tag(
                        label = "Info",
                        icon = LemonadeIcons.CircleInfo,
                        voice = TagVoice.Info,
                    )
                    LemonadeUi.Tag(
                        label = "Success",
                        icon = LemonadeIcons.CircleCheck,
                        voice = TagVoice.Positive,
                    )
                    LemonadeUi.Tag(
                        label = "Featured",
                        icon = LemonadeIcons.Sparkles,
                        voice = TagVoice.Featured,
                    )
                    OnColorContainer {
                        LemonadeUi.Tag(
                            label = "Neutral On Color",
                            icon = LemonadeIcons.Heart,
                            voice = TagVoice.NeutralOnColor,
                        )
                    }
                }
            }
        }

        item(key = "Use Cases") {
            TagSection(title = "Use Cases") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    StatusTagRows()
                    CategoryTags()
                }
            }
        }

        item(key = "In Context") {
            TagSection(title = "In Context") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(LemonadeTheme.radius.radius300))
                        .background(LemonadeTheme.colors.background.bgElevated)
                        .padding(LemonadeTheme.spaces.spacing400),
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(LemonadeTheme.radius.radius200))
                            .background(LemonadeTheme.colors.background.bgSubtle),
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing100),
                        modifier = Modifier.weight(1f),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LemonadeUi.Text(
                                text = "Product Name",
                                textStyle = LemonadeTheme.typography.headingXSmall,
                            )
                            LemonadeUi.Tag(
                                label = "New",
                                voice = TagVoice.Positive,
                            )
                        }

                        LemonadeUi.Text(
                            text = "$99.99",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing100),
                        ) {
                            LemonadeUi.Tag(
                                label = "In Stock",
                                voice = TagVoice.Info,
                            )
                            LemonadeUi.Tag(
                                label = "Free Shipping",
                                voice = TagVoice.Neutral,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusTagRows() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.Text(
            text = "Order Status:",
            textStyle = LemonadeTheme.typography.bodyMediumRegular,
        )
        LemonadeUi.Tag(
            label = "Shipped",
            icon = LemonadeIcons.Check,
            voice = TagVoice.Positive,
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.Text(
            text = "Payment:",
            textStyle = LemonadeTheme.typography.bodyMediumRegular,
        )
        LemonadeUi.Tag(
            label = "Pending",
            voice = TagVoice.Warning,
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.Text(
            text = "Account:",
            textStyle = LemonadeTheme.typography.bodyMediumRegular,
        )
        LemonadeUi.Tag(
            label = "Verified",
            icon = LemonadeIcons.CircleCheck,
            voice = TagVoice.Info,
        )
    }
}

@Composable
private fun CategoryTags() {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = "Categories:",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        ) {
            LemonadeUi.Tag(
                label = "Electronics",
                voice = TagVoice.Neutral,
            )
            LemonadeUi.Tag(
                label = "Sale",
                voice = TagVoice.Critical,
            )
            LemonadeUi.Tag(
                label = "New",
                voice = TagVoice.Positive,
            )
        }
    }
}

@Composable
private fun OnColorContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(LemonadeTheme.spaces.spacing200)
            .background(
                color = LemonadeTheme.colors.background.bgAlwaysDark,
                shape = LocalShapes.current.radius150,
            ),
    ) {
        content()
    }
}

@Composable
private fun TagSection(
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
