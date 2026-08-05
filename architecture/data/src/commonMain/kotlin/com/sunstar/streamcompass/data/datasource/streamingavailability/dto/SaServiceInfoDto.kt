package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

//sashowdto/streamingoptions/service
@Serializable
internal data class SaServiceInfoDto(
    val id: String = Constants.EMPTY_STRING,
    val name: String = Constants.EMPTY_STRING,
    val homePage: String = Constants.EMPTY_STRING,
    val themeColorCode: String = Constants.EMPTY_STRING,
    val imageSet: SaServiceImageSetDto = SaServiceImageSetDto(),
)
