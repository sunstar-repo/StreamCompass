package com.sunstar.streamcompass.data.datasource.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

internal actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("user.home"), ".streamcompass/stream_compass.db")
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
}
