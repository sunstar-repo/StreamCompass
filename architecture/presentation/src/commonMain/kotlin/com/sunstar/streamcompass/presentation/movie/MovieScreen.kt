package com.sunstar.streamcompass.presentation.movie

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.isInitialError
import com.sunstar.streamcompass.presentation.core.isInitialLoading
import com.sunstar.streamcompass.presentation.core.mediaRowItemKey
import com.sunstar.streamcompass.presentation.core.posterOverlayClip
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieScreen(
    viewModel: MovieViewModel = koinViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        state.rows.forEach { (rowType, contentsFlow) ->
            SuggestionRow(
                titleRes = rowType.titleRes,
                rowId = rowType.id,
                contentsFlow = contentsFlow,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onStreamClick = onStreamClick,
                onViewAllClick = onViewAllClick,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SuggestionRow(
    titleRes: StringResource,
    rowId: String,
    contentsFlow: Flow<PagingData<MovieStream>>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    onViewAllClick: (title: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()
    val title = stringResource(titleRes)

    MediaRow(
        titleRes = titleRes,
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
                        onStreamClick(
                            stream.tmdbId,
                            stream.posterPath,
                            rowId,
                            StreamType.Movie
                        )
                    },
                )
            }
        }
    }
}
