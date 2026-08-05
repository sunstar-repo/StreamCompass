package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity

@Dao
internal interface MovieDetailDao {
    @Query("SELECT * FROM movie_detail WHERE tmdbId = :tmdbId AND locale = :locale")
    suspend fun get(
        tmdbId: Int,
        locale: String,
    ): LocalMovieDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalMovieDetailEntity)
}
