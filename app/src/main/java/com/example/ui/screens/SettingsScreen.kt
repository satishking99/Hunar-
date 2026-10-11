package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.User
import com.example.ui.theme.*
import com.example.ui.components.HunarScreenTab
import com.example.ui.robot.RobotAssistantManager
import com.example.ui.robot.RobotSettingsSection

/**
 * SETTINGS PAGE - Full White Screen #FFFFFF
 *
 * Options:
 * > Account (Profile edit, verification badge, Merit score, delete/deactivate)
 * > AI Robot Assistant (Robot ON/OFF, voice commands, 5-min lap, screen navigation, messaging, apps, privacy)
 * > Notifications ON/OFF toggle (Push notifications, tips received, comments, live alerts)
 * > Appearance > Theme - Light / Dark Blue #0A1931
 * > Language / भाषा - NEW SECTION (Current language display & selector modal trigger)
 * > Privacy (Profile visibility, who can message, blocklist, 4 Golden Rules policy)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUser: User,
    currentLanguage: Language,
    isDarkBlueTheme: Boolean,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    robotManager: RobotAssistantManager? = null,
    onNavigate: (HunarScreenTab) -> Unit = {},
    onOpenAccountDetails: () -> Unit = {},
    onOpenPrivacyDetails: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Toggle states
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var tipNotificationsEnabled by remember { mutableStateOf(true) }
    var privateAccountEnabled by remember { mutableStateOf(false) }

    // Dialog state for Account Details
    var showAccountDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        containerColor = HunarPureWhite,
        topBar = {
            Surface(
                color = HunarPureWhite,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .testTag("settings_top_bar")
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("settings_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = HunarNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Settings",
                            color = HunarNavy,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = HunarCardLightGreyBorder, thickness = 1.dp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HunarPureWhite)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
        ) {
            // Profile Mini-Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = HunarCardLightGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAccountDialog = true }
                        .testTag("settings_profile_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(HunarOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentUser.avatarEmoji, fontSize = 26.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HunarNavy
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "★ ${currentUser.talentScore}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HunarOrange
                                )
                            }
                            Text(
                                text = "${currentUser.handle} • ${currentUser.verifiedSkillTitle}",
                                fontSize = 12.sp,
                                color = HunarTextSecondaryLight
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Profile",
                            tint = HunarNavy
                        )
                    }
                }
            }

            // PREMIER SECTION: AI ROBOT ASSISTANT
            robotManager?.let { rManager ->
                item {
                    SettingsSectionHeader(title = "AI Robot Assistant")
                }
                item {
                    RobotSettingsSection(
                        manager = rManager,
                        onNavigate = onNavigate
                    )
                }
            }

            // SECTION 1: ACCOUNT
            item {
                SettingsSectionHeader(title = "Account")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarPureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.Person,
                            title = "Account & Profile Details",
                            subtitle = "Edit verified skill, bio, UPI payout handle",
                            onClick = { showAccountDialog = true },
                            testTag = "settings_account_row"
                        )
                        HorizontalDivider(color = HunarCardLightGreyBorder.copy(alpha = 0.6f))
                        SettingsRow(
                            icon = Icons.Default.Verified,
                            title = "Merit & Talent Verification",
                            subtitle = "Status: Skill Verified (${currentUser.verifiedSkillTitle})",
                            onClick = {
                                Toast.makeText(context, "Skill Verified • Talent Score ${currentUser.talentScore}", Toast.LENGTH_SHORT).show()
                            },
                            badgeText = "VERIFIED"
                        )
                    }
                }
            }

            // SECTION 2: NOTIFICATIONS ON/OFF TOGGLE
            item {
                SettingsSectionHeader(title = "Notifications")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarPureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsToggleRow(
                            icon = Icons.Default.Notifications,
                            title = "Push Notifications",
                            subtitle = "Turn all alerts and community updates ON or OFF",
                            checked = pushNotificationsEnabled,
                            onCheckedChange = {
                                pushNotificationsEnabled = it
                                val msg = if (it) "Notifications ON" else "Notifications OFF"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            testTag = "settings_notifications_toggle"
                        )
                        HorizontalDivider(color = HunarCardLightGreyBorder.copy(alpha = 0.6f))
                        SettingsToggleRow(
                            icon = Icons.Default.Paid,
                            title = "Shabaashi & Tip Alerts",
                            subtitle = "Get notified instantly when someone tips your hard work",
                            checked = tipNotificationsEnabled,
                            onCheckedChange = {
                                tipNotificationsEnabled = it
                            }
                        )
                    }
                }
            }

            // SECTION 3: APPEARANCE > THEME (LIGHT / DARK BLUE #0A1931)
            item {
                SettingsSectionHeader(title = "Appearance")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarPureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(HunarCardLightGrey),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Theme Selection",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HunarNavy
                                )
                                Text(
                                    text = "Light (Pure White #FFFFFF) / Dark Navy Blue (#0A1931)",
                                    fontSize = 12.sp,
                                    color = HunarTextSecondaryLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Theme Options Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Light White Theme option
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isDarkBlueTheme) HunarOrange.copy(alpha = 0.12f) else HunarCardLightGrey,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (!isDarkBlueTheme) 1.5.dp else 1.dp,
                                    color = if (!isDarkBlueTheme) HunarOrange else HunarCardLightGreyBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onThemeToggle(false) }
                                    .testTag("theme_option_light")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = !isDarkBlueTheme,
                                        onClick = { onThemeToggle(false) },
                                        colors = RadioButtonDefaults.colors(selectedColor = HunarOrange)
                                    )
                                    Column {
                                        Text(
                                            text = "Light (White)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HunarNavy
                                        )
                                        Text(text = "#FFFFFF", fontSize = 11.sp, color = HunarTextSecondaryLight)
                                    }
                                }
                            }

                            // Dark Blue #0A1931 option
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkBlueTheme) HunarOrange.copy(alpha = 0.12f) else HunarCardLightGrey,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isDarkBlueTheme) 1.5.dp else 1.dp,
                                    color = if (isDarkBlueTheme) HunarOrange else HunarCardLightGreyBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onThemeToggle(true) }
                                    .testTag("theme_option_dark_blue")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isDarkBlueTheme,
                                        onClick = { onThemeToggle(true) },
                                        colors = RadioButtonDefaults.colors(selectedColor = HunarOrange)
                                    )
                                    Column {
                                        Text(
                                            text = "Dark Blue",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HunarNavy
                                        )
                                        Text(text = "#0A1931", fontSize = 11.sp, color = HunarTextSecondaryLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: LANGUAGE / भाषा — NEW SECTION
            item {
                SettingsSectionHeader(title = "Language / भाषा")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarPureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = "App Language / भाषा चुनें",
                        subtitle = "Currently set to ${currentLanguage.nativeName} (${currentLanguage.englishName})",
                        onClick = onLanguageClick,
                        badgeText = currentLanguage.code.uppercase(),
                        testTag = "settings_language_row"
                    )
                }
            }

            // SECTION 5: PRIVACY
            item {
                SettingsSectionHeader(title = "Privacy")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarPureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsToggleRow(
                            icon = Icons.Default.Lock,
                            title = "Private Account",
                            subtitle = "Only approved followers can view your detailed skills",
                            checked = privateAccountEnabled,
                            onCheckedChange = { privateAccountEnabled = it },
                            testTag = "settings_privacy_private_account"
                        )
                        HorizontalDivider(color = HunarCardLightGreyBorder.copy(alpha = 0.6f))
                        SettingsRow(
                            icon = Icons.Default.Block,
                            title = "Blocked Accounts",
                            subtitle = "Manage restricted and blocked creators",
                            onClick = {
                                Toast.makeText(context, "No blocked users currently", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(color = HunarCardLightGreyBorder.copy(alpha = 0.6f))
                        SettingsRow(
                            icon = Icons.Default.Gavel,
                            title = "4 Golden Content Rules & Constitution",
                            subtitle = "Zero vulgarity, pure talent & 3-strike policies",
                            onClick = { showPrivacyDialog = true }
                        )
                    }
                }
            }

            // SECTION 6: LOGOUT
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = HunarError.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HunarError.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogoutClick() }
                        .testTag("settings_logout_btn")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = HunarError)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out of HUNAR",
                            color = HunarError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Account Details Dialog
    if (showAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = HunarPureWhite,
            title = {
                Text(
                    text = "Account Details",
                    fontWeight = FontWeight.Bold,
                    color = HunarNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Name: ${currentUser.name}\nHandle: ${currentUser.handle}\nPrimary Skill: ${currentUser.verifiedSkillTitle}\nMerit Score: ${currentUser.talentScore}/100",
                        color = HunarNavy,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAccountDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Privacy & Rules Info Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = HunarPureWhite,
            title = {
                Text(
                    text = "HUNAR Privacy & Golden Rules",
                    fontWeight = FontWeight.Bold,
                    color = HunarNavy
                )
            },
            text = {
                Text(
                    text = "1. Zero adult/vulgar content\n2. Looks नहीं, Talent और मेहनत की पहचान\n3. Zero low-effort lip-syncing\n4. Respect & dignified interactions.\n\nYour data is protected and never sold.",
                    color = HunarNavy,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Understood", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = HunarTextSecondaryLight,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badgeText: String? = null,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(HunarCardLightGrey),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = HunarNavy, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HunarNavy
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = HunarTextSecondaryLight
            )
        }

        if (badgeText != null) {
            Surface(
                color = HunarOrange.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badgeText,
                    color = HunarOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = HunarTextSecondaryLight,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(HunarCardLightGrey),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = HunarNavy, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HunarNavy
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = HunarTextSecondaryLight
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HunarOrange,
                uncheckedThumbColor = HunarTextSecondaryLight,
                uncheckedTrackColor = HunarCardLightGrey
            )
        )
    }
}
