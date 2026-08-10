package com.sunstar.streamcompass.presentation.streamdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StreamDetailScreen(
    tmdbId: Int,
    viewModel: StreamDetailViewModel = koinViewModel(parameters = { parametersOf(tmdbId) }),
) {
    val state by viewModel.stateFlow.collectAsState()

    when (val current = state) {
        StreamDetailViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is StreamDetailViewModel.State.Succeed -> {
            StreamDetailContent(streamDetail = current.streamDetail)
        }
    }
}

@Composable
private fun StreamDetailContent(streamDetail: MovieStreamDetail) {
    var showStreamingOptionSheet by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Detail", "Recommended", "Review")

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
                onClick = { showStreamingOptionSheet = true },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = maxHeight * BACKDROP_BUTTON_POSITION_RATIO),
            ) {
                Text(text = "시청 옵션 보기")
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) },
                )
            }
        }

        when (selectedTabIndex) {
            0 -> Text(
                text = streamDetail.overview,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            1 -> Text(
                text = "추천 정보는 추후 연동됩니다",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            else -> Text(
                text = "리뷰 정보는 추후 연동됩니다",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }

    if (showStreamingOptionSheet) {
        StreamingOptionBottomSheet(
            deeplinks = streamDetail.deeplinks,
            onDismissRequest = { showStreamingOptionSheet = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamingOptionBottomSheet(deeplinks: List<Deeplink>, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (deeplinks.isEmpty()) {
                Text(text = "이용 가능한 스트리밍 서비스가 없습니다")
            } else {
                deeplinks.forEach { deeplink ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val context = LocalPlatformContext.current
                        AsyncImage(
                            model = remember(deeplink.logo.lightThemeImage) {
                                ImageRequest.Builder(context)
                                    .data(deeplink.logo.lightThemeImage)
                                    .crossfade(true)
                                    .build()
                            },
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
