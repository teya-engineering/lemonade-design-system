#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import java.io.File

/** Tokens under this group belong to the themed layer and are generated separately. */
private val THEMED_GROUP = "Themed"

data class ThemeResourceData(
    val valueGroup: String,
    val valueName: String,
)

fun main() {
    val colorTokensFile = tokenFile("theme-colors.light.tokens.json")
    val themesOutputDir = File("kmp/ui/src/commonMain/kotlin/com/teya/lemonade")
    val interfaceOutputDir = File("kmp/tokens/src/commonMain/kotlin/com/teya/lemonade")

    try {
        if (!themesOutputDir.exists()) {
            themesOutputDir.mkdirs()
        }
        if (!interfaceOutputDir.exists()) {
            interfaceOutputDir.mkdirs()
        }

        if (!colorTokensFile.exists() || !colorTokensFile.isFile) {
            error(message = "File $colorTokensFile does not exist in system")
        }

        val themeFiles = tokenFiles("theme-colors")
        requireModes(themeFiles, "Light", "Dark")
        val modeNames = availableModeNames(themeFiles)

        modeNames.forEach { modeName ->
            val themeName = when {
                modeName.equals("Light", ignoreCase = true) -> "LemonadeLightTheme"
                modeName.equals("Dark", ignoreCase = true) -> "LemonadeDarkTheme"
                else -> "Lemonade${modeName}Theme"
            }

            val themeResources = readFileResourceFileByMode(
                files = themeFiles,
                modeName = modeName,
                resourceMap = { jsonObject ->
                    val aliasName = jsonObject.optString("aliasName")
                    val groups = aliasName?.sanitizedGroups().orEmpty()
                    if (!aliasName.isNullOrBlank() && groups.isNotEmpty()) {
                        ThemeResourceData(
                            valueName = aliasName.sanitizedValueName(),
                            valueGroup = if (groups.contains("Alpha")) {
                                "Alpha.${groups.first()}"
                            } else {
                                "Solid.${groups.first()}"
                            },
                        )
                    } else {
                        null
                    }
                },
            ).filterNull().filterNot { it.groups.firstOrNull() == THEMED_GROUP }

            println("✓ Loaded $modeName theme resource")

            val classCode = buildThemeCode(
                fileName = themeName,
                scriptFilePath = "scripts/kmp-theme-token-converter.main.kts",
                resources = themeResources,
                themeName = modeName,
            )
            println("✓ $modeName implementation generated")

            File(themesOutputDir, "$themeName.kt").writeText(classCode)
            println("✓ $themeName.kt created")
        }

        val themeResources = readFileResourceFileByMode(
            files = themeFiles,
            modeName = modeNames.first { it.equals("Light", ignoreCase = true) },
            resourceMap = { jsonObject ->
                val aliasName = jsonObject.optString("aliasName")
                val groups = aliasName?.sanitizedGroups().orEmpty()
                if (!aliasName.isNullOrBlank() && groups.isNotEmpty()) {
                    ThemeResourceData(
                        valueName = aliasName.sanitizedValueName(),
                        valueGroup = if (groups.contains("Alpha")) {
                            "Alpha.${groups.first()}"
                        } else {
                            "Solid.${groups.first()}"
                        },
                    )
                } else {
                    null
                }
            },
        ).filterNull().filterNot { it.groups.firstOrNull() == THEMED_GROUP }

        val interfaceCode = buildThemeInterfaceCode(
            scriptFilePath = "scripts/kmp-theme-token-converter.main.kts",
            resources = themeResources,
        )
        println("✓ Interface generated")

        val interfaceOutputFile = File(interfaceOutputDir, "LemonadeSemanticColors.kt")
        interfaceOutputFile.writeText(interfaceCode)
        println("✓ Definition & Implementation files created")
    } catch (error: Throwable) {
        println("✗ Failed to convert ${colorTokensFile.name}: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

private fun buildThemeInterfaceCode(
    scriptFilePath: String,
    resources: List<ResourceData<ThemeResourceData>>,
): String {
    val groupedThemeResources = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("package com.teya.lemonade")
        appendLine()
        appendLine("import androidx.compose.ui.graphics.Color")
        appendLine()
        appendLine("/**")
        appendLine(" * Semantic color tokens from Lemonade DS - Foundations")
        appendLine(" * Organized by usage categories: Background, Content, Border, and Interaction")
        appendLine(" * These tokens map to primitive colors and provide semantic meaning for UI elements")
        appendLine(" * See [Lemonade  semantic colors](https://www.figma.com/design/3DI1AqqkYgRJgYCjOXjbDO/Review-and-update-colors?node-id=97-4923)")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("public interface LemonadeSemanticColors {")
        appendLine("    /**")
        appendLine("     * Whether this palette is a dark one.")
        appendLine("     *")
        appendLine("     * This is the single source of truth for the theme's brightness: components that")
        appendLine("     * cannot express a theme through colour alone - such as brand logos, which carry")
        appendLine("     * their own fixed brand palette - resolve their asset from this flag.")
        appendLine("     *")
        appendLine("     * Defaults to `false` so that existing implementations keep compiling. Custom")
        appendLine("     * palettes intended for dark surfaces must override this, otherwise those")
        appendLine("     * components will render their light variant.")
        appendLine("     */")
        appendLine("    public val isDark: Boolean")
        appendLine("        get() = false")
        appendLine()
        groupedThemeResources.keys.forEach { groupName ->
            if (groupName != null) {
                appendLine("    public val ${groupName.sanitizedValueName()}: ${groupName}Colors")
            }
        }
        groupedThemeResources.forEach { (groupName, resources) ->
            if (groupName != null) {
                appendLine()
                append(
                    buildGroupInterfaceCode(
                        groupName = groupName,
                        resources = resources,
                    )
                )
            }
        }
        appendLine("}")
    }
}

private fun buildGroupInterfaceCode(
    groupName: String,
    resources: List<ResourceData<ThemeResourceData>>,
): String {
    return buildString {
        appendLine("    public interface ${groupName}Colors {")
        resources.forEach { resource ->
            appendLine("        public val ${resource.name}: Color")
        }
        appendLine("    }")
    }
}

private fun buildThemeCode(
    fileName: String,
    scriptFilePath: String,
    resources: List<ResourceData<ThemeResourceData>>,
    themeName: String = "Light",
): String {
    val groupedThemeResources = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("package com.teya.lemonade")
        appendLine()
        appendLine("import androidx.compose.runtime.Stable")
        appendLine()
        appendLine("/**")
        appendLine(" * $themeName theme implementation of semantic colors")
        appendLine(" * See [LemonadeSemanticColors] for details on the color structure.")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@Stable")
        appendLine("@OptIn(InternalLemonadeApi::class)")
        appendLine("public object $fileName : LemonadeSemanticColors {")
        appendLine("    override val isDark: Boolean = ${themeName.equals("Dark", ignoreCase = true)}")
        appendLine()
        groupedThemeResources.forEach { (groupName, resources) ->
            if (groupName != null) {
                append(
                    buildGroupClassCode(
                        groupName = groupName,
                        resources = resources,
                    )
                )
            }
        }
        appendLine("}")
    }
}

private fun buildGroupClassCode(
    groupName: String,
    resources: List<ResourceData<ThemeResourceData>>,
): String {
    return buildString {
        appendLine("    override val ${groupName.sanitizedValueName()}: LemonadeSemanticColors.${groupName}Colors =")
        appendLine("        object : LemonadeSemanticColors.${groupName}Colors {")
        resources.forEach { resource ->
            appendLine("            override val ${resource.name} = LemonadePrimitiveColors.${resource.value.valueGroup}.${resource.value.valueName}")
        }
        appendLine("        }")
    }
}

main()
