package com.teya.lemonade.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.SegmentedControl
import com.teya.lemonade.Text
import com.teya.lemonade.clickable
import com.teya.lemonade.core.LemonadeSegmentedControlSize
import com.teya.lemonade.core.TabButtonProperties
import com.teya.lemonade.docs.theme.DocStyle
import com.teya.lemonade.docs.theme.DocStyleHandler
import com.teya.lemonade.docs.theme.DocThemeVariant

private val SidebarWidth = 280.dp

@Composable
internal fun DocSidebar(
    router: DocRouter,
    handler: DocStyleHandler,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(SidebarWidth)
            .fillMaxHeight()
            .background(LemonadeTheme.colors.background.bgDefault)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = LemonadeTheme.spaces.spacing400,
                vertical = LemonadeTheme.spaces.spacing600,
            ),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
    ) {
        Box(
            modifier = Modifier.clickable(
                onClick = {
                    router.navigate(DocRoute.Home)
                },
                role = Role.Button,
            ),
        ) {
            LemonadeUi.Text(
                text = "Lemonade",
                textStyle = LemonadeTheme.typography.headingSmall,
                color = LemonadeTheme.colors.content.contentPrimary,
            )
        }

        docSections.forEach { section ->
            Column(
                verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing100),
            ) {
                LemonadeUi.Text(
                    text = section.label,
                    textStyle = LemonadeTheme.typography.bodyXSmallOverline,
                    color = LemonadeTheme.colors.content.contentSecondary,
                    modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing100),
                )
                section.entries.forEach { entry ->
                    when (entry) {
                        is DocSectionEntry.Link -> SidebarLink(
                            route = entry.route,
                            router = router,
                        )

                        is DocSectionEntry.Group -> SidebarGroup(
                            group = entry,
                            router = router,
                        )
                    }
                }
            }
        }

        ThemeControls(handler = handler)
    }
}

@Composable
private fun SidebarGroup(
    group: DocSectionEntry.Group,
    router: DocRouter,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing100),
    ) {
        LemonadeUi.Text(
            text = group.label,
            textStyle = LemonadeTheme.typography.bodySmallSemiBold,
            color = LemonadeTheme.colors.content.contentSecondary,
            modifier = Modifier.padding(
                start = LemonadeTheme.spaces.spacing200,
                top = LemonadeTheme.spaces.spacing200,
            ),
        )
        group.routes.forEach { route ->
            SidebarLink(
                route = route,
                router = router,
                indented = true,
            )
        }
    }
}

@Composable
private fun SidebarLink(
    route: DocRoute,
    router: DocRouter,
    indented: Boolean = false,
) {
    val selected = router.current == route
    val background = if (selected) {
        LemonadeTheme.colors.background.bgBrandSubtle
    } else {
        LemonadeTheme.colors.background.bgDefault
    }
    val color = if (selected) {
        LemonadeTheme.colors.content.contentBrand
    } else {
        LemonadeTheme.colors.content.contentSecondary
    }
    val textStyle = if (selected) {
        LemonadeTheme.typography.bodySmallSemiBold
    } else {
        LemonadeTheme.typography.bodySmallRegular
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LemonadeTheme.shapes.radius200)
            .background(background)
            .clickable(
                onClick = {
                    router.navigate(route)
                },
                role = Role.Tab,
            ).padding(
                start = if (indented) {
                    LemonadeTheme.spaces.spacing400
                } else {
                    LemonadeTheme.spaces.spacing200
                },
                end = LemonadeTheme.spaces.spacing200,
                top = LemonadeTheme.spaces.spacing200,
                bottom = LemonadeTheme.spaces.spacing200,
            ),
    ) {
        LemonadeUi.Text(
            text = route.label,
            textStyle = textStyle,
            color = color,
        )
    }
}

@Composable
private fun ThemeControls(handler: DocStyleHandler) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.SegmentedControl(
            properties = DocStyle.entries.map { style ->
                TabButtonProperties.label(label = style.label)
            },
            selectedTab = DocStyle.entries.indexOf(handler.currentStyle),
            onTabSelected = { index ->
                handler.currentStyle = DocStyle.entries[index]
            },
            size = LemonadeSegmentedControlSize.Small,
        )
        LemonadeUi.SegmentedControl(
            properties = DocThemeVariant.entries.map { variant ->
                TabButtonProperties.label(label = variant.label)
            },
            selectedTab = DocThemeVariant.entries.indexOf(handler.currentVariant),
            onTabSelected = { index ->
                handler.currentVariant = DocThemeVariant.entries[index]
            },
            size = LemonadeSegmentedControlSize.Small,
        )
    }
}
