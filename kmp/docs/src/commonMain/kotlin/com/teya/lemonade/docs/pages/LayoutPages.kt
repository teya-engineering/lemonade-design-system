package com.teya.lemonade.docs.pages

import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.CodeLanguage
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage

internal val rhythmPage: DocPage = docPage(
    route = DocRoute.Rhythm,
    title = "Layout & rhythm",
    description = "The spacing scale, and why staying on it matters more than any individual value.",
) {
    p(
        "Lemonade has one spacing scale, and padding, gaps and margins all come from it. The scale " +
            "itself matters less than staying on it: a screen built entirely from scale values reads as " +
            "deliberate, and a screen with even one arbitrary number dropped in reads as slightly off, " +
            "even if nobody can say exactly why.",
    )

    h2("One scale")
    p {
        +"There's no separate scale for padding versus gaps versus margins — it's the same sequence "
        +"of steps wherever you use it. See "
        link(text = "Space & shape", route = DocRoute.SpaceAndShape)
        +" for the full set of values."
    }
    p(
        "The reason to care isn't aesthetic purism. Two screens built from the same scale sit together " +
            "without friction, because every rhythm on the page is a multiple of the same unit. Mix in a " +
            "value that isn't on the scale — even one that's only a couple of pixels off — and the screen " +
            "next to it starts to look misaligned, because it is.",
    )

    h2("Choosing a step")
    p {
        +"Within a component — around an icon, between a label and its value — reach for "
        c("spacing-200")
        +" or "
        c("spacing-300")
        +". Between related blocks on the same screen, "
        c("spacing-400")
        +" is the default. Between distinct sections, step up to "
        c("spacing-600")
        +" or beyond."
    }
    p(
        "When a gap feels like it wants something between two steps, that's the scale telling you to " +
            "step up, not a reason to invent a value. A slightly more generous gap at the next step almost " +
            "always reads better than a precise one that happens to sit off the scale.",
    )

    h2("Space belongs to the container")
    p(
        "A component owns its internal padding — the gap between a field and its own support text, the " +
            "padding inside a button. That space is not something a consumer reaches in and overrides " +
            "from outside.",
    )
    p(
        "If the space inside a component looks wrong, that's a component bug worth reporting, not a " +
            "padding value to fight from the outside. Overriding it locally fixes the one screen you're " +
            "looking at and leaves every other use of that component with the same problem, unfixed and " +
            "now inconsistent with the one you patched.",
    )

    h2("When the scale does not fit")
    p(
        "There are real exceptions. Optical adjustments — nudging an icon a pixel to sit visually " +
            "centred rather than mathematically centred, a single-pixel correction to stop a hairline " +
            "border from looking blurry — exist, and they don't come from the spacing scale.",
    )
    p(
        "These are deliberate, made once by someone looking carefully at the result, not a general " +
            "licence to leave the scale whenever a value is inconvenient. Most of the time the answer is " +
            "a different step, not a value off the scale.",
    )
    nextSteps(DocRoute.ContainerMargins, DocRoute.BlockGaps, DocRoute.SpaceAndShape)
}

internal val containerMarginsPage: DocPage = docPage(
    route = DocRoute.ContainerMargins,
    title = "Container margins",
    description =
        "How far content sits from the edge it lives inside — the viewport, a card, or a container.",
) {
    p {
        +"Content is spaced "
        b("16px from the edge it sits in")
        +" — the device viewport, or the container around it. That one number is most of what layout "
        +"consistency comes down to on a screen."
    }

    h2("Visually 16px, not structurally 16px")
    p {
        +"All content must be "
        b("visually")
        +" spaced 16px from the device's or the container's edge. Readability, comfort and consistency "
        +"across screens all follow from it."
    }
    callout(voice = NoticeVoice.Info) {
        p {
            +"Visually means the content "
            i("itself")
            +" respects the margin, not necessarily the outer container. If a component includes an "
            +"invisible container, wrapper, or interactive layer, you have to account for that internal "
            +"padding when you position it."
        }
    }
    p {
        +"That distinction is the whole subject. A frame whose edge sits at 16px is not the same as "
        +"content that "
        i("looks")
        +" 16px in — and the reader only ever sees the second one."
    }

    h3("At the device edge")
    table(headers = listOf("", "")) {
        row({ b("Do") }, { +"Position content visually 16px from the edge of the device." })
        row({ b("Don't") }, { +"Position content visually more than 16px from the edge of the device." })
    }

    h3("At a container edge")
    p(
        "For content inside a large visible container such as a card, use the same value for the " +
            "vertical padding as for the horizontal.",
    )
    table(headers = listOf("", "")) {
        row({ b("Do") }, { +"Keep the same visual padding on all sides." })
        row({ b("Don't") }, { +"Use different visual paddings on different sides." })
    }
    callout(voice = NoticeVoice.Info) {
        p(
            "Smaller containers, and anything you are aligning optically, can take less. Optical " +
                "alignment sometimes needs a value that is not on the scale at all. The recommended " +
                "default is a starting point — what matters is that the layout looks right.",
        )
    }

    h2("When the padding is invisible")
    p {
        +"Interactive elements usually carry padding you cannot see — a tap target, a hover or pressed "
        +"background, a safe area. When a container has inset padding and that padding is not visible, "
        +"its placement has to compensate, so that the "
        b("visible content still sits 16px from the screen edge")
        +"."
    }
    p {
        b("Lemonade's interactive components already do this.")
        +" They are built with the 16px visual margin in mind, so you do not need to wrap them in a "
        +"container to compensate. A list item that shows an interaction background when tapped already "
        +"handles its own safe area and interactive container: place it flush and the content inside "
        +"will land in the right place."
    }
    p(
        "Wrapping one anyway is the common mistake. It pushes the visible content to 32px and leaves " +
            "the row misaligned with everything above and below it.",
    )

    h3("Inside a container that already has padding")
    p(
        "For content inside a visual container — a card, say — that already has its own padding, do " +
            "not add margin values on top.",
    )
    table(headers = listOf("", "")) {
        row(
            { b("Do") },
            {
                +"Position content visually 16px from the parent container's edge, aligned with the "
                +"other content in that container."
            },
        )
        row(
            { b("Don't") },
            {
                +"Push content further than 16px from a container with visible boundaries, or misalign "
                +"it from the content beside it."
            },
        )
    }
    nextSteps(DocRoute.Rhythm, DocRoute.PatternLists)
}

