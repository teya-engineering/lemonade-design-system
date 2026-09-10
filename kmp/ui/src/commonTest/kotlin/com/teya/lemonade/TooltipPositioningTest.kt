package com.teya.lemonade

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import com.teya.lemonade.core.TooltipIndicatorPlacement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TooltipPositioningTest {
    private val host = Size(
        width = 393f,
        height = 852f,
    )
    private val topAnchor = Rect(
        offset = Offset(
            x = 142f,
            y = 185f,
        ),
        size = Size(
            width = 109f,
            height = 36f,
        ),
    )
    private val bottomAnchor = Rect(
        offset = Offset(
            x = 123f,
            y = 461f,
        ),
        size = Size(
            width = 146f,
            height = 36f,
        ),
    )

    /** A leading-edge icon, with room beside it for a tooltip pointing left at it. */
    private val leadingAnchor = Rect(
        offset = Offset(
            x = 16f,
            y = 400f,
        ),
        size = Size(
            width = 40f,
            height = 40f,
        ),
    )

    /** The same icon on the trailing edge, for a tooltip pointing right at it. */
    private val trailingAnchor = Rect(
        offset = Offset(
            x = 337f,
            y = 400f,
        ),
        size = Size(
            width = 40f,
            height = 40f,
        ),
    )

    /** 1:1 so the expected values below can be read straight off the dp constants. */
    private val density = Density(density = 1f)

    /** A title-and-content tooltip: 280dp of body plus the 8dp the side indicator adds. */
    private val sideTooltip = Size(
        width = 288f,
        height = 76f,
    )

    @Test
    fun `auto placement puts the tooltip below an anchor in the top half`() {
        assertEquals(
            expected = TooltipIndicatorEdge.Top,
            actual = resolveTooltipEdge(
                anchor = topAnchor,
                hostSize = host,
                forcedPlacement = null,
            ),
        )
    }

    @Test
    fun `auto placement puts the tooltip above an anchor in the bottom half`() {
        assertEquals(
            expected = TooltipIndicatorEdge.Bottom,
            actual = resolveTooltipEdge(
                anchor = bottomAnchor,
                hostSize = host,
                forcedPlacement = null,
            ),
        )
    }

    @Test
    fun `a forced placement wins over the anchor's half of the host`() {
        assertEquals(
            expected = TooltipIndicatorEdge.Bottom,
            actual = resolveTooltipEdge(
                anchor = topAnchor,
                hostSize = host,
                forcedPlacement = TooltipIndicatorPlacement.BottomCenter,
            ),
        )
        assertEquals(
            expected = TooltipIndicatorEdge.Top,
            actual = resolveTooltipEdge(
                anchor = bottomAnchor,
                hostSize = host,
                forcedPlacement = TooltipIndicatorPlacement.TopCenter,
            ),
        )
        assertEquals(
            expected = TooltipIndicatorEdge.Left,
            actual = resolveTooltipEdge(
                anchor = bottomAnchor,
                hostSize = host,
                forcedPlacement = TooltipIndicatorPlacement.LeftCenter,
            ),
        )
    }

    @Test
    fun `an indicatorless placement falls back to auto placement`() {
        assertEquals(
            expected = TooltipIndicatorEdge.Top,
            actual = resolveTooltipEdge(
                anchor = topAnchor,
                hostSize = host,
                forcedPlacement = TooltipIndicatorPlacement.None,
            ),
        )
    }

    @Test
    fun `auto placement never picks a side placement`() {
        listOf(TooltipIndicatorEdge.Top, TooltipIndicatorEdge.Bottom).forEach { edge ->
            val placement = resolveIndicatorPlacement(
                anchor = leadingAnchor,
                hostSize = host,
                edge = edge,
                density = density,
            )
            assertFalse(placement.isOnVerticalEdge)
        }
    }

    @Test
    fun `a left placement puts the tooltip to the right of its anchor`() {
        val offset = resolveTooltipOffset(
            anchor = leadingAnchor,
            hostSize = host,
            tooltipSize = sideTooltip,
            placement = TooltipIndicatorPlacement.LeftCenter,
            density = density,
        )

        // Anchor right edge plus the 4dp gap, and centred on the anchor vertically.
        assertEquals(
            expected = 60,
            actual = offset.x,
        )
        assertEquals(
            expected = 420 - 76 / 2,
            actual = offset.y,
        )
    }

    @Test
    fun `a right placement puts the tooltip to the left of its anchor`() {
        val offset = resolveTooltipOffset(
            anchor = trailingAnchor,
            hostSize = host,
            tooltipSize = sideTooltip,
            placement = TooltipIndicatorPlacement.RightCenter,
            density = density,
        )

        // Anchor left edge, less the 4dp gap and the tooltip's own width.
        assertEquals(
            expected = 337 - 4 - 288,
            actual = offset.x,
        )
        assertEquals(
            expected = 420 - 76 / 2,
            actual = offset.y,
        )
    }

    @Test
    fun `a side placement lines its indicator up with the anchor centre`() {
        val top = resolveTooltipOffset(
            anchor = leadingAnchor,
            hostSize = host,
            tooltipSize = sideTooltip,
            placement = TooltipIndicatorPlacement.LeftTop,
            density = density,
        )
        val bottom = resolveTooltipOffset(
            anchor = leadingAnchor,
            hostSize = host,
            tooltipSize = sideTooltip,
            placement = TooltipIndicatorPlacement.LeftBottom,
            density = density,
        )

        // The indicator is fixed within the tooltip, so reaching the same anchor with it lower down
        // has to lift the tooltip itself.
        assertTrue(bottom.y < top.y)
        // Both point at the anchor's centre, 420dp down: the offset from the tooltip's top edge to
        // the indicator is 24dp of inset plus half of the 15dp base for the top variant, and 13dp
        // further down for the bottom one.
        assertEquals(
            expected = 389,
            actual = top.y,
        )
        assertEquals(
            expected = 376,
            actual = bottom.y,
        )
    }

    @Test
    fun `an edge too short for the inset collapses all three positions onto its centre`() {
        // A content-only tooltip is barely taller than the inset on both ends, which would otherwise
        // put the bottom indicator above the top one.
        val shortEdge = 40f
        val positions = listOf(
            TooltipIndicatorPlacement.LeftTop,
            TooltipIndicatorPlacement.LeftCenter,
            TooltipIndicatorPlacement.LeftBottom,
        ).map { placement ->
            indicatorCenterOffset(
                placement = placement,
                edgeLength = shortEdge,
                density = density,
            )
        }

        assertEquals(
            expected = listOf(20f, 20f, 20f),
            actual = positions,
        )
    }
}
