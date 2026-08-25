#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

private val EXTENSIONS_KEY = "\$extensions"
private val DESCRIPTION_KEY = "\$description"

private val SCRIPT_PATH = "scripts/docs-token-catalog.main.kts"

private val OUTPUT_DIR =
    File("kmp/docs/src/commonMain/kotlin/com/teya/lemonade/docs/tokens")

/** Scale collections that carry a description and a codeSyntax worth surfacing. */
private val SCALE_FILES = listOf(
    ScaleSpec(file = "spacing.tokens.json", catalog = "Spacing"),
    ScaleSpec(file = "radius.tokens.json", catalog = "Radius"),
    ScaleSpec(file = "size.tokens.json", catalog = "Size"),
    ScaleSpec(file = "opacity.tokens.json", catalog = "Opacity"),
    ScaleSpec(file = "border-width.tokens.json", catalog = "BorderWidth"),
)

data class ScaleSpec(
    val file: String,
    val catalog: String,
)

data class ColorDoc(
    val tokenName: String,
    val group: String,
    val subgroup: String?,
    val description: String?,
    val android: String?,
    val ios: String?,
    val property: String,
)

data class ScaleDoc(
    val tokenName: String,
    val value: Double,
    val description: String?,
    val android: String?,
    val ios: String?,
)

fun main() {
    try {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdirs()
        }

        val themeFiles = tokenFiles("theme-colors")
        requireModes(themeFiles, "Light", "Dark")

        val light = modeTokens(files = themeFiles, modeName = "Light")
        val dark = modeTokens(files = themeFiles, modeName = "Dark")

        require(light.keys == dark.keys) {
            val onlyLight = light.keys - dark.keys
            val onlyDark = dark.keys - light.keys
            "Light and dark theme exports describe different tokens. " +
                "Only in light: ${onlyLight.joinToString().ifEmpty { "none" }}. " +
                "Only in dark: ${onlyDark.joinToString().ifEmpty { "none" }}. " +
                "Regenerating from a partial export leaves one theme stale."
        }

        // kmp-theme-token-converter only emits a semantic colour when the export aliases it to a
        // primitive; a literal value is dropped. Those tokens have no symbol on LemonadeLightTheme,
        // so referencing them here would not compile. The site documents what the design system
        // actually ships, and names the difference rather than hiding it.
        val unaliased = light.keys
            .sortedWith(::canonicalTokenOrder)
            .filter { name -> dtcgResolvedValueObject(light, name).optString("aliasName").isNullOrBlank() }
        if (unaliased.isNotEmpty()) {
            println(
                "! ${unaliased.size} published colour token(s) are literals rather than aliases, so " +
                    "kmp-theme-token-converter does not generate them and the site cannot show them: " +
                    unaliased.joinToString(),
            )
        }

        val colors = (light.keys - unaliased.toSet())
            .sortedWith(::canonicalTokenOrder)
            .map { name ->
                colorDoc(name = name, node = light.getValue(name))
            }
        require(colors.isNotEmpty()) {
            "No published colour tokens found — the export is empty or every token is hidden."
        }
        require(colors.any { doc -> doc.tokenName == "bg-always-light" }) {
            "bg-always-light is missing. The elevation previews sit on it, because the shadow " +
                "tokens are literal low-alpha black with no dark-theme counterpart."
        }
        println("✓ Loaded ${colors.size} colour tokens across ${colors.map { it.group }.distinct().size} groups")

        val scales = SCALE_FILES.associate { spec ->
            spec.catalog to scaleDocs(file = spec.file)
        }
        scales.forEach { (catalog, docs) ->
            println("✓ Loaded ${docs.size} $catalog tokens")
        }

        val code = buildCatalogCode(colors = colors, scales = scales)
        File(OUTPUT_DIR, "TokenCatalog.kt").writeText(code)
        println("✓ Token catalog generated")
    } catch (error: Throwable) {
        println("✗ Failed to generate the docs token catalog: ${error.message}")
        throw error
    }
}

/** Published leaf tokens for one mode, keyed by slash-joined group path. */
private fun modeTokens(files: List<File>, modeName: String): Map<String, JSONObject> {
    files.forEach { file ->
        val json = JSONObject(file.readText())
        if (!dtcgModeName(json).equals(modeName, ignoreCase = true)) return@forEach
        return dtcgTokens(json).filterNot { entry -> isHiddenNode(entry.value) }
    }
    error("No token file provides mode '$modeName'")
}

private fun isHiddenNode(node: JSONObject): Boolean {
    return node.optJSONObject(EXTENSIONS_KEY)?.optBoolean("com.figma.hiddenFromPublishing") ?: false
}

private fun descriptionOf(node: JSONObject): String? {
    return node.optString(DESCRIPTION_KEY).takeIf { text -> text.isNotBlank() }
}

private fun codeSyntaxOf(node: JSONObject, platform: String): String? {
    return node
        .optJSONObject(EXTENSIONS_KEY)
        ?.optJSONObject("com.figma.codeSyntax")
        ?.optString(platform)
        ?.takeIf { symbol -> symbol.isNotBlank() }
}

