package com.sunstar.streamcompass.presentation.tv

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
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.mediaRowItemKey
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvScreen(
    viewModel: TvViewModel = koinViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        state.rows.forEach { (rowType, contentsFlow) ->
            SuggestionRow(
                titleRes = rowType.titleRes,
                rowId = rowType.id,
                contentsFlow = contentsFlow,
                onStreamClick = onStreamClick,
            )
        }
    }
}

@Composable
private fun SuggestionRow(
    titleRes: StringResource,
    rowId: String,
    contentsFlow: Flow<PagingData<TvStream>>,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()

    MediaRow(titleRes = titleRes, minHeight = POSTER_ROW_MIN_HEIGHT) {
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
                    onClick = {
                        onStreamClick(
                            stream.tmdbId,
                            stream.posterPath,
                            rowId,
                            StreamType.Tv
                        )
                    },
                )
            }
        }
    }
}
