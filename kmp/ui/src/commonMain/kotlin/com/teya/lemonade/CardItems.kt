package com.teya.lemonade

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeCardBackground
import com.teya.lemonade.core.LemonadeCardPadding

private const val CARD_ITEM_ANIMATION_DURATION_MS = 450

// Hoisted so every slot shares one immutable spec instead of allocating specs per item.
private val cardItemFadeSpec: FiniteAnimationSpec<Float> =
    tween(durationMillis = CARD_ITEM_ANIMATION_DURATION_MS)

private val cardItemPlacementSpec: FiniteAnimationSpec<IntOffset> =
    tween(durationMillis = CARD_ITEM_ANIMATION_DURATION_MS)

private val cardItemRadiusSpec: FiniteAnimationSpec<Dp> =
    tween(durationMillis = CARD_ITEM_ANIMATION_DURATION_MS)

internal enum class LemonadeCardItemPosition {
    Single,
    First,
    Middle,
    Last,
}

internal fun resolveCardSlotPosition(
    visualIndex: Int,
    totalCount: Int,
): LemonadeCardItemPosition {
    val isFirst = visualIndex == 0
    val isLast = visualIndex == totalCount - 1
    return when {
        isFirst && isLast -> LemonadeCardItemPosition.Single
        isFirst -> LemonadeCardItemPosition.First
        isLast -> LemonadeCardItemPosition.Last
        else -> LemonadeCardItemPosition.Middle
    }
}

internal fun cardTotalSlotCount(
    rowCount: Int,
    hasHeader: Boolean,
    hasFooter: Boolean,
): Int {
    val headerCount = if (hasHeader) 1 else 0
    val footerCount = if (hasFooter) 1 else 0
    return headerCount + rowCount + footerCount
}

internal fun cardRowVisualIndex(
    rowIndex: Int,
    hasHeader: Boolean,
): Int = if (hasHeader) rowIndex + 1 else rowIndex

/**
 * Renders a Lemonade card into a [LazyListScope], one real lazy item per row.
 *
 * Rows compose on demand as they scroll into view, unlike [LemonadeUi.Card], whose whole content
 * composes at once.
 *
 * The card look is drawn per item: the first visual slot (the header when present,
 * otherwise the first row) carries the top corners, and the last visual slot (the footer
 * when present, otherwise the last row) carries the bottom corners.
 *
 * The host list must not use `verticalArrangement = Arrangement.spacedBy(...)`: the gaps
 * would appear between the card's own rows and visually split the card. Give rows their
 * own internal padding, and separate consecutive cards with a spacer item instead.
 *
 * [content] only exposes [LemonadeCardItemsScope.item] and [LemonadeCardItemsScope.items];
 * any other [LazyListScope] call (for example [LazyListScope.stickyHeader]) resolves to the host
 * [LazyListScope] instead and is emitted immediately, before this card's own header, rows,
 * and footer, rather than becoming part of it.
 *
 * As rows are added, removed, or reordered, each slot fades and slides into place and its
 * corners round or square off in step, so the card reshapes smoothly instead of snapping.
 * Give rows stable keys through [LemonadeCardItemsScope.items] so those changes animate.
 */
public fun LazyListScope.lemonadeCardItems(
    contentPadding: LemonadeCardPadding = LemonadeCardPadding.None,
    background: LemonadeCardBackground = LemonadeCardBackground.Default,
    header: CardHeaderConfig? = null,
    footerAction: CardFooterActionConfig? = null,
    content: LemonadeCardItemsScope.() -> Unit,
) {
    val recorder = CardItemsRecorder()
    recorder.content()

    val rowCount = recorder.rowCount
    val totalCount = cardTotalSlotCount(
        rowCount = rowCount,
        hasHeader = header != null,
        hasFooter = footerAction != null,
    )
    if (totalCount == 0) {
        return
    }

    if (header != null) {
        item(contentType = CardSlotContentType.Header) {
            CardSlotContainer(
                position = resolveCardSlotPosition(
                    visualIndex = 0,
                    totalCount = totalCount,
                ),
                background = background,
            ) {
                CardHeader(config = header)
            }
        }
    }

    cardRowItems(
        recorder = recorder,
        background = background,
        contentPadding = contentPadding,
        hasHeader = header != null,
        totalCount = totalCount,
    )

    if (footerAction != null) {
        item(contentType = CardSlotContentType.Footer) {
            CardSlotContainer(
                position = resolveCardSlotPosition(
                    visualIndex = totalCount - 1,
                    totalCount = totalCount,
                ),
                background = background,
            ) {
                CardFooterAction(config = footerAction)
            }
        }
    }
}

