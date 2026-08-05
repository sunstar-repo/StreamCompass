package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity

@Entity(tableName = "deeplink", primaryKeys = ["tmdbId", "streamType", "locale", "service"])
internal data class LocalDeeplinkEntity(
    val tmdbId: Int,
    val streamType: String,
    val locale: String,
    val service: String,
    val link: String,
    val videoLink: String,
    val lightThemeImage: String,
    val darkThemeImage: String,
)
