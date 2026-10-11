package com.example.data.model

data class Comment(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val avatarEmoji: String,
    val text: String,
    val timeAgo: String,
    val talentComplimentTag: String? = null,
    val likesCount: Int = 12,
    val isLiked: Boolean = false
)
