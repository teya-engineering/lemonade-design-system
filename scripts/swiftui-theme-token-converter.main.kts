#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

fun main() {
    val colorTokensFile = tokenFile("theme-colors.light.tokens.json")
    val outputDir = File("swiftui/Sources/Lemonade")

    try {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        if (!colorTokensFile.exists() || !colorTokensFile.isFile) {
            error(message = "File $colorTokensFile does not exist in system")
        }

        val themeFiles = tokenFiles("theme-colors")
        requireModes(themeFiles, "Light", "Dark")
        val modeNames = availableModeNames(themeFiles)
        val lightMode = modeNames.first { it.equals("Light", ignoreCase = true) }

        val lightFile = themeFiles.first { file ->
            val json = JSONObject(file.readText())
            dtcgModeName(json).equals(lightMode, ignoreCase = true)
        }
        val lightJson = JSONObject(lightFile.readText())
        require(isDtcgDocument(lightJson)) { "${lightFile.path} is not a Figma native DTCG export" }

        val tokens = dtcgTokens(lightJson)
        val tokenNames = tokens.keys
            .sortedWith(::canonicalTokenOrder)
            .filterNot { name ->
                tokens.getValue(name).optJSONObject("\$extensions")
                    ?.optBoolean("com.figma.hiddenFromPublishing") ?: false
            }

        val themeResources = readFileResourceFileByMode(
            files = themeFiles,
            modeName = lightMode,
            resourceMap = { _ -> Unit },
        )

        // Indexed once rather than scanned per token — first-wins, matching the
        // previous `find` semantics if two names ever sanitize to the same value.
        val resourcesByName = themeResources.groupBy { it.name }.mapValues { it.value.first() }

        val resourcesWithAssets = mutableListOf<Pair<ResourceData<Unit>, String>>()
        tokenNames.forEach { name ->
            val assetName = "lemonade-${name.split("/").joinToString("-") { it.lowercase().replace("_", "-") }}"
            val resource = resourcesByName[name.sanitizedSwiftValueName()]
            if (resource != null) {
                resourcesWithAssets.add(resource to assetName)
            }
        }

        // Generate the protocol
        val interfaceCode = buildThemeProtocolCode(
            scriptFilePath = "scripts/swiftui-theme-token-converter.main.kts",
            resources = themeResources,
        )
        println("✓ Protocol generated")

        val interfaceOutputFile = File(outputDir, "LemonadeSemanticColors.swift")
        interfaceOutputFile.writeText(interfaceCode)
        println("✓ LemonadeSemanticColors.swift created")

        // Generate single adaptive theme using Asset Catalog colors
        val adaptiveCode = buildAdaptiveThemeCode(
            scriptFilePath = "scripts/swiftui-theme-token-converter.main.kts",
            resourcesWithAssets = resourcesWithAssets,
        )
        println("✓ Adaptive theme generated")

        val adaptiveOutputFile = File(outputDir, "LemonadeAdaptiveTheme.swift")
        adaptiveOutputFile.writeText(adaptiveCode)
        println("✓ LemonadeAdaptiveTheme.swift created")

        println("\n✅ Theme generation complete!")
    } catch (error: Throwable) {
        println("✗ Failed to convert ${colorTokensFile.name}: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

private fun buildThemeProtocolCode(
    scriptFilePath: String,
    resources: List<ResourceData<Unit>>,
): String {
    val groupedThemeResources = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("import SwiftUI")
        appendLine()
        appendLine("/// Semantic color tokens from Lemonade DS - Foundations")
        appendLine("/// Organized by usage categories: Background, Content, Border, and Interaction")
        appendLine("/// These tokens map to primitive colors and provide semantic meaning for UI elements")
        appendLine("/// See [Lemonade semantic colors](https://www.figma.com/design/3DI1AqqkYgRJgYCjOXjbDO/Review-and-update-colors?node-id=97-4923)")
        appendLine("///")
        defaultSwiftAutoGenerationMessage(scriptFilePath = scriptFilePath).lines().forEach { line ->
            appendLine("/// $line")
        }
        appendLine()
        // Generate color group protocols
        groupedThemeResources.forEach { (groupName, resources) ->
            if (groupName != null) {
                append(
                    buildGroupProtocolCode(
                        groupName = groupName,
                        resources = resources,
                    )
                )
                appendLine()
            }
        }
        appendLine("/// Protocol defining semantic color categories")
        appendLine("public protocol LemonadeSemanticColors {")
        groupedThemeResources.keys.forEach { groupName ->
            if (groupName != null) {
                appendLine("    var ${groupName.sanitizedSwiftValueName()}: ${groupName}Colors { get }")
            }
        }
        appendLine("}")
    }
}

private fun buildGroupProtocolCode(
    groupName: String,
    resources: List<ResourceData<Unit>>,
): String {
    return buildString {
        appendLine("/// ${groupName} color definitions")
        appendLine("public protocol ${groupName}Colors {")
        resources.forEach { resource ->
            appendLine("    var ${resource.name}: Color { get }")
        }
        appendLine("}")
    }
}

private fun buildAdaptiveThemeCode(
    scriptFilePath: String,
    resourcesWithAssets: List<Pair<ResourceData<Unit>, String>>,
): String {
    val grouped = resourcesWithAssets.groupBy { it.first.groups.firstOrNull() }
    return buildString {
        appendLine("import SwiftUI")
        appendLine()
        appendLine("/// Adaptive theme that uses Asset Catalog colors with built-in light/dark appearance variants.")
        appendLine("///")
        appendLine("/// Returns `Color` values from the asset catalog that automatically adapt to the")
        appendLine("/// system's current appearance (light/dark mode) without any manual switching.")
        appendLine("///")
        appendLine("/// Usage:")
        appendLine("/// ```swift")
        appendLine("/// // Set once at app startup — no need to ever switch")
        appendLine("/// LemonadeTheme.colors = LemonadeAdaptiveTheme()")
        appendLine("/// ```")
        appendLine("///")
        defaultSwiftAutoGenerationMessage(scriptFilePath = scriptFilePath).lines().forEach { line ->
            appendLine("/// $line")
        }
        appendLine()

        // Generate group structs
        grouped.forEach { (groupName, resources) ->
            if (groupName != null) {
                appendLine("private struct Adaptive${groupName}Colors: ${groupName}Colors {")
                resources.forEach { (resource, assetName) ->
                    appendLine("    let ${resource.name} = Color(\"${assetName}\", bundle: .lemonade)")
                }
                appendLine("}")
                appendLine()
            }
        }

        appendLine("/// Adaptive theme implementation — colors resolve automatically via Asset Catalog")
        appendLine("public struct LemonadeAdaptiveTheme: LemonadeSemanticColors {")
        appendLine("    public init() {}")
        appendLine()
        grouped.forEach { (groupName, _) ->
            if (groupName != null) {
                appendLine("    public let ${groupName.sanitizedSwiftValueName()}: ${groupName}Colors = Adaptive${groupName}Colors()")
            }
        }
        appendLine("}")
    }
}

main()
