package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Defines the tone of a toast notification.
 */
public enum class ToastVoice {
    Success,
    Error,
    Neutral,

    /**
     * Communicates an ongoing action (e.g. "Downloading your document…"). The leading element is an
     * animated [LemonadeUi.Spinner] instead of a static icon. A loading toast persists until it is
     * explicitly dismissed or replaced — it does not auto-dismiss and cannot be swiped away.
     */
    Loading,
}

/**
 * How long a toast stays up before it auto-dismisses.
 */
public sealed class ToastDuration(internal val millis: kotlin.Long) {
    /** 3 seconds */
    public data object Short : ToastDuration(3_000L)

    /** 6 seconds */
    public data object Medium : ToastDuration(6_000L)

    /** 9 seconds */
    public data object Long : ToastDuration(9_000L)

    /** Duration in milliseconds; must be positive. */
    public data class Custom(val customMillis: kotlin.Long) : ToastDuration(customMillis) {
        init {
            require(customMillis > 0) { "Custom toast duration must be positive, was $customMillis" }
        }
    }
}

internal data class ToastData(
    val label: String,
    val voice: ToastVoice,
    val icon: LemonadeIcons?,
    val duration: ToastDuration,
    val dismissible: Boolean,
    val id: Int,
    val actionLabel: String?,
    val onAction: (() -> Unit)?,
    val paddingValues: PaddingValues?,
)

/**
 * State holder for the Lemonade Toast system. Obtain via [LocalLemonadeToastState].
 */
@Stable
public class LemonadeToastState {
    internal var currentToast by mutableStateOf<ToastData?>(null)
        private set

    private var nextId: Int = 0

    /**
     * Shows a toast, replacing any toast already visible.
     *
     * @param label text message to display
     * @param voice tone of voice — determines icon and icon color. Defaults to [ToastVoice.Neutral]
     * @param icon optional custom icon. Only used when [voice] is [ToastVoice.Neutral]
     * @param duration how long the toast stays up. Defaults to [ToastDuration.Short]
     * @param dismissible whether the user can swipe to dismiss. Defaults to `true`
     */
    @Deprecated(
        message = "Use show() with actionLabel and onAction to support an optional action button.",
        replaceWith = ReplaceWith(
            expression = "show(label, voice, icon, duration, dismissible, actionLabel = null, onAction = null)",
        ),
    )
    public fun show(
        label: String,
        voice: ToastVoice = ToastVoice.Neutral,
        icon: LemonadeIcons? = null,
        duration: ToastDuration = ToastDuration.Short,
        dismissible: Boolean = true,
    ) {
        show(
            label = label,
            voice = voice,
            icon = icon,
            duration = duration,
            dismissible = dismissible,
            actionLabel = null,
            onAction = null,
        )
    }

    /**
     * Shows a toast with an optional action button, replacing any toast already visible.
     *
     * Use [ToastVoice.Loading] to communicate an ongoing action (e.g. "Downloading your document…"). A
     * loading toast shows a spinner and persists until you call [dismiss] or replace it with another
     * [show] — [duration] and [dismissible] are ignored for it.
     *
     * @param label text message to display
     * @param voice tone of voice — determines icon and icon color. Defaults to [ToastVoice.Neutral]
     * @param icon optional custom icon. Only used when [voice] is [ToastVoice.Neutral]
     * @param duration how long the toast stays up. Defaults to [ToastDuration.Short]. Ignored when
     *   [voice] is [ToastVoice.Loading]
     * @param dismissible whether the user can swipe to dismiss. Defaults to `true`. Ignored (forced off)
     *   when [voice] is [ToastVoice.Loading]
     * @param actionLabel optional label for the action button shown at the trailing end of the toast
     * @param onAction optional callback run when the action button is tapped. The button only shows
     *   when both [actionLabel] and [onAction] are non-null
     * @param paddingValues extra space to clear around the toast, e.g. to raise it clear of a screen's
     *   persistent bottom action button, or to inset it from a side element. Bottom/start/end are honored;
     *   a zero edge falls back to the standard margin for that edge (there's no way to tell "unset" from
     *   "explicitly zero" on a plain [PaddingValues]). The top inset is never honored — the toast is always
     *   bottom-anchored with intrinsic height, so it has no visible effect. Defaults to `null` (standard
     *   margins on every edge)
     */
    public fun show(
        label: String,
        voice: ToastVoice = ToastVoice.Neutral,
        icon: LemonadeIcons? = null,
        duration: ToastDuration = ToastDuration.Short,
        dismissible: Boolean = true,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        paddingValues: PaddingValues? = null,
    ) {
        currentToast = ToastData(
            label = label,
            voice = voice,
            icon = icon,
            duration = duration,
            dismissible = dismissible,
            id = nextId++,
            actionLabel = actionLabel,
            onAction = onAction,
            paddingValues = paddingValues,
        )
    }

