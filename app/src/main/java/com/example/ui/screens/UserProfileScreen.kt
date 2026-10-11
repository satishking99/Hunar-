package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*

/**
 * USER PROFILE SCREEN
 *
 * Dedicated UI screen displaying user skills, audited achievements, and transparent
 * earnings to emphasize the merit-first philosophy of the HUNAR platform.
 *
 * Core Principles:
 * 1. "Mehnat aur Talent ko Attention, sirf Looks ko nahi" (Talent > Glamour)
 * 2. Purely merit-driven: No public vanity follower clout, no empty like counts.
 * 3. Transparent verified skill proficiencies backed by benchmarks and peer audits.
 * 4. Transparent earnings ledger earned strictly through skill, tutoring, grants, and tips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userProfile: UserProfile,
    onWithdrawSubmit: (amount: Double, upiId: String) -> Unit = { _, _ -> },
    onEndorseSkill: (skillId: String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Active Tab: 0 = Merit Dossier, 1 = Verified Skills, 2 = Achievements, 3 = Transparent Earnings
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog states
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showExportDossierDialog by remember { mutableStateOf(false) }
    var showPhilosophyDialog by remember { mutableStateOf(false) }
    var selectedSkillForDetails by remember { mutableStateOf<UserSkill?>(null) }
    var selectedAchievementForAudit by remember { mutableStateOf<UserAchievement?>(null) }

    BackHandler {
        onBackClick()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("user_profile_screen"),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("btn_user_profile_back")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Merit Profile",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = HunarSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "MERIT FIRST",
                                            color = HunarSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Talent > Glamour • Data-Driven Accomplishments",
                                    fontSize = 11.sp,
                                    color = HunarOrangeDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { showPhilosophyDialog = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_profile_philosophy_info")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = "Merit Philosophy",
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { showExportDossierDialog = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_profile_share_dossier")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Merit Dossier",
                                    tint = HunarOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // 4 Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = HunarOrange,
                        edgePadding = 12.dp,
                        divider = {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.testTag("tab_profile_dossier"),
                            text = {
                                Text(
                                    text = "🎯 Merit Dossier",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.testTag("tab_profile_skills"),
                            text = {
                                Text(
                                    text = "🛠️ Verified Skills (${userProfile.skills.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            modifier = Modifier.testTag("tab_profile_achievements"),
                            text = {
                                Text(
                                    text = "🏆 Achievements (${userProfile.achievements.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            modifier = Modifier.testTag("tab_profile_earnings"),
                            text = {
                                Text(
                                    text = "💰 Earnings (₹${userProfile.earnings.totalEarningsRupees.toInt()})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // HEADER: IDENTITY & MERIT-FIRST HERO CARD
            // ==========================================
            item {
                UserProfileHeroCard(
                    userProfile = userProfile,
                    onPhilosophyClick = { showPhilosophyDialog = true }
                )
            }

            // ==========================================
            // TAB CONTENT
            // ==========================================
            when (selectedTab) {
                0 -> {
                    // TAB 0: MERIT DOSSIER & OVERVIEW
                    item {
                        Text(
                            text = "Core Deliverables & Engineering Accomplishments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 4 KPI Tiles
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileKpiCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Shield,
                                iconColor = HunarSuccess,
                                value = "${userProfile.zeroDefectStreakDays} Days",
                                label = "Zero Defect Streak",
                                detail = "Zero critical regressions"
                            )
                            ProfileKpiCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AccountBalanceWallet,
                                iconColor = HunarOrange,
                                value = "₹${userProfile.earnings.totalEarningsRupees.toInt()}",
                                label = "Verified Earnings",
                                detail = "100% Talent-earned"
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileKpiCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.FactCheck,
                                iconColor = HunarNavyLight,
                                value = "${userProfile.slaComplianceRate}%",
                                label = "SLA Adherence",
                                detail = "${userProfile.deliverablesCompletedCount} deliverables live"
                            )
                            ProfileKpiCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.VerifiedUser,
                                iconColor = Color(0xFF8B5CF6),
                                value = userProfile.crashFreeRate,
                                label = "Crash-Free Rate",
                                detail = "1.2M+ live user sessions"
                            )
                        }
                    }

                    // Anti-vanity policy notice
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "HUNAR Zero-Vanity Clout Guarantee",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = HunarNavy
                                    )
                                    Text(
                                        text = "On HUNAR, ranking and recognition are determined strictly by verified skill proficiency, code deliverables, and peer audits. Follower counts and superficial likes are removed from public influence.",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Top Skills Section preview
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Top Verified Skills",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { selectedTab = 1 }) {
                                Text("View All (${userProfile.skills.size})", color = HunarOrange, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(userProfile.skills.take(2), key = { it.id }) { skill ->
                        UserSkillCard(
                            skill = skill,
                            onInspect = { selectedSkillForDetails = skill },
                            onEndorse = {
                                onEndorseSkill(skill.id)
                                Toast.makeText(context, "Endorsed ${skill.name}!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Top Achievements Section preview
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Audited Achievements",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { selectedTab = 2 }) {
                                Text("View All (${userProfile.achievements.size})", color = HunarOrange, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(userProfile.achievements.take(2), key = { it.id }) { achievement ->
                        UserAchievementCard(
                            achievement = achievement,
                            onInspect = { selectedAchievementForAudit = achievement }
                        )
                    }
                }

                1 -> {
                    // TAB 1: VERIFIED SKILLS (FULL LIST)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Demonstrable Skill Proficiencies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Every proficiency is validated through real repository code audits, latency profiling benchmarks, and peer sign-offs.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(userProfile.skills, key = { it.id }) { skill ->
                        UserSkillCard(
                            skill = skill,
                            onInspect = { selectedSkillForDetails = skill },
                            onEndorse = {
                                onEndorseSkill(skill.id)
                                Toast.makeText(context, "Endorsed ${skill.name}!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                2 -> {
                    // TAB 2: ACHIEVEMENTS & MILESTONES
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Audited Achievements & Impact",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Concrete milestones with measurable outcomes that prove technical and community contribution.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(userProfile.achievements, key = { it.id }) { achievement ->
                        UserAchievementCard(
                            achievement = achievement,
                            onInspect = { selectedAchievementForAudit = achievement }
                        )
                    }
                }

                3 -> {
                    // TAB 3: TRANSPARENT EARNINGS & MONETIZATION
                    item {
                        UserEarningsLedgerCard(
                            earnings = userProfile.earnings,
                            onRequestWithdraw = { showWithdrawDialog = true }
                        )
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // ==========================================
    // DIALOGS & MODALS
    // ==========================================

    // 1. Withdrawal Dialog
    if (showWithdrawDialog) {
        var withdrawAmountText by remember { mutableStateOf("${userProfile.earnings.withdrawableRupees.toInt()}") }
        var upiText by remember { mutableStateOf(userProfile.earnings.upiId) }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text(
                    text = "Request Merit Payout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Withdrawable balance: ₹${userProfile.earnings.withdrawableRupees.toInt()}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HunarSuccess
                    )
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = upiText,
                        onValueChange = { upiText = it },
                        label = { Text("UPI ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && amount <= userProfile.earnings.withdrawableRupees && upiText.isNotBlank()) {
                            onWithdrawSubmit(amount, upiText.trim())
                            showWithdrawDialog = false
                            Toast.makeText(context, "Withdrawal of ₹${amount.toInt()} requested to $upiText!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please enter a valid amount and UPI ID.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Submit Withdrawal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Export Dossier Dialog
    if (showExportDossierDialog) {
        val dossierSummary = remember(userProfile) {
            buildString {
                appendLine("=== HUNAR MERIT PROFILE DOSSIER ===")
                appendLine("Creator: ${userProfile.name} (${userProfile.handle})")
                appendLine("Talent Merit Score: ${userProfile.talentMeritScore}/100 [${userProfile.meritTier}]")
                appendLine("Motto: ${userProfile.meritPhilosophy}")
                appendLine()
                appendLine("--- CORE VERIFIED BENCHMARKS ---")
                appendLine("• Zero Defect Streak: ${userProfile.zeroDefectStreakDays} Days")
                appendLine("• Deliverables Completed: ${userProfile.deliverablesCompletedCount}")
                appendLine("• SLA Delivery Compliance: ${userProfile.slaComplianceRate}%")
                appendLine("• Crash-Free Rate: ${userProfile.crashFreeRate}")
                appendLine("• Verified Total Earnings: ₹${userProfile.earnings.totalEarningsRupees.toInt()}")
                appendLine()
                appendLine("--- VERIFIED SKILLS ---")
                userProfile.skills.forEach { s ->
                    appendLine("• ${s.name} (${s.masteryLevel}% Mastery) - ${s.experienceYears} yrs, ${s.peerEndorsementsCount} endorsements")
                }
                appendLine()
                appendLine("--- AUDITED ACHIEVEMENTS ---")
                userProfile.achievements.forEach { a ->
                    appendLine("• ${a.title}: ${a.metricProof} [${a.verificationAuditId}]")
                }
                appendLine()
                appendLine("Cryptographically Audited on HUNAR Platform.")
            }
        }

        Dialog(onDismissRequest = { showExportDossierDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Export Merit Dossier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showExportDossierDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(
                        text = "Unforgeable evidence of your engineering deliverables, verified skills, and pure talent.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = dossierSummary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showExportDossierDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close")
                        }
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("HUNAR Merit Dossier", dossierSummary)
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, "Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Dossier", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 3. Philosophy Info Dialog
    if (showPhilosophyDialog) {
        Dialog(onDismissRequest = { showPhilosophyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "HUNAR: Merit-First Philosophy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HunarNavy
                    )
                    Text(
                        text = "“Mehnat aur Talent ko Attention, sirf Looks aur Glamour ko nahi.”",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HunarOrangeDark
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = HunarSuccess.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "What makes HUNAR different:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = HunarSuccess
                            )
                            Text(
                                text = "• Discoverability is backed by real skill scores & deliverables, never appearance.\n" +
                                        "• Zero vanity metrics: No public like counters or follower clout games.\n" +
                                        "• Equal earning opportunities for programmers, artisans, and teachers.\n" +
                                        "• Strict anti-vulgarity AI moderation ensures respect for authentic craftsmanship.",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showPhilosophyDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Understood", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 4. Skill Details Dialog
    selectedSkillForDetails?.let { skill ->
        Dialog(onDismissRequest = { selectedSkillForDetails = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Audited Badge: ${skill.verificationBadge}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = HunarNavyLight
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HunarNavy.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "BENCHMARK PROOF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarOrange
                            )
                            Text(
                                text = skill.benchmarkProof,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "Key Verified Strengths:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    skill.keyStrengths.forEach { str ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = HunarSuccess, modifier = Modifier.size(14.dp))
                            Text(text = str, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { selectedSkillForDetails = null },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 5. Achievement Audit Dialog
    selectedAchievementForAudit?.let { achievement ->
        Dialog(onDismissRequest = { selectedAchievementForAudit = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = achievement.badgeIconEmoji, fontSize = 28.sp)
                        Column {
                            Text(
                                text = achievement.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Earned: ${achievement.dateEarned}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = achievement.description,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HunarSuccess.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "METRIC PROOF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarSuccess
                            )
                            Text(
                                text = achievement.metricProof,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarSuccess
                            )
                        }
                    }

                    Text(
                        text = "Audit Hash: ${achievement.verificationAuditId}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = HunarNavyMuted
                    )

                    Button(
                        onClick = { selectedAchievementForAudit = null },
                        colors = ButtonDefaults.buttonColors(containerColor = HunarNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Top hero card highlighting the user's verified identity and merit score.
 */
