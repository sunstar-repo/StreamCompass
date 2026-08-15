package com.sunstar.streamcompass.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.presentation.core.PosterCard
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_no_streaming_options
import streamcompass.architecture.presentation.generated.resources.stream_detail_review_placeholder
import streamcompass.architecture.presentation.generated.resources.stream_detail_view_streaming_options

@Composable
fun DetailScreen(
    tmdbId: Int,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(tmdbId) }),
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    when (val current = state) {
        DetailViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DetailViewModel.State.Succeed -> {
            DetailContent(
                streamDetail = current.streamDetail,
                selectedTab = current.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                recommendationsFlow = viewModel.recommendationsFlow,
                onStreamClick = onStreamClick,
            )
        }
    }
}

@Composable
private fun DetailContent(
    streamDetail: MovieStreamDetail,
    selectedTab: DetailViewModel.Tab,
    onTabSelected: (DetailViewModel.Tab) -> Unit,
    recommendationsFlow: Flow<PagingData<MovieStream>>,
    onStreamClick: (tmdbId: Int) -> Unit,
) {
    var isSheetShown by remember { mutableStateOf(false) }
    val tabs = DetailViewModel.Tab.values()

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(BACKDROP_HEIGHT)) {
            val context = LocalPlatformContext.current
            AsyncImage(
                model = remember(streamDetail.backdropPath) {
                    ImageRequest.Builder(context)
                        .data(streamDetail.backdropPath)
                        .crossfade(true)
                        .build()
                },
                contentDescription = streamDetail.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Button(
                onClick = { isSheetShown = true },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = maxHeight * BACKDROP_BUTTON_POSITION_RATIO),
            ) {
                Text(text = stringResource(Res.string.stream_detail_view_streaming_options))
            }
        }

        val selectedTabIndex = tabs.indexOf(selectedTab)
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = TabRowDefaults.primaryContainerColor,
            contentColor = TabRowDefaults.primaryContentColor,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(selectedTabIndex)
                )
            },
            divider = {
                HorizontalDivider()
            }
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(text = stringResource(tab.label)) },
                )
            }
        }

        when (selectedTab) {
            DetailViewModel.Tab.Detail -> Text(
                text = streamDetail.overview,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            DetailViewModel.Tab.Recommended -> RecommendationsGrid(
                recommendationsFlow = recommendationsFlow,
                onStreamClick = onStreamClick,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Review -> Text(
                text = stringResource(Res.string.stream_detail_review_placeholder),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }

    if (isSheetShown) {
        StreamingOptionBottomSheet(
            deeplinks = streamDetail.deeplinks,
            onDismissRequest = { isSheetShown = false },
        )
    }
}

@Composable
private fun RecommendationsGrid(
    recommendationsFlow: Flow<PagingData<MovieStream>>,
    onStreamClick: (tmdbId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagingItems = recommendationsFlow.collectAsLazyPagingItems()

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
                    title = stream.title,
                    onClick = { onStreamClick(stream.tmdbId) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamingOptionBottomSheet(deeplinks: List<Deeplink>, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    val uriHandler = LocalUriHandler.current
    val isDarkTheme = isSystemInDarkTheme()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (deeplinks.isEmpty()) {
                Text(text = stringResource(Res.string.stream_detail_no_streaming_options))
            } else {
                deeplinks.forEach { deeplink ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                uriHandler.openUri(uri = deeplink.link)
                                onDismissRequest()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val context = LocalPlatformContext.current
                        val svgImageLoader = remember(context) {
                            ImageLoader.Builder(context)
                                .components { add(SvgDecoder.Factory()) }
                                .build()
                        }
                        val logoImage = if (isDarkTheme) {
                            deeplink.logo.darkThemeImage
                        } else {
                            deeplink.logo.lightThemeImage
                        }
                        AsyncImage(
                            model = remember(logoImage) {
                                ImageRequest.Builder(context)
                                    .data(logoImage)
                                    .crossfade(true)
                                    .build()
                            },
                            imageLoader = svgImageLoader,
                            contentDescription = deeplink.service,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp),
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(text = deeplink.service, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

private val BACKDROP_HEIGHT = 220.dp
private const val BACKDROP_BUTTON_POSITION_RATIO = 0.8f

private const val RECOMMENDATIONS_GRID_COLUMNS = 3
