package com.sunstar.streamcompass.data.datasource.preference

import java.io.File

internal actual fun getPreferencesDataStorePath(): String {
    val file = File(
        System.getProperty("user.home"),
        ".streamcompass/stream_compass_settings.preferences_pb"
    )
    file.parentFile?.mkdirs()
    return file.absolutePath
}
