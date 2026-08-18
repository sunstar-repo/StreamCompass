package com.sunstar.streamcompass.presentation.detail.series

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Episode
import com.sunstar.streamcompass.domain.model.Season
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_series_episode_badge
import streamcompass.architecture.presentation.generated.resources.stream_detail_series_episode_count
import streamcompass.architecture.presentation.generated.resources.stream_detail_series_season_number

@Composable
fun DetailSeries(
    state: DetailViewModel.State.Succeed,
    onSeasonSelected: (seasonNumber: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSeason by remember { mutableStateOf<Season?>(null) }
    var isSeasonSheetShown by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        SeasonSelector(
            label = selectedSeason?.name
                ?: stringResource(
                    Res.string.stream_detail_series_season_number,
                    state.selectedSeasonNumber
                ),
            onClick = { isSeasonSheetShown = true },
            modifier = Modifier.padding(16.dp),
        )

        val episodePagingItems = state.episodesFlow.collectAsLazyPagingItems()
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            items(
                count = episodePagingItems.itemCount,
                key = { index -> "${index}_${episodePagingItems[index]?.episodeNumber}" },
            ) { index ->
                val episode = episodePagingItems[index]
                if (null != episode) {
                    EpisodeItem(episode = episode)
                }
            }
        }
    }

    if (isSeasonSheetShown) {
        SeasonBottomSheet(
            seasonsFlow = state.seasonsFlow,
            onSeasonSelected = { season ->
                selectedSeason = season
                onSeasonSelected(season.seasonNumber)
                isSeasonSheetShown = false
            },
            onDismissRequest = { isSeasonSheetShown = false },
        )
    }
}

@Composable
private fun SeasonSelector(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = SEASON_SELECTOR_CHEVRON, style = MaterialTheme.typography.titleMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonBottomSheet(
    seasonsFlow: Flow<PagingData<Season>>,
    onSeasonSelected: (Season) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val pagingItems = seasonsFlow.collectAsLazyPagingItems()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                count = pagingItems.itemCount,
                key = { index -> "${index}_${pagingItems[index]?.seasonNumber}" },
            ) { index ->
                val season = pagingItems[index]
                if (null != season) {
                    SeasonRow(season = season, onClick = { onSeasonSelected(season) })
                }
            }
        }
    }
}

@Composable
private fun SeasonRow(season: Season, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val context = LocalPlatformContext.current
        AsyncImage(
            model = remember(season.posterPath) {
                ImageRequest.Builder(context).data(season.posterPath).crossfade(true).build()
            },
            contentDescription = season.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(SEASON_POSTER_WIDTH)
                .height(SEASON_POSTER_HEIGHT)
                .clip(RoundedCornerShape(SEASON_POSTER_CORNER_RADIUS)),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = season.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(
                    Res.string.stream_detail_series_episode_count,
                    season.episodeCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box {
            ElevatedCard(shape = RoundedCornerShape(EPISODE_IMAGE_CORNER_RADIUS)) {
                val context = LocalPlatformContext.current
                AsyncImage(
                    model = remember(episode.stillPath) {
                        ImageRequest.Builder(context).data(episode.stillPath).crossfade(true)
                            .build()
                    },
                    contentDescription = episode.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(EPISODE_IMAGE_WIDTH).height(EPISODE_IMAGE_HEIGHT),
                )
            }

            Text(
                text = stringResource(
                    Res.string.stream_detail_series_episode_badge,
                    episode.episodeNumber
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(EPISODE_BADGE_CORNER_RADIUS))
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = EPISODE_BADGE_BACKGROUND_ALPHA))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = episode.overview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else EPISODE_OVERVIEW_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val SEASON_SELECTOR_CHEVRON = "▾"
private val SEASON_POSTER_WIDTH = 48.dp
private val SEASON_POSTER_HEIGHT = 72.dp
private val SEASON_POSTER_CORNER_RADIUS = 8.dp
private val EPISODE_IMAGE_WIDTH = 140.dp
private val EPISODE_IMAGE_HEIGHT = 79.dp
private val EPISODE_IMAGE_CORNER_RADIUS = 8.dp
private val EPISODE_BADGE_CORNER_RADIUS = 4.dp
private const val EPISODE_BADGE_BACKGROUND_ALPHA = 0.6f
private const val EPISODE_OVERVIEW_MAX_LINES = 3
