package com.example.data.model

data class User(
    val id: String,
    val name: String,
    val handle: String,
    val avatarEmoji: String = "👤",
    val primarySkill: SkillCategory,
    val talentScore: Int, // 0 - 100 merit score
    val verifiedSkillTitle: String = "Skill Verified",
    val bio: String = "Creating practical solutions with code & craft. Talent > Glamour.",
    val isSkillVerified: Boolean = true,
    val strikesCount: Int = 0,
    val isUploadBanned: Boolean = false,
    val banRemainingHours: Int = 0,
    val followersCount: String = "42.3K",
    val followingCount: String = "182",
    val totalLikesCount: String = "184.6K",
    val isFollowing: Boolean = false,
    val totalEarningsGenerated: Double = 12450.0,
    val avatarDrawableRes: Int? = null,
    val interests: List<SkillCategory> = listOf(
        SkillCategory.TECH,
        SkillCategory.EDUCATION,
        SkillCategory.FITNESS,
        SkillCategory.ART
    )
)
