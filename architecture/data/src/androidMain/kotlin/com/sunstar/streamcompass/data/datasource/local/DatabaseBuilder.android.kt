package com.sunstar.streamcompass.data.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.java.KoinJavaComponent.getKoin

internal actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = getKoin().get<Context>().applicationContext
    val dbFile = appContext.getDatabasePath("stream_compass.db")
    return Room.databaseBuilder<AppDatabase>(context = appContext, name = dbFile.absolutePath)
}
