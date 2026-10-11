package com.example.data.model

import com.example.R

/**
 * UserProfile data model representing a user's verified identity, skills,
 * achievements, and earnings to embody the merit-first philosophy of HUNAR.
 *
 * Philosophy:
 * "Mehnat aur Talent ko Attention, sirf Looks aur Glamour ko nahi."
 * Talent > Glamour • Data-Driven Accomplishments • Zero Vanity Clout.
 */
data class UserProfile(
    val id: String = "user_me",
    val name: String = "Arjun Patel",
    val handle: String = "@arjun_tech",
    val avatarEmoji: String = "👨‍💻",
    val avatarDrawableRes: Int? = R.drawable.img_arjun_avatar,
    val bio: String = "Building open-source robotics & clean code. Talent & hard work first. Zero vanity metrics.",
    val primarySkill: SkillCategory = SkillCategory.TECH,
    val verifiedSkillTitle: String = "Master Android & Robotics Architect",
    val isSkillVerified: Boolean = true,
    val talentMeritScore: Int = 96, // 0 - 100 merit score calibrated on deliverables & peer review
    val meritTier: String = "Master Artisan Tier • Top 1% Verified",
    val meritPhilosophy: String = "Mehnat aur Talent ko Attention, sirf Looks ko nahi",
    val rejectionOfVanityMotto: String = "Zero Vanity Metrics: No public like counters, no vanity follower clout",
    val skills: List<UserSkill> = emptyList(),
    val achievements: List<UserAchievement> = emptyList(),
    val earnings: UserEarningsSummary = UserEarningsSummary(),
    val deliverablesCompletedCount: Int = 24,
    val zeroDefectStreakDays: Int = 420,
    val slaComplianceRate: Float = 99.4f,
    val crashFreeRate: String = "99.98%",
    val interests: List<SkillCategory> = listOf(
        SkillCategory.TECH,
        SkillCategory.EDUCATION,
        SkillCategory.FITNESS,
        SkillCategory.ART
    )
)

/**
 * A verified skill entry emphasizing demonstrable competence over vanity claims.
 */
data class UserSkill(
    val id: String,
    val name: String,
    val category: SkillCategory,
    val masteryLevel: Int, // e.g. 96 for 96%
    val experienceYears: Float,
    val completedProjectsCount: Int,
    val peerEndorsementsCount: Int,
    val verificationBadge: String,
    val benchmarkProof: String,
    val keyStrengths: List<String> = emptyList()
)

/**
 * A concrete, audited achievement backed by measurable deliverables.
 */
data class UserAchievement(
    val id: String,
    val title: String,
    val description: String,
    val dateEarned: String,
    val category: SkillCategory,
    val metricProof: String,
    val badgeIconEmoji: String = "🏆",
    val verificationAuditId: String,
    val isNationalRecognition: Boolean = false
)

/**
 * Transparent earnings summary emphasizing money earned through real skill,
 * direct appreciation (Shabaashi), tutoring, and engineering grants.
 */
data class UserEarningsSummary(
    val totalEarningsRupees: Double = 24850.0,
    val withdrawableRupees: Double = 18450.0,
    val pendingClearanceRupees: Double = 6400.0,
    val skillTutoringRupees: Double = 9800.0,
    val directShabaashiTipsRupees: Double = 6450.0,
    val openSourceGrantsRupees: Double = 5200.0,
    val meritChallengePrizesRupees: Double = 3400.0,
    val upiId: String = "arjun.hunar@okhdfcbank",
    val verifiedTransactionsCount: Int = 38,
    val paymentStatus: String = "Verified Live Database Sync • UPI Direct"
)
