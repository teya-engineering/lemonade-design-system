package com.teya.lemonade

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.DividerVariant

/**
 * A horizontal divider to separate content. Optionally displays a label in the center.
 *
 * ## Usage
 * ```kotlin
 * // Simple divider
 * LemonadeUi.HorizontalDivider()
 *
 * // Divider with label
 * LemonadeUi.HorizontalDivider(label = "OR")
 *
 * // Dashed divider
 * LemonadeUi.HorizontalDivider(variant = DividerVariant.Dashed)
 * ```
 *
 * @param modifier [Modifier] applied to the divider
 * @param label optional label shown in the center of the divider
 * @param variant [DividerVariant] of the divider, defaults to [DividerVariant.Solid]
 */
@Composable
public fun LemonadeUi.HorizontalDivider(
    modifier: Modifier = Modifier,
    label: String? = null,
    variant: DividerVariant = DividerVariant.Solid,
) {
    val thickness = LocalBorderWidths.current.base.border25
    val dividerColor = when (variant) {
        DividerVariant.Solid -> LocalColors.current.border.borderNeutralLow
        DividerVariant.Dashed -> LocalColors.current.border.borderNeutralMedium
    }

    if (label != null) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoreHorizontalDivider(
                modifier = Modifier.weight(1f),
                color = dividerColor,
                variant = variant,
                thickness = thickness,
            )
            LemonadeUi.Text(
                modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing300),
                text = label,
                textStyle = LocalTypographies.current.bodySmallRegular,
                color = LocalColors.current.content.contentSecondary,
            )
            CoreHorizontalDivider(
                modifier = Modifier.weight(1f),
                color = dividerColor,
                variant = variant,
                thickness = thickness,
            )
        }
    } else {
        CoreHorizontalDivider(
            modifier = modifier.fillMaxWidth(),
            color = dividerColor,
            variant = variant,
            thickness = thickness,
        )
    }
}

@Composable
private fun CoreHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color,
    variant: DividerVariant,
    thickness: Dp,
) {
    val dashWidth = LocalSizes.current.size100
    val dashGap = LocalSpaces.current.spacing100

    Canvas(
        modifier = modifier.height(thickness),
    ) {
        val pathEffect = when (variant) {
            DividerVariant.Dashed -> PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidth.toPx(), dashGap.toPx()),
                phase = 0f,
            )
            DividerVariant.Solid -> null
        }

        drawLine(
            color = color,
            start = Offset(
                x = 0f,
                y = size.height / 2,
            ),
            end = Offset(
                x = size.width,
                y = size.height / 2,
            ),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect,
        )
    }
}

/**
 * A vertical divider to separate content.
 *
 * ## Usage
 * ```kotlin
 * // Simple vertical divider
 * LemonadeUi.VerticalDivider()
 *
 * // Dashed vertical divider
 * LemonadeUi.VerticalDivider(variant = DividerVariant.Dashed)
 * ```
 *
 * @param modifier [Modifier] applied to the divider
 * @param variant [DividerVariant] of the divider, defaults to [DividerVariant.Solid]
 */
@Composable
public fun LemonadeUi.VerticalDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Solid,
) {
    val thickness = LocalBorderWidths.current.base.border25
    val dividerColor = when (variant) {
        DividerVariant.Solid -> LocalColors.current.border.borderNeutralLow
        DividerVariant.Dashed -> LocalColors.current.border.borderNeutralMedium
    }

    CoreVerticalDivider(
        modifier = modifier.fillMaxHeight(),
        color = dividerColor,
        variant = variant,
        thickness = thickness,
    )
}

@Composable
private fun CoreVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color,
    variant: DividerVariant,
    thickness: Dp,
) {
    val dashWidth = LocalSizes.current.size100
    val dashGap = LocalSpaces.current.spacing100

    Canvas(
        modifier = modifier.width(thickness),
    ) {
        val pathEffect = when (variant) {
            DividerVariant.Dashed -> PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidth.toPx(), dashGap.toPx()),
                phase = 0f,
            )
            DividerVariant.Solid -> null
        }

        drawLine(
            color = color,
            start = Offset(
                x = size.width / 2,
                y = 0f,
            ),
            end = Offset(
                x = size.width / 2,
                y = size.height,
            ),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect,
        )
    }
}

private data class HorizontalDividerPreviewData(
    val label: String?,
    val variant: DividerVariant,
)

private class HorizontalDividerPreviewProvider :
    PreviewParameterProvider<HorizontalDividerPreviewData> {
    override val values: Sequence<HorizontalDividerPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<HorizontalDividerPreviewData> =
        buildList {
            listOf(null, "OR")
                .forEach { label ->
                    DividerVariant.entries.forEach { variant ->
                        add(
                            HorizontalDividerPreviewData(
                                label = label,
                                variant = variant,
                            ),
                        )
                    }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun HorizontalDividerPreview(
    @PreviewParameter(HorizontalDividerPreviewProvider::class)
    previewData: HorizontalDividerPreviewData,
) {
    LemonadeUi.HorizontalDivider(
        label = previewData.label,
        variant = previewData.variant,
    )
}

private data class VerticalDividerPreviewData(
    val variant: DividerVariant,
)

private class VerticalDividerPreviewProvider :
    PreviewParameterProvider<VerticalDividerPreviewData> {
    override val values: Sequence<VerticalDividerPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<VerticalDividerPreviewData> =
        DividerVariant.entries
            .map { variant ->
                VerticalDividerPreviewData(variant = variant)
            }.asSequence()
}

@LemonadePreview
@Composable
private fun VerticalDividerPreview(
    @PreviewParameter(VerticalDividerPreviewProvider::class)
    previewData: VerticalDividerPreviewData,
) {
    LemonadeUi.VerticalDivider(
        modifier = Modifier.height(48.dp),
        variant = previewData.variant,
    )
}
