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

/** Entries of a Kotlin `public enum class <name> { … }`, ignoring KDoc and comments. */
fun kotlinEnum(source: String, name: String): List<String> {
    val body = Regex("""public enum class $name\s*\{(.*?)\n\}""", RegexOption.DOT_MATCHES_ALL)
        .find(source)?.groupValues?.get(1)
        ?: error("enum class $name not found")
    return Regex("""^\s{4}([A-Z]\w*)\s*,""", RegexOption.MULTILINE).findAll(body).map { it.groupValues[1] }.toList()
}

/** Entries of a Swift `public enum <name> { … }`. */
fun swiftEnum(source: String, name: String): List<String> {
    val body = Regex("""public enum $name\s*\{(.*?)\n\}""", RegexOption.DOT_MATCHES_ALL)
        .find(source)?.groupValues?.get(1)
        ?: error("enum $name not found")
    return Regex("""^\s*case (\w+)""", RegexOption.MULTILINE).findAll(body).map { it.groupValues[1] }.toList()
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
            webPath = "web/src/asset-size.ts",
        ),
    )

    val failures = mutableListOf<String>()
    vocabularies.forEach { vocabulary ->
        val name = vocabulary.name
        val kotlin = kotlinEnum(readSource(vocabulary.kotlinPath), vocabulary.kotlinName)
        val swift = swiftEnum(readSource(vocabulary.swiftPath), vocabulary.swiftName)
        val web = typeScriptUnion(readSource(vocabulary.webPath), name)

        val platforms = mapOf("kmp" to kotlin, "swiftui" to swift, "web" to web)
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
