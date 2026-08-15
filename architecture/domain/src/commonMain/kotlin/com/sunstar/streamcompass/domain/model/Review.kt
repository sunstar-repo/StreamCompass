package com.sunstar.streamcompass.domain.model

data class Review(
    val id: String,
    val authorName: String,
    val avatarPath: String,
    val content: String,
    val createdAt: String,
)
