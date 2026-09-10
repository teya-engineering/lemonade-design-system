package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.NoticeVoice

@Composable
internal fun NoticeDisplay() {
    SampleScreenDisplayLazyColumn(title = "Notice") {
        item(key = "Voices") {
            NoticeSection(title = "Voices") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Notice(
                        content = "This is an informational notice.",
                        voice = NoticeVoice.Info,
                        actionLabel = "Action",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        content = "Operation completed successfully.",
                        voice = NoticeVoice.Positive,
                        actionLabel = "Action",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        content = "Please review before proceeding.",
                        voice = NoticeVoice.Warning,
                        actionLabel = "Action",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        content = "An error occurred. Please try again.",
                        voice = NoticeVoice.Critical,
                        actionLabel = "Action",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        content = "No new updates available.",
                        voice = NoticeVoice.Neutral,
                        actionLabel = "Action",
                        onActionClick = {},
                    )
                }
            }
        }

        item(key = "With Title") {
            NoticeSection(title = "With Title") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Notice(
                        title = "Information",
                        content = "Your account settings have been updated.",
                        voice = NoticeVoice.Info,
                    )
                    LemonadeUi.Notice(
                        title = "Success",
                        content = "Payment of $42.00 was processed.",
                        voice = NoticeVoice.Positive,
                    )
                    LemonadeUi.Notice(
                        title = "Warning",
                        content = "Your subscription expires in 3 days.",
                        voice = NoticeVoice.Warning,
                    )
                    LemonadeUi.Notice(
                        title = "Error",
                        content = "Unable to connect to the server.",
                        voice = NoticeVoice.Critical,
                    )
                    LemonadeUi.Notice(
                        title = "Note",
                        content = "You have no pending notifications.",
                        voice = NoticeVoice.Neutral,
                    )
                }
            }
        }

        item(key = "With Action") {
            NoticeSection(title = "With Action") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Notice(
                        content = "A new version is available.",
                        voice = NoticeVoice.Info,
                        actionLabel = "Update now",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        title = "Action required",
                        content = "Please update your billing information to avoid service interruption.",
                        voice = NoticeVoice.Warning,
                        actionLabel = "Update billing",
                        onActionClick = {},
                    )
                    LemonadeUi.Notice(
                        title = "Payment failed",
                        content = "We couldn't process your last payment.",
                        voice = NoticeVoice.Critical,
                        actionLabel = "Retry payment",
                        onActionClick = {},
                    )
                }
            }
        }

        item(key = "Without Icon") {
            NoticeSection(title = "Without Icon") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Notice(
                        content = "A simple notice without an icon.",
                        voice = NoticeVoice.Info,
                        showIcon = false,
                    )
                    LemonadeUi.Notice(
                        title = "Custom content",
                        content = "This notice has a title but no icon.",
                        voice = NoticeVoice.Positive,
                        showIcon = false,
                        actionLabel = "Learn more",
                        onActionClick = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}
