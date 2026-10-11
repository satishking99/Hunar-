package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * HUNAR Talent First App Theme:
 *
 * Requirements:
 * 1. Page 1 to 4 full WHITE THEME - background pure white #FFFFFF from top to bottom of screen. All screens white.
 * 2. Bottom navigation bar white background #FFFFFF, fixed at bottom.
 * 3. Text and icons in Dark Navy Blue #0A1931.
 * 4. Accent in HUNAR Orange #FF6B00 (active Home icon, buttons, badges).
 * 5. Replaced all pure black #000000 with Dark Navy Blue #0A1931.
 */
val HunarWhiteColorScheme = lightColorScheme(
    primary = HunarOrange,                // #FF6B00 HUNAR Orange accent
    onPrimary = HunarPureWhite,
    primaryContainer = HunarCardLightGrey,
    onPrimaryContainer = HunarNavy,
    secondary = HunarNavy,                // #0A1931 Dark Navy Blue
    onSecondary = HunarPureWhite,
    secondaryContainer = HunarCardLightGrey,
    onSecondaryContainer = HunarNavy,
    tertiary = HunarGold,
    onTertiary = HunarNavy,
    background = HunarPureWhite,          // #FFFFFF Pure White from top to bottom
    onBackground = HunarNavy,             // Dark Navy Blue text
    surface = HunarPureWhite,             // Pure White surfaces & bottom bar
    onSurface = HunarNavy,                // Dark Navy Blue text & icons (#0A1931)
    surfaceVariant = HunarCardLightGrey,  // Light Grey for cards (#F4F4F6)
    onSurfaceVariant = HunarTextSecondaryLight,
    outline = HunarCardLightGreyBorder,
    error = HunarError,
    onError = HunarPureWhite
)

@Composable
fun HunarTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    highContrastTypography: Boolean = false,
    content: @Composable () -> Unit
) {
    // Pure White Theme is globally enforced across all screens #FFFFFF
    val colorScheme: ColorScheme = HunarWhiteColorScheme
    val typography = createHunarTypography(AppThemeMode.LIGHT, highContrastTypography)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Pure white status bar and navigation bar with dark navy blue icons
                val statusBarColor = HunarPureWhite.toArgb()
                window.statusBarColor = statusBarColor
                window.navigationBarColor = statusBarColor

                val insetsController = WindowCompat.getInsetsController(window, view)
                // true = dark icons on white background
                insetsController.isAppearanceLightStatusBars = true
                insetsController.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
