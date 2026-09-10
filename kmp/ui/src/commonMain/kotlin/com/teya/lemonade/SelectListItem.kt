package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.teya.lemonade.core.CheckboxStatus
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SelectListItemType
import com.teya.lemonade.core.SelectListItemVariant

/**
 * Selects one or several items from a list.
 *
 * [type] picks both the selection behaviour and the control drawn on the trailing edge.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SelectListItem(
 *     label = "Label"
 *     supportText = "Support Text"
 *     type = SelectListItemType.Single,
 *     checked = true,
 *     onItemClicked = { /* trigger an action */ }
 *     enabled = false,
 *     showDivider = true,
 *     leadingSlot = { /* slot composable for any item */ },
 *     trailingSlot = { /* slot composable for any item */ },
 * )
 * ```
 *
 * @param label text shown in the selection item
 * @param type [SelectListItemType] setting the selection behaviour and the selection control
 * @param checked whether the item is selected
 * @param onItemClicked called when the list item is clicked
 * @param modifier optional [Modifier] applied to the base container
 * @param variant [SelectListItemVariant] controlling the visual treatment.
 *  [SelectListItemVariant.Plain] (default) delegates to the base [LemonadeUi.ListItem] row and is
 *  meant to sit inside a surrounding surface such as [LemonadeUi.Card].
 *  [SelectListItemVariant.Outlined] presents its own rounded container with a border and a
 *  brand-tinted background when selected, so items can stand alone in a stack
 * @param isLoading shows a skeleton loading placeholder instead of content; honored only by
 *  [SelectListItemVariant.Plain]
 * @param enabled whether the component responds to input; when false, click interactions and
 *  visual states are disabled
 * @param interactionSource [MutableInteractionSource] for the list item's interaction events
 * @param showDivider whether to show a divider below the list item; honored only by
 *  [SelectListItemVariant.Plain]
 * @param supportText text shown below [label]
 * @param leadingSlot slot placed at the leading edge of the list item
 * @param trailingSlot slot placed at the trailing edge of the list item
 * @param slotContent optional slot drawn below the support text, inside the label column so it
 *  stays aligned with the leading/trailing slots. Use for secondary content like an inline status
 *  text, badge, or compact widget that should sit under the row's text
 * @param labelMaxLines maximum lines for [label] before it truncates; defaults to [Int.MAX_VALUE]
 *  (no limit)
 * @param labelOverflow [TextOverflow] applied to [label] when it exceeds [labelMaxLines]; defaults
 *  to [TextOverflow.Clip]
 * @param supportTextMaxLines maximum lines for [supportText] before it truncates; defaults to
 *  [Int.MAX_VALUE] (no limit)
 * @param supportTextOverflow [TextOverflow] applied to [supportText] when it exceeds
 *  [supportTextMaxLines]; defaults to [TextOverflow.Clip]
 * @param leadingVerticalAlignment vertical alignment of [leadingSlot] within the row; defaults to
 *  [Alignment.Top], which keeps a leading icon level with the label's first line when the support
 *  text wraps. Pass [Alignment.CenterVertically] for a leading slot that should sit centred against
 *  the whole row instead — a large icon beside two lines of text, for example
 */
@Composable
public fun LemonadeUi.SelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SelectListItemVariant = SelectListItemVariant.Plain,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    supportTextMaxLines: Int = Int.MAX_VALUE,
    supportTextOverflow: TextOverflow = TextOverflow.Clip,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    SelectionHapticEffect(selected = checked)

    when (variant) {
        SelectListItemVariant.Plain -> {
            PlainSelectListItem(
                label = label,
                type = type,
                checked = checked,
                onItemClicked = onItemClicked,
                modifier = modifier,
                isLoading = isLoading,
                enabled = enabled,
                interactionSource = interactionSource,
                showDivider = showDivider,
                supportText = supportText,
                leadingSlot = leadingSlot,
                trailingSlot = trailingSlot,
                slotContent = slotContent,
                labelMaxLines = labelMaxLines,
                labelOverflow = labelOverflow,
                supportTextMaxLines = supportTextMaxLines,
                supportTextOverflow = supportTextOverflow,
                leadingVerticalAlignment = leadingVerticalAlignment,
            )
        }

        SelectListItemVariant.Outlined -> {
            OutlinedSelectListItem(
                label = label,
                type = type,
                checked = checked,
                onItemClicked = onItemClicked,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                supportText = supportText,
                leadingSlot = leadingSlot,
                trailingSlot = trailingSlot,
                slotContent = slotContent,
                labelMaxLines = labelMaxLines,
                labelOverflow = labelOverflow,
                supportTextMaxLines = supportTextMaxLines,
                supportTextOverflow = supportTextOverflow,
                leadingVerticalAlignment = leadingVerticalAlignment,
            )
        }
    }
}

@Deprecated(
    message = "Use the overload with leadingVerticalAlignment.",
    replaceWith = ReplaceWith(
        expression = "SelectListItem(label, type, checked, onItemClicked, modifier, variant, " +
            "isLoading, enabled, interactionSource, showDivider, supportText, leadingSlot, " +
            "trailingSlot, slotContent, labelMaxLines, labelOverflow, supportTextMaxLines, " +
            "supportTextOverflow, Alignment.Top)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.SelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SelectListItemVariant = SelectListItemVariant.Plain,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    supportTextMaxLines: Int = Int.MAX_VALUE,
    supportTextOverflow: TextOverflow = TextOverflow.Clip,
) {
    LemonadeUi.SelectListItem(
        label = label,
        type = type,
        checked = checked,
        onItemClicked = onItemClicked,
        modifier = modifier,
        variant = variant,
        isLoading = isLoading,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = slotContent,
        labelMaxLines = labelMaxLines,
        labelOverflow = labelOverflow,
        supportTextMaxLines = supportTextMaxLines,
        supportTextOverflow = supportTextOverflow,
        leadingVerticalAlignment = Alignment.Top,
    )
}

@Deprecated(
    message = "Use the overload with label/support-text truncation parameters.",
    replaceWith = ReplaceWith(
        expression = "SelectListItem(label, type, checked, onItemClicked, modifier, variant, " +
            "isLoading, enabled, interactionSource, showDivider, supportText, leadingSlot, " +
            "trailingSlot, slotContent, Int.MAX_VALUE, TextOverflow.Clip, Int.MAX_VALUE, " +
            "TextOverflow.Clip)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.SelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SelectListItemVariant = SelectListItemVariant.Plain,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    LemonadeUi.SelectListItem(
        label = label,
        type = type,
        checked = checked,
        onItemClicked = onItemClicked,
        modifier = modifier,
        variant = variant,
        isLoading = isLoading,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = slotContent,
        labelMaxLines = Int.MAX_VALUE,
        labelOverflow = TextOverflow.Clip,
        supportTextMaxLines = Int.MAX_VALUE,
        supportTextOverflow = TextOverflow.Clip,
    )
}

@Deprecated(
    message = "Use the overload with slotContent parameter.",
    replaceWith = ReplaceWith(
        expression = "SelectListItem(label, type, checked, onItemClicked, modifier, variant, " +
            "isLoading, enabled, interactionSource, showDivider, supportText, leadingSlot, " +
            "trailingSlot, null)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.SelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SelectListItemVariant = SelectListItemVariant.Plain,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
) {
    @Suppress("DEPRECATION")
    SelectListItem(
        label = label,
        type = type,
        checked = checked,
        onItemClicked = onItemClicked,
        modifier = modifier,
        variant = variant,
        isLoading = isLoading,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = null,
    )
}

private val SelectListItemType.role: Role
    get() =
        when (this) {
            SelectListItemType.Single -> Role.RadioButton
            SelectListItemType.Multiple -> Role.Checkbox
            SelectListItemType.Toggle -> Role.Switch
        }

private fun handleSelectClick(
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
) {
    when (type) {
        SelectListItemType.Single -> {
            if (!checked) {
                onItemClicked()
            }
        }

        SelectListItemType.Multiple,
        SelectListItemType.Toggle,
        -> {
            onItemClicked()
        }
    }
}

@Composable
private fun SelectionControl(
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
) {
    when (type) {
        SelectListItemType.Single -> {
            LemonadeUi.RadioButton(
                checked = checked,
                onRadioButtonClicked = onItemClicked,
                enabled = enabled,
                interactionSource = interactionSource,
            )
        }

        SelectListItemType.Multiple -> {
            LemonadeUi.Checkbox(
                status = if (checked) {
                    CheckboxStatus.Checked
                } else {
                    CheckboxStatus.Unchecked
                },
                onCheckboxClicked = onItemClicked,
                enabled = enabled,
                interactionSource = interactionSource,
            )
        }

        SelectListItemType.Toggle -> {
            LemonadeUi.Switch(
                checked = checked,
                onCheckedChange = { onItemClicked() },
                enabled = enabled,
                interactionSource = interactionSource,
            )
        }
    }
}

@Composable
private fun PlainSelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier,
    isLoading: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    showDivider: Boolean,
    supportText: String?,
    leadingSlot: (@Composable RowScope.() -> Unit)?,
    trailingSlot: (@Composable RowScope.() -> Unit)?,
    slotContent: (@Composable ColumnScope.() -> Unit)?,
    labelMaxLines: Int,
    labelOverflow: TextOverflow,
    supportTextMaxLines: Int,
    supportTextOverflow: TextOverflow,
    leadingVerticalAlignment: Alignment.Vertical,
) {
    LemonadeUi.ListItem(
        modifier = modifier,
        label = label,
        supportText = supportText,
        isLoading = isLoading,
        labelMaxLines = labelMaxLines,
        labelOverflow = labelOverflow,
        supportTextMaxLines = supportTextMaxLines,
        supportTextOverflow = supportTextOverflow,
        interactionSource = interactionSource,
        showDivider = showDivider,
        role = type.role,
        onListItemClick = {
            handleSelectClick(
                type = type,
                checked = checked,
                onItemClicked = onItemClicked,
            )
        },
        enabled = enabled,
        leadingVerticalAlignment = leadingVerticalAlignment,
        leadingSlot = leadingSlot,
        trailingSlot = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalSpaces.current.spacing200),
            ) {
                if (trailingSlot != null) {
                    trailingSlot()
                }

                SelectionControl(
                    type = type,
                    checked = checked,
                    onItemClicked = onItemClicked,
                    enabled = enabled,
                    interactionSource = interactionSource,
                )
            }
        },
        slotContent = slotContent,
    )
}

@Suppress("LongParameterList")
@Composable
private fun OutlinedSelectListItem(
    label: String,
    type: SelectListItemType,
    checked: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    supportText: String?,
    leadingSlot: (@Composable RowScope.() -> Unit)?,
    trailingSlot: (@Composable RowScope.() -> Unit)?,
    slotContent: (@Composable ColumnScope.() -> Unit)?,
    labelMaxLines: Int,
    labelOverflow: TextOverflow,
    supportTextMaxLines: Int,
    supportTextOverflow: TextOverflow,
    leadingVerticalAlignment: Alignment.Vertical,
) {
    val colors = LocalColors.current
    val spaces = LocalSpaces.current
    val shapes = LocalShapes.current
    val borderWidths = LocalBorderWidths.current
    val typographies = LocalTypographies.current
    val opacities = LocalOpacities.current

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            colors.background.bgBrandSubtle
        } else {
            colors.background.bgDefault
        },
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (checked) {
            colors.border.borderSelected
        } else {
            colors.border.borderNeutralLow
        },
    )
    val borderWidth = if (checked) {
        borderWidths.base.border50
    } else {
        borderWidths.base.border40
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = animatedBackgroundColor,
                shape = shapes.radius500,
            ).border(
                width = borderWidth,
                color = animatedBorderColor,
                shape = shapes.radius500,
            ).clickable(
                onClick = {
                    handleSelectClick(
                        type = type,
                        checked = checked,
                        onItemClicked = onItemClicked,
                    )
                },
                enabled = enabled,
                interactionSource = interactionSource,
                role = type.role,
            ).padding(
                start = spaces.spacing300,
                end = spaces.spacing400,
                top = spaces.spacing300,
                bottom = spaces.spacing300,
            ),
    ) {
        if (leadingSlot != null) {
            Row(
                modifier = Modifier
                    .align(alignment = leadingVerticalAlignment)
                    .modifyIf(predicate = !enabled) {
                        alpha(alpha = opacities.state.opacityDisabled)
                    }.padding(end = spaces.spacing300),
                content = leadingSlot,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(space = spaces.spacing50),
            modifier = Modifier
                .weight(weight = 1f)
                .padding(end = spaces.spacing300)
                .modifyIf(predicate = !enabled) {
                    alpha(alpha = opacities.state.opacityDisabled)
                },
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = typographies.bodyMediumMedium,
                color = colors.content.contentPrimary,
                maxLines = labelMaxLines,
                overflow = labelOverflow,
            )

            if (supportText != null) {
                LemonadeUi.Text(
                    text = supportText,
                    textStyle = typographies.bodySmallRegular,
                    color = colors.content.contentSecondary,
                    maxLines = supportTextMaxLines,
                    overflow = supportTextOverflow,
                )
            }

            if (slotContent != null) {
                slotContent()
            }
        }

        if (trailingSlot != null) {
            Row(
                modifier = Modifier.modifyIf(predicate = !enabled) {
                    alpha(alpha = opacities.state.opacityDisabled)
                },
                content = trailingSlot,
            )
        }

        Row(
            modifier = Modifier.padding(start = spaces.spacing200),
        ) {
            SelectionControl(
                type = type,
                checked = checked,
                onItemClicked = onItemClicked,
                enabled = enabled,
                interactionSource = interactionSource,
            )
        }
    }
}

private data class SelectionListItemPreviewData(
    val type: SelectListItemType,
    val supportText: Boolean,
    val enabled: Boolean,
    val leading: Boolean,
    val trailing: Boolean,
)

private class SelectionListItemPreviewProvider :
    PreviewParameterProvider<SelectionListItemPreviewData> {
    override val values: Sequence<SelectionListItemPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SelectionListItemPreviewData> =
        buildList {
            SelectListItemType.entries.forEach { type ->
                listOf(true, false)
                    .forEach { enabled ->
                        listOf(true, false)
                            .forEach { leading ->
                                listOf(true, false)
                                    .forEach { trailing ->
                                        listOf(true, false)
                                            .forEach { withSupportText ->
                                                add(
                                                    element = SelectionListItemPreviewData(
                                                        type = type,
                                                        supportText = withSupportText,
                                                        enabled = enabled,
                                                        leading = leading,
                                                        trailing = trailing,
                                                    ),
                                                )
                                            }
                                    }
                            }
                    }
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun SelectListItemPreview(
    @PreviewParameter(SelectionListItemPreviewProvider::class)
    previewData: SelectionListItemPreviewData,
) {
    LemonadeUi.SelectListItem(
        label = "Label",
        supportText = "Support Text".takeIf { previewData.supportText },
        type = previewData.type,
        checked = previewData.enabled,
        onItemClicked = { },
        leadingSlot = if (previewData.leading) {
            {
                LemonadeUi.Icon(
                    icon = LemonadeIcons.Check,
                    contentDescription = "Leading icon",
                )
            }
        } else {
            null
        },
        trailingSlot = if (previewData.trailing) {
            {
                LemonadeUi.Icon(
                    icon = LemonadeIcons.Airplane,
                    contentDescription = "Trailing icon",
                )
            }
        } else {
            null
        },
    )
}
