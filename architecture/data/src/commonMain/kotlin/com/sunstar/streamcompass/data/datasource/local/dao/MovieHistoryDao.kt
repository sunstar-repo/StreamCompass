package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MovieHistoryDao {
    @Query("SELECT * FROM movie_history ORDER BY visitedAt DESC LIMIT 20")
    fun observeRecent(): Flow<List<LocalMovieHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalMovieHistoryEntity)

    @Query("DELETE FROM movie_history WHERE tmdbId = :tmdbId")
    suspend fun delete(tmdbId: Int)
}
