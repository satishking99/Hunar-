package com.example.data.model

data class BrandCollab(
    val id: String,
    val brandName: String,
    val brandCategory: String,
    val campaignTitle: String,
    val rewardRupees: Int,
    val requiredSkill: SkillCategory,
    val minTalentScore: Int,
    val description: String,
    val slotsRemaining: Int,
    val isApplied: Boolean = false
)
