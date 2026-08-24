package com.sunstar.streamcompass.data.datasource.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.sunstar.streamcompass.data.datasource.local.converter.IntListConverter
import com.sunstar.streamcompass.data.datasource.local.converter.PersonListConverter
import com.sunstar.streamcompass.data.datasource.local.converter.StringListConverter
import com.sunstar.streamcompass.data.datasource.local.dao.DeeplinkDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieWatchlistDao
import com.sunstar.streamcompass.data.datasource.local.dao.SearchHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvWatchlistDao
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieWatchlistEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalSearchHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvWatchlistEntity

@Database(
    entities = [
        LocalMovieDetailEntity::class,
        LocalTvDetailEntity::class,
        LocalDeeplinkEntity::class,
        LocalMovieHistoryEntity::class,
        LocalTvHistoryEntity::class,
        LocalSearchHistoryEntity::class,
        LocalMovieWatchlistEntity::class,
        LocalTvWatchlistEntity::class,
    ],
    version = 1,
)
@TypeConverters(StringListConverter::class, IntListConverter::class, PersonListConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDetailDao(): MovieDetailDao

    abstract fun tvDetailDao(): TvDetailDao

    abstract fun deeplinkDao(): DeeplinkDao

    abstract fun movieHistoryDao(): MovieHistoryDao

    abstract fun tvHistoryDao(): TvHistoryDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun movieWatchlistDao(): MovieWatchlistDao

    abstract fun tvWatchlistDao(): TvWatchlistDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
