package com.sunstar.streamcompass.data.datasource.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.sunstar.streamcompass.data.datasource.local.converter.IntListConverter
import com.sunstar.streamcompass.data.datasource.local.converter.StringListConverter
import com.sunstar.streamcompass.data.datasource.local.dao.DeeplinkDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvDetailDao
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity

@Database(
    entities = [
        LocalMovieDetailEntity::class,
        LocalTvDetailEntity::class,
        LocalDeeplinkEntity::class,
    ],
    version = 1,
)
@TypeConverters(StringListConverter::class, IntListConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDetailDao(): MovieDetailDao

    abstract fun tvDetailDao(): TvDetailDao

    abstract fun deeplinkDao(): DeeplinkDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
