package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TvDetailDao {
    @Query("SELECT * FROM tv_detail WHERE tmdbId = :tmdbId")
    fun observe(tmdbId: Int): Flow<LocalTvDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LocalTvDetailEntity)
}
