package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalSearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 20")
    fun observeRecent(): Flow<List<LocalSearchHistoryEntity>>

    // REPLACE + query가 PrimaryKey라 같은 검색어를 다시 검색하면 새 searchedAt으로 갱신되어 자연스럽게 최신순 맨 위로 올라온다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalSearchHistoryEntity)
}
