package com.sunstar.streamcompass.data.datasource.local.converter

import androidx.room.TypeConverter
import com.sunstar.streamcompass.data.datasource.local.entity.LocalPerson
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

internal class PersonListConverter {
    @TypeConverter
    fun fromList(value: List<LocalPerson>): String =
        Json.encodeToString(ListSerializer(LocalPerson.serializer()), value)

    @TypeConverter
    fun toList(value: String): List<LocalPerson> =
        Json.decodeFromString(ListSerializer(LocalPerson.serializer()), value)
}
