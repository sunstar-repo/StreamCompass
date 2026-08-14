package com.sunstar.streamcompass.presentation.movie

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
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovieScreen(
    viewModel: MovieViewModel = koinViewModel(),
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        state.rows.forEach { (rowType, contentsFlow) ->
            SuggestionRow(
                titleRes = rowType.titleRes,
                contentsFlow = contentsFlow,
                onStreamClick = onStreamClick,
            )
        }
    }
}

@Composable
private fun SuggestionRow(
    titleRes: StringResource,
    contentsFlow: Flow<PagingData<MovieStream>>,
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()

    MediaRow(titleRes = titleRes, minHeight = POSTER_ROW_MIN_HEIGHT) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                "${index}_${pagingItems[index]?.tmdbId}"
            },
        ) { index ->
            val stream = pagingItems[index]
            if (null != stream) {
                PosterCard(
                    imageUrl = stream.posterPath,
                    title = stream.title,
                    onClick = { onStreamClick(stream.tmdbId) },
                )
            }
        }
    }
}