internal val blockGapsPage: DocPage = docPage(
    route = DocRoute.BlockGaps,
    title = "Block gaps",
    description = "How much space to leave between blocks, and which spacing token each gap comes from.",
) {
    p(
        "Sections group related content, and the gap between them is what tells a reader where one " +
            "group ends and the next begins. Getting these four values right does most of the work of " +
            "making a screen legible.",
    )
    table(headers = listOf("Gap between", "Value", "Token")) {
        row({ +"Sections" }, { +"32px" }, { c("spacing-800") })
        row({ +"Cards" }, { +"16px" }, { c("spacing-400") })
        row({ +"Medium elements — list items" }, { +"12px" }, { c("spacing-300") })
        row({ +"Small elements — tags, chips" }, { +"8px" }, { c("spacing-200") })
        row({ +"Stacked text" }, { +"none" }, { +"—" })
    }
    p {
        +"The pattern behind the numbers: "
        b("the more self-contained the things you are separating, the larger the gap.")
        +" A section is a whole idea, a chip is a fragment of one."
    }

    h2("Between sections")
    p {
        +"A section that holds different kinds of element — a heading with a card under it, say — is "
        +"separated from the next by "
        b("32px")
        +" ("
        c("spacing-800")
        +")."
    }
    p(
        "This is the biggest gap on the page, and it should be. It is the only one doing structural " +
            "work: everything inside a section reads as one idea, and 32px is what says the next idea " +
            "has started.",
    )

    h2("Between cards")
    p {
        +"When stacking cards, or any container whose boundaries are visible, the gap is "
        b("16px")
        +" ("
        c("spacing-400")
        +"). The container's own edge is already doing some of the separating, so the gap does not have "
        +"to work as hard as it does between sections."
    }

    h2("Between medium elements")
    p {
        +"List items and similar mid-sized elements sit "
        b("12px")
        +" ("
        c("spacing-300")
        +") apart."
    }

    h2("Between small elements")
    p {
        +"Tags, chips and similar small elements usually sit next to each other rather than stacked, "
        +"and take "
        b("8px")
        +" ("
        c("spacing-200")
        +"). At that distance they read as members of one group rather than as separate controls — "
        +"which is the point. Pull them further apart and the grouping falls away."
    }

    h2("Stacked text needs no gap")
    p {
        +"Labels stacked directly on top of each other need "
        b("no gap at all")
        +". Lemonade's text styles already carry enough line height to separate them, and adding space "
        +"on top of that breaks the relationship between a label and the value under it."
    }
    p(
        "For paragraphs, use the text style's own line height as the gap — a blank line's worth, not " +
            "an arbitrary value. Same principle: the type is already spaced, so let it do the work.",
    )
    callout(voice = NoticeVoice.Info) {
        p {
            +"This is the one place where reaching for a spacing token is the wrong instinct. Space "
            +"between lines of text belongs to the type, not to the layout — see "
            link(text = "Typography", route = DocRoute.Typography)
            +" for the line heights each style carries."
        }
    }
    nextSteps(DocRoute.Rhythm, DocRoute.ContainerMargins, DocRoute.SpaceAndShape)
}

