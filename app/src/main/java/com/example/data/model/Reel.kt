package com.example.data.model

data class Reel(
    val id: String,
    val creator: User,
    val title: String,
    val caption: String,
    val skillCategory: SkillCategory,
    val drawableRes: Int? = null,
    val skillScore: Int = 96,
    val watchTimeRatio: Double = 0.94,
    val watchTimeFormatted: String = "4m 12s",
    val viewsCount: String = "84.2K",
    val likesCount: Int = 6840,
    val commentsCount: Int = 342,
    val tipsRupees: Int = 2450,
    val soundTrackTitle: String = "Original Performance • Live Acoustic",
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val verifiedSkillHighlight: String = "Live Violin Raga Bhairav (Self Choreography)",
    val dressCodeStatus: String = "Passed • Modest attire verified",
    val lipSyncStatus: String = "Passed • Real Instrument Play (Not Lip-sync)"
)
