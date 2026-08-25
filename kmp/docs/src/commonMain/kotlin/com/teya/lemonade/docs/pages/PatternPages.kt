package com.teya.lemonade.docs.pages

import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.content.CodeLanguage
import com.teya.lemonade.docs.content.DocPage
import com.teya.lemonade.docs.content.docPage

internal val formsPage: DocPage = docPage(
    route = DocRoute.PatternForms,
    title = "Forms",
    description = "Assembling a form — labels, support text, validation timing, and errors.",
) {
    p(
        "A form is the most common screen in Teya's products and the easiest to get subtly wrong. " +
            "This is how Lemonade expects one to be built.",
    )

    h2("The shape of a field")
    p("Every text input in Lemonade is one component with a few slots of copy around it, and each slot has one job.")
    table(headers = listOf("Slot", "Job", "When to use it")) {
        row({ c("label") }, { +"Names the field" }, { +"Always. A placeholder is not a label." })
        row(
            { c("optionalIndicator") },
            { +"Marks a field as not required" },
            {
                +"When most fields on the form "
                i("are")
                +" required"
            },
        )
        row({ c("placeholderText") }, { +"Shows the expected format" }, { +"When format is genuinely ambiguous" })
        row({ c("supportText") }, { +"Explains why you're asking" }, { +"When the reason isn't obvious" })
        row(
            { c("errorMessage") },
            { +"Says what to fix" },
            {
                +"Only with "
                c("error = true")
            },
        )
    }
    code(
        language = CodeLanguage.Kotlin,
        source = """
            LemonadeUi.TextField(
                input = email,
                onInputChanged = { email = it },
                label = "Email address",
                supportText = "We'll send your receipt here",
                placeholderText = "name@company.com",
                errorMessage = "Enter an email address in the format name@company.com",
                error = emailTouched && !email.isValidEmail,
            )
        """,
    )
    code(
        language = CodeLanguage.Swift,
        source = """
            LemonadeUi.TextField(
                input: ${'$'}email,
                onInputChanged: { email = ${'$'}0 },
                label: "Email address",
                supportText: "We'll send your receipt here",
                placeholderText: "name@company.com",
                errorMessage: "Enter an email address in the format name@company.com",
                error: emailTouched && !email.isValidEmail
            )
        """,
    )
    p(
        "The two platforms take the same arguments in the same order. A form written on one should be " +
            "readable by someone who works on the other.",
    )

    h2("Placeholders are not labels")
    p(
        "Putting the label in the placeholder looks tidy on an empty form and fails the moment someone " +
            "types — the label vanishes exactly when it becomes useful for checking your work. It also " +
            "breaks screen reader navigation, because there is nothing left to announce.",
    )
    p("If a field is so obvious it seems not to need a label, it still needs a label.")

    h2("When to validate")
    p(
        "Validate on blur, not on keystroke. A field that turns red while you are still typing the " +
            "second character of your email address is telling you that you are wrong before you have " +
            "finished being right.",
    )
    bullets {
        item {
            b("On blur")
            +" — the field has lost focus and the user has finished their thought"
        }
        item {
            b("On submit")
            +" — everything gets re-checked, and focus moves to the first field in error"
        }
        item {
            b("While typing")
            +" — only to "
            i("clear")
            +" an error that has already been shown"
        }
    }
    p(
        "That last one matters. Once a field is in error, correcting it should return it to normal " +
            "immediately; making the user blur the field again to be told they've fixed it is needless.",
    )

    h2("Writing the error")
    p(
        "An error message replaces support text, so it has to carry the whole explanation. Say what " +
            "to do, not what went wrong.",
    )
    table(headers = listOf("", "")) {
        row({ b("Do") }, { +"Enter an email address in the format name@company.com" })
        row({ b("Don't") }, { +"Invalid input" })
    }
    p {
        +"Keep the field's own error message specific to the field. Errors that apply to the whole "
        +"form — a failed submission, a server that timed out — belong in a Notice above the form, not "
        +"attached to a field the user cannot fix. See "
        link(text = "Errors & recovery", route = DocRoute.Errors)
        +"."
    }

    h2("Spacing a form")
    p {
        +"Use "
        c("spacing-400")
        +" between fields and "
        c("spacing-600")
        +" between groups of fields. The gap between a field and its own support text is part of the "
        +"component and is not yours to adjust — if it looks wrong, that's a component bug worth "
        +"reporting rather than a padding to override."
    }

    h2("Submit")
    p(
        "One primary button, at the end, doing the thing the form is named after. Keep it enabled even " +
            "when the form is incomplete: disabled submit buttons give no explanation for why nothing " +
            "happens, whereas a form that validates on press can point at the field that needs attention.",
    )
    nextSteps(DocRoute.Errors, DocRoute.Accessibility, DocRoute.Rhythm)
}

