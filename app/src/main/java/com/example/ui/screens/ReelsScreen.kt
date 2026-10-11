package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Reel
import com.example.ui.theme.*

@Composable
fun ReelsScreen(
    reels: List<Reel>,
    onSaveReel: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentReel = reels.getOrNull(currentIndex) ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HunarNavy)
            .testTag("reels_screen_container")
    ) {
        // Full Screen Vertical Video / Image Backdrop (Matching Screen 3)
        if (currentReel.drawableRes != null) {
            Image(
                painter = painterResource(id = currentReel.drawableRes),
                contentDescription = currentReel.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(HunarNavyLight, HunarNavy, HunarNavy)
                        )
                    )
            )
        }

        // Gradient shadows for text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HunarNavy.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Transparent,
                            HunarNavy.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Bar: [<   Reels • Art   📷] (Matching Screen 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = HunarWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Reels • ${currentReel.skillCategory.displayName}",
                    color = HunarWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera",
                    tint = HunarWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Right Action Rail: Requirement 2 — Only Show the Save Option (Save Privately, No Public Counts)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Private Save / Bookmark Action
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (currentReel.isSaved) HunarOrange else HunarNavy.copy(alpha = 0.75f))
                    .clickable { onSaveReel(currentReel.id) }
                    .testTag("reel_save_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (currentReel.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (currentReel.isSaved) "Saved to Private Collection" else "Save Privately",
                    tint = HunarWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Private indicator badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HunarNavy.copy(alpha = 0.8f)
            ) {
                Text(
                    text = "Save Only 🔒",
                    color = HunarWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // Bottom Overlay (Matching Screen 3)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Profile Row: Circular Avatar + @priyacreates ✓
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HunarDarkCardElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎨", fontSize = 20.sp)
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentReel.creator.handle,
                            color = HunarWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(HunarOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                    Text(
                        text = "${currentReel.creator.name} • ${currentReel.skillCategory.displayName}",
                        color = HunarTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Caption lines:
            // "Quick watercolor tip 🎨"
            // "Layering = real looking petals! Save for later"
            // "#art #watercolor #hunar #talentfirst"
            Text(
                text = currentReel.title,
                color = HunarWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = currentReel.caption,
                color = HunarWhite.copy(alpha = 0.95f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth(0.78f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Audio: 🎵 Original Audio • priyacreates
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music",
                    tint = HunarWhite,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = currentReel.soundTrackTitle,
                    color = HunarWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Progress Scrubber (Matching Screen 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Background track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                )
                // Filled active progress (orange)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(HunarOrange)
                )
                // Scrubber thumb dot
                Box(
                    modifier = Modifier
                        .offset(x = 240.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HunarWhite)
                )
            }
        }
    }
}
