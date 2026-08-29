package com.sunstar.streamcompass

/**
 * Firebase SDK(GitLive)를 사용하기 전에 플랫폼별로 필요한 초기화를 수행한다.
 *
 * Android는 google-services.json + Firebase Android SDK가 앱 프로세스 시작 시
 * FirebaseInitProvider(ContentProvider)를 통해 FirebaseApp을 자동으로 초기화하므로
 * no-op으로 둔다. JVM(Desktop)은 이 자동 초기화 경로가 없어 firebase-java-sdk의
 * FirebasePlatform 등록 + Firebase.initialize(...)를 직접 호출해야 한다.
 */
expect fun initializeFirebasePlatform()
