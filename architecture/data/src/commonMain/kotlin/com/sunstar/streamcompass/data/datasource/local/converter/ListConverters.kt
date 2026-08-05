package com.sunstar.streamcompass.data.datasource.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

internal class StringListConverter {
    @TypeConverter
    fun fromList(value: List<String>): String =
        Json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun toList(value: String): List<String> =
        Json.decodeFromString(ListSerializer(String.serializer()), value)
}

internal class IntListConverter {
    @TypeConverter
    fun fromList(value: List<Int>): String =
        Json.encodeToString(ListSerializer(Int.serializer()), value)

    @TypeConverter
    fun toList(value: String): List<Int> =
        Json.decodeFromString(ListSerializer(Int.serializer()), value)
}
