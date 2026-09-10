package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeContentListItemDensity
import com.teya.lemonade.core.LemonadeContentListItemLayout
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice

/**
 * Shows a label-value pair as a display-only list item.
 *
 * Supports horizontal (label left, value right) and vertical (label top, value bottom) layouts.
 * In vertical layout, providing a [contentSlot] switches the value to a larger typography.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.ContentListItem(
 *     label = "Account holder",
 *     value = "John Doe",
 * )
 *
 * LemonadeUi.ContentListItem(
 *     label = "Balance",
 *     value = "$1,234.56",
 *     layout = LemonadeContentListItemLayout.Vertical,
 *     contentSlot = { LemonadeUi.Tag(label = "Available", voice = TagVoice.Positive) },
 * )
 * ```
 *
 * @param label label describing the data field
 * @param value value shown next to or below the label
 * @param layout [LemonadeContentListItemLayout] horizontal or vertical arrangement
 * @param modifier [Modifier] applied to the root container
 * @param showDivider whether a divider shows below the item
 * @param density [LemonadeContentListItemDensity] controlling the vertical padding, defaults to
 *  [LemonadeContentListItemDensity.Comfortable]
 * @param verticalAlignment vertical alignment used by the horizontal layout, defaults to
 *  [Alignment.CenterVertically]
 * @param leadingSlot optional slot for a leading element, for example a
 *  [LemonadeUi.SymbolContainer]
 * @param trailingSlot optional slot for a trailing element, for example an icon action
 * @param contentSlot optional slot for extra content. In vertical layout it also switches the
 *  value typography to bodyXLargeSemiBold
 * @param labelMaxLines maximum lines the [label] takes before it truncates, unlimited by default
 * @param labelOverflow [TextOverflow] applied to the [label] when it exceeds [labelMaxLines]
 * @param valueMaxLines maximum lines the [value] takes before it truncates, unlimited by default
 * @param valueOverflow [TextOverflow] applied to the [value] when it exceeds [valueMaxLines]
 */
@Composable
public fun LemonadeUi.ContentListItem(
    label: String,
    value: String,
    layout: LemonadeContentListItemLayout = LemonadeContentListItemLayout.Horizontal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    density: LemonadeContentListItemDensity = LemonadeContentListItemDensity.Comfortable,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    valueMaxLines: Int = Int.MAX_VALUE,
    valueOverflow: TextOverflow = TextOverflow.Clip,
) {
    val verticalPadding = when (density) {
        LemonadeContentListItemDensity.Comfortable -> LocalSpaces.current.spacing400
        LemonadeContentListItemDensity.Compact -> LocalSpaces.current.spacing200
    }
    val contentModifier = Modifier.padding(
        horizontal = LocalSpaces.current.spacing400,
        vertical = verticalPadding,
    )

    Column(modifier = modifier) {
        when (layout) {
            LemonadeContentListItemLayout.Horizontal -> HorizontalContentListItem(
                label = label,
                value = value,
                modifier = contentModifier,
                verticalAlignment = verticalAlignment,
                leadingSlot = leadingSlot,
                trailingSlot = trailingSlot,
                contentSlot = contentSlot,
                labelMaxLines = labelMaxLines,
                labelOverflow = labelOverflow,
                valueMaxLines = valueMaxLines,
                valueOverflow = valueOverflow,
            )

            LemonadeContentListItemLayout.Vertical -> VerticalContentListItem(
                label = label,
                value = value,
                modifier = contentModifier,
                leadingSlot = leadingSlot,
                trailingSlot = trailingSlot,
                contentSlot = contentSlot,
                labelMaxLines = labelMaxLines,
                labelOverflow = labelOverflow,
                valueMaxLines = valueMaxLines,
                valueOverflow = valueOverflow,
            )
        }

        if (showDivider) {
            LemonadeUi.HorizontalDivider(
                modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing400),
            )
        }
    }
}

