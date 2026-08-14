package com.sunstar.streamcompass.presentation.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.presentation.core.BACKDROP_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.BackdropCard
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.destination_movie
import streamcompass.architecture.presentation.generated.resources.destination_tv
import streamcompass.architecture.presentation.generated.resources.home_history_remove
import streamcompass.architecture.presentation.generated.resources.home_row_movie_history
import streamcompass.architecture.presentation.generated.resources.home_row_movie_history_empty
import streamcompass.architecture.presentation.generated.resources.home_row_tv_history
import streamcompass.architecture.presentation.generated.resources.home_row_tv_history_empty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    var selectedHistoryItem by remember { mutableStateOf<HistoryItem?>(null) }

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        if (state.trendingStreams.isEmpty()) {
            TrendingLoadingRow()
        } else {
            TrendingCarousel(items = state.trendingStreams, onClick = onStreamClick)
        }

        if (state.movieHistoryStreams.isEmpty()) {
            HistoryEmptyRow(
                titleRes = Res.string.home_row_movie_history,
                minHeight = POSTER_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_movie_history_empty,
            )
        } else {
            MovieHistoryRow(
                items = state.movieHistoryStreams,
                onClick = onStreamClick,
                onLongClick = { stream -> selectedHistoryItem = HistoryItem.Movie(stream = stream) },
            )
        }

        if (state.tvHistoryStreams.isEmpty()) {
            HistoryEmptyRow(
                titleRes = Res.string.home_row_tv_history,
                minHeight = BACKDROP_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_tv_history_empty,
            )
        } else {
            TvHistoryRow(
                items = state.tvHistoryStreams,
                onLongClick = { stream -> selectedHistoryItem = HistoryItem.Tv(stream = stream) },
            )
        }
    }

    val historyItem = selectedHistoryItem
    if (null != historyItem) {
        HistoryOptionBottomSheet(
            onRemoveClick = {
                when (historyItem) {
                    is HistoryItem.Movie -> viewModel.removeMovieHistory(tmdbId = historyItem.stream.tmdbId)
                    is HistoryItem.Tv -> viewModel.removeTvHistory(tmdbId = historyItem.stream.tmdbId)
                }
                selectedHistoryItem = null
            },
            onDismissRequest = { selectedHistoryItem = null },
        )
    }
}

private sealed interface HistoryItem {
    data class Movie(val stream: MovieStream) : HistoryItem
    data class Tv(val stream: TvStream) : HistoryItem
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryOptionBottomSheet(onRemoveClick: () -> Unit, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Text(
            text = stringResource(Res.string.home_history_remove),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRemoveClick()
                }
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun TrendingLoadingRow() {
    Box(
        modifier = Modifier.fillMaxWidth().height(CAROUSEL_ITEM_HEIGHT),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HistoryEmptyRow(titleRes: StringResource, minHeight: Dp, messageRes: StringResource) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight).padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendingCarousel(items: List<Stream>, onClick: (tmdbId: Int) -> Unit) {
    // for modulo indexing
    val virtualItemCount = items.size * VIRTUAL_LOOP_COUNT
    val initialItem = virtualItemCount / 2
    val carouselState = rememberCarouselState(initialItem = initialItem) { virtualItemCount }

    Column {
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(state = carouselState),
            itemSpacing = CAROUSEL_ITEM_SPACING,
            maxItemWidth = CAROUSEL_ITEM_HEIGHT * CAROUSEL_ASPECT_RATIO,
            minSmallItemWidth = CAROUSEL_SMALL_ITEM_WIDTH,
            maxSmallItemWidth = CAROUSEL_SMALL_ITEM_WIDTH,
            modifier = Modifier.fillMaxWidth(),
        ) { index ->
            val stream = items[index % items.size]
            TrendingCarouselItem(stream = stream, onClick = onClick)
        }

        // carousel row 하단: 현재 selected(focused) item의 title (item 자체가 아닌 row 공용 영역)
        val currentStream = items[carouselState.currentItem % items.size]
        val currentTitle = when (currentStream) {
            is Stream.MovieStream -> currentStream.title
            is Stream.TvStream -> currentStream.name
        }
        Text(
            text = currentTitle,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun CarouselItemScope.TrendingCarouselItem(stream: Stream, onClick: (tmdbId: Int) -> Unit) {
    val drawInfo = carouselItemDrawInfo
    val focusFraction = if (drawInfo.maxSize > drawInfo.minSize) {
        ((drawInfo.size - drawInfo.minSize) / (drawInfo.maxSize - drawInfo.minSize)).coerceIn(
            0f,
            1f
        )
    } else {
        1f
    }

    val (tmdbId, backdropPath, title) = when (stream) {
        is Stream.MovieStream -> Triple(stream.tmdbId, stream.backdropPath, stream.title)
        is Stream.TvStream -> Triple(stream.tmdbId, stream.backdropPath, stream.name)
    }
    val typeLabelRes = when (stream) {
        is Stream.MovieStream -> Res.string.destination_movie
        is Stream.TvStream -> Res.string.destination_tv
    }

    Box(
        modifier = Modifier
            .height(CAROUSEL_ITEM_HEIGHT)
            .maskClip(RoundedCornerShape(CAROUSEL_ITEM_CORNER_RADIUS))
            .clickable(enabled = stream is Stream.MovieStream) { onClick(tmdbId) }
            .graphicsLayer {
                alpha = lerp(start = DIM_ALPHA, stop = 1f, fraction = focusFraction)
            },
    ) {
        val context = LocalPlatformContext.current
        AsyncImage(
            model = remember(backdropPath) {
                ImageRequest.Builder(context)
                    .data(backdropPath)
                    .crossfade(true)
                    .build()
            },
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Text(
            text = stringResource(typeLabelRes),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(TYPE_BADGE_CORNER_RADIUS))
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = TYPE_BADGE_BACKGROUND_ALPHA))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun MovieHistoryRow(
    items: List<MovieStream>,
    onClick: (tmdbId: Int) -> Unit,
    onLongClick: (MovieStream) -> Unit,
) {
    MediaRow(titleRes = Res.string.home_row_movie_history, minHeight = POSTER_ROW_MIN_HEIGHT) {
        items(items = items, key = { it.tmdbId }) { stream ->
            PosterCard(
                imageUrl = stream.posterPath,
                title = stream.title,
                onClick = { onClick(stream.tmdbId) },
                onLongClick = { onLongClick(stream) },
            )
        }
    }
}

@Composable
private fun TvHistoryRow(items: List<TvStream>, onLongClick: (TvStream) -> Unit) {
    MediaRow(titleRes = Res.string.home_row_tv_history, minHeight = BACKDROP_ROW_MIN_HEIGHT) {
        items(items = items, key = { it.tmdbId }) { stream ->
            BackdropCard(
                imageUrl = stream.backdropPath,
                title = stream.name,
                onLongClick = { onLongClick(stream) },
            )
        }
    }
}

private val CAROUSEL_ITEM_HEIGHT = 200.dp
private val CAROUSEL_ITEM_CORNER_RADIUS = 20.dp
private val CAROUSEL_ITEM_SPACING = 8.dp
private val CAROUSEL_SMALL_ITEM_WIDTH = 24.dp
private const val CAROUSEL_ASPECT_RATIO = 16f / 9f
private const val DIM_ALPHA = 0.5f
private const val VIRTUAL_LOOP_COUNT = 1000
private val TYPE_BADGE_CORNER_RADIUS = CAROUSEL_ITEM_CORNER_RADIUS
private const val TYPE_BADGE_BACKGROUND_ALPHA = 0.6f
