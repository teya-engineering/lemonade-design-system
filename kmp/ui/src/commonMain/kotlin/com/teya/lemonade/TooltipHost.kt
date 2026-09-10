package com.teya.lemonade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.TooltipFooterActionVariant
import com.teya.lemonade.core.TooltipIndicatorPlacement
import com.teya.lemonade.core.TooltipScrim
import kotlin.math.abs
import kotlin.math.roundToInt

/** Gap between the indicator tip and the anchor it points at. */
private val TooltipAnchorGap = 4.dp

/** Smallest distance the tooltip keeps from the edges of the host. */
private val TooltipHostMargin = 8.dp

/** Space left around the anchor when the spotlight scrim punches through. */
private val TooltipSpotlightPadding = 4.dp

/** Corner radius of the spotlight cut-out. */
private val TooltipSpotlightRadius = 12.dp

/** Scale the tooltip grows from as it enters. */
private const val TOOLTIP_ENTER_INITIAL_SCALE = 0.8f

/** Bounce of the entry spring. Below 1 so the scale overshoots and settles — the "bubble" feel. */
private const val TOOLTIP_ENTER_DAMPING_RATIO = 0.55f

private const val TOOLTIP_ENTER_FADE_MILLIS = 120

private const val TOOLTIP_EXIT_FADE_MILLIS = 90

private const val TOOLTIP_SCRIM_FADE_IN_MILLIS = 180

/**
 * One step of a guided tour. See [LemonadeTooltipState.startTour].
 *
 * @param anchor key of the element this step points at, as registered by
 *   [Modifier.lemonadeTooltipAnchor]. A step whose anchor is not currently on screen does not render
 *   until it appears, so a tour can span several screens
 * @param content body text of the step
 * @param title optional bold heading
 * @param indicatorPlacement forces where the indicator sits. Defaults to `null`, which resolves the
 *   placement from where the anchor is on screen — always a top or bottom one, so pass a left or
 *   right placement to put the tooltip beside its anchor instead
 * @param cover optional cover slot, rendered above the text
 */
public class LemonadeTooltipStep(
    public val anchor: String,
    public val content: String,
    public val title: String? = null,
    public val indicatorPlacement: TooltipIndicatorPlacement? = null,
    public val cover: (@Composable BoxScope.() -> Unit)? = null,
)

/**
 * Text the host puts in a tour's footer. Pass translated strings to localise a tour.
 *
 * @param next label of the action that advances to the next step
 * @param done label that replaces [next] on the final step
 * @param skip label of the action that abandons the tour. Pass `null` to leave it out
 * @param close accessibility label for the close button. Kept separate from [skip], which can be
 *   `null` — the close button is always there, so it always needs a label
 * @param stepSeparator word between the two numbers of the step counter, as in `1 of 3`
 */
public class LemonadeTooltipTourLabels(
    public val next: String = "Next",
    public val done: String = "Done",
    public val skip: String? = "Skip",
    public val close: String = "Close",
    public val stepSeparator: String = "of",
)

/**
 * Carries the arguments behind one presented tooltip.
 *
 * They come straight from [LemonadeTooltipState.show] or [LemonadeTooltipState.startTour], so the
 * parameter count tracks theirs.
 */
@Suppress("LongParameterList")
internal class TooltipPresentation(
    val id: Int,
    val anchor: String,
    val content: String,
    val title: String?,
    val indicatorPlacement: TooltipIndicatorPlacement?,
    val scrim: TooltipScrim,
    val dismissOnOutsideTap: Boolean,
    val onCloseClick: (() -> Unit)?,
    val closeContentDescription: String?,
    val cover: (@Composable BoxScope.() -> Unit)?,
    val footer: (@Composable TooltipFooterScope.() -> Unit)?,
)

@Suppress("LongParameterList")
private class TooltipTour(
    val steps: List<LemonadeTooltipStep>,
    val labels: LemonadeTooltipTourLabels,
    val scrim: TooltipScrim,
    val showCloseButton: Boolean,
    val dismissOnOutsideTap: Boolean,
    val onFinish: () -> Unit,
    val onSkip: () -> Unit,
)

