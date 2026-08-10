package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity

@Entity(tableName = "deeplink", primaryKeys = ["tmdbId", "streamType", "country", "service"])
internal data class LocalDeeplinkEntity(
    val streamType: String,
    val tmdbId: Int,
    val country: String,
    val service: String,
    val link: String,
    val videoLink: String,
    val lightThemeImage: String,
    val darkThemeImage: String,
)
