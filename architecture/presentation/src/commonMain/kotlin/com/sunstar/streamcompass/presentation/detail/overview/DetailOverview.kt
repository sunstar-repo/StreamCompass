package com.sunstar.streamcompass.presentation.detail.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.sunstar.streamcompass.domain.model.Deeplink
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.ic_bookmark_filled
import streamcompass.architecture.presentation.generated.resources.ic_bookmark_outline
import streamcompass.architecture.presentation.generated.resources.stream_detail_no_streaming_options
import streamcompass.architecture.presentation.generated.resources.stream_detail_overview_more
import streamcompass.architecture.presentation.generated.resources.stream_detail_overview_runtime_format
import streamcompass.architecture.presentation.generated.resources.stream_detail_overview_runtime_hour_minute_format
import streamcompass.architecture.presentation.generated.resources.stream_detail_view_streaming_options
import streamcompass.architecture.presentation.generated.resources.watchlist_add
import streamcompass.architecture.presentation.generated.resources.watchlist_remove

@Composable
fun DetailOverview(
    title: String,
    logo: String,
    genres: List<String>,
    description: String,
    releaseDate: String,
    runtimeMinutes: Int,
    certification: String,
    deeplinks: List<Deeplink>,
    isWatchlisted: Boolean,
    onWatchlistToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isStreamingOptionsSheetShown by remember { mutableStateOf(false) }
    var isDescriptionSheetShown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = OVERVIEW_GRADIENT_BOTTOM_ALPHA)
                    ),
                )
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            // 1. logo, fallback으로 title 표현
            if (logo.isEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            } else {
                val context = LocalPlatformContext.current
                val svgImageLoader = remember(context) {
                    ImageLoader.Builder(context)
                        .components { add(SvgDecoder.Factory()) }
                        .build()
                }
                AsyncImage(
                    model = remember(logo) {
                        ImageRequest.Builder(context).data(logo).crossfade(true).build()
                    },
                    imageLoader = svgImageLoader,
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.heightIn(max = LOGO_MAX_HEIGHT)
                        .widthIn(max = LOGO_MAX_WIDTH),
                )
            }

            // 2. genre
            if (genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = genres.joinToString(GENRE_SEPARATOR),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. view streaming options button
            if (deeplinks.isEmpty()) {
                Text(
                    text = stringResource(Res.string.stream_detail_no_streaming_options),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            } else {
                Button(onClick = { isStreamingOptionsSheetShown = true }) {
                    Text(text = stringResource(Res.string.stream_detail_view_streaming_options))
                }
            }

            // 4. description, 2줄 제한 + 마지막 줄 끝에 inline 더보기 배지
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailDescriptionText(
                    description = description,
                    onMoreClick = { isDescriptionSheetShown = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // 5. 개봉연도 / 상영시간 / certification 배지(좌측) + watchlist 아이콘(우측).
            // 배지가 없어도 아이콘은 항상 노출돼야 하므로 배지 유무와 무관하게 이 줄 자체는 항상 그린다.
            val releaseYear = releaseDate.substringBefore("-")
            val runtimeLabel = if (runtimeMinutes > 0) {
                val hours = runtimeMinutes / MINUTES_PER_HOUR
                val minutes = runtimeMinutes % MINUTES_PER_HOUR
                if (hours > 0) {
                    stringResource(
                        Res.string.stream_detail_overview_runtime_hour_minute_format,
                        hours,
                        minutes
                    )
                } else {
                    stringResource(Res.string.stream_detail_overview_runtime_format, minutes)
                }
            } else {
                null
            }
            val metadataItems = listOfNotNull(
                releaseYear.takeIf { it.isNotEmpty() },
                runtimeLabel,
                certification.takeIf { it.isNotEmpty() },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    metadataItems.forEachIndexed { index, item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(BADGE_CORNER_RADIUS))
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = BADGE_BACKGROUND_ALPHA))
                                .padding(
                                    horizontal = BADGE_HORIZONTAL_PADDING,
                                    vertical = BADGE_VERTICAL_PADDING
                                ),
                        )
                        if (index != metadataItems.lastIndex) {
                            Text(
                                text = GENRE_SEPARATOR,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                            )
                        }
                    }
                }
                WatchlistIconButton(isWatchlisted = isWatchlisted, onClick = onWatchlistToggleClick)
            }
        }
    }

    if (isStreamingOptionsSheetShown) {
        DetailStreamingOptionBottomSheet(
            deeplinks = deeplinks,
            onDismissRequest = { isStreamingOptionsSheetShown = false },
        )
    }

    if (isDescriptionSheetShown) {
        DetailDescriptionBottomSheet(
            description = description,
            onDismissRequest = { isDescriptionSheetShown = false },
        )
    }
}