private fun colorDoc(name: String, node: JSONObject): ColorDoc {
    val groups = name.sanitizedGroups()
    require(groups.isNotEmpty()) {
        "Colour token '$name' sits at the document root; every colour must live under a group."
    }
    val leaf = name.substringAfterLast("/")
    return ColorDoc(
        tokenName = leaf,
        group = groups.first(),
        subgroup = groups.getOrNull(1),
        description = descriptionOf(node),
        android = codeSyntaxOf(node, "ANDROID"),
        ios = codeSyntaxOf(node, "iOS"),
        property = "${groups.first().sanitizedValueName()}.${name.sanitizedValueName()}",
    )
}

private fun scaleDocs(file: String): List<ScaleDoc> {
    val json = JSONObject(tokenFile(file).readText())
    val tokens = dtcgTokens(json).filterNot { entry -> isHiddenNode(entry.value) }
    require(tokens.isNotEmpty()) {
        "$file exports no published tokens."
    }
    return tokens.keys
        .sortedWith(::canonicalTokenOrder)
        .map { name ->
            val resolved = dtcgResolvedValueObject(tokens, name)
            val node = tokens.getValue(name)
            ScaleDoc(
                tokenName = name.substringAfterLast("/"),
                value = resolved.getDouble("resolvedValue"),
                description = descriptionOf(node),
                android = codeSyntaxOf(node, "ANDROID"),
                ios = codeSyntaxOf(node, "iOS"),
            )
        }
        .sortedBy { doc -> doc.value }
}

private fun String.asKotlinString(): String {
    return "\"" + trim().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\""
}

private fun String?.asKotlinNullableString(): String {
    return this?.asKotlinString() ?: "null"
}

private fun assignment(indent: String, property: String, value: String?): String {
    return "$indent$property = ${value.asKotlinNullableString()},"
}

private fun buildCatalogCode(
    colors: List<ColorDoc>,
    scales: Map<String, List<ScaleDoc>>,
): String {
    val groups = colors.map { doc -> doc.group }.distinct()
    return buildString {
        appendLine("@file:Suppress(\"MaxLineLength\", \"ktlint:standard:max-line-length\")")
        appendLine()
        appendLine("package com.teya.lemonade.docs.tokens")
        appendLine()
        appendLine("import androidx.compose.ui.graphics.Color")
        appendLine("import com.teya.lemonade.LemonadeDarkTheme")
        appendLine("import com.teya.lemonade.LemonadeLightTheme")
        appendLine()
        appendLine("/**")
        appendLine(" * Design token reference, read from the Figma export the Kotlin and Swift converters consume.")
        appendLine(" *")
        appendLine(" * Colour values are references to the generated theme objects rather than literals, so a")
        appendLine(" * renamed or unpublished token regenerates into a symbol that does not exist and this file")
        appendLine(" * stops compiling.")
        appendLine(" *")
        append(defaultAutoGenerationMessage(SCRIPT_PATH))
        appendLine(" */")
        appendLine()
        appendLine("internal enum class ColorTokenGroup(val label: String) {")
        groups.forEach { group ->
            appendLine("    $group(label = ${group.asKotlinString()}),")
        }
        appendLine("}")
        appendLine()
        appendLine("internal data class ColorTokenDoc(")
        appendLine("    val name: String,")
        appendLine("    val group: ColorTokenGroup,")
        appendLine("    val subgroup: String?,")
        appendLine("    val description: String?,")
        appendLine("    val androidSymbol: String?,")
        appendLine("    val iosSymbol: String?,")
        appendLine("    val light: Color,")
        appendLine("    val dark: Color,")
        appendLine(") {")
        appendLine("    /** Resolves to the same value in both themes, on purpose. */")
        appendLine("    val fixed: Boolean get() = light == dark")
        appendLine("}")
        appendLine()
        appendLine("internal data class ScaleTokenDoc(")
        appendLine("    val name: String,")
        appendLine("    val value: Float,")
        appendLine("    val description: String?,")
        appendLine("    val androidSymbol: String?,")
        appendLine("    val iosSymbol: String?,")
        appendLine(")")
        appendLine()
        appendLine("internal val LemonadeColorTokenDocs: List<ColorTokenDoc> = listOf(")
        colors.forEach { doc ->
            appendLine("    ColorTokenDoc(")
            appendLine("        name = ${doc.tokenName.asKotlinString()},")
            appendLine("        group = ColorTokenGroup.${doc.group},")
            appendLine("        subgroup = ${doc.subgroup.asKotlinNullableString()},")
            appendLine(assignment(indent = "        ", property = "description", value = doc.description))
            appendLine(assignment(indent = "        ", property = "androidSymbol", value = doc.android))
            appendLine(assignment(indent = "        ", property = "iosSymbol", value = doc.ios))
            appendLine("        light = LemonadeLightTheme.${doc.property},")
            appendLine("        dark = LemonadeDarkTheme.${doc.property},")
            appendLine("    ),")
        }
        appendLine(")")
        scales.forEach { (catalog, docs) ->
            appendLine()
            appendLine("internal val Lemonade${catalog}TokenDocs: List<ScaleTokenDoc> = listOf(")
            docs.forEach { doc ->
                appendLine("    ScaleTokenDoc(")
                appendLine("        name = ${doc.tokenName.asKotlinString()},")
                appendLine("        value = ${doc.value}f,")
                appendLine(assignment(indent = "        ", property = "description", value = doc.description))
                appendLine(assignment(indent = "        ", property = "androidSymbol", value = doc.android))
                appendLine(assignment(indent = "        ", property = "iosSymbol", value = doc.ios))
                appendLine("    ),")
            }
            appendLine(")")
        }
    }
}

main()
