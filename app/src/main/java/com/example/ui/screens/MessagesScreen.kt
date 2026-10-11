package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.ChatMessageType
import com.example.data.model.ConversationItem
import com.example.ui.theme.*

/**
 * PAGE 3 — MESSAGES 💬
 * Supports:
 * - One-to-one messaging
 * - Message requests
 * - Group chats
 * - Photo/video sharing simulation
 * - Voice messages simulation
 * - Online status indicators
 * - Block/report & privacy controls
 */
@Composable
fun MessagesScreen(
    conversations: List<ConversationItem>,
    isDarkActive: Boolean = false,
    onBlockUser: (String) -> Unit = {},
    onReportUser: (String) -> Unit = {},
    onThemeToggleClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedConversation by remember { mutableStateOf<ConversationItem?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Chats, 1 = Requests (Message Requests)

    if (selectedConversation != null) {
        ChatDetailScreen(
            conversation = selectedConversation!!,
            onBack = { selectedConversation = null },
            onBlock = {
                onBlockUser(selectedConversation!!.id)
                Toast.makeText(context, "${selectedConversation!!.participantName} has been blocked", Toast.LENGTH_SHORT).show()
                selectedConversation = null
            },
            onReport = {
                onReportUser(selectedConversation!!.id)
                Toast.makeText(context, "Report submitted for review", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .testTag("messages_screen")
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Messages 💬",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Connect with fellow creators & talent partners",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            Toast.makeText(context, "Starting new conversation with verified creator", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HunarOrange.copy(alpha = 0.15f),
                            contentColor = HunarOrange
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "＋ New Chat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = onThemeToggleClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("messages_theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkActive) "Switch to White Theme" else "Switch to Black Theme",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Tabs: All Chats vs Message Requests
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = HunarOrange,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Primary Chats", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        val reqCount = conversations.count { it.isRequest }
                        Text("Requests ($reqCount)", fontWeight = FontWeight.Bold)
                    }
                )
            }

            val filteredList = conversations.filter {
                if (selectedTab == 0) !it.isRequest else it.isRequest
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💬", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedTab == 0) "No active conversations" else "No pending requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Connect with mentors, collaborators, or brands",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { conv ->
                        ConversationRowItem(
                            conversation = conv,
                            onClick = { selectedConversation = conv }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationRowItem(
    conversation: ConversationItem,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("conversation_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar with Online status dot
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(HunarOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = conversation.participantAvatar, fontSize = 24.sp)
                }
                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(HunarSuccess)
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (conversation.isGroup) conversation.groupTitle ?: conversation.participantName else conversation.participantName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = conversation.lastMessageTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    if (conversation.unreadCount > 0) {
                        Surface(
                            color = HunarOrange,
                            shape = CircleShape,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${conversation.unreadCount}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (conversation.skillTag != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = HunarOrange.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = conversation.skillTag,
                            color = HunarOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDetailScreen(
    conversation: ConversationItem,
    onBack: () -> Unit,
    onBlock: () -> Unit,
    onReport: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val sampleChatHistory = remember {
        mutableStateListOf(
            ChatMessage("m1", conversation.participantName, false, "Hello! Loved your robotics rover tutorial on HUNAR.", "10:14 AM"),
            ChatMessage("m2", "Me", true, "Thanks a lot! Happy to share the code.", "10:16 AM"),
            ChatMessage("m3", conversation.participantName, false, "Would you be interested in collaborating on a STEM workshop?", "10:18 AM"),
            ChatMessage("m4", "Me", true, "🎙️ Voice message (0:24)", "10:20 AM", messageType = ChatMessageType.VOICE, voiceDurationSeconds = 24)
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = HunarOrange)
                        }
                        Box {
                            Text(text = conversation.participantAvatar, fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = conversation.participantName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (conversation.isOnline) "🟢 Online" else "Offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (conversation.isOnline) HunarSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Options menu (Block/Report & Privacy)
                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = HunarOrange)
                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Block Creator", color = HunarError) },
                                onClick = {
                                    showOptionsMenu = false
                                    onBlock()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Report Abuse") },
                                onClick = {
                                    showOptionsMenu = false
                                    onReport()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Privacy Controls") },
                                onClick = { showOptionsMenu = false }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { /* Simulated photo attach */ }) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = "Camera", tint = HunarOrange)
                    }
                    IconButton(onClick = { /* Simulated voice note */ }) {
                        Icon(Icons.Outlined.Mic, contentDescription = "Voice note", tint = HunarOrange)
                    }
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type a merit message...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                sampleChatHistory.add(ChatMessage("m_${System.currentTimeMillis()}", "Me", true, messageText, "Just now"))
                                messageText = ""
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = HunarOrange)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(sampleChatHistory, key = { it.id }) { msg ->
                val isMe = msg.isMe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMe) HunarOrange else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
