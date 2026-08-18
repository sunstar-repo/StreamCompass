package com.sunstar.streamcompass.data.datasource.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class LocalPerson(
    val name: String,
    val role: String,
    val profilePath: String,
)
