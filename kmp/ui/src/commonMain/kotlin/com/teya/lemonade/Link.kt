package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeLinkSize
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Clickable text styled as a hyperlink.
 *
 * Shows underlined text in the brand color with an optional trailing icon, and animates that color
 * on hover and press.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Link(
 *     text = "Learn more",
 *     onClick = { println("link clicked!") },
 * )
 *
 * LemonadeUi.Link(
 *     text = "Contact us",
 *     onClick = { println("link clicked!") },
 *     size = LemonadeLinkSize.Small,
 * )
 * ```
 *
 * @param text label shown as the link
 * @param onClick callback invoked when the link is clicked
 * @param modifier [Modifier] applied to the root container of the link
 * @param enabled whether the link accepts clicks, defaults to `true`
 * @param icon optional trailing [LemonadeIcons] shown after the text, e.g. an external link icon
 * @param size [LemonadeLinkSize] matching the body text around the link, defaults to
 * [LemonadeLinkSize.Medium]
 * @param interactionSource [MutableInteractionSource] observing the interaction states
 */
@Composable
public fun LemonadeUi.Link(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: LemonadeIcons? = null,
    size: LemonadeLinkSize = LemonadeLinkSize.Medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    CoreLink(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        size = size,
        interactionSource = interactionSource,
    )
}

@Deprecated(
    message = "Use the overload with a size parameter.",
    replaceWith = ReplaceWith(
        expression = "Link(text, onClick, modifier, enabled, icon, LemonadeLinkSize.Medium, interactionSource)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.Link(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: LemonadeIcons? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Link(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        size = LemonadeLinkSize.Medium,
        interactionSource = interactionSource,
    )
}

@Composable
private fun CoreLink(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    icon: LemonadeIcons?,
    size: LemonadeLinkSize,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val defaultColor = LocalColors.current.content.contentBrand
    val hoveredColor = LocalColors.current.interaction.bgBrandInteractive
    val pressedColor = LocalColors.current.interaction.bgBrandPressed

    val targetColor = when {
        isPressed -> pressedColor
        isHovered -> hoveredColor
        else -> defaultColor
    }

    val animatedColor by animateColorAsState(targetValue = targetColor)

    val textStyle = size.typography.textStyle.copy(
        textDecoration = TextDecoration.Underline,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        color = animatedColor,
    )

    val disabledModifier = if (!enabled) {
        Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
    } else {
        Modifier
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing100),
        modifier = modifier
            .then(other = disabledModifier)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
            ),
    ) {
        LemonadeUi.Text(
            text = text,
            textStyle = textStyle,
        )

        if (icon != null) {
            LemonadeUi.Icon(
                icon = icon,
                contentDescription = null,
                tint = animatedColor,
                size = LemonadeAssetSize.Small,
            )
        }
    }
}

private val LemonadeLinkSize.typography: LemonadeTextStyle
    @Composable get() = when (this) {
        LemonadeLinkSize.Small -> LocalTypographies.current.bodySmallMedium
        LemonadeLinkSize.Medium -> LocalTypographies.current.bodyMediumMedium
        LemonadeLinkSize.Large -> LocalTypographies.current.bodyLargeMedium
    }

private data class LinkPreviewData(
    val enabled: Boolean,
    val withIcon: Boolean,
    val size: LemonadeLinkSize,
)

private class LinkPreviewProvider : PreviewParameterProvider<LinkPreviewData> {
    override val values: Sequence<LinkPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<LinkPreviewData> =
        buildList {
            LemonadeLinkSize.entries.forEach { size ->
                listOf(true, false)
                    .forEach { enabled ->
                        listOf(true, false)
                            .forEach { withIcon ->
                                add(
                                    element = LinkPreviewData(
                                        enabled = enabled,
                                        withIcon = withIcon,
                                        size = size,
                                    ),
                                )
                            }
                    }
            }
        }.asSequence()
}

@Suppress("UnusedPrivateMember")
@Composable
@LemonadePreview
private fun LinkPreview(
    @PreviewParameter(LinkPreviewProvider::class)
    previewData: LinkPreviewData,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
        LemonadeUi.Link(
            text = "Learn more",
            onClick = { },
            enabled = previewData.enabled,
            icon = LemonadeIcons.ExternalLink.takeIf { previewData.withIcon },
            size = previewData.size,
        )
    }
}
