package com.sunstar.streamcompass.domain.model

import java.util.Locale

actual fun currentSystemLocale(): SystemLocale {
    val locale = Locale.getDefault()
    return SystemLocale(
        language = locale.language.ifEmpty { FALLBACK_LANGUAGE_CODE },
        country = locale.country.ifEmpty { FALLBACK_COUNTRY_CODE },
    )
}

private const val FALLBACK_LANGUAGE_CODE = "en"
private const val FALLBACK_COUNTRY_CODE = "US"
