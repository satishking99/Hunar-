package com.example

import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.ThemeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ThemeManagerTest {

    private lateinit var themeManager: ThemeManager

    @Before
    fun setUp() {
        themeManager = ThemeManager.instance
        themeManager.setThemeMode(AppThemeMode.LIGHT)
        themeManager.setReducedMotion(false)
        themeManager.setHighContrastTypography(false)
    }

    @Test
    fun testDefaultThemeMode_isLight() {
        assertEquals(AppThemeMode.LIGHT, themeManager.themeMode.value)
        assertFalse(themeManager.reducedMotion.value)
        assertFalse(themeManager.highContrastTypography.value)
    }

    @Test
    fun testSetThemeMode_updatesStateGlobally() {
        themeManager.setThemeMode(AppThemeMode.DARK)
        assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)

        themeManager.setThemeMode(AppThemeMode.SYSTEM)
        assertEquals(AppThemeMode.SYSTEM, themeManager.themeMode.value)

        themeManager.setThemeMode(AppThemeMode.LIGHT)
        assertEquals(AppThemeMode.LIGHT, themeManager.themeMode.value)
    }

    @Test
    fun testToggleTheme_togglesBetweenLightAndDark() {
        themeManager.setThemeMode(AppThemeMode.LIGHT)

        // Toggling from LIGHT (White Theme) switches to DARK (Black Theme)
        themeManager.toggleTheme()
        assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)

        // Toggling from DARK (Black Theme) switches to LIGHT (White Theme)
        themeManager.toggleTheme()
        assertEquals(AppThemeMode.LIGHT, themeManager.themeMode.value)
    }

    @Test
    fun testAccessibilityToggles() {
        themeManager.setReducedMotion(true)
        assertTrue(themeManager.reducedMotion.value)

        themeManager.setHighContrastTypography(true)
        assertTrue(themeManager.highContrastTypography.value)
    }

    @Test
    fun testThemeViewModel_propagatesChanges() = runBlocking {
        val viewModel = ThemeViewModel(themeManager)

        viewModel.setThemeMode(AppThemeMode.DARK)
        assertEquals(AppThemeMode.DARK, viewModel.themeMode.first())

        viewModel.setHighContrastTypography(true)
        assertTrue(viewModel.highContrastTypography.first())

        viewModel.setReducedMotion(true)
        assertTrue(viewModel.reducedMotion.first())
    }
}
