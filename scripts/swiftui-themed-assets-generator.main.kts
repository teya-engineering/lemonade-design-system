#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import java.io.File

/** The themed layer lives under this group inside the Theme collection. */
private val THEMED_GROUP = "Themed"

fun main() {
    val assetsDir = File("swiftui/Sources/Lemonade/Resources/Assets.xcassets/Colors")

    try {
        require(assetsDir.isDirectory) {
            "$assetsDir does not exist - run swiftui-color-assets-generator.main.kts first"
        }

        val themedFiles = tokenFiles("theme-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/theme-colors.*.tokens.json found - export the Theme collection from Figma first"
        }
        requireModes(themedFiles, "Light", "Dark")
        val modeNames = availableModeNames(themedFiles)

        // The Theme export carries both layers. Keep only the themed subtree — the
        // semantic colorsets belong to swiftui-color-assets-generator, and emitting
        // them here would have two generators writing the same folders.
        val themedOnly = { colors: Map<String, ColorValue> ->
            colors.filterKeys { key -> key.startsWith("$THEMED_GROUP/") }
        }

        val lightColors = themedOnly(
            parseThemeColors(themedFiles, modeNames.first { it.equals("Light", ignoreCase = true) })
        )
        println("✓ Loaded ${lightColors.size} themed colors from light mode")
        val darkColors = themedOnly(
            parseThemeColors(themedFiles, modeNames.first { it.equals("Dark", ignoreCase = true) })
        )
        println("✓ Loaded ${darkColors.size} themed colors from dark mode")

        val colorResources = lightColors.map { (key, lightColor) ->
            val parts = key.split("/")
            ColorResource(
                group = parts.getOrNull(0)?.sanitizeGroup() ?: "Other",
                name = parts.last().sanitizeSwiftName(),
                assetName = lemonadeAssetName(key),
                lightColor = lightColor,
                darkColor = darkColors[key],
            )
        }

        colorResources.forEach { resource ->
            generateColorAsset(assetsDir, resource)
        }
        println("✓ Generated ${colorResources.size} themed color assets")

        println("\n✅ Themed colour assets generation complete!")
    } catch (error: Throwable) {
        println("✗ Failed: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

main()
