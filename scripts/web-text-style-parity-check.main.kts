#!/usr/bin/env kotlin

// Re-parses swiftui/Sources/Lemonade/LemonadeTypography.swift and compares it against
// the committed text-styles.json, so a text style changed in Swift and not in web is
// caught instead of silently drifting.
//
// This script carries its own verbatim copy of the parser (TextStyle, DECL, FIELD,
// tokenName, SIZE_SUFFIXES, WEIGHT_SUFFIXES, cssNameFor, parseSwiftTextStyles) rather
// than `@file:Import`-ing web-text-style-extract.main.kts: that script runs its
// extraction as a side effect of being loaded (a top-level `main()` call), so
// importing it here would rewrite text-styles.json on every parity check and make the
// check incapable of ever failing. The duplication is deliberate and small — keep it
// byte-identical to the extractor's copy so the two are trivially diffable.

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.json:json:20240303")

import org.json.JSONArray
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
    val swift = parseSwiftTextStyles(File("swiftui/Sources/Lemonade/LemonadeTypography.swift"))
    val json = JSONArray(File("text-styles.json").readText())

    val fromJson = (0 until json.length()).map { index ->
        val obj = json.getJSONObject(index)
        TextStyle(
            name = obj.getString("name"),
            cssName = obj.getString("css"),
            fontSize = obj.getString("fontSize"),
            lineHeight = obj.getString("lineHeight"),
            fontWeight = obj.getString("fontWeight"),
            letterSpacing = if (obj.has("letterSpacing")) obj.getDouble("letterSpacing") else null,
        )
    }

    val onlyInSwift = swift.filter { it !in fromJson }
    val onlyInJson = fromJson.filter { it !in swift }

    if (onlyInSwift.isNotEmpty() || onlyInJson.isNotEmpty()) {
        println("✗ text-styles.json has drifted from LemonadeTypography.swift")
        onlyInSwift.forEach { println("   only in Swift: $it") }
        onlyInJson.forEach { println("   only in JSON:  $it") }
        println()
        println("   Re-run: kotlin scripts/web-text-style-extract.main.kts")
        error("text style parity failed")
    }
    println("✓ text-styles.json matches LemonadeTypography.swift (${swift.size} styles)")
}

main()
