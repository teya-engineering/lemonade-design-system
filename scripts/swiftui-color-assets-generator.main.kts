#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.json:json:20240303")
@file:Import("swiftui-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Script to generate SwiftUI Color Assets from theme tokens.
 *
 * This generates:
 * 1. .colorset folders in Assets.xcassets with light/dark variants
 * 2. Swift extension file for easy color access
 *
 * Usage: kotlin scripts/swiftui-color-assets-generator.main.kts
 */

data class ColorValue(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double,
)

data class ColorResource(
    val group: String,      // e.g., "Content", "Background", "Border", "Interaction"
    val name: String,       // e.g., "contentPrimary", "bgDefault"
    val assetName: String,  // e.g., "lemonade-content-primary"
    val lightColor: ColorValue,
    val darkColor: ColorValue?, // null if dark theme not available yet
)

fun main() {
    val themeFile = tokenFile("theme-colors.light.tokens.json")

    val assetsDir = File("swiftui/Sources/Lemonade/Resources/Assets.xcassets/Colors")
    val swiftOutputDir = File("swiftui/Sources/Lemonade")

    try {
        // Create Colors folder in Assets.xcassets
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        // Create Contents.json for the Colors folder
        val folderContentsJson = """
{
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
""".trimIndent()
        File(assetsDir, "Contents.json").writeText(folderContentsJson)

        if (!themeFile.exists()) {
            error("File $themeFile does not exist")
        }

        val themeFiles = tokenFiles("theme-colors")
        requireModes(themeFiles, "Light", "Dark")
        val modeNames = availableModeNames(themeFiles)
        val lightColors = parseThemeColors(themeFiles, modeNames.first { it.equals("Light", ignoreCase = true) })
        println("✓ Loaded ${lightColors.size} colors from light theme")
        val darkColors = parseThemeColors(themeFiles, modeNames.first { it.equals("Dark", ignoreCase = true) })
        println("✓ Loaded ${darkColors.size} colors from dark theme")

        // Create color resources with both light and dark values
        val colorResources = lightColors.map { (key, lightColor) ->
            val parts = key.split("/")
            val group = parts.getOrNull(0)?.sanitizeGroup() ?: "Other"
            val name = parts.last().sanitizeSwiftName()
            val assetName = "lemonade-${parts.joinToString("-") { it.lowercase().replace("_", "-") }}"

            ColorResource(
                group = group,
                name = name,
                assetName = assetName,
                lightColor = lightColor,
                darkColor = darkColors[key],
            )
        }

        // Generate color assets
        colorResources.forEach { resource ->
            generateColorAsset(assetsDir, resource)
        }
        println("✓ Generated ${colorResources.size} color assets")

        // Generate Color+Lemonade.swift shorthand extensions
        val shorthandCode = generateColorShorthand(colorResources)
        File(swiftOutputDir, "Extensions/Color+Lemonade.swift").writeText(shorthandCode)
        println("✓ Generated Color+Lemonade.swift")

        println("\n✅ Color assets generation complete!")
        println("   Assets location: ${assetsDir.absolutePath}")

    } catch (error: Throwable) {
        println("✗ Failed: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

fun parseThemeColors(files: List<File>, modeName: String): Map<String, ColorValue> {
    val colors = linkedMapOf<String, ColorValue>()
    readFileResourceFileByModeRaw(files, modeName) { name, resolved ->
        colors[name] = ColorValue(
            r = resolved.getDouble("r"),
            g = resolved.getDouble("g"),
            b = resolved.getDouble("b"),
            a = resolved.optDouble("a", 1.0),
        )
    }
    return colors
}

fun generateColorAsset(assetsDir: File, resource: ColorResource) {
    val colorsetDir = File(assetsDir, "${resource.assetName}.colorset")
    colorsetDir.mkdirs()

    val light = resource.lightColor
    val dark = resource.darkColor ?: resource.lightColor // Fallback to light if no dark

    val contentsJson = """
{
  "colors" : [
    {
      "color" : {
        "color-space" : "srgb",
        "components" : {
          "alpha" : "${formatColorComponent(light.a)}",
          "blue" : "${formatColorComponent(light.b)}",
          "green" : "${formatColorComponent(light.g)}",
          "red" : "${formatColorComponent(light.r)}"
        }
      },
      "idiom" : "universal"
    },
    {
      "appearances" : [
        {
          "appearance" : "luminosity",
          "value" : "dark"
        }
      ],
      "color" : {
        "color-space" : "srgb",
        "components" : {
          "alpha" : "${formatColorComponent(dark.a)}",
          "blue" : "${formatColorComponent(dark.b)}",
          "green" : "${formatColorComponent(dark.g)}",
          "red" : "${formatColorComponent(dark.r)}"
        }
      },
      "idiom" : "universal"
    }
  ],
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
""".trimIndent()

    File(colorsetDir, "Contents.json").writeText(contentsJson)
}

fun formatColorComponent(value: Double): String {
    return "%.3f".format(Locale.US, value)
}

fun generateColorShorthand(resources: List<ColorResource>): String {
    val grouped = resources.groupBy { it.group }

    return buildString {
        appendLine("import SwiftUI")
        appendLine()
        appendLine("// MARK: - Color Namespace Extensions")
        appendLine()
        appendLine("/// Enables shorthand color access like `.content.contentPrimary`")
        appendLine("///")
        appendLine("/// Colors automatically adapt to light/dark mode via Asset Catalog.")
        appendLine("///")
        appendLine("/// ## Usage")
        appendLine("/// ```swift")
        appendLine("/// Text(\"Hello\")")
        appendLine("///     .foregroundStyle(.content.contentPrimary)")
        appendLine("///     .background(.bg.bgDefault)")
        appendLine("/// ```")
        appendLine("///")
        appendLine("///  Auto-generated content.")
        appendLine("///  ⚠️ **DO NOT MODIFY THIS FILE MANUALLY** ⚠️")
        appendLine("///  @generated by scripts/swiftui-color-assets-generator.main.kts")
        appendLine()

        // Generate shorthand structs for each group
        grouped.forEach { (group, colors) ->
            val shorthandName = when (group) {
                "Background" -> "bg"
                "Content" -> "content"
                "Border" -> "border"
                "Interaction" -> "interaction"
                else -> group.lowercase()
            }

            appendLine("// MARK: - $group Colors Namespace")
            appendLine()
            appendLine("public struct Lemonade${group}ColorsShorthand: Sendable {")
            colors.forEach { color ->
                appendLine("    public let ${color.name} = Color(\"${color.assetName}\", bundle: .lemonade)")
            }
            appendLine("}")
            appendLine()
            // Cache a single instance per namespace so the `static var` accessors below
            // don't rebuild the struct (and its Color values) on every access.
            appendLine("private let lemonade${group}ColorsShorthand = Lemonade${group}ColorsShorthand()")
            appendLine()
        }

        // Generate ShapeStyle extensions
        appendLine("// MARK: - ShapeStyle Extensions")
        appendLine()
        appendLine("public extension ShapeStyle where Self == Color {")

        grouped.forEach { (group, colors) ->
            val shorthandName = when (group) {
                "Background" -> "bg"
                "Content" -> "content"
                "Border" -> "border"
                "Interaction" -> "interaction"
                else -> group.lowercase()
            }
            appendLine("    /// $group color tokens")
            // Use a real member of the group as the example; fall back to the
            // first token so groups without a "<name>Default" don't produce a
            // dangling reference (e.g. scoped, interaction).
            val exampleToken = when (group) {
                "Content" -> "contentPrimary"
                "Background" -> "bgDefault"
                "Border" -> "borderNeutralMedium"
                "Shadow" -> "shadowDefault"
                else -> colors.first().name
            }
            appendLine("    /// Usage: `.foregroundStyle(.$shorthandName.$exampleToken)`")
            appendLine("    static var $shorthandName: Lemonade${group}ColorsShorthand { lemonade${group}ColorsShorthand }")
            appendLine()
        }

        appendLine("}")
    }
}

fun String.sanitizeGroup(): String {
    return split("/").firstOrNull()
        ?.split("-")
        ?.joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
        ?: "Other"
}

fun String.sanitizeSwiftName(): String {
    return split("-")
        .mapIndexed { index, word ->
            if (index == 0) word.lowercase()
            else word.replaceFirstChar { it.uppercase() }
        }
        .joinToString("")
}

main()
