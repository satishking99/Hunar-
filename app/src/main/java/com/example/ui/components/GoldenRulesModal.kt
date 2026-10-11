package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun GoldenRulesModal(
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
                .testTag("golden_rules_modal")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "4 GOLDEN CONTENT RULES",
                        color = HunarOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "HUNAR Constitution",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hunar is India's dedicated talent platform. We strictly moderate against non-talent, zero-effort content to protect genuine hardworking artists, creators, and workers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Rule Cards
            GoldenRuleCard(
                number = "01",
                title = "Strictly ZERO Adult / Vulgar Content",
                description = "Any nudity, suggestive dances, obscene language or double-meaning content leads to an INSTANT PERMANENT BAN. Hunar is a safe space for all generations.",
                badge = "ZERO TOLERANCE"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GoldenRuleCard(
                number = "02",
                title = "No Beauty Without Skill / No Plain Selfie Flexing",
                description = "Looks, outfits, cosmetics, or lifestyle flexing without demonstrable skill are prohibited. Merit & mastery come first.",
                badge = "TALENT FIRST"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GoldenRuleCard(
                number = "03",
                title = "No Low-Effort Lip-Syncing",
                description = "Simply mouthing someone else's audio without original acting, mimicry skill, or physical mastery is penalized. Original talent always gets priority.",
                badge = "NO RE-PACKAGING"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GoldenRuleCard(
                number = "04",
                title = "No Vulgar Double-Meaning Gestures or Slang",
                description = "Slang, derogatory body movements, or bullying under the guise of comedy will result in immediate strike and loss of creator monetization privileges.",
                badge = "RESPECT & DIGNITY"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Strike Rule explanation
            Surface(
                color = HunarError.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚖️ Transparent 3-Strike System",
                        fontWeight = FontWeight.Bold,
                        color = HunarError,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Strike 1: 24-hr post shadowban + educational warning\n• Strike 2: 7-day monetization freeze & upload lockout\n• Strike 3: Permanent profile deletion with Aadhaar/PAN blocklist",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("I Support Pure Talent 🇮🇳", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GoldenRuleCard(
    number: String,
    title: String,
    description: String,
    badge: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RULE $number",
                    color = HunarOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Surface(
                    color = HunarOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        color = HunarOrange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
