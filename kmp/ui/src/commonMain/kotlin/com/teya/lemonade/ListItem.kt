@file:Suppress("TooManyFunctions")

package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeListItemPriority
import com.teya.lemonade.core.LemonadeListItemVoice
import com.teya.lemonade.core.LemonadeSkeletonSize
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice
import com.teya.lemonade.core.TagVoice

/**
 * A list item for resource info display.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.ResourceListItem(
 *     label = "Label"
 *     value = "Value",
 *     supportText = "Support Text"
 *     onItemClicked = { /* trigger an action */ }
 *     enabled = true,
 *     showDivider = true,
 *     leadingSlot = { /* slot composable for any item */ },
 *     addonSlot = { /* slot composable for any item */ },
 * )
 * ```
 * @param leadingSlot - slot component to be placed in the leading position of the list item.
 * @param label - main [String] to be displayed.
 * @param value - value [String] to be displayed in the trailing position.
 * @param modifier - [Modifier] to be applied to the base container of component.
 * @param addonSlot - slot to be displayed below the [value] parameter.
 * @param interactionSource - [MutableInteractionSource] of the component.
 * @param onItemClicked - callback called when component is tapped.
 * @param isLoading - shows a skeleton loading placeholder instead of content.
 * @param enabled - flag to define if the component is enabled or not. If disabled, click interactions
 *  and visual states are disabled.
 * @param supportText - [String] to be displayed as support text.
 * @param showDivider - flag to show a divider below the list item.
 */
@Composable
public fun LemonadeUi.ResourceListItem(
    leadingSlot: @Composable BoxScope.() -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    addonSlot: (@Composable ColumnScope.() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onItemClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    supportText: String? = null,
    showDivider: Boolean = false,
) {
    LemonadeUi.ListItem(
        label = label,
        supportText = supportText,
        isLoading = isLoading,
        leadingSlot = {
            Box(
                content = leadingSlot,
                contentAlignment = Alignment.Center,
                modifier = if (enabled) {
                    Modifier
                } else {
                    Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                },
            )
        },
        trailingSlot = {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing50),
                horizontalAlignment = Alignment.End,
                modifier = if (enabled) {
                    Modifier
                } else {
                    Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                },
            ) {
                LemonadeUi.Text(
                    text = value,
                    textStyle = LocalTypographies.current.bodyMediumMedium,
                    textAlign = TextAlign.Left,
                )

                if (addonSlot != null) {
                    addonSlot()
                }
            }
        },
        onListItemClick = onItemClicked,
        role = null,
        enabled = enabled,
        modifier = modifier,
        showDivider = showDivider,
        interactionSource = interactionSource,
        // Keep the value top-aligned with the label's first line: the label can wrap (no maxLines cap),
        // and the outer Row already centers a single-line row, so this handles both without extra logic.
        trailingVerticalAlignment = Alignment.Top,
    )
}

@Deprecated(
    message = "Use the overload with leadingVerticalAlignment parameter.",
    replaceWith = ReplaceWith(
        expression = "ActionListItem(label, modifier, topLabel, supportText, leadingSlot, " +
            "trailingSlot, voice, isLoading, enabled, onItemClicked, role, interactionSource, " +
            "showNavigationIndicator, showDivider, trailingVerticalAlignment, Alignment.Top)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ActionListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onItemClicked: (() -> Unit)? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showNavigationIndicator: Boolean = false,
    showDivider: Boolean = false,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    @Suppress("DEPRECATION")
    ActionListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        voice = voice,
        isLoading = isLoading,
        enabled = enabled,
        onItemClicked = onItemClicked,
        role = role,
        interactionSource = interactionSource,
        showNavigationIndicator = showNavigationIndicator,
        showDivider = showDivider,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = Alignment.Top,
    )
}

@Deprecated(
    message = "Use the overload with label/support-text truncation parameters.",
    replaceWith = ReplaceWith(
        expression = "ActionListItem(label, modifier, topLabel, supportText, leadingSlot, " +
            "trailingSlot, voice, isLoading, enabled, onItemClicked, role, interactionSource, " +
            "showNavigationIndicator, showDivider, trailingVerticalAlignment, " +
            "leadingVerticalAlignment, slotContent, Int.MAX_VALUE, TextOverflow.Clip, " +
            "Int.MAX_VALUE, TextOverflow.Clip)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ActionListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onItemClicked: (() -> Unit)? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showNavigationIndicator: Boolean = false,
    showDivider: Boolean = false,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    LemonadeUi.ActionListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        voice = voice,
        isLoading = isLoading,
        enabled = enabled,
        onItemClicked = onItemClicked,
        role = role,
        interactionSource = interactionSource,
        showNavigationIndicator = showNavigationIndicator,
        showDivider = showDivider,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = leadingVerticalAlignment,
        slotContent = slotContent,
        labelMaxLines = Int.MAX_VALUE,
        labelOverflow = TextOverflow.Clip,
        supportTextMaxLines = Int.MAX_VALUE,
        supportTextOverflow = TextOverflow.Clip,
    )
}

/**
 * Basic building block for list items.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.ActionListItem(
 *     label = "Label"
 *     supportText = "Support Text"
 *     onItemClicked = { /* trigger an action */ }
 *     enabled = false,
 *     showDivider = true,
 *     leadingSlot = { /* slot composable for any item */ },
 *     trailingSlot = { /* slot composable for any item */ },
 * )
 * ```
 * @param label - label [String] to be displayed in the list item.
 * @param modifier - [Modifier] to be applied to the base container of component.
 * @param topLabel - Optional label [String] displayed above the [label].
 * @param supportText - text [String] to be displayed as Support Text.
 * @param leadingSlot - slot content to be placed in the leading position of the component.
 * @param trailingSlot - slot content to be placed in the trailing position of the component.
 * @param voice - [LemonadeListItemVoice] to define the tone of voice. This will effectively
 *  define color of the background while it's hovered or pressed, alongside the content's
 *  tints. Defaults to [LemonadeListItemVoice.Neutral].
 * @param showNavigationIndicator - [Boolean] indicates navigation visually.
 * @param enabled - [Boolean] flag to define if the component is enabled or not. If disabled, click interactions
 *  and visual states are disabled.
 * @param onItemClicked - callback called when component is tapped.
 * @param role - [Role] interaction semantics.
 * @param interactionSource - [MutableInteractionSource] to be had within the component.
 * @param showDivider - [Boolean] flag to show a divider below the list item.
 * @param trailingVerticalAlignment - Vertical alignment of the trailing slot and navigation
 *  indicator against the label/supportText column. Defaults to [Alignment.CenterVertically].
 * @param leadingVerticalAlignment - Vertical alignment of the leading slot against the
 *  label/supportText column. Defaults to [Alignment.CenterVertically] for single-line content
 *  (no [topLabel] or [supportText]) and [Alignment.Top] otherwise.
 * @param slotContent - Optional slot rendered below the support text, inside the label column
 *  so it stays aligned with the leading/trailing slots. Use for secondary content like an
 *  inline status text, badge, or compact widget that should sit under the row's text.
 * @param labelMaxLines - Maximum number of lines for the [label] before it truncates. Defaults to
 *  [Int.MAX_VALUE] (no limit).
 * @param labelOverflow - [TextOverflow] strategy applied to the [label] when it exceeds
 *  [labelMaxLines]. Defaults to [TextOverflow.Clip].
 * @param supportTextMaxLines - Maximum number of lines for the [supportText] before it truncates.
 *  Defaults to [Int.MAX_VALUE] (no limit).
 * @param supportTextOverflow - [TextOverflow] strategy applied to the [supportText] when it exceeds
 *  [supportTextMaxLines]. Defaults to [TextOverflow.Clip].
 */
