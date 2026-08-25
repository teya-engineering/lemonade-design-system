package com.teya.lemonade.docs

/**
 * The navigation, as data. Two levels of grouping: an inert section heading, under which sit either
 * links or collapsible groups of links.
 *
 * Standards and Patterns sit above Foundations deliberately. The site is about how to build with
 * Lemonade, so the reference material is not what a reader should meet first.
 */
internal data class DocSection(
    val label: String,
    val entries: List<DocSectionEntry>,
)

internal sealed interface DocSectionEntry {
    data class Link(
        val route: DocRoute,
    ) : DocSectionEntry

    data class Group(
        val label: String,
        val routes: List<DocRoute>,
    ) : DocSectionEntry
}

internal val docSections: List<DocSection> = listOf(
    DocSection(
        label = "Get started",
        entries = listOf(
            DocSectionEntry.Link(route = DocRoute.Overview),
            DocSectionEntry.Link(route = DocRoute.Design),
            DocSectionEntry.Group(
                label = "Engineering",
                routes = listOf(
                    DocRoute.Kmp,
                    DocRoute.SwiftUi,
                ),
            ),
        ),
    ),
    DocSection(
        label = "Build",
        entries = listOf(
            DocSectionEntry.Link(route = DocRoute.Prototyping),
            DocSectionEntry.Group(
                label = "Standards",
                routes = listOf(
                    DocRoute.SemanticTokens,
                    DocRoute.Theming,
                    DocRoute.Accessibility,
                ),
            ),
            DocSectionEntry.Group(
                label = "Layout",
                routes = listOf(
                    DocRoute.Rhythm,
                    DocRoute.ContainerMargins,
                    DocRoute.BlockGaps,
                    DocRoute.CardsAndContainers,
                    DocRoute.LayoutLists,
                ),
            ),
            DocSectionEntry.Group(
                label = "Patterns",
                routes = listOf(
                    DocRoute.PatternForms,
                    DocRoute.PatternLists,
                    DocRoute.EmptyAndLoading,
                    DocRoute.Errors,
                ),
            ),
        ),
    ),
    DocSection(
        label = "Foundations",
        entries = listOf(
            DocSectionEntry.Link(route = DocRoute.Colour),
            DocSectionEntry.Link(route = DocRoute.Typography),
            DocSectionEntry.Link(route = DocRoute.SpaceAndShape),
            DocSectionEntry.Link(route = DocRoute.Elevation),
            DocSectionEntry.Link(route = DocRoute.OpacityAndBorders),
        ),
    ),
)

/** Every route the sidebar names, in render order. */
internal fun sidebarRoutes(): List<DocRoute> =
    docSections.flatMap { section ->
        section.entries.flatMap { entry ->
            when (entry) {
                is DocSectionEntry.Link -> listOf(entry.route)
                is DocSectionEntry.Group -> entry.routes
            }
        }
    }
