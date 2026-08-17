package com.sunstar.streamcompass.presentation.detail.recommended

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecommendationsGrid(
    recommendationsFlow: Flow<PagingData<MovieStream>>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagingItems = recommendationsFlow.collectAsLazyPagingItems()

    LazyVerticalGrid(
        columns = GridCells.Fixed(RECOMMENDATIONS_GRID_COLUMNS),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index -> "${index}_${pagingItems[index]?.tmdbId}" },
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
                                    rowId = RECOMMENDATIONS_ROW_ID,
                                    tmdbId = stream.tmdbId,
                                ),
                            ),
                            animatedVisibilityScope = animatedContentScope,
                        )
                    },
                    onClick = {
                        onStreamClick(
                            stream.tmdbId,
                            stream.posterPath,
                            RECOMMENDATIONS_ROW_ID
                        )
                    },
                )
            }
        }
    }
}

private const val RECOMMENDATIONS_ROW_ID = "recommendations"
private const val RECOMMENDATIONS_GRID_COLUMNS = 3
