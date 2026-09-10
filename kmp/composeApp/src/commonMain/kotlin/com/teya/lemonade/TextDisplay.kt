package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.teya.lemonade.core.LemonadeTypography

private data class TypographyCategory(
    val title: String,
    val styles: List<LemonadeTypography>,
)

private val categorizedStyles: List<TypographyCategory> = LemonadeTypography.entries
    .groupBy { typography -> typography.category() }
    .map { (category, styles) ->
        TypographyCategory(
            title = category,
            styles = styles.sortedByDescending { typography -> typography.style.fontSize },
        )
    }

@Composable
internal fun TextDisplay() {
    SampleScreenDisplayLazyColumn(title = "Text") {
        items(
            items = categorizedStyles,
            key = { category -> category.title },
        ) { category ->
            TextSection(title = category.title) {
                TypographyCategoryColumn(styles = category.styles)
            }
        }

        // These colours are not in the typography enum, so the section is written out by hand.
        item(key = "Colors") {
            TextSection(title = "Colors") {
                TextSectionColumn {
                    LemonadeUi.Text(
                        text = "Primary",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentPrimary,
                    )
                    LemonadeUi.Text(
                        text = "Secondary",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                    )
                    LemonadeUi.Text(
                        text = "Tertiary",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentTertiary,
                    )
                    LemonadeUi.Text(
                        text = "Critical",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentCritical,
                    )
                    LemonadeUi.Text(
                        text = "Positive",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentPositive,
                    )
                    LemonadeUi.Text(
                        text = "Info",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentInfo,
                    )
                }
            }
        }

        // lineSpacing adds space between lines on top of the natural line height.
        item(key = "Line Spacing") {
            TextSection(title = "Line Spacing") {
                TextSectionColumn {
                    LemonadeUi.Text(
                        text = "Default (no lineSpacing override).\n" +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                    )
                    LemonadeUi.Text(
                        text = "lineSpacing = 0.sp\n" +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        lineSpacing = 0.sp,
                    )
                    LemonadeUi.Text(
                        text = "lineSpacing = 8.sp\n" +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        lineSpacing = 8.sp,
                    )
                    LemonadeUi.Text(
                        text = "lineSpacing = 16.sp\n" +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        lineSpacing = 16.sp,
                    )
                }
            }
        }

        // Overflow is a behaviour rather than a style, so the section is written out by hand.
        item(key = "Overflow") {
            TextSection(title = "Overflow") {
                TextSectionColumn {
                    LemonadeUi.Text(
                        text = "This is a very long text that will be truncated at the end " +
                            "with ellipsis because it exceeds the available width",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    LemonadeUi.Text(
                        text = "This text allows multiple lines but is limited to 2 lines " +
                            "maximum. Lorem ipsum dolor sit amet, consectetur adipiscing " +
                            "elit. Sed do eiusmod tempor incididunt ut labore.",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun TypographyCategoryColumn(styles: List<LemonadeTypography>) {
    TextSectionColumn {
        styles.forEachIndexed { index, typography ->
            if (index > 0 && typography.subCategory() != styles[index - 1].subCategory()) {
                LemonadeUi.HorizontalDivider()
            }
            LemonadeUi.Text(
                text = typography.toDisplayLabel(),
                textStyle = typography.style,
            )
        }
    }
}

@Composable
private fun TextSectionColumn(content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
    ) {
        content()
    }
}

// Matches the boundary between a lowercase letter and the uppercase letter or digit that
// starts the next word: "BodyXLargeRegular" becomes "Body XLarge Regular".
private val camelCaseBoundaryRegex = Regex("([a-z])([A-Z0-9])")

private data class TypographyLabelInfo(
    val displayLabel: String,
    val category: String,
    val subCategory: String?,
)

private val typographyLabels: Map<LemonadeTypography, TypographyLabelInfo> =
    LemonadeTypography.entries.associateWith { typography ->
        val parts = typography.name
            .replace(
                regex = camelCaseBoundaryRegex,
                replacement = "$1 $2",
            ).split(" ")
        TypographyLabelInfo(
            displayLabel = parts.joinToString(" "),
            category = parts.first(),
            subCategory = if (parts.size > 2) parts[1] else null,
        )
    }

private fun LemonadeTypography.toDisplayLabel(): String = typographyLabels.getValue(this).displayLabel

private fun LemonadeTypography.category(): String = typographyLabels.getValue(this).category

/** Returns the weight-variant group, or null for styles that have none. */
private fun LemonadeTypography.subCategory(): String? = typographyLabels.getValue(this).subCategory

@Composable
private fun TextSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}
