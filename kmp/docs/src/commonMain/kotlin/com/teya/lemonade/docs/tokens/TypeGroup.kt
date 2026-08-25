package com.teya.lemonade.docs.tokens

import com.teya.lemonade.core.LemonadeTypography

/**
 * The composite text styles, grouped by the role they play.
 *
 * These come straight from [LemonadeTypography] rather than from the Figma export: the export
 * carries only the underlying scales, and the composite styles live in Kotlin.
 */
internal enum class TypeGroup(val label: String) {
    Display(label = "Display"),
    Heading(label = "Heading"),
    Body(label = "Body"),
    Overline(label = "Overline"),
}
