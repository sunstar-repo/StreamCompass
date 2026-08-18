package com.sunstar.streamcompass.presentation.core

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * 위쪽 헤더(poster 등)를 접었다 펼 수 있게 해주는 상태 — 헤더 자체를 직접 드래그하는 경우([scrollableState]를
 * `Modifier.scrollable`에 연결)와 아래 컨텐츠를 스크롤해서 헤더가 밀려 접히는 경우([nestedScrollConnection]을
 * `Modifier.nestedScroll`에 연결) 모두 같은 [offsetPx]를 공유한다. Box/Column/Row 등 컨테이너 종류와 무관하게
 * 필요한 노드에 각각의 Modifier만 붙이면 되므로, 헤더와 컨텐츠가 서로 다른(형제) composable이어도 재사용 가능하다.
 */
@Stable
class CollapsingHeaderState(maxCollapsePx: Float) {
    internal var maxCollapsePx: Float = maxCollapsePx

    var offsetPx by mutableFloatStateOf(0f)
        private set

    val scrollableState = ScrollableState { delta -> consume(delta) }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            return Offset(0f, consume(available.y))
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y <= 0f) return Offset.Zero
            return Offset(0f, consume(available.y))
        }
    }

    private fun consume(delta: Float): Float {
        val newOffsetPx = (offsetPx + delta).coerceIn(-maxCollapsePx, 0f)
        val consumed = newOffsetPx - offsetPx
        offsetPx = newOffsetPx
        return consumed
    }
}

@Composable
fun rememberCollapsingHeaderState(maxCollapsePx: Float): CollapsingHeaderState {
    val state = remember { CollapsingHeaderState(maxCollapsePx = maxCollapsePx) }
    state.maxCollapsePx = maxCollapsePx
    return state
}
