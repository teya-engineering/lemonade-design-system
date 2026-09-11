package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.LemonadeBadgeSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice
import lemonade.composeapp.generated.resources.Res
import lemonade.composeapp.generated.resources.netflix_logo
import org.jetbrains.compose.resources.painterResource

private val iconSizesFirstRow: List<SymbolContainerSize> = listOf(
    SymbolContainerSize.XSmall,
    SymbolContainerSize.Small,
    SymbolContainerSize.Medium,
    SymbolContainerSize.Large,
    SymbolContainerSize.XLarge,
)

private val iconSizesSecondRow: List<SymbolContainerSize> = listOf(SymbolContainerSize.XXLarge)

private data class ShapeSample(
    val label: String,
    val size: SymbolContainerSize,
    val shape: SymbolContainerShape,
)

private val shapeSamplesFirstRow: List<ShapeSample> = listOf(
    ShapeSample(
        label = "Circle",
        size = SymbolContainerSize.Medium,
        shape = SymbolContainerShape.Circle,
    ),
    ShapeSample(
        label = "Rounded",
        size = SymbolContainerSize.Medium,
        shape = SymbolContainerShape.Rounded,
    ),
)

private val shapeSamplesSecondRow: List<ShapeSample> = listOf(
    ShapeSample(
        label = "Rounded L",
        size = SymbolContainerSize.Large,
        shape = SymbolContainerShape.Rounded,
    ),
    ShapeSample(
        label = "Rounded XL",
        size = SymbolContainerSize.XLarge,
        shape = SymbolContainerShape.Rounded,
    ),
    ShapeSample(
        label = "Rounded XXL",
        size = SymbolContainerSize.XXLarge,
        shape = SymbolContainerShape.Rounded,
    ),
)

private data class VoiceSample(
    val label: String,
    val icon: LemonadeIcons,
    val voice: SymbolContainerVoice,
)

private val voiceSamplesFirstRow: List<VoiceSample> = listOf(
    VoiceSample(
        label = "Neutral",
        icon = LemonadeIcons.Heart,
        voice = SymbolContainerVoice.Neutral,
    ),
    VoiceSample(
        label = "Critical",
        icon = LemonadeIcons.CircleX,
        voice = SymbolContainerVoice.Critical,
    ),
    VoiceSample(
        label = "Warning",
        icon = LemonadeIcons.TriangleAlert,
        voice = SymbolContainerVoice.Warning,
    ),
    VoiceSample(
        label = "Info",
        icon = LemonadeIcons.CircleInfo,
        voice = SymbolContainerVoice.Info,
    ),
)

private val voiceSamplesSecondRow: List<VoiceSample> = listOf(
    VoiceSample(
        label = "Positive",
        icon = LemonadeIcons.CircleCheck,
        voice = SymbolContainerVoice.Positive,
    ),
    VoiceSample(
        label = "Brand",
        icon = LemonadeIcons.Star,
        voice = SymbolContainerVoice.Brand,
    ),
    VoiceSample(
        label = "Brand Subtle",
        icon = LemonadeIcons.Star,
        voice = SymbolContainerVoice.BrandSubtle,
    ),
)

private data class TextSample(
    val text: String,
    val voice: SymbolContainerVoice,
    val size: SymbolContainerSize,
)

private val textSamples: List<TextSample> = listOf(
    TextSample(
        text = "A",
        voice = SymbolContainerVoice.Neutral,
        size = SymbolContainerSize.Small,
    ),
    TextSample(
        text = "B",
        voice = SymbolContainerVoice.Info,
        size = SymbolContainerSize.Medium,
    ),
    TextSample(
        text = "C",
        voice = SymbolContainerVoice.Positive,
        size = SymbolContainerSize.Large,
    ),
    TextSample(
        text = "1",
        voice = SymbolContainerVoice.Critical,
        size = SymbolContainerSize.Medium,
    ),
    TextSample(
        text = "99",
        voice = SymbolContainerVoice.Warning,
        size = SymbolContainerSize.Large,
    ),
)

private data class BadgeSample(
    val size: SymbolContainerSize,
    val badgeSize: LemonadeBadgeSize?,
)

private val badgeSamples: List<BadgeSample> = listOf(
    BadgeSample(
        size = SymbolContainerSize.Small,
        badgeSize = LemonadeBadgeSize.XSmall,
    ),
    BadgeSample(
        size = SymbolContainerSize.Medium,
        badgeSize = null,
    ),
    BadgeSample(
        size = SymbolContainerSize.Large,
        badgeSize = null,
    ),
)

