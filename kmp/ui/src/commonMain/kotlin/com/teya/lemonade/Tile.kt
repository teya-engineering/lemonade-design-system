package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTileOrientation
import com.teya.lemonade.core.LemonadeTileVariant

/**
 * Shows an icon and a label in a selectable card.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Tile(
 *   label = "Transfer",
 *   icon = LemonadeIcons.ArrowLeftRight,
 *   onClick = { println("tile tapped!") },
 * )
 * ```
 *
 * @param label text shown as the tile's label
 * @param icon [LemonadeIcons] shown in the leading position
 * @param modifier [Modifier] applied to the tile
 * @param enabled `false` dims the tile and blocks clicks
 * @param isSelected `true` applies the selected styling
 * @param supportText text shown below the label
 * @param topAccessory composable shown at the top-right, only in
 *  [LemonadeTileOrientation.Vertical]
 * @param onClick callback run when the tile is clicked; `null` leaves the tile non-clickable
 * @param interactionSource [MutableInteractionSource] applied to the tile
 * @param variant [LemonadeTileVariant] driving the tile's fill and border
 * @param orientation [LemonadeTileOrientation] driving the layout direction
 */
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.Tile(
    label: String,
    icon: LemonadeIcons,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    supportText: String? = null,
    topAccessory: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    variant: LemonadeTileVariant = LemonadeTileVariant.Filled,
    orientation: LemonadeTileOrientation = LemonadeTileOrientation.Vertical,
) {
    val contentColor = LocalColors.current.content.contentPrimary

    CoreTile(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        enabled = enabled,
        isSelected = isSelected,
        interactionSource = interactionSource,
        content = {
            when (orientation) {
                LemonadeTileOrientation.Horizontal -> HorizontalTileContent(
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = icon,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = null,
                            tint = contentColor,
                        )
                    },
                    label = label,
                    supportText = supportText,
                    contentColor = contentColor,
                )
                LemonadeTileOrientation.Vertical -> Column(
                    verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                        .padding(all = LocalSpaces.current.spacing300),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        LemonadeUi.Icon(
                            icon = icon,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = null,
                            tint = contentColor,
                        )

                        if (topAccessory != null) {
                            topAccessory()
                        }
                    }

                    Spacer(modifier = Modifier.weight(weight = 1f))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        LemonadeUi.Text(
                            text = label,
                            textStyle = LocalTypographies.current.bodySmallMedium,
                            color = contentColor,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )

                        if (supportText != null) {
                            LemonadeUi.Text(
                                text = supportText,
                                textStyle = LocalTypographies.current.bodySmallRegular,
                                color = LocalColors.current.content.contentSecondary,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Deprecated(
    message = "Use the overload with an orientation parameter.",
    replaceWith = ReplaceWith(
        expression = "Tile(label, icon, modifier, enabled, isSelected, supportText, " +
            "topAccessory, onClick, interactionSource, variant, LemonadeTileOrientation.Vertical)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.Tile(
    label: String,
    icon: LemonadeIcons,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    supportText: String? = null,
    topAccessory: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    variant: LemonadeTileVariant = LemonadeTileVariant.Filled,
) {
    Tile(
        label = label,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        isSelected = isSelected,
        supportText = supportText,
        topAccessory = topAccessory,
        onClick = onClick,
        interactionSource = interactionSource,
        variant = variant,
        orientation = LemonadeTileOrientation.Vertical,
    )
}

/**
 * Shows a custom leading slot and a label in a selectable card.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Tile(
 *   label = "Custom",
 *   leadingSlot = {
 *     // Custom composable content
 *   },
 * )
 * ```
 *
 * @param label text shown as the tile's label
 * @param leadingSlot composable shown in the leading position
 * @param modifier [Modifier] applied to the tile
 * @param enabled `false` dims the tile and blocks clicks
 * @param isSelected `true` applies the selected styling
 * @param supportText text shown below the label
 * @param topAccessory composable shown at the top-right, only in
 *  [LemonadeTileOrientation.Vertical]
 * @param onClick callback run when the tile is clicked; `null` leaves the tile non-clickable
 * @param interactionSource [MutableInteractionSource] applied to the tile
 * @param variant [LemonadeTileVariant] driving the tile's fill and border
 * @param orientation [LemonadeTileOrientation] driving the layout direction
 */
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.Tile(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    supportText: String? = null,
    topAccessory: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    variant: LemonadeTileVariant = LemonadeTileVariant.Filled,
    orientation: LemonadeTileOrientation = LemonadeTileOrientation.Vertical,
    leadingSlot: @Composable () -> Unit,
) {
    val contentColor = LocalColors.current.content.contentPrimary

    CoreTile(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        enabled = enabled,
        isSelected = isSelected,
        interactionSource = interactionSource,
        content = {
            when (orientation) {
                LemonadeTileOrientation.Horizontal -> HorizontalTileContent(
                    leadingSlot = leadingSlot,
                    label = label,
                    supportText = supportText,
                    contentColor = contentColor,
                )
                LemonadeTileOrientation.Vertical -> Column(
                    verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                        .padding(all = LocalSpaces.current.spacing300),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        leadingSlot()

                        if (topAccessory != null) {
                            topAccessory()
                        }
                    }

                    Spacer(modifier = Modifier.weight(weight = 1f))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        LemonadeUi.Text(
                            text = label,
                            textStyle = LocalTypographies.current.bodySmallMedium,
                            color = contentColor,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )

                        if (supportText != null) {
                            LemonadeUi.Text(
                                text = supportText,
                                textStyle = LocalTypographies.current.bodySmallRegular,
                                color = LocalColors.current.content.contentSecondary,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Deprecated(
    message = "Use the overload with an orientation parameter.",
    replaceWith = ReplaceWith(
        expression = "Tile(label, modifier, enabled, isSelected, supportText, topAccessory, " +
            "onClick, interactionSource, variant, LemonadeTileOrientation.Vertical, leadingSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.Tile(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    supportText: String? = null,
    topAccessory: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    variant: LemonadeTileVariant = LemonadeTileVariant.Filled,
    leadingSlot: @Composable () -> Unit,
) {
    Tile(
        label = label,
        modifier = modifier,
        enabled = enabled,
        isSelected = isSelected,
        supportText = supportText,
        topAccessory = topAccessory,
        onClick = onClick,
        interactionSource = interactionSource,
        variant = variant,
        orientation = LemonadeTileOrientation.Vertical,
        leadingSlot = leadingSlot,
    )
}

@Composable
private fun HorizontalTileContent(
    leadingSlot: @Composable () -> Unit,
    label: String,
    supportText: String?,
    contentColor: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .defaultMinSize(
                minWidth = 120.dp,
                minHeight = LocalSizes.current.size1600,
            ).fillMaxWidth()
            .padding(all = LocalSpaces.current.spacing300),
    ) {
        leadingSlot()

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(weight = 1f),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = LocalTypographies.current.bodySmallMedium,
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            if (supportText != null) {
                LemonadeUi.Text(
                    text = supportText,
                    textStyle = LocalTypographies.current.bodySmallRegular,
                    color = LocalColors.current.content.contentSecondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun CoreTile(
    variant: LemonadeTileVariant,
    content: @Composable BoxScope.() -> Unit,
    enabled: Boolean,
    isSelected: Boolean,
    onClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val tileShape = LocalShapes.current.radius500
    val baseTileData = variant.data
    val tileData = if (isSelected) {
        baseTileData.copy(
            backgroundColor = LocalColors.current.background.bgDefault,
            borderColor = LocalColors.current.border.borderSelected,
            borderWidth = LocalBorderWidths.current.base.border50,
        )
    } else {
        baseTileData
    }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = tileData.backgroundColor,
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = tileData.borderColor,
    )
    val animatedBorderWidth by animateDpAsState(
        targetValue = tileData.borderWidth,
    )

    Box(
        contentAlignment = Alignment.Center,
        content = content,
        modifier = modifier
            .then(
                other = if (!enabled) {
                    Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                } else {
                    Modifier
                },
            ).then(
                other = if (isFocused) {
                    Modifier
                        .border(
                            width = LocalBorderWidths.current.base.border25,
                            color = LocalColors.current.border.borderSelected,
                            shape = tileShape,
                        ).padding(all = LocalSpaces.current.spacing50)
                } else {
                    Modifier
                },
            ).then(
                other = Modifier.border(
                    color = animatedBorderColor,
                    shape = tileShape,
                    width = animatedBorderWidth,
                ),
            ).clip(shape = tileShape)
            .then(
                other = if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        interactionSource = interactionSource,
                        role = Role.Button,
                        enabled = enabled,
                        indication = LocalEffects.current.interactionIndication,
                    )
                } else {
                    Modifier
                },
            ).background(
                color = animatedBackgroundColor,
                shape = tileShape,
            ),
    )
}

internal data class TileData(
    val backgroundColor: Color,
    val borderColor: Color,
    val borderWidth: Dp,
)

internal val LemonadeTileVariant.data: TileData
    @Composable get() = when (this) {
        LemonadeTileVariant.Filled -> TileData(
            backgroundColor = LocalColors.current.background.bgElevated,
            borderColor = Color.Transparent,
            borderWidth = 0.dp,
        )
        LemonadeTileVariant.Outlined -> TileData(
            backgroundColor = Color.Transparent,
            borderColor = LocalColors.current.border.borderNeutralMedium,
            borderWidth = LocalBorderWidths.current.base.border40,
        )
    }

private data class TilePreviewData(
    val enabled: Boolean,
    val variant: LemonadeTileVariant,
    val isSelected: Boolean,
    val orientation: LemonadeTileOrientation,
)

private class TilePreviewProvider : PreviewParameterProvider<TilePreviewData> {
    override val values: Sequence<TilePreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<TilePreviewData> =
        buildList {
            listOf(true, false)
                .forEach { enabled ->
                    listOf(
                        LemonadeTileVariant.Filled,
                        LemonadeTileVariant.Outlined,
                    ).forEach { variant ->
                        listOf(true, false)
                            .forEach { selected ->
                                listOf(
                                    LemonadeTileOrientation.Vertical,
                                    LemonadeTileOrientation.Horizontal,
                                ).forEach { orientation ->
                                    add(
                                        TilePreviewData(
                                            enabled = enabled,
                                            variant = variant,
                                            isSelected = selected,
                                            orientation = orientation,
                                        ),
                                    )
                                }
                            }
                    }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun LemonadeTilePreview(
    @PreviewParameter(TilePreviewProvider::class)
    previewData: TilePreviewData,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = LocalColors.current.background.bgBrand.copy(
                    alpha = LocalOpacities.current.base.opacity0,
                ),
            ).padding(all = 30.dp),
    ) {
        LemonadeUi.Tile(
            label = "Label",
            icon = LemonadeIcons.Heart,
            enabled = previewData.enabled,
            isSelected = previewData.isSelected,
            variant = previewData.variant,
            orientation = previewData.orientation,
        )
    }
}