@Deprecated(
    message = "Use the overload with a density parameter.",
    replaceWith = ReplaceWith(
        expression = "ContentListItem(label, value, layout, modifier, showDivider, " +
            "LemonadeContentListItemDensity.Comfortable, verticalAlignment, " +
            "leadingSlot, trailingSlot, contentSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ContentListItem(
    label: String,
    value: String,
    layout: LemonadeContentListItemLayout = LemonadeContentListItemLayout.Horizontal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
) {
    ContentListItem(
        label = label,
        value = value,
        layout = layout,
        modifier = modifier,
        showDivider = showDivider,
        density = LemonadeContentListItemDensity.Comfortable,
        verticalAlignment = verticalAlignment,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        contentSlot = contentSlot,
    )
}

@Deprecated(
    message = "Use the overload with label/value truncation parameters.",
    replaceWith = ReplaceWith(
        expression = "ContentListItem(label, value, layout, modifier, showDivider, density, " +
            "verticalAlignment, leadingSlot, trailingSlot, contentSlot, Int.MAX_VALUE, " +
            "TextOverflow.Clip, Int.MAX_VALUE, TextOverflow.Clip)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ContentListItem(
    label: String,
    value: String,
    layout: LemonadeContentListItemLayout = LemonadeContentListItemLayout.Horizontal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    density: LemonadeContentListItemDensity = LemonadeContentListItemDensity.Comfortable,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
) {
    ContentListItem(
        label = label,
        value = value,
        layout = layout,
        modifier = modifier,
        showDivider = showDivider,
        density = density,
        verticalAlignment = verticalAlignment,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        contentSlot = contentSlot,
        labelMaxLines = Int.MAX_VALUE,
        labelOverflow = TextOverflow.Clip,
        valueMaxLines = Int.MAX_VALUE,
        valueOverflow = TextOverflow.Clip,
    )
}

@Composable
private fun HorizontalContentListItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    valueMaxLines: Int = Int.MAX_VALUE,
    valueOverflow: TextOverflow = TextOverflow.Clip,
) {
    Row(
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
        modifier = modifier,
    ) {
        if (leadingSlot != null) {
            leadingSlot()
        }

        Column(
            modifier = Modifier.weight(weight = 1f),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = LocalTypographies.current.bodyMediumRegular,
                color = LocalColors.current.content.contentSecondary,
                maxLines = labelMaxLines,
                overflow = labelOverflow,
            )

            if (contentSlot != null) {
                contentSlot()
            }
        }

        if (value.isNotEmpty() || trailingSlot != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
                modifier = Modifier.weight(weight = 1f),
            ) {
                LemonadeUi.Text(
                    text = value,
                    textStyle = LocalTypographies.current.bodyMediumMedium,
                    color = LocalColors.current.content.contentPrimary,
                    textAlign = TextAlign.End,
                    maxLines = valueMaxLines,
                    overflow = valueOverflow,
                    modifier = Modifier.weight(weight = 1f),
                )

                if (trailingSlot != null) {
                    trailingSlot()
                }
            }
        }
    }
}

@Composable
private fun VerticalContentListItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    valueMaxLines: Int = Int.MAX_VALUE,
    valueOverflow: TextOverflow = TextOverflow.Clip,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
        modifier = modifier,
    ) {
        if (leadingSlot != null) {
            leadingSlot()
        }

        Column(
            modifier = Modifier.weight(weight = 1f),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = LocalTypographies.current.bodySmallRegular,
                color = LocalColors.current.content.contentSecondary,
                maxLines = labelMaxLines,
                overflow = labelOverflow,
            )

            if (value.isNotEmpty() || trailingSlot != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing100),
                ) {
                    LemonadeUi.Text(
                        text = value,
                        textStyle = if (contentSlot != null) {
                            LocalTypographies.current.bodyXLargeSemiBold
                        } else {
                            LocalTypographies.current.bodyMediumMedium
                        },
                        color = LocalColors.current.content.contentPrimary,
                        maxLines = valueMaxLines,
                        overflow = valueOverflow,
                        modifier = Modifier.weight(weight = 1f),
                    )

                    if (trailingSlot != null) {
                        trailingSlot()
                    }
                }
            }

            if (contentSlot != null) {
                contentSlot()
            }
        }
    }
}

private data class ContentListItemPreviewData(
    val layout: LemonadeContentListItemLayout,
    val density: LemonadeContentListItemDensity,
    val hasLeading: Boolean,
    val hasTrailing: Boolean,
    val hasContentSlot: Boolean,
    val showDivider: Boolean,
)

private class ContentListItemPreviewProvider :
    PreviewParameterProvider<ContentListItemPreviewData> {
    override val values: Sequence<ContentListItemPreviewData> =
        buildList {
            LemonadeContentListItemLayout.entries.forEach { layout ->
                LemonadeContentListItemDensity.entries.forEach { density ->
                    listOf(true, false)
                        .forEach { leading ->
                            listOf(true, false)
                                .forEach { trailing ->
                                    listOf(true, false)
                                        .forEach { contentSlot ->
                                            listOf(true, false)
                                                .forEach { divider ->
                                                    add(
                                                        ContentListItemPreviewData(
                                                            layout = layout,
                                                            density = density,
                                                            hasLeading = leading,
                                                            hasTrailing = trailing,
                                                            hasContentSlot = contentSlot,
                                                            showDivider = divider,
                                                        ),
                                                    )
                                                }
                                        }
                                }
                        }
                }
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun ContentListItemPreview(
    @PreviewParameter(ContentListItemPreviewProvider::class)
    previewData: ContentListItemPreviewData,
) {
    LemonadeUi.ContentListItem(
        label = "Label",
        value = "Value",
        layout = previewData.layout,
        showDivider = previewData.showDivider,
        density = previewData.density,
        leadingSlot = if (previewData.hasLeading) {
            {
                LemonadeUi.SymbolContainer(
                    icon = LemonadeIcons.Heart,
                    voice = SymbolContainerVoice.Neutral,
                    size = SymbolContainerSize.Medium,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        trailingSlot = if (previewData.hasTrailing) {
            {
                LemonadeUi.Icon(
                    icon = LemonadeIcons.PencilLine,
                    tint = LocalColors.current.content.contentBrand,
                    size = LemonadeAssetSize.Medium,
                    contentDescription = "Edit",
                )
            }
        } else {
            null
        },
        contentSlot = if (previewData.hasContentSlot) {
            {
                LemonadeUi.Text(
                    text = "Extra content",
                    textStyle = LocalTypographies.current.bodySmallRegular,
                    color = LocalColors.current.content.contentSecondary,
                )
            }
        } else {
            null
        },
    )
}
