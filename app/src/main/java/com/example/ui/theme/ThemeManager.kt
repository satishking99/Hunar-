package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global singleton ThemeManager to manage theme state across the entire application.
 * Supports:
 * - Default: Automatic / System theme or White theme
 * - Quick toggle between White Theme (#FFFFFF) and Black Theme (#000000)
 * - Persistent storage with SharedPreferences so user choice is saved across launches.
 */
class ThemeManager private constructor() {

    private var prefs: SharedPreferences? = null

    // Default to LIGHT theme as requested ("Make page 1 to 4 default WHITE THEME")
    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _reducedMotion = MutableStateFlow(false)
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    private val _highContrastTypography = MutableStateFlow(false)
    val highContrastTypography: StateFlow<Boolean> = _highContrastTypography.asStateFlow()

    fun initialize(context: Context) {
        val appPrefs = context.applicationContext.getSharedPreferences("hunar_theme_prefs", Context.MODE_PRIVATE)
        prefs = appPrefs

        // Load saved theme mode, defaulting to LIGHT
        val savedModeName = appPrefs.getString("saved_theme_mode", AppThemeMode.LIGHT.name)
        val savedMode = try {
            AppThemeMode.valueOf(savedModeName ?: AppThemeMode.LIGHT.name)
        } catch (_: Exception) {
            AppThemeMode.LIGHT
        }
        _themeMode.value = savedMode

        _reducedMotion.value = appPrefs.getBoolean("reduced_motion", false)
        _highContrastTypography.value = appPrefs.getBoolean("high_contrast", false)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString("saved_theme_mode", mode.name)?.apply()
    }

    /**
     * Toggles between White Theme and Black Theme.
     * When currently LIGHT, switches to DARK (Black Theme).
     * When currently DARK, switches to LIGHT (White Theme).
     * If currently SYSTEM, toggles to the opposite of current system or to DARK.
     */
    fun toggleTheme(isSystemDarkNow: Boolean = false) {
        val newMode = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> if (isSystemDarkNow) AppThemeMode.LIGHT else AppThemeMode.DARK
        }
        setThemeMode(newMode)
    }

    fun setReducedMotion(enabled: Boolean) {
        _reducedMotion.value = enabled
        prefs?.edit()?.putBoolean("reduced_motion", enabled)?.apply()
    }

    fun setHighContrastTypography(enabled: Boolean) {
        _highContrastTypography.value = enabled
        prefs?.edit()?.putBoolean("high_contrast", enabled)?.apply()
    }

    companion object {
        val instance: ThemeManager by lazy { ThemeManager() }
    }
}
