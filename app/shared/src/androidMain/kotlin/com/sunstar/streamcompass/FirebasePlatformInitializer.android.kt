package com.sunstar.streamcompass

/**
 * Android는 google-services.json + Firebase Android SDK가 앱 프로세스 시작 시
 * FirebaseInitProvider(ContentProvider)를 통해 FirebaseApp을 자동으로 초기화하므로
 * 별도 처리가 필요 없다.
 */
actual fun initializeFirebasePlatform() = Unit
