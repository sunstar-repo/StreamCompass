package com.sunstar.streamcompass.presentation.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.WatchlistActionBottomSheet
import com.sunstar.streamcompass.presentation.core.backdropPath
import com.sunstar.streamcompass.presentation.core.displayTitle
import com.sunstar.streamcompass.presentation.core.isInitialError
import com.sunstar.streamcompass.presentation.core.isInitialLoading
import com.sunstar.streamcompass.presentation.core.mediaRowItemKey
import com.sunstar.streamcompass.presentation.core.posterOverlayClip
import com.sunstar.streamcompass.presentation.core.posterPath
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.streamType
import com.sunstar.streamcompass.presentation.core.tmdbId
import com.sunstar.streamcompass.presentation.core.typeLabelRes
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.home_history_remove
import streamcompass.architecture.presentation.generated.resources.home_row_movie_history
import streamcompass.architecture.presentation.generated.resources.home_row_movie_history_empty
import streamcompass.architecture.presentation.generated.resources.home_row_new_movies
import streamcompass.architecture.presentation.generated.resources.home_row_new_tv
import streamcompass.architecture.presentation.generated.resources.home_row_tv_history
import streamcompass.architecture.presentation.generated.resources.home_row_tv_history_empty
import streamcompass.architecture.presentation.generated.resources.home_row_watchlist_movie
import streamcompass.architecture.presentation.generated.resources.home_row_watchlist_movie_empty
import streamcompass.architecture.presentation.generated.resources.home_row_watchlist_tv
import streamcompass.architecture.presentation.generated.resources.home_row_watchlist_tv_empty

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    var selectedRowItem by remember { mutableStateOf<RowItem?>(null) }
    val movieHistoryStreams by state.movieHistoryStreams.collectAsState(initial = emptyList())
    val tvHistoryStreams by state.tvHistoryStreams.collectAsState(initial = emptyList())
    val movieWatchlistStreams by state.movieWatchlistStreams.collectAsState(initial = emptyList())
    val tvWatchlistStreams by state.tvWatchlistStreams.collectAsState(initial = emptyList())
    val watchlistedMovieIds = remember(movieWatchlistStreams) {
        movieWatchlistStreams.map { it.tmdbId }.toSet()
    }
    val watchlistedTvIds = remember(tvWatchlistStreams) {
        tvWatchlistStreams.map { it.tmdbId }.toSet()
    }

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        if (state.trendingStreams.isEmpty()) {
            TrendingLoadingRow()
        } else {
            TrendingCarousel(items = state.trendingStreams, onClick = onStreamClick)
        }

        NewMoviesRow(
            contentsFlow = state.newMovieStreams,
            rowId = HomeViewModel.RowType.NewMovies.id,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onClick = onStreamClick,
            onLongClick = { stream -> selectedRowItem = RowItem.NewMovie(stream = stream) },
            onViewAllClick = onViewAllClick,
        )

        NewTvRow(
            contentsFlow = state.newTvStreams,
            rowId = HomeViewModel.RowType.NewTv.id,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onClick = onStreamClick,
            onLongClick = { stream -> selectedRowItem = RowItem.NewTv(stream = stream) },
            onViewAllClick = onViewAllClick,
        )

        if (movieWatchlistStreams.isEmpty()) {
            RowEmptyMessage(
                titleRes = Res.string.home_row_watchlist_movie,
                minHeight = POSTER_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_watchlist_movie_empty,
            )
        } else {
            MovieWatchlistRow(
                items = movieWatchlistStreams,
                rowId = HomeViewModel.RowType.MovieWatchlist.id,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onClick = onStreamClick,
                onLongClick = { stream ->
                    selectedRowItem = RowItem.WatchlistMovie(stream = stream)
                },
                onViewAllClick = onViewAllClick,
            )
        }

        if (tvWatchlistStreams.isEmpty()) {
            RowEmptyMessage(
                titleRes = Res.string.home_row_watchlist_tv,
                minHeight = POSTER_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_watchlist_tv_empty,
            )
        } else {
            TvWatchlistRow(
                items = tvWatchlistStreams,
                rowId = HomeViewModel.RowType.TvWatchlist.id,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onClick = onStreamClick,
                onLongClick = { stream -> selectedRowItem = RowItem.WatchlistTv(stream = stream) },
                onViewAllClick = onViewAllClick,
            )
        }

        if (movieHistoryStreams.isEmpty()) {
            RowEmptyMessage(
                titleRes = Res.string.home_row_movie_history,
                minHeight = POSTER_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_movie_history_empty,
            )
        } else {
            MovieHistoryRow(
                items = movieHistoryStreams,
                rowId = HomeViewModel.RowType.MovieHistory.id,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onClick = onStreamClick,
                onLongClick = { stream ->
                    selectedRowItem = RowItem.HistoryMovie(stream = stream)
                },
                onViewAllClick = onViewAllClick,
            )
        }

        if (tvHistoryStreams.isEmpty()) {
            RowEmptyMessage(
                titleRes = Res.string.home_row_tv_history,
                minHeight = POSTER_ROW_MIN_HEIGHT,
                messageRes = Res.string.home_row_tv_history_empty,
            )
        } else {
            TvHistoryRow(
                items = tvHistoryStreams,
                rowId = HomeViewModel.RowType.TvHistory.id,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onClick = onStreamClick,
                onLongClick = { stream -> selectedRowItem = RowItem.HistoryTv(stream = stream) },
                onViewAllClick = onViewAllClick,
            )
        }
    }

    val rowItem = selectedRowItem
    if (null != rowItem) {
        val isWatchlisted = when (rowItem) {
            is RowItem.WatchlistMovie, is RowItem.WatchlistTv -> true
            is RowItem.HistoryMovie -> watchlistedMovieIds.contains(rowItem.stream.tmdbId)
            is RowItem.HistoryTv -> watchlistedTvIds.contains(rowItem.stream.tmdbId)
            is RowItem.NewMovie -> watchlistedMovieIds.contains(rowItem.stream.tmdbId)
            is RowItem.NewTv -> watchlistedTvIds.contains(rowItem.stream.tmdbId)
        }

        WatchlistActionBottomSheet(
            isWatchlisted = isWatchlisted,
            onWatchlistToggleClick = {
                when (rowItem) {
                    is RowItem.HistoryMovie -> viewModel.toggleMovieWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )

                    is RowItem.HistoryTv -> viewModel.toggleTvWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )

                    is RowItem.WatchlistMovie -> viewModel.toggleMovieWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )

                    is RowItem.WatchlistTv -> viewModel.toggleTvWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )

                    is RowItem.NewMovie -> viewModel.toggleMovieWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )

                    is RowItem.NewTv -> viewModel.toggleTvWatchlist(
                        stream = rowItem.stream,
                        isCurrentlyWatchlisted = isWatchlisted
                    )
                }
                selectedRowItem = null
            },
            onDismissRequest = { selectedRowItem = null },
            extraContent = when (rowItem) {
                is RowItem.HistoryMovie -> {
                    {
                        HistoryRemoveOption(onClick = {
                            viewModel.removeMovieHistory(tmdbId = rowItem.stream.tmdbId)
                            selectedRowItem = null
                        })
                    }
                }

                is RowItem.HistoryTv -> {
                    {
                        HistoryRemoveOption(onClick = {
                            viewModel.removeTvHistory(tmdbId = rowItem.stream.tmdbId)
                            selectedRowItem = null
                        })
                    }
                }

                else -> null
            },
        )
    }
}

