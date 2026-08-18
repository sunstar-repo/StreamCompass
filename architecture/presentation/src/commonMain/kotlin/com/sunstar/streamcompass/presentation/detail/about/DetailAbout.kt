package com.sunstar.streamcompass.presentation.detail.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.sunstar.streamcompass.domain.model.Person
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.presentation.core.AvatarCard
import com.sunstar.streamcompass.presentation.core.BACKDROP_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.BackdropCard
import com.sunstar.streamcompass.presentation.core.MEDIA_TITLE_SPACING
import com.sunstar.streamcompass.presentation.core.MediaRow
import com.sunstar.streamcompass.presentation.core.POSTER_ROW_MIN_HEIGHT
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.backdrops
import com.sunstar.streamcompass.presentation.core.budget
import com.sunstar.streamcompass.presentation.core.cast
import com.sunstar.streamcompass.presentation.core.crew
import com.sunstar.streamcompass.presentation.core.networkLogo
import com.sunstar.streamcompass.presentation.core.originalTitle
import com.sunstar.streamcompass.presentation.core.posters
import com.sunstar.streamcompass.presentation.core.releaseDate
import com.sunstar.streamcompass.presentation.core.revenue
import com.sunstar.streamcompass.presentation.core.trailerKeys
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.ic_play
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_budget_label
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_cast
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_crew
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_gallery
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_information_header
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_network_label
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_original_title_label
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_posters
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_release_date_label
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_revenue_label
import streamcompass.architecture.presentation.generated.resources.stream_detail_about_trailers

@Composable
fun DetailAbout(state: DetailViewModel.State.Succeed, modifier: Modifier = Modifier) {
    val streamDetail = state.streamDetail
    val uriHandler = LocalUriHandler.current
    var selectedBackdropIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPosterIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        DetailAboutInfoSection(
            streamDetail = streamDetail,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )

        if (streamDetail.cast.isNotEmpty()) {
            MediaRow(
                titleRes = Res.string.stream_detail_about_cast,
                minHeight = PERSON_ROW_MIN_HEIGHT
            ) {
                itemsIndexed(
                    items = streamDetail.cast,
                    key = { index, person -> "${index}_${person.name}" }) { _, person ->
                    DetailPersonItem(person = person)
                }
            }
        }

        if (streamDetail.crew.isNotEmpty()) {
            MediaRow(
                titleRes = Res.string.stream_detail_about_crew,
                minHeight = PERSON_ROW_MIN_HEIGHT
            ) {
                itemsIndexed(
                    items = streamDetail.crew,
                    key = { index, person -> "${index}_${person.name}" }) { _, person ->
                    DetailPersonItem(person = person)
                }
            }
        }

        if (streamDetail.trailerKeys.isNotEmpty()) {
            MediaRow(
                titleRes = Res.string.stream_detail_about_trailers,
                minHeight = BACKDROP_ROW_MIN_HEIGHT
            ) {
                items(items = streamDetail.trailerKeys, key = { it }) { trailerKey ->
                    BackdropCard(
                        imageUrl = youtubeThumbnailUrl(videoKey = trailerKey),
                        contentDescription = null,
                        onClick = { uriHandler.openUri(youtubeWatchUrl(videoKey = trailerKey)) },
                        overlay = { DetailPlayIconOverlay() },
                    )
                }
            }
        }

        if (streamDetail.posters.isNotEmpty()) {
            MediaRow(
                titleRes = Res.string.stream_detail_about_posters,
                minHeight = POSTER_ROW_MIN_HEIGHT
            ) {
                itemsIndexed(
                    items = streamDetail.posters,
                    key = { _, posterUrl -> posterUrl }) { index, posterUrl ->
                    PosterCard(
                        imageUrl = posterUrl,
                        onClick = { selectedPosterIndex = index },
                    )
                }
            }
        }

        if (streamDetail.backdrops.isNotEmpty()) {
            MediaRow(
                titleRes = Res.string.stream_detail_about_gallery,
                minHeight = BACKDROP_ROW_MIN_HEIGHT
            ) {
                itemsIndexed(
                    items = streamDetail.backdrops,
                    key = { _, backdropUrl -> backdropUrl }) { index, backdropUrl ->
                    BackdropCard(
                        imageUrl = backdropUrl,
                        contentDescription = null,
                        onClick = { selectedBackdropIndex = index },
                    )
                }
            }
        }
    }

    val posterIndex = selectedPosterIndex
    if (null != posterIndex) {
        DetailImagePagerDialog(
            images = streamDetail.posters,
            initialIndex = posterIndex,
            aspectRatio = POSTER_ASPECT_RATIO,
            onDismissRequest = { selectedPosterIndex = null },
        )
    }

    val backdropIndex = selectedBackdropIndex
    if (null != backdropIndex) {
        DetailImagePagerDialog(
            images = streamDetail.backdrops,
            initialIndex = backdropIndex,
            aspectRatio = BACKDROP_ASPECT_RATIO,
            onDismissRequest = { selectedBackdropIndex = null },
        )
    }
}

