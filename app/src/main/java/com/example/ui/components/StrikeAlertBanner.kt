package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StrikeStatus
import com.example.ui.theme.*

/**
 * 4 Golden Content Rules Banner:
 * Designed with Dark Navy Blue gradient #0A1931 to #1A2E4A, crisp white text inside, premium look.
 */
@Composable
fun StrikeAlertBanner(
    strikeStatus: StrikeStatus,
    onViewRulesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strikes = strikeStatus.currentStrikes

    // Dark Navy Blue Gradient: #0A1931 to #1A2E4A per user design requirements
    val navyGradient = Brush.horizontalGradient(
        colors = listOf(
            HunarNavy,       // #0A1931 Dark Navy Blue
            HunarNavyLight   // #1A2E4A
        )
    )

    val backgroundModifier = if (strikes > 0) {
        Modifier.background(HunarError.copy(alpha = 0.15f))
    } else {
        Modifier.background(navyGradient)
    }

    val borderColor = if (strikes > 0) HunarError else HunarNavyLight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(backgroundModifier)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onViewRulesClick() }
            .padding(14.dp)
            .testTag("strike_alert_banner")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (strikes > 0) HunarError.copy(alpha = 0.25f)
                        else HunarOrange.copy(alpha = 0.22f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (strikes > 0) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = HunarError,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "🛡️", fontSize = 22.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (strikes == 0) "4 Golden Content Rules: Clean Standing"
                               else "Warning: Strike $strikes/3 Active",
                        color = if (strikes > 0) HunarError else Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (strikes == 0)
                        "Mehnat & Talent First! AI monitors modesty, zero lip-sync & pure skill quality."
                    else if (strikes == 1)
                        "Strike 1: Upload blocked for 24 hours. Zero earning on violating video."
                    else if (strikes == 2)
                        "Strike 2: Upload blocked for 7 days. Tap to review violations."
                    else
                        "Strike 3: Upload button removed permanently from app. Tap to appeal.",
                    color = if (strikes > 0) HunarTextSecondary else Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Surface(
                color = HunarOrange,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Text(
                    text = "Rules >",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
