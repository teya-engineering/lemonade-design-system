#!/usr/bin/env kotlin

// Reads the committed text-styles.json (extracted from SwiftUI by
// web-text-style-extract.main.kts) and emits the web-facing named text style classes:
// web/styles/typography.css and web/src/text-styles.generated.ts.

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONArray
import java.io.File

val TEXT_STYLE_SCRIPT = "scripts/web-text-style-converter.main.kts"

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
            appendLine("  font-size: var(${cssVar("font-size", style.getString("fontSize"), "font-size")});")
            appendLine("  line-height: var(${cssVar("line-height", style.getString("lineHeight"), "line-height")});")
            appendLine("  font-weight: var(${cssVar("font-weight", style.getString("fontWeight"))});")
            if (style.has("letterSpacing")) {
                appendLine("  letter-spacing: ${pxValue(style.getDouble("letterSpacing"))};")
            }
            appendLine("}")
        }
    }
    File("web/styles/typography.css").apply { parentFile.mkdirs() }.writeText(css)
    println("✓ web/styles/typography.css written with ${json.length()} classes")

    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        appendLine()
        appendLine("export const textStyles = {")
        (0 until json.length()).forEach { index ->
            val style = json.getJSONObject(index)
            appendLine("  ${style.getString("name")}: {")
            appendLine("    className: \"lmnd-text-${style.getString("css")}\",")
            appendLine("    fontSize: \"${style.getString("fontSize")}\",")
            appendLine("    lineHeight: \"${style.getString("lineHeight")}\",")
            appendLine("    fontWeight: \"${style.getString("fontWeight")}\",")
            if (style.has("letterSpacing")) {
                appendLine("    letterSpacing: ${style.getDouble("letterSpacing")},")
            }
            appendLine("  },")
        }
        appendLine("} as const")
        appendLine()
        appendLine("export type LemonadeTextStyles = typeof textStyles")
    }
    File("web/src/text-styles.generated.ts").apply { parentFile.mkdirs() }.writeText(ts)
    println("✓ web/src/text-styles.generated.ts written")
}

main()
