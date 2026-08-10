package com.sunstar.streamcompass.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    Column {
        SuggestionRow(
            title = "Now Playing",
            contentsFlow = state.nowPlayings,
            onStreamClick = onStreamClick,
        )

        Spacer(modifier = Modifier.height(8.dp))

        SuggestionRow(
            title = "Upcomming",
            contentsFlow = state.upcommings,
            onStreamClick = onStreamClick,
        )
    }
}

@Composable
private fun SuggestionRow(
    title: String,
    contentsFlow: Flow<PagingData<MovieStream>>,
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val pagingItems = contentsFlow.collectAsLazyPagingItems()

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
    Column(modifier = Modifier.width(120.dp)) {
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
                    .width(120.dp)
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stream.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}