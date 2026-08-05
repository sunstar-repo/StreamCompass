package com.sunstar.streamcompass.domain.model

data class Deeplink(
    val link: String,
    val videoLink: String,
    val tmdbId: Int,
    val streamType: StreamType,
    val locale: String,
    val service: String,
    val logo: Logo,
)
