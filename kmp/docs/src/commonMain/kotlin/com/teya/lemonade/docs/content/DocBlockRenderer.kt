package com.teya.lemonade.docs.content

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.teya.lemonade.HorizontalDivider
import com.teya.lemonade.Icon
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRouter
import com.teya.lemonade.textStyle

private val TableColumnWidth = 220.dp

@Composable
internal fun DocBlocks(
    blocks: List<DocBlock>,
    router: DocRouter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
    ) {
        blocks.forEach { block ->
            DocBlockContent(block = block, router = router)
        }
    }
}

@Composable
private fun DocBlockContent(
    block: DocBlock,
    router: DocRouter,
) {
    when (block) {
        is DocBlock.Heading -> DocHeading(block = block)
        is DocBlock.Paragraph -> DocParagraph(block = block, router = router)
        is DocBlock.Bullets -> DocBulletList(items = block.items, router = router, ordered = false)
        is DocBlock.Steps -> DocBulletList(items = block.items, router = router, ordered = true)
        is DocBlock.Table -> DocTable(block = block, router = router)
        is DocBlock.Code -> DocCode(block = block)
        is DocBlock.Callout -> DocCallout(block = block, router = router)
        is DocBlock.NextSteps -> DocNextSteps(block = block, router = router)
        is DocBlock.Sample -> block.content()
    }
}

@Composable
private fun DocHeading(block: DocBlock.Heading) {
    val style = headingStyle(level = block.level)
    LemonadeUi.Text(
        text = block.text,
        textStyle = style,
        color = LemonadeTheme.colors.content.contentPrimary,
        modifier = Modifier.padding(top = LemonadeTheme.spaces.spacing400),
    )
}

@Composable
private fun headingStyle(level: Int): LemonadeTextStyle =
    when (level) {
        2 -> LemonadeTheme.typography.headingMedium
        3 -> LemonadeTheme.typography.headingSmall
        else -> LemonadeTheme.typography.headingXSmall
    }

@Composable
private fun DocParagraph(
    block: DocBlock.Paragraph,
    router: DocRouter,
) {
    LemonadeUi.Text(
        text = block.spans.toAnnotatedString(router = router),
        textStyle = LemonadeTheme.typography.bodyMediumRegular,
        color = LemonadeTheme.colors.content.contentSecondary,
    )
}

@Composable
private fun DocBulletList(
    items: List<List<DocSpan>>,
    router: DocRouter,
    ordered: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        items.forEachIndexed { index, spans ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
            ) {
                LemonadeUi.Text(
                    text = if (ordered) {
                        "${index + 1}."
                    } else {
                        "•"
                    },
                    textStyle = LemonadeTheme.typography.bodyMediumRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
                LemonadeUi.Text(
                    text = spans.toAnnotatedString(router = router),
                    textStyle = LemonadeTheme.typography.bodyMediumRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
            }
        }
    }
}

@Composable
private fun DocTable(
    block: DocBlock.Table,
    router: DocRouter,
) {
    Column(
        modifier = Modifier
            .clip(LemonadeTheme.shapes.radius300)
            .background(LemonadeTheme.colors.background.bgDefault)
            .horizontalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .background(LemonadeTheme.colors.background.bgSubtle)
                .padding(
                    horizontal = LemonadeTheme.spaces.spacing300,
                    vertical = LemonadeTheme.spaces.spacing200,
                ),
        ) {
            block.headers.forEach { header ->
                LemonadeUi.Text(
                    text = header,
                    textStyle = LemonadeTheme.typography.bodySmallSemiBold,
                    color = LemonadeTheme.colors.content.contentPrimary,
                    modifier = Modifier.width(TableColumnWidth),
                )
            }
        }
        block.rows.forEachIndexed { index, cells ->
            if (index > 0) {
                LemonadeUi.HorizontalDivider()
            }
            Row(
                modifier = Modifier.padding(
                    horizontal = LemonadeTheme.spaces.spacing300,
                    vertical = LemonadeTheme.spaces.spacing300,
                ),
            ) {
                cells.forEach { cell ->
                    LemonadeUi.Text(
                        text = cell.toAnnotatedString(router = router),
                        textStyle = LemonadeTheme.typography.bodySmallRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                        modifier = Modifier.width(TableColumnWidth),
                    )
                }
            }
        }
    }
}