internal val cardsAndContainersPage: DocPage = docPage(
    route = DocRoute.CardsAndContainers,
    title = "Cards and containers",
    description =
        "How to make a container read as a container, and how round its corners should be.",
) {
    p(
        "A card has to separate itself from the page behind it. There are three ways to do that, and " +
            "a rule for how round its corners should be.",
    )

    h2("Making a container visible")

    h3("Lighter than the page")
    p {
        b("Prefer this one.")
        +" Fill the container with a colour lighter than the page background — "
        c("bg-subtle")
        +" for the page, "
        c("bg-default")
        +" for the container — so the content inside it comes forward."
    }

    h3("Darker than the page")
    p {
        +"The reverse also works: "
        c("bg-default")
        +" for the page, "
        c("bg-subtle")
        +" or "
        c("bg-elevated")
        +" for the container. It is a legitimate choice, but it crowds the interface more quickly, "
        +"because every container adds visual weight instead of lifting content out of the page."
    }

    h3("Outlined")
    p {
        +"Give the page and the container the same fill, and separate them with a border — "
        c("border-neutral-low")
        +". The lightest of the three: nothing changes tone, so a screen full of outlined containers "
        +"stays calm."
    }
    callout(voice = NoticeVoice.Info) {
        p(
            "All three are valid. The choice is about how much weight a screen can carry, not about " +
                "which is correct — a dense screen usually wants outlines, a sparse one can afford fills.",
        )
    }

    h2("Radius")
    p {
        +"A card or visible container of any considerable size, sitting "
        b("directly on the page")
        +", takes a "
        b("24px")
        +" ("
        c("radius-600")
        +") radius."
    }
    callout(voice = NoticeVoice.Info) {
        p {
            +"The token export also carries "
            c("radius-container-default")
            +", which resolves to the same 24px. If you are picking a radius for a container, that is "
            +"the name that says what you mean — see "
            link(text = "Semantic tokens first", route = DocRoute.SemanticTokens)
            +"."
        }
    }

    h3("Containers inside containers")
    p(
        "A rounded box nested inside another rounded box cannot reuse the outer radius — it would sit " +
            "inside the parent's curve and read as slightly wrong without it being obvious why. Subtract " +
            "the padding between them:",
    )
    code(
        language = CodeLanguage.Plain,
        source = """
            inner_radius = outer_radius − container_padding

            16px − 4px = 12px
        """,
    )
    p {
        +"In tokens, nested containers usually land on "
        c("radius-400")
        +" (16px) or "
        c("radius-300")
        +" (12px), depending on the parent."
    }
    nextSteps(
        DocRoute.ContainerMargins,
        DocRoute.BlockGaps,
        DocRoute.SpaceAndShape,
        DocRoute.Elevation,
    )
}

internal val layoutListsPage: DocPage = docPage(
    route = DocRoute.LayoutLists,
    title = "Lists",
    description =
        "Stacking list items — which rows bring their own spacing, and when to use dividers.",
) {
    p(
        "Lists come in two kinds, and they want opposite things from you. Interactive rows bring their " +
            "own spacing; static rows bring none. Most list layout problems come from treating one like " +
            "the other.",
    )

    h2("Interactive items stack on their own")
    p {
        +"Lemonade's interactive list items — "
        b("Action List Item")
        +", "
        b("Resource List Item")
        +" and "
        b("Selection List Item")
        +" — already carry the padding, margin and divider they need, because they have to reserve room "
        +"for an interaction feedback area."
    }
    p {
        +"So stack them and stop. No extra margins, no gaps, no dividers drawn by hand. To show a "
        +"divider between rows, turn on the item's "
        c("showDivider")
        +" property rather than adding one yourself."
    }
    p {
        +"This holds whether the list sits "
        b("directly on the page")
        +" or "
        b("inside a container")
        +". The item does not change; only what is behind it does."
    }
    callout(voice = NoticeVoice.Info) {
        p {
            +"Wrapping an interactive row to add spacing is the common mistake. The row already "
            +"accounts for the 16px visual margin — see "
            link(text = "Container margins", route = DocRoute.ContainerMargins)
            +" — so wrapping it pushes the content in twice and breaks alignment with everything "
            +"around it."
        }
    }

    h2("Static items need spacing from you")
    p {
        +"Static items — "
        b("Content Item")
        +" — carry no extra padding, margin or divider. They have no interaction feedback area, so "
        +"nothing reserves space on their behalf and the spacing is yours to set."
    }
    p {
        +"Wrap them in a container with "
        b("16px")
        +" ("
        c("spacing-400")
        +") on all sides, and put "
        b("16px")
        +" between items."
    }
    table(headers = listOf("", "")) {
        row(
            { b("Do") },
            {
                +"Wrap static items in a container with an even 16px margin on all sides, and "
                +"16px between items."
            },
        )
        row(
            { b("Don't") },
            { +"Leave the spacings looking uneven or unbalanced across the four directions." },
        )
    }

    h2("When to use dividers")
    p {
        +"As a rule of thumb, use dividers in "
        b("interactive")
        +" lists, especially dense ones."
    }

    h3("Interactive lists")
    p("Good use cases:")
    bullets {
        item("Dense or text-heavy lists where the items look alike")
        item("Data lists — transactions, settlements, settings")
        item("Long lists, where a visual rhythm helps someone keep their place")
    }
    callout(voice = NoticeVoice.Info) {
        p(
            "In these cases dividers are structure, not decoration. They exist to help someone track " +
                "across a row and find the boundary between one item and the next.",
        )
    }

    h3("Static lists")
    p(
        "Good use case: separating groups of content inside cards and containers — a details panel " +
            "where a few related fields belong together and the next few belong apart.",
    )
    nextSteps(DocRoute.ContainerMargins, DocRoute.BlockGaps, DocRoute.PatternLists)
}
