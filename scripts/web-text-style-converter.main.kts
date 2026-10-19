#!/usr/bin/env kotlin

// Reads the committed text-styles.json (extracted from SwiftUI by
// web-text-style-extract.main.kts) and emits the web-facing named text style classes:
// web/styles/typography.css and web/src/text-styles.generated.ts.

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONArray
import java.io.File

val TEXT_STYLE_SCRIPT = "scripts/web-text-style-converter.main.kts"

/** `font-size-600` -> 24.0, `semibold` -> "SemiBold", `base` -> "Figtree". */
fun typographyByLeaf(): Map<String, Any> {
    val values = mutableMapOf<String, Any>()
    readFileResourceFileRaw(tokenFile("typography.tokens.json")) { path, resolved ->
        values[leafOf(path)] = resolved.get("resolvedValue")
    }
    return values
}

fun main() {
    val json = JSONArray(File("text-styles.json").readText())
    val css = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        (0 until json.length()).forEach { index ->
            val style = json.getJSONObject(index)
            appendLine()
            appendLine(".lmnd-text-${style.getString("css")} {")
            appendLine("  font-family: var(--lmnd-font-family-base);")
            // Figtree is drawn for the weights the native platforms render with their own
            // smoothing. Without these, the browser's subpixel antialiasing thickens it,
            // and the same text looks heavier on web than in Figma or on device. Scoped to
            // the class rather than an element selector: the package owns no global rules.
            appendLine("  -webkit-font-smoothing: antialiased;")
            appendLine("  -moz-osx-font-smoothing: grayscale;")
            appendLine("  font-size: var(${cssVar("font-size", style.getString("fontSize"), "font-size")});")
            appendLine("  line-height: var(${cssVar("line-height", style.getString("lineHeight"), "line-height")});")
            appendLine("  font-weight: var(${cssVar("font-weight", style.getString("fontWeight"))});")
            if (style.has("letterSpacing")) {
                appendLine("  letter-spacing: ${pxValue(style.getDouble("letterSpacing"))};")
            }
            // KMP and SwiftUI uppercase the string itself for this one style. CSS renders the
            // same thing while leaving the text as authored, so a screen reader and a copy both
            // get the original — and markup that never loads the JS gets it too.
            if (style.getString("name") == "bodyXSmallOverline") {
                appendLine("  text-transform: uppercase;")
            }
            appendLine("}")
        }
    }
    File("web/styles/typography.css").apply { parentFile.mkdirs() }.writeText(css)
    println("✓ web/styles/typography.css written with ${json.length()} classes")

    val typography = typographyByLeaf()
    val entries = (0 until json.length()).flatMap { index ->
        val style = json.getJSONObject(index)
        val name = style.getString("name")
        val px = { leaf: String -> (typography.getValue(leaf) as Number).toDouble() }
        listOfNotNull(
            "className" to jsString("lmnd-text-${style.getString("css")}"),
            "fontFamily" to jsString("\"${typography.getValue("base")}\", $FONT_STACK"),
            "fontSize" to jsString(remValue(px(style.getString("fontSize")))),
            "lineHeight" to jsString(remValue(px(style.getString("lineHeight")))),
            "fontWeight" to FONT_WEIGHTS.getValue(typography.getValue(style.getString("fontWeight")) as String),
            if (style.has("letterSpacing")) "letterSpacing" to jsString(pxValue(style.getDouble("letterSpacing"))) else null,
            if (name == "bodyXSmallOverline") "textTransform" to jsString("uppercase") else null,
            "WebkitFontSmoothing" to jsString("antialiased"),
            "MozOsxFontSmoothing" to jsString("grayscale"),
        ).map { (key, literal) -> JsEntry(listOf(name, key), literal) }
    }
    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        appendLine()
        appendLine("/** Each style as values ready to spread into a style object, plus the class that applies it. */")
        appendLine("export const textStyles = ${jsObjectLiteral(entries)} as const")
        appendLine()
        appendLine("export type LemonadeTextStyles = typeof textStyles")
    }
    File("web/src/text-styles.generated.ts").apply { parentFile.mkdirs() }.writeText(ts)
    println("✓ web/src/text-styles.generated.ts written")
}

main()