@Composable
public fun LemonadeUi.ActionListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onItemClicked: (() -> Unit)? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showNavigationIndicator: Boolean = false,
    showDivider: Boolean = false,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    // slotContent is declared below and can't be referenced in this default, so an ActionListItem with
    // only slotContent centers its leading slot (a minor divergence from the slotContent-aware ListItem).
    leadingVerticalAlignment: Alignment.Vertical =
        singleLineLeadingAlignment(topLabel, supportText),
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    supportTextMaxLines: Int = Int.MAX_VALUE,
    supportTextOverflow: TextOverflow = TextOverflow.Clip,
) {
    LemonadeUi.ListItem(
        label = label,
        topLabel = topLabel,
        supportText = supportText,
        isLoading = isLoading,
        labelMaxLines = labelMaxLines,
        labelOverflow = labelOverflow,
        supportTextMaxLines = supportTextMaxLines,
        supportTextOverflow = supportTextOverflow,
        leadingSlot = leadingSlot,
        trailingSlot = if (trailingSlot != null) {
            {
                Row(
                    modifier = Modifier.then(
                        other = if (enabled) {
                            Modifier
                        } else {
                            Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                        },
                    ),
                ) {
                    trailingSlot()
                }
            }
        } else {
            null
        },
        slotContent = slotContent,
        navigationIndicator = showNavigationIndicator,
        voice = voice,
        onListItemClick = onItemClicked,
        role = role,
        enabled = enabled,
        modifier = modifier,
        showDivider = showDivider,
        interactionSource = interactionSource,
        leadingVerticalAlignment = leadingVerticalAlignment,
        trailingVerticalAlignment = trailingVerticalAlignment,
    )
}

