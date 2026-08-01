package com.sunstar.streamcompass

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
