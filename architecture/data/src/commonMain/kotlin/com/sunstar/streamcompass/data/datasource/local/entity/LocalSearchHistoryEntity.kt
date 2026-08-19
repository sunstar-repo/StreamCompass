package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
internal data class LocalSearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long,
)
