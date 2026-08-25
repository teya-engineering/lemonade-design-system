package com.teya.lemonade.docs.pages

import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.DocPage

internal val docPages: List<DocPage> = listOf(
    homePage,
    overviewPage,
    designPage,
    kmpPage,
    swiftUiPage,
    prototypingPage,
    semanticTokensPage,
    themingPage,
    accessibilityPage,
    rhythmPage,
    containerMarginsPage,
    blockGapsPage,
    cardsAndContainersPage,
    layoutListsPage,
    formsPage,
    patternListsPage,
    emptyAndLoadingPage,
    errorsPage,
    notFoundPage,
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
