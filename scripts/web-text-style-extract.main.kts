#!/usr/bin/env kotlin

// One-time bootstrap: extracts the named text style table from
// swiftui/Sources/Lemonade/LemonadeTypography.swift into text-styles.json.
//
// This is the source of truth for web (and later every platform); do not hand-edit
// text-styles.json — re-run this script instead. Drift between the Swift source and
// the committed JSON is caught by scripts/web-text-style-parity-check.main.kts, which
// keeps its own verbatim copy of the parser below (see that script for why).

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.json:json:20240303")

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class TextStyle(
    val name: String,
    val cssName: String,
    val fontSize: String,
    val lineHeight: String,
    val fontWeight: String,
    val letterSpacing: Double?,
)

private val DECL = Regex(
    """public let (\w+) = LemonadeTextStyle\(([^)]*)\)""",
    RegexOption.DOT_MATCHES_ALL,
)
private val FIELD = Regex("""(\w+):\s*([^,\n]+)""")

/** `LemonadeFontSizes.fontSize600.value` -> `font-size-600`. */
private fun tokenName(prefix: String, expression: String): String {
    val symbol = expression.substringAfter('.').substringBefore('.').trim()
    val digits = symbol.dropWhile { !it.isDigit() }
    return "$prefix-$digits"
}

/**
 * `displayXSmall` -> `display-xsmall`.
 *
 * A generic camelCase splitter produces `display-x-small` and `display-2-x-large`,
 * which read badly as class names, so size suffixes are mapped explicitly.
 */
private val SIZE_SUFFIXES = listOf(
    "XXSmall" to "xxsmall",
    "XSmall" to "xsmall",
    "3XLarge" to "3xlarge",
    "2XLarge" to "2xlarge",
    "XLarge" to "xlarge",
    "Small" to "small",
    "Medium" to "medium",
    "Large" to "large",
)
private val WEIGHT_SUFFIXES = listOf(
    "Regular" to "regular",
    "SemiBold" to "semibold",
    "Medium" to "medium",
    "Bold" to "bold",
    "Overline" to "overline",
)

fun cssNameFor(swiftName: String): String {
    val family = listOf("display", "heading", "body").firstOrNull { swiftName.startsWith(it) }
        ?: error("unrecognised text style family in '$swiftName'")
    var rest = swiftName.removePrefix(family)

    var weight: String? = null
    for ((suffix, slug) in WEIGHT_SUFFIXES) {
        if (rest.endsWith(suffix) && rest != suffix) { weight = slug; rest = rest.removeSuffix(suffix); break }
    }
    val size = SIZE_SUFFIXES.firstOrNull { rest == it.first }?.second
        ?: error("unrecognised size '$rest' in '$swiftName'")

    return listOfNotNull(family, size, weight).joinToString("-")
}

fun parseSwiftTextStyles(file: File): List<TextStyle> {
    val text = file.readText()
    return DECL.findAll(text).map { match ->
        val (name, body) = match.destructured
        val fields = FIELD.findAll(body).associate { it.groupValues[1] to it.groupValues[2].trim() }
        TextStyle(
            name = name,
            cssName = cssNameFor(name),
            fontSize = tokenName("font-size", fields.getValue("fontSize")),
            lineHeight = tokenName("line-height", fields.getValue("lineHeight")),
            fontWeight = fields.getValue("fontWeight").substringAfter('.').substringBefore('.').trim(),
            letterSpacing = fields["letterSpacing"]?.toDoubleOrNull(),
        )
    }.toList()
}

fun main() {
    val swift = File("swiftui/Sources/Lemonade/LemonadeTypography.swift")
    val styles = parseSwiftTextStyles(swift)
    require(styles.size == 30) { "expected 30 text styles, parsed ${styles.size}" }

    val array = JSONArray()
    styles.forEach { style ->
        val obj = JSONObject()
            .put("name", style.name)
            .put("css", style.cssName)
            .put("fontSize", style.fontSize)
            .put("lineHeight", style.lineHeight)
            .put("fontWeight", style.fontWeight)
        if (style.letterSpacing != null) obj.put("letterSpacing", style.letterSpacing)
        array.put(obj)
    }
    File("text-styles.json").writeText(array.toString(2) + "\n")
    println("✓ text-styles.json written with ${styles.size} styles")
}

main()
