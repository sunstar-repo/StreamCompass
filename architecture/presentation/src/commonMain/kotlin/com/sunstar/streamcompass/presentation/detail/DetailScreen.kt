package com.sunstar.streamcompass.presentation.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.presentation.core.AVATAR_SIZE
import com.sunstar.streamcompass.presentation.core.AvatarCard
import com.sunstar.streamcompass.presentation.core.PosterCard
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_no_streaming_options
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
                reviewsFlow = viewModel.reviewsFlow,
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
    reviewsFlow: Flow<PagingData<Review>>,
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

            DetailViewModel.Tab.Review -> ReviewsList(
                reviewsFlow = reviewsFlow,
                modifier = Modifier.weight(1f),
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

@Composable
private fun ReviewsList(reviewsFlow: Flow<PagingData<Review>>, modifier: Modifier = Modifier) {
    val pagingItems = reviewsFlow.collectAsLazyPagingItems()

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index -> "${index}_${pagingItems[index]?.id}" },
        ) { index ->
            val review = pagingItems[index]
            if (null != review) {
                ReviewRow(review = review)
            }
        }
    }
}

@Composable
private fun ReviewRow(review: Review) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Column(
            modifier = Modifier.width(AVATAR_SIZE),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AvatarCard(imageUrl = review.avatarPath, contentDescription = review.authorName)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.authorName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            val bubbleShape = remember {
                ReviewBubbleShape(
                    cornerRadius = REVIEW_BUBBLE_CORNER_RADIUS,
                    tailWidth = REVIEW_BUBBLE_TAIL_WIDTH,
                    tailHeight = REVIEW_BUBBLE_TAIL_HEIGHT,
                    tailTopOffset = REVIEW_BUBBLE_TAIL_TOP_OFFSET,
                )
            }
            var isExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { isExpanded = !isExpanded }
                    .padding(
                        start = REVIEW_BUBBLE_TAIL_WIDTH + 12.dp,
                        top = 10.dp,
                        end = 12.dp,
                        bottom = 10.dp,
                    ),
            ) {
                Text(
                    text = review.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else REVIEW_CONTENT_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.createdAt.substringBefore("T"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

private class ReviewBubbleShape(
    private val cornerRadius: Dp,
    private val tailWidth: Dp,
    private val tailHeight: Dp,
    private val tailTopOffset: Dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val tailWidthPx = with(density) { tailWidth.toPx() }
        val tailHeightPx = with(density) { tailHeight.toPx() }
        val tailTopOffsetPx = with(density) { tailTopOffset.toPx() }

        val bodyPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = tailWidthPx,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = cornerRadiusPx,
                    radiusY = cornerRadiusPx,
                )
            )
        }

        val tailPath = Path().apply {
            moveTo(tailWidthPx, tailTopOffsetPx)
            lineTo(0f, tailTopOffsetPx + tailHeightPx / 2f)
            lineTo(tailWidthPx, tailTopOffsetPx + tailHeightPx)
            close()
        }

        val combinedPath = Path()
        combinedPath.op(bodyPath, tailPath, PathOperation.Union)

        return Outline.Generic(combinedPath)
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

private val REVIEW_BUBBLE_CORNER_RADIUS = 12.dp
private val REVIEW_BUBBLE_TAIL_WIDTH = 8.dp
private val REVIEW_BUBBLE_TAIL_HEIGHT = 12.dp
private val REVIEW_BUBBLE_TAIL_TOP_OFFSET = 10.dp
private const val REVIEW_CONTENT_MAX_LINES = 5