/**
 * Declares the rows of a [lemonadeCardItems] card.
 *
 * Mirrors the [LazyListScope] item DSL; every entry becomes a real lazy item of the host list.
 */
public sealed interface LemonadeCardItemsScope {
    public fun item(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit,
    )

    public fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    )
}

/**
 * Adds a row for each element of [items], mirroring [LazyListScope.items].
 *
 * When provided, [key] values must be unique across the entire host [LazyListScope], not
 * just within this card, since every row becomes a real lazy item of the host list.
 */
public inline fun <T> LemonadeCardItemsScope.items(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    items(
        count = items.size,
        key = if (key != null) {
            { index: Int -> key(items[index]) }
        } else {
            null
        },
        contentType = { index: Int -> contentType(items[index]) },
    ) { index: Int ->
        itemContent(items[index])
    }
}

/**
 * Adds a row for each element of [items], exposing its index, mirroring
 * [LazyListScope.itemsIndexed].
 *
 * When provided, [key] values must be unique across the entire host [LazyListScope], not
 * just within this card, since every row becomes a real lazy item of the host list.
 */
public inline fun <T> LemonadeCardItemsScope.itemsIndexed(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) {
    items(
        count = items.size,
        key = if (key != null) {
            { index: Int -> key(index, items[index]) }
        } else {
            null
        },
        contentType = { index: Int -> contentType(index, items[index]) },
    ) { index: Int ->
        itemContent(index, items[index])
    }
}

private class CardItemsInterval(
    val count: Int,
    val key: ((index: Int) -> Any)?,
    val contentType: (index: Int) -> Any?,
    val itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
)

private class CardItemsRecorder : LemonadeCardItemsScope {
    val intervals: MutableList<CardItemsInterval> = mutableListOf()

    val rowCount: Int
        get() = intervals.sumOf { interval -> interval.count }

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        intervals.add(
            CardItemsInterval(
                count = 1,
                key = if (key != null) {
                    { _: Int -> key }
                } else {
                    null
                },
                contentType = { _: Int -> contentType },
                itemContent = { _: Int -> content() },
            ),
        )
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) {
        intervals.add(
            CardItemsInterval(
                count = count,
                key = key,
                contentType = contentType,
                itemContent = itemContent,
            ),
        )
    }
}

private data class CardRowContentType(
    val userType: Any?,
)

private enum class CardSlotContentType {
    Header,
    Footer,
}

private fun LazyListScope.cardRowItems(
    recorder: CardItemsRecorder,
    background: LemonadeCardBackground,
    contentPadding: LemonadeCardPadding,
    hasHeader: Boolean,
    totalCount: Int,
) {
    val rowCount = recorder.rowCount
    var emittedRows = 0
    recorder.intervals.forEach { interval ->
        val intervalStart = emittedRows
        items(
            count = interval.count,
            key = interval.key,
            contentType = { localIndex: Int ->
                CardRowContentType(userType = interval.contentType(localIndex))
            },
        ) { localIndex: Int ->
            val itemScope = this
            val rowIndex = intervalStart + localIndex
            CardRowContainer(
                position = resolveCardSlotPosition(
                    visualIndex = cardRowVisualIndex(
                        rowIndex = rowIndex,
                        hasHeader = hasHeader,
                    ),
                    totalCount = totalCount,
                ),
                background = background,
                contentPadding = contentPadding,
                isFirstRow = rowIndex == 0,
                isLastRow = rowIndex == rowCount - 1,
            ) {
                interval.itemContent(itemScope, localIndex)
            }
        }
        emittedRows += interval.count
    }
}

