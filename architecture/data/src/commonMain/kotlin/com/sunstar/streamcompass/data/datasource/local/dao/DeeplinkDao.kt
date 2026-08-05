package com.sunstar.streamcompass.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity

@Dao
internal interface DeeplinkDao {
    @Query("SELECT * FROM deeplink WHERE tmdbId = :tmdbId AND streamType = :streamType AND locale = :locale")
    suspend fun get(
        tmdbId: Int,
        streamType: String,
        locale: String,
    ): List<LocalDeeplinkEntity>

    @Query("DELETE FROM deeplink WHERE tmdbId = :tmdbId AND streamType = :streamType AND locale = :locale")
    suspend fun delete(
        tmdbId: Int,
        streamType: String,
        locale: String,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<LocalDeeplinkEntity>)

    @Transaction
    suspend fun replace(
        tmdbId: Int,
        streamType: String,
        locale: String,
        entities: List<LocalDeeplinkEntity>,
    ) {
        delete(tmdbId, streamType, locale)
        insertAll(entities)
    }
}
