#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import java.io.File

val SCRIPT_PATH = "scripts/web-token-converter.main.kts"

data class CssVar(val name: String, val value: String)

/** One scalar token collection and how its values become CSS. */
private data class ScalarSource(
    val fileName: String,
    val category: String,
    val strip: String?,
    val format: (Double) -> String,
)

/** Opacity tokens are authored 0-100; CSS and the TS export both want 0-1. */
private fun opacityScalar(raw: Double): Double = raw / 100.0

/** Opacity tokens are authored 0-100; CSS wants 0-1. */
private fun opacityValue(raw: Double): String = trimNumber(opacityScalar(raw))

private val SCALAR_SOURCES = listOf(
    ScalarSource("spacing.tokens.json", "spacing", "spacing", ::remValue),
    ScalarSource("radius.tokens.json", "radius", "radius", ::remValue),
    ScalarSource("size.tokens.json", "size", "size", ::remValue),
    ScalarSource("border-width.tokens.json", "border-width", "border", ::pxValue),
    ScalarSource("opacity.tokens.json", "opacity", "opacity", ::opacityValue),
)

fun scalarVars(): List<CssVar> {
    val vars = mutableListOf<CssVar>()
    SCALAR_SOURCES.forEach { source ->
        readFileResourceFileRaw(tokenFile(source.fileName)) { path, resolved ->
            val raw = resolved.get("resolvedValue")
            require(raw is Number) {
                "${source.fileName}: token '$path' is ${raw::class.simpleName}, expected a number"
            }
            vars.add(
                CssVar(
                    name = cssVar(source.category, leafOf(path), source.strip),
                    value = source.format(raw.toDouble()),
                )
            )
        }
    }
    return vars
}

/**
 * TS object key for a scalar token: category + the leaf's distinguishing part,
 * camelCased. `("spacing", "spacing-200", "spacing")` -> `spacing200`;
 * `("border-width", "focus-ring", "border")` -> `borderWidthFocusRing`.
 *
 * The category is folded into the key (unlike the CSS custom property, which is
 * already namespaced by its selector) because Task 12's consumers read
 * `tokens.spacing.spacing200`, matching the cross-platform naming already used by
 * `LemonadeTheme.spaces.spacing0` in the Figma token metadata.
 */
fun tsKey(category: String, leaf: String, strip: String?): String {
    val trimmed = if (strip != null) leaf.removePrefix("$strip-") else leaf
    return "$category-$trimmed".split("-").mapIndexed { index, word ->
        if (index == 0) word else word.replaceFirstChar { it.uppercase() }
    }.joinToString("")
}

/** `border-width` -> `borderWidth`, `spacing` -> `spacing`. */
fun tsGroupName(category: String): String =
    category.split("-").mapIndexed { index, word ->
        if (index == 0) word else word.replaceFirstChar { it.uppercase() }
    }.joinToString("")

/**
 * The five scalar groups, keyed and valued for the TS/JSON exports.
 *
 * Unlike [scalarVars] the values here are raw numbers (px, or 0-1 for opacity),
 * not CSS-formatted rem/px strings: the TS export exists precisely so consumers
 * doing arithmetic get the source numbers, preserving parity with KMP and SwiftUI.
 */
fun scalarEntries(): List<Triple<String, String, List<Pair<String, String>>>> {
    return SCALAR_SOURCES.map { source ->
        val entries = mutableListOf<Pair<String, String>>()
        readFileResourceFileRaw(tokenFile(source.fileName)) { path, resolved ->
            val raw = (resolved.get("resolvedValue") as Number).toDouble()
            val value = if (source.category == "opacity") opacityScalar(raw) else raw
            entries.add(tsKey(source.category, leafOf(path), source.strip) to trimNumber(value))
        }
        Triple(tsGroupName(source.category), "number", entries)
    }
}

/** Writes web/src/tokens.generated.ts: a typed `tokens` object of the five scalar groups. */
fun writeTokensTs(groups: List<Triple<String, String, List<Pair<String, String>>>>) {
    // Triple is (tsGroupName, tsType, entries of key to literal)
    val output = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = SCRIPT_PATH))
        appendLine(" */")
        appendLine()
        appendLine("export const tokens = {")
        groups.forEach { (name, _, entries) ->
            appendLine("  $name: {")
            entries.forEach { (key, literal) -> appendLine("    $key: $literal,") }
            appendLine("  },")
        }
        appendLine("} as const")
        appendLine()
        appendLine("export type LemonadeTokens = typeof tokens")
    }
    val target = File("web/src/tokens.generated.ts")
    target.parentFile.mkdirs()
    target.writeText(output)
    println("✓ web/src/tokens.generated.ts written")
}

