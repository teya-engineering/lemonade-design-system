#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import java.io.File

private data class Color(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double,
)

fun main() {
    val colorTokensFile = tokenFile("primitive-colors.tokens.json")
    val outputDir = File("kmp/tokens/src/commonMain/kotlin/com/teya/lemonade")
    try {
        if (!colorTokensFile.exists() || !colorTokensFile.isFile) {
            error(message = "File $colorTokensFile does not exist in system.")
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        println("Reading colors from '${colorTokensFile.path}'.")
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
            scriptFilePath = "scripts/kmp-color-token-converter.main.kts",
            resources = colors,
        )
        val outputFile = File(outputDir, "LemonadePrimitiveColors.kt")
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
        appendLine("package com.teya.lemonade")
        appendLine()
        appendLine("import androidx.compose.ui.graphics.Color")
        appendLine()
        appendLine("/**")
        appendLine(" * Primitive color system from Lemonade DS - Foundations")
        appendLine(" * Supports both solid and alpha color variants")
        appendLine(" * Using floating-point RGBA public values for precision (7 decimal places)")
        appendLine(" * See [Lemonade colors](https://www.figma.com/design/3DI1AqqkYgRJgYCjOXjbDO/Review-and-update-colors?node-id=97-4923)")
        appendLine(" *")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@InternalLemonadeApi(")
        appendLine("    message = \"This API is internal to Lemonade using it directly in general is a mistake, \" +")
        appendLine("        \"accessing the colors directly is not recommended, use the LemonadeTheme instead.\",")
        appendLine(")")
        appendLine("public sealed class LemonadePrimitiveColors {")
        appendLine("    public sealed class Solid : LemonadePrimitiveColors() {")
        solidGroup
            .groupBy { resource -> resource.groupFullName }
            .entries
            .filter { (groupName, _) -> groupName != null }
            .forEachIndexed { index, (groupName, resources) ->
                append(
                    buildColorGroupCode(
                        parentGroupName = "Solid",
                        groupName = groupName!!,
                        resources = resources,
                        isFirst = index == 0,
                    )
                )
            }
        appendLine("    }")
        appendLine()
        appendLine("    public sealed class Alpha : LemonadePrimitiveColors() {")
        alphaGroup
            .groupBy { resource -> resource.groupFullName }
            .entries
            .filter { (groupName, _) -> groupName != null }
            .forEachIndexed { index, (groupName, resources) ->
                append(
                    buildColorGroupCode(
                        parentGroupName = "Alpha",
                        groupName = groupName!!.removeSuffix("Alpha"),
                        resources = resources,
                        isFirst = index == 0,
                    )
                )
            }
        appendLine("    }")
        appendLine("}")
    }
}

private fun buildColorGroupCode(
    groupName: String,
    parentGroupName: String,
    resources: List<ResourceData<Color>>,
    isFirst: Boolean = true,
): String {
    return buildString {
        if (!isFirst) appendLine()
        appendLine("        public data object $groupName : $parentGroupName() {")
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
        appendLine("            public val ${colorName}: Color = Color(")
        appendLine("                red = ${color.r.formattedFloat}f,")
        appendLine("                green = ${color.g.formattedFloat}f,")
        appendLine("                blue = ${color.b.formattedFloat}f,")
        appendLine("                alpha = ${color.a.formattedFloat}f,")
        appendLine("            )")
    }
}

private val Double.formattedFloat
    get() = String.format("%.7f", this)
        .replace(
            oldChar = ',',
            newChar = '.'
        )

main()