/**
 * State holder for the Lemonade Tooltip system. Obtain it via [LocalLemonadeTooltipState].
 *
 * A tooltip always points at an element, so the element has to be tagged with
 * [Modifier.lemonadeTooltipAnchor] before anything can be shown against it. Only one tooltip is
 * visible at a time and showing another replaces it. Nothing is queued: unlike a toast, a tooltip
 * describes something on screen, so a queued one would usually be stale by the time it surfaced.
 */
@Stable
public class LemonadeTooltipState {
    internal var presentation: TooltipPresentation? by mutableStateOf(null)
        private set

    internal var hostBounds: Rect? by mutableStateOf(null)

    private val anchors = mutableStateMapOf<String, Rect>()

    private var tour: TooltipTour? by mutableStateOf(null)

    private var stepIndex: Int by mutableStateOf(0)

    private var nextId: Int = 0

    /** Index of the step being shown, or `-1` when no tour is running. */
    public val currentStepIndex: Int
        get() {
            if (tour == null) {
                return -1
            }
            return stepIndex
        }

    /** Number of steps in the running tour, or `0` when none is running. */
    public val tourStepCount: Int
        get() {
            return tour?.steps?.size
                ?: 0
        }

    /** Whether a tooltip is currently being presented. */
    public val isVisible: Boolean
        get() {
            return presentation != null
        }

    /**
     * Shows a single tooltip against [anchor]. Anything already showing is replaced.
     *
     * ## Usage
     * ```kotlin
     * val tooltips = LocalLemonadeTooltipState.current
     * tooltips.show(
     *     anchor = "fees-info",
     *     content = "Fees are deducted daily.",
     * )
     * ```
     *
     * @param anchor key of the element to point at, as registered by
     *   [Modifier.lemonadeTooltipAnchor]. Nothing renders until an element with this key is on screen
     * @param content body text
     * @param title optional bold heading
     * @param indicatorPlacement forces where the indicator sits. Defaults to `null`, which resolves
     *   the placement from where the anchor is on screen — always a top or bottom one, so pass a left
     *   or right placement to put the tooltip beside its anchor instead
     * @param scrim what to draw behind the tooltip. Defaults to [TooltipScrim.None] — on-demand help
     *   usually should not dim the screen
     * @param dismissOnOutsideTap whether a tap outside the tooltip dismisses it. Defaults to `true`.
     *   Either way the host swallows the tap, so the UI underneath is never acted on by accident
     * @param showCloseButton whether to show the close button. Defaults to `false`
     * @param closeContentDescription accessibility label for the close button
     * @param cover optional cover slot, rendered above the text
     * @param footer optional footer slot. See [TooltipFooterScope]
     */
    @Suppress("LongParameterList")
    public fun show(
        anchor: String,
        content: String,
        title: String? = null,
        indicatorPlacement: TooltipIndicatorPlacement? = null,
        scrim: TooltipScrim = TooltipScrim.None,
        dismissOnOutsideTap: Boolean = true,
        showCloseButton: Boolean = false,
        closeContentDescription: String? = null,
        cover: (@Composable BoxScope.() -> Unit)? = null,
        footer: (@Composable TooltipFooterScope.() -> Unit)? = null,
    ) {
        tour = null

        val onCloseClick: (() -> Unit)? = if (showCloseButton) {
            { dismiss() }
        } else {
            null
        }

        presentation = TooltipPresentation(
            id = nextId++,
            anchor = anchor,
            content = content,
            title = title,
            indicatorPlacement = indicatorPlacement,
            scrim = scrim,
            dismissOnOutsideTap = dismissOnOutsideTap,
            onCloseClick = onCloseClick,
            closeContentDescription = closeContentDescription,
            cover = cover,
            footer = footer,
        )
    }

