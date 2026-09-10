package com.teya.lemonade

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

/** Where contrast against black overtakes contrast against white. */
private const val DARK_LABEL_LUMINANCE_THRESHOLD = 0.179f

@Composable
internal fun SemanticColorsDisplay() {
    val colors = LemonadeTheme.colors
    val groups = remember(colors) { semanticGroups(colors = colors) }

    SampleScreenDisplayLazyColumn(
        title = "Semantic Colors",
        background = colors.background.bgDefault,
    ) {
        items(
            items = groups,
            key = { group -> group.title },
        ) { group ->
            ColorSwatchSection(
                group = group,
                outlined = true,
            )
        }
    }
}

private fun semanticGroups(colors: LemonadeSemanticColors): List<ColorSwatchGroup> =
    listOf(
        "Background" to backgroundTokens(colors = colors.background),
        "Border" to borderTokens(colors = colors.border),
        "Content" to contentTokens(colors = colors.content),
        "Interaction" to interactionTokens(colors = colors.interaction),
        "Scoped" to scopedTokens(colors = colors.scoped),
        "Shadow" to shadowTokens(colors = colors.shadow),
    ).map { (title, tokens) ->
        ColorSwatchGroup(
            title = title,
            swatches = tokens.map { (name, color) ->
                ColorSwatch(
                    path = title.lowercase(),
                    name = name,
                    fill = color,
                    label = labelColor(
                        fill = color,
                        colors = colors,
                    ),
                )
            },
        )
    }

/**
 * The always-dark or always-light content token, whichever contrasts more with the swatch.
 * Translucent tokens are drawn over the page, so contrast is measured on the composite.
 */
private fun labelColor(
    fill: Color,
    colors: LemonadeSemanticColors,
): Color {
    val luminance = fill
        .compositeOver(colors.background.bgDefault)
        .luminance()
    return if (luminance > DARK_LABEL_LUMINANCE_THRESHOLD) {
        colors.content.contentAlwaysDark
    } else {
        colors.content.contentAlwaysLight
    }
}

private fun backgroundTokens(colors: LemonadeSemanticColors.BackgroundColors): List<Pair<String, Color>> =
    listOf(
        "bgBrand" to colors.bgBrand,
        "bgBrandElevated" to colors.bgBrandElevated,
        "bgBrandHigh" to colors.bgBrandHigh,
        "bgBrandSubtle" to colors.bgBrandSubtle,
        "bgAlwaysDark" to colors.bgAlwaysDark,
        "bgAlwaysDarkHigh" to colors.bgAlwaysDarkHigh,
        "bgAlwaysDarkLow" to colors.bgAlwaysDarkLow,
        "bgAlwaysDarkMedium" to colors.bgAlwaysDarkMedium,
        "bgAlwaysLight" to colors.bgAlwaysLight,
        "bgAlwaysLightHigh" to colors.bgAlwaysLightHigh,
        "bgAlwaysLightLow" to colors.bgAlwaysLightLow,
        "bgAlwaysLightMedium" to colors.bgAlwaysLightMedium,
        "bgDefaultInverse" to colors.bgDefaultInverse,
        "bgElevatedInverse" to colors.bgElevatedInverse,
        "bgSubtleInverse" to colors.bgSubtleInverse,
        "bgCaution" to colors.bgCaution,
        "bgCautionSubtle" to colors.bgCautionSubtle,
        "bgCritical" to colors.bgCritical,
        "bgCriticalSubtle" to colors.bgCriticalSubtle,
        "bgFeatured" to colors.bgFeatured,
        "bgFeaturedSubtle" to colors.bgFeaturedSubtle,
        "bgInfo" to colors.bgInfo,
        "bgInfoSubtle" to colors.bgInfoSubtle,
        "bgNeutral" to colors.bgNeutral,
        "bgNeutralSubtle" to colors.bgNeutralSubtle,
        "bgPositive" to colors.bgPositive,
        "bgPositiveSubtle" to colors.bgPositiveSubtle,
        "bgDefault" to colors.bgDefault,
        "bgElevated" to colors.bgElevated,
        "bgElevatedHigh" to colors.bgElevatedHigh,
        "bgSubtle" to colors.bgSubtle,
    )

