package com.teya.lemonade.core

/**
 * Font metrics that define a text style.
 *
 * @property fontSize font size in sp (scale-independent pixels)
 * @property lineHeight line height in sp
 * @property fontWeight font weight (400 = Normal, 500 = Medium, 600 = SemiBold, 700 = Bold)
 * @property letterSpacing letter spacing in sp, or null for the font default
 */
public data class LemonadeTextStyle(
    val fontSize: Float,
    val lineHeight: Float,
    val fontWeight: Int,
    val letterSpacing: Float? = null,
)

/**
 * Every text style in the Lemonade Design System, each carrying its default [LemonadeTextStyle].
 */
public enum class LemonadeTypography(public val style: LemonadeTextStyle) {
    DisplayXSmall(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize600.value,
            lineHeight = LemonadeLineHeights.LineHeight800.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    DisplaySmall(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize700.value,
            lineHeight = LemonadeLineHeights.LineHeight900.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    DisplayMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize900.value,
            lineHeight = LemonadeLineHeights.LineHeight1100.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    DisplayLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize1200.value,
            lineHeight = LemonadeLineHeights.LineHeight1400.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    DisplayXLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize1400.value,
            lineHeight = LemonadeLineHeights.LineHeight1600.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    Display2XLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize1600.value,
            lineHeight = LemonadeLineHeights.LineHeight1800.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),
    Display3XLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize1800.value,
            lineHeight = LemonadeLineHeights.LineHeight2000.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = -0.25f,
        ),
    ),

    HeadingXLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize1000.value,
            lineHeight = LemonadeLineHeights.LineHeight1200.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    HeadingLarge(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize800.value,
            lineHeight = LemonadeLineHeights.LineHeight1000.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    HeadingMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize700.value,
            lineHeight = LemonadeLineHeights.LineHeight900.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    HeadingSmall(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize600.value,
            lineHeight = LemonadeLineHeights.LineHeight800.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    HeadingXSmall(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize450.value,
            lineHeight = LemonadeLineHeights.LineHeight650.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    HeadingXXSmall(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize400.value,
            lineHeight = LemonadeLineHeights.LineHeight600.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),

    BodyXLargeRegular(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize500.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Regular.weight,
        ),
    ),
    BodyXLargeMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize500.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Medium.weight,
        ),
    ),
    BodyXLargeSemiBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize500.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),

    BodyLargeRegular(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize450.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Regular.weight,
        ),
    ),
    BodyLargeMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize450.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Medium.weight,
        ),
    ),
    BodyLargeSemiBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize450.value,
            lineHeight = LemonadeLineHeights.LineHeight700.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),

    BodyMediumRegular(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize400.value,
            lineHeight = LemonadeLineHeights.LineHeight600.value,
            fontWeight = LemonadeFontWeights.Regular.weight,
        ),
    ),
    BodyMediumMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize400.value,
            lineHeight = LemonadeLineHeights.LineHeight600.value,
            fontWeight = LemonadeFontWeights.Medium.weight,
        ),
    ),
    BodyMediumSemiBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize400.value,
            lineHeight = LemonadeLineHeights.LineHeight600.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    BodyMediumBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize400.value,
            lineHeight = LemonadeLineHeights.LineHeight600.value,
            fontWeight = LemonadeFontWeights.Bold.weight,
        ),
    ),

    BodySmallRegular(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize350.value,
            lineHeight = LemonadeLineHeights.LineHeight500.value,
            fontWeight = LemonadeFontWeights.Regular.weight,
        ),
    ),
    BodySmallMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize350.value,
            lineHeight = LemonadeLineHeights.LineHeight500.value,
            fontWeight = LemonadeFontWeights.Medium.weight,
        ),
    ),
    BodySmallSemiBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize350.value,
            lineHeight = LemonadeLineHeights.LineHeight500.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),

    BodyXSmallRegular(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize300.value,
            lineHeight = LemonadeLineHeights.LineHeight400.value,
            fontWeight = LemonadeFontWeights.Regular.weight,
        ),
    ),
    BodyXSmallMedium(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize300.value,
            lineHeight = LemonadeLineHeights.LineHeight400.value,
            fontWeight = LemonadeFontWeights.Medium.weight,
        ),
    ),
    BodyXSmallSemiBold(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize300.value,
            lineHeight = LemonadeLineHeights.LineHeight400.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
        ),
    ),
    BodyXSmallOverline(
        style = LemonadeTextStyle(
            fontSize = LemonadeFontSizes.FontSize300.value,
            lineHeight = LemonadeLineHeights.LineHeight400.value,
            fontWeight = LemonadeFontWeights.Semibold.weight,
            letterSpacing = 1.5f,
        ),
    ),
}
