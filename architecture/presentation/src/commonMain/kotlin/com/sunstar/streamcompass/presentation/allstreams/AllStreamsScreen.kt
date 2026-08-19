package com.sunstar.streamcompass.presentation.allstreams

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.displayTitle
import com.sunstar.streamcompass.presentation.core.mediaRowItemKey
import com.sunstar.streamcompass.presentation.core.posterOverlayClip
import com.sunstar.streamcompass.presentation.core.posterPath
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.tmdbId
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.ic_arrow_back

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AllStreamsScreen(
    title: String,
    rowId: String,
    streamType: StreamType,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: AllStreamsViewModel = koinViewModel(parameters = {
        parametersOf(
            rowId,
            streamType
        )
    }),
    onBackClick: () -> Unit,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    val pagingItems = state.streams.collectAsLazyPagingItems()
    // 원래 Row와 동시에 composition에 남아있는 동안(전환 애니메이션 중) 동일한 shared element key가
    // 충돌하지 않도록, 이 grid 전용 key에는 별도 suffix를 붙인다 — DetailRecommendationsGrid와 동일한 이유.
    val gridRowId = "${rowId}_all"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(ALL_STREAMS_GRID_COLUMNS),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            items(
                count = pagingItems.itemCount,
                key = { index ->
                    mediaRowItemKey(
                        streamType = streamType,
                        rowId = gridRowId,
                        tmdbId = pagingItems[index]?.tmdbId,
                        index = index
                    )
                },
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
                                        streamType = streamType,
                                        rowId = gridRowId,
                                        tmdbId = stream.tmdbId,
                                    ),
                                ),
                                animatedVisibilityScope = animatedContentScope,
                                clipInOverlayDuringTransition = posterOverlayClip(),
                            )
                        },
                        onClick = {
                            onStreamClick(stream.tmdbId, stream.posterPath, rowId, streamType)
                        },
                    )
                }
            }
        }
    }
}

private const val ALL_STREAMS_GRID_COLUMNS = 3
