package com.sunstar.streamcompass

import android.app.Application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

private object FirebaseConstants {
    const val APPLICATION_ID = "1:913485225427:android:4df08cef4e118c6927e326"
    const val API_KEY = "AIzaSyBlYXQDPsMv2lU3z3rsf9uN-UYjHdpRc9w"
    const val PROJECT_ID = "streamcompass-c0d49"
}

actual fun initializeFirebasePlatform() {
    FirebasePlatform.initializeFirebasePlatform(
        object : FirebasePlatform() {
            private val storage = mutableMapOf<String, String>()
            override fun store(key: String, value: String) = storage.set(key, value)
            override fun retrieve(key: String) = storage[key]
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)
        },
    )

    Firebase.initialize(
        context = Application(),
        options = FirebaseOptions(
            applicationId = FirebaseConstants.APPLICATION_ID,
            apiKey = FirebaseConstants.API_KEY,
            projectId = FirebaseConstants.PROJECT_ID,
        ),
    )
}