@Composable
private fun LazyItemScope.CardSlotContainer(
    position: LemonadeCardItemPosition,
    background: LemonadeCardBackground,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = cardItemAnimationModifier()
            .fillMaxWidth()
            .clip(shape = animatedCardItemShape(position = position))
            .background(color = background.background),
    ) {
        content()
    }
}

@Composable
private fun LazyItemScope.CardRowContainer(
    position: LemonadeCardItemPosition,
    background: LemonadeCardBackground,
    contentPadding: LemonadeCardPadding,
    isFirstRow: Boolean,
    isLastRow: Boolean,
    content: @Composable () -> Unit,
) {
    val padding = contentPadding.spacing
    val zero = LocalSpaces.current.spacing0
    Box(
        modifier = cardItemAnimationModifier()
            .fillMaxWidth()
            .clip(shape = animatedCardItemShape(position = position))
            .background(color = background.background)
            .padding(
                start = padding,
                top = if (isFirstRow) padding else zero,
                end = padding,
                bottom = if (isLastRow) padding else zero,
            ),
    ) {
        content()
    }
}

private fun LazyItemScope.cardItemAnimationModifier(): Modifier =
    Modifier.animateItem(
        fadeInSpec = cardItemFadeSpec,
        placementSpec = cardItemPlacementSpec,
        fadeOutSpec = cardItemFadeSpec,
    )

@Composable
private fun animatedCardItemShape(position: LemonadeCardItemPosition): Shape {
    val radius = LocalRadius.current.semantic.radiusContainerDefault
    val zero = LocalRadius.current.radius0
    val roundsTop = position == LemonadeCardItemPosition.First || position == LemonadeCardItemPosition.Single
    val roundsBottom = position == LemonadeCardItemPosition.Last || position == LemonadeCardItemPosition.Single
    val topRadius by animateDpAsState(
        targetValue = if (roundsTop) radius else zero,
        animationSpec = cardItemRadiusSpec,
        label = "cardItemTopRadius",
    )
    val bottomRadius by animateDpAsState(
        targetValue = if (roundsBottom) radius else zero,
        animationSpec = cardItemRadiusSpec,
        label = "cardItemBottomRadius",
    )
    return RoundedCornerShape(
        topStart = topRadius,
        topEnd = topRadius,
        bottomStart = bottomRadius,
        bottomEnd = bottomRadius,
    )
}

@LemonadePreview
@Composable
private fun CardItemsPreview() {
    LazyColumn(
        modifier = Modifier.height(480.dp),
    ) {
        lemonadeCardItems(
            contentPadding = LemonadeCardPadding.Small,
            header = CardHeaderConfig(
                title = "Card heading",
                subtitle = "Lazy rows",
            ),
            footerAction = CardFooterActionConfig(
                label = "See all",
                onClick = {},
            ),
        ) {
            items(count = 5) { index: Int ->
                CardItemsPreviewRow(text = "Row ${index + 1}")
            }
        }
    }
}

@LemonadePreview
@Composable
private fun CardItemsHeaderlessPreview() {
    LazyColumn(
        modifier = Modifier.height(280.dp),
    ) {
        lemonadeCardItems(
            contentPadding = LemonadeCardPadding.Small,
        ) {
            items(count = 3) { index: Int ->
                CardItemsPreviewRow(text = "Row ${index + 1}")
            }
        }
        item {
            Spacer(modifier = Modifier.height(LocalSpaces.current.spacing400))
        }
        lemonadeCardItems {
            item {
                CardItemsPreviewRow(text = "Single row card")
            }
        }
    }
}

@Composable
private fun CardItemsPreviewRow(text: String) {
    Box(
        modifier = Modifier.padding(all = LocalSpaces.current.spacing200),
    ) {
        LemonadeUi.Text(
            text = text,
            textStyle = LocalTypographies.current.bodyMediumRegular,
            color = LocalColors.current.content.contentPrimary,
        )
    }
}
