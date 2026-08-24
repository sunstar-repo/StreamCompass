package com.sunstar.streamcompass.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.usecase.GetSearchHistoryUseCase
import com.sunstar.streamcompass.domain.usecase.GetSearchStreamUseCase
import com.sunstar.streamcompass.domain.usecase.RecordSearchHistoryUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val getSearchStreamUseCase: GetSearchStreamUseCase,
    private val recordSearchHistoryUseCase: RecordSearchHistoryUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = eventChannel.receiveAsFlow()
            .onStart { emit(Event.Initialize) }
            .runningFold(
                initial = State(),
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = State()
            )
    }

    fun onQueryChanged(query: String) {
        viewModelScope.launch { eventChannel.send(Event.QueryChanged(query = query)) }
    }

    fun onSearchEditTextFocused() {
        viewModelScope.launch { eventChannel.send(Event.EditTextFocused) }
    }

    fun onSearchSubmitted(query: String) {
        viewModelScope.launch { eventChannel.send(Event.Submit(query = query)) }
    }

    fun onTabSelected(streamType: StreamType) {
        viewModelScope.launch { eventChannel.send(Event.TabSelected(streamType = streamType)) }
    }

    // 2-page(Results)에서 검색창을 다시 탭해 1-page(History)가 뜬 상태일 때만 호출됨 — 새 검색 없이
    // 그 History 오버레이만 걷어내고 바로 아래 유지되고 있던 Results로 되돌아간다(재검색 없음).
    fun onHistoryOverlayDismissed() {
        viewModelScope.launch { eventChannel.send(Event.DismissHistoryOverlay) }
    }

    private fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(history = getSearchHistoryUseCase())

        is Event.QueryChanged -> current.copy(queryText = event.query)

        is Event.EditTextFocused -> current.copy(page = Page.History)

        is Event.Submit -> {
            val query = event.query.trim()
            if (query.isEmpty()) {
                current
            } else {
                viewModelScope.launch { recordSearchHistoryUseCase(query = query) }
                current.copy(
                    queryText = query,
                    page = Page.Results,
                    activeQuery = query,
                    movieResults = getSearchStreamUseCase(
                        query = query,
                        streamType = StreamType.Movie
                    )
                        .cachedIn(viewModelScope),
                    tvResults = getSearchStreamUseCase(query = query, streamType = StreamType.Tv)
                        .cachedIn(viewModelScope),
                )
            }
        }

        is Event.TabSelected -> current.copy(selectedTab = event.streamType)

        Event.DismissHistoryOverlay -> {
            val activeQuery = current.activeQuery
            if (null != activeQuery) {
                current.copy(page = Page.Results, queryText = activeQuery)
            } else {
                current
            }
        }
    }

    sealed interface Page {
        data object History : Page
        data object Results : Page
    }

    sealed interface Event {
        data object Initialize : Event
        data class QueryChanged(val query: String) : Event
        data object EditTextFocused : Event
        data class Submit(val query: String) : Event
        data class TabSelected(val streamType: StreamType) : Event
        data object DismissHistoryOverlay : Event
    }

    data class State(
        val queryText: String = "",
        val page: Page = Page.History,
        // Results(2-page)로 한 번이라도 진입한 뒤 검색창을 다시 탭해 History가 오버레이된 상태인지 판별하는 값 —
        // null이 아니면 History 오버레이를 걷어낼 때 이 값으로 되돌아갈 Results가 아직 살아있다는 뜻.
        val activeQuery: String? = null,
        val history: Flow<List<String>> = emptyFlow(),
        val selectedTab: StreamType = StreamType.Movie,
        val movieResults: Flow<PagingData<Stream>> = emptyFlow(),
        val tvResults: Flow<PagingData<Stream>> = emptyFlow(),
    )
}