private fun borderTokens(colors: LemonadeSemanticColors.BorderColors): List<Pair<String, Color>> =
    listOf(
        "borderBrand" to colors.borderBrand,
        "borderOnBrandHigh" to colors.borderOnBrandHigh,
        "borderOnBrandLow" to colors.borderOnBrandLow,
        "borderOnBrandMedium" to colors.borderOnBrandMedium,
        "borderAlwaysDark" to colors.borderAlwaysDark,
        "borderAlwaysDarkHigh" to colors.borderAlwaysDarkHigh,
        "borderAlwaysDarkLow" to colors.borderAlwaysDarkLow,
        "borderAlwaysDarkMedium" to colors.borderAlwaysDarkMedium,
        "borderAlwaysLight" to colors.borderAlwaysLight,
        "borderAlwaysLightHigh" to colors.borderAlwaysLightHigh,
        "borderAlwaysLightLow" to colors.borderAlwaysLightLow,
        "borderAlwaysLightMedium" to colors.borderAlwaysLightMedium,
        "borderBrandInverse" to colors.borderBrandInverse,
        "borderNeutralHighInverse" to colors.borderNeutralHighInverse,
        "borderNeutralLowInverse" to colors.borderNeutralLowInverse,
        "borderNeutralMediumInverse" to colors.borderNeutralMediumInverse,
        "borderSelectedInverse" to colors.borderSelectedInverse,
        "borderCaution" to colors.borderCaution,
        "borderCautionSubtle" to colors.borderCautionSubtle,
        "borderCritical" to colors.borderCritical,
        "borderCriticalSubtle" to colors.borderCriticalSubtle,
        "borderFeatured" to colors.borderFeatured,
        "borderFeaturedSubtle" to colors.borderFeaturedSubtle,
        "borderInfo" to colors.borderInfo,
        "borderInfoSubtle" to colors.borderInfoSubtle,
        "borderPositive" to colors.borderPositive,
        "borderPositiveSubtle" to colors.borderPositiveSubtle,
        "borderNeutralHigh" to colors.borderNeutralHigh,
        "borderNeutralLow" to colors.borderNeutralLow,
        "borderNeutralMedium" to colors.borderNeutralMedium,
        "borderSelected" to colors.borderSelected,
    )

private fun contentTokens(colors: LemonadeSemanticColors.ContentColors): List<Pair<String, Color>> =
    listOf(
        "contentBrand" to colors.contentBrand,
        "contentBrandHigh" to colors.contentBrandHigh,
        "contentOnBrandHigh" to colors.contentOnBrandHigh,
        "contentOnBrandLow" to colors.contentOnBrandLow,
        "contentAlwaysDark" to colors.contentAlwaysDark,
        "contentAlwaysLight" to colors.contentAlwaysLight,
        "contentCautionAlwaysOnColor" to colors.contentCautionAlwaysOnColor,
        "contentCriticalAlwaysOnColor" to colors.contentCriticalAlwaysOnColor,
        "contentInfoAlwaysOnColor" to colors.contentInfoAlwaysOnColor,
        "contentNeutralAlwaysOnColor" to colors.contentNeutralAlwaysOnColor,
        "contentPositiveAlwaysOnColor" to colors.contentPositiveAlwaysOnColor,
        "contentBrandInverse" to colors.contentBrandInverse,
        "contentPrimaryInverse" to colors.contentPrimaryInverse,
        "contentSecondaryInverse" to colors.contentSecondaryInverse,
        "contentTertiaryInverse" to colors.contentTertiaryInverse,
        "contentCautionOnColor" to colors.contentCautionOnColor,
        "contentCriticalOnColor" to colors.contentCriticalOnColor,
        "contentFeaturedOnColor" to colors.contentFeaturedOnColor,
        "contentInfoOnColor" to colors.contentInfoOnColor,
        "contentNeutralOnColor" to colors.contentNeutralOnColor,
        "contentPositiveOnColor" to colors.contentPositiveOnColor,
        "contentCaution" to colors.contentCaution,
        "contentCritical" to colors.contentCritical,
        "contentFeatured" to colors.contentFeatured,
        "contentInfo" to colors.contentInfo,
        "contentNeutral" to colors.contentNeutral,
        "contentPositive" to colors.contentPositive,
        "contentPrimary" to colors.contentPrimary,
        "contentSecondary" to colors.contentSecondary,
        "contentTertiary" to colors.contentTertiary,
    )

