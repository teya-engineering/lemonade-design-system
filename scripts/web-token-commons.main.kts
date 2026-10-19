#!/usr/bin/env kotlin

// Pure formatting helpers for the web token converters.
//
// Deliberately free of file I/O so that web-token-commons-test.main.kts can
// exercise every branch without fixtures.

import kotlin.math.abs
import kotlin.math.roundToInt
import java.util.Locale

/** The last segment of a DTCG slash path. `Content/Brand/content-brand` -> `content-brand`. */
fun leafOf(path: String): String = path.substringAfterLast('/')

/** Every hue in the Themed collection reuses the same leaves, so those names keep the full path. */
fun colourName(path: String): String =
    if (path.startsWith("Themed/")) path.replace('/', '-').lowercase() else leafOf(path)

/**
 * The CSS custom property for a token.
 *
 * Leaf names in the Figma export are already self-describing (`content-primary`,
 * `spacing-100`), so the scheme is `--lmnd-<category>-<leaf>` with the repeated
 * category word removed. [strip] is passed explicitly rather than derived from
 * [category] because they differ: the border-width collection prefixes its tokens
 * with `border-`, not `border-width-`.
 *
 * The namespacing is what keeps `border-selected` from collapsing: it exists both as
 * a border width and as a semantic colour.
 */
fun cssVar(category: String, leaf: String, strip: String? = null): String {
    val suffix = if (strip != null) leaf.removePrefix("$strip-") else leaf
    return "--lmnd-$category-$suffix"
}

/** Trims float noise and trailing zeros: 0.925000011920929 -> "0.925", 1.0 -> "1". */
fun trimNumber(value: Double): String {
    if (abs(value) < 1e-9) return "0"
    val rounded = String.format(Locale.ROOT, "%.4f", value)
    return rounded.trimEnd('0').trimEnd('.')
}

/**
 * Font sizes, line heights, spacing, sizes and radii, divided by the 16px root.
 *
 * rem rather than px because only rem follows a raised browser default font size —
 * a common low-vision accommodation. See spec §5.
 */
fun remValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px / 16.0) + "rem"
}

/** Border widths and shadow geometry, which are optical constants rather than proportional. */
fun pxValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px) + "px"
}

/**
 * A colour as space-separated `rgb()`.
 *
 * Never hex: the DTCG `hex` field discards alpha, and `content-primary` is 92.5%
 * opaque black. Emitting its hex would silently produce the wrong colour.
 */
fun rgbValue(r: Double, g: Double, b: Double, a: Double): String {
    val red = channel(r)
    val green = channel(g)
    val blue = channel(b)
    val opaque = abs(a - 1.0) < 1e-6
    return if (opaque) {
        "rgb($red $green $blue)"
    } else {
        "rgb($red $green $blue / ${trimNumber(a)})"
    }
}

/** The same colour in the comma `rgba()` form, alpha always written: older JS colour parsers read only this. */
fun rgbaValue(r: Double, g: Double, b: Double, a: Double): String =
    "rgba(${channel(r)}, ${channel(g)}, ${channel(b)}, ${trimNumber(a)})"

private fun channel(component: Double): Int = (component * 255).roundToInt()

/** JS key for a token leaf: numeric steps stay numeric (`400`), names are camelCased (`focusRing`). */
fun jsKey(leaf: String, strip: String?): String {
    val trimmed = if (strip != null) leaf.removePrefix("$strip-") else leaf
    if (trimmed.all { it.isDigit() }) return trimmed
    return camelCase(trimmed)
}

/** `green-lime` -> `greenLime`. */
fun camelCase(kebab: String): String =
    kebab.split("-").mapIndexed { index, word ->
        if (index == 0) word else word.replaceFirstChar { it.uppercase() }
    }.joinToString("")

fun jsString(value: String): String =
    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

/** One leaf of a generated JS object: its key path and its value as source text. */
data class JsEntry(val path: List<String>, val literal: String)

private class JsNode {
    val children = LinkedHashMap<String, Any>()
}

private fun jsTree(entries: List<JsEntry>): JsNode {
    val root = JsNode()
    entries.forEach { entry ->
        var node = root
        entry.path.dropLast(1).forEach { key ->
            val child = node.children.getOrPut(key) { JsNode() }
            require(child is JsNode) { "'${entry.path.joinToString(".")}' nests under a value" }
            node = child
        }
        val leaf = entry.path.last()
        require(leaf !in node.children) { "duplicate JS path '${entry.path.joinToString(".")}'" }
        node.children[leaf] = entry.literal
    }
    return root
}

private fun renderTree(node: JsNode, depth: Int, json: Boolean): String = buildString {
    appendLine("{")
    val pad = "  ".repeat(depth + 1)
    node.children.entries.forEachIndexed { index, (key, value) ->
        val name = if (json) "\"$key\"" else key
        val rendered = if (value is JsNode) renderTree(value, depth + 1, json) else value as String
        val comma = if (!json || index < node.children.size - 1) "," else ""
        appendLine("$pad$name: $rendered$comma")
    }
    append("  ".repeat(depth) + "}")
}

/** A nested TS object literal in insertion order; keys are emitted bare. */
fun jsObjectLiteral(entries: List<JsEntry>): String = renderTree(jsTree(entries), 0, json = false)

/** The same tree as JSON, hand-emitted so key order survives (`JSONObject` is HashMap-backed). */
fun jsonObjectLiteral(entries: List<JsEntry>): String = renderTree(jsTree(entries), 0, json = true)
