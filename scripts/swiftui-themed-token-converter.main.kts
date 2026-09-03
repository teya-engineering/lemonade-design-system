#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

fun main() {
    val outputDir = File("swiftui/Sources/Lemonade")

    try {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val themedFiles = tokenFiles("themed-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/themed-colors.*.tokens.json found - export the Themed Colors collection from Figma first"
        }
        requireModes(themedFiles, "Light", "Dark")
        val lightMode = availableModeNames(themedFiles).first { it.equals("Light", ignoreCase = true) }

        val lightFile = themedFiles.first { file ->
            dtcgModeName(JSONObject(file.readText())).equals(lightMode, ignoreCase = true)
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

        val themedResources = readFileResourceFileByMode(
            files = themedFiles,
            modeName = lightMode,
            resourceMap = { _ -> Unit },
        )

        val resourcesByName = themedResources.groupBy { it.name }.mapValues { it.value.first() }

        val resourcesWithAssets = mutableListOf<Pair<ResourceData<Unit>, String>>()
        tokenNames.forEach { name ->
            val resource = resourcesByName[name.sanitizedSwiftValueName()]
            if (resource != null) {
                resourcesWithAssets.add(resource to lemonadeAssetName(name, prefix = "themed"))
            }
        }

        val protocolCode = buildThemedProtocolCode(
            scriptFilePath = "scripts/swiftui-themed-token-converter.main.kts",
            resources = themedResources,
        )
        val adaptiveCode = buildAdaptiveThemedCode(
            scriptFilePath = "scripts/swiftui-themed-token-converter.main.kts",
            resourcesWithAssets = resourcesWithAssets,
        )

        File(outputDir, "LemonadeThemedColors.swift").writeText(protocolCode + "\n" + adaptiveCode)
        println("✓ LemonadeThemedColors.swift created (${resourcesWithAssets.size} tokens)")

        println("\n✅ Themed colour generation complete!")
    } catch (error: Throwable) {
        println("✗ Failed to convert themed colours: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

private fun buildThemedProtocolCode(
    scriptFilePath: String,
    resources: List<ResourceData<Unit>>,
): String {
    val grouped = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("import SwiftUI")
        appendLine()
        appendLine("/// Themed color tokens from Lemonade DS - Foundations")
        appendLine("///")
        appendLine("/// A generic, theme-aware hue palette sitting between the primitive ramps and the")
        appendLine("/// semantic tokens in `LemonadeSemanticColors`. Use these when a colour carries")
        appendLine("/// application meaning the design system does not model - chart series, categories,")
        appendLine("/// per-role or per-status accents.")
        appendLine("///")
        appendLine("/// Prefer a semantic token whenever one fits.")
        appendLine("///")
        defaultSwiftAutoGenerationMessage(scriptFilePath = scriptFilePath).lines().forEach { line ->
            appendLine("/// $line")
        }
        appendLine()
        grouped.forEach { (groupName, groupResources) ->
            if (groupName != null) {
                appendLine("/// Themed ${groupName.lowercase()} color definitions")
                appendLine("public protocol Themed${groupName}Colors {")
                groupResources.forEach { resource ->
                    appendLine("    var ${resource.name}: Color { get }")
                }
                appendLine("}")
                appendLine()
            }
        }
        appendLine("/// Protocol defining themed color categories")
        appendLine("public protocol LemonadeThemedColors {")
        grouped.keys.filterNotNull().forEach { groupName ->
            appendLine("    var ${groupName.sanitizedSwiftValueName()}: Themed${groupName}Colors { get }")
        }
        appendLine("}")
    }
}

private fun buildAdaptiveThemedCode(
    scriptFilePath: String,
    resourcesWithAssets: List<Pair<ResourceData<Unit>, String>>,
): String {
    val grouped = resourcesWithAssets.groupBy { it.first.groups.firstOrNull() }
    return buildString {
        grouped.forEach { (groupName, groupResources) ->
            if (groupName != null) {
                appendLine("private struct AdaptiveThemed${groupName}Colors: Themed${groupName}Colors {")
                groupResources.forEach { (resource, assetName) ->
                    appendLine("    let ${resource.name} = Color(\"${assetName}\", bundle: .lemonade)")
                }
                appendLine("}")
                appendLine()
            }
        }
        appendLine("/// Themed palette implementation - colors resolve automatically via Asset Catalog")
        appendLine("public struct LemonadeAdaptiveThemedColors: LemonadeThemedColors {")
        appendLine("    public init() {}")
        appendLine()
        grouped.keys.filterNotNull().forEach { groupName ->
            appendLine("    public let ${groupName.sanitizedSwiftValueName()}: Themed${groupName}Colors = AdaptiveThemed${groupName}Colors()")
        }
        appendLine("}")
    }
}

main()
