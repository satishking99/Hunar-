package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamModal(
    onDismiss: () -> Unit
) {
    var liveTipFeedback by remember { mutableStateOf<String?>(null) }
    var chatMessage by remember { mutableStateOf("") }
    val mockChat = remember {
        mutableStateListOf(
            "Ananya: Sir, how many hours of practice for this sitar taan?",
            "Vikas: Oblique strength in human flag is unreal!",
            "Rohan: Proud to see Indian talent getting real respect 🇮🇳",
            "Sneha: Sent ₹50 Super Shabaashi for this incredible technique!"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = HunarBlackDeep,
        scrimColor = HunarBlack.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp)
                .testTag("live_stream_modal")
        ) {
            // Live Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(HunarError)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("LIVE", color = HunarWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        text = "Aman Saxena • Classical Sitar Riyaz",
                        color = HunarTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = HunarTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulated Live Video Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF14141E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🪕", fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LIVE ACOUSTIC SITAR • 180 BPM",
                        color = HunarOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1.8K viewers watching real talent",
                        color = HunarTextMuted,
                        fontSize = 11.sp
                    )
                }

                // Modesty & Skill Stamp in Live
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(HunarBlack.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("✓ AI Modest & Skill Monitored", color = HunarSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Super Shabaashi Quick Tip Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Super Shabaashi:", color = HunarTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                listOf(20, 50, 100, 200).forEach { amt ->
                    Button(
                        onClick = {
                            liveTipFeedback = "Sent ₹$amt Super Shabaashi to Aman Saxena! 👏"
                            mockChat.add("You: Sent ₹$amt Super Shabaashi for pure talent! ⚡")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("₹$amt", color = HunarBlackDeep, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            liveTipFeedback?.let { feedback ->
                Text(
                    text = feedback,
                    color = HunarSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Chat
            Text(text = "Live Audience Chat", color = HunarTextMuted, fontSize = 11.sp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                mockChat.takeLast(4).forEach { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(HunarDarkCard.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = msg, color = HunarTextPrimary, fontSize = 12.sp)
                    }
                }
            }

            // Chat input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatMessage,
                    onValueChange = { chatMessage = it },
                    placeholder = { Text("Appreciate the live skill...", color = HunarTextMuted, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HunarTextPrimary,
                        unfocusedTextColor = HunarTextPrimary,
                        focusedBorderColor = HunarOrange,
                        unfocusedBorderColor = HunarBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                IconButton(
                    onClick = {
                        if (chatMessage.isNotBlank()) {
                            mockChat.add("You: $chatMessage")
                            chatMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HunarOrange)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = HunarBlackDeep)
                }
            }
        }
    }
}
