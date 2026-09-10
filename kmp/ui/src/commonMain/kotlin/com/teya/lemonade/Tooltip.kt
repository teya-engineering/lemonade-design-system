package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow
import com.teya.lemonade.core.TooltipFooterActionVariant
import com.teya.lemonade.core.TooltipIndicatorPlacement

internal val TooltipWidth = 280.dp

/** Height of the visible, rounded-off indicator — how far it protrudes past the tooltip body. */
internal val TooltipIndicatorHeight = 8.dp

/** Width of the indicator where it meets the tooltip body. */
internal val TooltipIndicatorBaseWidth = 15.dp

/** Height the indicator would reach with a sharp tip instead of a rounded one. */
internal val TooltipIndicatorApexHeight = 10.dp

/**
 * Distance from the tooltip corner to the near side of the indicator base.
 *
 * Applies along the top and bottom edges — the offset that separates
 * [TooltipIndicatorPlacement.TopLeft] from [TooltipIndicatorPlacement.TopCenter].
 */
internal val TooltipIndicatorHorizontalEdgeInset = 40.dp

/** As [TooltipIndicatorHorizontalEdgeInset], but along the shorter left and right edges. */
internal val TooltipIndicatorVerticalEdgeInset = 24.dp

/**
 * Fraction from the indicator base to its notional apex where the rounded tip starts.
 *
 * Sets the tip radius together with [TOOLTIP_INDICATOR_TIP_CURVATURE]; change the two together.
 */
private const val TOOLTIP_INDICATOR_TIP_START = 0.68f

/** How far each tip control point pulls towards the notional apex. */
private const val TOOLTIP_INDICATOR_TIP_CURVATURE = 0.5f

/**
 * How far, in pixels, the indicator base sinks into the tooltip body.
 *
 * The two sub-paths have to overlap rather than merely touch, or the join shows a hairline.
 */
private const val TOOLTIP_INDICATOR_BODY_OVERLAP = 1f

/** Cover slot aspect ratio — 272x158, the cover size at the default 280dp tooltip width. */
private const val TOOLTIP_COVER_ASPECT_RATIO = 272f / 158f

private const val TOOLTIP_STEP_COUNTER_SEPARATOR = "of"

/**
 * Receiver scope for the tooltip footer slot.
 *
 * The footer is a slot, but only a fixed set of parts belongs in it — they are members of this scope
 * rather than standalone components, so they can only be used where they make sense. The scope also
 * exposes [RowScope], so the parts can be spaced and weighted like any other row content.
 */
