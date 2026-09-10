package com.teya.lemonade

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import com.teya.lemonade.core.LemonadeBadgeSize

/**
 * Highlights new or unread items, or shows a status or category.
 *
 * Small rounded indicator that draws attention to an element, usually showing a count, a status
 * or a category.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Badge(
 *     text = "New",
 *     size = LemonadeBadgeSize.Small
 * )
 * ```
 *
 * @param text text shown inside the badge
 * @param modifier optional [Modifier] for styling and layout
 * @param size badge size, defaults to [LemonadeBadgeSize.Small]
 */
@Composable
public fun LemonadeUi.Badge(
    text: String,
    modifier: Modifier = Modifier,
    size: LemonadeBadgeSize = LemonadeBadgeSize.Small,
) {
    CoreBadge(
        text = text,
        modifier = modifier,
        badgeSize = size,
    )
}

@Composable
private fun CoreBadge(
    text: String,
    modifier: Modifier,
    badgeSize: LemonadeBadgeSize,
) {
    val props = badgeSize.badgeProps
    val brandColor = LocalColors.current.background.bgBrand
    val overlayColor = LocalColors.current.background.bgDefault
    Row(
        modifier = modifier
            .height(props.height)
            .clip(LocalShapes.current.radiusFull)
            .drawWithCache {
                // A corner-to-corner gradient approximates the angled highlight sweep on this
                // short, wide shape without size-dependent angle math.
                val overlay = Brush.linearGradient(
                    0f to overlayColor,
                    1f to overlayColor.copy(alpha = 0f),
                    start = Offset.Zero,
                    end = Offset(
                        x = size.width,
                        y = size.height,
                    ),
                )
                onDrawBehind {
                    drawRect(color = brandColor)
                    drawRect(
                        brush = overlay,
                        blendMode = BlendMode.Overlay,
                    )
                }
            }.padding(horizontal = props.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.Text(
            modifier = Modifier.padding(
                horizontal = props.textHorizontalPadding,
                vertical = props.textVerticalPadding,
            ),
            text = text,
            textStyle = props.textStyle,
        )
    }
}

private data class BadgeProps(
    val height: Dp,
    val horizontalPadding: Dp,
    val textStyle: TextStyle,
    val textVerticalPadding: Dp,
    val textHorizontalPadding: Dp,
)

private val LemonadeBadgeSize.badgeProps: BadgeProps
    @Composable get() {
        return when (this) {
            LemonadeBadgeSize.XSmall -> BadgeProps(
                height = 16.dp,
                horizontalPadding = LocalSpaces.current.spacing50,
                textVerticalPadding = 1.dp,
                textHorizontalPadding = LocalSpaces.current.spacing50,
                textStyle = LocalTypographies.current.bodyXSmallSemiBold.textStyle.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = LocalColors.current.content.contentOnBrandHigh,
                ),
            )

            LemonadeBadgeSize.Small -> BadgeProps(
                height = 20.dp,
                horizontalPadding = LocalSpaces.current.spacing100,
                textVerticalPadding = 2.dp,
                textHorizontalPadding = LocalSpaces.current.spacing50,
                textStyle = LocalTypographies.current.bodyXSmallSemiBold.textStyle.copy(
                    color = LocalColors.current.content.contentOnBrandHigh,
                ),
            )
        }
    }

@Composable
internal fun LemonadeBadgeBox(
    badge: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
    badgeOffset: (IntSize) -> DpOffset,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier.layoutId("anchor"),
                contentAlignment = Alignment.Center,
                content = content,
            )
            Box(
                modifier = Modifier.layoutId("badge"),
                content = badge,
            )
        },
        measurePolicy = { measurables, constraints ->
            val badgePlaceable = measurables
                .fastFirst { measurable -> measurable.layoutId == "badge" }
                .measure(constraints = constraints.copy(minHeight = 0))

            val anchorPlaceable = measurables
                .fastFirst { measurable -> measurable.layoutId == "anchor" }
                .measure(constraints = constraints)

            val firstBaseline = anchorPlaceable[FirstBaseline]
            val lastBaseline = anchorPlaceable[LastBaseline]
            val totalWidth = anchorPlaceable.width
            val totalHeight = anchorPlaceable.height

            layout(
                width = totalWidth,
                height = totalHeight,
                alignmentLines = mapOf(
                    FirstBaseline to firstBaseline,
                    LastBaseline to lastBaseline,
                ),
            ) {
                val badgeOffset = badgeOffset(
                    IntSize(
                        width = badgePlaceable.width,
                        height = badgePlaceable.height,
                    ),
                )
                anchorPlaceable.placeRelative(
                    x = 0,
                    y = 0,
                )
                badgePlaceable.placeRelative(
                    x = anchorPlaceable.width - badgePlaceable.width + badgeOffset.x.roundToPx(),
                    y = -badgeOffset.y.roundToPx(),
                )
            }
        },
    )
}

@LemonadePreview
@Composable
internal fun PreviewBadge() {
    Column {
        LemonadeUi.Badge(
            text = "Label",
            size = LemonadeBadgeSize.Small,
        )
    }
}
