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

/** The nested variant group inside each hue. */
private val SUBTLE_GROUP = "Subtle"

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

/** Splits the themed resources into the per-hue primary slots and the nested subtle ones. */
private fun partition(
    resources: List<ResourceData<ThemedResourceData>>,
): Pair<Map<String, List<ResourceData<ThemedResourceData>>>, Map<String, List<ResourceData<ThemedResourceData>>>> {
    val primary = resources.filter { it.groups.size == 2 }.groupBy { it.groups[1] }
    val subtle = resources.filter { it.groups.size == 3 && it.groups[2] == SUBTLE_GROUP }
        .groupBy { it.groups[1] }
    val stray = resources.filter { it.groups.size > 2 && it.groups.getOrNull(2) != SUBTLE_GROUP }
    require(stray.isEmpty()) {
        "Unexpected themed nesting, only '$SUBTLE_GROUP' is supported: " +
            stray.joinToString { (it.groups + it.name).joinToString("/") }
    }
    require(primary.keys == subtle.keys) {
        "Every hue needs both a primary and a subtle group.\n" +
            "  primary only: ${primary.keys - subtle.keys}\n  subtle only: ${subtle.keys - primary.keys}"
    }
    return primary to subtle
}

/**
 * The shared supertypes are only sound while every hue declares the same slots, and while
 * the subtle slots are a subset of the primary ones. If that stops holding - a variant with
 * its own extra slot, say - fail loudly rather than emit types the groups do not satisfy.
 */
private fun sharedSlots(
    primary: Map<String, List<ResourceData<ThemedResourceData>>>,
    subtle: Map<String, List<ResourceData<ThemedResourceData>>>,
): Pair<List<String>, List<String>> {
    fun uniform(groups: Map<String, List<ResourceData<ThemedResourceData>>>, what: String): List<String> {
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

private fun buildThemedInterfaceCode(
    scriptFilePath: String,
    resources: List<ResourceData<ThemedResourceData>>,
): String {
    val (primary, subtle) = partition(resources)
    val (commonSlots, primaryOnlySlots) = sharedSlots(primary, subtle)
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
        appendLine(" * Every hue is a [ThemedPrimaryColor], carrying a saturated palette and a")
        appendLine(" * [ThemedPrimaryColor.subtle] one, so a component can hold either as a [ThemedColor]")
        appendLine(" * and style itself from it. Hues share one type deliberately: they differ in value,")
        appendLine(" * not in shape, so a chart series or a per-role mapping can hold them together.")
        appendLine(" *")
        appendLine(" * Prefer a semantic token whenever one fits.")
        appendLine(" *")
        appendLine(" * Experimental: the shape of this layer is still settling. Opt in with")
        appendLine(" * `@OptIn(ExperimentalLemonadeApi::class)` and expect to revisit call sites.")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@ExperimentalLemonadeApi")
        appendLine("public interface LemonadeThemedColors {")
        primary.keys.forEach { hue ->
            appendLine("    public val ${hue.sanitizedValueName()}: ThemedPrimaryColor")
        }
        appendLine("}")
        appendLine()
        appendLine("/**")
        appendLine(" * The slots every themed palette provides, saturated or subtle alike, so one can be")
        appendLine(" * passed around as a value - a chart series, a per-role accent, a category.")
        appendLine(" *")
        appendLine(" * ```")
        appendLine(" * val style: ThemedColor = if (subtle) themed.blue.subtle else themed.blue")
        appendLine(" * Box(Modifier.background(style.background).border(1.dp, style.border))")
        appendLine(" * ```")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@ExperimentalLemonadeApi")
        appendLine("public interface ThemedColor {")
        commonSlots.forEach { appendLine("    public val $it: Color") }
        appendLine("}")
        appendLine()
        appendLine("/**")
        appendLine(" * A hue's saturated palette. Adds the slots that only make sense on the hue itself,")
        appendLine(" * plus its [subtle] counterpart.")
        append(defaultAutoGenerationMessage(scriptFilePath = scriptFilePath))
        appendLine(" */")
        appendLine("@ExperimentalLemonadeApi")
        appendLine("public interface ThemedPrimaryColor : ThemedColor {")
        primaryOnlySlots.forEach { appendLine("    public val $it: Color") }
        appendLine("    public val subtle: ThemedColor")
        appendLine("}")
    }
}

private fun buildThemedObjectCode(
    objectName: String,
    scriptFilePath: String,
    resources: List<ResourceData<ThemedResourceData>>,
    modeName: String,
): String {
    val (primary, subtle) = partition(resources)
    sharedSlots(primary, subtle)
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
        appendLine("@ExperimentalLemonadeApi")
        appendLine("public object $objectName : LemonadeThemedColors {")
        primary.entries.forEachIndexed { index, (hue, slots) ->
            if (index > 0) appendLine()
            appendLine("    override val ${hue.sanitizedValueName()}: ThemedPrimaryColor =")
            appendLine("        object : ThemedPrimaryColor {")
            slots.forEach { resource ->
                appendLine("            override val ${resource.name} = LemonadePrimitiveColors.${resource.value.valueGroup}.${resource.value.valueName}")
            }
            appendLine("            override val subtle: ThemedColor =")
            appendLine("                object : ThemedColor {")
            subtle.getValue(hue).forEach { resource ->
                appendLine("                    override val ${resource.name} = LemonadePrimitiveColors.${resource.value.valueGroup}.${resource.value.valueName}")
            }
            appendLine("                }")
            appendLine("        }")
        }
        appendLine("}")
    }
}

main()