    /**
     * Starts a guided tour. The host builds each step's footer itself — the step counter, the skip
     * action and the next/done action — so a caller only describes the steps.
     *
     * ## Usage
     * ```kotlin
     * tooltips.startTour(
     *     steps = listOf(
     *         LemonadeTooltipStep(
     *             anchor = "takings",
     *             title = "Daily takings",
     *             content = "Everything you sold today.",
     *         ),
     *     ),
     *     onFinish = { markOnboardingSeen() },
     * )
     * ```
     *
     * @param steps the steps, in order. An empty list does nothing
     * @param labels text for the generated footer. Pass translated strings to localise a tour
     * @param scrim what to draw behind each step. Defaults to [TooltipScrim.Spotlight], which keeps
     *   the element being described lit while dimming the rest
     * @param showCloseButton whether each step shows the close button. Defaults to `true`
     * @param dismissOnOutsideTap whether a tap outside the tooltip abandons the tour. Defaults to
     *   `true`. Pass `false` to require the next/done action — the tap is still swallowed, so the UI
     *   underneath is never acted on, it just does not end the tour
     * @param onFinish run once the final step is confirmed
     * @param onSkip run when the tour is abandoned, whether by the skip action, the close button or
     *   an outside tap
     */
    public fun startTour(
        steps: List<LemonadeTooltipStep>,
        labels: LemonadeTooltipTourLabels = LemonadeTooltipTourLabels(),
        scrim: TooltipScrim = TooltipScrim.Spotlight,
        showCloseButton: Boolean = true,
        dismissOnOutsideTap: Boolean = true,
        onFinish: () -> Unit = {},
        onSkip: () -> Unit = {},
    ) {
        if (steps.isEmpty()) {
            return
        }

        tour = TooltipTour(
            steps = steps,
            labels = labels,
            scrim = scrim,
            showCloseButton = showCloseButton,
            dismissOnOutsideTap = dismissOnOutsideTap,
            onFinish = onFinish,
            onSkip = onSkip,
        )
        stepIndex = 0
        presentCurrentStep()
    }

    /** Advances to the next step, finishing the tour after the last one. */
    public fun next() {
        val currentTour = tour
            ?: return

        if (stepIndex >= currentTour.steps.lastIndex) {
            tour = null
            presentation = null
            currentTour.onFinish()
            return
        }

        stepIndex += 1
        presentCurrentStep()
    }

    /** Goes back one step. Does nothing on the first step. */
    public fun previous() {
        if (tour == null || stepIndex == 0) {
            return
        }

        stepIndex -= 1
        presentCurrentStep()
    }

    /** Abandons the running tour and runs the `onSkip` given to [startTour]. */
    public fun skip() {
        val currentTour = tour
            ?: return

        tour = null
        presentation = null
        currentTour.onSkip()
    }

    /** Dismisses whatever is showing. Abandons a running tour, running the `onSkip` given to [startTour]. */
    public fun dismiss() {
        if (tour != null) {
            skip()
            return
        }

        presentation = null
    }

    internal fun updateAnchor(
        key: String,
        bounds: Rect,
    ) {
        anchors[key] = bounds
    }

    internal fun removeAnchor(key: String) {
        anchors.remove(key)
    }

    /** Anchor bounds translated into the host's own coordinate space, or `null` if not on screen. */
    internal fun anchorBoundsInHost(key: String): Rect? {
        val host = hostBounds
            ?: return null
        val bounds = anchors[key]
            ?: return null

        return bounds.translate(
            translateX = -host.left,
            translateY = -host.top,
        )
    }

    private fun presentCurrentStep() {
        val currentTour = tour
            ?: return
        val step = currentTour.steps[stepIndex]

        presentation = TooltipPresentation(
            id = nextId++,
            anchor = step.anchor,
            content = step.content,
            title = step.title,
            indicatorPlacement = step.indicatorPlacement,
            scrim = currentTour.scrim,
            dismissOnOutsideTap = currentTour.dismissOnOutsideTap,
            onCloseClick = if (currentTour.showCloseButton) {
                { skip() }
            } else {
                null
            },
            closeContentDescription = currentTour.labels.close,
            cover = step.cover,
            footer = tourFooter(
                tour = currentTour,
                index = stepIndex,
            ),
        )
    }

