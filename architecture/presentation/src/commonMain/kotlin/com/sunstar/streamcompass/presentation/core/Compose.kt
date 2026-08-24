package com.sunstar.streamcompass.presentation.core

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.StreamType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.media_row_view_all
import streamcompass.architecture.presentation.generated.resources.paging_load_failed
import streamcompass.architecture.presentation.generated.resources.watchlist_add
import streamcompass.architecture.presentation.generated.resources.watchlist_remove

fun mediaRowItemKey(streamType: StreamType, rowId: String, tmdbId: Int?, index: Int): String =
    "${streamType.rawValue}_${rowId}_${tmdbId}_${index}"

fun posterSharedElementKey(streamType: StreamType, rowId: String, tmdbId: Int): String =
    "poster_${streamType.rawValue}_${rowId}_${tmdbId}"

// sharedElement()의 clipInOverlayDuringTransition 기본값(ParentClip)은 전환 중 overlay 레이어에서
// 렌더링될 때 조상 clip을 못 찾아 사각형으로 그려지며 모서리가 깜빡인다 — PosterCard와 동일한
// 라운드를 명시적으로 지정해 전환 중에도 유지되게 한다.
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.posterOverlayClip(): SharedTransitionScope.OverlayClip =
    OverlayClip(MaterialTheme.shapes.medium)

@Composable
fun MediaRow(
    titleRes: StringResource,
    minHeight: Dp,
    topPadding: Dp = 16.dp,
    bottomPadding: Dp = 16.dp,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onViewAllClick: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    Column(modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).alignByBaseline(),
            )

            if (null != onViewAllClick) {
                Text(
                    text = stringResource(Res.string.media_row_view_all),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onViewAllClick).alignByBaseline(),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            LoadingIndicator(modifier = Modifier.fillMaxWidth().heightIn(min = minHeight))
        } else if (isError) {
            MessageIndicator(
                text = stringResource(Res.string.paging_load_failed),
                modifier = Modifier.fillMaxWidth().heightIn(min = minHeight),
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(min = minHeight),
                content = content,
            )
        }
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MessageIndicator(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Home(History row + New Movies/Tv row + Watchlist row)/Movie/Tv 3개 화면이 동일하게 필요로 해서
// 공용화 — History의 sheet는 화면 하나에서만 쓰여서 화면-로컬로 남겨뒀던 것과는 다른 경우.
// extraContent는 History row에서만 "이력에서 삭제" 줄을 추가로 넣기 위한 슬롯.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistActionBottomSheet(
    isWatchlisted: Boolean,
    onWatchlistToggleClick: () -> Unit,
    onDismissRequest: () -> Unit,
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column {
            Text(
                text = stringResource(
                    if (isWatchlisted) Res.string.watchlist_remove else Res.string.watchlist_add
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onWatchlistToggleClick)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
            extraContent?.invoke(this)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterCard(
    imageUrl: String,
    title: String? = null,
    imageModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.width(POSTER_WIDTH)) {
        val context = LocalPlatformContext.current
        val imageRequest = remember(imageUrl) {
            ImageRequest.Builder(context).data(imageUrl).crossfade(true).build()
        }
        val cardModifier = if (null != onClick || null != onLongClick) {
            Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick)
        } else {
            Modifier
        }
        ElevatedCard(modifier = cardModifier) {
            AsyncImage(
                model = imageRequest,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(POSTER_WIDTH).height(POSTER_HEIGHT).then(imageModifier),
            )
        }
        if (null != title) {
            Spacer(modifier = Modifier.height(MEDIA_TITLE_SPACING))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // 언어별 폰트 fallback(한글/영문 혼용 등)으로 줄 높이가 미세하게 달라지면 Row 전체 높이가
                // 흔들려 하단 Row들의 y좌표가 같이 움직인다 — heightIn(min=)은 바닥만 고정할 뿐 실제
                // 렌더링이 그 값을 넘으면(fallback 폰트로 더 큰 line height) 여전히 흔들리므로,
                // 고정 height()로 모든 항목이 항상 동일한 높이를 갖도록 강제한다.
                modifier = Modifier.height(MEDIA_TITLE_LINE_HEIGHT),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackdropCard(
    imageUrl: String,
    contentDescription: String?,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    val context = LocalPlatformContext.current
    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context).data(imageUrl).crossfade(true).build()
    }
    val cardModifier = if (null != onClick || null != onLongClick) {
        Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick)
    } else {
        Modifier
    }
    ElevatedCard(modifier = cardModifier.width(BACKDROP_WIDTH)) {
        Box(modifier = Modifier.width(BACKDROP_WIDTH).height(BACKDROP_HEIGHT)) {
            AsyncImage(
                model = imageRequest,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            overlay()
        }
    }
}

@Composable
fun AvatarCard(imageUrl: String, contentDescription: String?, size: Dp = AVATAR_SIZE) {
    ElevatedCard(shape = CircleShape, modifier = Modifier.size(size)) {
        val context = LocalPlatformContext.current
        AsyncImage(
            model = remember(imageUrl) {
                ImageRequest.Builder(context).data(imageUrl).crossfade(true).build()
            },
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