@Deprecated(
    message = "Use the overload with slotContent parameter.",
    replaceWith = ReplaceWith(
        expression = "ActionListItem(label, modifier, topLabel, supportText, leadingSlot, " +
            "trailingSlot, voice, isLoading, enabled, onItemClicked, role, interactionSource, " +
            "showNavigationIndicator, showDivider, trailingVerticalAlignment, " +
            "leadingVerticalAlignment, null)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ActionListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onItemClicked: (() -> Unit)? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showNavigationIndicator: Boolean = false,
    showDivider: Boolean = false,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    @Suppress("DEPRECATION")
    ActionListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        voice = voice,
        isLoading = isLoading,
        enabled = enabled,
        onItemClicked = onItemClicked,
        role = role,
        interactionSource = interactionSource,
        showNavigationIndicator = showNavigationIndicator,
        showDivider = showDivider,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = leadingVerticalAlignment,
        slotContent = null,
    )
}

@Deprecated(
    message = "Use the overload with leadingVerticalAlignment parameter.",
    replaceWith = ReplaceWith(
        expression = "ListItem(label, modifier, topLabel, supportText, onListItemClick, voice, " +
            "navigationIndicator, isLoading, role, enabled, interactionSource, showDivider, " +
            "leadingSlot, trailingSlot, slotContent, trailingVerticalAlignment, Alignment.Top)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    isLoading: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    @Suppress("DEPRECATION")
    ListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        onListItemClick = onListItemClick,
        voice = voice,
        navigationIndicator = navigationIndicator,
        isLoading = isLoading,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = slotContent,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = Alignment.Top,
    )
}

@Deprecated(
    message = "Use the overload with label/support-text truncation parameters.",
    replaceWith = ReplaceWith(
        expression = "ListItem(label, modifier, topLabel, supportText, onListItemClick, voice, " +
            "navigationIndicator, isLoading, role, enabled, interactionSource, showDivider, " +
            "leadingSlot, trailingSlot, slotContent, trailingVerticalAlignment, " +
            "leadingVerticalAlignment, Int.MAX_VALUE, TextOverflow.Clip, Int.MAX_VALUE, " +
            "TextOverflow.Clip)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    isLoading: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    LemonadeUi.ListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        onListItemClick = onListItemClick,
        voice = voice,
        navigationIndicator = navigationIndicator,
        isLoading = isLoading,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = slotContent,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = leadingVerticalAlignment,
        labelMaxLines = Int.MAX_VALUE,
        labelOverflow = TextOverflow.Clip,
        supportTextMaxLines = Int.MAX_VALUE,
        supportTextOverflow = TextOverflow.Clip,
    )
}

