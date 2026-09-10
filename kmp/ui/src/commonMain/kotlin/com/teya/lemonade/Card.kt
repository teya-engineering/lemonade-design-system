package com.teya.lemonade

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeCardBackground
import com.teya.lemonade.core.LemonadeCardHeadingStyle
import com.teya.lemonade.core.LemonadeCardPadding
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.TagVoice

@Composable
public fun LemonadeUi.Card(
    modifier: Modifier = Modifier,
    contentPadding: LemonadeCardPadding = LemonadeCardPadding.None,
    background: LemonadeCardBackground = LemonadeCardBackground.Default,
    header: CardHeaderConfig? = null,
    footerAction: CardFooterActionConfig? = null,
    content: (@Composable ColumnScope.() -> Unit),
) {
    CoreCard(
        modifier = modifier,
        contentPadding = contentPadding,
        background = background,
        header = header,
        footerAction = footerAction,
        content = content,
    )
}

@Composable
private fun CoreCard(
    modifier: Modifier = Modifier,
    contentPadding: LemonadeCardPadding,
    background: LemonadeCardBackground = LemonadeCardBackground.Default,
    header: CardHeaderConfig? = null,
    footerAction: CardFooterActionConfig? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = LocalShapes.current.semantic.radiusContainerDefault)
            .background(color = background.background),
    ) {
        if (header != null) {
            CardHeader(config = header)
        }

        Column(
            modifier = Modifier
                .padding(contentPadding.spacing),
        ) {
            content()
        }

        if (footerAction != null) {
            CardFooterAction(config = footerAction)
        }
    }
}

public data class CardHeaderConfig(
    val title: String,
    val headingStyle: LemonadeCardHeadingStyle = LemonadeCardHeadingStyle.Default,
    val leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    val trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    val showNavigationIndicator: Boolean = false,
    val subtitle: String? = null,
) {
    // Restores the pre-subtitle constructor symbol <init>(String, LemonadeCardHeadingStyle,
    // Function3, Function3, Z) so already-compiled consumers keep linking.
    @Deprecated(
        message = "kept for binary compatibility",
        level = DeprecationLevel.HIDDEN,
    )
    public constructor(
        title: String,
        headingStyle: LemonadeCardHeadingStyle = LemonadeCardHeadingStyle.Default,
        leadingSlot: (@Composable RowScope.() -> Unit)? = null,
        trailingSlot: (@Composable RowScope.() -> Unit)? = null,
        showNavigationIndicator: Boolean = false,
    ) : this(
        title = title,
        headingStyle = headingStyle,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        showNavigationIndicator = showNavigationIndicator,
        subtitle = null,
    )

    // Restores the pre-subtitle copy(...) and copy$default(...) symbols. The default on the
    // first parameter is what regenerates the old copy$default, keeping `copy()` callers linking.
    @Deprecated(
        message = "kept for binary compatibility",
        level = DeprecationLevel.HIDDEN,
    )
    public fun copy(
        title: String = this.title,
        headingStyle: LemonadeCardHeadingStyle = this.headingStyle,
        leadingSlot: (@Composable RowScope.() -> Unit)? = this.leadingSlot,
        trailingSlot: (@Composable RowScope.() -> Unit)? = this.trailingSlot,
        showNavigationIndicator: Boolean = this.showNavigationIndicator,
    ): CardHeaderConfig =
        copy(
            title = title,
            headingStyle = headingStyle,
            leadingSlot = leadingSlot,
            trailingSlot = trailingSlot,
            showNavigationIndicator = showNavigationIndicator,
            subtitle = this.subtitle,
        )
}

@Composable
internal fun CardHeader(
    config: CardHeaderConfig,
    modifier: Modifier = Modifier,
) {
    val titleTextStyle = config.headingStyle.textStyle
    val titleColor = config.headingStyle.color

    Row(
        horizontalArrangement = Arrangement.spacedBy(LocalSpaces.current.spacing200),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                start = LocalSpaces.current.spacing400,
                top = LocalSpaces.current.spacing300,
                end = LocalSpaces.current.spacing400,
                bottom = LocalSpaces.current.spacing0,
            ),
    ) {
        if (config.leadingSlot != null) {
            config.leadingSlot.invoke(this)
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1F)
                .defaultMinSize(minHeight = config.headingStyle.minTextBoxHeight),
        ) {
            LemonadeUi.Text(
                text = config.title,
                textStyle = titleTextStyle,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (config.subtitle != null) {
                LemonadeUi.Text(
                    text = config.subtitle,
                    textStyle = LocalTypographies.current.bodySmallRegular,
                    color = LocalColors.current.content.contentSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (config.trailingSlot != null) {
            config.trailingSlot.invoke(this)
        }

        if (config.showNavigationIndicator) {
            LemonadeUi.Icon(
                icon = LemonadeIcons.ChevronRight,
                contentDescription = null,
                size = LemonadeAssetSize.Medium,
                tint = LocalColors.current.content.contentSecondary,
            )
        }
    }
}

public data class CardFooterActionConfig(
    val label: String,
    val onClick: () -> Unit,
)

@Composable
internal fun CardFooterAction(config: CardFooterActionConfig) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f,
        label = "CardFooterActionAlpha",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha = alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = config.onClick,
            ).padding(
                start = LocalSpaces.current.spacing400,
                top = LocalSpaces.current.spacing200,
                end = LocalSpaces.current.spacing400,
                bottom = LocalSpaces.current.spacing400,
            ),
    ) {
        LemonadeUi.Text(
            text = config.label,
            textStyle = LocalTypographies.current.bodySmallSemiBold,
            color = LocalColors.current.content.contentPrimary,
        )
    }
}

