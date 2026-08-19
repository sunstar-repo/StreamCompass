package com.sunstar.streamcompass.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.domain.model.ThemeMode
import com.sunstar.streamcompass.domain.usecase.GetThemeModeUseCase
import com.sunstar.streamcompass.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val setThemeModeUseCase: SetThemeModeUseCase,
    getThemeModeUseCase: GetThemeModeUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = merge(
            eventChannel.receiveAsFlow().onStart { emit(Event.Initialize) },
            getThemeModeUseCase().map { Event.ThemeModeChanged(mode = it) },
        )
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

    fun onItemClick(item: SettingItem) {
        viewModelScope.launch {
            eventChannel.send(Event.ItemClicked(item = item))
        }
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode = mode)
            eventChannel.send(Event.ThemeSheetDismissed)
        }
    }

    fun onThemeSheetDismissRequest() {
        viewModelScope.launch {
            eventChannel.send(Event.ThemeSheetDismissed)
        }
    }

    private fun handleEvent(current: State, event: Event): State = when (event) {
        Event.Initialize -> current

        is Event.ItemClicked -> when (event.item) {
            SettingItem.Theme -> current.copy(isThemeSheetShown = true)
        }

        is Event.ThemeModeChanged -> current.copy(themeMode = event.mode)

        Event.ThemeSheetDismissed -> current.copy(isThemeSheetShown = false)
    }

    sealed interface Event {
        data object Initialize : Event
        data class ItemClicked(val item: SettingItem) : Event
        data class ThemeModeChanged(val mode: ThemeMode) : Event
        data object ThemeSheetDismissed : Event
    }

    data class State(
        val items: List<SettingItem> = SettingItem.entries,
        val themeMode: ThemeMode = ThemeMode.System,
        val isThemeSheetShown: Boolean = false,
    )
}
