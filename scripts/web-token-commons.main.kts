#!/usr/bin/env kotlin

// Pure formatting helpers for the web token converters.
//
// Deliberately free of file I/O so that web-token-commons-test.main.kts can
// exercise every branch without fixtures.

import kotlin.math.abs
import kotlin.math.roundToInt
import java.util.Locale

/** The last segment of a DTCG slash path. `Content/Brand/content-brand` -> `content-brand`. */
fun leafOf(path: String): String = path.substringAfterLast('/')

/**
 * The CSS custom property for a token.
 *
 * Leaf names in the Figma export are already self-describing (`content-primary`,
 * `spacing-100`), so the scheme is `--lmnd-<category>-<leaf>` with the repeated
 * category word removed. [strip] is passed explicitly rather than derived from
 * [category] because they differ: the border-width collection prefixes its tokens
 * with `border-`, not `border-width-`.
 *
 * The namespacing is what keeps `border-selected` from collapsing: it exists both as
 * a border width and as a semantic colour.
 */
fun cssVar(category: String, leaf: String, strip: String? = null): String {
    val suffix = if (strip != null) leaf.removePrefix("$strip-") else leaf
    return "--lmnd-$category-$suffix"
}

/** Trims float noise and trailing zeros: 0.925000011920929 -> "0.925", 1.0 -> "1". */
fun trimNumber(value: Double): String {
    if (abs(value) < 1e-9) return "0"
    val rounded = String.format(Locale.ROOT, "%.4f", value)
    return rounded.trimEnd('0').trimEnd('.')
}

/**
 * Font sizes, line heights, spacing, sizes and radii, divided by the 16px root.
 *
 * rem rather than px because only rem follows a raised browser default font size —
 * a common low-vision accommodation. See spec §5.
 */
fun remValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px / 16.0) + "rem"
}

/** Border widths and shadow geometry, which are optical constants rather than proportional. */
fun pxValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px) + "px"
}

/**
 * A colour as space-separated `rgb()`.
 *
 * Never hex: the DTCG `hex` field discards alpha, and `content-primary` is 92.5%
 * opaque black. Emitting its hex would silently produce the wrong colour.
 */
fun rgbValue(r: Double, g: Double, b: Double, a: Double): String {
    val red = (r * 255).roundToInt()
    val green = (g * 255).roundToInt()
    val blue = (b * 255).roundToInt()
    val opaque = abs(a - 1.0) < 1e-6
    return if (opaque) {
        "rgb($red $green $blue)"
    } else {
        "rgb($red $green $blue / ${trimNumber(a)})"
    }
}
