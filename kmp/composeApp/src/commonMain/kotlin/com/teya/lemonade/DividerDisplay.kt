package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.DividerVariant

@OptIn(ExperimentalLemonadeComponent::class)
@Composable
internal fun DividerDisplay() {
    SampleScreenDisplayLazyColumn(title = "Divider") {
        item(key = "Horizontal Divider") {
            DividerSection(title = "Horizontal Divider") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Default",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.HorizontalDivider()
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Dashed",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.HorizontalDivider(variant = DividerVariant.Dashed)
                    }
                }
            }
        }

        item(key = "Horizontal Divider with Label") {
            DividerSection(title = "Horizontal Divider with Label") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Default with Label",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.HorizontalDivider(label = "OR")
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Dashed with Label",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.HorizontalDivider(
                            label = "OR",
                            variant = DividerVariant.Dashed,
                        )
                    }
                }
            }
        }

        item(key = "Vertical Divider") {
            DividerSection(title = "Vertical Divider") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Default",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.VerticalDivider(modifier = Modifier.height(48.dp))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Text(
                            text = "Dashed",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                        LemonadeUi.VerticalDivider(
                            modifier = Modifier.height(48.dp),
                            variant = DividerVariant.Dashed,
                        )
                    }
                }
            }
        }

        item(key = "In Context") {
            DividerSection(title = "In Context") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
                ) {
                    ContentSeparationExample()
                    LoginSeparatorExample()
                    VerticalDividerRowExample()
                }
            }
        }
    }
}

@Composable
private fun ContentSeparationExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LemonadeTheme.radius.radius300))
            .background(LemonadeTheme.colors.background.bgElevated)
            .padding(LemonadeTheme.spaces.spacing400),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
    ) {
        LemonadeUi.Text(
            text = "Section 1",
            textStyle = LemonadeTheme.typography.bodyMediumMedium,
        )
        LemonadeUi.Text(
            text = "Some content for the first section",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        LemonadeUi.HorizontalDivider()
        LemonadeUi.Text(
            text = "Section 2",
            textStyle = LemonadeTheme.typography.bodyMediumMedium,
        )
        LemonadeUi.Text(
            text = "Some content for the second section",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun LoginSeparatorExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LemonadeTheme.radius.radius300))
            .background(LemonadeTheme.colors.background.bgElevated)
            .padding(LemonadeTheme.spaces.spacing400),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LemonadeUi.Button(
            label = "Continue with Email",
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
        )
        LemonadeUi.HorizontalDivider(label = "OR")
        LemonadeUi.Button(
            label = "Continue with Google",
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun VerticalDividerRowExample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LemonadeTheme.radius.radius300))
            .background(LemonadeTheme.colors.background.bgElevated)
            .padding(LemonadeTheme.spaces.spacing400),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LemonadeUi.Text(
                text = "125",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "Posts",
                textStyle = LemonadeTheme.typography.bodySmallRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
        }
        LemonadeUi.VerticalDivider(modifier = Modifier.height(40.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LemonadeUi.Text(
                text = "1.2K",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "Followers",
                textStyle = LemonadeTheme.typography.bodySmallRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
        }
        LemonadeUi.VerticalDivider(modifier = Modifier.height(40.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LemonadeUi.Text(
                text = "348",
                textStyle = LemonadeTheme.typography.headingSmall,
            )
            LemonadeUi.Text(
                text = "Following",
                textStyle = LemonadeTheme.typography.bodySmallRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
        }
    }
}

@Composable
private fun DividerSection(
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
