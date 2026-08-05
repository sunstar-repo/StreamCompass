package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

//sashowdto/streamingoptions/audios
@Serializable
internal data class SaLocaleDto(
    val language: String = Constants.EMPTY_STRING,
    val region: String = Constants.EMPTY_STRING,
)
