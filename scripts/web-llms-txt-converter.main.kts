#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONObject
import java.io.File

/** The `$description` on a token, which the other platforms ignore. */
fun descriptionOf(node: JSONObject): String = node.optString("\$description").trim()

fun main() {
    val out = StringBuilder()
    out.appendLine("# Lemonade Design System — web tokens")
    out.appendLine()
    out.appendLine("CSS custom properties for Teya's Lemonade design system.")
    out.appendLine("Import `@teya/lemonade-ds/tokens.css`, or paste `lemonade.css` into a <style> block.")
    out.appendLine("Light theme is the default; dark applies automatically from the OS, or explicitly")
    out.appendLine("with `data-lmnd-theme=\"dark\"` on any element.")
    out.appendLine()
    out.appendLine("Always prefer a semantic token over a literal value.")
    out.appendLine()
    out.appendLine("## Colours")
    out.appendLine()

    // Descriptions live on the raw token nodes, so re-read the document directly
    // rather than going through the resolved-value path.
    val lightFile = tokenFiles("theme-colors.").first { it.name.contains("light") }
    val root = JSONObject(lightFile.readText())
    val nodes = dtcgTokens(root)
    nodes.keys.sortedWith(::canonicalTokenOrder).forEach { path ->
        val node = nodes.getValue(path)
        // Four colour tokens (content-accent, bg-accent, bg-accent-subtle,
        // border-accent) are hidden from publishing. tokens.css does not emit
        // CSS variables for them, so listing them here would advertise
        // `var(--lmnd-color-*)` references that resolve to nothing.
        val hidden = node.optJSONObject("\$extensions")
            ?.optBoolean("com.figma.hiddenFromPublishing") ?: false
        if (hidden) return@forEach
        val leaf = leafOf(path)
        val description = descriptionOf(node)
        out.append("- `var(${cssVar("color", leaf)})`")
        if (description.isNotEmpty()) out.append(" — $description")
        out.appendLine()
    }

    listOf(
        Triple("Spacing", "spacing.tokens.json", Triple("spacing", "spacing", true)),
        Triple("Radius", "radius.tokens.json", Triple("radius", "radius", true)),
        Triple("Size", "size.tokens.json", Triple("size", "size", true)),
        Triple("Border width", "border-width.tokens.json", Triple("border-width", "border", false)),
    ).forEach { (heading, fileName, naming) ->
        val (category, strip, isRem) = naming
        out.appendLine()
        out.appendLine("## $heading")
        out.appendLine()
        readFileResourceFileRaw(tokenFile(fileName)) { path, resolved ->
            val raw = (resolved.get("resolvedValue") as Number).toDouble()
            val rendered = if (isRem) remValue(raw) else pxValue(raw)
            out.appendLine("- `var(${cssVar(category, leafOf(path), strip)})` = $rendered")
        }
    }

    out.appendLine()
    out.appendLine("## Text styles")
    out.appendLine()
    out.appendLine("Apply as a class, e.g. `<p class=\"lmnd-text-body-medium-regular\">`.")
    out.appendLine()
    val styles = org.json.JSONArray(File("text-styles.json").readText())
    (0 until styles.length()).forEach { index ->
        out.appendLine("- `.lmnd-text-${styles.getJSONObject(index).getString("css")}`")
    }

    out.appendLine()
    out.appendLine("## Shadows")
    out.appendLine()
    listOf("xs", "sm", "md", "lg", "xl").forEach { out.appendLine("- `var(--lmnd-shadow-$it)`") }

    File("web/llms.txt").apply { parentFile.mkdirs() }.writeText(out.toString())
    println("✓ web/llms.txt written (${out.length} chars)")
}

main()
