package com.teya.lemonade

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultCollapseDistance: Dp = 64.dp

private fun collapseProgress(
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
    collapsePx: Float,
): Float {
    val offset = if (firstVisibleItemIndex > 0) {
        collapsePx
    } else {
        firstVisibleItemScrollOffset.toFloat()
    }
    return (offset / collapsePx).coerceIn(
        minimumValue = 0f,
        maximumValue = 1f,
    )
}

@Composable
private fun rememberCollapseProgress(
    listState: LazyListState,
    collapseDistance: Dp = DefaultCollapseDistance,
): State<Float> {
    val collapsePx = with(LocalDensity.current) { collapseDistance.toPx() }

    return remember(listState, collapsePx) {
        derivedStateOf {
            collapseProgress(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                collapsePx = collapsePx,
            )
        }
    }
}

@Composable
private fun rememberCollapseProgress(
    gridState: LazyGridState,
    collapseDistance: Dp = DefaultCollapseDistance,
): State<Float> {
    val collapsePx = with(LocalDensity.current) { collapseDistance.toPx() }

    return remember(gridState, collapsePx) {
        derivedStateOf {
            collapseProgress(
                firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = gridState.firstVisibleItemScrollOffset,
                collapsePx = collapsePx,
            )
        }
    }
}

@Composable
private fun rememberCollapseProgress(
    scrollState: ScrollState,
    collapseDistance: Dp = DefaultCollapseDistance,
): State<Float> {
    val collapsePx = with(LocalDensity.current) { collapseDistance.toPx() }

    return remember(scrollState, collapsePx) {
        derivedStateOf {
            collapseProgress(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = scrollState.value,
                collapsePx = collapsePx,
            )
        }
    }
}

@Composable
internal fun SampleScreenDisplayLazyColumn(
    title: String,
    modifier: Modifier = Modifier,
    contentHorizontalPadding: Dp = LemonadeTheme.spaces.spacing400,
    background: Color = LemonadeTheme.colors.background.bgSubtle,
    action: (@Composable () -> Unit)? = null,
    header: @Composable (progress: State<Float>) -> Unit = { progress ->
        SampleScreenHeader(
            title = title,
            progress = progress,
            action = action,
        )
    },
    content: LazyListScope.() -> Unit,
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()

    val bottomGesturePadding = WindowInsets.safeGestures.getBottom(density).dp / 2

    val progress = rememberCollapseProgress(listState = listState)
    var headerHeightDp by remember { mutableStateOf(value = 0.dp) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(top = LemonadeTheme.spaces.spacing400),
            contentPadding = PaddingValues(
                top = headerHeightDp,
                start = contentHorizontalPadding,
                end = contentHorizontalPadding,
                bottom = bottomGesturePadding,
            ),
            content = content,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    headerHeightDp = with(density) { size.height.toDp() }
                },
        ) {
            header(progress)
        }
    }
}

@Composable
internal fun SampleScreenDisplayLazyGrid(
    title: String,
    modifier: Modifier = Modifier,
    contentHorizontalPadding: Dp = LemonadeTheme.spaces.spacing400,
    background: Color = LemonadeTheme.colors.background.bgSubtle,
    header: @Composable (progress: State<Float>) -> Unit = { progress ->
        SampleScreenHeader(
            title = title,
            progress = progress,
        )
    },
    columns: GridCells = GridCells.Adaptive(minSize = 100.dp),
    columnsGap: Dp = LemonadeTheme.spaces.spacing200,
    content: LazyGridScope.() -> Unit,
) {
    val density = LocalDensity.current
    val bottomGesturePadding = WindowInsets.safeGestures.getBottom(density).dp / 2

    val gridState = rememberLazyGridState()

    val progress = rememberCollapseProgress(gridState = gridState)
    var headerHeightDp by remember { mutableStateOf(value = 0.dp) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background),
        ) {
            Spacer(modifier = Modifier.height(height = headerHeightDp))

            LazyVerticalGrid(
                state = gridState,
                columns = columns,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                    bottom = bottomGesturePadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = columnsGap,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = columnsGap,
                    alignment = Alignment.CenterVertically,
                ),
                content = content,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    headerHeightDp = with(density) { size.height.toDp() }
                },
        ) {
            header(progress)
        }
    }
}

@Composable
internal fun SampleScreenDisplayColumn(
    title: String,
    modifier: Modifier = Modifier,
    contentHorizontalPadding: Dp = LemonadeTheme.spaces.spacing400,
    contentBottomPadding: Dp = LemonadeTheme.spaces.spacing400,
    background: Color = LemonadeTheme.colors.background.bgSubtle,
    itemsSpacing: Dp = LemonadeTheme.spaces.spacing300,
    header: @Composable (progress: State<Float>) -> Unit = { progress ->
        SampleScreenHeader(
            title = title,
            progress = progress,
        )
    },
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    val progress = rememberCollapseProgress(scrollState = scrollState)
    var headerHeightDp by remember { mutableStateOf(value = 0.dp) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .navigationBarsPadding()
                .background(background)
                .padding(
                    PaddingValues(
                        top = headerHeightDp,
                        start = contentHorizontalPadding,
                        end = contentHorizontalPadding,
                        bottom = contentBottomPadding,
                    ),
                ),
            verticalArrangement = Arrangement.spacedBy(space = itemsSpacing),
            content = content,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    headerHeightDp = with(density) { size.height.toDp() }
                },
        ) {
            header(progress)
        }
    }
}
