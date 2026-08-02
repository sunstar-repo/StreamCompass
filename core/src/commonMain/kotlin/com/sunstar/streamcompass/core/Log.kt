package com.sunstar.streamcompass.core

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object Log {

    enum class Level(val symbol: Char) {
        VERBOSE('V'),
        DEBUG('D'),
        INFO('I'),
        WARN('W'),
        ERROR('E'),
    }

    fun v(tag: String, message: String) = log(Level.VERBOSE, tag, message)

    fun d(tag: String, message: String) = log(Level.DEBUG, tag, message)

    fun i(tag: String, message: String) = log(Level.INFO, tag, message)

    fun w(tag: String, message: String) = log(Level.WARN, tag, message)

    fun e(tag: String, message: String) = log(Level.ERROR, tag, message)

    private fun log(
        level: Level,
        tag: String,
        message: String,
    ) {
        println("${timestamp()} ${level.symbol}/$tag: $message")
    }

    @OptIn(ExperimentalTime::class)
    private fun timestamp(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${now.monthNumber.pad(2)}-${now.dayOfMonth.pad(2)} " +
            "${now.hour.pad(2)}:${now.minute.pad(2)}:${now.second.pad(2)}.${(now.nanosecond / 1_000_000).pad(3)}"
    }

    private fun Int.pad(length: Int): String = toString().padStart(length, '0')
}
