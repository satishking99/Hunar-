package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ========================================================
// HUNAR - Official Brand Colors & Semantic Accessibility Tokens
// Theme: Full WHITE THEME (Pure White #FFFFFF from top to bottom)
// Accent Colors:
// - HUNAR Orange (#FF6B00) for buttons, active Home icon, LIVE badges
// - Dark Navy Blue (#0A1931) replaces all pure black (#000000)
// - Gradient Navy Blue (#0A1931 to #1A2E4A) for Golden Content Rules banner
// - Light Grey (#F4F4F6) for cards, category chips & inputs
// ========================================================

// 1. Core Brand Colors
val HunarOrange = Color(0xFFFF6B00)          // #FF6B00 HUNAR Orange accent
val HunarOrangeDark = Color(0xFFE05D00)
val HunarOrangeLight = Color(0xFFFF8533)
val HunarAmber = Color(0xFFFF9800)
val HunarGold = Color(0xFFFFB703)

// 2. Dark Navy Blue (#0A1931) - Replaces all pure black
val HunarNavy = Color(0xFF0A1931)            // #0A1931 Dark Navy Blue
val HunarNavyLight = Color(0xFF1A2E4A)       // #1A2E4A Gradient companion for Golden Rules banner
val HunarNavyMuted = Color(0xFF243B5A)
val HunarNavyBorder = Color(0xFF1E3557)

// 3. Card & Surface Tokens
val HunarPureWhite = Color(0xFFFFFFFF)       // Pure White #FFFFFF
val HunarCardLightGrey = Color(0xFFF4F4F6)   // Light Grey for cards & chips
val HunarCardLightGreyBorder = Color(0xFFE5E5EA)

// Status & Action Colors
val HunarSuccess = Color(0xFF22C55E)
val HunarWarning = Color(0xFFF59E0B)
val HunarError = Color(0xFFEF4444)

// Backwards compatibility mappings - replaced pure black with Dark Navy Blue
val HunarBlack = Color(0xFF0A1931)
val HunarBlackDeep = Color(0xFF0A1931)
val HunarDarkSurface = Color(0xFF0A1931)
val HunarDarkCard = Color(0xFF132440)
val HunarDarkCardElevated = Color(0xFF1A2E4A)
val HunarDarkBorder = Color(0xFF233B5D)
val HunarBorder = Color(0xFFE5E5EA)
val HunarWhite = Color(0xFFFFFFFF)

// Text tokens - All text in Dark Navy Blue or subtle Navy variants (no pure black)
val HunarTextPrimary = Color(0xFF0A1931)
val HunarTextSecondary = Color(0xFF4A5D78)
val HunarTextMuted = Color(0xFF7A8CA4)

val HunarTextPrimaryLight = Color(0xFF0A1931)
val HunarTextSecondaryLight = Color(0xFF4A5D78)
val HunarTextMutedLight = Color(0xFF7A8CA4)

// Surface and background fallbacks
val HunarCreamBg = Color(0xFFFFFFFF)
val HunarSurfaceLight = Color(0xFFFFFFFF)
val HunarCardLight = Color(0xFFF4F4F6)
val HunarBorderLight = Color(0xFFE5E5EA)

val HunarComfortBg = Color(0xFFFFFFFF)
val HunarComfortSurface = Color(0xFFFFFFFF)
val HunarComfortCard = Color(0xFFF4F4F6)
val HunarComfortBorder = Color(0xFFE5E5EA)
val HunarComfortTextPrimary = Color(0xFF0A1931)
val HunarComfortTextSecondary = Color(0xFF4A5D78)
val HunarComfortAccent = Color(0xFFFF6B00)

val HunarSafeGreen = Color(0xFF2E7D4E)
val HunarCalmWarning = Color(0xFFB87823)
val HunarGentleError = Color(0xFFBA4545)
val HunarSoftBlue = Color(0xFF1A2E4A)
val HunarSoftBlueBg = Color(0xFFF0F4FA)
val HunarSoftGreen = Color(0xFF387352)
val HunarSoftGreenBg = Color(0xFFE7F2EB)
val HunarGentleLavender = Color(0xFF63567D)
val HunarWarmHoney = Color(0xFFFF6B00)
val HunarWarmHoneyBg = Color(0xFFFFF0E5)
