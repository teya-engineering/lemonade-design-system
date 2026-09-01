#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    val fixture = File("scripts/testdata/sample.tokens.json")
    check(fixture.isFile, "fixture exists at ${fixture.path}")

    val rawNames = mutableListOf<String>()
    val rawValues = mutableMapOf<String, JSONObject>()
    readFileResourceFileRaw(fixture) { name, value ->
        rawNames.add(name)
        rawValues[name] = value
    }

    check(rawNames.size == 5, "hidden tokens are excluded (expected 5, got ${rawNames.size})")

    check(
        rawNames == listOf(
            "base/border-0",
            "base/border-50",
            "base/border-100",
            "state/border-selected",
            "tint/brand",
        ),
        "raw slash paths are preserved in canonical order, got $rawNames",
    )

    val selected = rawValues.getValue("state/border-selected")
    check(
        selected.getInt("resolvedValue") == 2,
        "local DTCG reference resolves to its target value",
    )

    val tint = rawValues.getValue("tint/brand").getJSONObject("resolvedValue")
    check(tint.has("r") && tint.has("a"), "colour tokens expose r/g/b/a components")

    println("All web loader checks passed")
}

main()
