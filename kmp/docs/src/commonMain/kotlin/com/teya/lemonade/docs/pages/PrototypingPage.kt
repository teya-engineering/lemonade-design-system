package com.teya.lemonade.docs.pages

import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.CodeLanguage
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage
import com.teya.lemonade.docs.tokens.prototypingPrompt

internal val prototypingPage: DocPage = docPage(
    route = DocRoute.Prototyping,
    title = "Prototyping with Lemonade",
    description = "How to build a web prototype that reads as Teya, including a block to paste into an AI session.",
) {
    p(
        "For designers, product managers and anyone validating an idea in a web prototype — usually " +
            "with an AI tool such as v0, Lovable or Claude.",
    )

    h2("Three things to know first")
    p {
        b("There is no Lemonade component library for the web.")
        +" The Kotlin Multiplatform modules build for the browser, so a Compose Multiplatform app can "
        +"target the web — but there is nothing to install from npm for React, Vue or plain HTML. An "
        +"AI tool will happily invent a "
        c("<LemonadeButton>")
        +" if you let it; that component does not exist, and a prototype built on it cannot be handed "
        +"to anyone."
    }
    p(
        "The values here are reference, not a contract. Copy them into your prototype as raw values. " +
            "If a web token package ever ships, the names and structure may differ from anything you " +
            "paste today.",
    )
    p {
        b("Approximate is the goal.")
        +" A prototype should read as Teya at a glance: right colours, right typeface, right rhythm. "
        +"It should not look production-shippable, because it is not. Chasing pixel fidelity builds a "
        +"second, unmaintained implementation of the design system."
    }

    h2("Load the typeface")
    p {
        +"Lemonade is set in "
        b("Figtree")
        +", which is on Google Fonts. Without it a prototype falls back to something generic and stops "
        +"looking like Teya, so load it first."
    }
    code(
        language = CodeLanguage.Html,
        source = """
            <link
              href="https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600;700&display=swap"
              rel="stylesheet"
            />
        """,
    )

    h2("Set up your AI session")
    p(
        "Paste this at the start of a prototyping session. It carries the visual foundations and the " +
            "behaviour rules that matter most.",
    )
    p(
        "Every value in it is read from the same Figma export the platform code is generated from, so " +
            "it cannot drift the way a hand-maintained copy of the palette would.",
    )
    code(language = CodeLanguage.Plain, source = prototypingPrompt())
    p(
        "That last line is deliberate: it makes the tool stop and check rather than confidently " +
            "improvise a value that is nearly right. Drop it if you find it slows you down.",
    )

    h2("Where the full values live")
    p {
        +"The block above is a working subset, chosen because colour, type and spacing are what make a "
        +"prototype read as Teya. The complete set — every token, its light and dark value, and the "
        +"usage note design wrote in Figma — is in Foundations. Elevation and opacity are left out of "
        +"the paste block on purpose: they matter less to a prototype's credibility than the three "
        +"above, and every line in that block costs attention."
    }
    nextSteps(
        DocRoute.Colour,
        DocRoute.Typography,
        DocRoute.SpaceAndShape,
        DocRoute.Elevation,
        DocRoute.OpacityAndBorders,
    )

    h2("The thinking behind the rules")
    p(
        "The behaviour rules are compressed to fit. When a prototype needs to get one right in detail, " +
            "the reasoning is written up in full, and it is platform-agnostic — it applies to a web " +
            "prototype exactly as it applies to production.",
    )
    nextSteps(
        DocRoute.SemanticTokens,
        DocRoute.Rhythm,
        DocRoute.Accessibility,
        DocRoute.PatternForms,
        DocRoute.EmptyAndLoading,
        DocRoute.Errors,
    )
}
