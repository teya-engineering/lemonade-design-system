#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

/** The themed layer lives under this group inside the Theme collection. */
private val THEMED_GROUP = "Themed"

fun main() {
    val outputDir = File("swiftui/Sources/Lemonade")

    try {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val themedFiles = tokenFiles("theme-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/theme-colors.*.tokens.json found - export the Theme collection from Figma first"
        }
        requireModes(themedFiles, "Light", "Dark")
        val lightMode = availableModeNames(themedFiles).first { it.equals("Light", ignoreCase = true) }

        val lightFile = themedFiles.first { file ->
            dtcgModeName(JSONObject(file.readText())).equals(lightMode, ignoreCase = true)
        }
        val lightJson = JSONObject(lightFile.readText())
        require(isDtcgDocument(lightJson)) { "${lightFile.path} is not a Figma native DTCG export" }

        val tokens = dtcgTokens(lightJson)
        // The Theme export carries both layers; keep only the themed subtree.
        val tokenNames = tokens.keys
            .filter { name -> name.startsWith("$THEMED_GROUP/") }
            .sortedWith(::canonicalTokenOrder)
            .filterNot { name ->
                tokens.getValue(name).optJSONObject("\$extensions")
                    ?.optBoolean("com.figma.hiddenFromPublishing") ?: false
            }

        val themedResources = readFileResourceFileByMode(
            files = themedFiles,
            modeName = lightMode,
            resourceMap = { _ -> Unit },
        ).filter { it.groups.firstOrNull() == THEMED_GROUP }

        // Keyed by the full group-qualified path, not the leaf name alone: the themed
        // collection is grouped by hue, so leaf names ("background", "border", ...)
        // repeat across all 16 hues and a leaf-only key would collapse them onto one.
        val resourcesByPath = themedResources.associateBy { resource ->
            (resource.groups + resource.name).joinToString("/")
        }

        val resourcesWithAssets = mutableListOf<Pair<ResourceData<Unit>, String>>()
        tokenNames.forEach { name ->
            val path = (name.sanitizedGroups() + name.sanitizedSwiftValueName()).joinToString("/")
            val resource = resourcesByPath[path]
            if (resource != null) {
                resourcesWithAssets.add(resource to lemonadeAssetName(name))
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
    val grouped = resources.groupBy { it.groups.getOrNull(1) }

    // ThemedColor is only sound while every group declares exactly the same slots.
    // If a group ever differs - a variant with a reduced slot set, say - fail loudly
    // here rather than silently emitting a supertype the groups do not satisfy.
    val slotSets = grouped.values.map { group -> group.map { it.name } }
    val sharedSlots = slotSets.first()
    slotSets.forEach { slots ->
        require(slots == sharedSlots) {
            "Themed groups declare different slots, so they cannot share ThemedColor:\n" +
                "  $sharedSlots\n  vs\n  $slots"
        }
    }

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
        appendLine("/// The slots every themed color provides, so one can be passed around as a")
        appendLine("/// value - a chart series, a per-role accent, a category - without naming a")
        appendLine("/// specific one.")
        appendLine("///")
        appendLine("/// ```swift")
        appendLine("/// let series: [ThemedColor] = [LemonadeTheme.themed.blue, LemonadeTheme.themed.amber]")
        appendLine("/// series.map(\\.background)")
        appendLine("/// ```")
        appendLine("public protocol ThemedColor {")
        sharedSlots.forEach { slot ->
            appendLine("    var $slot: Color { get }")
        }
        appendLine("}")
        appendLine()
        grouped.keys.filterNotNull().forEach { groupName ->
            appendLine("/// Themed ${groupName.lowercase()} color definitions")
            appendLine("public protocol Themed${groupName}Colors: ThemedColor {}")
            appendLine()
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
    val grouped = resourcesWithAssets.groupBy { it.first.groups.getOrNull(1) }
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
