package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.zIndex
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIconButtonShape
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TopBarAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * State holder for [TopBar][LemonadeUi.TopBar] that manages the collapse/expand
 * behavior based on scroll events from external scrollable content.
 *
 * Use [rememberTopBarState] to create and remember an instance.
 *
 * @param coroutineScope The [CoroutineScope] used to launch scroll-offset animations.
 * @param startCollapsed When `true`, the top bar starts in the collapsed state.
 * @param lockGestureAnimation When `true`, scroll gestures will not collapse or expand the
 *        top bar; only programmatic calls to [collapse] and [expand] will work.
 * @see rememberTopBarState
 */
@Stable
public class TopBarState internal constructor(
    private val coroutineScope: CoroutineScope,
    private val startCollapsed: Boolean = false,
    lockGestureAnimation: Boolean = false,
) {
    private var lockGestureAnimation by mutableStateOf(lockGestureAnimation)

    internal val isPermanentlyCollapsed: Boolean by derivedStateOf {
        startCollapsed && lockGestureAnimation
    }

    // A fraction, not a pixel offset: the collapsable height isn't known until the layout has
    // measured, so a fraction is the only position meaningful on the first composition, and it
    // survives a remeasure instead of being reset by one.
    private val collapseFraction = Animatable(if (startCollapsed) 1f else 0f)

    internal val scrollOffset: Float
        get() = collapseFraction.value * maxScrollOffset
    internal var maxScrollOffset: Float by mutableFloatStateOf(0f)

    private var scrolledOffsetPx: Float by mutableFloatStateOf(0f)

    /** Whether the top bar is fully collapsed (`collapseProgress == 1f`). */
    public val isCollapsed: Boolean by derivedStateOf {
        collapseProgress == 1f
    }

    /**
     * The current collapse progress as a value between `0f` (fully expanded) and `1f` (fully collapsed).
     */
    public val collapseProgress: Float by derivedStateOf {
        collapseFraction.value
    }

    /**
     * `true` when the scrollable content has moved off its top, `false` when it sits at the top.
     *
     * Tracked from `onPostScroll(consumed)` so it follows the content, not bar-consumed scroll
     * or overscroll. Independent of [collapseProgress] and updated regardless of
     * `lockGestureAnimation`, so a permanently-collapsed bar can still react to scroll — see
     * the `scrolledBackgroundColor` parameter on [TopBar][LemonadeUi.TopBar].
     */
    public val isScrolled: Boolean by derivedStateOf {
        scrolledOffsetPx > 0f
    }

    internal val heightOffset: Float by derivedStateOf {
        -scrollOffset
    }

    /**
     * Animates the top bar to the fully collapsed state.
     * Does nothing if already fully collapsed.
     *
     * @param animationSpec The animation specification to use. Defaults to a 300ms tween.
     */
    public fun collapse(animationSpec: AnimationSpec<Float> = tween(durationMillis = 300)) {
        if (collapseFraction.value < 1f) {
            coroutineScope.launch {
                collapseFraction.animateTo(
                    targetValue = 1f,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    /**
     * Animates the top bar to the fully expanded state.
     * Does nothing if already fully expanded.
     *
     * @param animationSpec The animation specification to use. Defaults to a 300ms tween.
     */
    public fun expand(animationSpec: AnimationSpec<Float> = tween(durationMillis = 300)) {
        if (collapseFraction.value > 0f) {
            coroutineScope.launch {
                collapseFraction.animateTo(
                    targetValue = 0f,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    /**
     * Locks or unlocks scroll-gesture–driven collapse/expand animations.
     *
     * When [locked] is `true`, nested-scroll gestures will not change the collapse state;
     * only programmatic calls to [collapse] and [expand] will have effect.
     *
     * @param locked `true` to lock gesture animations, `false` to unlock.
     */
    public fun setAnimationGesturesLock(locked: Boolean) {
        lockGestureAnimation = locked
    }

    /**
     * [NestedScrollConnection] that captures scroll events from child scrollable content.
     * Apply this to your scrollable content using [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
     *
     * The scroll behavior is:
     * - **Collapse (scroll down)**: Top bar collapses first, then list scrolls
     * - **Expand (scroll up)**: List scrolls to top first, then top bar expands
     *
     * **Note:** When [lockGestureAnimation] is `true`, this connection becomes a no-op—all scroll
     * events pass through to the content unchanged.
     *
     * ## Usage
     * ```kotlin
     * val state = rememberTopBarState()
     *
     * LazyColumn(
     *     modifier = Modifier.nestedScroll(state.nestedScrollConnection)
     * ) {
     *     // content
     * }
     * ```
     */
    public val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (lockGestureAnimation) {
                return Offset.Zero
            }
            val delta = available.y
            if (delta < 0 && scrollOffset < maxScrollOffset) {
                val newOffset = scrollOffset - delta
                val previousOffset = scrollOffset
                val targetOffset = newOffset.coerceIn(
                    minimumValue = 0f,
                    maximumValue = maxScrollOffset,
                )
                coroutineScope.launch {
                    collapseFraction.snapTo(targetValue = targetOffset.asCollapseFraction())
                }
                return Offset(
                    x = 0f,
                    y = previousOffset - targetOffset,
                )
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            accumulateScrolledFade(deltaY = consumed.y)
            // `available.y > 0f` after the child has run means the child stopped consuming an
            // upward gesture — the content has reached its top edge. The delta-accumulator can
            // drift on fling (the down + up passes don't always net to exactly 0 in practice), so
            // hard-pin to 0 here. `isScrolled` flips false the moment the user bottoms out at the
            // top, even after asymmetric scroll/fling sessions.
            if (available.y > 0f) {
                scrolledOffsetPx = 0f
            }
            if (lockGestureAnimation) {
                return Offset.Zero
            }
            val delta = available.y
            if (delta > 0 && scrollOffset > 0f) {
                val newOffset = scrollOffset - delta
                val previousOffset = scrollOffset
                val targetOffset = newOffset.coerceIn(
                    minimumValue = 0f,
                    maximumValue = maxScrollOffset,
                )
                coroutineScope.launch {
                    collapseFraction.snapTo(targetValue = targetOffset.asCollapseFraction())
                }
                return Offset(
                    x = 0f,
                    y = previousOffset - targetOffset,
                )
            }
            return Offset.Zero
        }
    }

    private fun accumulateScrolledFade(deltaY: Float) {
        if (deltaY == 0f) return
        scrolledOffsetPx = (scrolledOffsetPx - deltaY).coerceAtLeast(minimumValue = 0f)
    }

    /** Gestures arrive in pixels; the bar holds its position as a fraction of the collapsable height. */
    private fun Float.asCollapseFraction(): Float = if (maxScrollOffset > 0f) this / maxScrollOffset else 0f
}

// 400ms tween over FastOutSlowInEasing — gentle enough that high-contrast transitions
// (e.g. Color.Transparent → bgDefault on a details screen) read as a fade rather than a snap.
// The default `animateColorAsState` spring (StiffnessMedium = 1500f) completes in ~150ms which
// looked like a hard jump on the bar background.
private val TopBarBackgroundAnimationSpec: AnimationSpec<Color> = tween(
    durationMillis = 400,
    easing = FastOutSlowInEasing,
)

/**
 * Creates and remembers a [TopBarState] instance.
 *
 * @param startCollapsed When `true`, the top bar starts in the collapsed state.
 *        The collapsable content will be hidden and the inline title will be visible immediately.
 *        Defaults to `false`.
 * @param coroutineScope The [CoroutineScope] used for scroll-offset animations. Defaults to
 *        [rememberCoroutineScope].
 * @param lockGestureAnimation When `true`, scroll gestures from nested scrollable content
 *        will not collapse or expand the top bar. The bar can still be collapsed or
 *        expanded programmatically via [TopBarState.collapse] and [TopBarState.expand].
 *        Defaults to `false`.
 * @return A remembered [TopBarState] instance.
 *
 * ## Usage
 * ```kotlin
 * val topBarState = rememberTopBarState(
 *     startCollapsed = true,
 *     lockGestureAnimation = true,
 * )
 *
 * Column {
 *     LemonadeUi.TopBar(
 *         label = "Screen Title",
 *         state = topBarState,
 *     )
 *
 *     LazyColumn(
 *         modifier = Modifier.nestedScroll(topBarState.nestedScrollConnection)
 *     ) {
 *         // Your content here
 *     }
 * }
 * ```
 */

@Composable
public fun rememberTopBarState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startCollapsed: Boolean = false,
    lockGestureAnimation: Boolean = false,
): TopBarState =
    remember(startCollapsed, lockGestureAnimation) {
        TopBarState(
            coroutineScope = coroutineScope,
            startCollapsed = startCollapsed,
            lockGestureAnimation = lockGestureAnimation,
        )
    }

/**
 * Holds the configuration for the navigation action displayed in the leading slot of a [TopBar][LemonadeUi.TopBar].
 *
 * @property navigationAction The visual action type (e.g. [TopBarAction.Back] or [TopBarAction.Close]).
 * @property onNavigationActionClicked Callback invoked when the navigation action button is clicked.
 * @property filled Whether the action button uses a filled background style. Defaults to `false`.
 */
public data class NavigationAction(
    val navigationAction: TopBarAction,
    val onNavigationActionClicked: (() -> Unit),
    val filled: Boolean = false,
)

/**
 * A collapsible top bar component that displays a large title which collapses
 * into a smaller inline title as the user scrolls through content.
 *
 * The TopBar works with external scrollable content through [NestedScrollConnection].
 * Use [rememberTopBarState] to create the state and apply its [TopBarState.nestedScrollConnection]
 * [TopBarState.nestedScrollConnection] to your scrollable content via
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 *
 * ## Usage
 * ```kotlin
 * val topBarState = rememberTopBarState()
 *
 * Column {
 *     LemonadeUi.TopBar(
 *         label = "Screen Title",
 *         state = topBarState,
 *         navigationAction = NavigationAction(
 *             navigationAction = TopBarAction.Back,
 *             onNavigationActionClicked = { /* handle back */ },
 *         ),
 *         trailingSlot = {
 *             LemonadeUi.IconButton(
 *                 icon = LemonadeIcons.Settings,
 *                 contentDescription = "Settings",
 *                 onClick = { /* handle settings */ }
 *             )
 *         },
 *     )
 *
 *     LazyColumn(
 *         modifier = Modifier.nestedScroll(topBarState.nestedScrollConnection)
 *     ) {
 *         items(100) { index ->
 *             Text("Item $index")
 *         }
 *     }
 * }
 * ```
 *
 * @param label The title text displayed in both expanded (large) and collapsed (small - if [collapsedLabel] is null) states.
 * @param collapsedLabel The title text displayed in collapsed (small) state. If not set it will display [label] instead.
 * @param subtitle Optional secondary text displayed below the title in both expanded (left-aligned) and collapsed (centered) states.
 * @param state The [TopBarState] that manages collapse behavior. Create with [rememberTopBarState].
 * @param backgroundColor The background color of the top bar when the scrollable content is at the top.
 * @param scrolledBackgroundColor The background color the top bar fades to once the scrollable
 *        content has moved off the top. Defaults to a fully transparent copy of [backgroundColor];
 *        pass [backgroundColor] to keep the bar opaque at all times and disable the fade.
 *        The fade is driven by [TopBarState.isScrolled] and works even when
 *        `lockGestureAnimation = true`, so a permanently-collapsed bar can sit on a transparent gradient
 *        at the top and switch to a solid theme color while the user scrolls.
 * @param modifier [Modifier] applied to the top bar container.
 * @param navigationAction Optional [NavigationAction] displayed in the leading slot (e.g. back or close button).
 * @param trailingSlot Optional composable displayed at the end of the fixed header (typically action buttons).
 * @param bottomSlot Optional composable displayed below the expanded title. This content remains
 *        visible and acts as a sticky area when fully collapsed.
 */
@Composable
public fun LemonadeUi.TopBar(
    label: String,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    collapsedLabel: String? = null,
    subtitle: String? = null,
    navigationAction: NavigationAction? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    val effectiveBackgroundColor by animateColorAsState(
        targetValue = if (state.isScrolled) scrolledBackgroundColor else backgroundColor,
        animationSpec = TopBarBackgroundAnimationSpec,
        label = "TopBarBackgroundColor",
    )
    CoreTopBar(
        state = state,
        backgroundColor = effectiveBackgroundColor,
        modifier = modifier,
        bottomSlot = bottomSlot,
        fixedHeaderSlot = { fixedHeaderModifier ->
            CoreTopBarContent(
                leadingSlot = {
                    if (navigationAction != null) {
                        CoreTopBarActionContent(
                            navigationAction = navigationAction,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                },
                trailingSlot = trailingSlot,
                label = collapsedLabel ?: label,
                subtitle = subtitle,
                isCollapsed = state.isCollapsed,
                modifier = fixedHeaderModifier
                    .zIndex(zIndex = 1f)
                    .padding(
                        horizontal = LocalSpaces.current.spacing200,
                        vertical = LocalSpaces.current.spacing50,
                    ),
            )
        },
        collapsableSlot = { collapsableSlotModifier ->
            Column(
                modifier = collapsableSlotModifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalSpaces.current.spacing400)
                    .padding(
                        top = LocalSpaces.current.spacing50,
                        bottom = LocalSpaces.current.spacing200,
                    ),
            ) {
                LemonadeUi.Text(
                    text = label,
                    textStyle = LocalTypographies.current.headingLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                if (subtitle != null) {
                    LemonadeUi.Text(
                        text = subtitle,
                        textStyle = LocalTypographies.current.bodySmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        },
    )
}

@Deprecated(
    message = "Use the overload with a scrolledBackgroundColor parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, modifier, state, backgroundColor, " +
            "backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0), collapsedLabel, " +
            "subtitle, navigationAction, trailingSlot, bottomSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.TopBar(
    label: String,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    collapsedLabel: String? = null,
    subtitle: String? = null,
    navigationAction: NavigationAction? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    CoreTopBar(
        state = state,
        backgroundColor = backgroundColor,
        modifier = modifier,
        bottomSlot = bottomSlot,
        fixedHeaderSlot = { fixedHeaderModifier ->
            CoreTopBarContent(
                leadingSlot = {
                    if (navigationAction != null) {
                        CoreTopBarActionContent(
                            navigationAction = navigationAction,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                },
                trailingSlot = trailingSlot,
                label = collapsedLabel ?: label,
                subtitle = subtitle,
                isCollapsed = state.isCollapsed,
                modifier = fixedHeaderModifier
                    .zIndex(zIndex = 1f)
                    .padding(
                        horizontal = LocalSpaces.current.spacing200,
                        vertical = LocalSpaces.current.spacing50,
                    ),
            )
        },
        collapsableSlot = { collapsableSlotModifier ->
            Column(
                modifier = collapsableSlotModifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalSpaces.current.spacing400)
                    .padding(
                        top = LocalSpaces.current.spacing50,
                        bottom = LocalSpaces.current.spacing200,
                    ),
            ) {
                LemonadeUi.Text(
                    text = label,
                    textStyle = LocalTypographies.current.headingLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                if (subtitle != null) {
                    LemonadeUi.Text(
                        text = subtitle,
                        textStyle = LocalTypographies.current.bodySmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        },
    )
}

/**
 * A collapsible top bar with an integrated search field that collapses into the bar
 * as the user scrolls through content.
 *
 * When the search field gains focus, the fixed header (title and actions) animates out,
 * scroll-gesture animations are locked, the top bar expands, and the [bottomSlot] becomes visible.
 * Tapping the leading icon clears focus and restores the default state.
 *
 * Use [rememberTopBarState] to create the state and apply its [TopBarState.nestedScrollConnection]
 * to your scrollable content via
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 *
 * ## Usage
 * ```kotlin
 * val topBarState = rememberTopBarState()
 * var query by remember { mutableStateOf("") }
 *
 * Column {
 *     LemonadeUi.TopBar(
 *         label = "Search",
 *         state = topBarState,
 *         searchInput = query,
 *         onSearchChanged = { query = it },
 *         expandedLabel = "Discover",
 *         navigationAction = NavigationAction(
 *             navigationAction = TopBarAction.Back,
 *             onNavigationActionClicked = { /* handle back */ },
 *         ),
 *     )
 *
 *     LazyColumn(
 *         modifier = Modifier.nestedScroll(topBarState.nestedScrollConnection)
 *     ) {
 *         // search results
 *     }
 * }
 * ```
 *
 * @param label The title text displayed in the fixed header.
 * @param searchInput The current search query text.
 * @param onSearchChanged Callback invoked when the search query changes.
 * @param modifier [Modifier] applied to the top bar container.
 * @param state The [TopBarState] that manages collapse behavior. Create with [rememberTopBarState].
 * @param backgroundColor The background color of the top bar when the scrollable content is at the top.
 * @param scrolledBackgroundColor The background color the top bar fades to once the scrollable
 *        content has moved off the top. Defaults to a fully transparent copy of [backgroundColor];
 *        pass [backgroundColor] to keep the bar opaque at all times and disable the fade.
 *        See [TopBarState.isScrolled].
 * @param expandedLabel Optional large title displayed above the search field in the collapsable area.
 * @param subtitle Optional secondary text displayed below the title in both expanded (below [expandedLabel], left-aligned) and collapsed (below [label], centered) states.
 * @param searchPlaceholder Optional placeholder text shown in the search field while it is empty.
 * @param navigationAction Optional [NavigationAction] displayed in the leading slot (e.g. back or close button).
 * @param trailingSlot Optional composable displayed at the end of the fixed header.
 * @param bottomSlot Optional composable shown below the search field when focused
 *        (e.g. search suggestions or filters).
 */
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    expandedLabel: String? = null,
    subtitle: String? = null,
    searchPlaceholder: String? = null,
    navigationAction: NavigationAction? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchDismissRequester = remember { FocusRequester() }
    var isSearchFocused by remember {
        mutableStateOf(false)
    }
    val effectiveBackgroundColor by animateColorAsState(
        targetValue = if (state.isScrolled) scrolledBackgroundColor else backgroundColor,
        animationSpec = TopBarBackgroundAnimationSpec,
        label = "TopBarBackgroundColor",
    )
    CoreTopBar(
        state = state,
        backgroundColor = effectiveBackgroundColor,
        fixedHeaderSlot = { fixedHeaderModifier ->
            AnimatedContent(
                modifier = fixedHeaderModifier
                    .zIndex(zIndex = 1f)
                    .padding(
                        horizontal = LocalSpaces.current.spacing200,
                        vertical = LocalSpaces.current.spacing50,
                    ),
                targetState = isSearchFocused,
                transitionSpec = { expandVertically() togetherWith shrinkVertically() },
                content = { searchFocused ->
                    if (!searchFocused) {
                        CoreTopBarContent(
                            leadingSlot = {
                                if (navigationAction != null) {
                                    CoreTopBarActionContent(
                                        navigationAction = navigationAction,
                                        modifier = Modifier.matchParentSize(),
                                    )
                                }
                            },
                            isCollapsed = state.isCollapsed,
                            trailingSlot = trailingSlot,
                            label = label,
                            subtitle = subtitle,
                        )
                    }
                },
            )
        },
        collapsableSlot = { collapsableSlotModifier ->
            Column(
                modifier = Modifier
                    .clipToBounds()
                    .then(other = collapsableSlotModifier)
                    .fillMaxWidth()
                    .padding(horizontal = LocalSpaces.current.spacing400)
                    .padding(
                        top = LocalSpaces.current.spacing50,
                        bottom = LocalSpaces.current.spacing200,
                    ),
            ) {
                SearchFocusDecoy(focusRequester = searchDismissRequester, claimFocusOnEntry = true)

                AnimatedContent(
                    targetState = expandedLabel != null && !isSearchFocused,
                    transitionSpec = { expandVertically() togetherWith shrinkVertically() + fadeOut() },
                    content = { shouldShow ->
                        if (shouldShow) {
                            Column(
                                modifier = Modifier.padding(bottom = LocalSpaces.current.spacing100),
                            ) {
                                LemonadeUi.Text(
                                    text = expandedLabel.orEmpty(),
                                    textStyle = LocalTypographies.current.headingLarge,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                )
                                if (subtitle != null) {
                                    LemonadeUi.Text(
                                        text = subtitle,
                                        textStyle = LocalTypographies.current.bodySmallRegular,
                                        color = LocalColors.current.content.contentSecondary,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxWidth())
                        }
                    },
                )

                CoreSearchField(
                    input = searchInput,
                    onInputChanged = onSearchChanged,
                    placeholder = searchPlaceholder,
                    leadingIcon = if (isSearchFocused) {
                        LemonadeIcons.ArrowLeft
                    } else {
                        LemonadeIcons.Search
                    },
                    // Only the back arrow is clickable — the resting magnifier stays tap-through
                    // so tapping it focuses the field.
                    onLeadingIconClicked = if (isSearchFocused) {
                        {
                            keyboardController?.hide()
                            searchDismissRequester.requestFocus()
                        }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss { searchDismissRequester.requestFocus() }
                        .onFocusChanged { focusState ->
                            isSearchFocused = focusState.isFocused
                            state.setAnimationGesturesLock(locked = focusState.isFocused)
                            if (focusState.isFocused) {
                                state.expand()
                            }
                        },
                )
            }
        },
        bottomSlot = bottomSlot?.let {
            {
                AnimatedContent(
                    targetState = isSearchFocused,
                    transitionSpec = { expandVertically() togetherWith shrinkVertically() },
                    content = { searchFocused ->
                        if (searchFocused) {
                            bottomSlot()
                        }
                    },
                )
            }
        },
        modifier = modifier,
    )
}

@Deprecated(
    message = "Use the overload with a scrolledBackgroundColor parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, searchInput, onSearchChanged, modifier, state, backgroundColor, " +
            "backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0), expandedLabel, " +
            "subtitle, navigationAction, trailingSlot, bottomSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    expandedLabel: String? = null,
    subtitle: String? = null,
    navigationAction: NavigationAction? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    TopBar(
        label = label,
        searchInput = searchInput,
        onSearchChanged = onSearchChanged,
        modifier = modifier,
        state = state,
        backgroundColor = backgroundColor,
        scrolledBackgroundColor = backgroundColor,
        expandedLabel = expandedLabel,
        subtitle = subtitle,
        searchPlaceholder = null,
        navigationAction = navigationAction,
        trailingSlot = trailingSlot,
        bottomSlot = bottomSlot,
    )
}

@Deprecated(
    message = "Use the overload with a searchPlaceholder parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, searchInput, onSearchChanged, modifier, state, backgroundColor, " +
            "scrolledBackgroundColor, expandedLabel, subtitle, null, navigationAction, trailingSlot, " +
            "bottomSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    expandedLabel: String? = null,
    subtitle: String? = null,
    navigationAction: NavigationAction? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    TopBar(
        label = label,
        searchInput = searchInput,
        onSearchChanged = onSearchChanged,
        modifier = modifier,
        state = state,
        backgroundColor = backgroundColor,
        scrolledBackgroundColor = scrolledBackgroundColor,
        expandedLabel = expandedLabel,
        subtitle = subtitle,
        searchPlaceholder = null,
        navigationAction = navigationAction,
        trailingSlot = trailingSlot,
        bottomSlot = bottomSlot,
    )
}

/**
 * A top bar with a large left-aligned title, optional subheading,
 * and trailing action slot — designed for top-level screens without navigation.
 *
 * When [bottomSlot] is provided, the title row scrolls away on scroll and the
 * bottom slot becomes sticky. When [bottomSlot] is `null`, the title is fixed
 * and does not scroll.
 *
 * Use [rememberTopBarState] to create the state and apply its [TopBarState.nestedScrollConnection]
 * to your scrollable content via
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 *
 * ## Usage
 * ```kotlin
 * // Fixed title (no scroll)
 * LemonadeUi.TopBar(
 *     label = "Home",
 *     subheading = "Welcome back",
 *     trailingSlot = {
 *         LemonadeUi.IconButton(
 *             icon = LemonadeIcons.Bell,
 *             contentDescription = "Notifications",
 *             onClick = { /* handle */ }
 *         )
 *     },
 * )
 *
 * // Title scrolls away, slot sticks
 * val state = rememberTopBarState()
 * LemonadeUi.TopBar(
 *     label = "Browse",
 *     subheading = null,
 *     state = state,
 *     trailingSlot = { /* actions */ },
 *     bottomSlot = {
 *         // Sticky content (e.g. filters, tabs)
 *     },
 * )
 * LazyColumn(
 *     modifier = Modifier.nestedScroll(state.nestedScrollConnection)
 * ) { /* content */ }
 * ```
 *
 * @param label The large title text displayed in the heading.
 * @param subheading Optional secondary text displayed below the title.
 * @param modifier [Modifier] applied to the top bar container.
 * @param state The [TopBarState] that manages scroll behavior. Create with [rememberTopBarState].
 * @param backgroundColor The background color of the top bar when the scrollable content is at the top.
 * @param scrolledBackgroundColor The background color the top bar fades to once the scrollable
 *        content has moved off the top. Defaults to a fully transparent copy of [backgroundColor];
 *        pass [backgroundColor] to keep the bar opaque at all times and disable the fade.
 *        See [TopBarState.isScrolled].
 * @param trailingSlot Optional composable displayed at the end of the title row (typically action buttons).
 * @param bottomSlot Optional composable displayed below the title. When provided, the title scrolls
 *        away and this slot becomes sticky. When `null`, the title is fixed.
 */
@Composable
public fun LemonadeUi.TopBar(
    label: String,
    subheading: String?,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    val effectiveBackgroundColor by animateColorAsState(
        targetValue = if (state.isScrolled) scrolledBackgroundColor else backgroundColor,
        animationSpec = TopBarBackgroundAnimationSpec,
        label = "TopBarBackgroundColor",
    )
    CoreTopBar(
        state = state,
        backgroundColor = effectiveBackgroundColor,
        modifier = modifier,
        fixedHeaderSlot = { fixedHeaderModifier ->
            if (bottomSlot != null) {
                Spacer(modifier = fixedHeaderModifier)
            } else {
                CompactLargeTopBarHeading(
                    label = label,
                    subheading = subheading,
                    trailingSlot = trailingSlot,
                    modifier = fixedHeaderModifier
                        .zIndex(zIndex = 1f),
                )
            }
        },
        collapsableSlot = { collapsableSlotModifier ->
            if (bottomSlot != null) {
                CompactLargeTopBarHeading(
                    label = label,
                    subheading = subheading,
                    trailingSlot = trailingSlot,
                    modifier = collapsableSlotModifier,
                )
            } else {
                Spacer(modifier = collapsableSlotModifier)
            }
        },
        bottomSlot = bottomSlot,
    )
}

@Deprecated(
    message = "Use the overload with a scrolledBackgroundColor parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, subheading, modifier, state, backgroundColor, " +
            "backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0), trailingSlot, bottomSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.TopBar(
    label: String,
    subheading: String?,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
    bottomSlot: @Composable (BoxScope.() -> Unit)? = null,
) {
    CoreTopBar(
        state = state,
        backgroundColor = backgroundColor,
        modifier = modifier,
        fixedHeaderSlot = { fixedHeaderModifier ->
            if (bottomSlot != null) {
                Spacer(modifier = fixedHeaderModifier)
            } else {
                CompactLargeTopBarHeading(
                    label = label,
                    subheading = subheading,
                    trailingSlot = trailingSlot,
                    modifier = fixedHeaderModifier
                        .zIndex(zIndex = 1f),
                )
            }
        },
        collapsableSlot = { collapsableSlotModifier ->
            if (bottomSlot != null) {
                CompactLargeTopBarHeading(
                    label = label,
                    subheading = subheading,
                    trailingSlot = trailingSlot,
                    modifier = collapsableSlotModifier,
                )
            } else {
                Spacer(modifier = collapsableSlotModifier)
            }
        },
        bottomSlot = bottomSlot,
    )
}

/**
 * A top bar with a large left-aligned title, optional subheading,
 * trailing action slot, and an integrated search field — designed for top-level
 * screens with search capability. The title remains fixed while the search area
 * collapses on scroll.
 *
 * On scroll, the search field collapses away while the title remains fixed.
 * When the search field gains focus, the title animates out and only the search
 * field remains visible. Tapping the search leading icon clears focus and restores
 * the title.
 *
 * Use [rememberTopBarState] to create the state and apply its [TopBarState.nestedScrollConnection]
 * to your scrollable content via
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 *
 * ## Usage
 * ```kotlin
 * val state = rememberTopBarState()
 * var query by remember { mutableStateOf("") }
 *
 * Column {
 *     LemonadeUi.TopBar(
 *         label = "Discover",
 *         subheading = null,
 *         searchInput = query,
 *         onSearchChanged = { query = it },
 *         state = state,
 *     )
 *
 *     LazyColumn(
 *         modifier = Modifier.nestedScroll(state.nestedScrollConnection)
 *     ) {
 *         // search results
 *     }
 * }
 * ```
 *
 * @param label The large title text displayed in the fixed heading.
 * @param subheading Optional secondary text displayed below the title.
 * @param searchInput The current search query text.
 * @param onSearchChanged Callback invoked when the search query changes.
 * @param modifier [Modifier] applied to the top bar container.
 * @param state The [TopBarState] that manages scroll behavior. Create with [rememberTopBarState].
 * @param backgroundColor The background color of the top bar when the scrollable content is at the top.
 * @param scrolledBackgroundColor The background color the top bar fades to once the scrollable
 *        content has moved off the top. Defaults to a fully transparent copy of [backgroundColor];
 *        pass [backgroundColor] to keep the bar opaque at all times and disable the fade.
 *        See [TopBarState.isScrolled].
 * @param searchPlaceholder Optional placeholder text shown in the search field while it is empty.
 * @param trailingSlot Optional composable displayed at the end of the title row (typically action buttons).
 */
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    subheading: String?,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    searchPlaceholder: String? = null,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchDismissRequester = remember { FocusRequester() }
    var isSearchFocused by remember {
        mutableStateOf(false)
    }
    val effectiveBackgroundColor by animateColorAsState(
        targetValue = if (state.isScrolled) scrolledBackgroundColor else backgroundColor,
        animationSpec = TopBarBackgroundAnimationSpec,
        label = "TopBarBackgroundColor",
    )
    CoreTopBar(
        state = state,
        backgroundColor = effectiveBackgroundColor,
        modifier = modifier,
        fixedHeaderSlot = { fixedHeaderModifier ->
            AnimatedContent(
                modifier = fixedHeaderModifier
                    .zIndex(zIndex = 1f),
                targetState = isSearchFocused,
                transitionSpec = { expandVertically() togetherWith shrinkVertically() },
                content = { searchFocused ->
                    if (!searchFocused) {
                        CompactLargeTopBarHeading(
                            label = label,
                            subheading = subheading,
                            trailingSlot = trailingSlot,
                        )
                    }
                },
            )
        },
        collapsableSlot = { collapsableSlotModifier ->
            Box(modifier = collapsableSlotModifier) {
                SearchFocusDecoy(focusRequester = searchDismissRequester, claimFocusOnEntry = true)

                CoreSearchField(
                    input = searchInput,
                    onInputChanged = onSearchChanged,
                    placeholder = searchPlaceholder,
                    leadingIcon = if (isSearchFocused) {
                        LemonadeIcons.ArrowLeft
                    } else {
                        LemonadeIcons.Search
                    },
                    // Only the back arrow is clickable — the resting magnifier stays tap-through
                    // so tapping it focuses the field.
                    onLeadingIconClicked = if (isSearchFocused) {
                        {
                            keyboardController?.hide()
                            searchDismissRequester.requestFocus()
                        }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LocalSpaces.current.spacing400)
                        .padding(vertical = LocalSpaces.current.spacing300)
                        .clearFocusOnKeyboardDismiss { searchDismissRequester.requestFocus() }
                        .onFocusChanged { focusState ->
                            isSearchFocused = focusState.isFocused
                            state.setAnimationGesturesLock(locked = focusState.isFocused)
                            if (focusState.isFocused) {
                                state.expand()
                            }
                        },
                )
            }
        },
        bottomSlot = null,
    )
}

@Deprecated(
    message = "Use the overload with a scrolledBackgroundColor parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, subheading, searchInput, onSearchChanged, modifier, state, " +
            "backgroundColor, backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0), " +
            "trailingSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    subheading: String?,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    TopBar(
        label = label,
        subheading = subheading,
        searchInput = searchInput,
        onSearchChanged = onSearchChanged,
        modifier = modifier,
        state = state,
        backgroundColor = backgroundColor,
        scrolledBackgroundColor = backgroundColor,
        searchPlaceholder = null,
        trailingSlot = trailingSlot,
    )
}

@Deprecated(
    message = "Use the overload with a searchPlaceholder parameter.",
    replaceWith = ReplaceWith(
        expression = "TopBar(label, subheading, searchInput, onSearchChanged, modifier, state, " +
            "backgroundColor, scrolledBackgroundColor, null, trailingSlot)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
@OptIn(ExperimentalLemonadeComponent::class)
public fun LemonadeUi.TopBar(
    label: String,
    subheading: String?,
    searchInput: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: TopBarState = rememberTopBarState(),
    backgroundColor: Color = LocalColors.current.background.bgDefault,
    scrolledBackgroundColor: Color = backgroundColor.copy(alpha = LocalOpacities.current.base.opacity0),
    trailingSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    TopBar(
        label = label,
        subheading = subheading,
        searchInput = searchInput,
        onSearchChanged = onSearchChanged,
        modifier = modifier,
        state = state,
        backgroundColor = backgroundColor,
        scrolledBackgroundColor = scrolledBackgroundColor,
        searchPlaceholder = null,
        trailingSlot = trailingSlot,
    )
}

@Composable
private fun CoreTopBarActionContent(
    navigationAction: NavigationAction,
    modifier: Modifier = Modifier,
) {
    val action = navigationAction.navigationAction
    val icon = when (action) {
        is TopBarAction.Back -> LemonadeIcons.ArrowLeft
        is TopBarAction.Close -> LemonadeIcons.Times
        is TopBarAction.Custom -> action.icon
    }
    val type = if (navigationAction.filled) {
        LemonadeButtonType.Subtle
    } else {
        LemonadeButtonType.Ghost
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        LemonadeUi.IconButton(
            icon = icon,
            contentDescription = null,
            onClick = navigationAction.onNavigationActionClicked,
            variant = LemonadeButtonVariant.Neutral,
            type = type,
            size = LemonadeButtonSize.Small,
            shape = LemonadeIconButtonShape.Circular,
        )
    }
}

@Composable
private fun CoreTopBar(
    state: TopBarState,
    backgroundColor: Color,
    fixedHeaderSlot: @Composable (modifier: Modifier) -> Unit,
    collapsableSlot: @Composable (modifier: Modifier) -> Unit,
    bottomSlot: @Composable (BoxScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    TopBarLayout(
        state = state,
        modifier = Modifier
            .clipToBounds()
            .then(other = modifier)
            .background(color = backgroundColor)
            .displayCutoutPadding()
            .statusBarsPadding(),
        fixedHeaderSlot = fixedHeaderSlot,
        collapsableSlot = collapsableSlot,
        bottomStickySlot = bottomSlot?.let { content ->
            { bottomSlotModifier ->
                Box(
                    content = content,
                    modifier = bottomSlotModifier.fillMaxWidth(),
                )
            }
        },
        dividerSlot = { dividerModifier ->
            val dividerAlpha by animateFloatAsState(
                targetValue = if (state.collapseProgress == 1f && !state.isPermanentlyCollapsed) {
                    LocalOpacities.current.base.opacity100
                } else {
                    LocalOpacities.current.base.opacity0
                },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing,
                ),
                label = "DividerOpacity",
            )

            Spacer(
                modifier = dividerModifier
                    .alpha(alpha = dividerAlpha)
                    .fillMaxWidth()
                    .background(color = LocalColors.current.border.borderNeutralMedium)
                    .height(height = LocalBorderWidths.current.base.border25),
            )
        },
    )
}

private const val LAYOUT_ID_FIXED_HEADER = "fixed_header"
private const val LAYOUT_ID_DIVIDER = "divider"
private const val LAYOUT_ID_COLLAPSABLE_SLOT = "collapsable_slot"
private const val LAYOUT_ID_BOTTOM_SLOT = "bottom_slot"
private const val LAYOUT_ID_LEADING = "leading"
private const val LAYOUT_ID_LABEL = "label"
private const val LAYOUT_ID_TRAILING = "trailing"

@Composable
internal fun TopBarLayout(
    state: TopBarState,
    fixedHeaderSlot: @Composable (modifier: Modifier) -> Unit,
    dividerSlot: @Composable (modifier: Modifier) -> Unit,
    collapsableSlot: @Composable (modifier: Modifier) -> Unit,
    bottomStickySlot: (@Composable (modifier: Modifier) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            fixedHeaderSlot(
                Modifier.layoutId(layoutId = LAYOUT_ID_FIXED_HEADER),
            )

            dividerSlot(
                Modifier.layoutId(layoutId = LAYOUT_ID_DIVIDER),
            )

            collapsableSlot(
                Modifier
                    .layoutId(layoutId = LAYOUT_ID_COLLAPSABLE_SLOT)
                    // Clip before translating: the slot's top edge is the fixed header's bottom
                    // edge, so the title disappears under the header without the header needing
                    // its own background coat. A second translucent coat on the header would
                    // alpha-stack over the root background and desync the fade above/below it.
                    .clipToBounds()
                    .graphicsLayer {
                        translationY = state.heightOffset
                        alpha = 1f - state.collapseProgress
                    },
            )

            if (bottomStickySlot != null) {
                bottomStickySlot(
                    Modifier.layoutId(layoutId = LAYOUT_ID_BOTTOM_SLOT),
                )
            }
        },
        measurePolicy = { measurables, constraints ->
            val fixedHeaderPlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_FIXED_HEADER }
                .measure(constraints = constraints)

            val collapsablePlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_COLLAPSABLE_SLOT }
                .measure(constraints = constraints)

            val bottomSlotPlaceable = measurables
                .find { measurable -> measurable.layoutId == LAYOUT_ID_BOTTOM_SLOT }
                ?.measure(constraints = constraints)

            val dividerPlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_DIVIDER }
                .measure(constraints = constraints)

            state.maxScrollOffset = collapsablePlaceable.height.toFloat()

            val visibleCollapsablePlaceableHeight =
                (collapsablePlaceable.height + state.heightOffset)
                    .coerceAtLeast(minimumValue = 0f)
                    .roundToInt()

            val totalHeight = fixedHeaderPlaceable.height +
                dividerPlaceable.height +
                visibleCollapsablePlaceableHeight +
                (bottomSlotPlaceable?.height ?: 0)

            layout(
                width = constraints.maxWidth,
                height = totalHeight,
            ) {
                var yPosition = 0

                fixedHeaderPlaceable.placeRelative(
                    x = 0,
                    y = yPosition,
                )
                yPosition += fixedHeaderPlaceable.height

                collapsablePlaceable.placeRelative(
                    x = 0,
                    y = yPosition,
                )
                yPosition += visibleCollapsablePlaceableHeight

                bottomSlotPlaceable?.placeRelative(
                    x = 0,
                    y = yPosition,
                )
                yPosition += bottomSlotPlaceable?.height ?: 0

                dividerPlaceable.placeRelative(
                    x = 0,
                    y = yPosition,
                )
            }
        },
    )
}

@Composable
internal fun CoreTopBarContent(
    leadingSlot: @Composable (BoxScope.() -> Unit)?,
    trailingSlot: @Composable (RowScope.() -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isCollapsed: Boolean,
) {
    val animatedLabelOffsetY by animateDpAsState(
        targetValue = if (isCollapsed) {
            LocalSpaces.current.spacing0
        } else {
            LocalSpaces.current.spacing200
        },
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing,
        ),
    )

    val animatedLabelAlpha by animateFloatAsState(
        targetValue = if (isCollapsed) {
            LocalOpacities.current.base.opacity100
        } else {
            LocalOpacities.current.base.opacity0
        },
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing,
        ),
    )

    Layout(
        modifier = modifier
            .heightIn(min = LocalSizes.current.size1400)
            .padding(horizontal = LocalSpaces.current.spacing100),
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .layoutId(layoutId = LAYOUT_ID_LEADING)
                    .requiredSize(size = LocalSizes.current.size1000),
                content = {
                    leadingSlot?.invoke(this)
                },
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .layoutId(layoutId = LAYOUT_ID_LABEL)
                    .offset(y = animatedLabelOffsetY)
                    .alpha(alpha = animatedLabelAlpha),
            ) {
                LemonadeUi.Text(
                    text = label,
                    textStyle = LocalTypographies.current.headingXXSmall,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                if (subtitle != null) {
                    LemonadeUi.Text(
                        text = subtitle,
                        textStyle = LocalTypographies.current.bodyXSmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                content = { trailingSlot?.invoke(this) },
                modifier = Modifier
                    .layoutId(layoutId = LAYOUT_ID_TRAILING)
                    .requiredSizeIn(
                        maxHeight = LocalSizes.current.size1000,
                        minWidth = LocalSizes.current.size1000,
                    ),
            )
        },
        measurePolicy = { measurables, constraints ->
            val childConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0,
            )

            val leadingPlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_LEADING }
                .measure(constraints = childConstraints)

            val trailingPlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_TRAILING }
                .measure(constraints = childConstraints)

            val labelMaxWidth =
                (constraints.maxWidth - leadingPlaceable.width - trailingPlaceable.width)
                    .coerceAtLeast(minimumValue = 0)

            val labelPlaceable = measurables
                .first { measurable -> measurable.layoutId == LAYOUT_ID_LABEL }
                .measure(
                    constraints = childConstraints.copy(
                        maxWidth = labelMaxWidth,
                    ),
                )

            val fullWidth = constraints.maxWidth
            val contentHeight = maxOf(
                a = leadingPlaceable.height,
                b = trailingPlaceable.height,
                c = labelPlaceable.height,
            )
            val fullHeight = contentHeight.coerceIn(
                minimumValue = constraints.minHeight,
                maximumValue = constraints.maxHeight,
            )

            layout(width = fullWidth, height = fullHeight) {
                leadingPlaceable.placeRelative(
                    x = 0,
                    y = (fullHeight - leadingPlaceable.height) / 2,
                )

                trailingPlaceable.placeRelative(
                    x = fullWidth - trailingPlaceable.width,
                    y = (fullHeight - trailingPlaceable.height) / 2,
                )

                val labelX = ((fullWidth - labelPlaceable.width) / 2)
                    .coerceAtLeast(minimumValue = leadingPlaceable.width)
                    .coerceAtMost(maximumValue = fullWidth - trailingPlaceable.width - labelPlaceable.width)

                labelPlaceable.placeRelative(
                    x = labelX,
                    y = (fullHeight - labelPlaceable.height) / 2,
                )
            }
        },
    )
}

@Composable
private fun CompactLargeTopBarHeading(
    label: String,
    subheading: String?,
    trailingSlot: @Composable (RowScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalSpaces.current.spacing400)
            .padding(bottom = LocalSpaces.current.spacing200),
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalSpaces.current.spacing200,
        ),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = LocalTypographies.current.headingLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
            if (subheading != null) {
                LemonadeUi.Text(
                    text = subheading,
                    textStyle = LocalTypographies.current.bodySmallRegular,
                    color = LocalColors.current.content.contentSecondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
        if (trailingSlot != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                content = { trailingSlot.invoke(this) },
                modifier = Modifier.requiredSizeIn(
                    minWidth = LocalSizes.current.size1000,
                    maxHeight = LocalSizes.current.size1000,
                ),
            )
        }
    }
}

private data class TopBarPreviewData(
    val collapsed: Boolean,
    val action: NavigationAction?,
    val trailingIconCount: Int,
    val longLabel: Boolean = false,
)

private class TopBarPreviewProvider : PreviewParameterProvider<TopBarPreviewData> {
    override val values: Sequence<TopBarPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<TopBarPreviewData> =
        buildList {
            listOf(true, false).forEach { filled ->
                listOf(true, false).forEach { collapsed ->
                    listOf(TopBarAction.Back, TopBarAction.Close).forEach { action ->
                        listOf(0, 1, 2).forEach { trailingIconCount ->
                            listOf(false, true).forEach { longLabel ->
                                add(
                                    element = TopBarPreviewData(
                                        collapsed = collapsed,
                                        action = NavigationAction(
                                            navigationAction = action,
                                            onNavigationActionClicked = { /* nothing */ },
                                            filled = filled,
                                        ),
                                        trailingIconCount = trailingIconCount,
                                        longLabel = longLabel,
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
private fun TopBarPreview(
    @PreviewParameter(TopBarPreviewProvider::class)
    previewData: TopBarPreviewData,
) {
    val label = if (previewData.longLabel) {
        "A very long title that should truncate"
    } else {
        "Label"
    }
    LemonadeUi.TopBar(
        label = label,
        collapsedLabel = label,
        navigationAction = previewData.action,
        state = rememberTopBarState(
            startCollapsed = previewData.collapsed,
        ),
        trailingSlot = previewData.trailingIconCount.toPreviewTrailingSlot(),
    )
}

@LemonadePreview
@Composable
private fun SearchableTopBarPreview(
    @PreviewParameter(TopBarPreviewProvider::class)
    previewData: TopBarPreviewData,
) {
    val label = if (previewData.longLabel) {
        "A very long title that should truncate"
    } else {
        "Label"
    }
    LemonadeUi.TopBar(
        label = label,
        navigationAction = previewData.action,
        searchInput = "Search",
        onSearchChanged = { /* Search Callback */ },
        state = rememberTopBarState(
            startCollapsed = previewData.collapsed,
        ),
        trailingSlot = previewData.trailingIconCount.toPreviewTrailingSlot(),
    )
}

@LemonadePreview
@Composable
private fun SearchableTopBarWithExpandedTitlePreview() {
    LemonadeUi.TopBar(
        label = "Search",
        searchInput = "",
        onSearchChanged = {},
        expandedLabel = "Discover",
        navigationAction = NavigationAction(
            navigationAction = TopBarAction.Back,
            onNavigationActionClicked = {},
        ),
        state = rememberTopBarState(),
    )
}

@LemonadePreview
@Composable
private fun TopBarWithSubtitlePreview(
    @PreviewParameter(TopBarPreviewProvider::class)
    previewData: TopBarPreviewData,
) {
    val label = if (previewData.longLabel) {
        "A very long title that should truncate"
    } else {
        "Title"
    }
    LemonadeUi.TopBar(
        label = label,
        subtitle = "Subheading",
        navigationAction = previewData.action,
        state = rememberTopBarState(
            startCollapsed = previewData.collapsed,
        ),
        trailingSlot = previewData.trailingIconCount.toPreviewTrailingSlot(),
    )
}

@LemonadePreview
@Composable
private fun SearchableTopBarWithSubtitlePreview() {
    LemonadeUi.TopBar(
        label = "Title",
        subtitle = "Subheading",
        searchInput = "",
        onSearchChanged = {},
        expandedLabel = "Title",
        navigationAction = NavigationAction(
            navigationAction = TopBarAction.Back,
            onNavigationActionClicked = {},
        ),
        state = rememberTopBarState(),
    )
}

private fun Int.toPreviewTrailingSlot(): @Composable (RowScope.() -> Unit)? {
    if (this == 0) {
        return null
    }
    val icons = listOf(LemonadeIcons.Bell, LemonadeIcons.EllipsisVertical)
    return {
        icons.take(this@toPreviewTrailingSlot).forEach { icon ->
            LemonadeUi.IconButton(
                icon = icon,
                contentDescription = null,
                onClick = {},
                type = LemonadeButtonType.Ghost,
            )
        }
    }
}

@LemonadePreview
@Composable
private fun CompactLargeTopBarPreview() {
    LemonadeUi.TopBar(
        label = "Home",
        subheading = "Welcome back",
        trailingSlot = {
            LemonadeUi.IconButton(
                icon = LemonadeIcons.Bell,
                contentDescription = null,
                onClick = {},
                type = LemonadeButtonType.Ghost,
            )
        },
    )
}

@LemonadePreview
@Composable
private fun CompactLargeTopBarNoSubheadingPreview() {
    LemonadeUi.TopBar(
        label = "Browse",
        subheading = null,
        trailingSlot = {
            LemonadeUi.IconButton(
                icon = LemonadeIcons.Bell,
                contentDescription = null,
                onClick = {},
                type = LemonadeButtonType.Ghost,
            )
        },
    )
}

@LemonadePreview
@Composable
private fun CompactLargeTopBarSearchPreview() {
    LemonadeUi.TopBar(
        label = "Discover",
        subheading = "Find what you need",
        searchInput = "",
        onSearchChanged = {},
    )
}
