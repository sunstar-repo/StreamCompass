package com.sunstar.streamcompass.presentation.movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
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

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(min = ROW_MIN_HEIGHT),
        ) {
            items(
                count = pagingItems.itemCount,
                key = { index ->
                    "${index}_${pagingItems[index]?.tmdbId}"
                },
            ) { index ->
                val stream = pagingItems[index]
                if (null != stream) {
                    SuggestionColumn(stream = stream, onClick = onStreamClick)
                }
            }
        }
    }
}

@Composable
private fun SuggestionColumn(stream: MovieStream, onClick: (tmdbId: Int) -> Unit) {
    Column(modifier = Modifier.width(POSTER_WIDTH)) {
        ElevatedCard(
            onClick = { onClick(stream.tmdbId) }
        ) {
            val context = LocalPlatformContext.current
            AsyncImage(
                model = remember(stream.posterPath) {
                    ImageRequest.Builder(context)
                        .data(stream.posterPath)
                        .crossfade(true)
                        .build()
                },
                contentDescription = stream.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(POSTER_WIDTH)
                    .height(POSTER_HEIGHT)
            )
        }
        Spacer(modifier = Modifier.height(TITLE_SPACING))
        Text(
            text = stream.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val POSTER_WIDTH = 120.dp
private val POSTER_HEIGHT = 180.dp
private val TITLE_SPACING = 4.dp
private val TITLE_LINE_HEIGHT = 16.dp
private val ROW_MIN_HEIGHT = POSTER_HEIGHT + TITLE_SPACING + TITLE_LINE_HEIGHT
