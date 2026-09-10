package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.teya.lemonade.core.LemonadeIcons

@Composable
internal fun LinkDisplay() {
    SampleScreenDisplayLazyColumn(title = "Link") {
        item(key = "Basic") {
            BasicLinkSection()
        }
        item(key = "With Icon") {
            WithIconLinkSection()
        }
        item(key = "Disabled") {
            DisabledLinkSection()
        }
        item(key = "In Context") {
            InContextLinkSection()
        }
    }
}

@Composable
private fun BasicLinkSection() {
    LinkSection(title = "Basic") {
        LemonadeUi.Link(
            text = "Learn more",
            onClick = { },
        )
    }
}

@Composable
private fun WithIconLinkSection() {
    LinkSection(title = "With Icon") {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        ) {
            LemonadeUi.Link(
                text = "Open in browser",
                onClick = { },
                icon = LemonadeIcons.ExternalLink,
            )
            LemonadeUi.Link(
                text = "Continue reading",
                onClick = { },
                icon = LemonadeIcons.ArrowRight,
            )
        }
    }
}

@Composable
private fun DisabledLinkSection() {
    LinkSection(title = "Disabled") {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        ) {
            LemonadeUi.Link(
                text = "Unavailable link",
                onClick = { },
                enabled = false,
            )
            LemonadeUi.Link(
                text = "Unavailable link with icon",
                onClick = { },
                enabled = false,
                icon = LemonadeIcons.ExternalLink,
            )
        }
    }
}

@Composable
private fun InContextLinkSection() {
    LinkSection(title = "In Context") {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LemonadeTheme.radius.radius300))
                .background(LemonadeTheme.colors.background.bgElevated)
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Terms and Conditions",
                textStyle = LemonadeTheme.typography.headingXSmall,
            )
            LemonadeUi.Text(
                text = "By continuing you agree to our terms of service and privacy policy.",
                textStyle = LemonadeTheme.typography.bodyMediumRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LemonadeUi.Link(
                    text = "Terms of Service",
                    onClick = { },
                    icon = LemonadeIcons.ExternalLink,
                )
                LemonadeUi.Link(
                    text = "Privacy Policy",
                    onClick = { },
                    icon = LemonadeIcons.ExternalLink,
                )
            }
        }
    }
}

@Composable
private fun LinkSection(
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
