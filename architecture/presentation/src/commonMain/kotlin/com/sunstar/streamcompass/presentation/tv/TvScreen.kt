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
import com.sunstar.streamcompass.presentation.core.BACKDROP_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.BackdropCard
import com.sunstar.streamcompass.presentation.core.MediaRow
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvScreen(
    viewModel: TvViewModel = koinViewModel(),
    scrollState: ScrollState = rememberScrollState(),
) {
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        state.rows.forEach { (rowType, contentsFlow) ->
            SuggestionRow(titleRes = rowType.titleRes, contentsFlow = contentsFlow)
        }
    }
}

@Composable
private fun SuggestionRow(titleRes: StringResource, contentsFlow: Flow<PagingData<TvStream>>) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()

    MediaRow(titleRes = titleRes, minHeight = BACKDROP_ROW_MIN_HEIGHT) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                "${index}_${pagingItems[index]?.tmdbId}"
            },
        ) { index ->
            val stream = pagingItems[index]
            if (null != stream) {
                BackdropCard(imageUrl = stream.backdropPath, title = stream.name)
            }
        }
    }
}
