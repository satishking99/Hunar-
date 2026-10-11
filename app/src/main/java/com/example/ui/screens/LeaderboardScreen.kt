package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.BrandCollab
import com.example.data.model.SkillCategory
import com.example.data.model.User
import com.example.ui.components.TalentBadge
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    brandCollabs: List<BrandCollab>,
    onApplyCollab: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Merit Leaderboard, 1: Brand Collabs

    val meritRanking = remember {
        listOf(
            User("u4", "Vikram Rathore", "@vikram_botics", "🤖", SkillCategory.TECH, 97, "Autonomous Robotics"),
            User("u1", "Priya Verma", "@priya_calisthenics", "🤸‍♀️", SkillCategory.FITNESS, 96, "Calisthenics Athlete"),
            User("u_me", "Rahul Sharma (You)", "@rahul_tech_dev", "⚡", SkillCategory.TECH, 95, "Android Architect"),
            User("u2", "Aman Saxena", "@aman_sitar_craft", "🪕", SkillCategory.MUSIC, 94, "Classical Sitar"),
            User("u6", "Kavita Reddy", "@kavita_esports", "🎮", SkillCategory.GAMING, 93, "Strategy Esports"),
            User("u3", "Neha Patil", "@neha_pottery_art", "🏺", SkillCategory.ART, 92, "Terracotta Potter"),
            User("u5", "Farhan Khan", "@farhan_skits", "🎭", SkillCategory.COMEDY, 91, "Observational Satirist")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("leaderboard_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Merit Hub",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = HunarTextPrimary
                    )
                    Text(
                        text = "Zero Vanity Bias • 50/50 Gender Equal Algorithm",
                        color = HunarOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Subtabs: Leaderboard vs Brand Collabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HunarDarkCard)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 0) HunarOrange else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆 Weekly Merit Ranking",
                        color = if (selectedTab == 0) HunarBlackDeep else HunarTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 1) HunarOrange else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤝 Brand Collab Hub",
                        color = if (selectedTab == 1) HunarBlackDeep else HunarTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Merit Ranking Explanation
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HunarDarkSurface)
                        .border(1.dp, HunarBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "⚖️", fontSize = 20.sp)
                        Column {
                            Text(
                                text = "Rule 4: Merit Algorithm Guarantee",
                                color = HunarTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rankings are based 100% on watch time, technique difficulty & community Shabaashi tips. Male and female creators receive equal algorithmic distribution.",
                                color = HunarTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Leaderboard list
            itemsIndexed(meritRanking) { index, user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.handle == "@rahul_tech_dev") HunarOrange.copy(alpha = 0.1f) else HunarDarkSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.handle == "@rahul_tech_dev") HunarOrange else HunarBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Rank Number with medals
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (index) {
                                            0 -> HunarGold
                                            1 -> Color(0xFFC0C0C0)
                                            2 -> Color(0xFFCD7F32)
                                            else -> HunarDarkCard
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = if (index < 3) HunarBlackDeep else HunarTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(HunarDarkCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = user.avatarEmoji, fontSize = 20.sp)
                            }

                            Column {
                                Text(
                                    text = user.name,
                                    color = HunarTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${user.primarySkill.icon} ${user.verifiedSkillTitle}",
                                    color = HunarOrangeLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        TalentBadge(score = user.talentScore)
                    }
                }
            }
        } else {
            // Brand Collaborations Tab
            item {
                Text(
                    text = "Skill-Based Sponsorship Campaigns",
                    color = HunarTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            items(brandCollabs) { collab ->
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = collab.brandName,
                                    color = HunarOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = collab.campaignTitle,
                                    color = HunarTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "₹${"%,d".format(collab.rewardRupees)}",
                                color = HunarSuccess,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Text(
                            text = collab.description,
                            color = HunarTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Requires: ${collab.requiredSkill.displayName} (${collab.minTalentScore}% Score)",
                                color = HunarTextMuted,
                                fontSize = 11.sp
                            )

                            Button(
                                onClick = { onApplyCollab(collab.id) },
                                enabled = !collab.isApplied,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (collab.isApplied) HunarDarkCard else HunarOrange
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (collab.isApplied) "Applied ✓" else "Apply (${collab.slotsRemaining} left)",
                                    color = if (collab.isApplied) HunarSuccess else HunarBlackDeep,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
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