private data class StatusSample(
    val label: String,
    val icon: LemonadeIcons,
    val voice: SymbolContainerVoice,
)

private val statusSamples: List<StatusSample> = listOf(
    StatusSample(
        label = "Completed",
        icon = LemonadeIcons.CircleCheck,
        voice = SymbolContainerVoice.Positive,
    ),
    StatusSample(
        label = "Pending",
        icon = LemonadeIcons.Clock,
        voice = SymbolContainerVoice.Warning,
    ),
    StatusSample(
        label = "Failed",
        icon = LemonadeIcons.CircleX,
        voice = SymbolContainerVoice.Critical,
    ),
)

private val painterOutlineSizes: List<SymbolContainerSize> = listOf(
    SymbolContainerSize.Small,
    SymbolContainerSize.Medium,
    SymbolContainerSize.Large,
    SymbolContainerSize.XLarge,
    SymbolContainerSize.XXLarge,
)

private val painterFilledSamples: List<ShapeSample> = listOf(
    ShapeSample(
        label = "Circle",
        size = SymbolContainerSize.Small,
        shape = SymbolContainerShape.Circle,
    ),
    ShapeSample(
        label = "Rounded",
        size = SymbolContainerSize.Medium,
        shape = SymbolContainerShape.Rounded,
    ),
    ShapeSample(
        label = "Large",
        size = SymbolContainerSize.Large,
        shape = SymbolContainerShape.Circle,
    ),
)

