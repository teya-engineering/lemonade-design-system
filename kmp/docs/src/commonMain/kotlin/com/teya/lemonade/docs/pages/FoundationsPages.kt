package com.teya.lemonade.docs.pages

import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage
import com.teya.lemonade.docs.tokens.ColorTokenGroup
import com.teya.lemonade.docs.tokens.ColourGallery
import com.teya.lemonade.docs.tokens.LemonadeBorderWidthTokenDocs
import com.teya.lemonade.docs.tokens.LemonadeOpacityTokenDocs
import com.teya.lemonade.docs.tokens.LemonadeRadiusTokenDocs
import com.teya.lemonade.docs.tokens.LemonadeSizeTokenDocs
import com.teya.lemonade.docs.tokens.LemonadeSpacingTokenDocs
import com.teya.lemonade.docs.tokens.ScaleGallery
import com.teya.lemonade.docs.tokens.ScalePreview
import com.teya.lemonade.docs.tokens.ShadowGallery
import com.teya.lemonade.docs.tokens.TypeGroup
import com.teya.lemonade.docs.tokens.TypeRamp
import com.teya.lemonade.docs.tokens.TypeSpecimens

internal val colourPage: DocPage = docPage(
    route = DocRoute.Colour,
    title = "Colour",
    description = "Lemonade's semantic colour tokens, read from the Figma export that generates the platform code.",
) {
    p {
        +"Every colour below is read at build time from "
        c("tokens/theme-colors.*.tokens.json")
        +" — the same files the Kotlin and Swift token converters generate from. When design "
        +"re-exports the Figma collection, this page changes with it. Nobody updates it by hand."
    }
    p {
        +"Each token carries its light and dark value, the guidance design wrote in Figma, and the "
        +"symbol to use on each platform. Tokens marked "
        b("fixed")
        +" resolve to the same value in both themes, on purpose — they are for surfaces that must not flip."
    }
    callout(voice = NoticeVoice.Warning, title = "Reach for these, not the primitives") {
        p {
            +"These are the semantic tokens. The primitive palette underneath them ("
            c("blue/500")
            +", "
            c("green-lime/alpha/200")
            +", and so on) exists to define these, not to be used in product code. See "
            link(text = "Semantic tokens first", route = DocRoute.SemanticTokens)
            +"."
        }
    }

    h2("Background")
    p("Surfaces, from the page underneath everything to the elevated layers on top of it.")
    sample { ColourGallery(group = ColorTokenGroup.Background) }

    h2("Content")
    p {
        +"Text and icons. "
        c("content-primary")
        +" carries body copy, "
        c("content-secondary")
        +" supporting copy, "
        c("content-tertiary")
        +" the quietest labels."
    }
    sample { ColourGallery(group = ColorTokenGroup.Content) }

    h2("Border")
    sample { ColourGallery(group = ColorTokenGroup.Border) }

    h2("Interaction")
    p(
        "Applied on press, hover and focus. These are layered over a background token rather than " +
            "replacing it.",
    )
    sample { ColourGallery(group = ColorTokenGroup.Interaction) }

    h2("Scoped")
    p(
        "Tied to one feature rather than reusable across the product — currently the Settlements " +
            "frequency badges. Don't reach for these outside that context; if you need the same visual " +
            "treatment elsewhere, that's a case for a general-purpose semantic token, not for reusing a " +
            "scoped one.",
    )
    sample { ColourGallery(group = ColorTokenGroup.Scoped) }

    h2("Shadow")
    p {
        +"The colour a shadow renders in, used together with the offset, blur and spread values on "
        link(text = "Elevation", route = DocRoute.Elevation)
        +"."
    }
    sample { ColourGallery(group = ColorTokenGroup.Shadow) }
}

