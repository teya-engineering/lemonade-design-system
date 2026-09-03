#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import java.io.File

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

    check(resources.size == 5, "all five tokens load (expected 5, got ${resources.size})")

    val byName = resources.associateBy { resource ->
        (resource.groups + resource.name).joinToString("/")
    }

    check(
        byName.keys.sorted() == listOf(
            "Background/bgBlue",
            "Background/bgBlueSubtle",
            "Border/borderGreenLime",
            "Content/contentOnAmber",
            "Content/contentOnBlue",
        ),
        "token paths flatten to one group plus a camelCase leaf: ${byName.keys.sorted()}",
    )

    check(
        byName.getValue("Background/bgBlue").value == "Solid.Blue.blue600",
        "solid hue alias -> Solid.Blue.blue600 (got ${byName.getValue("Background/bgBlue").value})",
    )
    check(
        byName.getValue("Background/bgBlueSubtle").value == "Alpha.Blue.alpha100",
        "alpha hue alias -> Alpha.Blue.alpha100 (got ${byName.getValue("Background/bgBlueSubtle").value})",
    )
    check(
        byName.getValue("Border/borderGreenLime").value == "Solid.GreenLime.greenLime600",
        "hyphenated hue alias -> Solid.GreenLime.greenLime600 (got ${byName.getValue("Border/borderGreenLime").value})",
    )
    check(
        byName.getValue("Content/contentOnBlue").value == "Solid.White.white950",
        "white label alias -> Solid.White.white950 (got ${byName.getValue("Content/contentOnBlue").value})",
    )
    check(
        byName.getValue("Content/contentOnAmber").value == "Alpha.Neutral.alpha900",
        "ink label alias -> Alpha.Neutral.alpha900 (got ${byName.getValue("Content/contentOnAmber").value})",
    )

    check(dtcgModeName(org.json.JSONObject(fixture.readText())) == "Light", "mode name reads as Light")

    println("PASS")
}

main()
