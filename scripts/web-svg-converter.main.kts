#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")

import java.io.File

val SVG_SCRIPT = "scripts/web-svg-converter.main.kts"

private val INLINE_FILL_STYLE = Regex("""\s*style="[^"]*fill[^"]*"""")
private val FILL_ATTRIBUTE = Regex("""fill="(?!none)[^"]*"""")

/**
 * Makes an icon inherit the current text colour.
 *
 * Both the attribute and the inline style must go: an inline style beats every
 * stylesheet rule, so rewriting `fill="black"` alone leaves the icon rendering black.
 * `fill="none"` is preserved — it marks genuinely unfilled shapes.
 */
fun monochrome(svg: String): String =
    svg.replace(INLINE_FILL_STYLE, "").replace(FILL_ATTRIBUTE, """fill="currentColor"""")

private data class Family(val source: String, val target: String, val recolour: Boolean)

private val FAMILIES = listOf(
    Family("svg/icons", "web/assets/icons", recolour = true),
    Family("svg/flags", "web/assets/flags", recolour = false),
    Family("svg/brandLogos", "web/assets/brand-logos", recolour = false),
)

fun main() {
    val manifests = mutableMapOf<String, List<String>>()

    FAMILIES.forEach { family ->
        val sourceDir = File(family.source)
        require(sourceDir.isDirectory) { "${family.source} is missing" }
        val targetDir = File(family.target)
        targetDir.mkdirs()
        targetDir.listFiles()?.filter { it.extension == "svg" }?.forEach { it.delete() }

        val names = sourceDir.listFiles()
            .filter { it.isFile && it.extension == "svg" }
            .sortedBy { it.name }
            .map { file ->
                val content = file.readText()
                File(targetDir, file.name)
                    .writeText(if (family.recolour) monochrome(content) else content)
                file.nameWithoutExtension
            }
        manifests[family.target.substringAfterLast('/')] = names
        println("✓ ${names.size} files -> ${family.target}")
    }

    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = SVG_SCRIPT))
        appendLine(" */")
        listOf(
            "iconNames" to "icons",
            "flagNames" to "flags",
            "brandLogoNames" to "brand-logos",
        ).forEach { (constant, key) ->
            val type = constant.removeSuffix("s").replaceFirstChar { it.uppercase() }
            appendLine()
            appendLine("export const $constant = [")
            manifests.getValue(key).forEach { appendLine("  \"$it\",") }
            appendLine("] as const")
            appendLine()
            appendLine("export type $type = typeof $constant[number]")
        }
    }
    File("web/src/icons.generated.ts").apply { parentFile.mkdirs() }.writeText(ts)
    println("✓ web/src/icons.generated.ts written")
}

main()
