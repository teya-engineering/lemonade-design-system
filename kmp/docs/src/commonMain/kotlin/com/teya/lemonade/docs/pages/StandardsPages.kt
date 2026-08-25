package com.teya.lemonade.docs.pages

import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.CodeLanguage
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage

internal val semanticTokensPage: DocPage = docPage(
    route = DocRoute.SemanticTokens,
    title = "Semantic tokens first",
    description = "Why Lemonade asks you to name the job a colour is doing rather than the colour itself.",
) {
    p {
        +"Lemonade gives you two layers of colour. The "
        b("primitive")
        +" palette is the raw material — "
        c("green-lime/500")
        +", "
        c("blue/alpha/200")
        +". The "
        b("semantic")
        +" layer sits on top and names the job: "
        c("bg-critical-subtle")
        +", "
        c("content-secondary")
        +", "
        c("border-brand")
        +"."
    }
    p("Always reach for the semantic layer.")

    h2("Why it matters")
    p {
        +"A primitive says what a colour "
        i("is")
        +". A semantic token says what it is "
        i("for")
        +". That difference is what lets one codebase render correctly in light and dark, survive a "
        +"palette change, and stay legible when someone turns contrast up."
    }
    p {
        +"When you write "
        c("green-lime/500")
        +" into a screen, you have made a promise that this particular green is correct in every "
        +"theme, forever. It isn't. "
        c("content-positive")
        +" resolves to one green in light and a brighter one in dark, because the same green that "
        +"reads well on white disappears on near-black."
    }

    h2("What good looks like")
    p("Name the role, and both themes come for free.")
    code(
        language = CodeLanguage.Kotlin,
        source = """
            // Do
            LemonadeUi.Text(
                text = "Payment received",
                color = LemonadeTheme.colors.content.contentPositive,
            )

            // Don't — this is a light-theme-only decision
            LemonadeUi.Text(
                text = "Payment received",
                color = Color(0xFF497D00),
            )
        """,
    )
    code(
        language = CodeLanguage.Swift,
        source = """
            // Do
            LemonadeUi.Text("Payment received", color: LemonadeTheme.colors.content.contentPositive)
        """,
    )

    h2("Choosing the right one")
    p("Work down the name. The first segment is the property you're setting.")
    table(headers = listOf("Prefix", "Sets", "Example")) {
        row({ c("bg-") }, { +"A surface fill" }, { c("bg-critical-subtle") })
        row({ c("content-") }, { +"Text and icons" }, { c("content-secondary") })
        row({ c("border-") }, { +"A stroke" }, { c("border-neutral-low") })
    }
    p {
        +"The middle is the "
        i("voice")
        +" — positive, caution, critical, info, neutral, brand — and it should match the meaning of "
        +"what you're building, not the colour you had in mind. A destructive action is critical "
        +"because it is destructive, not because it happens to be red."
    }
    p {
        +"The suffix is the weight. "
        c("-subtle")
        +" is a tinted background you put content on top of; the unsuffixed version is the "
        +"full-strength colour. Pair them: "
        c("bg-critical-subtle")
        +" with "
        c("content-critical")
        +", never "
        c("bg-critical")
        +" with "
        c("content-critical")
        +"."
    }

    h2("When you may go lower")
    p("Rarely, and deliberately:")
    bullets {
        item {
            b("You are building a component for Lemonade itself.")
            +" New components define their own semantic tokens; that work happens with design, in "
            +"Figma, and lands as a token export."
        }
        item {
            b("You are rendering brand artwork")
            +" — a partner logo, an illustration — where the colour is content rather than interface."
        }
    }
    p(
        "Outside those two cases, a primitive in product code is a bug that hasn't been noticed yet. " +
            "If nothing in the semantic layer fits what you're building, that's a gap worth raising with " +
            "the design systems team rather than routing around.",
    )

    h2("Fixed tokens")
    p {
        +"A handful of tokens resolve to the same value in both themes, and their names say so: "
        c("bg-always-dark")
        +", "
        c("content-always-light")
        +", "
        c("content-critical-always-on-color")
        +". These are for surfaces that must not flip — a photo overlay, a coloured banner that keeps "
        +"its colour in dark mode. Using one because you want to pin a colour is a misuse; using one "
        +"because the surface underneath is genuinely fixed is correct."
    }
    nextSteps(DocRoute.Theming, DocRoute.Colour)
}

