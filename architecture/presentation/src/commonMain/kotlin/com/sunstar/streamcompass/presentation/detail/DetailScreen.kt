package com.sunstar.streamcompass.presentation.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.statusBarProtectionHeight
import com.sunstar.streamcompass.presentation.detail.about.About
import com.sunstar.streamcompass.presentation.detail.overview.Overview
import com.sunstar.streamcompass.presentation.detail.recommended.RecommendationsGrid
import com.sunstar.streamcompass.presentation.detail.review.ReviewsList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_no_streaming_options

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    tmdbId: Int,
    posterPath: String,
    rowId: String,
    recordHistory: Boolean,
    viewModel: DetailViewModel = koinViewModel(parameters = {
        parametersOf(
            tmdbId,
            recordHistory
        )
    }),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    var isSheetShown by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        // 짧은 변(가로/세로 중 작은 쪽)을 가득 채우는 폭 기준으로 크기를 고정 — 세로 화면에서는 너비, 가로/태블릿에서는 높이가 기준이 된다.
        val posterWidth = minOf(maxWidth, maxHeight)
        val posterHeight = posterWidth / POSTER_ASPECT_RATIO
        val posterHeightPx = with(density) { posterHeight.toPx() }
        // edge-to-edge라 poster가 완전히 접히면 tab이 statusBar와 겹친다 — StatusBarProtection과 동일한 높이만큼은 항상 남겨둔다.
        val statusBarProtectionHeightPx = with(density) { statusBarProtectionHeight().toPx() }
        val maxCollapsePx = (posterHeightPx - statusBarProtectionHeightPx).coerceAtLeast(0f)

        var posterOffsetPx by remember { mutableFloatStateOf(0f) }
        val posterScrollableState = rememberScrollableState { delta ->
            val newOffset = (posterOffsetPx + delta).coerceIn(-maxCollapsePx, 0f)
            val consumed = newOffset - posterOffsetPx
            posterOffsetPx = newOffset
            consumed
        }
        val nestedScrollConnection = remember(maxCollapsePx) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (available.y >= 0f) return Offset.Zero
                    val newOffset = (posterOffsetPx + available.y).coerceIn(-maxCollapsePx, 0f)
                    val consumed = newOffset - posterOffsetPx
                    posterOffsetPx = newOffset
                    return Offset(0f, consumed)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y <= 0f) return Offset.Zero
                    val newOffset = (posterOffsetPx + available.y).coerceIn(-maxCollapsePx, 0f)
                    val consumedByPoster = newOffset - posterOffsetPx
                    posterOffsetPx = newOffset
                    return Offset(0f, consumedByPoster)
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { (posterHeightPx + posterOffsetPx).toDp() })
                    .clipToBounds()
                    .scrollable(state = posterScrollableState, orientation = Orientation.Vertical),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(modifier = Modifier.width(posterWidth).height(posterHeight)) {
                    DetailPoster(
                        tmdbId = tmdbId,
                        posterPath = posterPath,
                        rowId = rowId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                    )

                    Overview(
                        title = (state as? DetailViewModel.State.Succeed)?.streamDetail?.title,
                        deeplinks = (state as? DetailViewModel.State.Succeed)?.streamDetail?.deeplinks.orEmpty(),
                        onViewStreamingOptionsClick = { isSheetShown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(POSTER_GRADIENT_HEIGHT_FRACTION)
                            .align(Alignment.BottomCenter),
                    )
                }
            }

            when (val current = state) {
                DetailViewModel.State.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DetailViewModel.State.Succeed -> {
                    DetailBody(
                        streamDetail = current.streamDetail,
                        selectedTab = current.selectedTab,
                        onTabSelected = viewModel::onTabSelected,
                        recommendationsFlow = viewModel.recommendationsFlow,
                        reviewsFlow = viewModel.reviewsFlow,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        onStreamClick = onStreamClick,
                        nestedScrollConnection = nestedScrollConnection,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    val current = state
    if (isSheetShown && current is DetailViewModel.State.Succeed) {
        StreamingOptionBottomSheet(
            deeplinks = current.streamDetail.deeplinks,
            onDismissRequest = { isSheetShown = false },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailPoster(
    tmdbId: Int,
    posterPath: String,
    rowId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val context = LocalPlatformContext.current
    AsyncImage(
        model = remember(posterPath) {
            ImageRequest.Builder(context)
                .data(posterPath)
                .crossfade(true)
                .build()
        },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = with(sharedTransitionScope) {
            Modifier
                .fillMaxSize()
                .sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = posterSharedElementKey(
                            streamType = StreamType.Movie,
                            rowId = rowId,
                            tmdbId = tmdbId
                        ),
                    ),
                    animatedVisibilityScope = animatedContentScope,
                )
        },
    )
}

@Composable
private fun DetailBody(
    streamDetail: MovieStreamDetail,
    selectedTab: DetailViewModel.Tab,
    onTabSelected: (DetailViewModel.Tab) -> Unit,
    recommendationsFlow: Flow<PagingData<MovieStream>>,
    reviewsFlow: Flow<PagingData<Review>>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String) -> Unit,
    nestedScrollConnection: NestedScrollConnection,
    modifier: Modifier = Modifier,
) {
    val tabs = DetailViewModel.Tab.values()

    Column(modifier = modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
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
            DetailViewModel.Tab.About -> About(
                streamDetail = streamDetail,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Recommended -> RecommendationsGrid(
                recommendationsFlow = recommendationsFlow,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onStreamClick = onStreamClick,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Review -> ReviewsList(
                reviewsFlow = reviewsFlow,
                modifier = Modifier.weight(1f),
            )
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

private const val POSTER_ASPECT_RATIO = 2f / 3f
private const val POSTER_GRADIENT_HEIGHT_FRACTION = 0.5f