    private fun tourFooter(
        tour: TooltipTour,
        index: Int,
    ): @Composable TooltipFooterScope.() -> Unit =
        {
            val isLast = index == tour.steps.lastIndex

            // `1 of 1` says nothing, so a single-step tour gets no counter — but the weight has to
            // stay, or the actions stop being trailing-aligned.
            if (tour.steps.size > 1) {
                StepCounter(
                    currentStep = index + 1,
                    totalSteps = tour.steps.size,
                    modifier = Modifier.weight(weight = 1f),
                    separator = tour.labels.stepSeparator,
                )
            } else {
                Spacer(modifier = Modifier.weight(weight = 1f))
            }

            val skipLabel = tour.labels.skip
            if (skipLabel != null && !isLast) {
                Action(
                    label = skipLabel,
                    onClick = { skip() },
                    variant = TooltipFooterActionVariant.Secondary,
                )
            }

            Action(
                label = if (isLast) tour.labels.done else tour.labels.next,
                onClick = { next() },
            )
        }
}

/**
 * Current [LemonadeTooltipState]. Throws unless read inside a [LemonadeTooltipHost].
 */
public val LocalLemonadeTooltipState: ProvidableCompositionLocal<LemonadeTooltipState> =
    staticCompositionLocalOf {
        error("No LemonadeTooltipState provided. Wrap your content with LemonadeTooltipHost.")
    }

/**
 * Registers this element as a tooltip anchor under [key], so [LemonadeTooltipState.show] and
 * [LemonadeTooltipState.startTour] can point at it.
 *
 * Keys are global to the enclosing [LemonadeTooltipHost]. The registration is removed when the
 * element leaves composition, so a tooltip aimed at an anchor that is not on screen simply does not
 * render — which is what lets a tour walk across several screens.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Button(
 *     label = "Takings",
 *     onClick = { },
 *     modifier = Modifier.lemonadeTooltipAnchor(key = "takings"),
 * )
 * ```
 */
public fun Modifier.lemonadeTooltipAnchor(key: String): Modifier =
    composed {
        val state = LocalLemonadeTooltipState.current

        DisposableEffect(key) {
            onDispose { state.removeAnchor(key = key) }
        }

        onGloballyPositioned { coordinates ->
            state.updateAnchor(
                key = key,
                bounds = coordinates.boundsInWindow(),
            )
        }
    }

/**
 * Renders the tooltip overlay — the scrim and the anchored tooltip — above your content.
 *
 * Supplies [LemonadeTooltipState] to the composition tree. Place this at the root of your app,
 * wrapping your main content.
 *
 * ## Usage
 * ```kotlin
 * LemonadeTooltipHost {
 *     // Your app content
 * }
 * ```
 */
@Composable
public fun LemonadeTooltipHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val tooltipState = remember { LemonadeTooltipState() }

    CompositionLocalProvider(LocalLemonadeTooltipState provides tooltipState) {
        Box(
            modifier = modifier.onGloballyPositioned { coordinates ->
                tooltipState.hostBounds = coordinates.boundsInWindow()
            },
        ) {
            content()

            val presentation = tooltipState.presentation

            // The presentation is cleared the moment a tooltip is dismissed, so it has to be held
            // on to for the exit animation to have anything left to fade out.
            var retained by remember { mutableStateOf<TooltipPresentation?>(null) }
            if (presentation != null) {
                retained = presentation
            }

            val visibleState = remember { MutableTransitionState(initialState = false) }
            visibleState.targetState = presentation != null

            // The presentation that should be on screen right now. Still set while the exit
            // animation runs, hence currentState as well as targetState.
            val shown = retained.takeIf {
                visibleState.currentState || visibleState.targetState
            }
            val hostBounds = tooltipState.hostBounds
            val anchor = shown?.let { current ->
                tooltipState.anchorBoundsInHost(key = current.anchor)
            }

            if (shown != null && hostBounds != null && anchor != null) {
                TooltipOverlay(
                    state = tooltipState,
                    presentation = shown,
                    anchor = anchor,
                    hostSize = Size(
                        width = hostBounds.width,
                        height = hostBounds.height,
                    ),
                    visibleState = visibleState,
                )
            }
        }
    }
}

