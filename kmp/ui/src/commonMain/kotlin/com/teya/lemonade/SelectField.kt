package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeIcons

/**
 * Opens an options list or picker without typing.
 *
 * Looks like a [TextField] but the whole field is clickable and takes no text input. Ideal for
 * country selection, category pickers, or any dropdown trigger.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SelectField(
 *     onClick = { /* Open options list */ },
 *     selectedValue = "Option A",
 *     enabled = true,
 *     error = false,
 *     label = "Label",
 *     supportText = "Support Text",
 *     optionalIndicator = "Optional",
 *     leadingContent = {
 *         LemonadeUi.Icon(
 *             icon = LemonadeIcons.Heart,
 *             contentDescription = null,
 *         )
 *     },
 * )
 * ```
 *
 * @param onClick called when the select field is clicked
 * @param selectedValue currently selected value, or null when nothing is selected
 * @param placeholderText text shown while [selectedValue] is null
 * @param label text shown above the select field
 * @param optionalIndicator optional text shown at the top right of the select field
 * @param supportText text shown below the select field
 * @param errorMessage message shown while [error] is true
 * @param error whether the select field is in an error state
 * @param enabled whether the select field responds to clicks
 * @param interactionSource [MutableInteractionSource] applied to the select field for hover and
 *   focus state handling
 * @param leadingContent optional content shown at the leading edge of the select field
 * @param modifier optional [Modifier] applied to the root container of the select field
 */
@Composable
public fun LemonadeUi.SelectField(
    onClick: () -> Unit,
    selectedValue: String?,
    placeholderText: String? = null,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    errorMessage: String? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) {
            LocalOpacities.current.base.opacity100
        } else {
            LocalOpacities.current.state.opacityDisabled
        },
    )
    CoreTextFieldDecorator(
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        errorMessage = errorMessage,
        size = null,
        interactionSource = interactionSource,
        error = error,
        enabled = enabled,
        modifier = modifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            role = Role.DropdownList,
            onClick = onClick,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
            modifier = Modifier
                .alpha(alpha = animatedAlpha)
                .align(alignment = Alignment.CenterStart)
                .padding(
                    horizontal = LocalSpaces.current.spacing400,
                    vertical = LocalSpaces.current.spacing400,
                ),
        ) {
            if (leadingContent != null) {
                leadingContent()
            }

            AnimatedContent(
                targetState = selectedValue
                    ?: placeholderText,
                modifier = Modifier.weight(weight = 1f),
                content = { showingText ->
                    if (showingText != null) {
                        LemonadeUi.Text(
                            text = showingText,
                            textStyle = LocalTypographies.current.bodyMediumRegular,
                            color = LocalColors.current.content.contentPrimary,
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                },
            )

            LemonadeUi.Icon(
                icon = LemonadeIcons.ChevronDown,
                contentDescription = null,
                tint = LocalColors.current.content.contentSecondary,
            )
        }
    }
}

private data class SelectFieldPreviewData(
    val enabled: Boolean,
    val error: Boolean,
    val withLeadingIcon: Boolean,
    val isFilled: Boolean,
    val withOuterContent: Boolean,
)

private class SelectFieldPreviewProvider : PreviewParameterProvider<SelectFieldPreviewData> {
    override val values: Sequence<SelectFieldPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SelectFieldPreviewData> =
        buildList {
            listOf(true, false)
                .forEach { withOuterContent ->
                    listOf(true, false)
                        .forEach { enabled ->
                            listOf(true, false)
                                .forEach { error ->
                                    listOf(true, false)
                                        .forEach { withLeadingIcon ->
                                            listOf(true, false)
                                                .forEach { isFilled ->
                                                    add(
                                                        element = SelectFieldPreviewData(
                                                            enabled = enabled,
                                                            error = error,
                                                            withLeadingIcon = withLeadingIcon,
                                                            isFilled = isFilled,
                                                            withOuterContent = withOuterContent,
                                                        ),
                                                    )
                                                }
                                        }
                                }
                        }
                }
        }.asSequence()
}

@Composable
@LemonadePreview
private fun SelectFieldPreview(
    @PreviewParameter(SelectFieldPreviewProvider::class)
    previewData: SelectFieldPreviewData,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
        LemonadeUi.SelectField(
            onClick = { },
            selectedValue = "Selected option".takeIf { previewData.isFilled },
            placeholderText = "Select an option",
            enabled = previewData.enabled,
            error = previewData.error,
            label = "Label".takeIf { previewData.withOuterContent },
            supportText = "Support Text".takeIf { previewData.withOuterContent },
            optionalIndicator = "Optional".takeIf { previewData.withOuterContent },
            errorMessage = "This is an error message".takeIf { previewData.withOuterContent },
            leadingContent = if (previewData.withLeadingIcon) {
                {
                    LemonadeUi.Icon(
                        icon = LemonadeIcons.Heart,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
        )
    }
}
