package com.example.ui.robot

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HunarScreenTab
import com.example.ui.theme.*

/**
 * Rich Modal Bottom Sheet / Dialog for AI Robot Assistant commands:
 * - Voice input & visible transcript
 * - Text commands input
 * - Contact Satish message preparation with confirmation
 * - Sports video search & results
 * - Supported app launcher
 * - Hunar navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobotCommandSheet(
    manager: RobotAssistantManager,
    onNavigate: (HunarScreenTab) -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }

    // Speech Recognizer Intent Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                manager.lastVoiceTranscript = spokenText
                inputText = spokenText
                manager.processCommand(spokenText, context, onNavigate, onOpenSettings)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.testTag("robot_command_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Robot Identity and Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (manager.config.isRobotModeOn) Color(0xFF10B981).copy(alpha = 0.2f) else HunarOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CuteRobot3DCanvas(
                            size = 38.dp,
                            headingAngle = 0f,
                            walkPhase = 0f,
                            isRobotModeOn = manager.config.isRobotModeOn,
                            mood = if (manager.config.isRobotModeOn) RobotMood.HAPPY else RobotMood.WALKING
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Hunar AI Robot",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (manager.config.isRobotModeOn) Color(0xFF10B981) else Color(0xFF64748B)
                            ) {
                                Text(
                                    text = if (manager.config.isRobotModeOn) "MODE ON" else "MODE OFF",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Walk Speed: ${manager.config.speedMultiplier}x | 5-min lap",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onDismiss()
                        onOpenSettings()
                    },
                    modifier = Modifier.testTag("robot_open_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Robot Settings",
                        tint = HunarNavy
                    )
                }
            }

            // Visible Transcript Box if voice/text was recent
            if (manager.lastVoiceTranscript.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = HunarOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Last recognized: \"${manager.lastVoiceTranscript}\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input Bar (Voice Mic + Text Input + Send)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask robot (e.g. 'Message Satish', 'Sports')") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("robot_command_text_input"),
                    shape = RoundedCornerShape(24.dp)
                )

                // Voice Mic Button
                FilledIconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a command to Hunar Robot Assistant...")
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice recognizer simulated. Try typing!", Toast.LENGTH_SHORT).show()
                            inputText = "Send message to Satish"
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = HunarOrange),
                    modifier = Modifier.size(46.dp).testTag("robot_voice_mic_btn")
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White)
                }

                // Send Button
                FilledIconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            manager.processCommand(inputText, context, onNavigate, onOpenSettings)
                            inputText = ""
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = HunarNavy),
                    modifier = Modifier.size(46.dp).testTag("robot_send_command_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Command", tint = Color.White)
                }
            }

            // Quick Assistant Shortcuts
            Text(
                text = "Assistant Features & Shortcuts",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AssistChip(
                        onClick = { manager.initiateSatishMessage(context) },
                        label = { Text("💬 Message Satish ('Hi')") },
                        modifier = Modifier.testTag("chip_message_satish")
                    )
                }
                item {
                    AssistChip(
                        onClick = { manager.searchSportsVideos("sports") },
                        label = { Text("🏏 Find Sports Videos") },
                        modifier = Modifier.testTag("chip_find_sports")
                    )
                }
                item {
                    AssistChip(
                        onClick = { manager.isAppLauncherVisible = true },
                        label = { Text("📱 Open Applications") },
                        modifier = Modifier.testTag("chip_open_apps")
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            onDismiss()
                            onNavigate(HunarScreenTab.REELS_SEARCH)
                        },
                        label = { Text("🎥 Navigate to Reels") },
                        modifier = Modifier.testTag("chip_nav_reels")
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            onDismiss()
                            onNavigate(HunarScreenTab.MESSAGES)
                        },
                        label = { Text("💬 Open Messages") },
                        modifier = Modifier.testTag("chip_nav_messages")
                    )
                }
            }

            // Emergency Stop or Mode Quick Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (manager.config.isRobotModeOn) "Robot Mode Active 🦾" else "Robot Mode Inactive (Touch normal)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        manager.emergencyStop()
                        Toast.makeText(context, "Touch control completely restored", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("sheet_emergency_stop_btn")
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Emergency STOP", fontSize = 12.sp)
                }
            }
        }
    }

    // 1. CONFIRMATION MODAL FOR SENSITIVE ACTIONS (e.g. Messaging Satish)
    manager.pendingAction?.let { pending ->
        AlertDialog(
            onDismissRequest = { manager.cancelPendingAction() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🛡️", fontSize = 20.sp)
                    Text(text = pending.title, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = pending.description, fontSize = 13.sp)
                    Surface(
                        color = HunarCardLightGrey,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Recipient: ${pending.recipientOrTarget}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            if (pending.messagePayload.isNotBlank()) {
                                Text(text = "Message: \"${pending.messagePayload}\"", color = HunarNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    Text(
                        text = "User approval is strictly required before sending any external message or dispatching actions.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { manager.executeApprovedAction(context, onNavigate) },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                    modifier = Modifier.testTag("confirm_action_btn")
                ) {
                    Text("Approve & Send")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { manager.cancelPendingAction() },
                    modifier = Modifier.testTag("cancel_action_btn")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. SPORTS VIDEOS SEARCH RESULTS DIALOG
    if (manager.isSportsResultsVisible) {
        AlertDialog(
            onDismissRequest = { manager.closeSportsResults() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🏆", fontSize = 20.sp)
                    Text(text = "Sports Talent Videos", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(manager.sportsSearchResults) { video ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HunarCardLightGrey,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = HunarOrange
                                    ) {
                                        Text(
                                            text = video.sportCategory,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "⏱️ ${video.duration}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = video.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = video.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "By ${video.creatorName}",
                                        fontSize = 11.sp,
                                        color = HunarNavy,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Button(
                                        onClick = {
                                            manager.closeSportsResults()
                                            onDismiss()
                                            onNavigate(HunarScreenTab.REELS_SEARCH)
                                            Toast.makeText(context, "Opened ${video.title} in Reels", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = HunarNavy),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Watch & Save", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { manager.closeSportsResults() }) {
                    Text("Close")
                }
            }
        )
    }

    // 3. SUPPORTED APPS CHOOSER DIALOG
    if (manager.isAppLauncherVisible) {
        AlertDialog(
            onDismissRequest = { manager.isAppLauncherVisible = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📱", fontSize = 20.sp)
                    Text(text = "Open Supported Apps", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select an application to launch via supported Android Intents:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(manager.supportedApps) { appItem ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HunarCardLightGrey,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        manager.isAppLauncherVisible = false
                                        manager.prepareOpenApp(appItem.id, context)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = appItem.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = appItem.iconDescription,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = "Launch",
                                        tint = HunarNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { manager.isAppLauncherVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