@Composable
internal fun SymbolContainerDisplay() {
    SampleScreenDisplayLazyColumn(title = "SymbolContainer") {
        item(key = "Sizes (Icon)") {
            SymbolContainerSection(title = "Sizes (Icon)") {
                SymbolRow {
                    iconSizesFirstRow.forEach { size ->
                        LabelledSymbol(label = size.name) {
                            LemonadeUi.SymbolContainer(
                                icon = LemonadeIcons.Heart,
                                contentDescription = null,
                                size = size,
                            )
                        }
                    }
                }
                SymbolRow {
                    iconSizesSecondRow.forEach { size ->
                        LabelledSymbol(label = size.name) {
                            LemonadeUi.SymbolContainer(
                                icon = LemonadeIcons.Heart,
                                contentDescription = null,
                                size = size,
                            )
                        }
                    }
                }
            }
        }

        item(key = "Shapes") {
            SymbolContainerSection(title = "Shapes") {
                SymbolRow {
                    shapeSamplesFirstRow.forEach { sample ->
                        ShapeSymbol(sample = sample)
                    }
                }
                SymbolRow {
                    shapeSamplesSecondRow.forEach { sample ->
                        ShapeSymbol(sample = sample)
                    }
                }
            }
        }

        item(key = "Voices") {
            SymbolContainerSection(title = "Voices") {
                SymbolRow {
                    voiceSamplesFirstRow.forEach { sample ->
                        VoiceSymbol(sample = sample)
                    }
                }
                SymbolRow {
                    voiceSamplesSecondRow.forEach { sample ->
                        VoiceSymbol(sample = sample)
                    }
                }
            }
        }

        item(key = "Themes (Solid)") {
            SymbolContainerSection(title = "Themes (Solid)") {
                ThemedSymbols(subtle = false)
            }
        }

        item(key = "Themes (Subtle)") {
            SymbolContainerSection(title = "Themes (Subtle)") {
                ThemedSymbols(subtle = true)
            }
        }

        item(key = "Text Variant") {
            SymbolContainerSection(title = "Text Variant") {
                SymbolRow {
                    textSamples.forEach { sample ->
                        LemonadeUi.SymbolContainer(
                            text = sample.text,
                            voice = sample.voice,
                            size = sample.size,
                        )
                    }
                }
            }
        }

        item(key = "With Badge") {
            SymbolContainerSection(title = "With Badge") {
                SymbolRow(spacing = LemonadeTheme.spaces.spacing600) {
                    badgeSamples.forEach { sample ->
                        LabelledSymbol(label = sample.size.name) {
                            LemonadeUi.SymbolContainer(
                                icon = LemonadeIcons.Heart,
                                contentDescription = null,
                                size = sample.size,
                                badgeSlot = {
                                    if (sample.badgeSize != null) {
                                        LemonadeUi.Badge(
                                            text = "3",
                                            size = sample.badgeSize,
                                        )
                                    } else {
                                        LemonadeUi.Badge(text = "3")
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        item(key = "Use Cases") {
            SymbolContainerSection(title = "Use Cases") {
                UserAvatarSample()

                SymbolRow(spacing = LemonadeTheme.spaces.spacing600) {
                    statusSamples.forEach { sample ->
                        LabelledSymbol(label = sample.label) {
                            LemonadeUi.SymbolContainer(
                                icon = sample.icon,
                                contentDescription = null,
                                voice = sample.voice,
                                size = SymbolContainerSize.Large,
                            )
                        }
                    }
                }
            }
        }

        item(key = "Painter (fill = false)") {
            SymbolContainerSection(title = "Painter (fill = false)") {
                val netflixLogo = painterResource(Res.drawable.netflix_logo)
                SymbolRow {
                    painterOutlineSizes.forEach { size ->
                        LabelledSymbol(label = size.name) {
                            LemonadeUi.SymbolContainer(
                                painter = netflixLogo,
                                contentDescription = "Netflix logo",
                                fill = false,
                                size = size,
                            )
                        }
                    }
                }
            }
        }

        item(key = "Painter (fill = true)") {
            SymbolContainerSection(title = "Painter (fill = true)") {
                val netflixLogo = painterResource(Res.drawable.netflix_logo)
                SymbolRow {
                    painterFilledSamples.forEach { sample ->
                        LabelledSymbol(label = sample.label) {
                            LemonadeUi.SymbolContainer(
                                painter = netflixLogo,
                                contentDescription = "Netflix logo",
                                fill = true,
                                size = sample.size,
                                shape = sample.shape,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapeSymbol(sample: ShapeSample) {
    LabelledSymbol(label = sample.label) {
        LemonadeUi.SymbolContainer(
            icon = LemonadeIcons.Heart,
            contentDescription = null,
            size = sample.size,
            shape = sample.shape,
        )
    }
}

@Composable
private fun VoiceSymbol(sample: VoiceSample) {
    LabelledSymbol(label = sample.label) {
        LemonadeUi.SymbolContainer(
            icon = sample.icon,
            contentDescription = null,
            voice = sample.voice,
            size = SymbolContainerSize.Medium,
        )
    }
}

@OptIn(ExperimentalLemonadeApi::class)
@Composable
private fun ThemedSymbols(subtle: Boolean) {
    val rows = ThemedHue.entries.chunked(size = 5)
    val style = { hue: ThemedHue -> if (subtle) hue.subtle else hue }
    rows.forEach { hues ->
        SymbolRow {
            hues.forEach { hue ->
                LemonadeUi.SymbolContainer(
                    icon = LemonadeIcons.Heart,
                    contentDescription = hue.name,
                    theme = style(hue),
                )
            }
        }
    }
    rows.forEach { hues ->
        SymbolRow {
            hues.forEach { hue ->
                LemonadeUi.SymbolContainer(
                    text = hue.abbreviation(),
                    theme = style(hue),
                    shape = SymbolContainerShape.Rounded,
                )
            }
        }
    }
}

@OptIn(ExperimentalLemonadeApi::class)
private fun ThemedHue.abbreviation(): String {
    val initials = name.filter { char -> char.isUpperCase() }
    return if (initials.length > 1) initials else name.take(n = 2)
}

@Composable
private fun UserAvatarSample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = LemonadeTheme.radius.radius300))
            .background(color = LemonadeTheme.colors.background.bgElevated)
            .padding(all = LemonadeTheme.spaces.spacing400),
        horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.SymbolContainer(
            text = "JD",
            voice = SymbolContainerVoice.Brand,
            size = SymbolContainerSize.Large,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing100),
        ) {
            LemonadeUi.Text(
                text = "John Doe",
                textStyle = LemonadeTheme.typography.headingXSmall,
            )
            LemonadeUi.Text(
                text = "john@example.com",
                textStyle = LemonadeTheme.typography.bodySmallRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f))
    }
}

@Composable
private fun LabelledSymbol(
    label: String,
    symbol: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
    ) {
        symbol()
        LemonadeUi.Text(
            text = label,
            textStyle = LemonadeTheme.typography.bodySmallRegular,
        )
    }
}

@Composable
private fun SymbolRow(
    spacing: Dp = LemonadeTheme.spaces.spacing400,
    content: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = spacing),
    ) {
        content()
    }
}

@Composable
private fun SymbolContainerSection(
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
