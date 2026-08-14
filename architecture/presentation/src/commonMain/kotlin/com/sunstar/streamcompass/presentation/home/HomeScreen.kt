package com.sunstar.streamcompass.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Stream
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val items by viewModel.trendingStreams.collectAsState()

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // for modulo indexing
    val virtualItemCount = items.size * VIRTUAL_LOOP_COUNT
    val initialItem = virtualItemCount / 2
    val carouselState = rememberCarouselState(initialItem = initialItem) { virtualItemCount }

    HorizontalCenteredHeroCarousel(
        state = carouselState,
        flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(state = carouselState),
        itemSpacing = 10.dp,
        modifier = Modifier.fillMaxWidth(),
    ) { index ->
        val stream = items[index % items.size]
        TrendingCarouselItem(stream = stream, onClick = onStreamClick)
    }
}

@Composable
private fun CarouselItemScope.TrendingCarouselItem(stream: Stream, onClick: (tmdbId: Int) -> Unit) {
    val drawInfo = carouselItemDrawInfo
    val focusFraction = if (drawInfo.maxSize > drawInfo.minSize) {
        ((drawInfo.size - drawInfo.minSize) / (drawInfo.maxSize - drawInfo.minSize)).coerceIn(0f, 1f)
    } else {
        1f
    }

    val (tmdbId, backdropPath, title) = when (stream) {
        is Stream.MovieStream -> Triple(stream.tmdbId, stream.backdropPath, stream.title)
        is Stream.TvStream -> Triple(stream.tmdbId, stream.backdropPath, stream.name)
    }

    val context = LocalPlatformContext.current
    AsyncImage(
        model = remember(backdropPath) {
            ImageRequest.Builder(context)
                .data(backdropPath)
                .crossfade(true)
                .build()
        },
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(ITEM_HEIGHT)
            .maskClip(RoundedCornerShape(ITEM_CORNER_RADIUS))
            .clickable(enabled = stream is Stream.MovieStream) { onClick(tmdbId) }
            .graphicsLayer {
                alpha = lerp(start = DIM_ALPHA, stop = 1f, fraction = focusFraction)
            },
    )
}

private val ITEM_HEIGHT = 200.dp
private val ITEM_CORNER_RADIUS = 20.dp
private const val DIM_ALPHA = 0.5f
private const val VIRTUAL_LOOP_COUNT = 1000
