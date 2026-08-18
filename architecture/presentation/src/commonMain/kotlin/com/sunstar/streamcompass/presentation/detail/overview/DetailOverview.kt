package com.sunstar.streamcompass.presentation.detail.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.sunstar.streamcompass.domain.model.Deeplink
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_no_streaming_options
import streamcompass.architecture.presentation.generated.resources.stream_detail_view_streaming_options

@Composable
fun DetailOverview(
    title: String,
    deeplinks: List<Deeplink>,
    modifier: Modifier = Modifier,
) {
    var isSheetShown by remember { mutableStateOf(false) }

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
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (deeplinks.isEmpty()) {
                Text(
                    text = stringResource(Res.string.stream_detail_no_streaming_options),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            } else {
                Button(onClick = { isSheetShown = true }) {
                    Text(text = stringResource(Res.string.stream_detail_view_streaming_options))
                }
            }
        }
    }

    if (isSheetShown) {
        DetailStreamingOptionBottomSheet(
            deeplinks = deeplinks,
            onDismissRequest = { isSheetShown = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailStreamingOptionBottomSheet(deeplinks: List<Deeplink>, onDismissRequest: () -> Unit) {
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
