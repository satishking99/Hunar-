package com.example.data.model

data class StrikeStatus(
    val currentStrikes: Int = 0, // 0 = Safe, 1 = 24h ban, 2 = 7d ban, 3 = Permanent (+) upload lock
    val banExpiryFormatted: String? = null,
    val canUpload: Boolean = true,
    val isPermanentlyBlocked: Boolean = false,
    val violations: List<ViolationRecord> = emptyList()
)

data class ViolationRecord(
    val id: String,
    val timestamp: String,
    val ruleBroken: String, // "RULE 1: Inappropriate Dress Code", "RULE 3: Plain Lip-Sync without Skill"
    val consequence: String,
    val strikeNumber: Int,
    val status: String // "Enforced", "Appeal Under Review"
)
