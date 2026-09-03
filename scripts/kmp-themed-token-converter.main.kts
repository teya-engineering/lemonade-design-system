#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

data class ThemedResourceData(
    val valueGroup: String,
    val valueName: String,
)

private fun primitiveReference(jsonObject: JSONObject): ThemedResourceData? {
    val aliasName = jsonObject.optString("aliasName")
    val groups = aliasName?.sanitizedGroups().orEmpty()
    if (aliasName.isNullOrBlank() || groups.isEmpty()) return null
    return ThemedResourceData(
        valueName = aliasName.sanitizedValueName(),
        valueGroup = if (groups.contains("Alpha")) {
            "Alpha.${groups.first()}"
        } else {
            "Solid.${groups.first()}"
        },
    )
}

fun main() {
    val themedOutputDir = File("kmp/ui/src/commonMain/kotlin/com/teya/lemonade")
    val interfaceOutputDir = File("kmp/tokens/src/commonMain/kotlin/com/teya/lemonade")

    try {
        if (!themedOutputDir.exists()) themedOutputDir.mkdirs()
        if (!interfaceOutputDir.exists()) interfaceOutputDir.mkdirs()

        val themedFiles = tokenFiles("themed-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/themed-colors.*.tokens.json found — export the Themed Colors collection from Figma first"
        }
        requireModes(themedFiles, "Light", "Dark")
        val modeNames = availableModeNames(themedFiles)

        modeNames.forEach { modeName ->
            val objectName = when {
                modeName.equals("Light", ignoreCase = true) -> "LemonadeLightThemedColors"
                modeName.equals("Dark", ignoreCase = true) -> "LemonadeDarkThemedColors"
                else -> "Lemonade${modeName}ThemedColors"
            }

            val resources = readFileResourceFileByMode(
                files = themedFiles,
                modeName = modeName,
                resourceMap = ::primitiveReference,
            ).filterNull()

            println("✓ Loaded $modeName themed resources")

            val objectCode = buildThemedObjectCode(
                objectName = objectName,
                scriptFilePath = "scripts/kmp-themed-token-converter.main.kts",
                resources = resources,
                modeName = modeName,
            )
            File(themedOutputDir, "$objectName.kt").writeText(objectCode)
            println("✓ $objectName.kt created")
        }

        val lightResources = readFileResourceFileByMode(
            files = themedFiles,
            modeName = modeNames.first { it.equals("Light", ignoreCase = true) },
            resourceMap = ::primitiveReference,
        ).filterNull()

        val interfaceCode = buildThemedInterfaceCode(
            scriptFilePath = "scripts/kmp-themed-token-converter.main.kts",
            resources = lightResources,
        )
        File(interfaceOutputDir, "LemonadeThemedColors.kt").writeText(interfaceCode)
        println("✓ LemonadeThemedColors.kt created")
    } catch (error: Throwable) {
        println("✗ Failed to convert themed colours: ${error.message}")
        error.printStackTrace()
        throw error
    }
}

private fun buildThemedInterfaceCode(
    scriptFilePath: String,
    resources: List<ResourceData<ThemedResourceData>>,
): String {
    val grouped = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("package com.teya.lemonade")
        appendLine()
        appendLine("import androidx.compose.ui.graphics.Color")
        appendLine()
        appendLine("/**")
        appendLine(" * Themed color tokens from Lemonade DS - Foundations.")
        appendLine(" *")
        appendLine(" * A generic, theme-aware hue palette sitting between the primitive ramps and the")
        appendLine(" * semantic tokens in [LemonadeSemanticColors]. Use these when a colour carries")
        appendLine(" * application meaning that the design system does not model - chart series,")
        appendLine(" * categories, per-role or per-status accents - instead of reaching for")
        appendLine(" * [LemonadePrimitiveColors], which is not theme-aware.")
        appendLine(" *")
        appendLine(" * Prefer a semantic token whenever one fits.")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("public interface LemonadeThemedColors {")
        grouped.keys.filterNotNull().forEach { groupName ->
            appendLine("    public val ${groupName.sanitizedValueName()}: ${groupName}Colors")
        }
        grouped.forEach { (groupName, groupResources) ->
            if (groupName != null) {
                appendLine()
                appendLine("    public interface ${groupName}Colors {")
                groupResources.forEach { resource ->
                    appendLine("        public val ${resource.name}: Color")
                }
                appendLine("    }")
            }
        }
        appendLine("}")
    }
}

private fun buildThemedObjectCode(
    objectName: String,
    scriptFilePath: String,
    resources: List<ResourceData<ThemedResourceData>>,
    modeName: String,
): String {
    val grouped = resources.groupBy { it.groups.firstOrNull() }
    return buildString {
        appendLine("package com.teya.lemonade")
        appendLine()
        appendLine("import androidx.compose.runtime.Stable")
        appendLine()
        appendLine("/**")
        appendLine(" * $modeName implementation of the themed colour palette.")
        appendLine(" * See [LemonadeThemedColors] for details on the colour structure.")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@Stable")
        appendLine("public object $objectName : LemonadeThemedColors {")
        grouped.entries.forEachIndexed { index, (groupName, groupResources) ->
            if (groupName != null) {
                if (index > 0) appendLine()
                appendLine("    override val ${groupName.sanitizedValueName()}: LemonadeThemedColors.${groupName}Colors =")
                appendLine("        object : LemonadeThemedColors.${groupName}Colors {")
                groupResources.forEach { resource ->
                    appendLine("            override val ${resource.name} = LemonadePrimitiveColors.${resource.value.valueGroup}.${resource.value.valueName}")
                }
                appendLine("        }")
            }
        }
        appendLine("}")
    }
}

main()
