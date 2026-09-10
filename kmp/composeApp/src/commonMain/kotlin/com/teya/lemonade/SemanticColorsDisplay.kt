package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

/** Where contrast against black overtakes contrast against white. */
private const val DARK_LABEL_LUMINANCE_THRESHOLD = 0.179f

private typealias TokenSubgroups = Map<String?, Map<String, Color>>

private data class SemanticGroup(
    val title: String,
    val subgroups: List<ColorSwatchGroup>,
)

@Composable
internal fun SemanticColorsDisplay() {
    val colors = LemonadeTheme.colors
    val groups = remember(colors) { semanticGroups(colors = colors) }

    SampleScreenDisplayLazyColumn(
        title = "Semantic Colors",
        background = colors.background.bgDefault,
    ) {
        groups.forEach { group ->
            item(key = group.title) {
                LemonadeUi.Text(
                    text = group.title,
                    textStyle = LemonadeTheme.typography.headingSmall,
                    modifier = Modifier.padding(
                        start = LemonadeTheme.spaces.spacing100,
                        top = LemonadeTheme.spaces.spacing600,
                    ),
                )
            }
            items(
                items = group.subgroups,
                key = { subgroup -> subgroup.id },
            ) { subgroup ->
                ColorSwatchSection(
                    group = subgroup,
                    outlined = true,
                )
            }
        }
    }
}