internal val patternListsPage: DocPage = docPage(
    route = DocRoute.PatternLists,
    title = "Lists",
    description = "Choosing a list item, handling selection, and when a divider helps.",
) {
    p(
        "Most screens in Teya's products are lists of something — transactions, resources, settings, " +
            "people. Lemonade gives you five list-item components, not one, because a row that displays " +
            "a balance and a row that toggles a setting are doing different jobs even when they're the " +
            "same height.",
    )

    h2("Choosing a list item")
    p("Both platforms ship the same five.")
    table(headers = listOf("Component", "What the row does")) {
        row(
            { c("ListItem") },
            {
                +"The general-purpose row — a label, optional support text, leading and trailing slots, "
                +"an optional tap action and chevron. The default choice when nothing more specific fits."
            },
        )
        row(
            { c("ActionListItem") },
            {
                +"The same shape, with an optional line above the label and a slot below the support "
                +"text. Reach for it when a row needs more text stacked than label-plus-support-text."
            },
        )
        row(
            { c("ResourceListItem") },
            {
                +"A label-value pair built for a resource's info — a required leading slot, a value in "
                +"the trailing position, and an optional tag or badge underneath the value."
            },
        )
        row(
            { c("ContentListItem") },
            {
                +"Display-only label-value pairs, horizontal or vertical. It has no click handler at "
                +"all — use it for read-only data, not for anything tappable."
            },
        )
        row({ c("SelectListItem") }, { +"Selection, and only selection. See below." })
    }
    p {
        +"Pick by what the row does, not by which one happens to render closest to the mock. A row that "
        +"shows a balance and lets you tap through to a statement is a "
        c("ResourceListItem")
        +" with "
        c("onItemClicked")
        +" set, not a "
        c("ListItem")
        +" with the value crammed into the trailing slot."
    }

    h2("Selection")
    p {
        c("SelectListItem")
        +" is the only list item built for choosing. Its "
        c("type")
        +" — Single, Multiple, or Toggle — decides which control renders (radio button, checkbox, or "
        +"switch) and how a tap behaves: a Single item that's already checked ignores the next tap, so "
        +"you can't uncheck a radio button by tapping it again."
    }
    p {
        +"The whole row is the click target, not just the control. The "
        c("onItemClicked")
        +" handler is attached to the entire row's interactive background, so a user can tap the label, "
        +"the support text, or the leading icon and get the same result as tapping the switch itself. "
        +"Don't shrink the hit area back down to the control — that's undoing work the component "
        +"already did for you."
    }
    p {
        c("SelectListItem")
        +" has two visual variants: Plain, a bare row meant to sit inside a surrounding Card or list "
        +"surface, and Outlined, which draws its own bordered container with a brand-tinted background "
        +"when selected, for items that need to stand alone rather than stack inside a shared surface."
    }

    h2("Dividers")
    p {
        +"Every list item takes a "
        c("showDivider")
        +" flag, off by default. A divider is for separating things that are actually different — a "
        +"settings section from the one below it, a list from a summary row that follows it. A uniform "
        +"list of like items almost always reads better with padding alone; a divider under every row "
        +"turns into visual noise at that density."
    }
    callout(voice = NoticeVoice.Warning) {
        p {
            +"Tabs are not symmetric here. On SwiftUI, Tabs takes its own "
            c("showDivider")
            +" flag, defaulting to true, for the rule under the tab strip. On KMP, Tabs always draws "
            +"that rule — there's no parameter to turn it off. If you're building a tab strip that "
            +"needs to blend into the surface below it, that's a KMP gap to route around rather than a "
            +"property you're missing."
        }
    }

    h2("Long content")
    p {
        +"Every list item's "
        c("label")
        +" and "
        c("supportText")
        +" default to no line limit and clipping overflow — nothing truncates unless you opt in with "
        c("labelMaxLines")
        +" or "
        c("supportTextMaxLines")
        +". That default is deliberate: a row should wrap rather than cut off content that's meant to "
        +"be read."
    }
    p(
        "Truncation is for identifiers — a long reference number, a UUID, a file name that's mostly " +
            "noise past the first twenty characters. It's the wrong tool for prose. If a support line " +
            "genuinely doesn't fit in the space you have, let the row grow instead of lopping the " +
            "sentence off with an ellipsis.",
    )
    nextSteps(DocRoute.LayoutLists, DocRoute.EmptyAndLoading, DocRoute.BlockGaps)
}

