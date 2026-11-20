#!/usr/bin/env kotlin

@file:Import("kmp-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    val fixture = File("scripts/testdata/sample.tokens.json")
    check(fixture.isFile, "fixture exists at ${fixture.path}")

    val resources = readFileResourceFile(
        file = fixture,
        resourceMap = { jsonObject -> jsonObject },
    )

    check(resources.size == 5, "hidden tokens are excluded (expected 5, got ${resources.size})")

    val names = resources.map { resource ->
        (resource.groups + resource.name).joinToString("/")
    }
    check(
        names == listOf(
            "Base/border0",
            "Base/border50",
            "Base/border100",
            "State/borderSelected",
            "Tint/brand",
        ),
        "canonical order is group-then-natural-numeric, got $names",
    )

    val selected = resources.first { it.name == "borderSelected" }
    check(selected.value.getInt("resolvedValue") == 2, "local DTCG reference resolves to its target value")
    check(
        selected.value.getString("aliasName") == "base/border-50",
        "local reference exposes aliasName as a slash path",
    )
    check(
        selected.value.getString("alias") == "VariableID:1:2",
        "local reference exposes the target's variable id as alias",
    )

    val brand = resources.first { it.name == "brand" }
    val colour = brand.value.getJSONObject("resolvedValue")
    check(colour.getDouble("r") == 0.5, "colour red component comes from components[0]")
    check(colour.getDouble("g") == 0.25, "colour green component comes from components[1]")
    check(colour.getDouble("b") == 0.125, "colour blue component comes from components[2]")
    check(colour.getDouble("a") == 0.8, "colour alpha comes from \$value.alpha")
    check(brand.value.getString("aliasName") == "purple/500", "remote alias name comes from aliasData")
    check(
        brand.value.getString("alias") == "VariableID:9:9",
        "remote alias id comes from aliasData",
    )

    val withHidden = readFileResourceFile(
        file = fixture,
        includeHidden = true,
        resourceMap = { jsonObject -> jsonObject },
    )
    check(
        withHidden.map { it.name } == listOf("border0", "border50", "border100", "borderSelected", "hiddenOne", "brand"),
        "includeHidden keeps hidden tokens in canonical order, got ${withHidden.map { it.name }}",
    )

    val modes = availableModeNames(listOf(fixture))
    check(modes == listOf("Default"), "mode name is read from the file-level extensions, got $modes")

    println("ALL PASSED")
}

main()