    @Deprecated(
        message = "Use show() with a paddingValues parameter to position the toast.",
        replaceWith = ReplaceWith(
            expression = "show(label, voice, icon, duration, dismissible, actionLabel, onAction, null)",
        ),
        level = DeprecationLevel.HIDDEN,
    )
    public fun show(
        label: String,
        voice: ToastVoice = ToastVoice.Neutral,
        icon: LemonadeIcons? = null,
        duration: ToastDuration = ToastDuration.Short,
        dismissible: Boolean = true,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
    ) {
        show(
            label = label,
            voice = voice,
            icon = icon,
            duration = duration,
            dismissible = dismissible,
            actionLabel = actionLabel,
            onAction = onAction,
            paddingValues = null,
        )
    }

    /** Dismisses the current toast. */
    public fun dismiss() {
        currentToast = null
    }
}

/**
 * Current [LemonadeToastState]. Throws unless read inside a [LemonadeToastHost].
 */
public val LocalLemonadeToastState: ProvidableCompositionLocal<LemonadeToastState> = staticCompositionLocalOf {
    error("No LemonadeToastState provided. Wrap your content with LemonadeToastHost.")
}

/**
 * A brief notification that appears at the bottom of the screen.
 *
 * ## Usage
 * ```kotlin
 * val toastState = LocalLemonadeToastState.current
 * toastState.show(label = "Changes saved", voice = ToastVoice.Success)
 * ```
 *
 * @param label text to display
 * @param modifier [Modifier] applied to the toast container
 * @param voice tone — determines default icon and icon color. Defaults to [ToastVoice.Neutral]
 * @param icon optional custom icon, only used when [voice] is [ToastVoice.Neutral]
 */
@Deprecated(
    message = "Use Toast() with actionLabel and onAction to support an optional action button.",
    replaceWith = ReplaceWith(
        expression = "Toast(label, modifier, voice, icon, actionLabel = null, onAction = null)",
    ),
)
@Composable
public fun LemonadeUi.Toast(
    label: String,
    modifier: Modifier = Modifier,
    voice: ToastVoice = ToastVoice.Neutral,
    icon: LemonadeIcons? = null,
) {
    Toast(
        label = label,
        modifier = modifier,
        voice = voice,
        icon = icon,
        actionLabel = null,
        onAction = null,
    )
}

