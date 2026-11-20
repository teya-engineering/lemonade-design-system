#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import java.io.File

private data class Color(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double,
)

fun main() {
    val colorTokensFile = tokenFile("primitive-colors.tokens.json")
    val outputDir = File("swiftui/Sources/Lemonade")
    try {
        if (!colorTokensFile.exists() || !colorTokensFile.isFile) {
            error(message = "File $colorTokensFile does not exist in system.")
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        println("Reading colors from 'tokens/primitive-colors.json' file.")
        val colors = readFileResourceFile(
            file = colorTokensFile,
            includeHidden = true,
            resourceMap = { jsonObject ->
                val resolvedValue = jsonObject.getJSONObject("resolvedValue")
                Color(
                    r = resolvedValue.getDouble("r"),
                    g = resolvedValue.getDouble("g"),
                    b = resolvedValue.getDouble("b"),
                    a = resolvedValue.getDouble("a"),
                )
            }
        )
        println("Colors read and parsed.")
        val generatedCode = generateColorCodes(
            scriptFilePath = "scripts/swiftui-color-token-converter.main.kts",
            resources = colors,
        )
        val outputFile = File(outputDir, "LemonadePrimitiveColors.swift")
        outputFile.writeText(generatedCode)

        println("✓ Converted ${colorTokensFile.name} -> ${outputFile.name}!")
    } catch (error: Throwable) {
        println("✗ Failed to convert ${colorTokensFile.name}: ${error.message}")
        throw error
    }
}

private fun generateColorCodes(
    scriptFilePath: String,
    resources: List<ResourceData<Color>>,
): String {
    val (alphaGroup, solidGroup) = resources.partition { resource -> resource.groups.contains("Alpha") }
    return buildString {
        appendLine("import SwiftUI")
        appendLine()
        appendLine("/// Primitive color system from Lemonade DS - Foundations")
        appendLine("/// Supports both solid and alpha color variants")
        appendLine("/// Using floating-point RGBA values for precision")
        appendLine("/// See [Lemonade colors](https://www.figma.com/design/3DI1AqqkYgRJgYCjOXjbDO/Review-and-update-colors?node-id=97-4923)")
        appendLine("///")
        defaultSwiftAutoGenerationMessage(scriptFilePath = scriptFilePath).lines().forEach { line ->
            appendLine("/// $line")
        }
        appendLine()
        appendLine("/// Internal API - Use LemonadeTheme instead of accessing colors directly")
        appendLine("public enum LemonadePrimitiveColors {")
        appendLine("    public enum Solid {")
        solidGroup
            .groupBy { resource -> resource.groupFullName }
            .forEach { (groupName, resources) ->
                if (groupName != null) {
                    append(
                        buildColorGroupCode(
                            groupName = groupName,
                            resources = resources,
                        )
                    )
                }
            }
        appendLine("    }")
        appendLine()
        appendLine("    public enum Alpha {")
        alphaGroup
            .groupBy { resource -> resource.groupFullName }
            .forEach { (groupName, resources) ->
                if (groupName != null) {
                    append(
                        buildColorGroupCode(
                            groupName = groupName.removeSuffix("Alpha"),
                            resources = resources,
                        )
                    )
                }
            }
        appendLine("    }")
        appendLine("}")
    }
}

private fun buildColorGroupCode(
    groupName: String,
    resources: List<ResourceData<Color>>,
): String {
    return buildString {
        appendLine("        public enum $groupName {")
        resources.forEach { resource ->
            append(
                colorCode(
                    colorName = resource.name,
                    color = resource.value,
                )
            )
        }
        appendLine("        }")
    }
}

private fun colorCode(
    colorName: String,
    color: Color,
): String {
    return buildString {
        appendLine("            public static let ${colorName}: Color = Color(")
        appendLine("                red: ${color.r.formattedDouble},")
        appendLine("                green: ${color.g.formattedDouble},")
        appendLine("                blue: ${color.b.formattedDouble},")
        appendLine("                opacity: ${color.a.formattedDouble}")
        appendLine("            )")
    }
}

private val Double.formattedDouble
    get() = String.format("%.7f", this)
        .replace(
            oldChar = ',',
            newChar = '.'
        )

main()