@Composable
private fun WatchlistIconButton(isWatchlisted: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(WATCHLIST_ICON_BUTTON_SIZE)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = BADGE_BACKGROUND_ALPHA))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                if (isWatchlisted) Res.drawable.ic_bookmark_filled else Res.drawable.ic_bookmark_outline
            ),
            contentDescription = stringResource(
                if (isWatchlisted) Res.string.watchlist_remove else Res.string.watchlist_add
            ),
            tint = Color.White,
            modifier = Modifier.size(WATCHLIST_ICON_SIZE),
        )
    }
}

// description을 최대 DESCRIPTION_MAX_LINES줄로 측정해보고, 넘칠 때만 마지막 줄 끝에
// "…더보기" 배지가 정확히 들어갈 만큼 텍스트를 잘라 inline placeholder로 붙인다.
@Composable
private fun DetailDescriptionText(
    description: String,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle =
        MaterialTheme.typography.bodyMedium.copy(color = Color.White, textAlign = TextAlign.Center)
    val badgeStyle = MaterialTheme.typography.labelSmall.copy(color = Color.White)
    val moreLabel = stringResource(Res.string.stream_detail_overview_more)

    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        val constraints = Constraints(maxWidth = maxWidthPx)

        val fullResult = remember(description, maxWidthPx, textStyle) {
            textMeasurer.measure(
                text = description,
                style = textStyle,
                maxLines = DESCRIPTION_MAX_LINES,
                constraints = constraints,
            )
        }

        if (!fullResult.hasVisualOverflow) {
            Text(text = description, style = textStyle, modifier = Modifier.fillMaxWidth())
            return@BoxWithConstraints
        }

        val badgeTextResult = remember(moreLabel, badgeStyle) {
            textMeasurer.measure(text = moreLabel, style = badgeStyle)
        }
        val badgePlaceholder = with(density) {
            Placeholder(
                width = (badgeTextResult.size.width.toDp() + BADGE_HORIZONTAL_PADDING * 2).toSp(),
                height = (badgeTextResult.size.height.toDp() + BADGE_VERTICAL_PADDING * 2).toSp(),
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
            )
        }

        fun buildDescriptionWithBadge(cutLength: Int): AnnotatedString = buildAnnotatedString {
            append(description.substring(0, cutLength))
            if (cutLength < description.length) append(ELLIPSIS)
            append(BADGE_LEADING_SPACE)
            appendInlineContent(BADGE_ID, BADGE_PLACEHOLDER_TEXT)
        }

        fun fits(cutLength: Int): Boolean {
            val annotated = buildDescriptionWithBadge(cutLength)
            val result = textMeasurer.measure(
                text = annotated,
                style = textStyle,
                maxLines = DESCRIPTION_MAX_LINES,
                constraints = constraints,
                placeholders = listOf(
                    AnnotatedString.Range(
                        item = badgePlaceholder,
                        start = annotated.length - BADGE_PLACEHOLDER_TEXT.length,
                        end = annotated.length,
                    ),
                ),
            )
            return !result.hasVisualOverflow
        }

        val cutLength = remember(description, maxWidthPx, badgePlaceholder) {
            var low = 0
            var high = description.length
            while (low < high) {
                val mid = (low + high + 1) / 2
                if (fits(mid)) low = mid else high = mid - 1
            }
            low
        }

        val annotatedText =
            remember(description, cutLength) { buildDescriptionWithBadge(cutLength) }

        Text(
            text = annotatedText,
            style = textStyle,
            maxLines = DESCRIPTION_MAX_LINES,
            overflow = TextOverflow.Clip,
            inlineContent = mapOf(
                BADGE_ID to InlineTextContent(placeholder = badgePlaceholder) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(BADGE_CORNER_RADIUS))
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = BADGE_BACKGROUND_ALPHA))
                            .clickable(onClick = onMoreClick),
                    ) {
                        Text(text = moreLabel, style = badgeStyle)
                    }
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailDescriptionBottomSheet(description: String, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailStreamingOptionBottomSheet(
    deeplinks: List<Deeplink>,
    onDismissRequest: () -> Unit
) {
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

private const val OVERVIEW_GRADIENT_BOTTOM_ALPHA = 0.85f
private const val GENRE_SEPARATOR = " · "
private const val DESCRIPTION_MAX_LINES = 2
private const val MINUTES_PER_HOUR = 60
private val LOGO_MAX_HEIGHT = 56.dp
private val LOGO_MAX_WIDTH = 200.dp
private val BADGE_CORNER_RADIUS = 4.dp
private const val BADGE_BACKGROUND_ALPHA = 0.6f
private val BADGE_HORIZONTAL_PADDING = 6.dp
private val BADGE_VERTICAL_PADDING = 2.dp
private val WATCHLIST_ICON_BUTTON_SIZE = 36.dp
private val WATCHLIST_ICON_SIZE = 20.dp
private const val ELLIPSIS = "…"
private const val BADGE_LEADING_SPACE = " "
private const val BADGE_ID = "more_badge"
private const val BADGE_PLACEHOLDER_TEXT = "￼"
