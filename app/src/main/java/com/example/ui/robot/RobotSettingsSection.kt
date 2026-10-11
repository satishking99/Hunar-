package com.example.ui.robot

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HunarScreenTab
import com.example.ui.theme.*

/**
 * Dedicated "AI Robot Assistant" Settings Card inside Hunar Settings:
 * - Robot ON/OFF
 * - Voice Commands ON/OFF
 * - Movement Speed (Slider & presets)
 * - Five-Minute Lap Setting
 * - Screen Navigation Permission
 * - Messaging Permission
 * - App-Opening Permission
 * - Privacy and Security Details
 * - Reset Robot Settings
 */
@Composable
fun RobotSettingsSection(
    manager: RobotAssistantManager,
    onNavigate: (HunarScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPrivacyModal by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = HunarCardLightGrey,
        modifier = modifier
            .fillMaxWidth()
            .testTag("robot_settings_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Header with Cute Robot Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CuteRobot3DCanvas(
                        size = 32.dp,
                        headingAngle = 0f,
                        walkPhase = 0f,
                        isRobotModeOn = manager.config.isRobotModeOn,
                        mood = if (manager.config.isRobotModeOn) RobotMood.HAPPY else RobotMood.WALKING
                    )
                    Column {
                        Text(
                            text = "AI Robot Assistant",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HunarNavy
                        )
                        Text(
                            text = "Smart Companion • 2-Min Screen Perimeter Lap (120s)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (manager.config.isRobotModeOn) Color(0xFF10B981) else Color(0xFF64748B)
                ) {
                    Text(
                        text = if (manager.config.isRobotModeOn) "ACTIVE" else "WALKING",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = HunarCardLightGreyBorder, thickness = 1.dp)

            // 1. Robot ON/OFF
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Robot Mode ON / OFF",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "When ON, enables robot-controlled commands and voice assistance. When OFF, walks calmly with normal finger-touch restored.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
                Switch(
                    checked = manager.config.isRobotModeOn,
                    onCheckedChange = { manager.toggleRobotMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.testTag("toggle_robot_mode")
                )
            }

            // 2. Touch Interception Guidance Toggle (When mode is ON)
            if (manager.config.isRobotModeOn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Touch Interception Guidance",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = HunarNavy
                        )
                        Text(
                            text = "Intercepts ordinary taps so only the robot controls actions. Emergency STOP always restores touch.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = manager.config.touchInterceptionActive,
                        onCheckedChange = { manager.setTouchInterception(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HunarOrange
                        ),
                        modifier = Modifier.testTag("toggle_touch_interception")
                    )
                }
            }

            // 3. Voice Commands ON/OFF
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Voice Commands",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Listen for spoken commands with visible speech transcript before any sensitive action.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = manager.config.voiceCommandsEnabled,
                    onCheckedChange = { manager.toggleVoiceCommands(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HunarOrange
                    ),
                    modifier = Modifier.testTag("toggle_voice_commands")
                )
            }

            // 4. Two-Minute Lap Setting (Standard 120s Loop)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Standard 2-Minute Lap (120s)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Continuous perimeter walk around 4 screen edges in exactly 120 seconds (~30s per side).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = manager.config.isTwoMinuteLapLocked,
                    onCheckedChange = { manager.setTwoMinuteLap(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HunarNavy
                    ),
                    modifier = Modifier.testTag("toggle_two_min_lap")
                )
            }

            // 5. Movement Speed Control
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Movement Speed: ${String.format("%.1f", manager.config.speedMultiplier)}x",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Lap: ${manager.config.lapDurationSeconds.toInt()}s",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Slider(
                    value = manager.config.speedMultiplier,
                    onValueChange = { manager.setSpeedMultiplier(it) },
                    valueRange = 0.5f..4.0f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = HunarOrange,
                        activeTrackColor = HunarOrange
                    ),
                    modifier = Modifier.testTag("slider_movement_speed")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { manager.setSpeedMultiplier(1.0f) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("1.0x (5 Min)", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { manager.setSpeedMultiplier(2.0f) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("2.0x (Fast)", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { manager.setSpeedMultiplier(4.0f) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("4.0x (Turbo)", fontSize = 11.sp)
                    }
                }
            }

            HorizontalDivider(color = HunarCardLightGreyBorder, thickness = 1.dp)

            // Permissions Group
            Text(
                text = "Assistant Action Permissions",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = HunarNavy
            )

            // 6. Screen Navigation Permission
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Screen Navigation",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Allow robot to switch between Home, Reels, Messages, Profile, and Settings on command.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = manager.config.screenNavigationAllowed,
                    onCheckedChange = { manager.setScreenNavigationAllowed(it) },
                    modifier = Modifier.testTag("toggle_screen_navigation")
                )
            }

            // 7. Messaging Permission
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Messaging Permission",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Allow preparing messages (e.g. to Satish). Strictly requires explicit confirmation before sending.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = manager.config.messagingPermissionGranted,
                    onCheckedChange = { manager.setMessagingPermission(it) },
                    modifier = Modifier.testTag("toggle_messaging_perm")
                )
            }

            // 8. App-Opening Permission
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "App-Opening Permission",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = HunarNavy
                    )
                    Text(
                        text = "Allow launching supported apps (YouTube, WhatsApp, Maps, Calculator) via Android Intents.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = manager.config.appOpeningPermissionGranted,
                    onCheckedChange = { manager.setAppOpeningPermission(it) },
                    modifier = Modifier.testTag("toggle_app_opening_perm")
                )
            }

            HorizontalDivider(color = HunarCardLightGreyBorder, thickness = 1.dp)

            // 9. Quick Feature Action Buttons
            Text(
                text = "Quick Assistant Test Actions",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = HunarNavy
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { manager.initiateSatishMessage(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).testTag("btn_test_satish_msg")
                ) {
                    Text("💬 Message Satish", fontSize = 11.sp)
                }

                Button(
                    onClick = { manager.searchSportsVideos("sports") },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarNavy),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).testTag("btn_test_sports_video")
                ) {
                    Text("🏆 Sports Videos", fontSize = 11.sp)
                }
            }

            // 10. Privacy and Security & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showPrivacyModal = true },
                    modifier = Modifier.testTag("btn_robot_privacy_info")
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Privacy & Security", fontSize = 12.sp)
                }

                TextButton(
                    onClick = {
                        manager.resetToDefaults()
                        Toast.makeText(context, "Robot settings reset to defaults", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("btn_reset_robot_settings")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Robot Settings", fontSize = 12.sp, color = Color(0xFFDC2626))
                }
            }
        }
    }

    // Privacy & Security Explanation Modal
    if (showPrivacyModal) {
        AlertDialog(
            onDismissRequest = { showPrivacyModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🛡️", fontSize = 20.sp)
                    Text("Robot Assistant Privacy & Safety", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "• Zero-Data-Leak Guarantee: All command processing runs on-device. No voice recordings or private messages are uploaded to external servers.",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Mandatory User Confirmation: The robot will NEVER send a message, launch an external application, or perform sensitive actions without your explicit button confirmation.",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Android Permission Compliance: Uses standard Android Intent APIs (ACTION_SENDTO, ACTION_VIEW). When the app is closed, robot background movement ceases to respect battery life and user privacy.",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Emergency Touch Restore: When Robot Mode is ON, you can disengage or tap Emergency STOP at any time to regain instantaneous finger-touch control.",
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyModal = false }) {
                    Text("Got It")
                }
            }
        )
    }
}
