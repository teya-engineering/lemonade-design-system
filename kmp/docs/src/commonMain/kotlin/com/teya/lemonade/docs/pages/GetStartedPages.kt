package com.teya.lemonade.docs.pages

import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.CodeLanguage
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage

private const val FIGMA_COMPONENTS =
    "https://www.figma.com/design/91S16rhVrl5wivqV66fNjm/%F0%9F%8D%8B-Lemonade-DS---Components"
private const val FIGMA_FOUNDATIONS =
    "https://www.figma.com/design/mmSKfenwtw1xujWwXvs9wJ/%F0%9F%94%B7-Lemonade-DS---Foundations"
private const val FIGMA_ICONS =
    "https://www.figma.com/design/f7zokCdnayXejxc2y7r1Qt/%F0%9F%8E%B2-Lemonade-DS---Icons"
private const val FIGMA_GLOBAL_COLORS =
    "https://www.figma.com/design/zVaShjKRQi4OmCgNgQ5QhK/%F0%9F%8E%A8-Lemonade-Global-Colors"

internal val overviewPage: DocPage = docPage(
    route = DocRoute.Overview,
    title = "Overview",
    description = "What the design system covers, the problem it exists to solve, and where it stops.",
) {
    p {
        b("Lemonade")
        +" is the shared language behind Teya's products: a set of production components, one set of "
        +"design tokens, and the standards that keep them honest. It ships as two real "
        +"implementations — Compose Multiplatform and SwiftUI — rather than one wrapper stretched "
        +"over every platform."
    }

    h2("The problem it solves")
    p(
        "Without a system, the same decisions get made again and again — by the designer laying out a " +
            "form, then by the engineers building it on two platforms. Which grey, how much space above " +
            "the heading, what disabled looks like. Each answer is reasonable on its own; together they drift.",
    )
    p(
        "Lemonade settles them once. A designer reaches for a pattern that already works instead of " +
            "redrawing a form from scratch; an engineer gets a component that already knows its states. " +
            "Both spend their effort on whatever is genuinely new about the screen.",
    )
    p(
        "Change is the expensive half. A shifted palette or a new control state would otherwise be " +
            "corrected by hand, everywhere, forever. Lemonade's values come from one Figma export, so a " +
            "change lands once and travels.",
    )

    h2("What it gives you")

    h3("Design tokens")
    p {
        +"The named design decisions the system is built from — a colour, a text size, a unit of "
        +"space. You use the name rather than the value, so what you write carries its meaning, adapts "
        +"to light and dark, and keeps working when the value changes. They are listed in "
        link(text = "Foundations", route = DocRoute.Colour)
        +"."
    }

    h3("Components")
    p(
        "Buttons, text fields, selection controls, list items, tabs, notices, toasts and tooltips, " +
            "among others — each already carrying its states, its theming and its accessibility behaviour.",
    )

    h3("Standards")
    p {
        +"The reasoning that makes the pieces fit — "
        link(text = "semantic tokens over raw values", route = DocRoute.SemanticTokens)
        +", how "
        link(text = "theming and dark mode", route = DocRoute.Theming)
        +" actually work, the "
        link(text = "accessibility", route = DocRoute.Accessibility)
        +" baseline, and the "
        link(text = "layout rhythm", route = DocRoute.Rhythm)
        +" everything sits on."
    }

    h3("Patterns")
    p {
        +"Whole solutions rather than parts — a "
        link(text = "form", route = DocRoute.PatternForms)
        +", an "
        link(text = "empty state", route = DocRoute.EmptyAndLoading)
        +", an "
        link(text = "error", route = DocRoute.Errors)
        +" — assembled from components with the decisions written down."
    }

    h2("Where it runs")
    table(headers = listOf("Platform", "Built with", "Targets", "Status")) {
        row(
            { b("Kotlin Multiplatform") },
            { +"Compose Multiplatform" },
            { +"Android, iOS, JVM Desktop, browser (wasm)" },
            { +"Published" },
        )
        row(
            { b("SwiftUI") },
            { +"Native SwiftUI" },
            { +"iOS 15+, macOS 12+" },
            { +"Published" },
        )
        row(
            { b("Flutter") },
            { +"Flutter" },
            { +"Android, iOS, Web" },
            { +"Unmaintained" },
        )
    }
    p {
        +"The published KMP modules are "
        c("core")
        +", "
        c("tokens")
        +", "
        c("ui")
        +", "
        c("expressive")
        +" and "
        c("calendar")
        +". Their public surface is locked by a binary compatibility check, so an upgrade will not "
        +"break your build without someone deciding it should."
    }
    callout(voice = NoticeVoice.Info, title = "Flutter support") {
        p("Flutter was an earlier implementation and is no longer maintained. Nothing new should start there.")
    }

    h2("Where it stops")

    h3("There is no web component library for non-Compose stacks")
    p {
        +"The KMP modules build for the browser — this site is written with them — so a Compose "
        +"Multiplatform app can target the web today. What does not exist is a CSS or React package "
        +"for a web stack that is not Compose. If you are putting together a web prototype in one of "
        +"those, start with "
        link(text = "Prototyping with Lemonade", route = DocRoute.Prototyping)
        +", which explains how to borrow the values by hand and what you may safely approximate."
    }

    h3("Lemonade is not every screen")
    p(
        "It gives you the vocabulary, not the product. Where nothing in the system fits what you are " +
            "building, that is a gap worth raising with the design systems team rather than routing " +
            "around quietly.",
    )

    h2("Start here")
    bullets {
        item("Building on Android, iOS, Desktop or the browser — Kotlin Multiplatform")
        item("Building a native Apple app — SwiftUI")
        item("Prototyping on the web outside Compose — Prototyping with Lemonade")
        item("Designing rather than building — Foundations for the raw values, Standards for the reasoning")
    }
    nextSteps(DocRoute.Kmp, DocRoute.SwiftUi, DocRoute.Prototyping, DocRoute.Design)
}

