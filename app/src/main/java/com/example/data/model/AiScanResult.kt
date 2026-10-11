package com.example.data.model

data class AiScanResult(
    val passed: Boolean,
    val ruleTriggered: String? = null,
    val details: String,
    val strikeNumberApplied: Int = 0,
    val isPermanentlyBlocked: Boolean = false,
    val zeroEarningFlagged: Boolean = false,
    val talentScore: Int = 95
)

enum class UploadTestScenario {
    COMPLIANT_SKILL,
    RULE_1_DRESS_CODE_VIOLATION,
    RULE_3_PLAIN_LIPSYNC
}
