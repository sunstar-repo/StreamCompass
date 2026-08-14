package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TvHistoryDao {
    @Query("SELECT * FROM tv_history ORDER BY visitedAt DESC LIMIT 20")
    fun observeRecent(): Flow<List<LocalTvHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalTvHistoryEntity)

    @Query("DELETE FROM tv_history WHERE tmdbId = :tmdbId")
    suspend fun delete(tmdbId: Int)
}