private fun semanticGroups(colors: LemonadeSemanticColors): List<SemanticGroup> =
    listOf(
        "Background" to backgroundTokens(colors = colors.background),
        "Border" to borderTokens(colors = colors.border),
        "Content" to contentTokens(colors = colors.content),
        "Interaction" to interactionTokens(colors = colors.interaction),
        "Scoped" to scopedTokens(colors = colors.scoped),
        "Shadow" to shadowTokens(colors = colors.shadow),
    ).map { (title, subgroups) ->
        SemanticGroup(
            title = title,
            subgroups = subgroups.map { (subgroupTitle, tokens) ->
                val path = subgroupTitle
                    ?.lowercase()
                    ?: title.lowercase()
                ColorSwatchGroup(
                    id = "$title/$subgroupTitle",
                    title = subgroupTitle,
                    swatches = tokens.map { (name, color) ->
                        ColorSwatch(
                            path = path,
                            name = name,
                            fill = color,
                            label = labelColor(
                                fill = color,
                                colors = colors,
                            ),
                        )
                    },
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

private fun backgroundTokens(colors: LemonadeSemanticColors.BackgroundColors): TokenSubgroups =
    mapOf(
        null to mapOf(
            "bgDefault" to colors.bgDefault,
            "bgSubtle" to colors.bgSubtle,
            "bgElevated" to colors.bgElevated,
            "bgElevatedHigh" to colors.bgElevatedHigh,
        ),
        "Brand" to mapOf(
            "bgBrand" to colors.bgBrand,
            "bgBrandElevated" to colors.bgBrandElevated,
            "bgBrandSubtle" to colors.bgBrandSubtle,
            "bgBrandHigh" to colors.bgBrandHigh,
        ),
        "Voice" to mapOf(
            "bgCritical" to colors.bgCritical,
            "bgCaution" to colors.bgCaution,
            "bgInfo" to colors.bgInfo,
            "bgPositive" to colors.bgPositive,
            "bgFeatured" to colors.bgFeatured,
            "bgNeutral" to colors.bgNeutral,
            "bgCriticalSubtle" to colors.bgCriticalSubtle,
            "bgCautionSubtle" to colors.bgCautionSubtle,
            "bgInfoSubtle" to colors.bgInfoSubtle,
            "bgPositiveSubtle" to colors.bgPositiveSubtle,
            "bgFeaturedSubtle" to colors.bgFeaturedSubtle,
            "bgNeutralSubtle" to colors.bgNeutralSubtle,
        ),
        "Inverse" to mapOf(
            "bgDefaultInverse" to colors.bgDefaultInverse,
            "bgSubtleInverse" to colors.bgSubtleInverse,
            "bgElevatedInverse" to colors.bgElevatedInverse,
        ),
        "Fixed" to mapOf(
            "bgAlwaysDark" to colors.bgAlwaysDark,
            "bgAlwaysDarkHigh" to colors.bgAlwaysDarkHigh,
            "bgAlwaysDarkMedium" to colors.bgAlwaysDarkMedium,
            "bgAlwaysDarkLow" to colors.bgAlwaysDarkLow,
            "bgAlwaysLight" to colors.bgAlwaysLight,
            "bgAlwaysLightHigh" to colors.bgAlwaysLightHigh,
            "bgAlwaysLightMedium" to colors.bgAlwaysLightMedium,
            "bgAlwaysLightLow" to colors.bgAlwaysLightLow,
        ),
    )

private fun borderTokens(colors: LemonadeSemanticColors.BorderColors): TokenSubgroups =
    mapOf(
        null to mapOf(
            "borderNeutralLow" to colors.borderNeutralLow,
            "borderNeutralMedium" to colors.borderNeutralMedium,
            "borderNeutralHigh" to colors.borderNeutralHigh,
            "borderSelected" to colors.borderSelected,
        ),
        "Brand" to mapOf(
            "borderBrand" to colors.borderBrand,
            "borderOnBrandLow" to colors.borderOnBrandLow,
            "borderOnBrandMedium" to colors.borderOnBrandMedium,
            "borderOnBrandHigh" to colors.borderOnBrandHigh,
        ),
        "Voice" to mapOf(
            "borderCritical" to colors.borderCritical,
            "borderCaution" to colors.borderCaution,
            "borderInfo" to colors.borderInfo,
            "borderPositive" to colors.borderPositive,
            "borderFeatured" to colors.borderFeatured,
            "borderCriticalSubtle" to colors.borderCriticalSubtle,
            "borderCautionSubtle" to colors.borderCautionSubtle,
            "borderInfoSubtle" to colors.borderInfoSubtle,
            "borderPositiveSubtle" to colors.borderPositiveSubtle,
            "borderFeaturedSubtle" to colors.borderFeaturedSubtle,
        ),
        "Inverse" to mapOf(
            "borderNeutralLowInverse" to colors.borderNeutralLowInverse,
            "borderNeutralMediumInverse" to colors.borderNeutralMediumInverse,
            "borderNeutralHighInverse" to colors.borderNeutralHighInverse,
            "borderSelectedInverse" to colors.borderSelectedInverse,
            "borderBrandInverse" to colors.borderBrandInverse,
        ),
        "Fixed" to mapOf(
            "borderAlwaysLight" to colors.borderAlwaysLight,
            "borderAlwaysLightLow" to colors.borderAlwaysLightLow,
            "borderAlwaysLightMedium" to colors.borderAlwaysLightMedium,
            "borderAlwaysLightHigh" to colors.borderAlwaysLightHigh,
            "borderAlwaysDark" to colors.borderAlwaysDark,
            "borderAlwaysDarkLow" to colors.borderAlwaysDarkLow,
            "borderAlwaysDarkMedium" to colors.borderAlwaysDarkMedium,
            "borderAlwaysDarkHigh" to colors.borderAlwaysDarkHigh,
        ),
    )

private fun contentTokens(colors: LemonadeSemanticColors.ContentColors): TokenSubgroups =
    mapOf(
        null to mapOf(
            "contentPrimary" to colors.contentPrimary,
            "contentSecondary" to colors.contentSecondary,
            "contentTertiary" to colors.contentTertiary,
        ),
        "Brand" to mapOf(
            "contentBrand" to colors.contentBrand,
            "contentBrandHigh" to colors.contentBrandHigh,
            "contentOnBrandHigh" to colors.contentOnBrandHigh,
            "contentOnBrandLow" to colors.contentOnBrandLow,
        ),
        "Voice" to mapOf(
            "contentCritical" to colors.contentCritical,
            "contentCaution" to colors.contentCaution,
            "contentInfo" to colors.contentInfo,
            "contentPositive" to colors.contentPositive,
            "contentFeatured" to colors.contentFeatured,
            "contentNeutral" to colors.contentNeutral,
        ),
        "Voice / On Color" to mapOf(
            "contentCriticalOnColor" to colors.contentCriticalOnColor,
            "contentCautionOnColor" to colors.contentCautionOnColor,
            "contentInfoOnColor" to colors.contentInfoOnColor,
            "contentPositiveOnColor" to colors.contentPositiveOnColor,
            "contentFeaturedOnColor" to colors.contentFeaturedOnColor,
            "contentNeutralOnColor" to colors.contentNeutralOnColor,
        ),
        "Inverse" to mapOf(
            "contentPrimaryInverse" to colors.contentPrimaryInverse,
            "contentSecondaryInverse" to colors.contentSecondaryInverse,
            "contentTertiaryInverse" to colors.contentTertiaryInverse,
            "contentBrandInverse" to colors.contentBrandInverse,
        ),
        "Fixed" to mapOf(
            "contentAlwaysLight" to colors.contentAlwaysLight,
            "contentAlwaysDark" to colors.contentAlwaysDark,
            "contentCriticalAlwaysOnColor" to colors.contentCriticalAlwaysOnColor,
            "contentCautionAlwaysOnColor" to colors.contentCautionAlwaysOnColor,
            "contentInfoAlwaysOnColor" to colors.contentInfoAlwaysOnColor,
            "contentPositiveAlwaysOnColor" to colors.contentPositiveAlwaysOnColor,
            "contentNeutralAlwaysOnColor" to colors.contentNeutralAlwaysOnColor,
        ),
    )

private fun interactionTokens(colors: LemonadeSemanticColors.InteractionColors): TokenSubgroups =
    mapOf(
        "Interactive / Background" to mapOf(
            "bgDefaultInteractive" to colors.bgDefaultInteractive,
            "bgSubtleInteractive" to colors.bgSubtleInteractive,
            "bgElevatedInteractive" to colors.bgElevatedInteractive,
            "bgElevatedHighInteractive" to colors.bgElevatedHighInteractive,
            "bgBrandInteractive" to colors.bgBrandInteractive,
            "bgBrandElevatedInteractive" to colors.bgBrandElevatedInteractive,
            "bgBrandHighInteractive" to colors.bgBrandHighInteractive,
            "bgCriticalInteractive" to colors.bgCriticalInteractive,
            "bgCautionInteractive" to colors.bgCautionInteractive,
            "bgInfoInteractive" to colors.bgInfoInteractive,
            "bgPositiveInteractive" to colors.bgPositiveInteractive,
            "bgFeaturedInteractive" to colors.bgFeaturedInteractive,
            "bgNeutralInteractive" to colors.bgNeutralInteractive,
            "bgCriticalSubtleInteractive" to colors.bgCriticalSubtleInteractive,
            "bgCautionSubtleInteractive" to colors.bgCautionSubtleInteractive,
            "bgInfoSubtleInteractive" to colors.bgInfoSubtleInteractive,
            "bgPositiveSubtleInteractive" to colors.bgPositiveSubtleInteractive,
            "bgFeaturedSubtleInteractive" to colors.bgFeaturedSubtleInteractive,
            "bgNeutralSubtleInteractive" to colors.bgNeutralSubtleInteractive,
            "bgAlwaysDarkHighInteractive" to colors.bgAlwaysDarkHighInteractive,
            "bgAlwaysDarkMediumInteractive" to colors.bgAlwaysDarkMediumInteractive,
            "bgAlwaysDarkLowInteractive" to colors.bgAlwaysDarkLowInteractive,
            "bgAlwaysLightHighInteractive" to colors.bgAlwaysLightHighInteractive,
            "bgAlwaysLightMediumInteractive" to colors.bgAlwaysLightMediumInteractive,
            "bgAlwaysLightLowInteractive" to colors.bgAlwaysLightLowInteractive,
        ),
        "Pressed / Background" to mapOf(
            "bgDefaultPressed" to colors.bgDefaultPressed,
            "bgSubtlePressed" to colors.bgSubtlePressed,
            "bgElevatedPressed" to colors.bgElevatedPressed,
            "bgBrandPressed" to colors.bgBrandPressed,
            "bgBrandElevatedPressed" to colors.bgBrandElevatedPressed,
            "bgBrandHighPressed" to colors.bgBrandHighPressed,
            "bgCriticalPressed" to colors.bgCriticalPressed,
            "bgCautionPressed" to colors.bgCautionPressed,
            "bgInfoPressed" to colors.bgInfoPressed,
            "bgPositivePressed" to colors.bgPositivePressed,
            "bgFeaturedPressed" to colors.bgFeaturedPressed,
            "bgNeutralPressed" to colors.bgNeutralPressed,
            "bgCriticalSubtlePressed" to colors.bgCriticalSubtlePressed,
            "bgCautionSubtlePressed" to colors.bgCautionSubtlePressed,
            "bgInfoSubtlePressed" to colors.bgInfoSubtlePressed,
            "bgPositiveSubtlePressed" to colors.bgPositiveSubtlePressed,
            "bgFeaturedSubtlePressed" to colors.bgFeaturedSubtlePressed,
            "bgNeutralSubtlePressed" to colors.bgNeutralSubtlePressed,
        ),
    )

private fun scopedTokens(colors: LemonadeSemanticColors.ScopedColors): TokenSubgroups =
    mapOf(
        "Settlements" to mapOf(
            "bgSettlementInstant" to colors.bgSettlementInstant,
            "bgSettlementBusinessDays" to colors.bgSettlementBusinessDays,
            "bgSettlementEveryday" to colors.bgSettlementEveryday,
            "bgSettlementScheduled" to colors.bgSettlementScheduled,
            "contentOnSettlementInstant" to colors.contentOnSettlementInstant,
            "contentOnSettlementBusinessDays" to colors.contentOnSettlementBusinessDays,
            "contentOnSettlementEveryday" to colors.contentOnSettlementEveryday,
            "contentOnSettlementScheduled" to colors.contentOnSettlementScheduled,
        ),
    )

private fun shadowTokens(colors: LemonadeSemanticColors.ShadowColors): TokenSubgroups =
    mapOf(
        null to mapOf(
            "shadowDefault" to colors.shadowDefault,
        ),
    )