internal val designPage: DocPage = docPage(
    route = DocRoute.Design,
    title = "Design",
    description = "How Lemonade is set up in Figma — the libraries, what each holds, and how to enable them.",
) {
    p {
        +"Lemonade is designed in "
        b("Figma")
        +". The libraries there are not a picture of the system; they are the system's other half. The "
        +"values in them are exported and generated into the platform code, so a token you pick in "
        +"Figma is the same token an engineer writes."
    }
    p(
        "This page gets you set up. Everything else — the values, the reasoning, the assembled " +
            "solutions — is linked at the bottom.",
    )

    h2("How the libraries are organised")
    p("Four published libraries, split by what they hold rather than by who uses them.")
    table(headers = listOf("Library", "What it holds")) {
        row(
            { href(text = "Components", url = FIGMA_COMPONENTS) },
            {
                +"The components — buttons, inputs, cards, list items, top bars and the rest, with "
                +"their variants and states."
            },
        )
        row(
            { href(text = "Foundations", url = FIGMA_FOUNDATIONS) },
            {
                +"The semantic tokens and the scales behind them: colour, typography, spacing, "
                +"radius, elevation, opacity, border width."
            },
        )
        row(
            { href(text = "Icons", url = FIGMA_ICONS) },
            { +"The icon set." },
        )
        row(
            { href(text = "Global Colors", url = FIGMA_GLOBAL_COLORS) },
            { +"The raw colour ramps — green-lime/500, blue/alpha/200 and their neighbours." },
        )
    }
    p {
        +"The split between the last two is the one worth understanding. "
        b("Global Colors is the primitive layer")
        +" — every ramp, unopinionated about meaning. "
        b("Foundations sits on top and names the job")
        +": "
        c("bg-critical-subtle")
        +", "
        c("content-secondary")
        +". Design against Foundations, the same way engineers are asked to. A primitive picked "
        +"directly is a light-mode-only decision that will not survive a theme change or a palette "
        +"update — the reasoning is in "
        link(text = "Semantic tokens first", route = DocRoute.SemanticTokens)
        +"."
    }

    h2("Enabling them in a file")
    p {
        b("New files usually have them already.")
        +" The core libraries are set as team defaults, so a file created in the team inherits them "
        +"and there is nothing to do."
    }
    p("If a file is missing one — an older file, or one that came from elsewhere:")
    steps {
        item {
            +"Open the "
            b("Assets")
            +" panel in the left sidebar."
        }
        item {
            +"Click the "
            b("Libraries")
            +" button (the book icon)."
        }
        item("Enable the library you need.")
    }
    p {
        +"For ordinary product work, enable "
        b("Components")
        +", "
        b("Foundations")
        +" and "
        b("Icons")
        +". Global Colors is there for the rarer job of defining a new semantic token, and can stay "
        +"off the rest of the time."
    }
    nextSteps(DocRoute.Colour, DocRoute.SemanticTokens, DocRoute.PatternForms, DocRoute.Prototyping)
}

