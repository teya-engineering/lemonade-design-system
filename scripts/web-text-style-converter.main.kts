#!/usr/bin/env kotlin

// Reads the committed text-styles.json (extracted from SwiftUI by
// web-text-style-extract.main.kts) and emits the web-facing named text style classes:
// web/styles/typography.css and web/src/text-styles.generated.ts.

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

val TEXT_STYLE_SCRIPT = "scripts/web-text-style-converter.main.kts"

/** `font-size-600` -> 24.0, `semibold` -> "SemiBold", `base` -> "Figtree". */
fun typographyByLeaf(): Map<String, Any> {
    val values = mutableMapOf<String, Any>()
    readFileResourceFileRaw(tokenFile("typography.tokens.json")) { path, resolved ->
        val previous = values.put(leafOf(path), resolved.get("resolvedValue"))
        require(previous == null) { "typography leaf '${leafOf(path)}' appears twice — text styles look tokens up by leaf" }
    }
    return values
}

/** One property of a text style, as the CSS class declares it and as `textStyles` spreads it. */
data class Declaration(val cssProperty: String, val cssValue: String, val jsKey: String, val jsLiteral: String)

fun declarations(style: JSONObject, typography: Map<String, Any>): List<Declaration> {
    val px = { leaf: String -> (typography.getValue(leaf) as Number).toDouble() }
    val fontSize = style.getString("fontSize")
    val lineHeight = style.getString("lineHeight")
    val fontWeight = style.getString("fontWeight")
    val weight = typography.getValue(fontWeight) as String
    return listOfNotNull(
        Declaration(
            "font-family", "var(--lmnd-font-family-base)",
            "fontFamily", jsString("\"${typography.getValue("base")}\", $FONT_STACK"),
        ),
        // Figtree is drawn for the weights the native platforms render with their own
        // smoothing. Without these, the browser's subpixel antialiasing thickens it,
        // and the same text looks heavier on web than in Figma or on device. Scoped to
        // the class rather than an element selector: the package owns no global rules.
        Declaration("-webkit-font-smoothing", "antialiased", "WebkitFontSmoothing", jsString("antialiased")),
        Declaration("-moz-osx-font-smoothing", "grayscale", "MozOsxFontSmoothing", jsString("grayscale")),
        Declaration(
            "font-size", "var(${cssVar("font-size", fontSize, "font-size")})",
            "fontSize", jsString(remValue(px(fontSize))),
        ),
        Declaration(
            "line-height", "var(${cssVar("line-height", lineHeight, "line-height")})",
            "lineHeight", jsString(remValue(px(lineHeight))),
        ),
        Declaration(
            "font-weight", "var(${cssVar("font-weight", fontWeight)})",
            "fontWeight", FONT_WEIGHTS[weight] ?: error("unmapped font weight '$weight' — add it to FONT_WEIGHTS"),
        ),
        if (style.has("letterSpacing")) {
            pxValue(style.getDouble("letterSpacing")).let { Declaration("letter-spacing", it, "letterSpacing", jsString(it)) }
        } else {
            null
        },
        // KMP and SwiftUI uppercase the string itself for this one style. CSS renders the
        // same thing while leaving the text as authored, so a screen reader and a copy both
        // get the original — and markup that never loads the JS gets it too.
        if (style.getString("name") == "bodyXSmallOverline") {
            Declaration("text-transform", "uppercase", "textTransform", jsString("uppercase"))
        } else {
            null
        },
    )
}

fun main() {
    val json = JSONArray(File("text-styles.json").readText())
    val typography = typographyByLeaf()
    val styles = (0 until json.length()).map { json.getJSONObject(it) }

    val css = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        styles.forEach { style ->
            appendLine()
            appendLine(".lmnd-text-${style.getString("css")} {")
            declarations(style, typography).forEach { appendLine("  ${it.cssProperty}: ${it.cssValue};") }
            appendLine("}")
        }
    }
    File("web/styles/typography.css").apply { parentFile.mkdirs() }.writeText(css)
    println("✓ web/styles/typography.css written with ${styles.size} classes")

    val entries = styles.flatMap { style ->
        val name = style.getString("name")
        listOf(JsEntry(listOf(name, "className"), jsString("lmnd-text-${style.getString("css")}"))) +
            declarations(style, typography).map { JsEntry(listOf(name, "style", it.jsKey), it.jsLiteral) }
    }
    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        appendLine()
        appendLine("/** Each style's `className`, and its values as a `style` object ready to spread. */")
        appendLine("export const textStyles = ${jsObjectLiteral(entries)} as const")
        appendLine()
        appendLine("export type LemonadeTextStyles = typeof textStyles")
    }
    File("web/src/text-styles.generated.ts").apply { parentFile.mkdirs() }.writeText(ts)
    println("✓ web/src/text-styles.generated.ts written")
}

main()