internal val themingPage: DocPage = docPage(
    route = DocRoute.Theming,
    title = "Theming & dark mode",
    description = "How Lemonade resolves light and dark, and what a host app controls.",
) {
    p {
        +"Lemonade does not have a light mode and a dark mode you switch between. It has one theme, "
        +"and every semantic token in it carries both a light and a dark value. Your job is to name "
        +"the role you want — "
        c("content-positive")
        +", "
        c("bg-critical-subtle")
        +" — and let the token resolve. The resolution isn't your problem."
    }

    h2("One theme, two resolutions")
    p {
        +"There is no "
        c("if (isDarkTheme)")
        +" in product code. When you write "
        c("content-positive")
        +", the token itself knows its light and dark values — that decision was made once, by design, "
        +"and every screen that reaches for the token gets it for free."
    }
    p(
        "Writing a theme conditional in your own code is a sign you've reached for a primitive, or " +
            "hard-coded a colour, instead of a semantic token. If you find yourself branching on theme, " +
            "back up and look for the token that already encodes the difference.",
    )

    h2("Wrapping your app")
    p("On Kotlin Multiplatform, wrap the app once, at the root.")
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
        +"Every parameter — colours, typography, spacing, radius, shapes — has a default and is "
        +"independently overridable, so a host app can swap in its own values for one axis without "
        +"touching the rest. Left alone, "
        c("LemonadeTheme")
        +" follows the system setting: light or dark based on what the device reports."
    }
    p {
        +"The wrapper is not optional. Colour has no fallback: skip "
        c("LemonadeTheme { }")
        +" and the first component that reads a colour throws at composition time, because there is "
        +"no default to fall back to. Every other axis does have a built-in default and would render "
        +"fine on its own, but that doesn't save you — colour is the one that's missing, and colour is "
        +"what almost every visual component reads. Wrap at the root, always."
    }
    p(
        "On SwiftUI there is no equivalent wrapper to add, and that's by design rather than an " +
            "oversight: colours resolve through Asset Catalog named colours that adapt to the system's " +
            "light/dark appearance automatically, so there's no root token to install before a colour " +
            "is available.",
    )

    h2("Fixed tokens")
    p {
        +"A handful of tokens are exceptions to everything resolving per theme: "
        c("bg-always-dark")
        +", "
        c("content-always-light")
        +", and the "
        c("*-always-on-color")
        +" family hold the same value in both themes. See "
        link(text = "Colour", route = DocRoute.Colour)
        +" for the full set marked fixed."
    }
    p(
        "These exist for surfaces that are genuinely fixed regardless of the surrounding theme — a " +
            "photo overlay, a coloured banner that keeps its own colour in dark mode. The content sitting " +
            "on top of that banner needs to stay legible against it no matter what theme the rest of the " +
            "screen is in, which is exactly what an always-on-color token is for.",
    )
    p(
        "Using one of these because the surface really doesn't flip is correct. Using one because you " +
            "want a colour to just stay put — because chasing it through both themes is inconvenient — is " +
            "a misuse.",
    )

    h2("Testing both themes")
    p(
        "Check both themes before merging, not just the one your simulator happened to boot into. The " +
            "two failures that show up nearly every time are the same: a hard-coded colour that was never " +
            "a token in the first place, and a fixed token reached for because a real dark-mode value was " +
            "never chosen. Neither is caught by looking at light mode alone.",
    )
    nextSteps(DocRoute.SemanticTokens, DocRoute.Accessibility)
}