internal val kmpPage: DocPage = docPage(
    route = DocRoute.Kmp,
    title = "Kotlin Multiplatform",
    description = "Add Lemonade to a Kotlin Multiplatform project and render your first component.",
) {
    p {
        +"Add the dependency to your version catalog. The version below is the latest published at "
        +"the time of writing; check the repository's tag list for a "
        c("lemonade-kmp-*")
        +" tag newer than 0.38.1 (tags have no "
        c("v")
        +" prefix) before you publish."
    }
    code(
        language = CodeLanguage.Toml,
        source = """
            [versions]
            lemonade = "0.38.1"

            [libraries]
            lemonade-ui = { module = "com.teya.foundation:lemonade-ui", version.ref = "lemonade" }
        """,
    )
    p {
        +"Then pull it into "
        c("commonMain")
        +":"
    }
    code(
        language = CodeLanguage.Kotlin,
        source = """
            kotlin {
                sourceSets {
                    val commonMain by getting {
                        dependencies {
                            implementation(libs.lemonade.ui)
                        }
                    }
                }
            }
        """,
    )
    p("Wrap your app once, at the root:")
    code(
        language = CodeLanguage.Kotlin,
        source = """
            @Composable
            fun App() {
                LemonadeTheme {
                    MyScreenContent()
                }
            }
        """,
    )
    p {
        c("LemonadeTheme")
        +" is required, not optional: colour has no fallback, so the first component that reads one "
        +"throws at composition time if you skip the wrapper. Wrap at the root before rendering "
        +"anything. See "
        link(text = "Theming & dark mode", route = DocRoute.Theming)
        +" for why."
    }
    p {
        +"Components hang off the "
        c("LemonadeUi")
        +" object, which exists so autocomplete tells you what the design system offers and so a "
        +"reader can see at a glance where a component came from:"
    }
    code(
        language = CodeLanguage.Kotlin,
        source = """
            import com.teya.lemonade.LemonadeUi

            @Composable
            fun MyScreenContent() {
                var checked by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.padding(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.Text(text = "Notifications")
                    LemonadeUi.Switch(checked = checked, onCheckedChange = { checked = it })
                }
            }
        """,
    )
    nextSteps(DocRoute.SemanticTokens, DocRoute.PatternForms, DocRoute.Colour)
}

internal val swiftUiPage: DocPage = docPage(
    route = DocRoute.SwiftUi,
    title = "SwiftUI",
    description = "Add Lemonade to a SwiftUI project and render your first component.",
) {
    callout(voice = NoticeVoice.Warning, title = "Unverified") {
        p(
            "These installation steps have not been walked through end to end. If you follow them " +
                "and something is wrong, please correct this page.",
        )
    }
    p {
        +"Add the package in Xcode via "
        b("File → Add Package Dependencies")
        +", pointing at "
        c("github.com/saltpay/lemonade-design-system")
        +", and pick the most recent "
        c("lemonade-swiftui-*")
        +" tag (no "
        c("v")
        +" prefix — for example lemonade-swiftui-0.38.1)."
    }
    p {
        +"The same "
        c("LemonadeUi")
        +" namespace applies:"
    }
    code(
        language = CodeLanguage.Swift,
        source = """
            import Lemonade

            struct MyScreen: View {
                @State private var checked = false

                var body: some View {
                    VStack {
                        LemonadeUi.Text("Notifications")
                        LemonadeUi.Switch(checked: checked, onCheckedChange: { checked = ${'$'}0 })
                    }
                }
            }
        """,
    )
    nextSteps(DocRoute.SemanticTokens, DocRoute.PatternForms, DocRoute.Colour)
}
