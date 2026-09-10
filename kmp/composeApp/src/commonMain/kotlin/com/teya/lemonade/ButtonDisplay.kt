package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeCardPadding
import com.teya.lemonade.core.LemonadeIcons

private data class ButtonSectionSpec(
    val variant: LemonadeButtonVariant,
    val type: LemonadeButtonType,
) {
    val key: String
        get() = "$variant+$type"
}

// OnBrand / OnColor (the variants that need a backdrop) only have a Subtle treatment and ignore the
// type axis, so show each once instead of repeating an identical section under every type.
private val buttonSectionSpecs: List<ButtonSectionSpec> = LemonadeButtonVariant.entries.flatMap { variant ->
    val types = when (variant) {
        LemonadeButtonVariant.OnBrand,
        LemonadeButtonVariant.OnColor,
        -> listOf(LemonadeButtonType.Subtle)

        else -> LemonadeButtonType.entries
    }
    types.map { type ->
        ButtonSectionSpec(
            variant = variant,
            type = type,
        )
    }
}

@Composable
internal fun ButtonDisplay() {
    SampleScreenDisplayLazyColumn(title = "Button") {
        items(
            items = buttonSectionSpecs,
            key = { spec -> spec.key },
        ) { spec ->
            ButtonVariantSection(spec = spec)
        }
    }
}

// On Brand / On Color use translucent fills meant to sit on a filled surface, so give their cards a
// backdrop — otherwise the light fill (and On Color's white content) is invisible against the
// default card surface.
@Composable
private fun backdropFor(variant: LemonadeButtonVariant): Color? =
    when (variant) {
        LemonadeButtonVariant.OnBrand -> LemonadeTheme.colors.background.bgBrand
        LemonadeButtonVariant.OnColor -> LemonadeTheme.colors.background.bgSubtleInverse
        else -> null
    }

@Composable
private fun ButtonVariantSection(spec: ButtonSectionSpec) {
    val variant = spec.variant
    val type = spec.type
    val backdrop = backdropFor(variant = variant)

    ButtonSection(
        title = spec.key,
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = LemonadeTheme.spaces.spacing300,
            ),
        ) {
            ButtonCard(backdrop = backdrop) {
                LemonadeButtonSize.entries.forEach { size ->
                    LemonadeUi.Button(
                        label = size.toString(),
                        onClick = {},
                        variant = variant,
                        type = type,
                        size = size,
                    )
                }
            }

            ButtonCard(backdrop = backdrop) {
                LemonadeUi.Button(
                    label = "Leading",
                    onClick = {},
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                    leadingIcon = getButtonLeadingIcon(variant = variant),
                )
                LemonadeUi.Button(
                    label = "Trailing",
                    onClick = {},
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                    trailingIcon = LemonadeIcons.ChevronRight,
                )
            }

            ButtonCard(backdrop = backdrop) {
                LemonadeUi.Button(
                    label = "Loading",
                    onClick = {},
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                    loading = true,
                )

                LemonadeUi.Button(
                    label = "Disabled",
                    onClick = {},
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                    enabled = false,
                )
            }

            ButtonCard(backdrop = backdrop) {
                LemonadeUi.Button(
                    label = "Dual Action",
                    onClick = {},
                    trailingSlot = { colors -> DualActionTrailingSlot(colors = colors) },
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                )

                LemonadeUi.Button(
                    modifier = Modifier.fillMaxWidth(),
                    expandContents = true,
                    label = "Dual Action",
                    onClick = {},
                    trailingSlot = { colors -> DualActionTrailingSlot(colors = colors) },
                    variant = variant,
                    type = type,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }
    }
}

@Composable
private fun DualActionTrailingSlot(colors: LemonadeButtonColors) {
    LemonadeUi.VerticalDivider(modifier = Modifier.fillMaxHeight())
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) {
            colors.pressedBackgroundColor
        } else {
            colors.pressedBackgroundColor.copy(
                alpha = LemonadeTheme.opacities.base.opacity0,
            )
        },
    )
    LemonadeUi.Icon(
        icon = LemonadeIcons.EllipsisVertical,
        contentDescription = null,
        tint = colors.contentColor,
        modifier = Modifier
            .clickable(
                onClick = { },
                interactionSource = interactionSource,
            ).background(color = backgroundColor)
            .fillMaxHeight()
            .padding(horizontal = LemonadeTheme.spaces.spacing400),
    )
}

@Composable
private fun ButtonCard(
    backdrop: Color? = null,
    content: @Composable () -> Unit,
) {
    LemonadeUi.Card(
        contentPadding = LemonadeCardPadding.Medium,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = LemonadeTheme.spaces.spacing300,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (backdrop != null) {
                        Modifier
                            .background(color = backdrop)
                            .padding(all = LemonadeTheme.spaces.spacing300)
                    } else {
                        Modifier
                    },
                ),
        ) {
            content()
        }
    }
}

@Composable
private fun ButtonSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        modifier = modifier,
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}

private fun getButtonLeadingIcon(variant: LemonadeButtonVariant = LemonadeButtonVariant.Primary): LemonadeIcons =
    when (variant) {
        LemonadeButtonVariant.Primary,
        LemonadeButtonVariant.Secondary,
        LemonadeButtonVariant.Neutral,
        LemonadeButtonVariant.OnBrand,
        LemonadeButtonVariant.OnColor,
        -> LemonadeIcons.Heart

        LemonadeButtonVariant.Critical -> LemonadeIcons.Trash
    }
