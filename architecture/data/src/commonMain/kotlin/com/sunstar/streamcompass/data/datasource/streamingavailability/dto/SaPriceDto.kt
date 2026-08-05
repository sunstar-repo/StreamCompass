package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

//sashowdto/streamingoptions/price
@Serializable
internal data class SaPriceDto(
    val amount: String = Constants.EMPTY_STRING,
    val currency: String = Constants.EMPTY_STRING,
    val formatted: String = Constants.EMPTY_STRING,
)