@Deprecated(
    message = "Use the overload with priority parameter.",
    replaceWith = ReplaceWith(
        expression = "ListItem(label, modifier, topLabel, supportText, onListItemClick, voice, " +
            "navigationIndicator, isLoading, role, enabled, interactionSource, showDivider, " +
            "leadingSlot, trailingSlot, slotContent, trailingVerticalAlignment, " +
            "leadingVerticalAlignment, labelMaxLines, labelOverflow, supportTextMaxLines, " +
            "supportTextOverflow, LemonadeListItemPriority.Trailing)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    isLoading: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    supportTextMaxLines: Int = Int.MAX_VALUE,
    supportTextOverflow: TextOverflow = TextOverflow.Clip,
) {
    ListItem(
        label = label,
        modifier = modifier,
        topLabel = topLabel,
        supportText = supportText,
        onListItemClick = onListItemClick,
        voice = voice,
        navigationIndicator = navigationIndicator,
        isLoading = isLoading,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        slotContent = slotContent,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = leadingVerticalAlignment,
        labelMaxLines = labelMaxLines,
        labelOverflow = labelOverflow,
        supportTextMaxLines = supportTextMaxLines,
        supportTextOverflow = supportTextOverflow,
        priority = LemonadeListItemPriority.Trailing,
    )
}

/**
 * Convenience overload that composes standard label and support-text content from string parameters
 * and delegates to the content-slot variant of [ListItem].
 *
 * @param label - Label [String] to be displayed in the list item.
 * @param topLabel - Optional label [String] displayed above the [label].
 * @param supportText - Optional support text [String] displayed below the [label].
 * @param leadingSlot - A slot to be placed in the leading position of the list item.
 * @param trailingSlot - A slot to be placed in the trailing position of the list item.
 * @param voice - [LemonadeListItemVoice] that defines the visual voice of the list item.
 * @param navigationIndicator - Shows a chevron-right navigation indicator.
 * @param onListItemClick - Optional callback triggered on click interaction with the list item.
 * @param role - Optional semantic [Role] applied to the list item for accessibility.
 * @param enabled - Flag that defines if the component is enabled or not. If disabled, click
 *  interactions and visual states are disabled.
 * @param modifier - [Modifier] to be applied to the base container of the component.
 * @param showDivider - Flag to show a divider below the list item.
 * @param interactionSource - [MutableInteractionSource] for interaction events.
 * @param slotContent - Optional slot content below the label and support text.
 * @param trailingVerticalAlignment - Vertical alignment of the trailing slot and navigation
 *  indicator against the label/supportText column. Defaults to [Alignment.CenterVertically].
 * @param leadingVerticalAlignment - Vertical alignment of the leading slot against the
 *  label/supportText column. Defaults to [Alignment.CenterVertically] for single-line content
 *  (no [topLabel], [supportText], or [slotContent]) and [Alignment.Top] otherwise.
 * @param labelMaxLines - Maximum number of lines for the [label] before it truncates. Defaults to
 *  [Int.MAX_VALUE] (no limit).
 * @param labelOverflow - [TextOverflow] strategy applied to the [label] when it exceeds
 *  [labelMaxLines]. Defaults to [TextOverflow.Clip].
 * @param supportTextMaxLines - Maximum number of lines for the [supportText] before it truncates.
 *  Defaults to [Int.MAX_VALUE] (no limit).
 * @param supportTextOverflow - [TextOverflow] strategy applied to the [supportText] when it exceeds
 *  [supportTextMaxLines]. Defaults to [TextOverflow.Clip].
 * @param priority - [LemonadeListItemPriority] deciding which slot claims layout space first when
 *  the label and trailing slots compete for width. Use [LemonadeListItemPriority.Both] to split the
 *  width evenly. Defaults to [LemonadeListItemPriority.Trailing].
 */
@Composable
public fun LemonadeUi.ListItem(
    label: String,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    supportText: String? = null,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    isLoading: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical =
        singleLineLeadingAlignment(topLabel, supportText, slotContent),
    labelMaxLines: Int = Int.MAX_VALUE,
    labelOverflow: TextOverflow = TextOverflow.Clip,
    supportTextMaxLines: Int = Int.MAX_VALUE,
    supportTextOverflow: TextOverflow = TextOverflow.Clip,
    priority: LemonadeListItemPriority = LemonadeListItemPriority.Trailing,
) {
    if (isLoading) {
        ListItemSkeleton(
            modifier = modifier,
            showDivider = showDivider,
        )
    } else {
        LemonadeUi.ListItem(
            leadingSlot = leadingSlot,
            trailingSlot = trailingSlot,
            voice = voice,
            navigationIndicator = navigationIndicator,
            onListItemClick = onListItemClick,
            role = role,
            enabled = enabled,
            modifier = modifier,
            showDivider = showDivider,
            interactionSource = interactionSource,
            leadingVerticalAlignment = leadingVerticalAlignment,
            trailingVerticalAlignment = trailingVerticalAlignment,
            priority = priority,
            contentSlot = {
                if (topLabel != null) {
                    LemonadeUi.Text(
                        text = topLabel,
                        textStyle = LocalTypographies.current.bodySmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                    )
                }

                LemonadeUi.Text(
                    text = label,
                    textStyle = LocalTypographies.current.bodyMediumMedium,
                    color = voice.contentColor,
                    maxLines = labelMaxLines,
                    overflow = labelOverflow,
                )

                if (supportText != null) {
                    LemonadeUi.Text(
                        text = supportText,
                        textStyle = LocalTypographies.current.bodySmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                        maxLines = supportTextMaxLines,
                        overflow = supportTextOverflow,
                    )
                }

                if (slotContent != null) {
                    slotContent()
                }
            },
        )
    }
}

@Deprecated(
    message = "Use the overload with leadingVerticalAlignment parameter.",
    replaceWith = ReplaceWith(
        expression = "ListItem(contentSlot, modifier, onListItemClick, voice, navigationIndicator, " +
            "role, enabled, interactionSource, showDivider, leadingSlot, trailingSlot, " +
            "trailingVerticalAlignment, Alignment.Top)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ListItem(
    contentSlot: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    @Suppress("DEPRECATION")
    ListItem(
        contentSlot = contentSlot,
        modifier = modifier,
        onListItemClick = onListItemClick,
        voice = voice,
        navigationIndicator = navigationIndicator,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = Alignment.Top,
    )
}

@Deprecated(
    message = "Use the overload with priority parameter.",
    replaceWith = ReplaceWith(
        expression = "ListItem(contentSlot, modifier, onListItemClick, voice, navigationIndicator, " +
            "role, enabled, interactionSource, showDivider, leadingSlot, trailingSlot, " +
            "trailingVerticalAlignment, leadingVerticalAlignment, LemonadeListItemPriority.Trailing)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.ListItem(
    contentSlot: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    ListItem(
        contentSlot = contentSlot,
        modifier = modifier,
        onListItemClick = onListItemClick,
        voice = voice,
        navigationIndicator = navigationIndicator,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        showDivider = showDivider,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        trailingVerticalAlignment = trailingVerticalAlignment,
        leadingVerticalAlignment = leadingVerticalAlignment,
        priority = LemonadeListItemPriority.Trailing,
    )
}

/**
 * Foundational list-item overload that accepts a generic content slot for custom content,
 * delegating layout and interaction handling to [CoreListItem].
 *
 * @param contentSlot - Composable content slot for the main body of the list item.
 * @param leadingSlot - A slot to be placed in the leading position of the list item.
 * @param trailingSlot - A slot to be placed in the trailing position of the list item.
 * @param voice - [LemonadeListItemVoice] that defines the visual voice of the list item.
 * @param navigationIndicator - Shows a chevron-right navigation indicator.
 * @param onListItemClick - Optional callback triggered on click interaction with the list item.
 * @param role - Optional semantic [Role] applied to the list item for accessibility.
 * @param enabled - Flag that defines if the component is enabled or not. If disabled, click
 *  interactions and visual states are disabled.
 * @param modifier - [Modifier] to be applied to the base container of the component.
 * @param showDivider - Flag to show a divider below the list item.
 * @param interactionSource - [MutableInteractionSource] for interaction events.
 * @param trailingVerticalAlignment - Vertical alignment of the trailing slot and navigation
 *  indicator against the content slot. Defaults to [Alignment.CenterVertically].
 * @param leadingVerticalAlignment - Vertical alignment of the leading slot against the
 *  content slot. Defaults to [Alignment.Top].
 * @param priority - [LemonadeListItemPriority] deciding which slot claims layout space first when
 *  the content and trailing slots compete for width. Use [LemonadeListItemPriority.Both] to split the
 *  width evenly. Defaults to [LemonadeListItemPriority.Trailing].
 */
@Composable
public fun LemonadeUi.ListItem(
    contentSlot: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    onListItemClick: (() -> Unit)? = null,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    role: Role? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showDivider: Boolean = false,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
    priority: LemonadeListItemPriority = LemonadeListItemPriority.Trailing,
) {
    CoreListItem(
        contentSlot = contentSlot,
        leadingSlot = leadingSlot,
        trailingSlot = trailingSlot,
        voice = voice,
        navigationIndicator = navigationIndicator,
        onListItemClick = onListItemClick,
        role = role,
        enabled = enabled,
        modifier = modifier,
        showDivider = showDivider,
        interactionSource = interactionSource,
        leadingVerticalAlignment = leadingVerticalAlignment,
        trailingVerticalAlignment = trailingVerticalAlignment,
        priority = priority,
    )
}

/**
 * Vertical alignment for a list item's leading slot: centered against single-line content (nothing
 * stacked below the label), top-aligned otherwise so it lines up with the label's first line.
 */
private fun singleLineLeadingAlignment(
    topLabel: String?,
    supportText: String?,
    slotContent: (@Composable ColumnScope.() -> Unit)? = null,
): Alignment.Vertical =
    if (topLabel == null && supportText == null && slotContent == null) {
        Alignment.CenterVertically
    } else {
        Alignment.Top
    }

@Composable
private fun CoreListItem(
    contentSlot: @Composable ColumnScope.() -> Unit,
    leadingSlot: (@Composable RowScope.() -> Unit)?,
    trailingSlot: (@Composable RowScope.() -> Unit)?,
    voice: LemonadeListItemVoice = LemonadeListItemVoice.Neutral,
    navigationIndicator: Boolean = false,
    onListItemClick: (() -> Unit)?,
    role: Role?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    showDivider: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    trailingVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingVerticalAlignment: Alignment.Vertical = Alignment.Top,
    priority: LemonadeListItemPriority = LemonadeListItemPriority.Trailing,
) {
    SafeArea(modifier = modifier, showDivider = showDivider) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(
                    other = if (onListItemClick != null) {
                        Modifier.interactiveBackground(
                            interactionSource = interactionSource,
                            voice = voice,
                            enabled = enabled,
                            role = role,
                            onClick = onListItemClick,
                        )
                    } else {
                        Modifier
                    },
                ).defaultMinSize(minHeight = LocalSizes.current.size1200)
                .padding(
                    horizontal = LocalSpaces.current.spacing300,
                    vertical = LocalSpaces.current.spacing300,
                ),
        ) {
            if (leadingSlot != null) {
                Row(
                    modifier = Modifier
                        .align(leadingVerticalAlignment)
                        .padding(end = LocalSpaces.current.spacing300)
                        .padding(vertical = LocalSpaces.current.spacing50)
                        .then(
                            other = if (!enabled) {
                                Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    leadingSlot()
                }
            }

            val contentAlpha: Modifier = if (!enabled) {
                Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
            } else {
                Modifier
            }

            val hasTrailingContent = trailingSlot != null || navigationIndicator
            val slotFloor = LocalSizes.current.size2000
            val gap = LocalSpaces.current.spacing300

            when (priority) {
                LemonadeListItemPriority.Label -> {
                    // Content keeps its width; the trailing slot yields and truncates. The content is
                    // capped so the trailing slot always retains a readable floor instead of vanishing —
                    // but only when there is trailing content to keep visible.
                    BoxWithConstraints(modifier = Modifier.weight(weight = 1f)) {
                        val contentMaxWidth = if (hasTrailingContent) {
                            (maxWidth - slotFloor - gap).coerceAtLeast(0.dp)
                        } else {
                            maxWidth
                        }

                        Row(verticalAlignment = trailingVerticalAlignment) {
                            Column(
                                content = contentSlot,
                                modifier = Modifier
                                    .widthIn(max = contentMaxWidth)
                                    .then(other = contentAlpha),
                            )

                            if (hasTrailingContent) {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(weight = 1f)
                                        .padding(start = gap),
                                ) {
                                    ListItemTrailingContent(
                                        trailingSlot = trailingSlot,
                                        navigationIndicator = navigationIndicator,
                                        enabled = enabled,
                                    )
                                }
                            }
                        }
                    }
                }

                LemonadeListItemPriority.Trailing -> {
                    // Default: the trailing slot keeps its width; the content column yields and
                    // truncates. The trailing slot is capped so the content always retains a readable
                    // floor instead of collapsing to a bare ellipsis.
                    BoxWithConstraints(modifier = Modifier.weight(weight = 1f)) {
                        val trailingMaxWidth = (maxWidth - slotFloor).coerceAtLeast(0.dp)

                        Row(verticalAlignment = trailingVerticalAlignment) {
                            Column(
                                content = contentSlot,
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .then(other = contentAlpha),
                            )

                            if (hasTrailingContent) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.widthIn(max = trailingMaxWidth),
                                ) {
                                    ListItemTrailingContent(
                                        trailingSlot = trailingSlot,
                                        navigationIndicator = navigationIndicator,
                                        enabled = enabled,
                                    )
                                }
                            }
                        }
                    }
                }

                LemonadeListItemPriority.Both -> {
                    // Neither slot is prioritized: split the available width evenly so the content
                    // and trailing slots each occupy half and truncate together.
                    Row(
                        modifier = Modifier.weight(weight = 1f),
                        verticalAlignment = trailingVerticalAlignment,
                    ) {
                        Column(
                            content = contentSlot,
                            modifier = Modifier
                                .weight(weight = 1f)
                                .then(other = contentAlpha),
                        )

                        if (hasTrailingContent) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .padding(start = gap),
                            ) {
                                ListItemTrailingContent(
                                    trailingSlot = trailingSlot,
                                    navigationIndicator = navigationIndicator,
                                    enabled = enabled,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ListItemTrailingContent(
    trailingSlot: (@Composable RowScope.() -> Unit)?,
    navigationIndicator: Boolean,
    enabled: Boolean,
) {
    if (trailingSlot != null) {
        trailingSlot()
    }

    if (navigationIndicator) {
        LemonadeUi.Icon(
            icon = LemonadeIcons.ChevronRight,
            tint = LocalColors.current.content.contentTertiary,
            size = LemonadeAssetSize.Medium,
            contentDescription = null,
            modifier = Modifier
                .then(
                    other = if (!enabled) {
                        Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                    } else {
                        Modifier
                    },
                ).padding(start = LocalSpaces.current.spacing100),
        )
    }
}

/**
 * Applies click handling and the animated press/hover highlight for interactive rows.
 *
 * Only used for clickable rows: a row without an `onListItemClick` can never be pressed or
 * hovered, so it needs none of this apparatus and is left untouched.
 *
 * The highlight is painted directly in the draw phase via [drawWithCache], reading the animated
 * colour at draw time (not composition time) and using the row's rounded [androidx.compose.ui.graphics.Shape]
 * outline instead of a [androidx.compose.ui.draw.clip] graphics layer. During a scroll the colour is fully
 * transparent, so the row draws nothing and pays no clip/background cost — this removes the
 * per-row draw floor seen in the scroll-jank profiling. The click indication is `null` (no
 * ripple); press feedback is the animated fill, mirroring the iOS `ListItemButtonStyle`.
 */
@Composable
private fun Modifier.interactiveBackground(
    interactionSource: MutableInteractionSource,
    voice: LemonadeListItemVoice,
    enabled: Boolean,
    role: Role?,
    onClick: () -> Unit,
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovering by interactionSource.collectIsHoveredAsState()

    val highlightShape = LocalShapes.current.radius500
    val transparentColor = voice.interactionBackground.copy(
        alpha = LocalOpacities.current.base.opacity0,
    )
    val highlightColor by animateColorAsState(
        targetValue = if (isHovering || isPressed) {
            voice.interactionBackground
        } else {
            transparentColor
        },
    )

    return this
        .clickable(
            enabled = enabled,
            role = role,
            onClick = onClick,
            interactionSource = interactionSource,
            indication = null,
        ).drawWithCache {
            val outline = highlightShape.createOutline(
                size = size,
                layoutDirection = layoutDirection,
                density = this,
            )
            onDrawBehind {
                if (highlightColor.alpha > 0f) {
                    drawOutline(outline = outline, color = highlightColor)
                }
            }
        }
}

@Composable
private fun SafeArea(
    modifier: Modifier = Modifier,
    showDivider: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!showDivider) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.padding(all = LocalSpaces.current.spacing100),
            content = content,
        )
        return
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(all = LocalSpaces.current.spacing100),
            content = content,
        )

        LemonadeUi.HorizontalDivider(
            modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing400),
        )
    }
}

private val LemonadeListItemVoice.interactionBackground: Color
    @Composable get() {
        return when (this) {
            LemonadeListItemVoice.Neutral -> LocalColors.current.interaction.bgSubtleInteractive
            LemonadeListItemVoice.Critical -> LocalColors.current.interaction.bgCriticalSubtleInteractive
        }
    }

private val LemonadeListItemVoice.contentColor: Color
    @Composable get() {
        return when (this) {
            LemonadeListItemVoice.Neutral -> LocalColors.current.content.contentPrimary
            LemonadeListItemVoice.Critical -> LocalColors.current.content.contentCritical
        }
    }

@Composable
private fun ListItemSkeleton(
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
) {
    SafeArea(modifier = modifier, showDivider = showDivider) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = LocalSpaces.current.spacing300,
                    vertical = LocalSpaces.current.spacing300,
                ),
        ) {
            LemonadeUi.CircleSkeleton(
                size = LemonadeSkeletonSize.XLarge,
                modifier = Modifier.padding(end = LocalSpaces.current.spacing300),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(weight = 1f),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing100),
                    modifier = Modifier.weight(weight = 1f),
                ) {
                    LemonadeUi.LineSkeleton(
                        size = LemonadeSkeletonSize.Medium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    LemonadeUi.LineSkeleton(
                        size = LemonadeSkeletonSize.Small,
                        modifier = Modifier.fillMaxWidth(fraction = 0.6f),
                    )
                }

                LemonadeUi.LineSkeleton(
                    size = LemonadeSkeletonSize.Medium,
                    modifier = Modifier
                        .padding(start = LocalSpaces.current.spacing300)
                        .width(width = 54.dp),
                )
            }
        }
    }
}