public class TooltipFooterScope internal constructor(
    private val rowScope: RowScope,
) : RowScope by rowScope {
    /**
     * Progress indicator for a multi-step tooltip tour, rendered as `1 of 3`.
     *
     * Give it `Modifier.weight(1f)` to push the actions to the trailing edge.
     *
     * @param currentStep step being shown, 1-based
     * @param totalSteps total number of steps in the tour
     * @param modifier [Modifier] applied to the counter
     * @param separator word placed between the two numbers. Defaults to `"of"` — pass a translated
     *   string to localise it
     */
    @Composable
    public fun StepCounter(
        currentStep: Int,
        totalSteps: Int,
        modifier: Modifier = Modifier,
        separator: String = TOOLTIP_STEP_COUNTER_SEPARATOR,
    ) {
        val colors = LocalColors.current
        val opacities = LocalOpacities.current

        LemonadeUi.Text(
            text = "$currentStep $separator $totalSteps",
            textStyle = LocalTypographies.current.bodyXSmallMedium,
            color = colors.content.contentPrimaryInverse.copy(alpha = opacities.base.opacity80),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
    }

    /**
     * A tappable action in the tooltip footer.
     *
     * @param label text shown on the action
     * @param onClick run when the action is tapped
     * @param modifier [Modifier] applied to the action
     * @param variant emphasis of the action. Defaults to [TooltipFooterActionVariant.Primary]
     */
    @Composable
    public fun Action(
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        variant: TooltipFooterActionVariant = TooltipFooterActionVariant.Primary,
    ) {
        val colors = LocalColors.current
        val spaces = LocalSpaces.current
        val shapes = LocalShapes.current
        val sizes = LocalSizes.current
        val typographies = LocalTypographies.current

        val backgroundColor = when (variant) {
            TooltipFooterActionVariant.Primary -> colors.background.bgDefault
            TooltipFooterActionVariant.Secondary -> Color.Transparent
        }
        val contentColor = when (variant) {
            TooltipFooterActionVariant.Primary -> colors.content.contentPrimary
            TooltipFooterActionVariant.Secondary -> colors.content.contentPrimaryInverse
        }
        val textStyle = when (variant) {
            TooltipFooterActionVariant.Primary -> typographies.bodySmallSemiBold
            TooltipFooterActionVariant.Secondary -> typographies.bodySmallMedium
        }
        val borderModifier = when (variant) {
            TooltipFooterActionVariant.Primary -> Modifier
            TooltipFooterActionVariant.Secondary -> Modifier.border(
                width = LocalBorderWidths.current.base.border25,
                color = colors.border.borderNeutralMediumInverse,
                shape = shapes.radiusFull,
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .heightIn(min = sizes.size800)
                .clip(shape = shapes.radiusFull)
                .background(color = backgroundColor)
                .then(other = borderModifier)
                .clickable(
                    onClick = onClick,
                    role = Role.Button,
                ).padding(
                    horizontal = spaces.spacing300,
                    vertical = spaces.spacing100,
                ),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = textStyle,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * A floating container that explains or draws attention to another element on screen.
 *
 * The tooltip renders its own surface, indicator and content — it does not position itself. Place it
 * in a popup, overlay or anchored layout of your choosing and pick the [indicatorPlacement] that
 * points back at the element being described.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Tooltip(
 *     content = "Tap here to see everything you sold today.",
 *     title = "Daily takings",
 *     indicatorPlacement = TooltipIndicatorPlacement.TopLeft,
 *     onCloseClick = { visible = false },
 *     footer = {
 *         StepCounter(
 *             currentStep = 1,
 *             totalSteps = 3,
 *             modifier = Modifier.weight(weight = 1f),
 *         )
 *         Action(
 *             label = "Skip",
 *             onClick = { visible = false },
 *             variant = TooltipFooterActionVariant.Secondary,
 *         )
 *         Action(
 *             label = "Next",
 *             onClick = { step++ },
 *         )
 *     },
 * )
 * ```
 *
 * ## Design Notes
 * The tooltip body is 280dp wide by default; pass a width in [modifier] to override it. The indicator
 * is drawn outside the body, so the composable is [TooltipIndicatorHeight] larger than the body on
 * whichever edge the indicator sits — taller for a top or bottom placement, wider for a left or right
 * one.
 *
 * @param content body text. Required — a tooltip always says something
 * @param modifier [Modifier] applied to the tooltip container
 * @param title optional bold heading shown above [content]
 * @param indicatorPlacement where the indicator points from. Defaults to
 *   [TooltipIndicatorPlacement.None], which draws no indicator
 * @param onCloseClick run when the close button is tapped. The close button only shows when this is
 *   non-null
 * @param closeContentDescription accessibility label for the close button
 * @param cover optional slot rendered above the text, in a 272:158 box clipped to the tooltip's top
 *   corners. Use it for an illustration or screenshot
 * @param footer optional slot rendered below the text. See [TooltipFooterScope] for the parts that
 *   belong in it
 */
@Composable
public fun LemonadeUi.Tooltip(
    content: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    indicatorPlacement: TooltipIndicatorPlacement = TooltipIndicatorPlacement.None,
    onCloseClick: (() -> Unit)? = null,
    closeContentDescription: String? = null,
    cover: (@Composable BoxScope.() -> Unit)? = null,
    footer: (@Composable TooltipFooterScope.() -> Unit)? = null,
) {
    CoreTooltip(
        content = content,
        title = title,
        indicatorPlacement = indicatorPlacement,
        onCloseClick = onCloseClick,
        closeContentDescription = closeContentDescription,
        cover = cover,
        footer = footer,
        modifier = modifier,
    )
}

@Suppress("LongParameterList")
@Composable
private fun CoreTooltip(
    content: String,
    title: String?,
    indicatorPlacement: TooltipIndicatorPlacement,
    onCloseClick: (() -> Unit)?,
    closeContentDescription: String?,
    cover: (@Composable BoxScope.() -> Unit)?,
    footer: (@Composable TooltipFooterScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColors.current
    val spaces = LocalSpaces.current
    val radius = LocalRadius.current
    val opacities = LocalOpacities.current

    val cornerRadius = radius.radius600
    val shape = remember(indicatorPlacement, cornerRadius) {
        TooltipShape(
            indicatorPlacement = indicatorPlacement,
            cornerRadius = cornerRadius,
        )
    }

    // The indicator is drawn outside the body, so the body has to be inset by its height on whichever
    // edge the indicator sits on — the same insets [TooltipShape] builds its outline from.
    val insets = indicatorPlacement.indicatorInsets
    val indicatorPadding = Modifier.absolutePadding(
        left = insets.left,
        top = insets.top,
        right = insets.right,
        bottom = insets.bottom,
    )

    // Keep this surface fully opaque. Nothing blurs the backdrop behind it, so any alpha lets busy
    // content read straight through the text.
    val surfaceColor = colors.background.bgDefaultInverse
    // The width covers the body plus whatever the indicator adds beside it, so the body itself stays
    // TooltipWidth wide whichever edge the indicator sits on.
    Box(
        modifier = modifier.width(width = TooltipWidth + insets.left + insets.right),
    ) {
        // The shadow is cast from the body rectangle rather than the full tooltip outline. Given an
        // Outline.Generic shape, Compose's dropShadow clips the blur a few dp past the node bounds
        // on Android, which cuts the falloff off square; a rounded rect takes the un-clipped path.
        // The indicator goes unshadowed as a result.
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(other = indicatorPadding)
                .lemonadeShadow(
                    shadow = LemonadeShadow.Xlarge,
                    shape = LocalShapes.current.radius600,
                ),
        )

        // Without its own compositing layer the close button's blend would reach through to whatever
        // is behind the tooltip. It wraps the surface alone — an offscreen layer clips to its own
        // bounds, so putting it any higher would take the shadow with it and crop the blur.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = surfaceColor,
                        shape = shape,
                    ).then(other = indicatorPadding)
                    .padding(all = spaces.spacing100),
            ) {
                if (cover != null) {
                    TooltipCover(
                        cornerRadius = cornerRadius,
                        outerPadding = spaces.spacing100,
                        cover = cover,
                    )
                }

                TooltipBody(
                    content = content,
                    title = title,
                    footer = footer,
                )
            }

            if (onCloseClick != null) {
                TooltipCloseButton(
                    onClick = onCloseClick,
                    contentDescription = closeContentDescription,
                    // The indicator insets are physical and the spacing is not, so they go on in two
                    // passes: whichever edge `end` resolves to, the button clears the indicator on
                    // that side and then keeps its own spacing inside the body. The inset on the
                    // opposite edge is slack — the button is pinned to `end`, so padding on its far
                    // side cannot move it.
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .then(other = indicatorPadding)
                        .padding(
                            top = spaces.spacing100,
                            end = spaces.spacing100,
                        ),
                )
            }
        }
    }
}

@Composable
private fun TooltipCover(
    cornerRadius: Dp,
    outerPadding: Dp,
    cover: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalColors.current
    val radius = LocalRadius.current

    // The cover sits inside the tooltip's outer padding, so its top corners are the tooltip's own
    // radius less that padding; the bottom corners sit inside the body and take their own radius.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = TOOLTIP_COVER_ASPECT_RATIO)
            .clip(
                shape = RoundedCornerShape(
                    topStart = cornerRadius - outerPadding,
                    topEnd = cornerRadius - outerPadding,
                    bottomStart = radius.radius250,
                    bottomEnd = radius.radius250,
                ),
            ).background(color = colors.background.bgElevatedHigh),
        content = cover,
    )
}

@Composable
private fun TooltipBody(
    content: String,
    title: String?,
    footer: (@Composable TooltipFooterScope.() -> Unit)?,
) {
    val colors = LocalColors.current
    val spaces = LocalSpaces.current
    val opacities = LocalOpacities.current
    val typographies = LocalTypographies.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = spaces.spacing300),
        verticalArrangement = Arrangement.spacedBy(space = spaces.spacing300),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            // Only takes effect when there is a title — spacedBy adds nothing to a single child.
            verticalArrangement = Arrangement.spacedBy(space = spaces.spacing100),
        ) {
            if (title != null) {
                LemonadeUi.Text(
                    text = title,
                    textStyle = typographies.bodySmallSemiBold,
                    color = colors.content.contentPrimaryInverse,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            LemonadeUi.Text(
                text = content,
                textStyle = typographies.bodySmallRegular,
                color = colors.content.contentPrimaryInverse.copy(alpha = opacities.base.opacity90),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (footer != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = spaces.spacing100),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val footerScope = remember(this@Row) { TooltipFooterScope(rowScope = this@Row) }
                footerScope.footer()
            }
        }
    }
}

@Composable
private fun TooltipCloseButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColors.current
    val sizes = LocalSizes.current
    val shapes = LocalShapes.current
    val opacities = LocalOpacities.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size = sizes.size800)
            .clip(shape = shapes.radiusFull)
            .clickable(
                onClick = onClick,
                role = Role.Button,
            ).graphicsLayer {
                alpha = opacities.base.opacity40
                // Hard light rather than a flat tint: over the dark light-mode surface a light icon
                // screens and stays visible, and over the light dark-mode surface a dark icon
                // multiplies, so the same token reads on both without a per-theme colour.
                blendMode = BlendMode.Hardlight
            },
    ) {
        LemonadeUi.Icon(
            icon = LemonadeIcons.Times,
            contentDescription = contentDescription,
            size = LemonadeAssetSize.Small,
            tint = colors.content.contentPrimaryInverse,
        )
    }
}

internal enum class TooltipIndicatorEdge {
    None,
    Top,
    Bottom,
    Left,
    Right,
}

/**
 * Where along its edge the indicator sits.
 *
 * [Start] and [End] are physical, like the placements themselves: the left and right ends of the top
 * and bottom edges, the top and bottom ends of the left and right ones.
 */
internal enum class TooltipIndicatorAlignment {
    Start,
    Center,
    End,
}

internal val TooltipIndicatorPlacement.edge: TooltipIndicatorEdge
    get() {
        return when (this) {
            TooltipIndicatorPlacement.None -> TooltipIndicatorEdge.None

            TooltipIndicatorPlacement.TopLeft,
            TooltipIndicatorPlacement.TopCenter,
            TooltipIndicatorPlacement.TopRight,
            -> TooltipIndicatorEdge.Top

            TooltipIndicatorPlacement.BottomLeft,
            TooltipIndicatorPlacement.BottomCenter,
            TooltipIndicatorPlacement.BottomRight,
            -> TooltipIndicatorEdge.Bottom

            TooltipIndicatorPlacement.LeftTop,
            TooltipIndicatorPlacement.LeftCenter,
            TooltipIndicatorPlacement.LeftBottom,
            -> TooltipIndicatorEdge.Left

            TooltipIndicatorPlacement.RightTop,
            TooltipIndicatorPlacement.RightCenter,
            TooltipIndicatorPlacement.RightBottom,
            -> TooltipIndicatorEdge.Right
        }
    }

internal val TooltipIndicatorPlacement.alignment: TooltipIndicatorAlignment
    get() {
        return when (this) {
            TooltipIndicatorPlacement.TopLeft,
            TooltipIndicatorPlacement.BottomLeft,
            TooltipIndicatorPlacement.LeftTop,
            TooltipIndicatorPlacement.RightTop,
            -> TooltipIndicatorAlignment.Start

            TooltipIndicatorPlacement.TopRight,
            TooltipIndicatorPlacement.BottomRight,
            TooltipIndicatorPlacement.LeftBottom,
            TooltipIndicatorPlacement.RightBottom,
            -> TooltipIndicatorAlignment.End

            TooltipIndicatorPlacement.None,
            TooltipIndicatorPlacement.TopCenter,
            TooltipIndicatorPlacement.BottomCenter,
            TooltipIndicatorPlacement.LeftCenter,
            TooltipIndicatorPlacement.RightCenter,
            -> TooltipIndicatorAlignment.Center
        }
    }

/**
 * Whether the indicator runs along the left or right edge.
 *
 * When it does, its position is measured down the tooltip's height rather than across its width.
 * [TooltipIndicatorPlacement.None] counts as horizontal — it has no indicator, and the host still
 * centres it on its anchor horizontally.
 */
internal val TooltipIndicatorPlacement.isOnVerticalEdge: Boolean
    get() {
        return edge == TooltipIndicatorEdge.Left || edge == TooltipIndicatorEdge.Right
    }

/**
 * How far the indicator protrudes past the tooltip body on each edge.
 *
 * [TooltipIndicatorHeight] on the indicator's own edge, zero on the other three. The edges are
 * physical, like the placements themselves, so a left indicator stays on the left in an RTL layout.
 * This is the single statement of "which edge does the indicator add to": the layout pads the body by
 * it and [TooltipShape] insets its outline by it, and the two have to agree exactly or the drawn
 * surface and the padded content come apart.
 */
internal data class TooltipIndicatorInsets(
    val left: Dp,
    val top: Dp,
    val right: Dp,
    val bottom: Dp,
)

internal val TooltipIndicatorPlacement.indicatorInsets: TooltipIndicatorInsets
    get() {
        val protrusion = TooltipIndicatorHeight

        return TooltipIndicatorInsets(
            left = if (edge == TooltipIndicatorEdge.Left) protrusion else 0.dp,
            top = if (edge == TooltipIndicatorEdge.Top) protrusion else 0.dp,
            right = if (edge == TooltipIndicatorEdge.Right) protrusion else 0.dp,
            bottom = if (edge == TooltipIndicatorEdge.Bottom) protrusion else 0.dp,
        )
    }

/** Rotation that carries the canonical upward-pointing indicator onto this edge. */
private val TooltipIndicatorEdge.rotationDegrees: Float
    get() {
        return when (this) {
            TooltipIndicatorEdge.None,
            TooltipIndicatorEdge.Top,
            -> 0f

            TooltipIndicatorEdge.Right -> 90f
            TooltipIndicatorEdge.Bottom -> 180f
            TooltipIndicatorEdge.Left -> 270f
        }
    }

/**
 * Distance from the start of the indicator's edge to the centre of the indicator.
 *
 * Measured from the body's left edge for a top or bottom placement, from its top edge for a left or
 * right one. The indicator sits at one of three fixed positions along its edge, so this is the reach a
 * given placement can offer. Pass the length of the edge the placement sits on: the body's width for a
 * top or bottom placement, its height for a left or right one.
 */
internal fun indicatorCenterOffset(
    placement: TooltipIndicatorPlacement,
    edgeLength: Float,
    density: Density,
): Float {
    val halfBase = with(density) { TooltipIndicatorBaseWidth.toPx() } / 2f
    val inset = with(density) {
        if (placement.isOnVerticalEdge) {
            TooltipIndicatorVerticalEdgeInset.toPx()
        } else {
            TooltipIndicatorHorizontalEdgeInset.toPx()
        }
    }

    // An edge too short for the inset would put End ahead of Start; collapsing both onto the centre
    // keeps the three positions in order instead of crossing them over.
    val center = edgeLength / 2f

    return when (placement.alignment) {
        TooltipIndicatorAlignment.Start -> minOf(
            a = inset + halfBase,
            b = center,
        )

        TooltipIndicatorAlignment.Center -> center

        TooltipIndicatorAlignment.End -> maxOf(
            a = edgeLength - inset - halfBase,
            b = center,
        )
    }
}

/**
 * The tooltip surface: a rounded rectangle with the indicator triangle added to one edge.
 *
 * The two are separate sub-paths of a single [Path] rather than two drawn shapes. Non-zero winding
 * fills overlapping sub-paths as their union in one operation, so the join is seamless; two drawn
 * shapes would composite the overlap twice and show a darker wedge across it. Being one shape also
 * means one shadow outline and one clip for the host's backdrop blur.
 *
 * The indicator is drawn outside the body, so the shape expects a size that already includes
 * [TooltipIndicatorHeight] on the indicator's edge.
 */
internal data class TooltipShape(
    private val indicatorPlacement: TooltipIndicatorPlacement,
    private val cornerRadius: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val insets = indicatorPlacement.indicatorInsets
        val bodyRect = with(density) {
            Rect(
                left = insets.left.toPx(),
                top = insets.top.toPx(),
                right = size.width - insets.right.toPx(),
                bottom = size.height - insets.bottom.toPx(),
            )
        }
        val shortestSide = minOf(
            a = bodyRect.width,
            b = bodyRect.height,
        )
        val radius = with(density) { cornerRadius.toPx() }
            .coerceAtMost(maximumValue = shortestSide / 2f)

        val body = Path()
            .apply {
                addRoundRect(
                    roundRect = RoundRect(
                        rect = bodyRect,
                        cornerRadius = CornerRadius(
                            x = radius,
                            y = radius,
                        ),
                    ),
                )
            }

        if (indicatorPlacement == TooltipIndicatorPlacement.None) {
            return Outline.Generic(path = body)
        }

        val indicator = indicatorPath(
            bodyRect = bodyRect,
            density = density,
        )

        // An explicit union rather than appending the triangle as a second sub-path. Skia adds the
        // round rect with the opposite winding to the triangle, so under the non-zero rule the two
        // cancel exactly where they overlap and punch a 1px hole the width of the indicator base
        // across the join. A boolean union does not care about winding.
        val path = Path()
            .apply {
                op(
                    path1 = body,
                    path2 = indicator,
                    operation = PathOperation.Union,
                )
            }

        return Outline.Generic(path = path)
    }

    /**
     * The indicator triangle. Its straight edges stop short of the notional apex and a cubic bridges
     * the gap, which is what rounds the tip.
     *
     * It is built once in a canonical frame — base centred on the origin, apex pointing up — and then
     * rotated onto whichever edge it belongs to, so the four edges cannot drift apart. The base is
     * deliberately sunk [TOOLTIP_INDICATOR_BODY_OVERLAP] into the body, on the far side of the origin
     * from the apex.
     */
    private fun indicatorPath(
        bodyRect: Rect,
        density: Density,
    ): Path {
        val halfBase = with(density) { TooltipIndicatorBaseWidth.toPx() } / 2f
        val apexY = -with(density) { TooltipIndicatorApexHeight.toPx() }

        val tipEntryX = -halfBase * (1f - TOOLTIP_INDICATOR_TIP_START)
        val tipExitX = -tipEntryX
        val tipY = apexY * TOOLTIP_INDICATOR_TIP_START
        val controlY = tipY + (apexY - tipY) * TOOLTIP_INDICATOR_TIP_CURVATURE

        val indicator = Path()
            .apply {
                moveTo(
                    x = -halfBase,
                    y = TOOLTIP_INDICATOR_BODY_OVERLAP,
                )
                lineTo(
                    x = tipEntryX,
                    y = tipY,
                )
                cubicTo(
                    x1 = tipEntryX * (1f - TOOLTIP_INDICATOR_TIP_CURVATURE),
                    y1 = controlY,
                    x2 = tipExitX * (1f - TOOLTIP_INDICATOR_TIP_CURVATURE),
                    y2 = controlY,
                    x3 = tipExitX,
                    y3 = tipY,
                )
                lineTo(
                    x = halfBase,
                    y = TOOLTIP_INDICATOR_BODY_OVERLAP,
                )
                close()
            }

        val centerOffset = indicatorCenterOffset(
            placement = indicatorPlacement,
            edgeLength = if (indicatorPlacement.isOnVerticalEdge) bodyRect.height else bodyRect.width,
            density = density,
        )
        val edge = indicatorPlacement.edge
        val origin = when (edge) {
            TooltipIndicatorEdge.Top,
            TooltipIndicatorEdge.None,
            -> Offset(
                x = bodyRect.left + centerOffset,
                y = bodyRect.top,
            )

            TooltipIndicatorEdge.Bottom -> Offset(
                x = bodyRect.left + centerOffset,
                y = bodyRect.bottom,
            )

            TooltipIndicatorEdge.Left -> Offset(
                x = bodyRect.left,
                y = bodyRect.top + centerOffset,
            )

            TooltipIndicatorEdge.Right -> Offset(
                x = bodyRect.right,
                y = bodyRect.top + centerOffset,
            )
        }

        indicator.transform(
            matrix = Matrix()
                .apply {
                    translate(
                        x = origin.x,
                        y = origin.y,
                    )
                    rotateZ(degrees = edge.rotationDegrees)
                },
        )

        return indicator
    }
}

private data class TooltipPreviewData(
    val indicatorPlacement: TooltipIndicatorPlacement,
    val withTitle: Boolean,
    val withCloseButton: Boolean,
    val withCover: Boolean,
    val withFooter: Boolean,
)

private class TooltipPreviewProvider : PreviewParameterProvider<TooltipPreviewData> {
    override val values: Sequence<TooltipPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<TooltipPreviewData> =
        buildList {
            TooltipIndicatorPlacement.entries.forEach { placement ->
                add(
                    element = TooltipPreviewData(
                        indicatorPlacement = placement,
                        withTitle = true,
                        withCloseButton = false,
                        withCover = false,
                        withFooter = true,
                    ),
                )
            }
            listOf(true, false)
                .forEach { withTitle ->
                    listOf(true, false)
                        .forEach { withCover ->
                            add(
                                element = TooltipPreviewData(
                                    indicatorPlacement = TooltipIndicatorPlacement.TopCenter,
                                    withTitle = withTitle,
                                    withCloseButton = true,
                                    withCover = withCover,
                                    withFooter = withTitle,
                                ),
                            )
                        }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun TooltipPreview(
    @PreviewParameter(TooltipPreviewProvider::class)
    previewData: TooltipPreviewData,
) {
    val onCloseClick: (() -> Unit)? = if (previewData.withCloseButton) {
        {}
    } else {
        null
    }

    LemonadeUi.Tooltip(
        content = "Tap here to see everything you sold today.",
        title = "Daily takings".takeIf { previewData.withTitle },
        indicatorPlacement = previewData.indicatorPlacement,
        onCloseClick = onCloseClick,
        closeContentDescription = "Close",
        cover = if (previewData.withCover) {
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = LocalColors.current.background.bgBrandSubtle),
                )
            }
        } else {
            null
        },
        footer = if (previewData.withFooter) {
            {
                StepCounter(
                    currentStep = 1,
                    totalSteps = 3,
                    modifier = Modifier.weight(weight = 1f),
                )
                Action(
                    label = "Skip",
                    onClick = {},
                    variant = TooltipFooterActionVariant.Secondary,
                )
                Action(
                    label = "Next",
                    onClick = {},
                )
            }
        } else {
            null
        },
    )
}