private fun interactionTokens(colors: LemonadeSemanticColors.InteractionColors): List<Pair<String, Color>> =
    listOf(
        "bgAlwaysDarkHighInteractive" to colors.bgAlwaysDarkHighInteractive,
        "bgAlwaysDarkLowInteractive" to colors.bgAlwaysDarkLowInteractive,
        "bgAlwaysDarkMediumInteractive" to colors.bgAlwaysDarkMediumInteractive,
        "bgAlwaysLightHighInteractive" to colors.bgAlwaysLightHighInteractive,
        "bgAlwaysLightLowInteractive" to colors.bgAlwaysLightLowInteractive,
        "bgAlwaysLightMediumInteractive" to colors.bgAlwaysLightMediumInteractive,
        "bgBrandElevatedInteractive" to colors.bgBrandElevatedInteractive,
        "bgBrandHighInteractive" to colors.bgBrandHighInteractive,
        "bgBrandInteractive" to colors.bgBrandInteractive,
        "bgCautionInteractive" to colors.bgCautionInteractive,
        "bgCautionSubtleInteractive" to colors.bgCautionSubtleInteractive,
        "bgCriticalInteractive" to colors.bgCriticalInteractive,
        "bgCriticalSubtleInteractive" to colors.bgCriticalSubtleInteractive,
        "bgDefaultInteractive" to colors.bgDefaultInteractive,
        "bgElevatedHighInteractive" to colors.bgElevatedHighInteractive,
        "bgElevatedInteractive" to colors.bgElevatedInteractive,
        "bgFeaturedInteractive" to colors.bgFeaturedInteractive,
        "bgFeaturedSubtleInteractive" to colors.bgFeaturedSubtleInteractive,
        "bgInfoInteractive" to colors.bgInfoInteractive,
        "bgInfoSubtleInteractive" to colors.bgInfoSubtleInteractive,
        "bgNeutralInteractive" to colors.bgNeutralInteractive,
        "bgNeutralSubtleInteractive" to colors.bgNeutralSubtleInteractive,
        "bgPositiveInteractive" to colors.bgPositiveInteractive,
        "bgPositiveSubtleInteractive" to colors.bgPositiveSubtleInteractive,
        "bgSubtleInteractive" to colors.bgSubtleInteractive,
        "bgBrandElevatedPressed" to colors.bgBrandElevatedPressed,
        "bgBrandHighPressed" to colors.bgBrandHighPressed,
        "bgBrandPressed" to colors.bgBrandPressed,
        "bgCautionPressed" to colors.bgCautionPressed,
        "bgCautionSubtlePressed" to colors.bgCautionSubtlePressed,
        "bgCriticalPressed" to colors.bgCriticalPressed,
        "bgCriticalSubtlePressed" to colors.bgCriticalSubtlePressed,
        "bgDefaultPressed" to colors.bgDefaultPressed,
        "bgElevatedPressed" to colors.bgElevatedPressed,
        "bgFeaturedPressed" to colors.bgFeaturedPressed,
        "bgFeaturedSubtlePressed" to colors.bgFeaturedSubtlePressed,
        "bgInfoPressed" to colors.bgInfoPressed,
        "bgInfoSubtlePressed" to colors.bgInfoSubtlePressed,
        "bgNeutralPressed" to colors.bgNeutralPressed,
        "bgNeutralSubtlePressed" to colors.bgNeutralSubtlePressed,
        "bgPositivePressed" to colors.bgPositivePressed,
        "bgPositiveSubtlePressed" to colors.bgPositiveSubtlePressed,
        "bgSubtlePressed" to colors.bgSubtlePressed,
    )

private fun scopedTokens(colors: LemonadeSemanticColors.ScopedColors): List<Pair<String, Color>> =
    listOf(
        "bgSettlementBusinessDays" to colors.bgSettlementBusinessDays,
        "bgSettlementEveryday" to colors.bgSettlementEveryday,
        "bgSettlementInstant" to colors.bgSettlementInstant,
        "bgSettlementScheduled" to colors.bgSettlementScheduled,
        "contentOnSettlementBusinessDays" to colors.contentOnSettlementBusinessDays,
        "contentOnSettlementEveryday" to colors.contentOnSettlementEveryday,
        "contentOnSettlementInstant" to colors.contentOnSettlementInstant,
        "contentOnSettlementScheduled" to colors.contentOnSettlementScheduled,
    )

private fun shadowTokens(colors: LemonadeSemanticColors.ShadowColors): List<Pair<String, Color>> =
    listOf(
        "shadowDefault" to colors.shadowDefault,
    )