/**
 * Serialises the same scalar groups as web/tokens.json, for non-TS consumers.
 *
 * Hand-emitted rather than routed through `org.json.JSONObject`: that type is
 * HashMap-backed and does not preserve insertion order, which would scramble
 * this committed file's key order independently of [scalarEntries] on every
 * regeneration. [literal] is already the trimmed numeric text (`8`, `1.5`,
 * `999`), so writing it verbatim also avoids a Double round-trip turning
 * integers into `8.0`.
 */
fun buildJsonFromEntries(
    groups: List<Triple<String, String, List<Pair<String, String>>>>,
): String {
    return buildString {
        appendLine("{")
        groups.forEachIndexed { groupIndex, (name, _, entries) ->
            val groupComma = if (groupIndex == groups.lastIndex) "" else ","
            appendLine("  \"$name\": {")
            entries.forEachIndexed { entryIndex, (key, literal) ->
                val entryComma = if (entryIndex == entries.lastIndex) "" else ","
                appendLine("    \"$key\": $literal$entryComma")
            }
            appendLine("  }$groupComma")
        }
        appendLine("}")
    }
}

private val FONT_WEIGHTS = mapOf(
    "Regular" to "400",
    "Medium" to "500",
    "SemiBold" to "600",
    "Bold" to "700",
)

/**
 * A bare `Figtree` leaves every machine without the font on its browser default.
 * The stack mirrors what the native platforms fall back to.
 */
private val FONT_STACK =
    "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif"

fun typographyVars(): List<CssVar> {
    val vars = mutableListOf<CssVar>()
    readFileResourceFileRaw(tokenFile("typography.tokens.json")) { path, resolved ->
        val group = path.substringBefore('/')
        val leaf = leafOf(path)
        val raw = resolved.get("resolvedValue")
        val declaration = when (group) {
            "font-size" -> CssVar(
                cssVar("font-size", leaf, "font-size"),
                remValue((raw as Number).toDouble()),
            )
            "line-height" -> CssVar(
                cssVar("line-height", leaf, "line-height"),
                remValue((raw as Number).toDouble()),
            )
            "font-weight" -> CssVar(
                cssVar("font-weight", leaf),
                FONT_WEIGHTS[raw as String]
                    ?: error("unmapped font weight '$raw' on '$path' — add it to FONT_WEIGHTS"),
            )
            "font-family" -> CssVar(
                cssVar("font-family", leaf),
                "\"$raw\", $FONT_STACK",
            )
            else -> error("unexpected typography group '$group' on token '$path'")
        }
        vars.add(declaration)
    }
    return vars
}

private val SHADOW_LEAF = Regex("""^sd-([a-z]+)-lv(\d+)-(.+)$""")
private val SHADOW_ORDER = listOf("xs", "sm", "md", "lg", "xl")
private val SHADOW_COLOUR_TOKEN = "Shadow/shadow-default"

/**
 * Composes the 45 scalar shadow parts into one usable value per size.
 *
 * The raw tokens are unusable directly — `sd-md-lv2-blur` is not something anyone
 * writes in a stylesheet. Each size's lv1 and lv2 layers become a single comma-joined
 * box-shadow, ordered lv1 first so the tighter layer paints nearest.
 *
 * The colour is emitted as var(--lmnd-color-shadow-default) rather than its resolved
 * value, because that token differs between themes. Referencing the variable makes
 * shadows theme-reactive for free.
 */
fun shadowVars(): List<CssVar> {
    // size -> level -> part -> value
    val parts = sortedMapOf<String, MutableMap<Int, MutableMap<String, Double>>>()

    readFileResourceFileRaw(tokenFile("shadow.tokens.json")) { path, resolved ->
        val match = SHADOW_LEAF.matchEntire(leafOf(path))
            ?: error("shadow token '$path' does not match sd-<size>-lv<n>-<part>")
        val (size, level, part) = match.destructured

        if (part == "color") {
            val alias = resolved.optString("aliasName")
            require(alias == SHADOW_COLOUR_TOKEN) {
                "shadow colour '$path' aliases '$alias', expected '$SHADOW_COLOUR_TOKEN'. " +
                    "Shadows reference the theme colour variable; a different source needs new handling."
            }
            return@readFileResourceFileRaw
        }

        val value = (resolved.get("resolvedValue") as Number).toDouble()
        parts.getOrPut(size) { mutableMapOf() }
            .getOrPut(level.toInt()) { mutableMapOf() }[part] = value
    }

    val unknown = parts.keys - SHADOW_ORDER.toSet()
    require(unknown.isEmpty()) { "unknown shadow sizes $unknown — add them to SHADOW_ORDER" }

    return SHADOW_ORDER.filter { it in parts }.map { size ->
        val layers = parts.getValue(size).toSortedMap().map { (_, part) ->
            listOf(
                pxValue(part.getValue("offset-x")),
                pxValue(part.getValue("offset-y")),
                pxValue(part.getValue("blur")),
                pxValue(part.getValue("spread")),
                "var(${cssVar("color", "shadow-default")})",
            ).joinToString(" ")
        }
        CssVar("--lmnd-shadow-$size", layers.joinToString(", "))
    }
}