@Composable
private fun DocCode(block: DocBlock.Code) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LemonadeTheme.shapes.radius300)
            .background(LemonadeTheme.colors.background.bgSubtle)
            .padding(LemonadeTheme.spaces.spacing300),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = block.language.label,
            textStyle = LemonadeTheme.typography.bodyXSmallOverline,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        LemonadeUi.Text(
            text = block.source,
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            textStyle = LemonadeTheme.typography.bodySmallRegular.textStyle.copy(
                fontFamily = FontFamily.Monospace,
                color = LemonadeTheme.colors.content.contentPrimary,
            ),
        )
    }
}

@Composable
private fun DocCallout(
    block: DocBlock.Callout,
    router: DocRouter,
) {
    val colors = calloutColors(voice = block.voice)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LemonadeTheme.shapes.radius300)
            .background(colors.container)
            .padding(LemonadeTheme.spaces.spacing300),
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        verticalAlignment = Alignment.Top,
    ) {
        LemonadeUi.Icon(
            icon = calloutIcon(voice = block.voice),
            contentDescription = null,
            tint = colors.accent,
            size = LemonadeAssetSize.Small,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        ) {
            if (block.title != null) {
                LemonadeUi.Text(
                    text = block.title,
                    textStyle = LemonadeTheme.typography.bodySmallSemiBold,
                    color = colors.accent,
                )
            }
            DocBlocks(blocks = block.body, router = router)
        }
    }
}

@Composable
private fun DocNextSteps(
    block: DocBlock.NextSteps,
    router: DocRouter,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        block.routes.forEach { route ->
            DocParagraph(
                block = DocBlock.Paragraph(
                    spans = listOf(
                        DocSpan.Link(text = route.label, route = route),
                    ),
                ),
                router = router,
            )
        }
    }
}

private data class CalloutColors(
    val container: Color,
    val accent: Color,
)

@Composable
private fun calloutColors(voice: NoticeVoice): CalloutColors =
    when (voice) {
        NoticeVoice.Info -> CalloutColors(
            container = LemonadeTheme.colors.background.bgInfoSubtle,
            accent = LemonadeTheme.colors.content.contentInfo,
        )

        NoticeVoice.Positive -> CalloutColors(
            container = LemonadeTheme.colors.background.bgPositiveSubtle,
            accent = LemonadeTheme.colors.content.contentPositive,
        )

        NoticeVoice.Warning -> CalloutColors(
            container = LemonadeTheme.colors.background.bgCautionSubtle,
            accent = LemonadeTheme.colors.content.contentCaution,
        )

        NoticeVoice.Critical -> CalloutColors(
            container = LemonadeTheme.colors.background.bgCriticalSubtle,
            accent = LemonadeTheme.colors.content.contentCritical,
        )

        NoticeVoice.Neutral -> CalloutColors(
            container = LemonadeTheme.colors.background.bgElevated,
            accent = LemonadeTheme.colors.content.contentSecondary,
        )
    }

private fun calloutIcon(voice: NoticeVoice): LemonadeIcons =
    when (voice) {
        NoticeVoice.Info -> LemonadeIcons.CircleInfo
        NoticeVoice.Positive -> LemonadeIcons.CircleCheck
        NoticeVoice.Warning -> LemonadeIcons.TriangleAlert
        NoticeVoice.Critical -> LemonadeIcons.TriangleAlert
        NoticeVoice.Neutral -> LemonadeIcons.CircleInfo
    }
