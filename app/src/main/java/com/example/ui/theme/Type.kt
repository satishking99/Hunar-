package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createHunarTypography(themeMode: AppThemeMode, highContrast: Boolean): Typography {
    // Primary text in Dark Navy Blue #0A1931 (replacing pure black #000000)
    val textPrimary = HunarNavy
    val textSecondary = HunarTextSecondaryLight
    val textMuted = HunarTextMutedLight

    val baseFontWeight = if (highContrast) FontWeight.Bold else FontWeight.SemiBold
    val bodyWeight = if (highContrast) FontWeight.Medium else FontWeight.Normal

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp,
            color = textPrimary
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.2).sp,
            color = textPrimary
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = baseFontWeight,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            color = textPrimary
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            color = textPrimary
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = baseFontWeight,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = textPrimary
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = textPrimary
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = bodyWeight,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = textPrimary
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = bodyWeight,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = textSecondary
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = bodyWeight,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = textMuted
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.5.sp,
            color = textPrimary
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = textSecondary
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = textMuted
        )
    )
}

val Typography = createHunarTypography(AppThemeMode.LIGHT, false)
