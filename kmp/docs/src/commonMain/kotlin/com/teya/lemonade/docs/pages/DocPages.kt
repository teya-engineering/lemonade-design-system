package com.teya.lemonade.docs.pages

import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.DocPage

internal val docPages: List<DocPage> = listOf(
    semanticTokensPage,
    themingPage,
    accessibilityPage,
    colourPage,
    typographyPage,
    spaceAndShapePage,
    elevationPage,
    opacityAndBordersPage,
)

private val pagesByRoute: Map<DocRoute, DocPage> = docPages.associateBy { page ->
    page.route
}

internal fun pageFor(route: DocRoute): DocPage? = pagesByRoute[route]