private val LemonadeCardHeadingStyle.textStyle: LemonadeTextStyle
    @Composable get() {
        return when (this) {
            LemonadeCardHeadingStyle.Default -> LocalTypographies.current.headingXXSmall
            LemonadeCardHeadingStyle.Overline -> LocalTypographies.current.bodyXSmallOverline
        }
    }

// The overline's line box is shorter than the default heading's, so a header titled with only an
// overline would sit tighter than one with a default heading; the floor evens that out. The floor
// is in dp while the line box is in sp, so at large font scales the line box outgrows the floor
// and wins, leaving the text unclipped.
private val LemonadeCardHeadingStyle.minTextBoxHeight: Dp
    @Composable get() {
        return when (this) {
            LemonadeCardHeadingStyle.Default -> Dp.Unspecified
            LemonadeCardHeadingStyle.Overline -> LocalSizes.current.size500
        }
    }

private val LemonadeCardHeadingStyle.color: Color
    @Composable get() {
        return when (this) {
            LemonadeCardHeadingStyle.Default -> LocalColors.current.content.contentPrimary
            LemonadeCardHeadingStyle.Overline -> LocalColors.current.content.contentSecondary
        }
    }

internal val LemonadeCardPadding.spacing: Dp
    @Composable get() {
        return when (this) {
            LemonadeCardPadding.None -> LocalSpaces.current.spacing0
            LemonadeCardPadding.XSmall -> LocalSpaces.current.spacing100
            LemonadeCardPadding.Small -> LocalSpaces.current.spacing200
            LemonadeCardPadding.Medium -> LocalSpaces.current.spacing400
        }
    }

internal val LemonadeCardBackground.background: Color
    @Composable get() {
        return when (this) {
            LemonadeCardBackground.Default -> LocalColors.current.background.bgDefault
            LemonadeCardBackground.Subtle -> LocalColors.current.background.bgSubtle
            LemonadeCardBackground.Elevated -> LocalColors.current.background.bgElevated
        }
    }

@Suppress("DataClassShouldBeImmutable")
private data class CardPreviewData(
    val background: LemonadeCardBackground,
    val contentPadding: LemonadeCardPadding,
    var header: CardHeaderConfig?,
)

private class CardPreviewProvider : PreviewParameterProvider<CardPreviewData> {
    override val values: Sequence<CardPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<CardPreviewData> =
        buildList {
            LemonadeCardPadding.entries.forEach { contentPadding ->
                LemonadeCardBackground.entries.forEach { background ->
                    listOf(true, false)
                        .forEach { withHeader ->
                            add(
                                CardPreviewData(
                                    background = background,
                                    contentPadding = contentPadding,
                                    header = CardHeaderConfig(
                                        title = "Card heading",
                                        trailingSlot = {
                                            LemonadeUi.Tag(
                                                label = "Tag label",
                                                voice = TagVoice.Neutral,
                                            )
                                        },
                                    ).takeIf { withHeader },
                                ),
                            )
                        }
                }
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun CardHeadingSubtitlePreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalSpaces.current.spacing400),
    ) {
        LemonadeCardHeadingStyle.entries.forEach { headingStyle ->
            LemonadeUi.Card(
                header = CardHeaderConfig(
                    title = "Card heading",
                    subtitle = "Subtitle",
                    headingStyle = headingStyle,
                    trailingSlot = {
                        LemonadeUi.Tag(
                            label = "Tag label",
                            voice = TagVoice.Neutral,
                        )
                    },
                ),
                content = {},
            )
        }
    }
}

// The overline's minimum box height only changes the layout when nothing else in the header row
// is taller than it: no subtitle, no trailing slot.
@LemonadePreview
@Composable
private fun CardOverlineHeadingPreview() {
    LemonadeUi.Card(
        header = CardHeaderConfig(
            title = "Card heading",
            headingStyle = LemonadeCardHeadingStyle.Overline,
        ),
        content = {},
    )
}

@OptIn(InternalLemonadeApi::class)
@LemonadePreview
@Composable
private fun CardPreview(
    @PreviewParameter(CardPreviewProvider::class)
    previewData: CardPreviewData,
) {
    LemonadeUi.Card(
        background = previewData.background,
        header = previewData.header,
        contentPadding = previewData.contentPadding,
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalSizes.current.size1600)
                    .background(LemonadePrimitiveColors.Alpha.Pink.alpha200),
            ) {
                LemonadeUi.Text(
                    text = "Background: ${previewData.background} • Spacing: ${previewData.contentPadding}",
                    textStyle = LocalTypographies.current.bodySmallMedium,
                    color = LocalColors.current.content.contentSecondary,
                )
            }
        },
    )
}
