package com.sunstar.streamcompass.presentation.main

import com.sunstar.streamcompass.presentation.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
sealed class MainDestination(
    override val label: String,
    override val icon: String,
) : Destination {
    @Serializable
    data object HomeDestination : MainDestination(label = "Home", icon = "🏠")

    @Serializable
    data object MovieDestination : MainDestination(label = "Movie", icon = "🎬")

    @Serializable
    data object TvDestination : MainDestination(label = "Tv", icon = "📺")

    @Serializable
    data object SettingDestination : MainDestination(label = "Setting", icon = "⚙️")

    @Serializable
    data class StreamDetailDestination(val tmdbId: Int) : MainDestination(label = "StreamDetail", icon = "🎬")

    @Serializable
    data object SearchDestination : MainDestination(label = "Search", icon = "🔍")

    companion object {
        fun values(): List<MainDestination> =
            listOf(HomeDestination, MovieDestination, TvDestination, SettingDestination)
    }
}
