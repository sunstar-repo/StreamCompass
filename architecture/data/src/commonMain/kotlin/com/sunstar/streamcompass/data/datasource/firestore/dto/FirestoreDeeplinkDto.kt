package com.sunstar.streamcompass.data.datasource.firestore.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

@Serializable
internal data class FirestoreDeeplinkDto(
    val link: String = Constants.EMPTY_STRING,
    val videoLink: String = Constants.EMPTY_STRING,
    val lightThemeImage: String = Constants.EMPTY_STRING,
    val darkThemeImage: String = Constants.EMPTY_STRING,
)