/** Draws the scrim behind a tooltip, punching out the anchor for a spotlight. */
private fun Modifier.drawTooltipScrim(
    scrim: TooltipScrim,
    color: Color,
    spotlight: Rect,
    spotlightRadius: Float,
): Modifier =
    drawBehind {
        when (scrim) {
            TooltipScrim.None -> Unit

            TooltipScrim.Dim -> drawRect(color = color)

            TooltipScrim.Spotlight -> {
                val cutout = Path()
                    .apply {
                        addRoundRect(
                            roundRect = RoundRect(
                                rect = spotlight,
                                cornerRadius = CornerRadius(
                                    x = spotlightRadius,
                                    y = spotlightRadius,
                                ),
                            ),
                        )
                    }
                clipPath(
                    path = cutout,
                    clipOp = ClipOp.Difference,
                ) {
                    drawRect(color = color)
                }
            }
        }
    }

@Composable
private fun TooltipOverlay(
    state: LemonadeTooltipState,
    presentation: TooltipPresentation,
    anchor: Rect,
    hostSize: Size,
    visibleState: MutableTransitionState<Boolean>,
) {
    val colors = LocalColors.current
    val opacities = LocalOpacities.current
    val density = LocalDensity.current

    // The scrim only fades — scaling it with the tooltip would sweep the dimming across the screen.
    val scrimProgress by animateFloatAsState(
        targetValue = if (visibleState.targetState) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (visibleState.targetState) {
                TOOLTIP_SCRIM_FADE_IN_MILLIS
            } else {
                TOOLTIP_EXIT_FADE_MILLIS
            },
        ),
        label = "tooltipScrim",
    )

    val scrimSource = colors.background.bgAlwaysDark
    val scrimColor = scrimSource.copy(
        alpha = scrimSource.alpha * opacities.base.opacity40 * scrimProgress,
    )
    val spotlight = anchor.inflate(delta = with(density) { TooltipSpotlightPadding.toPx() })
    val spotlightRadius = with(density) { TooltipSpotlightRadius.toPx() }

    // The tooltip is composed with its final indicator before it is measured, so the placement is
    // resolved from the anchor and the host alone — neither of which needs the measured size.
    val edge = resolveTooltipEdge(
        anchor = anchor,
        hostSize = hostSize,
        forcedPlacement = presentation.indicatorPlacement,
    )
    val placement = presentation.indicatorPlacement
        ?: resolveIndicatorPlacement(
            anchor = anchor,
            hostSize = hostSize,
            edge = edge,
            density = density,
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(
                key1 = presentation.id,
                key2 = presentation.dismissOnOutsideTap,
            ) {
                // The tap is always swallowed so the UI underneath cannot be acted on while a
                // tooltip is up; whether it also dismisses is the caller's choice.
                detectTapGestures {
                    if (presentation.dismissOnOutsideTap) {
                        state.dismiss()
                    }
                }
            }.drawTooltipScrim(
                scrim = presentation.scrim,
                color = scrimColor,
                spotlight = spotlight,
                spotlightRadius = spotlightRadius,
            ),
    ) {
        Layout(
            content = {
                // AnimatedVisibility wraps the tooltip rather than the whole overlay so that
                // scaleIn's transform origin resolves against the tooltip's own bounds, letting it
                // grow out of its indicator instead of the centre of the screen.
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = tween(durationMillis = TOOLTIP_ENTER_FADE_MILLIS)) +
                        scaleIn(
                            animationSpec = spring(
                                dampingRatio = TOOLTIP_ENTER_DAMPING_RATIO,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            initialScale = TOOLTIP_ENTER_INITIAL_SCALE,
                            transformOrigin = placement.transformOrigin(density = density),
                        ),
                    exit = fadeOut(animationSpec = tween(durationMillis = TOOLTIP_EXIT_FADE_MILLIS)),
                ) {
                    Box(
                        // Swallow taps on the tooltip itself so they never reach the dismiss handler.
                        modifier = Modifier.pointerInput(presentation.id) {
                            detectTapGestures { }
                        },
                    ) {
                        LemonadeUi.Tooltip(
                            content = presentation.content,
                            title = presentation.title,
                            indicatorPlacement = placement,
                            onCloseClick = presentation.onCloseClick,
                            closeContentDescription = presentation.closeContentDescription,
                            cover = presentation.cover,
                            footer = presentation.footer,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) { measurables, constraints ->
            val placeable = measurables
                .first()
                .measure(
                    constraints = constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                    ),
                )
            val offset = resolveTooltipOffset(
                anchor = anchor,
                hostSize = Size(
                    width = constraints.maxWidth.toFloat(),
                    height = constraints.maxHeight.toFloat(),
                ),
                tooltipSize = Size(
                    width = placeable.width.toFloat(),
                    height = placeable.height.toFloat(),
                ),
                placement = placement,
                density = this,
            )

            layout(
                width = constraints.maxWidth,
                height = constraints.maxHeight,
            ) {
                placeable.place(position = offset)
            }
        }
    }
}

/**
 * Pivot for the entry scale, placed at the indicator.
 *
 * The tooltip then grows out of the element it points at rather than out of its own centre.
 */
private fun TooltipIndicatorPlacement.transformOrigin(density: Density): TransformOrigin {
    // Derived from indicatorCenterOffset rather than restated, so the pivot cannot drift from where
    // the indicator is actually drawn. The width is fixed, so the fraction is exact.
    val tooltipWidth = with(density) { TooltipWidth.toPx() }
    val alongTopOrBottom = indicatorCenterOffset(
        placement = this,
        edgeLength = tooltipWidth,
        density = density,
    ) / tooltipWidth

    // A left or right indicator's position along its edge cannot be turned into a fraction here: the
    // tooltip's height depends on its content and is not known until it has been measured. Those
    // placements pivot on the corner nearest the indicator instead, which over the entry spring does
    // not read differently.
    val alongLeftOrRight = when (alignment) {
        TooltipIndicatorAlignment.Start -> 0f
        TooltipIndicatorAlignment.Center -> 0.5f
        TooltipIndicatorAlignment.End -> 1f
    }

    return when (edge) {
        TooltipIndicatorEdge.Top -> TransformOrigin(
            pivotFractionX = alongTopOrBottom,
            pivotFractionY = 0f,
        )

        TooltipIndicatorEdge.Bottom -> TransformOrigin(
            pivotFractionX = alongTopOrBottom,
            pivotFractionY = 1f,
        )

        TooltipIndicatorEdge.Left -> TransformOrigin(
            pivotFractionX = 0f,
            pivotFractionY = alongLeftOrRight,
        )

        TooltipIndicatorEdge.Right -> TransformOrigin(
            pivotFractionX = 1f,
            pivotFractionY = alongLeftOrRight,
        )

        TooltipIndicatorEdge.None -> TransformOrigin.Center
    }
}

/**
 * Which edge of the tooltip its indicator protrudes from.
 *
 * That is also which side of [anchor] the tooltip sits on. A caller that forces a placement is
 * choosing the side too — an indicator drawn on top of the body only makes sense with the body below
 * the anchor. Without one, and for [TooltipIndicatorPlacement.None] which names no edge, the tooltip
 * goes below an anchor in the top half of the host and above one in the bottom half. Never returns
 * [TooltipIndicatorEdge.None]: the tooltip is always on one side of its anchor, even when no
 * indicator is drawn.
 */
internal fun resolveTooltipEdge(
    anchor: Rect,
    hostSize: Size,
    forcedPlacement: TooltipIndicatorPlacement?,
): TooltipIndicatorEdge {
    val forcedEdge = forcedPlacement?.edge
    if (forcedEdge != null && forcedEdge != TooltipIndicatorEdge.None) {
        return forcedEdge
    }

    return if (anchor.center.y < hostSize.height / 2f) {
        TooltipIndicatorEdge.Top
    } else {
        TooltipIndicatorEdge.Bottom
    }
}

/**
 * Picks the placement whose indicator lands closest to the centre of [anchor].
 *
 * The tooltip is kept inside the host first. The indicator has only three possible positions, so near
 * an edge the left or right variant reaches an anchor that the centred one cannot.
 *
 * Only the top and bottom placements are candidates: choosing to sit beside an anchor rather than
 * above or below it is a deliberate call, so the left and right ones have to be asked for.
 */
internal fun resolveIndicatorPlacement(
    anchor: Rect,
    hostSize: Size,
    edge: TooltipIndicatorEdge,
    density: Density,
): TooltipIndicatorPlacement {
    val candidates = if (edge == TooltipIndicatorEdge.Bottom) {
        listOf(
            TooltipIndicatorPlacement.BottomCenter,
            TooltipIndicatorPlacement.BottomLeft,
            TooltipIndicatorPlacement.BottomRight,
        )
    } else {
        listOf(
            TooltipIndicatorPlacement.TopCenter,
            TooltipIndicatorPlacement.TopLeft,
            TooltipIndicatorPlacement.TopRight,
        )
    }

    val tooltipWidth = with(density) { TooltipWidth.toPx() }
    val margin = with(density) { TooltipHostMargin.toPx() }
    val anchorCenterX = anchor.center.x
    val maxX = (hostSize.width - tooltipWidth - margin).coerceAtLeast(minimumValue = margin)

    var best = candidates.first()
    var bestError = Float.MAX_VALUE
    candidates.forEach { candidate ->
        val offset = indicatorCenterOffset(
            placement = candidate,
            edgeLength = tooltipWidth,
            density = density,
        )
        val x = (anchorCenterX - offset).coerceIn(
            minimumValue = margin,
            maximumValue = maxX,
        )
        val error = abs(x + offset - anchorCenterX)
        if (error < bestError) {
            bestError = error
            best = candidate
        }
    }

    return best
}

/**
 * Where to place the tooltip inside the host so its indicator points at [anchor].
 *
 * The result is kept inside the host's margins. One rule, applied per edge: along the indicator's own
 * axis the tooltip clears the corresponding anchor bound by [TooltipAnchorGap]; across it the
 * indicator lines up with the anchor's centre. The indicator tip sits on the tooltip's own bounds, so
 * the gap is measured from those.
 *
 * The edge is resolved here rather than passed in, so it cannot contradict [placement], and so
 * [TooltipIndicatorPlacement.None] — which names no edge — still lands above or below its anchor.
 */
internal fun resolveTooltipOffset(
    anchor: Rect,
    hostSize: Size,
    tooltipSize: Size,
    placement: TooltipIndicatorPlacement,
    density: Density,
): IntOffset {
    val edge = resolveTooltipEdge(
        anchor = anchor,
        hostSize = hostSize,
        forcedPlacement = placement,
    )
    val margin = with(density) { TooltipHostMargin.toPx() }
    val gap = with(density) { TooltipAnchorGap.toPx() }

    val besideAnchor = edge == TooltipIndicatorEdge.Left || edge == TooltipIndicatorEdge.Right
    val alongIndicatorEdge = indicatorCenterOffset(
        placement = placement,
        edgeLength = if (besideAnchor) tooltipSize.height else tooltipSize.width,
        density = density,
    )
    val centredOnAnchorX = anchor.center.x - alongIndicatorEdge
    val centredOnAnchorY = anchor.center.y - alongIndicatorEdge

    val raw = when (edge) {
        TooltipIndicatorEdge.Top,
        TooltipIndicatorEdge.None,
        -> Offset(
            x = centredOnAnchorX,
            y = anchor.bottom + gap,
        )

        TooltipIndicatorEdge.Bottom -> Offset(
            x = centredOnAnchorX,
            y = anchor.top - gap - tooltipSize.height,
        )

        TooltipIndicatorEdge.Left -> Offset(
            x = anchor.right + gap,
            y = centredOnAnchorY,
        )

        TooltipIndicatorEdge.Right -> Offset(
            x = anchor.left - gap - tooltipSize.width,
            y = centredOnAnchorY,
        )
    }

    val maxX = (hostSize.width - tooltipSize.width - margin).coerceAtLeast(minimumValue = margin)
    val maxY = (hostSize.height - tooltipSize.height - margin).coerceAtLeast(minimumValue = margin)

    return IntOffset(
        x = raw.x
            .coerceIn(
                minimumValue = margin,
                maximumValue = maxX,
            ).roundToInt(),
        y = raw.y
            .coerceIn(
                minimumValue = margin,
                maximumValue = maxY,
            ).roundToInt(),
    )
}
