package com.sunstar.streamcompass.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.usecase.GetStreamDetailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_detail
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_recommended
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_review

class DetailViewModel(
    private val tmdbId: Int,
    private val getStreamDetailUseCase: GetStreamDetailUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = eventChannel.receiveAsFlow()
            .onStart {
                emit(Event.Initialize)
            }
            .runningFold(
                initial = State.Loading,
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = State.Loading
            )
    }

    fun onTabSelected(tab: Tab) {
        viewModelScope.launch {
            eventChannel.send(Event.SelectTab(tab = tab))
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> State.Succeed(
            streamDetail = getStreamDetailUseCase(tmdbId = tmdbId, locale = DEFAULT_LOCALE),
        )

        is Event.SelectTab -> when (current) {
            is State.Succeed -> current.copy(selectedTab = event.tab)
            State.Loading -> current
        }
    }

    sealed class Tab {
        abstract val label: StringResource

        data object Detail : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_detail
        }

        data object Recommended : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_recommended
        }

        data object Review : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_review
        }

        companion object {
            fun values(): List<Tab> = listOf(Detail, Recommended, Review)
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class SelectTab(val tab: Tab) : Event
    }

    sealed interface State {
        data object Loading : State
        data class Succeed(
            val streamDetail: MovieStreamDetail,
            val selectedTab: Tab = Tab.Detail,
        ) : State
    }

    private companion object {
        const val DEFAULT_LOCALE = "en-US"
    }
}