// 원제 / 개봉일 / 방송사(네트워크·제작사) 로고 / 제작비 / 수익 — 하나의 Section으로 묶어 "라벨: 값" 형태로 세로 나열.
// Tv는 제작비/수익 값이 없어 자연히 숨겨진다.
@Composable
private fun DetailAboutInfoSection(streamDetail: StreamDetail, modifier: Modifier = Modifier) {
    val originalTitle = streamDetail.originalTitle
    val releaseDate = streamDetail.releaseDate
    val networkLogo = streamDetail.networkLogo
    val budget = streamDetail.budget
    val revenue = streamDetail.revenue

    if (originalTitle.isEmpty() && releaseDate.isEmpty() && networkLogo.isEmpty() && budget <= 0L && revenue <= 0L) return

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.stream_detail_about_information_header),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SECTION_CORNER_RADIUS))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
        ) {
            if (originalTitle.isNotEmpty()) {
                DetailInfoRow(label = stringResource(Res.string.stream_detail_about_original_title_label)) {
                    Text(text = originalTitle, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (releaseDate.isNotEmpty()) {
                DetailInfoRow(label = stringResource(Res.string.stream_detail_about_release_date_label)) {
                    Text(text = releaseDate, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (networkLogo.isNotEmpty()) {
                DetailInfoRow(label = stringResource(Res.string.stream_detail_about_network_label)) {
                    DetailNetworkLogo(logoUrl = networkLogo)
                }
            }
            if (budget > 0L) {
                DetailInfoRow(label = stringResource(Res.string.stream_detail_about_budget_label)) {
                    Text(text = formatCurrency(budget), style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (revenue > 0L) {
                DetailInfoRow(label = stringResource(Res.string.stream_detail_about_revenue_label)) {
                    Text(
                        text = formatCurrency(revenue),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// 얼굴은 Review 작성자 아바타와 동일하게 원형(AvatarCard)으로 표현, 이름 1줄 아래에 역할을 괄호로 표기.
@Composable
private fun DetailPersonItem(person: Person) {
    Column(
        modifier = Modifier.width(PERSON_ITEM_WIDTH),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarCard(
            imageUrl = person.profilePath,
            contentDescription = person.name,
            size = PERSON_AVATAR_SIZE
        )
        Spacer(modifier = Modifier.height(MEDIA_TITLE_SPACING))
        Text(
            text = person.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        if (person.role.isNotEmpty()) {
            Text(
                text = "(${person.role})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, valueContent: @Composable () -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(INFO_ROW_LABEL_SPACING))
        valueContent()
    }
}

@Composable
private fun DetailNetworkLogo(logoUrl: String) {
    val context = LocalPlatformContext.current
    val svgImageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    }
    AsyncImage(
        model = remember(logoUrl) {
            ImageRequest.Builder(context).data(logoUrl).crossfade(true).build()
        },
        imageLoader = svgImageLoader,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(height = NETWORK_LOGO_HEIGHT, width = NETWORK_LOGO_MAX_WIDTH),
    )
}

private fun formatCurrency(amount: Long): String {
    val grouped =
        amount.toString().reversed().chunked(CURRENCY_GROUPING_SIZE).joinToString(",").reversed()
    return "$$grouped"
}

@Composable
private fun BoxScope.DetailPlayIconOverlay() {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(PLAY_ICON_BADGE_SIZE)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = PLAY_ICON_BADGE_ALPHA)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_play),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(PLAY_ICON_SIZE),
        )
    }
}

private fun youtubeThumbnailUrl(videoKey: String): String =
    "https://img.youtube.com/vi/$videoKey/hqdefault.jpg"

private fun youtubeWatchUrl(videoKey: String): String = "https://www.youtube.com/watch?v=$videoKey"

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailImagePagerDialog(
    images: List<String>,
    initialIndex: Int,
    aspectRatio: Float,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }
        val context = LocalPlatformContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = IMAGE_DIALOG_DIM_ALPHA))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                ),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().aspectRatio(aspectRatio),
            ) { page ->
                AsyncImage(
                    model = remember(images[page]) {
                        ImageRequest.Builder(context).data(images[page]).crossfade(true).build()
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private val PERSON_ITEM_WIDTH = 72.dp
private val PERSON_AVATAR_SIZE = 64.dp
private val PERSON_ROW_MIN_HEIGHT = 100.dp
private val SECTION_CORNER_RADIUS = 12.dp
private val INFO_ROW_LABEL_SPACING = 2.dp
private val NETWORK_LOGO_HEIGHT = 20.dp
private val NETWORK_LOGO_MAX_WIDTH = 80.dp
private const val CURRENCY_GROUPING_SIZE = 3
private val PLAY_ICON_BADGE_SIZE = 48.dp
private val PLAY_ICON_SIZE = 24.dp
private const val PLAY_ICON_BADGE_ALPHA = 0.6f
private const val BACKDROP_ASPECT_RATIO = 16f / 9f
private const val POSTER_ASPECT_RATIO = 2f / 3f
private const val IMAGE_DIALOG_DIM_ALPHA = 0.9f