/**
 * A brief notification that appears at the bottom of the screen. Supports an optional tappable
 * action rendered at the trailing end of the toast.
 *
 * ## Usage
 * ```kotlin
 * val toastState = LocalLemonadeToastState.current
 * toastState.show(label = "Changes saved", voice = ToastVoice.Success)
 * toastState.show(label = "Item deleted", voice = ToastVoice.Neutral, actionLabel = "Undo", onAction = { /* undo */ })
 * ```
 *
 * @param label text to display
 * @param modifier [Modifier] applied to the toast container
 * @param voice tone — determines default icon and icon color. Defaults to [ToastVoice.Neutral]
 * @param icon optional custom icon, only used when [voice] is [ToastVoice.Neutral]
 * @param actionLabel optional label for the action button shown at the trailing end of the toast
 * @param onAction optional callback run when the action button is tapped. The button only shows
 *   when both [actionLabel] and [onAction] are non-null
 */
@Composable
public fun LemonadeUi.Toast(
    label: String,
    modifier: Modifier = Modifier,
    voice: ToastVoice = ToastVoice.Neutral,
    icon: LemonadeIcons? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    CoreToast(
        label = label,
        voice = voice,
        icon = icon,
        actionLabel = actionLabel,
        onAction = onAction,
        modifier = modifier,
    )
}

@Composable
private fun CoreToast(
    label: String,
    voice: ToastVoice,
    icon: LemonadeIcons?,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColors.current
    val spaces = LocalSpaces.current
    val shapes = LocalShapes.current
    val sizes = LocalSizes.current

    val resolvedIcon = when (voice) {
        ToastVoice.Success -> LemonadeIcons.CircleCheck
        ToastVoice.Error -> LemonadeIcons.CircleX
        ToastVoice.Neutral -> icon
        ToastVoice.Loading -> null
    }

    val iconTint = when (voice) {
        ToastVoice.Success -> colors.content.contentPositiveOnColor
        ToastVoice.Error -> colors.content.contentCriticalOnColor
        ToastVoice.Neutral, ToastVoice.Loading -> colors.content.contentNeutralOnColor
    }

    // A label that wraps stretches the row to the full width the host allows, so a multi-line toast
    // reads as a block instead of a ragged pill. A single-line label still hugs its content.
    var labelWraps by remember(label) { mutableStateOf(false) }

    Row(
        modifier = modifier
            .heightIn(min = sizes.size1100)
            .lemonadeShadow(
                shadow = LemonadeShadow.Large,
                shape = shapes.radiusFull,
            ).clip(shape = shapes.radiusFull)
            .background(color = colors.background.bgDefaultInverse)
            .padding(
                horizontal = spaces.spacing400,
                vertical = spaces.spacing300,
            ),
        horizontalArrangement = Arrangement.spacedBy(spaces.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (voice == ToastVoice.Loading) {
            LemonadeUi.Spinner(
                size = LemonadeAssetSize.Medium,
                tint = iconTint,
            )
        } else if (resolvedIcon != null) {
            LemonadeUi.Icon(
                icon = resolvedIcon,
                contentDescription = null,
                size = LemonadeAssetSize.Medium,
                tint = iconTint,
            )
        }

        LemonadeUi.Text(
            text = label,
            textStyle = LocalTypographies.current.bodySmallMedium,
            color = colors.content.contentPrimaryInverse,
            onTextLayout = { layoutResult -> labelWraps = layoutResult.lineCount > 1 },
            modifier = Modifier
                .weight(
                    weight = 1f,
                    fill = labelWraps,
                ).padding(horizontal = spaces.spacing100),
        )

        if (actionLabel != null && onAction != null) {
            LemonadeUi.Text(
                text = actionLabel,
                textStyle = LocalTypographies.current.bodySmallMedium,
                color = colors.content.contentInfoAlwaysOnColor,
                modifier = Modifier
                    .semantics { role = Role.Button }
                    .clickable(onClick = onAction)
                    .padding(
                        horizontal = spaces.spacing100,
                        vertical = spaces.spacing100,
                    ),
            )
        }
    }
}

private const val DRAG_DISMISS_THRESHOLD_DP = 25
private const val DRAG_FADE_MULTIPLIER = 4

/**
 * Renders toast overlays at the bottom of the screen with entry/exit animations.
 *
 * Supplies [LemonadeToastState] to the composition tree. Place this at the root of your app, wrapping
 * your main content.
 *
 * ## Usage
 * ```kotlin
 * LemonadeToastHost {
 *     // Your app content
 * }
 * ```
 */
@Composable
public fun LemonadeToastHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val toastState = remember { LemonadeToastState() }

    val toast = toastState.currentToast
    val haptic = LocalHapticFeedback.current
    // Hosted here, not in the platform overlay, so it runs the same inline or in a window.
    LaunchedEffect(toast?.id) {
        if (toast != null) {
            val feedbackType = when (toast.voice) {
                ToastVoice.Neutral, ToastVoice.Loading -> HapticFeedbackType.TextHandleMove
                ToastVoice.Success, ToastVoice.Error -> HapticFeedbackType.LongPress
            }
            haptic.performHapticFeedback(feedbackType)
            // A loading toast describes an ongoing action: it persists until explicitly
            // dismissed or replaced, so skip the auto-dismiss timer.
            if (toast.voice != ToastVoice.Loading) {
                delay(toast.duration.millis)
                toastState.dismiss()
            }
        }
    }

    CompositionLocalProvider(LocalLemonadeToastState provides toastState) {
        PlatformToastHost(
            modifier = modifier,
            toastState = toastState,
            content = content,
        )
    }
}

/**
 * Renders the toast overlay for the current platform. Android draws it in its own window so it z-orders
 * above any [androidx.compose.material3.ModalBottomSheet] or [androidx.compose.ui.window.Dialog]; other
 * platforms render it inline as a sibling of [content].
 */
@Composable
internal expect fun PlatformToastHost(
    modifier: Modifier,
    toastState: LemonadeToastState,
    content: @Composable () -> Unit,
)

/** Inline overlay: the animated toast pinned to the bottom of the host [Box], over [content]. */
@Composable
internal fun InlineToastHost(
    modifier: Modifier,
    toastState: LemonadeToastState,
    content: @Composable () -> Unit,
) {
    var hostHeightPx by remember { mutableStateOf(0) }
    Box(modifier = modifier.onSizeChanged { size -> hostHeightPx = size.height }) {
        content()

        val toast = toastState.currentToast
        val padding = rememberToastPadding(
            override = toast?.paddingValues,
            layoutDirection = LocalLayoutDirection.current,
        )

        AnimatedContent(
            targetState = toast,
            contentAlignment = Alignment.BottomCenter,
            transitionSpec = {
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                    initialOffsetY = { hostHeightPx },
                ).togetherWith(
                    slideOutVertically(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                        targetOffsetY = { hostHeightPx },
                    ),
                ).using(SizeTransform(clip = false) { _, _ -> snap() })
            },
            contentKey = { toastData -> toastData?.id },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(padding),
        ) { animatedToast ->
            if (animatedToast != null) {
                SwipeableToast(
                    toast = animatedToast,
                    onDismiss = { toastState.dismiss() },
                )
            }
        }
    }
}

/** The toast plus swipe-to-dismiss: a downward drag past a threshold dismisses it (never a loading toast). */
@Composable
internal fun SwipeableToast(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetY by remember(toast.id) { mutableStateOf(0f) }
    val swipeDismissible = toast.dismissible && toast.voice != ToastVoice.Loading
    val swipeModifier = if (swipeDismissible) {
        Modifier.pointerInput(toast.id) {
            detectVerticalDragGestures(
                onDragEnd = {
                    if (offsetY > DRAG_DISMISS_THRESHOLD_DP.dp.toPx()) {
                        onDismiss()
                    } else {
                        offsetY = 0f
                    }
                },
                onDragCancel = { offsetY = 0f },
                onVerticalDrag = { change, dragAmount ->
                    offsetY = (offsetY + dragAmount).coerceAtLeast(0f)
                    change.consume()
                },
            )
        }
    } else {
        Modifier
    }

    LemonadeUi.Toast(
        label = toast.label,
        voice = toast.voice,
        icon = toast.icon,
        actionLabel = toast.actionLabel,
        onAction = toast.onAction,
        modifier = modifier
            .then(swipeModifier)
            .offset {
                IntOffset(
                    x = 0,
                    y = offsetY.roundToInt(),
                )
            }.graphicsLayer {
                val fadeDistance = DRAG_DISMISS_THRESHOLD_DP.dp.toPx() * DRAG_FADE_MULTIPLIER
                alpha = 1f - (offsetY / fadeDistance).coerceIn(minimumValue = 0f, maximumValue = 1f)
            },
    )
}

/**
 * Resolves the toast margins from a per-edge [override]. A zero edge means "not overridden" and falls
 * back to the standard margin; the top edge is never honored (the toast is bottom-anchored).
 */
@Composable
internal fun rememberToastPadding(
    override: PaddingValues?,
    layoutDirection: LayoutDirection,
): PaddingValues {
    val spaces = LocalSpaces.current
    val bottom = override
        ?.calculateBottomPadding()
        ?.takeIf { edge -> edge > 0.dp }
        ?: spaces.spacing600
    val start = override
        ?.calculateStartPadding(layoutDirection)
        ?.takeIf { edge -> edge > 0.dp }
        ?: spaces.spacing400
    val end = override
        ?.calculateEndPadding(layoutDirection)
        ?.takeIf { edge -> edge > 0.dp }
        ?: spaces.spacing400
    return PaddingValues(
        start = start,
        end = end,
        bottom = bottom,
    )
}
