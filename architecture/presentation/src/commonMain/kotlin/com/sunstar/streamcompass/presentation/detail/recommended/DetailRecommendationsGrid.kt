package com.sunstar.streamcompass.presentation.detail.recommended

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.LoadingIndicator
import com.sunstar.streamcompass.presentation.core.MessageIndicator
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.displayTitle
import com.sunstar.streamcompass.presentation.core.isInitialError
import com.sunstar.streamcompass.presentation.core.isInitialLoading
import com.sunstar.streamcompass.presentation.core.posterOverlayClip
import com.sunstar.streamcompass.presentation.core.posterPath
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.streamType
import com.sunstar.streamcompass.presentation.core.tmdbId
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.paging_load_failed

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailRecommendationsGrid(
    state: DetailViewModel.State.Succeed,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagingItems = state.recommendationsFlow.collectAsLazyPagingItems()

    if (pagingItems.isInitialLoading) {
        LoadingIndicator(
            modifier = modifier.fillMaxWidth().heightIn(min = RECOMMENDATIONS_GRID_MIN_HEIGHT)
        )
        return
    }

    if (pagingItems.isInitialError) {
        MessageIndicator(
            text = stringResource(Res.string.paging_load_failed),
            modifier = modifier.fillMaxWidth().heightIn(min = RECOMMENDATIONS_GRID_MIN_HEIGHT),
        )
        return
    }

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
                    title = stream.displayTitle,
                    imageModifier = with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = posterSharedElementKey(
                                    streamType = stream.streamType,
                                    rowId = RECOMMENDATIONS_ROW_ID,
                                    tmdbId = stream.tmdbId,
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
                            RECOMMENDATIONS_ROW_ID,
                            stream.streamType,
                        )
                    },
                )
            }
        }
    }
}

private const val RECOMMENDATIONS_ROW_ID = "recommendations"
private const val RECOMMENDATIONS_GRID_COLUMNS = 3
private val RECOMMENDATIONS_GRID_MIN_HEIGHT = POSTER_ROW_MIN_HEIGHT
