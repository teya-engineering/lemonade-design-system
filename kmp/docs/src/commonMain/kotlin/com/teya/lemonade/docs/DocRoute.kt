package com.teya.lemonade.docs

/**
 * Every page the site can show, as a closed set.
 *
 * Internal links are [DocRoute] values rather than strings, so a link to a page that does not
 * exist — or to one that has been renamed — is a compile error rather than a broken link found in
 * production. This is what replaces the link validator a Markdown-based site needs.
 */
internal sealed interface DocRoute {
    /** Fragment path, without the leading `#`. */
    val path: String

    /** Name used in the sidebar, in breadcrumbs, and in "next steps" links. */
    val label: String

    data object Home : DocRoute {
        override val path: String = "/"
        override val label: String = "Home"
    }

    data object Overview : DocRoute {
        override val path: String = "/get-started/overview"
        override val label: String = "Overview"
    }

    data object Design : DocRoute {
        override val path: String = "/get-started/design"
        override val label: String = "Design"
    }

    data object Kmp : DocRoute {
        override val path: String = "/get-started/kmp"
        override val label: String = "Kotlin Multiplatform"
    }

    data object SwiftUi : DocRoute {
        override val path: String = "/get-started/swiftui"
        override val label: String = "SwiftUI"
    }

    data object Prototyping : DocRoute {
        override val path: String = "/prototyping"
        override val label: String = "Prototyping with Lemonade"
    }

    data object SemanticTokens : DocRoute {
        override val path: String = "/standards/semantic-tokens"
        override val label: String = "Semantic tokens first"
    }

    data object Theming : DocRoute {
        override val path: String = "/standards/theming"
        override val label: String = "Theming & dark mode"
    }

    data object Accessibility : DocRoute {
        override val path: String = "/standards/accessibility"
        override val label: String = "Accessibility"
    }

    data object Rhythm : DocRoute {
        override val path: String = "/layout/rhythm"
        override val label: String = "Layout & rhythm"
    }

    data object ContainerMargins : DocRoute {
        override val path: String = "/layout/container-margins"
        override val label: String = "Container margins"
    }

    data object BlockGaps : DocRoute {
        override val path: String = "/layout/block-gaps"
        override val label: String = "Block gaps"
    }

    data object CardsAndContainers : DocRoute {
        override val path: String = "/layout/cards-and-containers"
        override val label: String = "Cards and containers"
    }

    data object LayoutLists : DocRoute {
        override val path: String = "/layout/lists"
        override val label: String = "Lists"
    }

    data object PatternForms : DocRoute {
        override val path: String = "/patterns/forms"
        override val label: String = "Forms"
    }

    data object PatternLists : DocRoute {
        override val path: String = "/patterns/lists"
        override val label: String = "Lists"
    }

    data object EmptyAndLoading : DocRoute {
        override val path: String = "/patterns/empty-and-loading"
        override val label: String = "Empty & loading"
    }

    data object Errors : DocRoute {
        override val path: String = "/patterns/errors"
        override val label: String = "Errors & recovery"
    }

    data object Colour : DocRoute {
        override val path: String = "/foundations/colour"
        override val label: String = "Colour"
    }

    data object Typography : DocRoute {
        override val path: String = "/foundations/typography"
        override val label: String = "Typography"
    }

    data object SpaceAndShape : DocRoute {
        override val path: String = "/foundations/space-and-shape"
        override val label: String = "Space & shape"
    }

    data object Elevation : DocRoute {
        override val path: String = "/foundations/elevation"
        override val label: String = "Elevation"
    }

    data object OpacityAndBorders : DocRoute {
        override val path: String = "/foundations/opacity-and-borders"
        override val label: String = "Opacity & borders"
    }

    data object NotFound : DocRoute {
        override val path: String = "/404"
        override val label: String = "Not found"
    }

    companion object {
        val entries: List<DocRoute> = listOf(
            Home,
            Overview,
            Design,
            Kmp,
            SwiftUi,
            Prototyping,
            SemanticTokens,
            Theming,
            Accessibility,
            Rhythm,
            ContainerMargins,
            BlockGaps,
            CardsAndContainers,
            LayoutLists,
            PatternForms,
            PatternLists,
            EmptyAndLoading,
            Errors,
            Colour,
            Typography,
            SpaceAndShape,
            Elevation,
            OpacityAndBorders,
            NotFound,
        )

        fun parse(fragment: String): DocRoute {
            val trimmed = fragment
                .removePrefix("#")
                .trim('/')
            val normalised = "/$trimmed"
            return entries.firstOrNull { route ->
                route.path == normalised
            }
                ?: NotFound
        }
    }
}
