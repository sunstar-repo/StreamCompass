package com.sunstar.streamcompass.presentation.detail.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.presentation.core.AVATAR_SIZE
import com.sunstar.streamcompass.presentation.core.AvatarCard
import kotlinx.coroutines.flow.Flow

@Composable
fun ReviewsList(reviewsFlow: Flow<PagingData<Review>>, modifier: Modifier = Modifier) {
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
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
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

private val REVIEW_BUBBLE_CORNER_RADIUS = 12.dp
private val REVIEW_BUBBLE_TAIL_WIDTH = 8.dp
private val REVIEW_BUBBLE_TAIL_HEIGHT = 12.dp
private val REVIEW_BUBBLE_TAIL_TOP_OFFSET = 10.dp
private const val REVIEW_CONTENT_MAX_LINES = 5
