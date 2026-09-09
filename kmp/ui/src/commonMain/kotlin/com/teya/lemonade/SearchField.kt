package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIconButtonShape
import com.teya.lemonade.core.LemonadeIcons

private const val SEARCH_FIELD_ANIMATION_MILLIS = 150

private val SearchFieldFadeSpec = tween<Float>(
    durationMillis = SEARCH_FIELD_ANIMATION_MILLIS,
    easing = EaseInOut,
)

// Without an explicit spec `expandHorizontally` and `shrinkHorizontally` fall back to their default
// spring, and the width then drifts out of step with the opacity and scale.
private val SearchFieldSizeSpec = tween<IntSize>(
    durationMillis = SEARCH_FIELD_ANIMATION_MILLIS,
    easing = EaseInOut,
)

// The cancel button pops in from — and collapses back to — slightly under its full size, so the
// entrance reads as the button growing into the gap the field gives up rather than blinking in.
private const val CANCEL_BUTTON_COLLAPSED_SCALE = 0.8f

// The field takes the row's spare width but is not forced to fill it, so on a wide layout it stops
// stretching into a full-bleed bar with the cancel button marooned at the far edge. The floor keeps
// it from collapsing onto its placeholder when the content is short.
private val SEARCH_FIELD_MIN_WIDTH = 240.dp

/**
 * Text input for search and querying.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SearchField(
 *    input = inputtedContent,
 *    onInputChanged = { inputtedContent = it },
 *    enabled = enabled,
 * )
 * ```
 *
 * @param input current text shown in the field
 * @param onInputChanged called when the input changes
 * @param placeholder optional text shown while [input] is empty
 * @param onInputClear called when the user asks to clear the input
 * @param dismissible whether to show the trailing cancel button, on by default. The button shows
 * while the field is focused, and tapping it hides the keyboard and clears the focus, leaving the
 * input untouched. Turn it off for hosts that already provide their own dismissal affordance
 * @param onCancel called after the search is dismissed through the cancel button. The input is left
 * as typed — clearing it stays with the inner clear icon and [onInputClear] — so use this to react
 * to the dismissal itself, such as collapsing a results overlay
 * @param cancelContentDescription optional accessibility label for the cancel button. The component
 * leaves it unset by default so the label can be localised by the consumer; supply one whenever the
 * field is [dismissible]
 * @param interactionSource [MutableInteractionSource] applied to the component
 * @param keyboardActions [KeyboardActions] applied to the component
 * @param keyboardOptions [KeyboardOptions] applied to the component
 * @param enabled whether the component accepts input; disabling also dims it
 * @param modifier optional [Modifier] applied to the component
 */
@Suppress("LongParameterList")
@Composable
@ExperimentalLemonadeComponent
public fun LemonadeUi.SearchField(
    input: String,
    onInputChanged: (String) -> Unit,
    placeholder: String? = null,
    onInputClear: () -> Unit = { onInputChanged("") },
    dismissible: Boolean = true,
    onCancel: () -> Unit = {},
    cancelContentDescription: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val dismissRequester = remember { FocusRequester() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        SearchFocusDecoy(
            focusRequester = dismissRequester,
            claimFocusOnEntry = false,
        )

        CoreSearchField(
            input = input,
            onInputChanged = onInputChanged,
            placeholder = placeholder,
            onInputClear = onInputClear,
            interactionSource = interactionSource,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            modifier = Modifier
                .defaultMinSize(minWidth = SEARCH_FIELD_MIN_WIDTH)
                .weight(
                    weight = 1f,
                    fill = false,
                ).clearFocusOnKeyboardDismiss { dismissRequester.requestFocus() },
        )

        if (dismissible) {
            SearchCancelButton(
                onDismiss = {
                    keyboardController?.hide()
                    dismissRequester.requestFocus()
                },
                onCancel = onCancel,
                contentDescription = cancelContentDescription,
                interactionSource = interactionSource,
                enabled = enabled,
            )
        }
    }
}

// Split out so the focus subscription lives in a leaf recomposition scope: reading it here means a
// focus toggle recomposes the button alone, not the whole [SearchField] body. Skipped entirely when
// the field is not dismissible, so opting out costs no collector and no transition.
@Composable
private fun SearchCancelButton(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    contentDescription: String?,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Dismissal only drops the focus, so the button only earns its space while there is focus to
    // drop.
    AnimatedVisibility(
        visible = enabled && isFocused,
        enter = fadeIn(animationSpec = SearchFieldFadeSpec) +
            scaleIn(
                animationSpec = SearchFieldFadeSpec,
                initialScale = CANCEL_BUTTON_COLLAPSED_SCALE,
            ) + expandHorizontally(animationSpec = SearchFieldSizeSpec),
        exit = fadeOut(animationSpec = SearchFieldFadeSpec) +
            scaleOut(
                animationSpec = SearchFieldFadeSpec,
                targetScale = CANCEL_BUTTON_COLLAPSED_SCALE,
            ) + shrinkHorizontally(animationSpec = SearchFieldSizeSpec),
    ) {
        LemonadeUi.IconButton(
            icon = LemonadeIcons.Times,
            contentDescription = contentDescription,
            onClick = {
                onDismiss()
                onCancel()
            },
            variant = LemonadeButtonVariant.Neutral,
            type = LemonadeButtonType.Solid,
            size = LemonadeButtonSize.Small,
            shape = LemonadeIconButtonShape.Circular,
            // The gap lives inside the animated content so that it collapses together with the
            // button instead of leaving a permanent trailing gutter next to the field.
            modifier = Modifier.padding(start = LocalSpaces.current.spacing200),
        )
    }
}

