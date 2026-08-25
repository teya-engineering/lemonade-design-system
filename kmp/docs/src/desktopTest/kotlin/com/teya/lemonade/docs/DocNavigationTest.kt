package com.teya.lemonade.docs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The invariants a Markdown site needs a link checker for. Here they are ordinary assertions,
 * because the navigation is data and the routes are a closed set.
 */
class DocNavigationTest {
    @Test
    fun `every route except home and 404 is reachable from the sidebar`() {
        val reachable = sidebarRoutes().toSet()
        val orphans = DocRoute.entries
            .filterNot { route ->
                route == DocRoute.Home || route == DocRoute.NotFound
            }.filterNot { route ->
                route in reachable
            }
        assertTrue(
            orphans.isEmpty(),
            "Routes missing from the sidebar: ${orphans.map { route -> route.path }}",
        )
    }

    @Test
    fun `the sidebar names no route twice`() {
        val routes = sidebarRoutes()
        assertEquals(
            routes.size,
            routes.toSet().size,
            "The sidebar lists a route more than once.",
        )
    }

    @Test
    fun `route paths are unique`() {
        val paths = DocRoute.entries.map { route ->
            route.path
        }
        assertEquals(
            paths.size,
            paths.toSet().size,
            "Two routes share a path.",
        )
    }

    @Test
    fun `every route parses back from its own path`() {
        DocRoute.entries.forEach { route ->
            assertEquals(
                route,
                DocRoute.parse(fragment = route.path),
                "Route ${route.path} did not round-trip.",
            )
            assertEquals(
                route,
                DocRoute.parse(fragment = "#${route.path}"),
                "Route #${route.path} did not round-trip.",
            )
        }
    }

    @Test
    fun `an unknown fragment falls back to not found`() {
        assertEquals(DocRoute.NotFound, DocRoute.parse(fragment = "#/nope"))
        assertEquals(DocRoute.NotFound, DocRoute.parse(fragment = "#/standards/nope"))
    }

    @Test
    fun `sections and groups are labelled and non-empty`() {
        docSections.forEach { section ->
            assertTrue(section.label.isNotBlank(), "A section has no label.")
            assertTrue(section.entries.isNotEmpty(), "Section ${section.label} is empty.")
            section.entries.filterIsInstance<DocSectionEntry.Group>().forEach { group ->
                assertTrue(group.label.isNotBlank(), "A group in ${section.label} has no label.")
                assertTrue(group.routes.isNotEmpty(), "Group ${group.label} is empty.")
            }
        }
    }
}
