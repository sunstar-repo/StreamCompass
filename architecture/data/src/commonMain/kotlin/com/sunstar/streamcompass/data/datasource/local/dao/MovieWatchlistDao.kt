package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieWatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MovieWatchlistDao {
    @Query("SELECT * FROM movie_watchlist ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<LocalMovieWatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalMovieWatchlistEntity)

    @Query("DELETE FROM movie_watchlist WHERE tmdbId = :tmdbId")
    suspend fun delete(tmdbId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM movie_watchlist WHERE tmdbId = :tmdbId)")
    suspend fun exists(tmdbId: Int): Boolean
}
