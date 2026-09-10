#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import java.io.File

private val THEMED_GROUP = "Themed"

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    val fixture = File("scripts/testdata/sample-themed.tokens.json")
    check(fixture.isFile, "fixture exists at ${fixture.path}")

    val resources = readFileResourceFile(
        file = fixture,
        resourceMap = { jsonObject ->
            val aliasName = jsonObject.optString("aliasName")
            val groups = aliasName?.sanitizedGroups().orEmpty()
            if (!aliasName.isNullOrBlank() && groups.isNotEmpty()) {
                val valueGroup = if (groups.contains("Alpha")) {
                    "Alpha.${groups.first()}"
                } else {
                    "Solid.${groups.first()}"
                }
                "$valueGroup.${aliasName.sanitizedValueName()}"
            } else {
                null
            }
        },
    ).filterNull()

    check(resources.size == 6, "all six fixture tokens load (expected 6, got ${resources.size})")

    // The Theme export carries both layers. Each converter filters to its own subtree,
    // so the loader must expose the group path that makes that split possible.
    val themed = resources.filter { it.groups.firstOrNull() == THEMED_GROUP }
    val semantic = resources.filterNot { it.groups.firstOrNull() == THEMED_GROUP }
    check(themed.size == 5, "the themed filter keeps only the Themed subtree (expected 5, got ${themed.size})")
    check(semantic.size == 1, "the semantic filter keeps everything else (expected 1, got ${semantic.size})")

    // A themed token's hue is the SECOND path segment, which is what the output groups by.
    val hues = themed.mapNotNull { it.groups.getOrNull(1) }.distinct().sorted()
    check(
        hues == listOf("Amber", "Blue", "GreenLime"),
        "hue is the second group and Pascal-cases hyphenated names: $hues",
    )

    val byPath = themed.associateBy { resource -> (resource.groups + resource.name).joinToString("/") }
    check(
        byPath.keys.sorted() == listOf(
            "Themed/Amber/onBackground",
            "Themed/Blue/background",
            "Themed/Blue/backgroundSubtle",
            "Themed/Blue/onBackground",
            "Themed/GreenLime/border",
        ),
        "token paths flatten to Themed/<Hue>/<camelCaseLeaf>: ${byPath.keys.sorted()}",
    )

    check(
        byPath.getValue("Themed/Blue/background").value == "Solid.Blue.blue600",
        "solid hue alias -> Solid.Blue.blue600 (got ${byPath.getValue("Themed/Blue/background").value})",
    )
    check(
        byPath.getValue("Themed/Blue/backgroundSubtle").value == "Alpha.Blue.alpha100",
        "alpha hue alias -> Alpha.Blue.alpha100 (got ${byPath.getValue("Themed/Blue/backgroundSubtle").value})",
    )
    check(
        byPath.getValue("Themed/GreenLime/border").value == "Solid.GreenLime.greenLime600",
        "hyphenated hue alias -> Solid.GreenLime.greenLime600 (got ${byPath.getValue("Themed/GreenLime/border").value})",
    )
    check(
        byPath.getValue("Themed/Blue/onBackground").value == "Solid.White.white950",
        "white label alias -> Solid.White.white950 (got ${byPath.getValue("Themed/Blue/onBackground").value})",
    )
    check(
        byPath.getValue("Themed/Amber/onBackground").value == "Alpha.Neutral.alpha900",
        "ink label alias -> Alpha.Neutral.alpha900 (got ${byPath.getValue("Themed/Amber/onBackground").value})",
    )

    check(dtcgModeName(org.json.JSONObject(fixture.readText())) == "Light", "mode name reads as Light")

    println("PASS")
}

main()
