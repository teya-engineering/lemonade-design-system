package com.teya.lemonade.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text
import com.teya.lemonade.docs.content.DocBlocks
import com.teya.lemonade.docs.pages.pageFor
import com.teya.lemonade.docs.theme.DocStyledTheme
import com.teya.lemonade.docs.theme.rememberDocStyleHandler

private val ContentMaxWidth = 760.dp

@Composable
internal fun DocsApp(router: DocRouter) {
    val handler = rememberDocStyleHandler()
    DocStyledTheme(handler = handler) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(LemonadeTheme.colors.background.bgSubtle),
        ) {
            DocSidebar(
                router = router,
                handler = handler,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                DocContent(router = router)
            }
        }
    }
}

@Composable
private fun DocContent(router: DocRouter) {
    val route = router.current
    val page = pageFor(route)
    Column(
        modifier = Modifier
            .widthIn(max = ContentMaxWidth)
            .fillMaxWidth()
            .padding(
                horizontal = LemonadeTheme.spaces.spacing600,
                vertical = LemonadeTheme.spaces.spacing800,
            ),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = page?.title ?: route.label,
            textStyle = LemonadeTheme.typography.displaySmall,
            color = LemonadeTheme.colors.content.contentPrimary,
        )
        if (page != null) {
            LemonadeUi.Text(
                text = page.description,
                textStyle = LemonadeTheme.typography.bodyLargeRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
            DocBlocks(
                blocks = page.blocks,
                router = router,
                modifier = Modifier.padding(top = LemonadeTheme.spaces.spacing400),
            )
        }
    }
}
