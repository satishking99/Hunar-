package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.theme.*

/**
 * Top Bar for Hunar Home Screen.
 *
 * Displays:
 * - Brand logo & tagline: "Hunar — Looks नहीं, Talent और मेहनत की पहचान"
 * - Language selection trigger
 * - 🔴 LIVE trigger
 * - Theme toggle button at top right corner (sun/moon icon)
 * - Notification Bell 🔔 with red badge (opens separate Notifications page)
 */
@Composable
fun HunarHomeTopBar(
    selectedLanguage: Language,
    unreadNotificationsCount: Int,
    currentTheme: AppThemeMode,
    isDarkActive: Boolean,
    onLanguageClick: () -> Unit,
    onThemeToggleClick: () -> Unit,
    onNotificationBellClick: () -> Unit,
    onLiveClick: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("hunar_home_top_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & Subtitle
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HUNAR",
                            color = HunarOrange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = HunarOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "TALENT FIRST",
                                color = HunarOrange,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Looks नहीं, Talent और मेहनत की पहचान",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                }

                // Top Actions: Live, Language, Theme Toggle (Sun/Moon), Notification Bell 🔔
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Live Stream Button
                    FilledTonalButton(
                        onClick = onLiveClick,
                        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 3.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HunarError.copy(alpha = 0.15f),
                            contentColor = HunarError
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("home_live_btn")
                    ) {
                        Text(text = "🔴 LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Language Selector
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onLanguageClick)
                            .testTag("home_language_btn")
                    ) {
                        Text(
                            text = "🌐 ${selectedLanguage.nativeName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    // Theme toggle button at top right corner (sun/moon icon)
                    // If WHITE then show Moon (clicking switches to BLACK)
                    // If BLACK then show Sun (clicking switches to WHITE)
                    IconButton(
                        onClick = onThemeToggleClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkActive) "Switch to White Theme" else "Switch to Black Theme",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Notification Bell 🔔 with Red Badge (Opens separate Notifications page)
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = onNotificationBellClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("home_notification_bell_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (unreadNotificationsCount > 0) HunarOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                        if (unreadNotificationsCount > 0) {
                            Surface(
                                color = HunarError,
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (unreadNotificationsCount > 9) "9+" else "$unreadNotificationsCount",
                                        color = HunarPureWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