internal val typographyPage: DocPage = docPage(
    route = DocRoute.Typography,
    title = "Typography",
    description = "The Lemonade text styles, and the scales they are built from.",
) {
    p {
        +"Lemonade sets everything in "
        b("Figtree")
        +", and product code reaches for a named style — "
        c("BodyMediumRegular")
        +", "
        c("HeadingSmall")
        +" — rather than for a size. A style bundles the size, the line height, the weight and the "
        +"letter spacing that belong together, so the pairing is decided once instead of at every call site."
    }

    h2("Styles and roles")

    h3("Display")
    p(
        "Display sizes are for large text on screen rather than for document structure. They add " +
            "hierarchy and weight to a single piece of information — a statistic, a balance, a total — and " +
            "do not need to carry heading semantics.",
    )
    sample { TypeSpecimens(group = TypeGroup.Display) }

    h3("Heading")
    p(
        "Headings are the navigational and organisational keystones of a screen: headlines, section " +
            "titles, the labels that let someone find their place. Pick the level by the structure it " +
            "expresses, not by the size you want.",
    )
    sample { TypeSpecimens(group = TypeGroup.Heading) }

    h3("Body")
    p(
        "Body is the workhorse. It is optimised for reading at length and for the short factual text " +
            "that fills components — form fields, buttons, labels, descriptions. Each size step comes in " +
            "the weights that step actually needs.",
    )
    sample { TypeSpecimens(group = TypeGroup.Body) }

    h3("Overline")
    p {
        +"A compact, letter-spaced style for metadata and small annotations that label the content next "
        +"to them without competing with it. The components uppercase the text for you — pass it in "
        +"ordinary case and let "
        c("LemonadeUi.Text")
        +" handle it, rather than shouting in the string."
    }
    sample { TypeSpecimens(group = TypeGroup.Overline) }

    h2("Choosing a style")
    p(
        "Pick by role, not by measurement. If a heading needs to be smaller, the answer is usually a " +
            "different heading level rather than a smaller token — text that steps outside the ramp stops " +
            "scaling with the rest of the interface when someone turns their font size up.",
    )
    p(
        "Reach past the named styles only when you are building something the system does not cover " +
            "yet, and treat that as a gap worth raising rather than a decision to make alone.",
    )

    h2("The scales underneath")
    p(
        "The styles above are assembled from these three scales. You rarely need them directly — reach " +
            "for a named style instead — but they are what a new style would be built from.",
    )
    p {
        +"Sizes are unitless design tokens — each platform maps them to its own unit ("
        c("sp")
        +" on Android, points on iOS), so the same token produces the correct result once the user's own "
        +"text-size preference is applied."
    }
    sample { TypeRamp() }
}

internal val spaceAndShapePage: DocPage = docPage(
    route = DocRoute.SpaceAndShape,
    title = "Space & shape",
    description = "The spacing scale and corner radius tokens that give Lemonade its rhythm.",
) {
    h2("Spacing")
    p(
        "One scale covers padding, gaps and margins. Values step in a way that keeps layouts on a " +
            "shared rhythm — mixing in arbitrary numbers is what makes screens feel subtly misaligned next " +
            "to each other.",
    )
    sample {
        ScaleGallery(tokens = LemonadeSpacingTokenDocs, preview = ScalePreview.Length)
    }

    h2("Radius")
    sample {
        ScaleGallery(tokens = LemonadeRadiusTokenDocs, preview = ScalePreview.Radius)
    }
    p {
        c("radius-full")
        +" is the pill shape — it resolves to a value large enough to fully round whatever it is "
        +"applied to, so it stays a pill at any height."
    }

    h2("Sizes")
    p(
        "Fixed dimensions for things that are not spacing — icon boxes, control heights, avatar " +
            "diameters. Reach for a size token when a component needs a specific measurement rather than a gap.",
    )
    sample {
        ScaleGallery(tokens = LemonadeSizeTokenDocs, preview = ScalePreview.Length)
    }
}

internal val elevationPage: DocPage = docPage(
    route = DocRoute.Elevation,
    title = "Elevation",
    description = "The shadow sets that lift Lemonade surfaces off the page.",
) {
    p(
        "Shadows are stored decomposed in the Figma export — each size is one or two layers, and each " +
            "layer is four or five separate variables (offset, blur, spread, colour). A usable shadow is " +
            "the composition of every layer at a given size. Using one layer alone is not a smaller " +
            "shadow — it is a broken one.",
    )
    sample { ShadowGallery() }

    h2("Picking a size")
    p(
        "Elevation is ordinal: pick a shadow by how far the surface should sit above the page — a " +
            "card, a menu, a modal — not by how the shadow looks in isolation. Reach for the next size up " +
            "when a surface needs to read as closer to the user, not for a shadow that merely looks stronger.",
    )
    p(
        "The samples above are drawn by the same renderer a product screen uses, so what you see here " +
            "is what a Compose surface gets — not an approximation of it.",
    )
}

internal val opacityAndBordersPage: DocPage = docPage(
    route = DocRoute.OpacityAndBorders,
    title = "Opacity & borders",
    description = "Opacity levels and border widths, including the state tokens for focus and selection.",
) {
    h2("Opacity")
    p {
        +"Values are percentages. "
        c("base/*")
        +" tokens are raw levels with no attached meaning — pick one because you need 40% "
        +"see-through, not because of what it is for. "
        c("state/*")
        +" tokens, such as "
        c("opacity-disabled")
        +", carry meaning: they encode a specific UI state, so reach for the state token instead of the "
        +"base level it happens to resolve to whenever the situation matches."
    }
    sample {
        ScaleGallery(tokens = LemonadeOpacityTokenDocs, preview = ScalePreview.Percent)
    }

    h2("Border widths")
    sample {
        ScaleGallery(tokens = LemonadeBorderWidthTokenDocs, preview = ScalePreview.None)
    }
    p {
        c("focus-ring")
        +" and "
        c("border-selected")
        +" exist so focus and selection stay consistent across components, wherever they show up. Both "
        +"currently resolve to the same 2px as "
        c("border-50")
        +", but that is incidental, not a guarantee — do not replace either state token with the raw "
        +"value. The state tokens are free to diverge later, and components that referenced them by "
        +"meaning will pick up the change automatically; components that reached for the raw value will not."
    }
}