@Composable
fun UserProfileHeroCard(
    userProfile: UserProfile,
    onPhilosophyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_hero_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HunarNavy),
        border = BorderStroke(1.dp, HunarNavyBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(HunarNavy, HunarNavyLight)
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Avatar, Name, Handle, Merit Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (userProfile.avatarDrawableRes != null) {
                            Image(
                                painter = painterResource(id = userProfile.avatarDrawableRes),
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, HunarOrange, CircleShape)
                            )
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = HunarOrange,
                                modifier = Modifier.size(62.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = userProfile.avatarEmoji, fontSize = 28.sp)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (userProfile.isSkillVerified) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Talent",
                                        tint = HunarSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = userProfile.handle,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = userProfile.verifiedSkillTitle,
                                color = HunarOrange,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Merit Score Circle
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, HunarOrange.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${userProfile.talentMeritScore}",
                                color = HunarGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "MERIT",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bio
                Text(
                    text = userProfile.bio,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )

                // Merit-First Philosophy Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPhilosophyClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = HunarOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = userProfile.meritPhilosophy,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = userProfile.rejectionOfVanityMotto,
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * KPI card for user profile metrics.
 */
@Composable
fun ProfileKpiCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    detail: String
) {
    Card(
        modifier = modifier.testTag("kpi_card_${label.replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = iconColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * User skill card with mastery progress bar and endorsement button.
 */
@Composable
fun UserSkillCard(
    skill: UserSkill,
    onInspect: () -> Unit,
    onEndorse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_skill_card_${skill.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${skill.experienceYears} yrs experience • ${skill.completedProjectsCount} completed projects",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HunarOrange.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, HunarOrange.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${skill.masteryLevel}% Mastery",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HunarOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { skill.masteryLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = HunarOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HunarNavy.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = HunarOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = skill.benchmarkProof,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${skill.peerEndorsementsCount} peer endorsements",
                    fontSize = 11.sp,
                    color = HunarSuccess,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onEndorse,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Endorse", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = onInspect,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HunarNavyLight,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Audit Badge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * User achievement card showing the audited metric proof.
 */
@Composable
fun UserAchievementCard(
    achievement: UserAchievement,
    onInspect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_achievement_card_${achievement.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = achievement.badgeIconEmoji, fontSize = 22.sp)
                    Column {
                        Text(
                            text = achievement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Earned ${achievement.dateEarned}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (achievement.isNationalRecognition) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HunarOrange.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "NATIONAL",
                            color = HunarOrange,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = achievement.description,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HunarSuccess.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VERIFIED METRIC PROOF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HunarSuccess
                    )
                    Text(
                        text = achievement.metricProof,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HunarSuccess
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.verificationAuditId,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = HunarNavyMuted
                )
                TextButton(onClick = onInspect) {
                    Text("Audit Details", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = HunarOrange)
                }
            }
        }
    }
}

/**
 * Transparent earnings ledger card detailing skill-based monetization.
 */
@Composable
fun UserEarningsLedgerCard(
    earnings: UserEarningsSummary,
    onRequestWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_earnings_ledger_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Verified Skill Earnings",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${earnings.totalEarningsRupees.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = HunarOrange
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HunarSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "VERIFIED LEDGER",
                        color = HunarSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Balances Split Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = HunarSuccess.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Withdrawable Balance", fontSize = 10.5.sp, color = HunarSuccess, fontWeight = FontWeight.Bold)
                        Text(text = "₹${earnings.withdrawableRupees.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HunarSuccess)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = HunarWarning.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, HunarWarning.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Pending Clearance", fontSize = 10.5.sp, color = HunarWarning, fontWeight = FontWeight.Bold)
                        Text(text = "₹${earnings.pendingClearanceRupees.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HunarWarning)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Breakdown by Source
            Text(
                text = "TRANSPARENT REVENUE BREAKDOWN:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            EarningsSourceLine(label = "Skill Tutoring & Workshops", amount = earnings.skillTutoringRupees, icon = "👨‍🏫")
            EarningsSourceLine(label = "Direct Shabaashi (Appreciation Tips)", amount = earnings.directShabaashiTipsRupees, icon = "👏")
            EarningsSourceLine(label = "Open-Source Engineering Grants", amount = earnings.openSourceGrantsRupees, icon = "💻")
            EarningsSourceLine(label = "Merit Challenge Community Prizes", amount = earnings.meritChallengePrizesRupees, icon = "🏆")

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // UPI Account and Withdraw button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Payout UPI ID", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = earnings.upiId, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = onRequestWithdraw,
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Withdraw Funds", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun EarningsSourceLine(
    label: String,
    amount: Double,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(text = "₹${amount.toInt()}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
