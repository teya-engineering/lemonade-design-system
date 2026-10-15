#!/usr/bin/env kotlin

// Compares the component vocabularies web declares as TypeScript string unions against
// the Kotlin and Swift enums they mirror, so an entry added on one platform and not the
// others is caught instead of drifting. Flutter's Button drifted to an entirely different
// variant enum precisely because nothing compared them.
//
// Casing differs by language convention — Kotlin `XSmall`, Swift `xSmall`, web `'xSmall'`
// — so entries are compared lowercased. Order is compared too: these vocabularies are
// read as ordered scales in every gallery and doc.

import java.io.File

/** `Primary,` / `case primary` / `'primary' | …` all reduce to the same key. */
fun key(entry: String): String = entry.trim().lowercase()

fun readSource(path: String): String {
    val file = File(path)
    require(file.isFile) { "$path is missing" }
    return file.readText()
}

/**
 * Entries of a Kotlin `public enum class <name> { … }`, ignoring KDoc and comments. An enum
 * may implement an interface — `ThemedHue : ThemedStyle` — so a supertype list is skipped
 * rather than leaving the enum unfound.
 */
fun kotlinEnum(source: String, name: String): List<String> {
    val body = Regex("""public enum class $name\s*(?::[^{]*)?\{(.*?)\n\}""", RegexOption.DOT_MATCHES_ALL)
        .find(source)?.groupValues?.get(1)
        ?: error("enum class $name not found")
    return Regex("""^\s{4}([A-Z]\w*)\s*,""", RegexOption.MULTILINE).findAll(body).map { it.groupValues[1] }.toList()
}

/**
 * Entries of a Swift `public enum <name> { … }`. A case whose name is a Swift keyword is
 * written in backticks — `case \`default\`` — so they are stripped rather than letting the
 * entry go unread. A conformance list, as on `ThemedHue: CaseIterable`, is skipped the way
 * [kotlinEnum] skips a supertype.
 */
fun swiftEnum(source: String, name: String): List<String> {
    val body = Regex("""public enum $name\s*(?::[^{]*)?\{(.*?)\n\}""", RegexOption.DOT_MATCHES_ALL)
        .find(source)?.groupValues?.get(1)
        ?: error("enum $name not found")
    return Regex("""^\s*case `?(\w+)`?""", RegexOption.MULTILINE).findAll(body).map { it.groupValues[1] }.toList()
}

/** Members of a TypeScript `export type <name> = 'a' | 'b'`, across line breaks. */
fun typeScriptUnion(source: String, name: String): List<String> {
    val declaration = Regex("""export type $name =([^\n]*(?:\n\s{2}\|[^\n]*)*)""")
        .find(source)?.groupValues?.get(1)
        ?: error("type $name not found")
    return Regex("""'([^']+)'""").findAll(declaration).map { it.groupValues[1] }.toList()
}

/**
 * One vocabulary, and where each platform declares it. The names differ: KMP has a single
 * LemonadeAssetSize shared by Icon, CountryFlag and Spinner, while SwiftUI names it per
 * component. Only the entries have to agree.
 */
data class Vocabulary(
    val name: String,
    val kotlinName: String,
    val kotlinPath: String,
    val swiftName: String,
    val swiftPath: String,
    val webName: String,
    val webPath: String,
)

