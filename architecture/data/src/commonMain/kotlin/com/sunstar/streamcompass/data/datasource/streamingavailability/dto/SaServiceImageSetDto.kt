package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

//sashowdto/streamingoptions/service/imageset
@Serializable
internal data class SaServiceImageSetDto(
    val lightThemeImage: String = Constants.EMPTY_STRING,
    val darkThemeImage: String = Constants.EMPTY_STRING,
    val whiteImage: String = Constants.EMPTY_STRING,
)
