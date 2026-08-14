package com.sunstar.streamcompass.presentation.main

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.destination_home
import streamcompass.architecture.presentation.generated.resources.destination_movie
import streamcompass.architecture.presentation.generated.resources.destination_search
import streamcompass.architecture.presentation.generated.resources.destination_setting
import streamcompass.architecture.presentation.generated.resources.destination_stream_detail
import streamcompass.architecture.presentation.generated.resources.destination_tv
import streamcompass.architecture.presentation.generated.resources.ic_home
import streamcompass.architecture.presentation.generated.resources.ic_movie
import streamcompass.architecture.presentation.generated.resources.ic_search
import streamcompass.architecture.presentation.generated.resources.ic_settings
import streamcompass.architecture.presentation.generated.resources.ic_tv

@Serializable
sealed class MainDestination {
    abstract val label: StringResource
    abstract val icon: DrawableResource
    abstract val isNavigationUsed: Boolean
    abstract val isTopBarUsed: Boolean

    @Serializable
    data object HomeDestination : MainDestination() {
        override val label: StringResource get() = Res.string.destination_home
        override val icon: DrawableResource get() = Res.drawable.ic_home
        override val isNavigationUsed: Boolean get() = true
        override val isTopBarUsed: Boolean get() = true
    }

    @Serializable
    data object MovieDestination : MainDestination() {
        override val label: StringResource get() = Res.string.destination_movie
        override val icon: DrawableResource get() = Res.drawable.ic_movie
        override val isNavigationUsed: Boolean get() = true
        override val isTopBarUsed: Boolean get() = true
    }

    @Serializable
    data object TvDestination : MainDestination() {
        override val label: StringResource get() = Res.string.destination_tv
        override val icon: DrawableResource get() = Res.drawable.ic_tv
        override val isNavigationUsed: Boolean get() = true
        override val isTopBarUsed: Boolean get() = true
    }

    @Serializable
    data object SettingDestination : MainDestination() {
        override val label: StringResource get() = Res.string.destination_setting
        override val icon: DrawableResource get() = Res.drawable.ic_settings
        override val isNavigationUsed: Boolean get() = true
        override val isTopBarUsed: Boolean get() = true
    }

    @Serializable
    data class DetailDestination(val tmdbId: Int) : MainDestination() {
        override val label: StringResource get() = Res.string.destination_stream_detail
        override val icon: DrawableResource get() = Res.drawable.ic_movie
        override val isNavigationUsed: Boolean get() = false
        override val isTopBarUsed: Boolean get() = false
    }

    @Serializable
    data object SearchDestination : MainDestination() {
        override val label: StringResource get() = Res.string.destination_search
        override val icon: DrawableResource get() = Res.drawable.ic_search
        override val isNavigationUsed: Boolean get() = false
        override val isTopBarUsed: Boolean get() = false
    }

    companion object {
        fun values(): List<MainDestination> =
            listOf(HomeDestination, MovieDestination, TvDestination, SettingDestination)

        fun allValues(): List<MainDestination> =
            values() + listOf(SearchDestination, DetailDestination(tmdbId = 0))
    }
}