fun cssBanner(): String = buildString {
    appendLine("/**")
    append(defaultAutoGenerationMessage(scriptFilePath = SCRIPT_PATH))
    appendLine(" */")
}

/**
 * Writes web/styles/tokens.css.
 *
 * [sections] is an ordered list of (selector, declarations). Everything theme-neutral
 * goes under `:root`; Task 4 adds the theme selectors.
 */
fun writeTokensCss(sections: List<Pair<String, List<CssVar>>>) {
    // Guards the border-selected class of bug: two tokens mapping to one property
    // would silently drop one, and nothing downstream would notice. The same
    // property legitimately appears across selectors (e.g. light vs dark), so the
    // guard only rejects duplicates *within* one selector.
    sections.forEach { (selector, vars) ->
        val duplicates = vars.groupBy { it.name }.filterValues { it.size > 1 }
        require(duplicates.isEmpty()) {
            "duplicate CSS custom properties in '$selector': ${duplicates.keys.sorted()}"
        }
    }

    val output = buildString {
        append(cssBanner())
        sections.forEach { (selector, vars) ->
            val isAtRule = selector.startsWith("@")
            appendLine()
            appendLine("$selector {")
            vars.forEach { appendLine("  ${it.name}: ${it.value};") }
            appendLine("}")
            if (isAtRule) appendLine("}")
        }
    }

    val target = File("web/styles/tokens.css")
    target.parentFile.mkdirs()
    target.writeText(output)
    println("✓ web/styles/tokens.css written (${sections.sumOf { it.second.size }} properties)")
}

/** Every semantic colour for one Figma mode, as CSS custom properties. */
fun themeVars(mode: String): List<CssVar> {
    val files = tokenFiles("theme-colors.")
    // Regenerating from a subset would leave the other theme silently stale, and the
    // drift job would stay green because the tree stays clean.
    requireModes(files, "Light", "Dark")

    val vars = mutableListOf<CssVar>()
    readFileResourceFileByModeRaw(files, mode) { path, resolved ->
        vars.add(
            CssVar(
                name = cssVar("color", leafOf(path)),
                value = rgbValue(
                    r = resolved.getDouble("r"),
                    g = resolved.getDouble("g"),
                    b = resolved.getDouble("b"),
                    a = resolved.getDouble("a"),
                ),
            )
        )
    }
    return vars
}

/**
 * The four theme selectors.
 *
 * Light lives on bare `:root` so that importing tokens.css and setting nothing at all
 * is already correct. Dark then applies two ways: automatically from the OS, and
 * explicitly via the attribute, which always wins. The attribute is matched on any
 * element rather than only `<html>`, so a dark card inside a light page works.
 *
 * `color-scheme` makes native scrollbars, form controls and browser UI follow the
 * theme; without it the page is themed but the scrollbar is glaringly not.
 */
fun themeSections(): List<Pair<String, List<CssVar>>> {
    val light = themeVars("Light")
    val dark = themeVars("Dark")
    require(light.map { it.name } == dark.map { it.name }) {
        "light and dark themes declare different colour tokens — one export is stale"
    }
    val scheme = { value: String -> CssVar("color-scheme", value) }
    return listOf(
        ":root" to light + scheme("light"),
        "@media (prefers-color-scheme: dark) { :root:not([data-lmnd-theme=\"light\"])" to
            dark + scheme("dark"),
        "[data-lmnd-theme=\"dark\"]" to dark + scheme("dark"),
        "[data-lmnd-theme=\"light\"]" to light + scheme("light"),
    )
}

fun main() {
    try {
        val scalars = scalarVars() + typographyVars() + shadowVars()
        println("✓ Loaded ${scalars.size} theme-neutral tokens")
        val themes = themeSections()
        println("✓ Loaded ${themes.first().second.size - 1} colours per theme")
        val sections = listOf(":root" to scalars) + themes
        writeTokensCss(sections)

        val entries = scalarEntries()
        writeTokensTs(entries)
        File("web/tokens.json").writeText(buildJsonFromEntries(entries))
        println("✓ web/tokens.json written")
    } catch (error: Throwable) {
        println("✗ Failed to generate web tokens: ${error.message}")
        throw error
    }
}

main()