// long-press 대상 6곳(History Movie/Tv, Watchlist Movie/Tv, New Movie/Tv)을 한 sheet로 처리하기 위한
// 선택 상태 — History만 "이력에서 삭제"를 추가로 보여준다(WatchlistActionBottomSheet의 extraContent).
private sealed interface RowItem {
    data class HistoryMovie(val stream: MovieStream) : RowItem
    data class HistoryTv(val stream: TvStream) : RowItem
    data class WatchlistMovie(val stream: MovieStream) : RowItem
    data class WatchlistTv(val stream: TvStream) : RowItem
    data class NewMovie(val stream: MovieStream) : RowItem
    data class NewTv(val stream: TvStream) : RowItem
}

@Composable
private fun HistoryRemoveOption(onClick: () -> Unit) {
    Text(
        text = stringResource(Res.string.home_history_remove),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    )
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
private fun RowEmptyMessage(titleRes: StringResource, minHeight: Dp, messageRes: StringResource) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight)
                .padding(horizontal = 16.dp),
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
private fun TrendingCarousel(
    items: List<Stream>,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
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
        Text(
            text = currentStream.displayTitle,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun CarouselItemScope.TrendingCarouselItem(
    stream: Stream,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val drawInfo = carouselItemDrawInfo
    val focusFraction = if (drawInfo.maxSize > drawInfo.minSize) {
        ((drawInfo.size - drawInfo.minSize) / (drawInfo.maxSize - drawInfo.minSize)).coerceIn(
            0f,
            1f
        )
    } else {
        1f
    }

    ElevatedCard(
        shape = RoundedCornerShape(CAROUSEL_ITEM_CORNER_RADIUS),
        modifier = Modifier
            .height(CAROUSEL_ITEM_HEIGHT)
            .maskClip(RoundedCornerShape(CAROUSEL_ITEM_CORNER_RADIUS))
            .clickable {
                onClick(
                    stream.tmdbId,
                    stream.posterPath,
                    HomeViewModel.RowType.Trending.id,
                    stream.streamType
                )
            }
            .graphicsLayer {
                alpha = lerp(start = DIM_ALPHA, stop = 1f, fraction = focusFraction)
            },
    ) {
        Box {
            val context = LocalPlatformContext.current
            AsyncImage(
                model = remember(stream.backdropPath) {
                    ImageRequest.Builder(context)
                        .data(stream.backdropPath)
                        .crossfade(true)
                        .build()
                },
                contentDescription = stream.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Text(
                text = stringResource(stream.typeLabelRes),
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
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun NewMoviesRow(
    contentsFlow: Flow<PagingData<MovieStream>>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (MovieStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()
    val title = stringResource(Res.string.home_row_new_movies)

    MediaRow(
        titleRes = Res.string.home_row_new_movies,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        isLoading = pagingItems.isInitialLoading,
        isError = pagingItems.isInitialError,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Movie) },
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                mediaRowItemKey(
                    streamType = StreamType.Movie,
                    rowId = rowId,
                    tmdbId = pagingItems[index]?.tmdbId,
                    index = index
                )
            },
        ) { index ->
            val stream = pagingItems[index]
            if (null != stream) {
                PosterCard(
                    imageUrl = stream.posterPath,
                    title = stream.title,
                    imageModifier = with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = posterSharedElementKey(
                                    streamType = StreamType.Movie,
                                    rowId = rowId,
                                    tmdbId = stream.tmdbId
                                ),
                            ),
                            animatedVisibilityScope = animatedContentScope,
                            clipInOverlayDuringTransition = posterOverlayClip(),
                        )
                    },
                    onClick = {
                        onClick(
                            stream.tmdbId,
                            stream.posterPath,
                            rowId,
                            StreamType.Movie
                        )
                    },
                    onLongClick = { onLongClick(stream) },
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun NewTvRow(
    contentsFlow: Flow<PagingData<TvStream>>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (TvStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()
    val title = stringResource(Res.string.home_row_new_tv)

    MediaRow(
        titleRes = Res.string.home_row_new_tv,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        isLoading = pagingItems.isInitialLoading,
        isError = pagingItems.isInitialError,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Tv) },
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                mediaRowItemKey(
                    streamType = StreamType.Tv,
                    rowId = rowId,
                    tmdbId = pagingItems[index]?.tmdbId,
                    index = index
                )
            },
        ) { index ->
            val stream = pagingItems[index]
            if (null != stream) {
                PosterCard(
                    imageUrl = stream.posterPath,
                    title = stream.name,
                    imageModifier = with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = posterSharedElementKey(
                                    streamType = StreamType.Tv,
                                    rowId = rowId,
                                    tmdbId = stream.tmdbId
                                ),
                            ),
                            animatedVisibilityScope = animatedContentScope,
                            clipInOverlayDuringTransition = posterOverlayClip(),
                        )
                    },
                    onClick = { onClick(stream.tmdbId, stream.posterPath, rowId, StreamType.Tv) },
                    onLongClick = { onLongClick(stream) },
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MovieHistoryRow(
    items: List<MovieStream>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (MovieStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val title = stringResource(Res.string.home_row_movie_history)

    MediaRow(
        titleRes = Res.string.home_row_movie_history,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Movie) },
    ) {
        itemsIndexed(
            items = items,
            key = { index, stream ->
                mediaRowItemKey(
                    streamType = StreamType.Movie,
                    rowId = rowId,
                    tmdbId = stream.tmdbId,
                    index = index
                )
            },
        ) { _, stream ->
            PosterCard(
                imageUrl = stream.posterPath,
                title = stream.title,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = posterSharedElementKey(
                                streamType = StreamType.Movie,
                                rowId = rowId,
                                tmdbId = stream.tmdbId
                            ),
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        clipInOverlayDuringTransition = posterOverlayClip(),
                    )
                },
                onClick = { onClick(stream.tmdbId, stream.posterPath, rowId, StreamType.Movie) },
                onLongClick = { onLongClick(stream) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TvHistoryRow(
    items: List<TvStream>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (TvStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val title = stringResource(Res.string.home_row_tv_history)

    MediaRow(
        titleRes = Res.string.home_row_tv_history,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Tv) },
    ) {
        itemsIndexed(
            items = items,
            key = { index, stream ->
                mediaRowItemKey(
                    streamType = StreamType.Tv,
                    rowId = rowId,
                    tmdbId = stream.tmdbId,
                    index = index
                )
            },
        ) { _, stream ->
            PosterCard(
                imageUrl = stream.posterPath,
                title = stream.name,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = posterSharedElementKey(
                                streamType = StreamType.Tv,
                                rowId = rowId,
                                tmdbId = stream.tmdbId
                            ),
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        clipInOverlayDuringTransition = posterOverlayClip(),
                    )
                },
                onClick = { onClick(stream.tmdbId, stream.posterPath, rowId, StreamType.Tv) },
                onLongClick = { onLongClick(stream) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MovieWatchlistRow(
    items: List<MovieStream>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (MovieStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val title = stringResource(Res.string.home_row_watchlist_movie)

    MediaRow(
        titleRes = Res.string.home_row_watchlist_movie,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Movie) },
    ) {
        itemsIndexed(
            items = items,
            key = { index, stream ->
                mediaRowItemKey(
                    streamType = StreamType.Movie,
                    rowId = rowId,
                    tmdbId = stream.tmdbId,
                    index = index
                )
            },
        ) { _, stream ->
            PosterCard(
                imageUrl = stream.posterPath,
                title = stream.title,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = posterSharedElementKey(
                                streamType = StreamType.Movie,
                                rowId = rowId,
                                tmdbId = stream.tmdbId
                            ),
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        clipInOverlayDuringTransition = posterOverlayClip(),
                    )
                },
                onClick = { onClick(stream.tmdbId, stream.posterPath, rowId, StreamType.Movie) },
                onLongClick = { onLongClick(stream) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TvWatchlistRow(
    items: List<TvStream>,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onLongClick: (TvStream) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val title = stringResource(Res.string.home_row_watchlist_tv)

    MediaRow(
        titleRes = Res.string.home_row_watchlist_tv,
        minHeight = POSTER_ROW_MIN_HEIGHT,
        onViewAllClick = { onViewAllClick(title, rowId, StreamType.Tv) },
    ) {
        itemsIndexed(
            items = items,
            key = { index, stream ->
                mediaRowItemKey(
                    streamType = StreamType.Tv,
                    rowId = rowId,
                    tmdbId = stream.tmdbId,
                    index = index
                )
            },
        ) { _, stream ->
            PosterCard(
                imageUrl = stream.posterPath,
                title = stream.name,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = posterSharedElementKey(
                                streamType = StreamType.Tv,
                                rowId = rowId,
                                tmdbId = stream.tmdbId
                            ),
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        clipInOverlayDuringTransition = posterOverlayClip(),
                    )
                },
                onClick = { onClick(stream.tmdbId, stream.posterPath, rowId, StreamType.Tv) },
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
