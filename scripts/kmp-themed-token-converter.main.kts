#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

/**
 * The themed layer lives under this group inside the Theme collection, so the file
 * holds both layers: the semantic converters skip this subtree and these keep only it.
 * The hue is therefore the SECOND path segment, which is what the output groups by.
 */
private val THEMED_GROUP = "Themed"

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

        val themedFiles = tokenFiles("theme-colors")
        require(themedFiles.isNotEmpty()) {
            "No tokens/theme-colors.*.tokens.json found — export the Theme collection from Figma first"
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
            ).filterNull().filter { it.groups.firstOrNull() == THEMED_GROUP }

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
        ).filterNull().filter { it.groups.firstOrNull() == THEMED_GROUP }

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
        grouped.keys.filterNotNull().forEach { groupName ->
            appendLine()
            appendLine("    public interface ${groupName}Colors : ThemedColor")
        }
        appendLine("}")
        appendLine()
        appendLine("/**")
        appendLine(" * The slots every themed colour provides, so one can be passed around as a value -")
        appendLine(" * a chart series, a per-role accent, a category - without naming a specific one.")
        appendLine(" *")
        appendLine(" * ```")
        appendLine(" * val series = listOf(LemonadeTheme.themed.blue, LemonadeTheme.themed.amber)")
        appendLine(" * series.map { it.background }")
        appendLine(" * ```")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("public interface ThemedColor {")
        sharedSlots.forEach { slot ->
            appendLine("    public val $slot: Color")
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
    val grouped = resources.groupBy { it.groups.getOrNull(1) }
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
