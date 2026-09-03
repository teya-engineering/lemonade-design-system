#!/usr/bin/env kotlin

@file:Import("swiftui-resource-file-loading.main.kts")

import java.io.File

fun main() {
    val assetsDir = File("swiftui/Sources/Lemonade/Resources/Assets.xcassets/Colors")

    try {
        require(assetsDir.isDirectory) {
            "$assetsDir does not exist - run swiftui-color-assets-generator.main.kts first"
        }

        val themedFiles = tokenFiles("themed-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/themed-colors.*.tokens.json found - export the Themed Colors collection from Figma first"
        }
        requireModes(themedFiles, "Light", "Dark")
        val modeNames = availableModeNames(themedFiles)

        val lightColors = parseThemeColors(themedFiles, modeNames.first { it.equals("Light", ignoreCase = true) })
        println("✓ Loaded ${lightColors.size} themed colors from light mode")
        val darkColors = parseThemeColors(themedFiles, modeNames.first { it.equals("Dark", ignoreCase = true) })
        println("✓ Loaded ${darkColors.size} themed colors from dark mode")

        val colorResources = lightColors.map { (key, lightColor) ->
            val parts = key.split("/")
            ColorResource(
                group = parts.getOrNull(0)?.sanitizeGroup() ?: "Other",
                name = parts.last().sanitizeSwiftName(),
                assetName = lemonadeAssetName(key, prefix = "themed"),
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
