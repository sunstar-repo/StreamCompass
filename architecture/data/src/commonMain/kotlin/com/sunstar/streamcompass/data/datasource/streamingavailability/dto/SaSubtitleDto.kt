package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import kotlinx.serialization.Serializable

//sashowdto/streamingoptions/subtitles
@Serializable
internal data class SaSubtitleDto(
    val closedCaptions: Boolean = false,
    val locale: SaLocaleDto = SaLocaleDto(),
)