private data class ResourceListItemPreviewData(
    val withAddonSlot: Boolean,
    val enabled: Boolean,
    val supportText: Boolean,
)

private class ResourceListItemPreviewProvider :
    PreviewParameterProvider<ResourceListItemPreviewData> {
    override val values: Sequence<ResourceListItemPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<ResourceListItemPreviewData> =
        buildList {
            listOf(true, false).forEach { addonSlot ->
                listOf(true, false).forEach { enabled ->
                    listOf(true, false).forEach { withSupportText ->
                        add(
                            ResourceListItemPreviewData(
                                withAddonSlot = addonSlot,
                                enabled = enabled,
                                supportText = withSupportText,
                            ),
                        )
                    }
                }
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun ResourceListItemPreview(
    @PreviewParameter(ResourceListItemPreviewProvider::class)
    previewData: ResourceListItemPreviewData,
) {
    LemonadeUi.ResourceListItem(
        label = "Label",
        showDivider = true,
        supportText = "Metadata 1 * Metadata 2\nSupport text".takeIf { previewData.supportText },
        value = "Value",
        enabled = previewData.enabled,
        addonSlot = if (previewData.withAddonSlot) {
            {
                LemonadeUi.Tag(
                    label = "Approved",
                    voice = TagVoice.Positive,
                )
            }
        } else {
            null
        },
        leadingSlot = {
            LemonadeUi.SymbolContainer(
                icon = LemonadeIcons.Heart,
                voice = SymbolContainerVoice.Neutral,
                size = SymbolContainerSize.Large,
                contentDescription = null,
            )
        },
    )
}

private data class ActionListItemPreviewData(
    val voice: Boolean,
    val enabled: Boolean,
    val topLabel: Boolean,
    val supportText: Boolean,
    val trailingSlot: Boolean,
    val showNavigationIndicator: Boolean,
)

private class ActionListItemPreviewProvider :
    PreviewParameterProvider<ActionListItemPreviewData> {
    override val values: Sequence<ActionListItemPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<ActionListItemPreviewData> =
        buildList {
            listOf(true, false).forEach { voice ->
                listOf(true, false).forEach { enabled ->
                    listOf(true, false).forEach { topLabel ->
                        listOf(true, false).forEach { withSupportText ->
                            listOf(true, false).forEach { trailingSlot ->
                                listOf(true, false).forEach { showNavigationIndicator ->
                                    add(
                                        ActionListItemPreviewData(
                                            voice = voice,
                                            enabled = enabled,
                                            topLabel = topLabel,
                                            supportText = withSupportText,
                                            trailingSlot = trailingSlot,
                                            showNavigationIndicator = showNavigationIndicator,
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
private fun ActionListItemPreview(
    @PreviewParameter(ActionListItemPreviewProvider::class)
    previewData: ActionListItemPreviewData,
) {
    LemonadeUi.ActionListItem(
        label = "Label",
        showDivider = true,
        supportText = "Support Text".takeIf { previewData.supportText },
        topLabel = "Top Label Text".takeIf { previewData.topLabel },
        enabled = previewData.enabled,
        voice = if (previewData.voice) LemonadeListItemVoice.Critical else LemonadeListItemVoice.Neutral,
        showNavigationIndicator = previewData.showNavigationIndicator,
        trailingSlot = if (previewData.trailingSlot) {
            {
                LemonadeUi.Tag(
                    label = "New",
                    voice = TagVoice.Warning,
                )
            }
        } else {
            null
        },
        leadingSlot = {
            LemonadeUi.Icon(
                icon = LemonadeIcons.Heart,
                size = LemonadeAssetSize.Medium,
                contentDescription = null,
            )
        },
    )
}
