#!/usr/bin/env kotlin

@file:Import("web-token-commons.main.kts")

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    // --- leafOf ---------------------------------------------------------
    check(leafOf("Content/Brand/content-brand") == "content-brand", "leafOf takes the last segment")
    check(leafOf("spacing-100") == "spacing-100", "leafOf tolerates a bare name")

    // --- cssVar: colours keep their full leaf --------------------------
    check(
        cssVar("color", "content-primary") == "--lmnd-color-content-primary",
        "colour tokens are namespaced without stripping",
    )
    check(
        cssVar("color", "bg-default") == "--lmnd-color-bg-default",
        "background colour keeps its bg- prefix",
    )

    // --- cssVar: scalars strip their repeated category ------------------
    check(cssVar("spacing", "spacing-100", strip = "spacing") == "--lmnd-spacing-100", "spacing strips")
    check(cssVar("radius", "radius-200", strip = "radius") == "--lmnd-radius-200", "radius strips")
    check(cssVar("size", "size-400", strip = "size") == "--lmnd-size-400", "size strips")
    check(
        cssVar("opacity", "opacity-disabled", strip = "opacity") == "--lmnd-opacity-disabled",
        "opacity strips",
    )

    // --- cssVar: the border-selected collision (spec §5) ----------------
    // `border-selected` exists twice: a border WIDTH and a semantic COLOUR.
    check(
        cssVar("border-width", "border-25", strip = "border") == "--lmnd-border-width-25",
        "border width strips the shorter 'border' prefix, not the category name",
    )
    check(
        cssVar("border-width", "border-selected", strip = "border") == "--lmnd-border-width-selected",
        "the border-width half of the collision",
    )
    check(
        cssVar("color", "border-selected") == "--lmnd-color-border-selected",
        "the colour half of the collision — the two must not collapse",
    )
    check(
        cssVar("border-width", "focus-ring", strip = "border") == "--lmnd-border-width-focus-ring",
        "a leaf that does not carry the prefix is left intact",
    )

    // --- cssVar: typography --------------------------------------------
    check(
        cssVar("font-size", "font-size-400", strip = "font-size") == "--lmnd-font-size-400",
        "font size strips",
    )
    check(cssVar("font-family", "base") == "--lmnd-font-family-base", "font family keeps its leaf")
    check(cssVar("font-weight", "semibold") == "--lmnd-font-weight-semibold", "font weight keeps its leaf")

    // --- remValue -------------------------------------------------------
    check(remValue(0.0) == "0", "zero is emitted bare, with no unit")
    check(remValue(8.0) == "0.5rem", "8px is half a rem")
    check(remValue(4.0) == "0.25rem", "4px is a quarter rem")
    check(remValue(16.0) == "1rem", "16px is one rem, with no trailing zeros")
    check(remValue(2.0) == "0.125rem", "2px survives without float noise")

    // --- pxValue --------------------------------------------------------
    check(pxValue(0.0) == "0", "zero is emitted bare")
    check(pxValue(1.0) == "1px", "integers do not gain a decimal point")
    check(pxValue(0.5) == "0.5px", "sub-pixel shadow offsets are preserved")

    // --- rgbValue -------------------------------------------------------
    check(
        rgbValue(1.0, 1.0, 1.0, 1.0) == "rgb(255 255 255)",
        "fully opaque colours omit the alpha channel entirely",
    )
    // content-primary: the hex says #090806 but alpha is 0.925 — spec §5.
    check(
        rgbValue(0.03529411926865578, 0.0313725508749485, 0.0235294122248888, 0.925000011920929)
            == "rgb(9 8 6 / 0.925)",
        "components round to 0-255 and float noise is trimmed off the alpha",
    )
    check(
        rgbValue(0.0, 0.0, 0.0, 0.05000000074505806) == "rgb(0 0 0 / 0.05)",
        "the light-theme shadow alpha survives rounding",
    )

    println("All web token commons checks passed")
}

main()
