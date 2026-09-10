package com.teya.lemonade

import androidx.compose.foundation.Indication
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.LemonadeTypography

@InternalLemonadeApi
public val LocalColors: ProvidableCompositionLocal<LemonadeSemanticColors> =
    staticCompositionLocalOf {
        error("No default colors set. Wrap your content in LemonadeTheme.")
    }

@InternalLemonadeApi
public val LocalThemedColors: ProvidableCompositionLocal<LemonadeThemedColors> =
    staticCompositionLocalOf {
        error("No default themed colors set. Wrap your content in LemonadeTheme.")
    }

@InternalLemonadeApi
public val LocalTypographies: ProvidableCompositionLocal<LemonadeTypographyProvider> =
    staticCompositionLocalOf {
        LemonadeTypographyProvider()
    }

@InternalLemonadeApi
public val LocalContentColors: ProvidableCompositionLocal<Color> = staticCompositionLocalOf {
    error("No default Content Colors set in the LocalContentColors for theme")
}

@InternalLemonadeApi
public val LocalTextStyles: ProvidableCompositionLocal<LemonadeTextStyle> =
    staticCompositionLocalOf {
        LemonadeTypography.BodyMediumRegular.style
    }

@InternalLemonadeApi
public val LocalRadius: ProvidableCompositionLocal<LemonadeRadiusValues> =
    staticCompositionLocalOf {
        InternalLemonadeRadiusValues()
    }

@InternalLemonadeApi
public val LocalShapes: ProvidableCompositionLocal<LemonadeShapes> = staticCompositionLocalOf {
    InternalLemonadeShapes()
}

@InternalLemonadeApi
public val LocalSpaces: ProvidableCompositionLocal<LemonadeSpaceValues> =
    staticCompositionLocalOf {
        InternalLemonadeSpaceValues()
    }

@InternalLemonadeApi
public val LocalOpacities: ProvidableCompositionLocal<LemonadeOpacity> =
    staticCompositionLocalOf {
        InternalLemonadeOpacityTokens()
    }

@InternalLemonadeApi
public val LocalBorderWidths: ProvidableCompositionLocal<LemonadeBorderWidth> =
    staticCompositionLocalOf {
        InternalLemonadeBorderWidth()
    }

@InternalLemonadeApi
public val LocalSizes: ProvidableCompositionLocal<LemonadeSizeValues> = staticCompositionLocalOf {
    InternalLemonadeSizeValues()
}

@InternalLemonadeApi
public val LocalEffects: ProvidableCompositionLocal<LemonadeEffects> = staticCompositionLocalOf {
    object : LemonadeEffects {
        override val interactionIndication: Indication? = null
    }
}
