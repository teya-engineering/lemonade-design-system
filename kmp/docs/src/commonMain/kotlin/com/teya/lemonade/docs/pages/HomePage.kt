package com.teya.lemonade.docs.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage
import com.teya.lemonade.docs.tokens.LemonadeColorTokenDocs

private val ChipSize = 40.dp
private val RuleHeight = 1.dp

internal val homePage: DocPage = docPage(
    route = DocRoute.Home,
    title = "Build it once. Build it right.",
    description =
        "The shared language behind Teya's products — components, tokens, and the standards.",
) {
    sample { PaletteChips() }

    h2("One system, native everywhere")
    table(headers = listOf("Platform", "Targets", "Status")) {
        row(
            { b("Kotlin Multiplatform") },
            { +"Android, iOS, JVM Desktop, browser" },
            { +"Published" },
        )
        row(
            { b("SwiftUI") },
            { +"iOS 15+, macOS 12+" },
            { +"Published" },
        )
        row(
            { b("Flutter") },
            { +"Android, iOS, Web" },
            { +"Unmaintained" },
        )
    }
    p(
        "Two real implementations rather than one wrapper stretched over every platform, both " +
            "generated from a single Figma export. This site is itself built with the Kotlin " +
            "Multiplatform modules, compiled to the browser — every control on these pages is the " +
            "shipped component.",
    )

    h2("Standards — the rules of the system")
    p {
        +"Why a semantic token beats a raw value, how one theme resolves to two, and the "
        +"accessibility baseline every screen is expected to meet. Start with "
        link(text = "Semantic tokens first", route = DocRoute.SemanticTokens)
        +"."
    }

    h2("Patterns — solutions worth reusing")
    p {
        +"Whole screens rather than parts: a "
        link(text = "form", route = DocRoute.PatternForms)
        +", a "
        link(text = "list", route = DocRoute.PatternLists)
        +", an "
        link(text = "empty state", route = DocRoute.EmptyAndLoading)
        +", an "
        link(text = "error", route = DocRoute.Errors)
        +" — assembled, with the decisions written down."
    }

    h2("Foundations — generated from the source of truth")
    p {
        +"Every token, its light and dark value, the guidance design wrote in Figma, and the symbol "
        +"to use on each platform. Read from the same export the Kotlin and Swift converters consume, "
        +"so it cannot go stale. Start with "
        link(text = "Colour", route = DocRoute.Colour)
        +"."
    }

    h2("Start here")
    nextSteps(
        DocRoute.Overview,
        DocRoute.Kmp,
        DocRoute.SwiftUi,
        DocRoute.Design,
        DocRoute.Prototyping,
    )
}

internal val notFoundPage: DocPage = docPage(
    route = DocRoute.NotFound,
    title = "Not found",
    description = "That page does not exist.",
) {
    p("The link that brought you here points at a page this site does not have.")
    nextSteps(DocRoute.Home, DocRoute.Overview, DocRoute.Colour)
}

/**
 * A sample of the palette, read from the catalog so a renamed token fails the build rather than
 * quietly shrinking the row.
 */
@Composable
private fun PaletteChips() {
    val chips = listOf("bg-brand", "content-positive", "content-caution", "content-critical")
        .mapNotNull { name ->
            LemonadeColorTokenDocs.firstOrNull { doc -> doc.name == name }
        }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(RuleHeight)
                .background(LemonadeTheme.colors.border.borderNeutralLow),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        ) {
            chips.forEach { doc ->
                Chip(color = if (LemonadeTheme.colors.isDark) doc.dark else doc.light)
            }
        }
        LemonadeUi.Text(
            text = "A sample of the palette, in the theme you are reading in.",
            textStyle = LemonadeTheme.typography.bodyXSmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun Chip(color: Color) {
    Box(
        modifier = Modifier
            .size(ChipSize)
            .clip(LemonadeTheme.shapes.radiusFull)
            .background(color),
    )
}
