package com.example.data.model

data class Story(
    val id: String,
    val creator: User,
    val skillTag: String,
    val isLive: Boolean = false,
    val timeAgo: String = "45m",
    val isViewed: Boolean = false
)
