package com.example.data.model

/**
 * Status of a portfolio project.
 */
enum class ProjectStatus(val displayName: String, val badgeColorHex: Long) {
    PRODUCTION_DEPLOYED("Production Deployed", 0xFF22C55E),
    OPEN_SOURCE_CORE("Open Source Core", 0xFF0A1931),
    CLIENT_ENTERPRISE("Enterprise Client", 0xFFFF6B00),
    BENCHMARK_VERIFIED("Benchmark Verified", 0xFF8B5CF6)
}

/**
 * A concrete, data-driven measurable metric.
 * Prioritizes verifiable technical/functional performance over vanity metrics.
 */
data class MeasurableMetric(
    val label: String,
    val value: String,
    val context: String,
    val isPositive: Boolean = true
)

/**
 * A verifiable project in the user's portfolio.
 */
data class ProjectPortfolio(
    val id: String,
    val title: String,
    val tagline: String,
    val category: SkillCategory,
    val timeline: String,
    val status: ProjectStatus,
    val role: String,
    val teamSize: Int,
    val impactMetrics: List<MeasurableMetric>,
    val problemStatement: String,
    val architecturalSolution: String,
    val techStack: List<String>,
    val deliverables: List<String>,
    val verifiableAuditId: String,
    val repoOrLiveUrl: String = "https://github.com/hunar-verified/project",
    val peerReviewRating: Float = 4.95f,
    val clientOrPeerQuote: String = "Engineered with exceptional discipline and verified zero-defect deployment.",
    val isFeatured: Boolean = true
)

/**
 * A skill proficiency entry backed by verifiable project history and peer reviews.
 */
data class SkillProficiency(
    val id: String,
    val name: String,
    val category: SkillCategory,
    val masteryPercentage: Int, // e.g. 94 for 94%
    val yearsExperience: Float,
    val projectsCompleted: Int,
    val verifiedEndorsements: Int,
    val auditedArtifactsCount: Int,
    val keyStrengths: List<String>,
    val benchmarkHighlight: String,
    val verificationId: String
)

/**
 * Overall summary prioritizing data-driven accomplishments over vanity metrics.
 */
data class DataDrivenAccomplishmentsSummary(
    val totalProductionDeployments: Int = 24,
    val verifiedSkillsCount: Int = 8,
    val averageSlaAdherence: Float = 99.4f,
    val codeReviewAcceptanceRate: Float = 98.2f,
    val totalClientValueRupees: String = "₹48.5 Lakhs",
    val zeroCriticalDefectsStreakDays: Int = 420,
    val crashFreeRate: String = "99.98%",
    val averageTestCoverage: String = "96.4%",
    val peerMeritScore: Int = 95
)

/**
 * A peer endorsement linked to concrete project deliverables.
 */
data class SkillEndorsement(
    val id: String,
    val endorserName: String,
    val endorserRole: String,
    val skillName: String,
    val date: String,
    val comment: String
)