internal val emptyAndLoadingPage: DocPage = docPage(
    route = DocRoute.EmptyAndLoading,
    title = "Empty & loading states",
    description = "What to show before there is anything to show.",
) {
    p(
        "Every screen that shows data has to answer the same question first: what does it show before " +
            "the data arrives, or when there isn't any? Getting this wrong is what makes an app feel " +
            "unfinished even when the happy path is polished.",
    )

    h2("Skeletons vs. spinners")
    p {
        +"Lemonade gives you both Skeleton (as "
        c("LineSkeleton")
        +", "
        c("CircleSkeleton")
        +" and "
        c("BlockSkeleton")
        +") and "
        c("Spinner")
        +", and they answer different questions."
    }
    p(
        "Use a skeleton when you already know the shape of what's coming and the wait is short — a " +
            "list of transactions whose rows you can lay out before the data lands, a profile card whose " +
            "fields you know in advance. The shimmer previews the layout so the content doesn't jump " +
            "when it arrives.",
    )
    p(
        "Use a spinner when neither of those holds — you don't know what the result will look like, or " +
            "the wait could run long enough that a static shape sitting on screen starts to look stuck " +
            "rather than loading.",
    )
    p(
        "Don't show both in the same region. A skeleton row with a spinner floating over it is two " +
            "components disagreeing about how long this is going to take.",
    )

    h2("Do not flash")
    p(
        "If the data usually arrives in a couple hundred milliseconds, don't show a loading state at " +
            "all. A skeleton that appears and vanishes almost immediately doesn't read as fast — it " +
            "reads as a flicker, and it's more disruptive than the brief blank moment it was meant to " +
            "smooth over. Debounce it: only switch to the loading state once the wait has run long " +
            "enough that showing nothing would look broken.",
    )

    h2("Empty is not an error")
    p(
        "An empty list is a normal, expected state — the first time someone opens a screen, after they " +
            "clear a filter down to nothing, right after they delete the last item. It is not a failure " +
            "and shouldn't be styled or worded like one.",
    )
    p(
        "An empty state needs two things: an explanation of why there's nothing here, and, where one " +
            "exists, the action that fills it. \"No saved cards yet\" paired with an \"Add a card\" " +
            "button gives someone somewhere to go. \"No saved cards yet\" on its own is half a sentence.",
    )

    h2("First-run vs. filtered-empty")
    p(
        "\"You haven't created anything yet\" and \"nothing matches this filter\" are different " +
            "states, and the fix for conflating them is usually a support ticket. The first-run empty " +
            "state should point at the action that creates the first item. A filtered-empty state should " +
            "point at clearing or adjusting the filter — offering to create something when the real " +
            "problem is that a filter is too narrow sends someone down the wrong path entirely.",
    )
    p(
        "If a screen can be empty for either reason, check which one you're in before you decide what " +
            "the empty state says. Same layout, different copy, different action.",
    )
    nextSteps(DocRoute.Errors, DocRoute.PatternLists)
}

