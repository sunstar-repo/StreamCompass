package com.sunstar.streamcompass.presentation.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.CollapsingHeaderState
import com.sunstar.streamcompass.presentation.core.certification
import com.sunstar.streamcompass.presentation.core.deeplinks
import com.sunstar.streamcompass.presentation.core.displayTitle
import com.sunstar.streamcompass.presentation.core.genres
import com.sunstar.streamcompass.presentation.core.logo
import com.sunstar.streamcompass.presentation.core.overview
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.releaseDate
import com.sunstar.streamcompass.presentation.core.rememberCollapsingHeaderState
import com.sunstar.streamcompass.presentation.core.runtimeMinutes
import com.sunstar.streamcompass.presentation.core.statusBarProtectionHeight
import com.sunstar.streamcompass.presentation.detail.about.DetailAbout
import com.sunstar.streamcompass.presentation.detail.overview.DetailOverview
import com.sunstar.streamcompass.presentation.detail.recommended.DetailRecommendationsGrid
import com.sunstar.streamcompass.presentation.detail.review.DetailReviewsList
import com.sunstar.streamcompass.presentation.detail.series.DetailSeries
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    tmdbId: Int,
    posterPath: String,
    rowId: String,
    recordHistory: Boolean,
    streamType: StreamType,
    viewModel: DetailViewModel = koinViewModel(parameters = {
        parametersOf(
            tmdbId,
            streamType,
            recordHistory
        )
    }),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        // 짧은 변(가로/세로 중 작은 쪽)을 가득 채우는 폭 기준으로 크기를 고정 — 세로 화면에서는 너비, 가로/태블릿에서는 높이가 기준이 된다.
        val posterWidth = minOf(maxWidth, maxHeight)
        val posterHeight = posterWidth / POSTER_ASPECT_RATIO
        val posterHeightPx = with(density) { posterHeight.toPx() }
        // edge-to-edge라 poster가 완전히 접히면 tab이 statusBar와 겹친다 — StatusBarProtection과 동일한 높이만큼은 항상 남겨둔다.
        val statusBarProtectionHeightPx = with(density) { statusBarProtectionHeight().toPx() }
        val maxCollapsePx = (posterHeightPx - statusBarProtectionHeightPx).coerceAtLeast(0f)

        val headerState = rememberCollapsingHeaderState(maxCollapsePx = maxCollapsePx)

        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(
                tmdbId = tmdbId,
                posterPath = posterPath,
                rowId = rowId,
                streamType = streamType,
                streamDetail = (state as? DetailViewModel.State.Succeed)?.streamDetail,
                posterWidth = posterWidth,
                posterHeight = posterHeight,
                posterHeightPx = posterHeightPx,
                headerState = headerState,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
            )

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
                    DetailContent(
                        state = current,
                        streamType = streamType,
                        onTabSelected = viewModel::onTabSelected,
                        onSeasonSelected = viewModel::onSeasonSelected,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        onStreamClick = onStreamClick,
                        nestedScrollConnection = headerState.nestedScrollConnection,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailHeader(
    tmdbId: Int,
    posterPath: String,
    rowId: String,
    streamType: StreamType,
    streamDetail: StreamDetail?,
    posterWidth: Dp,
    posterHeight: Dp,
    posterHeightPx: Float,
    headerState: CollapsingHeaderState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(density) { (posterHeightPx + headerState.offsetPx).toDp() })
            .clipToBounds()
            .scrollable(state = headerState.scrollableState, orientation = Orientation.Vertical),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(modifier = Modifier.width(posterWidth).height(posterHeight)) {
            DetailPoster(
                tmdbId = tmdbId,
                posterPath = posterPath,
                rowId = rowId,
                streamType = streamType,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
            )

            AnimatedVisibility(
                visible = null != streamDetail,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(POSTER_GRADIENT_HEIGHT_FRACTION)
                    .align(Alignment.BottomCenter),
            ) {
                if (null != streamDetail) {
                    DetailOverview(
                        title = streamDetail.displayTitle,
                        logo = streamDetail.logo,
                        genres = streamDetail.genres,
                        description = streamDetail.overview,
                        releaseDate = streamDetail.releaseDate,
                        runtimeMinutes = streamDetail.runtimeMinutes,
                        certification = streamDetail.certification,
                        deeplinks = streamDetail.deeplinks,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailPoster(
    tmdbId: Int,
    posterPath: String,
    rowId: String,
    streamType: StreamType,
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
                            streamType = streamType,
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
private fun DetailContent(
    state: DetailViewModel.State.Succeed,
    streamType: StreamType,
    onTabSelected: (DetailViewModel.Tab) -> Unit,
    onSeasonSelected: (seasonNumber: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
    nestedScrollConnection: NestedScrollConnection,
    modifier: Modifier = Modifier,
) {
    val tabs = DetailViewModel.Tab.values(streamType = streamType)
    val selectedTab = state.selectedTab

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
            DetailViewModel.Tab.About -> DetailAbout(
                state = state,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Series -> DetailSeries(
                state = state,
                onSeasonSelected = onSeasonSelected,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Recommended -> DetailRecommendationsGrid(
                state = state,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onStreamClick = onStreamClick,
                modifier = Modifier.weight(1f),
            )

            DetailViewModel.Tab.Review -> DetailReviewsList(
                state = state,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val POSTER_ASPECT_RATIO = 2f / 3f
private const val POSTER_GRADIENT_HEIGHT_FRACTION = 0.5f