fun main() {
    val vocabularies = listOf(
        Vocabulary(
            name = "LemonadeAssetSize",
            kotlinName = "LemonadeAssetSize",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeAssetSize.kt",
            swiftName = "LemonadeUiIconSize",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeIcon.swift",
            webName = "LemonadeAssetSize",
            webPath = "web/src/asset-size.ts",
        ),
        // Swift keeps Variant and Type on the icon button, which the button shares.
        Vocabulary(
            name = "LemonadeButtonVariant",
            kotlinName = "LemonadeButtonVariant",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Button.kt",
            swiftName = "LemonadeButtonVariant",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift",
            webName = "LemonadeButtonVariant",
            webPath = "web/src/components/button/button.types.ts",
        ),
        // The platforms' fill treatment. Web carries it on `emphasis`, because `type` on a
        // <button> is the DOM's own attribute — the entries still have to agree.
        Vocabulary(
            name = "LemonadeButtonType",
            kotlinName = "LemonadeButtonType",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Button.kt",
            swiftName = "LemonadeButtonType",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift",
            webName = "LemonadeButtonType",
            webPath = "web/src/components/button/button.types.ts",
        ),
        Vocabulary(
            name = "LemonadeButtonSize",
            kotlinName = "LemonadeButtonSize",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Button.kt",
            swiftName = "LemonadeButtonSize",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeButton.swift",
            webName = "LemonadeButtonSize",
            webPath = "web/src/components/button/button.types.ts",
        ),
        Vocabulary(
            name = "LemonadeIconButtonShape",
            kotlinName = "LemonadeIconButtonShape",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeIconButtonShape.kt",
            swiftName = "LemonadeIconButtonShape",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift",
            webName = "LemonadeIconButtonShape",
            webPath = "web/src/components/icon-button/icon-button.types.ts",
        ),
        Vocabulary(
            name = "DividerVariant",
            kotlinName = "DividerVariant",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/DividerVariant.kt",
            swiftName = "DividerVariant",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeDivider.swift",
            webName = "DividerVariant",
            webPath = "web/src/components/divider/divider.types.ts",
        ),
        Vocabulary(
            name = "LemonadeCardPadding",
            kotlinName = "LemonadeCardPadding",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeCard.kt",
            swiftName = "LemonadeCardPadding",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeCard.swift",
            webName = "LemonadeCardPadding",
            webPath = "web/src/components/card/card.types.ts",
        ),
        Vocabulary(
            name = "LemonadeCardBackground",
            kotlinName = "LemonadeCardBackground",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeCard.kt",
            swiftName = "LemonadeCardBackground",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeCard.swift",
            webName = "LemonadeCardBackground",
            webPath = "web/src/components/card/card.types.ts",
        ),
        Vocabulary(
            name = "LemonadeCardHeadingStyle",
            kotlinName = "LemonadeCardHeadingStyle",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeCard.kt",
            swiftName = "LemonadeCardHeadingStyle",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeCard.swift",
            webName = "LemonadeCardHeadingStyle",
            webPath = "web/src/components/card/card.types.ts",
        ),
        Vocabulary(
            name = "TagVoice",
            kotlinName = "TagVoice",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Tag.kt",
            swiftName = "TagVoice",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeTag.swift",
            webName = "TagVoice",
            webPath = "web/src/components/tag/tag.types.ts",
        ),
        Vocabulary(
            name = "SymbolContainerVoice",
            kotlinName = "SymbolContainerVoice",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/SymbolContainer.kt",
            swiftName = "SymbolContainerVoice",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift",
            webName = "SymbolContainerVoice",
            webPath = "web/src/components/symbol-container/symbol-container.types.ts",
        ),
        Vocabulary(
            name = "SymbolContainerSize",
            kotlinName = "SymbolContainerSize",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/SymbolContainer.kt",
            swiftName = "SymbolContainerSize",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift",
            webName = "SymbolContainerSize",
            webPath = "web/src/components/symbol-container/symbol-container.types.ts",
        ),
        Vocabulary(
            name = "SymbolContainerShape",
            kotlinName = "SymbolContainerShape",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/SymbolContainer.kt",
            swiftName = "SymbolContainerShape",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift",
            webName = "SymbolContainerShape",
            webPath = "web/src/components/symbol-container/symbol-container.types.ts",
        ),
        // A token vocabulary rather than a component's: the themed palettes live in
        // kmp/tokens, and SymbolContainer is the first web component to read one.
        Vocabulary(
            name = "ThemedHue",
            kotlinName = "ThemedHue",
            kotlinPath = "kmp/tokens/src/commonMain/kotlin/com/teya/lemonade/LemonadeThemedColors.kt",
            swiftName = "ThemedHue",
            swiftPath = "swiftui/Sources/Lemonade/LemonadeThemedColors.swift",
            webName = "ThemedHue",
            webPath = "web/src/themed-hue.ts",
        ),
        // Spinner takes the same eight sizes. SwiftUI declares its own copy, so it can
        // drift from the icon's on that platform alone, which this catches.
        Vocabulary(
            name = "LemonadeSpinnerSize",
            kotlinName = "LemonadeAssetSize",
            kotlinPath = "kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/LemonadeAssetSize.kt",
            swiftName = "LemonadeSpinnerSize",
            swiftPath = "swiftui/Sources/Lemonade/Components/LemonadeSpinner.swift",
            webName = "LemonadeAssetSize",
            webPath = "web/src/asset-size.ts",
        ),
    )

    val failures = mutableListOf<String>()
    vocabularies.forEach { vocabulary ->
        val name = vocabulary.name
        val kotlin = kotlinEnum(readSource(vocabulary.kotlinPath), vocabulary.kotlinName)
        val swift = swiftEnum(readSource(vocabulary.swiftPath), vocabulary.swiftName)
        val web = typeScriptUnion(readSource(vocabulary.webPath), vocabulary.webName)

        val platforms = mapOf("kmp" to kotlin, "swiftui" to swift, "web" to web)
        // Three empty lists agree with each other. An extractor that stops matching has to
        // say so rather than report a vocabulary that matched on nothing.
        platforms.forEach { (platform, entries) ->
            require(entries.isNotEmpty()) { "$name has no entries on $platform — the extractor stopped matching" }
        }
        val keys = platforms.mapValues { (_, entries) -> entries.map(::key) }
        if (keys.values.distinct().size == 1) {
            println("✓ $name — ${web.size} entries agree across kmp, swiftui and web")
            return@forEach
        }
        failures += buildString {
            appendLine("✗ $name disagrees:")
            platforms.forEach { (platform, entries) -> appendLine("    ${platform.padEnd(8)} ${entries.joinToString(", ")}") }
            val union = keys.values.flatten().distinct()
            union.forEach { entry ->
                val absent = keys.filterValues { entry !in it }.keys
                if (absent.isNotEmpty()) appendLine("    '$entry' is missing from ${absent.joinToString(", ")}")
            }
        }
    }

    if (failures.isEmpty()) {
        println("✓ component vocabularies match across all three platforms")
        return
    }
    System.err.println(failures.joinToString("\n"))
    System.err.println(
        "A vocabulary must match on every platform. Add the entry everywhere, or raise the divergence " +
            "in section 17.5 of .claude/specs/2026-08-21-lemonade-web-support-design.md.",
    )
    kotlin.system.exitProcess(1)
}

main()