internal val errorsPage: DocPage = docPage(
    route = DocRoute.Errors,
    title = "Errors & recovery",
    description = "Where an error belongs, and how to write one.",
) {
    p(
        "An error is information the user needs at the moment they need it. Where you put it and how " +
            "you word it decide whether that's true.",
    )

    h2("Put the error where the fix is")
    p("Lemonade gives you three places to surface a problem, and they aren't interchangeable.")
    bullets {
        item {
            b("Field-level messages")
            +" — a text field's own "
            c("errorMessage")
            +", shown with "
            c("error = true")
            +" — for a problem with that specific field. See "
            link(text = "Forms", route = DocRoute.PatternForms)
            +" for validation timing."
        }
        item {
            b("Notice")
            +", placed above the form, for a problem with the whole form or the request behind it — a "
            +"failed submission, a server that timed out. Give it "
            c("voice = Critical")
            +" and, where there's something to do about it, an "
            c("actionLabel")
            +" and "
            c("onActionClick")
            +"."
        }
        item {
            b("Toast")
            +", for a transient, background failure the user can't act on right now — a sync that "
            +"failed quietly, a background refresh that didn't come through. Toasts auto-dismiss (the "
            +"Loading voice is the one exception), so don't put anything in one that the user has to "
            +"read before they can continue."
        }
    }
    p(
        "The test is simple: if fixing the problem means correcting one field, the message belongs on " +
            "that field. If it means retrying or going somewhere else, it belongs in a Notice. If " +
            "there's nothing to do but acknowledge it happened, it's a Toast — and even then, only if " +
            "missing it wouldn't block anything.",
    )

    h2("Write the fix, not the fault")
    p("Say what to do, not what went wrong. This is the same rule Forms sets for field-level messages.")
    table(headers = listOf("", "")) {
        row({ b("Do") }, { +"Enter an email address in the format name@company.com" })
        row({ b("Don't") }, { +"Invalid input" })
    }
    p(
        "\"Invalid input\" is true and useless — it tells the user something is wrong without telling " +
            "them what wrong looks like or what right looks like. Every error message should survive " +
            "being read in isolation, out of context, by someone who doesn't know what the system was " +
            "trying to do when it failed.",
    )

    h2("Always leave a way forward")
    p(
        "Retry, go back, or a way to contact support — pick at least one. An error with no next step " +
            "is a dead end, and a dead end in a shipped product is a bug, not a UX nicety to revisit later.",
    )
    p {
        +"Notice has an "
        c("actionLabel")
        +" and "
        c("onActionClick")
        +" for exactly this. If the failure is retryable, wire the action to retry. If it isn't, the "
        +"action can still open a support channel or take the user back to where they were — the "
        +"requirement is that tapping something does something, not that it fixes the problem outright."
    }

    h2("Do not blame the user")
    p(
        "Avoid \"you failed to enter a valid email\" or \"you didn't complete this step.\" Describe " +
            "what happened and what to do next. The distinction isn't cosmetic — accusatory copy reads " +
            "as the system deflecting responsibility for a mistake that, from the user's side, might not " +
            "even be theirs: a flaky connection, a server timeout, a stale session. Neutral, instructive " +
            "wording holds up regardless of whose fault it actually was.",
    )
    nextSteps(DocRoute.PatternForms, DocRoute.EmptyAndLoading)
}
