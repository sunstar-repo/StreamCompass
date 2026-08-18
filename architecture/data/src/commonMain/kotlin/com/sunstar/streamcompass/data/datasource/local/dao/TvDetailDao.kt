package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity

@Dao
internal interface TvDetailDao {
    @Query("SELECT * FROM tv_detail WHERE tmdbId = :tmdbId AND locale = :locale")
    suspend fun get(
        tmdbId: Int,
        locale: String,
    ): LocalTvDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalTvDetailEntity)
}
