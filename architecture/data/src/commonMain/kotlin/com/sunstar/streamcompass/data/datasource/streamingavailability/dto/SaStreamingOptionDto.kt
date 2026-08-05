package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

//sashowdto/streamingoptions
@Serializable
internal data class SaStreamingOptionDto(
    val service: SaServiceInfoDto = SaServiceInfoDto(),
    val type: String = Constants.EMPTY_STRING,
    val addon: SaAddonDto = SaAddonDto(),
    val link: String = Constants.EMPTY_STRING,
    val videoLink: String = Constants.EMPTY_STRING,
    val quality: String = Constants.EMPTY_STRING,
    val audios: List<SaLocaleDto> = emptyList(),
    val subtitles: List<SaSubtitleDto> = emptyList(),
    val price: SaPriceDto = SaPriceDto(),
    val expiresSoon: Boolean = false,
    val expiresOn: Long = Constants.UNSET_LONG,
    val availableSince: Long = Constants.UNSET_LONG,
)
