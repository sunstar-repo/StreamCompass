package com.sunstar.streamcompass.domain.model

sealed interface StreamListType {
    data object NowPlaying : StreamListType

    data object Popular : StreamListType

    data object TopRated : StreamListType

    data object Upcoming : StreamListType
}
