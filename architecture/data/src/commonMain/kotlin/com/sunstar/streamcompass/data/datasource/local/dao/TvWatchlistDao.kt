package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvWatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TvWatchlistDao {
    @Query("SELECT * FROM tv_watchlist ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<LocalTvWatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalTvWatchlistEntity)

    @Query("DELETE FROM tv_watchlist WHERE tmdbId = :tmdbId")
    suspend fun delete(tmdbId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM tv_watchlist WHERE tmdbId = :tmdbId)")
    suspend fun exists(tmdbId: Int): Boolean
}