@Composable
internal fun CoreSearchField(
    input: String,
    onInputChanged: (String) -> Unit,
    leadingIcon: LemonadeIcons = LemonadeIcons.Search,
    onLeadingIconClicked: (() -> Unit)? = null,
    placeholder: String? = null,
    onInputClear: () -> Unit = { onInputChanged("") },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    BasicTextField(
        value = input,
        onValueChange = onInputChanged,
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(contentColor),
        textStyle = LocalTypographies.current.bodyMediumRegular.textStyle.copy(
            color = contentColor,
        ),
        singleLine = true,
        modifier = modifier.then(
            other = if (enabled) {
                Modifier
            } else {
                Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
            },
        ),
        decorationBox = { innerTextField ->
            CoreSearchFieldDecorationBox(
                innerTextField = innerTextField,
                interactionSource = interactionSource,
                placeholder = placeholder,
                input = input,
                enabled = enabled,
                onInputClear = onInputClear,
                leadingIcon = leadingIcon,
                onLeadingIconClicked = onLeadingIconClicked,
            )
        },
    )
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun CoreSearchFieldDecorationBox(
    input: String,
    placeholder: String?,
    enabled: Boolean,
    onInputClear: () -> Unit,
    leadingIcon: LemonadeIcons,
    onLeadingIconClicked: (() -> Unit)?,
    interactionSource: MutableInteractionSource,
    innerTextField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchFieldShape = LocalShapes.current.radiusFull
    val isFocused by interactionSource.collectIsFocusedAsState()

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            LocalColors.current.background.bgDefault
        } else {
            LocalColors.current.background.bgElevated
        },
    )
    val animatedSelectionColor by animateColorAsState(
        targetValue = if (isFocused) {
            LocalColors.current.border.borderSelected
        } else {
            LocalColors.current.border.borderSelected.copy(
                alpha = LocalOpacities.current.base.opacity0,
            )
        },
    )

    val animatedFocusedShadowColor by animateColorAsState(
        targetValue = if (isFocused) {
            LocalColors.current.background.bgElevatedHigh
        } else {
            LocalColors.current.background.bgElevatedHigh.copy(
                alpha = LocalOpacities.current.base.opacity0,
            )
        },
    )

    val animatedFocusedShadowSpread by animateDpAsState(
        targetValue = if (isFocused) {
            LocalBorderWidths.current.base.border50
        } else {
            Dp.Hairline
        },
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        modifier = modifier
            .shadowBorder(
                width = animatedFocusedShadowSpread,
                shape = searchFieldShape,
                color = animatedFocusedShadowColor,
            ).clip(shape = searchFieldShape)
            .height(LocalSizes.current.size1100)
            .background(color = animatedBackgroundColor)
            .border(
                width = LocalBorderWidths.current.state.borderSelected,
                shape = searchFieldShape,
                color = animatedSelectionColor,
            ).padding(horizontal = LocalSpaces.current.spacing300),
    ) {
        AnimatedContent(
            targetState = leadingIcon,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(150),
                ) togetherWith fadeOut(
                    animationSpec = tween(150),
                )
            },
        ) { icon ->
            if (onLeadingIconClicked != null) {
                SearchFieldIconTarget(
                    icon = icon,
                    tint = LocalColors.current.content.contentPrimary,
                    onClick = onLeadingIconClicked,
                )
            } else {
                LemonadeUi.Icon(
                    icon = icon,
                    tint = LocalColors.current.content.contentPrimary,
                    contentDescription = null,
                )
            }
        }

        Box(modifier = Modifier.weight(weight = 1f)) {
            innerTextField()

            if (placeholder != null && input.isEmpty()) {
                LemonadeUi.Text(
                    text = placeholder,
                    textStyle = LocalTypographies.current.bodyMediumRegular,
                    color = LocalColors.current.content.contentTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        AnimatedVisibility(
            visible = input.isNotEmpty() && enabled,
            enter = fadeIn(animationSpec = SearchFieldFadeSpec),
            exit = fadeOut(animationSpec = SearchFieldFadeSpec),
        ) {
            SearchFieldIconTarget(
                icon = LemonadeIcons.CircleXSolid,
                tint = LocalColors.current.content.contentSecondary,
                onClick = onInputClear,
            )
        }
    }
}

// The inner box deliberately overflows the fixed outer box, so the tap area covers the field's full
// height without moving any layout.
@Composable
private fun SearchFieldIconTarget(
    icon: LemonadeIcons,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size = LocalSizes.current.size500),
    ) {
        Box(
            modifier = Modifier
                .requiredSize(size = LocalSizes.current.size1100)
                .clip(shape = LocalShapes.current.radiusFull)
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalEffects.current.interactionIndication,
                ),
        )

        LemonadeUi.Icon(
            icon = icon,
            tint = tint,
            contentDescription = null,
        )
    }
}

private data class SearchFieldPreviewData(
    val withContent: Boolean,
    val enabled: Boolean,
)

private class SearchFieldPreviewProvider : PreviewParameterProvider<SearchFieldPreviewData> {
    override val values: Sequence<SearchFieldPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SearchFieldPreviewData> =
        buildList {
            listOf(true, false)
                .forEach { withContent ->
                    listOf(true, false)
                        .forEach { enabled ->
                            add(
                                element = SearchFieldPreviewData(
                                    withContent = withContent,
                                    enabled = enabled,
                                ),
                            )
                        }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun LemonadeSearchFieldPreview(
    @PreviewParameter(SearchFieldPreviewProvider::class)
    previewData: SearchFieldPreviewData,
) {
    @OptIn(ExperimentalLemonadeComponent::class)
    LemonadeUi.SearchField(
        onInputChanged = { },
        placeholder = "This is a placeholder",
        enabled = previewData.enabled,
        cancelContentDescription = "Cancel search",
        input = if (previewData.withContent) {
            "Sample text"
        } else {
            ""
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
