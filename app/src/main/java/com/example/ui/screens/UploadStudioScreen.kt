package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiScanResult
import com.example.data.model.SkillCategory
import com.example.data.model.StrikeStatus
import com.example.data.model.UploadTestScenario
import com.example.ui.theme.*

@Composable
fun UploadStudioScreen(
    strikeStatus: StrikeStatus,
    onSimulateScan: (UploadTestScenario, String, String, SkillCategory) -> AiScanResult,
    onAppeal: () -> Unit,
    onResetStrikes: () -> Unit
) {
    var title by remember { mutableStateOf("Algorithmic Data Structures in Kotlin") }
    var caption by remember { mutableStateOf("Clean code and pure problem-solving! 🇮🇳 #HunarTech #SkillFirst") }
    var selectedCategory by remember { mutableStateOf(SkillCategory.TECH) }
    var scanResult by remember { mutableStateOf<AiScanResult?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("upload_studio_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Talent Upload Studio",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = HunarTextPrimary
                    )
                    Text(
                        text = "AI Verified Modesty & Skill Moderation",
                        color = HunarOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(HunarDarkCard)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AI Vision v3.8",
                        color = HunarTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Strike Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        strikeStatus.currentStrikes >= 3 -> HunarError.copy(alpha = 0.15f)
                        strikeStatus.currentStrikes > 0 -> HunarWarning.copy(alpha = 0.15f)
                        else -> HunarDarkSurface
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when {
                        strikeStatus.currentStrikes >= 3 -> HunarError
                        strikeStatus.currentStrikes > 0 -> HunarWarning
                        else -> HunarBorder
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (strikeStatus.currentStrikes) {
                                    0 -> "🛡️"
                                    1 -> "⚠️"
                                    2 -> "🚨"
                                    else -> "🚫"
                                },
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rule 1 Strike Standing: ${strikeStatus.currentStrikes} / 3",
                                color = when (strikeStatus.currentStrikes) {
                                    0 -> HunarSuccess
                                    1 -> HunarWarning
                                    else -> HunarError
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (strikeStatus.currentStrikes > 0) {
                            TextButton(onClick = onResetStrikes) {
                                Text(text = "Reset Demo", color = HunarOrange, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (strikeStatus.currentStrikes) {
                            0 -> "Account in Clean Standing. You can freely upload genuine skill videos."
                            1 -> "Strike 1 Active: Upload suspended for 24 hours. Violating video removed."
                            2 -> "Strike 2 Active: Upload suspended for 7 days. Further violations will cause permanent ban."
                            else -> "Strike 3 PERMANENT BAN: The (+) upload option has been permanently removed from the app! Your profile stays active but you cannot post."
                        },
                        color = HunarTextSecondary,
                        fontSize = 12.sp
                    )

                    if (strikeStatus.currentStrikes >= 3) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onAppeal,
                            colors = ButtonDefaults.buttonColors(containerColor = HunarError),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Appeal to Merit Council", color = HunarWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Upload Form (Title, Category, Caption)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HunarDarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HunarBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Content Details",
                        color = HunarTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Talent Title / Demonstration", color = HunarTextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HunarTextPrimary,
                            unfocusedTextColor = HunarTextPrimary,
                            focusedBorderColor = HunarOrange,
                            unfocusedBorderColor = HunarBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text("Caption & Technique Notes", color = HunarTextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HunarTextPrimary,
                            unfocusedTextColor = HunarTextPrimary,
                            focusedBorderColor = HunarOrange,
                            unfocusedBorderColor = HunarBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Skill Category Selector
                    Text(
                        text = "Skill Category Filter",
                        color = HunarTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(SkillCategory.TECH, SkillCategory.FITNESS, SkillCategory.MUSIC, SkillCategory.ART).forEach { cat ->
                            val isSel = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) HunarOrange else HunarDarkCard)
                                    .border(1.dp, if (isSel) HunarOrange else HunarBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cat.icon} ${cat.name.take(4)}",
                                    color = if (isSel) HunarBlackDeep else HunarTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live AI Test Scenarios (Demonstrating Rules 1, 2, 3, 4)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HunarDarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HunarOrange.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧪", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Moderation Pre-Scanner",
                            color = HunarOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Test how HUNAR's automated AI engine enforces the 4 Golden Rules prior to publishing:",
                        color = HunarTextSecondary,
                        fontSize = 11.sp
                    )

                    // Button 1: Compliant Skill (Passing)
                    Button(
                        onClick = {
                            val res = onSimulateScan(UploadTestScenario.COMPLIANT_SKILL, title, caption, selectedCategory)
                            scanResult = res
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarSuccess),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_compliant_btn")
                    ) {
                        Text("✅ Publish Compliant Skill Video (Passes Rule 1 & 3)", color = HunarWhite, fontWeight = FontWeight.Bold)
                    }

                    // Button 2: Test Rule 1 (Dress Code Violation -> Triggers Strike)
                    Button(
                        onClick = {
                            val res = onSimulateScan(UploadTestScenario.RULE_1_DRESS_CODE_VIOLATION, title, caption, selectedCategory)
                            scanResult = res
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarError),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_rule1_violation_btn")
                    ) {
                        Text("⚠️ Test Rule 1: Revealing Clothes (Triggers Strike)", color = HunarWhite, fontWeight = FontWeight.Bold)
                    }

                    // Button 3: Test Rule 3 (Plain Lip-sync without skill)
                    Button(
                        onClick = {
                            val res = onSimulateScan(UploadTestScenario.RULE_3_PLAIN_LIPSYNC, title, caption, selectedCategory)
                            scanResult = res
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarWarning),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_rule3_lipsync_btn")
                    ) {
                        Text("🚫 Test Rule 3: Plain Lip-Sync Without Skill", color = HunarBlackDeep, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Scan Result Card
        scanResult?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.passed) HunarSuccess.copy(alpha = 0.15f) else HunarError.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (result.passed) HunarSuccess else HunarError
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (result.passed) "🎉" else "🛑", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (result.passed) "Upload Approved & Live" else "Content Blocked by AI",
                                color = if (result.passed) HunarSuccess else HunarError,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = result.details,
                            color = HunarTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        if (!result.passed && result.zeroEarningFlagged) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HunarBlackDeep)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "RULE 2 ENFORCED: Even if this content has views, its revenue is ₹0. Watch time discarded.",
                                    color = HunarError,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
