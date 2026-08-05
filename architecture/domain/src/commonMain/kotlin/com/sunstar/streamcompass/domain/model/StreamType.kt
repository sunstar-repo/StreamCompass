package com.sunstar.streamcompass.domain.model

//deeplink/streamtype
sealed interface StreamType {
    data object Movie : StreamType

    data object Tv : StreamType
}
