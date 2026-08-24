#!/usr/bin/env kotlin

// Validates that Lemonade's neutral text/surface colour pairs meet WCAG 2.2 AA
// contrast (4.5:1) in both themes.
//
// Scope is deliberately narrow: the three neutral text tokens (content-primary,
// content-secondary, content-tertiary) against the four neutral surfaces
// (bg-default, bg-subtle, bg-elevated, bg-elevated-high), in Light and Dark. That
// is 12 pairs per theme, 24 total.
//
// Borders and on-colour pairs (content-on-brand-high, content-critical-on-color,
// etc.) are OUT of scope for v0. WCAG 1.4.11 exempts purely decorative separators,
// and nothing in the token names distinguishes a decorative divider from a real
// interactive-component boundary — that needs design input, not a guess. Every
// content-* token cross-multiplied against every bg-* and border-* surface produces
// 251 failures, most of them nonsense pairings (e.g. content-critical-on-color,
// designed for a red surface, tested against plain white) — an allowlist that long
// stops being a documented exception and becomes the check switched off.

@file:Import("web-resource-file-loading.main.kts")

import org.json.JSONArray
import java.io.File
import kotlin.math.pow

data class Rgba(val r: Double, val g: Double, val b: Double, val a: Double)

/** Alpha-composites [fg] over [bg], both in 0-1 sRGB. [bg] is assumed opaque. */
fun composite(fg: Rgba, bg: Rgba): Rgba = Rgba(
    r = fg.r * fg.a + bg.r * (1 - fg.a),
    g = fg.g * fg.a + bg.g * (1 - fg.a),
    b = fg.b * fg.a + bg.b * (1 - fg.a),
    a = 1.0,
)

private fun channel(value: Double): Double =
    if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)

/** WCAG relative luminance. */
fun luminance(colour: Rgba): Double =
    0.2126 * channel(colour.r) + 0.7152 * channel(colour.g) + 0.0722 * channel(colour.b)

/** Contrast ratio between two *opaque* colours. Callers must composite translucent inputs first. */
fun contrastRatio(fg: Rgba, bg: Rgba): Double {
    val lighter = maxOf(luminance(fg), luminance(bg))
    val darker = minOf(luminance(fg), luminance(bg))
    return (lighter + 0.05) / (darker + 0.05)
}

/**
 * Resolves a translucent colour to an opaque one by compositing it over [under].
 * Already-opaque colours pass through unchanged.
 *
 * Used twice: to composite `bg-elevated`/`bg-elevated-high` (translucent overlays
 * meant to sit on top of `bg-default`, not standalone surfaces — contrast against a
 * translucent colour is undefined) over `bg-default`, and to composite each
 * translucent `content-*` foreground over its now-opaque surface. Uncorrected
 * surface handling reports nonsense ratios — e.g. content-primary on bg-elevated in
 * dark computes as ~1.00:1, which reads as a catastrophic failure that doesn't exist.
 */
fun resolveOpaque(colour: Rgba, under: Rgba): Rgba =
    if (colour.a < 1.0) composite(colour, under) else colour

val AA_TEXT = 4.5

// Tolerance for float noise between runs (rounding in the Figma export, colour-space
// conversion, etc). A measured ratio more than this far below the recorded one is
// treated as a real regression, not noise.
val RATCHET_TOLERANCE = 0.05

val NEUTRAL_FOREGROUNDS = listOf("content-primary", "content-secondary", "content-tertiary")
val NEUTRAL_SURFACES = listOf("bg-default", "bg-subtle", "bg-elevated", "bg-elevated-high")

/** Parses "4.07:1" -> 4.07. */
fun parseRatio(recorded: String): Double = recorded.substringBefore(":").toDouble()

fun main() {
    val files = tokenFiles("theme-colors.")
    requireModes(files, "Light", "Dark")
    val allowlist = JSONArray(File("web/contrast-allowlist.json").readText())
        .let { array -> (0 until array.length()).map { array.getJSONObject(it) } }
        .associate {
            "${it.getString("theme")}|${it.getString("pair")}" to parseRatio(it.getString("ratio"))
        }

    val failures = mutableListOf<String>()
    val ratios = mutableListOf<String>()

    listOf("Light", "Dark").forEach { theme ->
        val colours = mutableMapOf<String, Rgba>()
        readFileResourceFileByModeRaw(files, theme) { path, resolved ->
            colours[path.substringAfterLast('/')] = Rgba(
                resolved.getDouble("r"), resolved.getDouble("g"),
                resolved.getDouble("b"), resolved.getDouble("a"),
            )
        }

        val base = colours["bg-default"] ?: error("$theme is missing bg-default")

        val surfaces = NEUTRAL_SURFACES.map { name ->
            val raw = colours[name] ?: error("$theme is missing surface token '$name'")
            name to resolveOpaque(raw, base)
        }
        val foregrounds = NEUTRAL_FOREGROUNDS.map { name ->
            name to (colours[name] ?: error("$theme is missing foreground token '$name'"))
        }

        surfaces.forEach { (bgName, bg) ->
            foregrounds.forEach { (fgName, rawFg) ->
                val fg = resolveOpaque(rawFg, bg)
                val ratio = contrastRatio(fg, bg)
                val pair = "$fgName on $bgName"
                val key = "$theme|$pair"
                ratios.add("$theme  %.2f:1  %s".format(ratio, pair))
                val recorded = allowlist[key]
                if (recorded != null) {
                    // Ratchet: an allowlisted pair may not get *worse* than the ratio recorded
                    // the day it was allowlisted. It is free to improve without updating the
                    // entry — only a regression beyond the float-noise tolerance fails.
                    if (ratio < recorded - RATCHET_TOLERANCE) {
                        failures.add(
                            "$theme  %.2f:1  %s  (REGRESSION — allowlist records %.2f:1; ".format(ratio, pair, recorded) +
                                "update the entry in web/contrast-allowlist.json or revert the regression)",
                        )
                    }
                } else if (ratio < AA_TEXT) {
                    failures.add("$theme  %.2f:1  %s  (needs %.1f)".format(ratio, pair, AA_TEXT))
                }
            }
        }
    }

    println("Computed ratios for all ${ratios.size} checked pairs:")
    ratios.sorted().forEach { println("   $it") }
    println()

    if (failures.isNotEmpty()) {
        println("✗ ${failures.size} colour pairs below WCAG 2.2 AA")
        failures.sorted().forEach { println("   $it") }
        println()
        println("   Fix the token in Figma, or add an entry to web/contrast-allowlist.json")
        println("   with a reason and an owner.")
        error("contrast check failed")
    }
    println("✓ All checked colour pairs meet WCAG 2.2 AA in both themes " +
        "(${allowlist.size} known failures allowlisted)")
}

main()
