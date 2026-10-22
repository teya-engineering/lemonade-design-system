#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import java.io.File

val SCRIPT_PATH = "scripts/web-token-converter.main.kts"

data class CssVar(val name: String, val value: String)

/**
 * One theme-neutral token: its CSS custom property, where it sits in the JS exports, its
 * `tokens` literal, and its `tokenValues` literal (null when it has no plain value).
 */
data class WebToken(val css: CssVar, val path: List<String>, val style: String, val value: String?)

/** One scalar token collection and how its values become CSS. */
private data class ScalarSource(
    val fileName: String,
    val category: String,
    val strip: String?,
    val format: (Double) -> String,
    val plain: (Double) -> Double = { it },
    val unitless: Boolean = false,
)

/** Opacity tokens are authored 0-100; CSS and JS both want 0-1. */
private fun opacityScalar(raw: Double): Double = raw / 100.0

private fun opacityValue(raw: Double): String = trimNumber(opacityScalar(raw))

private val SCALAR_SOURCES = listOf(
    ScalarSource("spacing.tokens.json", "spacing", "spacing", ::remValue),
    ScalarSource("radius.tokens.json", "radius", "radius", ::remValue),
    ScalarSource("size.tokens.json", "size", "size", ::remValue),
    ScalarSource("border-width.tokens.json", "border-width", "border", ::pxValue),
    ScalarSource("opacity.tokens.json", "opacity", "opacity", ::opacityValue, ::opacityScalar, unitless = true),
)

fun scalarTokens(): List<WebToken> {
    val tokens = mutableListOf<WebToken>()
    SCALAR_SOURCES.forEach { source ->
        readFileResourceFileRaw(tokenFile(source.fileName)) { path, resolved ->
            val raw = resolved.get("resolvedValue")
            require(raw is Number) {
                "${source.fileName}: token '$path' is ${raw::class.simpleName}, expected a number"
            }
            val css = source.format(raw.toDouble())
            tokens.add(
                WebToken(
                    css = CssVar(cssVar(source.category, leafOf(path), source.strip), css),
                    path = listOf(camelCase(source.category), jsKey(leafOf(path), source.strip)),
                    style = if (source.unitless) css else jsString(css),
                    value = trimNumber(source.plain(raw.toDouble())),
                )
            )
        }
    }
    return tokens
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

fun typographyTokens(): List<WebToken> {
    val tokens = mutableListOf<WebToken>()
    readFileResourceFileRaw(tokenFile("typography.tokens.json")) { path, resolved ->
        val group = path.substringBefore('/')
        val leaf = leafOf(path)
        val raw = resolved.get("resolvedValue")
        val token = when (group) {
            "font-size", "line-height" -> {
                val px = (raw as Number).toDouble()
                val css = remValue(px)
                val jsPath = if (group == "font-size") listOf("font", "size") else listOf("lineHeight")
                WebToken(CssVar(cssVar(group, leaf, group), css), jsPath + jsKey(leaf, group), jsString(css), trimNumber(px))
            }
            "font-weight" -> {
                val weight = FONT_WEIGHTS[raw as String]
                    ?: error("unmapped font weight '$raw' on '$path' — add it to FONT_WEIGHTS")
                WebToken(CssVar(cssVar(group, leaf), weight), listOf("font", "weight", jsKey(leaf, null)), weight, weight)
            }
            "font-family" -> {
                val stack = "\"$raw\", $FONT_STACK"
                WebToken(CssVar(cssVar(group, leaf), stack), listOf("font", "family", jsKey(leaf, null)), jsString(stack), jsString(stack))
            }
            else -> error("unexpected typography group '$group' on token '$path'")
        }
        tokens.add(token)
    }
    return tokens
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
 * shadows theme-reactive for free. A composed shadow has no plain value for `tokenValues`.
 */
fun shadowTokens(): List<WebToken> {
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
        val value = layers.joinToString(", ")
        WebToken(CssVar("--lmnd-shadow-$size", value), listOf("shadow", size), jsString(value), null)
    }
}

private val COLOUR_GROUPS = setOf("bg", "content", "border", "shadow")

/**
 * `Themed/green-lime/subtle/on-background` -> `color.themed.greenLime.subtle.onBackground`, the KMP
 * shape. The hue comes from the path, not the CSS name, because a hue can itself contain a hyphen.
 */
fun colourJsPath(path: String): List<String> {
    if (path.startsWith("Themed/")) {
        return listOf("color", "themed") + path.split('/').drop(1).map(::camelCase)
    }
    val leaf = leafOf(path)
    val group = leaf.substringBefore('-')
    require(group in COLOUR_GROUPS) { "colour '$path' has no JS group for '$group' — add it to COLOUR_GROUPS" }
    return listOf("color", group, jsKey(leaf, group))
}

fun colourValues(theme: String, colours: List<Colour>): List<JsEntry> = colours.map {
    val path = colourJsPath(it.path)
    JsEntry(listOf(path[0], theme) + path.drop(1), jsString(rgbaValue(it.r, it.g, it.b, it.a)))
}

/** Writes web/src/tokens.generated.ts: `tokens` for styling and `tokenValues` for code. */
fun writeTokensTs(styles: List<JsEntry>, values: List<JsEntry>) {
    val output = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = SCRIPT_PATH))
        appendLine(" */")
        appendLine()
        appendLine("/** Style-ready values. Colours are `var()` references and need `tokens.css` loaded. */")
        appendLine("export const tokens = ${jsObjectLiteral(styles)} as const")
        appendLine()
        appendLine("export type LemonadeTokens = typeof tokens")
        appendLine()
        appendLine("/** Plain values for code (px numbers, per-theme colours). Style components with `tokens` instead. */")
        appendLine("export const tokenValues = ${jsObjectLiteral(values)} as const")
        appendLine()
        appendLine("export type LemonadeTokenValues = typeof tokenValues")
    }
    val target = File("web/src/tokens.generated.ts")
    target.parentFile.mkdirs()
    target.writeText(output)
    println("✓ web/src/tokens.generated.ts written")
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

