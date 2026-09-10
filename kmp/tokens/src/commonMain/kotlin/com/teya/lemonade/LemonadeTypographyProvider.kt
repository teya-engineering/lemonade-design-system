package com.teya.lemonade

import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.LemonadeTypography

/**
 * Typography styles for the Lemonade theme.
 * Subclass to override specific styles while keeping the [LemonadeTypography] defaults.
 */
public open class LemonadeTypographyProvider {
    public open val displayXSmall: LemonadeTextStyle get() = LemonadeTypography.DisplayXSmall.style
    public open val displaySmall: LemonadeTextStyle get() = LemonadeTypography.DisplaySmall.style
    public open val displayMedium: LemonadeTextStyle get() = LemonadeTypography.DisplayMedium.style
    public open val displayLarge: LemonadeTextStyle get() = LemonadeTypography.DisplayLarge.style
    public open val displayXLarge: LemonadeTextStyle get() = LemonadeTypography.DisplayXLarge.style
    public open val display2XLarge: LemonadeTextStyle get() = LemonadeTypography.Display2XLarge.style
    public open val display3XLarge: LemonadeTextStyle get() = LemonadeTypography.Display3XLarge.style

    public open val headingXLarge: LemonadeTextStyle get() = LemonadeTypography.HeadingXLarge.style
    public open val headingLarge: LemonadeTextStyle get() = LemonadeTypography.HeadingLarge.style
    public open val headingMedium: LemonadeTextStyle get() = LemonadeTypography.HeadingMedium.style
    public open val headingSmall: LemonadeTextStyle get() = LemonadeTypography.HeadingSmall.style
    public open val headingXSmall: LemonadeTextStyle get() = LemonadeTypography.HeadingXSmall.style
    public open val headingXXSmall: LemonadeTextStyle get() = LemonadeTypography.HeadingXXSmall.style

    public open val bodyXLargeRegular: LemonadeTextStyle get() = LemonadeTypography.BodyXLargeRegular.style
    public open val bodyXLargeMedium: LemonadeTextStyle get() = LemonadeTypography.BodyXLargeMedium.style
    public open val bodyXLargeSemiBold: LemonadeTextStyle get() = LemonadeTypography.BodyXLargeSemiBold.style

    public open val bodyLargeRegular: LemonadeTextStyle get() = LemonadeTypography.BodyLargeRegular.style
    public open val bodyLargeMedium: LemonadeTextStyle get() = LemonadeTypography.BodyLargeMedium.style
    public open val bodyLargeSemiBold: LemonadeTextStyle get() = LemonadeTypography.BodyLargeSemiBold.style

    public open val bodyMediumRegular: LemonadeTextStyle get() = LemonadeTypography.BodyMediumRegular.style
    public open val bodyMediumMedium: LemonadeTextStyle get() = LemonadeTypography.BodyMediumMedium.style
    public open val bodyMediumSemiBold: LemonadeTextStyle get() = LemonadeTypography.BodyMediumSemiBold.style
    public open val bodyMediumBold: LemonadeTextStyle get() = LemonadeTypography.BodyMediumBold.style

    public open val bodySmallRegular: LemonadeTextStyle get() = LemonadeTypography.BodySmallRegular.style
    public open val bodySmallMedium: LemonadeTextStyle get() = LemonadeTypography.BodySmallMedium.style
    public open val bodySmallSemiBold: LemonadeTextStyle get() = LemonadeTypography.BodySmallSemiBold.style

    public open val bodyXSmallRegular: LemonadeTextStyle get() = LemonadeTypography.BodyXSmallRegular.style
    public open val bodyXSmallMedium: LemonadeTextStyle get() = LemonadeTypography.BodyXSmallMedium.style
    public open val bodyXSmallSemiBold: LemonadeTextStyle get() = LemonadeTypography.BodyXSmallSemiBold.style
    public open val bodyXSmallOverline: LemonadeTextStyle get() = LemonadeTypography.BodyXSmallOverline.style
}
