package com.sunstar.streamcompass.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.sunstar.streamcompass.domain.model.Stream
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val nowPlayingStreams = viewModel.nowPlayingStreams.collectAsLazyPagingItems()

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = nowPlayingStreams.itemCount,
                key = nowPlayingStreams.itemKey { it.tmdbId },
            ) { index ->
                nowPlayingStreams[index]?.let { stream ->
                    StreamPosterItem(stream)
                }
            }
        }
    }
}

@Composable
private fun StreamPosterItem(stream: Stream) {
    Column(modifier = Modifier.width(120.dp)) {
        AsyncImage(
            model = stream.posterPath,
            contentDescription = stream.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stream.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
