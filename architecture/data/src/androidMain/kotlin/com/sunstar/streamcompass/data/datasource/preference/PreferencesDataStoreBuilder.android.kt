package com.sunstar.streamcompass.data.datasource.preference

import android.content.Context
import org.koin.java.KoinJavaComponent.getKoin

internal actual fun getPreferencesDataStorePath(): String {
    val appContext = getKoin().get<Context>().applicationContext
    val file = appContext.filesDir.resolve("datastore/stream_compass_settings.preferences_pb")
    file.parentFile?.mkdirs()
    return file.absolutePath
}
