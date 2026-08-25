package com.teya.lemonade.docs.tokens

import com.teya.lemonade.core.LemonadeFontSizes

/**
 * The block a designer pastes into an AI prototyping session.
 *
 * Every value in it is read from the token catalog rather than typed out, so the prompt cannot
 * drift from the export the platform code is generated from. A hand-maintained copy of the palette
 * would be the exact failure this site tells readers to avoid.
 */
internal fun prototypingPrompt(): String =
    buildString {
        appendLine("We're building a WEB PROTOTYPE for Teya, styled with the Lemonade design system.")
        appendLine()
        appendLine("IMPORTANT CONSTRAINT")
        appendLine("Lemonade ships Kotlin Multiplatform and SwiftUI. There is NO web component")
        appendLine("library for React, Vue or plain HTML. Do not import, install, or invent any")
        appendLine("Lemonade npm package or component. Build plain HTML/CSS (or React, if I ask)")
        appendLine("and style it with the raw values below. Treat these values as reference only:")
        appendLine("they are not a public API.")
        appendLine()
        appendLine("TYPEFACE")
        appendLine("Figtree throughout, loaded from Google Fonts.")
        appendLine("Weights: Regular 400, Medium 500, SemiBold 600, Bold 700.")
        appendLine("Sizes (px): ${fontSizeList()}.")
        appendLine("Body copy is 16. Never invent sizes between these steps.")
        appendLine()
        appendLine("COLOUR — light / dark. 8-digit hex is #RRGGBBAA.")
        PROMPT_COLORS.forEach { entry ->
            appendLine(colorLine(entry))
        }
        appendLine()
        appendLine("Pair a status TEXT colour with its matching SUBTLE fill — never a status text")
        appendLine("colour on a full-strength status background.")
        appendLine()
        appendLine("SPACING (px) — use only these: ${scaleList(LemonadeSpacingTokenDocs)}.")
        appendLine("16 between related fields, 24 between groups, 32+ between sections.")
        appendLine("Never use a value between two steps.")
        appendLine()
        appendLine("RADIUS (px) — use only these: ${scaleList(LemonadeRadiusTokenDocs)}.")
        appendLine("Cards and containers default to 24.")
        appendLine()
        appendLine("BEHAVIOUR RULES — these hold regardless of platform:")
        appendLine("- Every input has a visible label. A placeholder is NOT a label.")
        appendLine("- Validate on blur, not on keystroke. Clear an error as soon as it's corrected.")
        appendLine("- Error messages say what to fix (\"Enter an email in the format name@company.com\"),")
        appendLine("  not what went wrong (\"Invalid input\").")
        appendLine("- Field-level errors go on the field. Whole-form or server errors go in a notice")
        appendLine("  above the form. Toasts are only for transient things the user can't act on.")
        appendLine("- Empty states are normal, not errors — explain them and offer the action that")
        appendLine("  fills them. \"Nothing created yet\" and \"no results match this filter\" are")
        appendLine("  different states with different actions.")
        appendLine("- Minimum touch target 44x44pt (iOS) / 48x48dp (Android), even when the visible")
        appendLine("  control is smaller.")
        appendLine("- Support light and dark using the pairs above.")
        appendLine()
        append("Ask me before inventing any colour, size, spacing or radius not listed here.")
    }

private data class PromptColor(
    val group: String,
    val label: String,
    val token: String,
)

/**
 * The working subset: colour, type and spacing are what make a prototype read as Teya. Elevation
 * and opacity are left out on purpose — they matter less to a prototype's credibility, and every
 * line in the block costs attention.
 */
private val PROMPT_COLORS = listOf(
    PromptColor(group = "Surfaces", label = "bg-default", token = "bg-default"),
    PromptColor(group = "", label = "bg-subtle", token = "bg-subtle"),
    PromptColor(group = "Text/icons", label = "content-primary", token = "content-primary"),
    PromptColor(group = "", label = "content-secondary", token = "content-secondary"),
    PromptColor(group = "", label = "content-tertiary", token = "content-tertiary"),
    PromptColor(group = "Brand", label = "bg-brand", token = "bg-brand"),
    PromptColor(group = "Status text", label = "positive", token = "content-positive"),
    PromptColor(group = "", label = "caution", token = "content-caution"),
    PromptColor(group = "", label = "critical", token = "content-critical"),
    PromptColor(group = "", label = "info", token = "content-info"),
    PromptColor(group = "Status fills", label = "positive-subtle", token = "bg-positive-subtle"),
    PromptColor(group = "", label = "caution-subtle", token = "bg-caution-subtle"),
    PromptColor(group = "", label = "critical-subtle", token = "bg-critical-subtle"),
    PromptColor(group = "", label = "info-subtle", token = "bg-info-subtle"),
    PromptColor(group = "Borders", label = "neutral-low", token = "border-neutral-low"),
    PromptColor(group = "", label = "neutral-high", token = "border-neutral-high"),
)

private const val GROUP_WIDTH = 14
private const val LABEL_WIDTH = 18
private const val VALUE_WIDTH = 11

private fun colorLine(entry: PromptColor): String {
    val doc = LemonadeColorTokenDocs.firstOrNull { candidate ->
        candidate.name == entry.token
    }
        ?: return "${entry.group.padEnd(GROUP_WIDTH)}${entry.label.padEnd(LABEL_WIDTH)}(not exported)"
    val fixed = if (doc.fixed) "   (fixed in both themes)" else ""
    return entry.group.padEnd(GROUP_WIDTH) +
        entry.label.padEnd(LABEL_WIDTH) +
        doc.light
            .toHexString()
            .lowercase()
            .padEnd(VALUE_WIDTH) +
        "/ " +
        doc.dark.toHexString().lowercase() +
        fixed
}

private fun scaleList(tokens: List<ScaleTokenDoc>): String =
    tokens
        .map { token -> token.value.toInt() }
        .distinct()
        .sorted()
        .joinToString(", ")

private fun fontSizeList(): String =
    LemonadeFontSizes.entries
        .map { size -> size.value.toInt() }
        .distinct()
        .sorted()
        .joinToString(", ")
