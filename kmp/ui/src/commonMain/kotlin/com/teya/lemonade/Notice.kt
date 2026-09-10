package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.NoticeVoice

/**
 * Shows a brief, important message inside content.
 *
 * Optionally carries an icon and an action for contextual information or status updates.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Notice(
 *     content = "Your payment was processed successfully.",
 *     voice = NoticeVoice.Positive,
 * )
 *
 * LemonadeUi.Notice(
 *     title = "Action required",
 *     content = "Please update your billing information.",
 *     voice = NoticeVoice.Warning,
 *     actionLabel = "Update",
 *     onActionClick = { /* handle action */ },
 * )
 * ```
 *
 * @param content body text shown in the notice
 * @param voice semantic tone, driving the background, icon tint and action text colors
 * @param modifier optional [Modifier] applied to the root container
 * @param title optional bold heading shown above [content]
 * @param showIcon whether to show the leading icon, chosen from [voice]; defaults to `true`
 * @param actionLabel optional text for the action button below [content]
 * @param onActionClick called when the action is tapped
 */
@Composable
public fun LemonadeUi.Notice(
    content: String,
    voice: NoticeVoice,
    modifier: Modifier = Modifier,
    title: String? = null,
    showIcon: Boolean = true,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val icon = if (showIcon) voice.defaultIcon else null

    CoreNotice(
        content = content,
        title = title,
        voice = voice,
        icon = icon,
        actionLabel = actionLabel,
        onActionClick = onActionClick,
        modifier = modifier,
        interactionSource = interactionSource,
    )
}

@Composable
private fun CoreNotice(
    content: String,
    title: String?,
    voice: NoticeVoice,
    icon: LemonadeIcons?,
    actionLabel: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = voice.noticeColors

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing300),
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.containerColor,
                shape = LocalShapes.current.radius500,
            ).padding(all = LocalSpaces.current.spacing400),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(
                    width = LocalSizes.current.size500,
                    height = if (title != null) LocalSizes.current.size600 else LocalSizes.current.size500,
                ),
                contentAlignment = Alignment.Center,
            ) {
                LemonadeUi.Icon(
                    icon = icon,
                    contentDescription = null,
                    tint = colors.iconTintColor,
                    size = LemonadeAssetSize.Medium,
                )
            }
        }

        Column(
            modifier = Modifier.weight(weight = 1f),
        ) {
            if (title != null) {
                LemonadeUi.Text(
                    text = title,
                    color = LocalColors.current.content.contentPrimary,
                    textStyle = LocalTypographies.current.bodyMediumSemiBold,
                )
            }

            LemonadeUi.Text(
                text = content,
                color = LocalColors.current.content.contentPrimary,
                textStyle = LocalTypographies.current.bodySmallRegular,
            )

            if (actionLabel != null) {
                LemonadeUi.Text(
                    text = actionLabel,
                    textStyle = LocalTypographies.current.bodySmallSemiBold,
                    color = colors.actionTextColor,
                    modifier = Modifier
                        .padding(top = LocalSpaces.current.spacing200)
                        .clickable(
                            onClick = { onActionClick?.invoke() },
                            role = Role.Button,
                            indication = LocalEffects.current.interactionIndication,
                            interactionSource = interactionSource,
                            enabled = onActionClick != null,
                        ),
                )
            }
        }
    }
}

@Stable
private data class NoticeColors(
    val containerColor: Color,
    val iconTintColor: Color,
    val actionTextColor: Color,
)

private val NoticeVoice.noticeColors: NoticeColors
    @Composable get() {
        return when (this) {
            NoticeVoice.Info -> NoticeColors(
                containerColor = LocalColors.current.background.bgInfoSubtle,
                iconTintColor = LocalColors.current.content.contentInfo,
                actionTextColor = LocalColors.current.content.contentInfo,
            )

            NoticeVoice.Positive -> NoticeColors(
                containerColor = LocalColors.current.background.bgPositiveSubtle,
                iconTintColor = LocalColors.current.content.contentPositive,
                actionTextColor = LocalColors.current.content.contentPositive,
            )

            NoticeVoice.Warning -> NoticeColors(
                containerColor = LocalColors.current.background.bgCautionSubtle,
                iconTintColor = LocalColors.current.content.contentCaution,
                actionTextColor = LocalColors.current.content.contentCaution,
            )

            NoticeVoice.Critical -> NoticeColors(
                containerColor = LocalColors.current.background.bgCriticalSubtle,
                iconTintColor = LocalColors.current.content.contentCritical,
                actionTextColor = LocalColors.current.content.contentCritical,
            )

            NoticeVoice.Neutral -> NoticeColors(
                containerColor = LocalColors.current.background.bgElevated,
                iconTintColor = LocalColors.current.content.contentSecondary,
                actionTextColor = LocalColors.current.content.contentNeutral,
            )
        }
    }

private val NoticeVoice.defaultIcon: LemonadeIcons
    get() = when (this) {
        NoticeVoice.Info -> LemonadeIcons.CircleInfo
        NoticeVoice.Positive -> LemonadeIcons.CircleCheck
        NoticeVoice.Warning -> LemonadeIcons.TriangleAlert
        NoticeVoice.Critical -> LemonadeIcons.TriangleAlert
        NoticeVoice.Neutral -> LemonadeIcons.Heart
    }

private data class NoticePreviewData(
    val voice: NoticeVoice,
    val withTitle: Boolean,
    val withAction: Boolean,
    val withIcon: Boolean,
)

private class NoticePreviewProvider : PreviewParameterProvider<NoticePreviewData> {
    override val values: Sequence<NoticePreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<NoticePreviewData> =
        buildList {
            NoticeVoice.entries.forEach { voice ->
                listOf(true, false)
                    .forEach { withTitle ->
                        listOf(true, false)
                            .forEach { withAction ->
                                listOf(true, false)
                                    .forEach { withIcon ->
                                        add(
                                            element = NoticePreviewData(
                                                voice = voice,
                                                withTitle = withTitle,
                                                withAction = withAction,
                                                withIcon = withIcon,
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
private fun NoticePreview(
    @PreviewParameter(NoticePreviewProvider::class)
    previewData: NoticePreviewData,
) {
    LemonadeUi.Notice(
        content = "This is a notice message with important information.",
        title = "Notice Title".takeIf { previewData.withTitle },
        voice = previewData.voice,
        showIcon = previewData.withIcon,
        actionLabel = "Action".takeIf { previewData.withAction },
        onActionClick = {},
    )
}
