package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeCardBackground
import com.teya.lemonade.core.LemonadeCardHeadingStyle
import com.teya.lemonade.core.LemonadeCardPadding
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TagVoice

@Composable
internal fun CardDisplay() {
    SampleScreenDisplayLazyColumn(title = "Card") {
        item(key = "Backgrounds") {
            CardSection(title = "Backgrounds") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        background = LemonadeCardBackground.Default,
                    ) {
                        LemonadeUi.Text(
                            text = "Default",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        background = LemonadeCardBackground.Subtle,
                    ) {
                        LemonadeUi.Text(
                            text = "Subtle",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        background = LemonadeCardBackground.Elevated,
                    ) {
                        LemonadeUi.Text(
                            text = "Elevated",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }
                }
            }
        }

        item(key = "Spacing") {
            CardSection(title = "Spacing") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeCardPadding.entries.forEach { padding ->
                        LemonadeUi.Card(contentPadding = padding) {
                            LemonadeUi.Text(
                                text = padding.name,
                                textStyle = LemonadeTheme.typography.bodyMediumRegular,
                            )
                        }
                    }
                }
            }
        }

        item(key = "Heading Styles") {
            CardSection(title = "Heading Styles") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Default Heading",
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = "Tag",
                                    voice = TagVoice.Neutral,
                                )
                            },
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Card with default heading style.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Default Heading",
                            subtitle = "Subtitle",
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = "Tag",
                                    voice = TagVoice.Neutral,
                                )
                            },
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Card with default heading and subtitle.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Overline Heading",
                            headingStyle = LemonadeCardHeadingStyle.Overline,
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = "Tag",
                                    voice = TagVoice.Neutral,
                                )
                            },
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Card with overline heading style.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Overline Heading",
                            subtitle = "Subtitle",
                            headingStyle = LemonadeCardHeadingStyle.Overline,
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = "Tag",
                                    voice = TagVoice.Neutral,
                                )
                            },
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Card with overline heading and subtitle.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }
                }
            }
        }

        item(key = "Header Slots") {
            CardSection(title = "Header Slots") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Leading Icon",
                            leadingSlot = {
                                LemonadeUi.Icon(
                                    icon = LemonadeIcons.Store,
                                    contentDescription = null,
                                    size = LemonadeAssetSize.Medium,
                                )
                            },
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Header with leading slot.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "Navigation",
                            showNavigationIndicator = true,
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Header with navigation indicator.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }

                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(
                            title = "All Slots",
                            leadingSlot = {
                                LemonadeUi.Icon(
                                    icon = LemonadeIcons.Store,
                                    contentDescription = null,
                                    size = LemonadeAssetSize.Medium,
                                )
                            },
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = "Active",
                                    voice = TagVoice.Positive,
                                )
                            },
                            showNavigationIndicator = true,
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Leading, trailing, and navigation combined.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }
                }
            }
        }

        item(key = "Footer Action") {
            CardSection(title = "Footer Action") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.Card(
                        contentPadding = LemonadeCardPadding.Medium,
                        header = CardHeaderConfig(title = "Card with Footer"),
                        footerAction = CardFooterActionConfig(
                            label = "Action",
                            onClick = {},
                        ),
                    ) {
                        LemonadeUi.Text(
                            text = "Card content with a footer action button.",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }
                }
            }
        }

        lazyGroupedCardSection()
    }
}

private fun LazyListScope.lazyGroupedCardSection() {
    item(key = "Lazy Grouped List title") {
        CardSectionTitle(
            title = "Lazy Grouped List",
            modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing300),
        )
    }

    lemonadeCardItems(
        contentPadding = LemonadeCardPadding.Medium,
        header = CardHeaderConfig(
            title = "Transactions",
            subtitle = "Rows compose lazily as they scroll into view",
        ),
        footerAction = CardFooterActionConfig(
            label = "See all",
            onClick = {},
        ),
    ) {
        items(
            count = 20,
            key = { index -> "transaction-$index" },
        ) { index ->
            LazyGroupedCardRow(index = index)
        }
    }
}

@Composable
private fun LazyGroupedCardRow(index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = "Transaction ${index + 1}",
            textStyle = LemonadeTheme.typography.bodyMediumRegular,
            modifier = Modifier.weight(weight = 1f),
        )
        LemonadeUi.Text(
            text = "£${index + 1}.00",
            textStyle = LemonadeTheme.typography.bodyMediumSemiBold,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun CardSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        CardSectionTitle(title = title)
        content()
    }
}

@Composable
private fun CardSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    LemonadeUi.Text(
        text = title,
        textStyle = LemonadeTheme.typography.headingXSmall,
        color = LemonadeTheme.colors.content.contentSecondary,
        modifier = modifier,
    )
}
