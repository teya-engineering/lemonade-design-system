package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TagVoice

/**
 * Shows a compact label that categorises or annotates content.
 *
 * The tag is static and does not react to input.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Tag(
 *     icon = LemonadeIcons.Warning,
 *     label = "WARNING",
 *     voice = TagVoice.Warning,
 * )
 * ```
 *
 * @param label text shown in the tag
 * @param modifier [Modifier] applied to the root container of the tag
 * @param icon [LemonadeIcons] shown before the label
 * @param voice [TagVoice] driving the background color and the label and icon tints
 */
@Composable
public fun LemonadeUi.Tag(
    label: String,
    modifier: Modifier = Modifier,
    icon: LemonadeIcons? = null,
    voice: TagVoice = TagVoice.Neutral,
) {
    CoreTag(
        label = label,
        icon = icon,
        voice = voice,
        modifier = modifier,
    )
}

@Composable
private fun CoreTag(
    label: String,
    icon: LemonadeIcons?,
    voice: TagVoice,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing50),
        modifier = modifier
            .background(
                color = voice.containerColor,
                shape = LocalShapes.current.radius150,
            ).border(
                width = LocalBorderWidths.current.base.border25,
                color = voice.borderColor,
                shape = LocalShapes.current.radius150,
            ).padding(
                vertical = LocalSpaces.current.spacing50,
                horizontal = LocalSpaces.current.spacing100,
            ),
    ) {
        if (icon != null) {
            LemonadeUi.Icon(
                icon = icon,
                contentDescription = null,
                tint = voice.tintColor,
                size = LemonadeAssetSize.Small,
            )
        }

        LemonadeUi.Text(
            text = label,
            color = voice.tintColor,
            overflow = TextOverflow.Ellipsis,
            textStyle = LocalTypographies.current.bodyXSmallSemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing50),
        )
    }
}

private val TagVoice.tintColor: Color
    @Composable get() {
        return when (this) {
            TagVoice.Neutral -> LocalColors.current.content.contentPrimary
            TagVoice.Critical -> LocalColors.current.content.contentCritical
            TagVoice.Warning -> LocalColors.current.content.contentCaution
            TagVoice.Info -> LocalColors.current.content.contentInfo
            TagVoice.Positive -> LocalColors.current.content.contentPositive
            TagVoice.NeutralOnColor -> LocalColors.current.content.contentAlwaysDark
            TagVoice.Featured -> LocalColors.current.content.contentFeatured
        }
    }

private val TagVoice.containerColor: Color
    @Composable get() {
        return when (this) {
            TagVoice.Neutral -> LocalColors.current.background.bgNeutralSubtle
            TagVoice.Critical -> LocalColors.current.background.bgCriticalSubtle
            TagVoice.Warning -> LocalColors.current.background.bgCautionSubtle
            TagVoice.Info -> LocalColors.current.background.bgInfoSubtle
            TagVoice.Positive -> LocalColors.current.background.bgPositiveSubtle
            TagVoice.NeutralOnColor -> LocalColors.current.background.bgAlwaysLight
            TagVoice.Featured -> LocalColors.current.background.bgFeaturedSubtle
        }
    }

private val TagVoice.borderColor: Color
    @Composable get() {
        return when (this) {
            TagVoice.Neutral -> LocalColors.current.border.borderNeutralMedium
            TagVoice.Critical -> LocalColors.current.border.borderCriticalSubtle
            TagVoice.Warning -> LocalColors.current.border.borderCautionSubtle
            TagVoice.Info -> LocalColors.current.border.borderInfoSubtle
            TagVoice.Positive -> LocalColors.current.border.borderPositiveSubtle
            TagVoice.NeutralOnColor -> LocalColors.current.border.borderNeutralLow
            TagVoice.Featured -> LocalColors.current.border.borderFeaturedSubtle
        }
    }

private data class TagPreviewData(
    val voice: TagVoice,
    val withIcon: Boolean,
)

private class TagPreviewProvider :
    PreviewParameterProvider<TagPreviewData> {
    override val values: Sequence<TagPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<TagPreviewData> =
        buildList {
            listOf(true, false)
                .forEach { withIcon ->
                    TagVoice.entries.forEach { voice ->
                        add(
                            element = TagPreviewData(
                                voice = voice,
                                withIcon = withIcon,
                            ),
                        )
                    }
                }
        }.asSequence()
}

@Composable
@LemonadePreview
private fun LemonadeTagPreview(
    @PreviewParameter(TagPreviewProvider::class)
    previewData: TagPreviewData,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
        LemonadeUi.Tag(
            label = "Small content",
            icon = LemonadeIcons.Heart.takeIf { previewData.withIcon },
            voice = previewData.voice,
        )

        LemonadeUi.Tag(
            label = "Fake long message - it had to be longer than that",
            icon = LemonadeIcons.Heart.takeIf { previewData.withIcon },
            voice = previewData.voice,
            modifier = Modifier.requiredWidth(width = 100.dp),
        )
    }
}