data class Colour(val path: String, val r: Double, val g: Double, val b: Double, val a: Double) {
    val cssName: String get() = cssVar("color", colourName(path))
}

/** Every semantic colour for one Figma mode. */
fun colours(mode: String): List<Colour> {
    val files = tokenFiles("theme-colors.")
    // Regenerating from a subset would leave the other theme silently stale, and the
    // drift job would stay green because the tree stays clean.
    requireModes(files, "Light", "Dark")

    val colours = mutableListOf<Colour>()
    readFileResourceFileByModeRaw(files, mode) { path, resolved ->
        colours.add(
            Colour(path, resolved.getDouble("r"), resolved.getDouble("g"), resolved.getDouble("b"), resolved.getDouble("a"))
        )
    }
    return colours
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
fun themeSections(lightColours: List<Colour>, darkColours: List<Colour>): List<Pair<String, List<CssVar>>> {
    val light = lightColours.map { CssVar(it.cssName, rgbValue(it.r, it.g, it.b, it.a)) }
    val dark = darkColours.map { CssVar(it.cssName, rgbValue(it.r, it.g, it.b, it.a)) }
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
        val tokens = scalarTokens() + typographyTokens() + shadowTokens()
        println("✓ Loaded ${tokens.size} theme-neutral tokens")
        val light = colours("Light")
        val dark = colours("Dark")
        require(light.map { it.path } == dark.map { it.path }) {
            "light and dark themes declare different colour tokens — one export is stale"
        }
        println("✓ Loaded ${light.size} colours per theme")
        writeTokensCss(listOf(":root" to tokens.map { it.css }) + themeSections(light, dark))

        val styles = tokens.map { JsEntry(it.path, it.style) } +
            light.map { JsEntry(colourJsPath(it.path), jsString("var(${it.cssName})")) }
        val values = tokens.mapNotNull { token -> token.value?.let { JsEntry(token.path, it) } } +
            colourValues("light", light) + colourValues("dark", dark)
        writeTokensTs(styles, values)
        File("web/tokens.json").writeText(jsonObjectLiteral(values) + "\n")
        println("✓ web/tokens.json written")
    } catch (error: Throwable) {
        println("✗ Failed to generate web tokens: ${error.message}")
        throw error
    }
}

main()
