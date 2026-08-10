package com.sunstar.streamcompass.domain.model

data class Deeplink(
    val streamType: StreamType,
    val tmdbId: Int,
    val locale: String,
    val service: String,
    val logo: Logo,
    val link: String,
    val videoLink: String,
)
