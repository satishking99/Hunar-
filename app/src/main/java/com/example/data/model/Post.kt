package com.example.data.model

data class Post(
    val id: String,
    val creator: User,
    val title: String,
    val caption: String,
    val skillCategory: SkillCategory,
    val mediaType: MediaType = MediaType.VIDEO,
    val visualPlaceholderBgHex: Long = 0xFF1A1A24,
    val drawableRes: Int? = null,
    val skillScore: Int = 94,
    val watchTimeRatio: Double = 0.89,
    val watchTimeHours: String = "312h",
    val viewsCount: String = "48.2K",
    val likesCount: Int = 3420,
    val commentsCount: Int = 184,
    val sharesCount: Int = 512,
    val earningsGenerated: Double = 4250.0,
    val tipsRupees: Int = 1250,
    val audioTitle: String = "Original Skill Audio • Live Demo",
    val timeAgo: String = "2h ago",
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val dressCodePassed: Boolean = true,
    val noPlainLipSyncPassed: Boolean = true,
    val isZeroEarningPenalty: Boolean = false,
    val aiAuditDetails: String = "AI Audit: 100% Modest Clothing • Verified Talent Choreography • High Watch Time Retention"
)

enum class MediaType {
    VIDEO, PHOTO
}
