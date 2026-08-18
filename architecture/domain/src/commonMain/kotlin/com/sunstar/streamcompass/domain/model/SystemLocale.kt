package com.sunstar.streamcompass.domain.model

data class SystemLocale(
    val language: String,
    val country: String,
) {
    // 기존 language 파라미터들이 쓰던 "en-US" 같은 locale 형태 — language/country 조합으로 계산.
    val locale: String get() = "$language-$country"
}

expect fun currentSystemLocale(): SystemLocale