internal val accessibilityPage: DocPage = docPage(
    route = DocRoute.Accessibility,
    title = "Accessibility",
    description = "The baseline every Lemonade screen is expected to meet.",
) {
    p(
        "Accessibility isn't a pass you make at the end. Most of it is decided by which token you " +
            "reach for and whether you fill in a label — decisions you're already making, so getting them " +
            "right costs nothing extra.",
    )

    h2("Contrast")
    p {
        +"Content tokens and background tokens are designed together, in pairs. "
        c("content-critical")
        +" is checked against "
        c("bg-critical-subtle")
        +", not against whatever background happens to be nearby. Pair a content token with its "
        +"matching background token and contrast is correct by construction — see "
        link(text = "Semantic tokens first", route = DocRoute.SemanticTokens)
        +" for how the naming maps a content token to the background it's meant for."
    }
    p {
        +"Contrast failures come from crossing families: putting "
        c("content-critical")
        +" on "
        c("bg-caution-subtle")
        +" because both looked fine in isolation, or laying content over a background that was never "
        +"the pair it was designed against. If a token combination looks wrong, check whether the two "
        +"are actually a matched pair before assuming the colours themselves are at fault."
    }

    h2("Touch targets")
    p(
        "Apple's Human Interface Guidelines set a 44×44pt minimum for anything a user taps; Android's " +
            "Material guidelines set a 48×48dp minimum. Neither figure converts exactly to the other " +
            "platform's unit, so Lemonade expects every interactive element to clear its own platform's " +
            "minimum — the visual size of the control is not the same as its hit area, and the hit area " +
            "is what each guideline is about.",
    )
    p(
        "Lemonade's own components don't all sit at that minimum by default: the smallest icon button " +
            "sizes render well below it. Where a control's visible size is smaller than its platform's " +
            "minimum, either choose a larger size or give it a comfortably large hit area so the tap " +
            "target still clears it.",
    )

    h2("Labels")
    p {
        +"Every interactive control needs an accessible label — something a screen reader can announce "
        +"that says what the control does. A placeholder is not a label: it disappears the moment "
        +"there's content in the field, which is exactly when a screen reader user needs it most. See "
        link(text = "Forms", route = DocRoute.PatternForms)
        +" for how this plays out on a text field specifically."
    }
    p(
        "Icon-only controls — an icon button with no visible text — always need an explicit label " +
            "describing the action, not the icon. \"Favorite\", not \"heart icon.\" Without one, a screen " +
            "reader has nothing to announce and the control is effectively invisible to anyone using one.",
    )

    h2("Text scaling")
    p {
        +"Type tokens carry a size that maps to "
        c("sp")
        +" on Android and points on iOS, which is what lets a user's system text-size setting actually "
        +"change the text on screen. Overriding a type token's size with a raw pixel value opts that "
        +"text out of the user's setting, which is the whole point of asking for a larger size in the "
        +"first place."
    }
    p(
        "The corollary is on your layout, not the token: a layout that assumes text stays at one " +
            "height will clip or overlap the moment someone scales it up. Don't put fixed heights around " +
            "text — let the container grow with it.",
    )

    h2("Focus")
    p {
        +"Focus needs to be visible. The "
        c("focus-ring")
        +" token exists for exactly this — see "
        link(text = "Opacity & borders", route = DocRoute.OpacityAndBorders)
        +" for how it sits alongside the other state tokens. Reach for it rather than inventing a "
        +"focus treatment, for the same reason any state token beats a raw value: components and "
        +"screens that use the token pick up any future change together, and the ones that didn't, won't."
    }
    p(
        "A control that a keyboard or switch-access user can reach but can't see focused is no better, " +
            "for them, than a control they can't reach at all.",
    )
    nextSteps(DocRoute.SemanticTokens, DocRoute.Theming)
}
