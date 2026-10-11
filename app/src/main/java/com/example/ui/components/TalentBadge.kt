package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategory
import com.example.ui.theme.*

@Composable
fun TalentBadge(
    score: Int,
    category: SkillCategory? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HunarOrange.copy(alpha = 0.15f))
            .border(1.dp, HunarOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = category?.icon ?: "⚡", fontSize = 11.sp)
            Text(
                text = "Score: $score",
                color = HunarOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RuleAuditPill(
    passed: Boolean,
    title: String,
    modifier: Modifier = Modifier
) {
    val pillBg = if (passed) HunarSuccess.copy(alpha = 0.15f) else HunarError.copy(alpha = 0.15f)
    val pillBorder = if (passed) HunarSuccess.copy(alpha = 0.4f) else HunarError.copy(alpha = 0.4f)
    val textColor = if (passed) HunarSuccess else HunarError
    val icon = if (passed) "✓" else "✕"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillBg)
            .border(1.dp, pillBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$icon $title",
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
