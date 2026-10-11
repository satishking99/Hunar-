package com.example.ui.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel exposing theme state to Compose UI with lifecycle awareness.
 * Delegates directly to the singleton ThemeManager for persistence and consistency.
 */
class ThemeViewModel(
    private val themeManager: ThemeManager = ThemeManager.instance
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = themeManager.themeMode

    val reducedMotion: StateFlow<Boolean> = themeManager.reducedMotion

    val highContrastTypography: StateFlow<Boolean> = themeManager.highContrastTypography

    fun initPreferences(context: Context) {
        themeManager.initialize(context)
    }

    fun setThemeMode(mode: AppThemeMode) {
        themeManager.setThemeMode(mode)
    }

    fun toggleTheme(isSystemDarkNow: Boolean = false) {
        themeManager.toggleTheme(isSystemDarkNow)
    }

    fun setReducedMotion(enabled: Boolean) {
        themeManager.setReducedMotion(enabled)
    }

    fun setHighContrastTypography(enabled: Boolean) {
        themeManager.setHighContrastTypography(enabled)
    }
}
