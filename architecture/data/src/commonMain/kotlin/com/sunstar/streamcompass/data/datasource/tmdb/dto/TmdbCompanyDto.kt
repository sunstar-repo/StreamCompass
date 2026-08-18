package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbCompanyDto(
    @SerialName("logo_path") private val _logoPath: String? = null,
) {
    val logoPath: String get() = _logoPath.orEmpty()
}
