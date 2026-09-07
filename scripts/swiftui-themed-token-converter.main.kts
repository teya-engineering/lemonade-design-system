#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

/** The themed layer lives under this group inside the Theme collection. */
private val THEMED_GROUP = "Themed"

/** The nested variant group inside each hue. */
private val SUBTLE_GROUP = "Subtle"

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

private fun <T> partition(
    resources: List<ResourceData<T>>,
): Pair<Map<String, List<ResourceData<T>>>, Map<String, List<ResourceData<T>>>> {
    val primary = resources.filter { it.groups.size == 2 }.groupBy { it.groups[1] }
    val subtle = resources.filter { it.groups.size == 3 && it.groups[2] == SUBTLE_GROUP }
        .groupBy { it.groups[1] }
    require(primary.keys == subtle.keys) {
        "Every hue needs both a primary and a subtle group.\n" +
            "  primary only: ${primary.keys - subtle.keys}\n  subtle only: ${subtle.keys - primary.keys}"
    }
    return primary to subtle
}

private fun <T> sharedSlots(
    primary: Map<String, List<ResourceData<T>>>,
    subtle: Map<String, List<ResourceData<T>>>,
): Pair<List<String>, List<String>> {
    fun uniform(groups: Map<String, List<ResourceData<T>>>, what: String): List<String> {
        val sets = groups.values.map { group -> group.map { it.name } }
        val first = sets.first()
        sets.forEach { slots ->
            require(slots == first) { "Themed $what groups declare different slots:\n  $first\n  vs\n  $slots" }
        }
        return first
    }
    val common = uniform(subtle, "subtle")
    val all = uniform(primary, "primary")
    require(all.containsAll(common)) {
        "Subtle slots must be a subset of the primary slots so both can share ThemedColor.\n" +
            "  primary: $all\n  subtle:  $common"
    }
    return common to all.filterNot { it in common }
}

private fun buildThemedProtocolCode(
    scriptFilePath: String,
    resources: List<ResourceData<Unit>>,
): String {
    val (primary, subtle) = partition(resources)
    val (commonSlots, primaryOnlySlots) = sharedSlots(primary, subtle)
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
        appendLine("/// Each hue carries a saturated palette and a `subtle` one, so a component can hold")
        appendLine("/// either as a `ThemedColor` and style itself from it.")
        appendLine("///")
        appendLine("/// Prefer a semantic token whenever one fits.")
        appendLine("///")
        defaultSwiftAutoGenerationMessage(scriptFilePath = scriptFilePath).lines().forEach { line ->
            appendLine("/// $line")
        }
        appendLine()
        appendLine("/// The slots every themed palette provides, saturated or subtle alike, so one can be")
        appendLine("/// passed around as a value - a chart series, a per-role accent, a category.")
        appendLine("///")
        appendLine("/// ```swift")
        appendLine("/// let style: ThemedColor = subtle ? LemonadeTheme.themed.blue.subtle : LemonadeTheme.themed.blue")
        appendLine("/// ```")
        appendLine("public protocol ThemedColor {")
        commonSlots.forEach { appendLine("    var $it: Color { get }") }
        appendLine("}")
        appendLine()
        appendLine("/// A hue's saturated palette. Adds the slots that only make sense on the hue itself,")
        appendLine("/// plus its `subtle` counterpart.")
        appendLine("public protocol ThemedPrimaryColor: ThemedColor {")
        primaryOnlySlots.forEach { appendLine("    var $it: Color { get }") }
        appendLine("    var subtle: ThemedColor { get }")
        appendLine("}")
        appendLine()
        primary.keys.forEach { hue ->
            appendLine("/// Themed ${hue.lowercase()} color definitions")
            appendLine("public protocol Themed${hue}Colors: ThemedPrimaryColor {}")
            appendLine()
        }
        appendLine("/// Protocol defining themed color categories")
        appendLine("public protocol LemonadeThemedColors {")
        primary.keys.forEach { hue ->
            appendLine("    var ${hue.sanitizedSwiftValueName()}: Themed${hue}Colors { get }")
        }
        appendLine("}")
    }
}

private fun buildAdaptiveThemedCode(
    scriptFilePath: String,
    resourcesWithAssets: List<Pair<ResourceData<Unit>, String>>,
): String {
    val assetOf = resourcesWithAssets.associate { (resource, asset) ->
        (resource.groups + resource.name).joinToString("/") to asset
    }
    val (primary, subtle) = partition(resourcesWithAssets.map { it.first })
    fun asset(resource: ResourceData<Unit>) =
        assetOf.getValue((resource.groups + resource.name).joinToString("/"))

    return buildString {
        primary.keys.forEach { hue ->
            appendLine("private struct AdaptiveThemed${hue}SubtleColors: ThemedColor {")
            subtle.getValue(hue).forEach { resource ->
                appendLine("    let ${resource.name} = Color(\"${asset(resource)}\", bundle: .lemonade)")
            }
            appendLine("}")
            appendLine()
            appendLine("private struct AdaptiveThemed${hue}Colors: Themed${hue}Colors {")
            primary.getValue(hue).forEach { resource ->
                appendLine("    let ${resource.name} = Color(\"${asset(resource)}\", bundle: .lemonade)")
            }
            appendLine("    let subtle: ThemedColor = AdaptiveThemed${hue}SubtleColors()")
            appendLine("}")
            appendLine()
        }
        appendLine("/// Themed palette implementation - colors resolve automatically via Asset Catalog")
        appendLine("public struct LemonadeAdaptiveThemedColors: LemonadeThemedColors {")
        appendLine("    public init() {}")
        appendLine()
        primary.keys.forEach { hue ->
            appendLine("    public let ${hue.sanitizedSwiftValueName()}: Themed${hue}Colors = AdaptiveThemed${hue}Colors()")
        }
        appendLine("}")
    }
}

main()
