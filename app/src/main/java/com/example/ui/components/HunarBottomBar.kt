package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * HUNAR 4 Main Bottom Navigation Tabs (Fixed Horizontal Bottom Bar - Icons Only):
 *
 * 1. HOME 🏠 (Active icon orange #FF6B00)
 * 2. REELS + SEARCH 🎬
 * 3. MESSAGES 💬 (With red 4 badge)
 * 4. PROFILE 👤
 *
 * Design Requirements:
 * - Keep bottom navigation bar FIXED at bottom of screen in horizontal row.
 * - Do NOT move to top or side.
 * - Keep 4 icons exactly where they are at bottom - Home, Reels & Search, Messages with red badge 4, Profile.
 * - REMOVE all text labels below icons - delete text "Home", "Reels & Search", "Messages", "Profile". Show icons only.
 * - White background for bottom bar (#FFFFFF).
 * - Icons color dark navy blue #0A1931, active Home icon orange #FF6B00.
 * - Keep notification badge 4 on Messages.
 */
enum class HunarScreenTab(val title: String, val testTagId: String) {
    HOME("Home", "nav_home"),
    REELS_SEARCH("Reels & Search", "nav_reels_search"),
    MESSAGES("Messages", "nav_messages"),
    PROFILE("Profile", "nav_profile")
}

@Composable
fun HunarBottomBar(
    currentTab: HunarScreenTab,
    unreadMessagesCount: Int = 4,
    onTabSelected: (HunarScreenTab) -> Unit
) {
    Surface(
        color = HunarPureWhite, // Pure White #FFFFFF background for bottom bar
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hunar_bottom_bar")
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(
                color = HunarCardLightGreyBorder,
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(HunarPureWhite)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: HOME 🏠 - Active icon orange #FF6B00, icons only, no text label
                HunarBottomNavIconOnlyItem(
                    contentDescription = "Home",
                    outlineIconRes = R.drawable.ic_hunar_nav_home,
                    activeIconRes = R.drawable.ic_hunar_nav_home_active,
                    isSelected = currentTab == HunarScreenTab.HOME,
                    isHomeOrange = true,
                    testTag = "nav_home",
                    onClick = { onTabSelected(HunarScreenTab.HOME) }
                )

                // Tab 2: REELS + SEARCH 🎬 - Icons only, dark navy blue #0A1931 inactive, orange when selected
                HunarBottomNavIconOnlyItem(
                    contentDescription = "Reels & Search",
                    outlineIconRes = R.drawable.ic_hunar_nav_reels_search,
                    activeIconRes = R.drawable.ic_hunar_nav_reels_search_active,
                    isSelected = currentTab == HunarScreenTab.REELS_SEARCH,
                    testTag = "nav_reels_search",
                    onClick = { onTabSelected(HunarScreenTab.REELS_SEARCH) }
                )

                // Tab 3: MESSAGES 💬 - Icons only, dark navy blue #0A1931, with red 4 badge
                HunarBottomNavIconOnlyItem(
                    contentDescription = "Messages",
                    outlineIconRes = R.drawable.ic_hunar_nav_messages,
                    activeIconRes = R.drawable.ic_hunar_nav_messages_active,
                    isSelected = currentTab == HunarScreenTab.MESSAGES,
                    badgeCount = if (unreadMessagesCount > 0) unreadMessagesCount else 4,
                    testTag = "nav_messages",
                    onClick = { onTabSelected(HunarScreenTab.MESSAGES) }
                )

                // Tab 4: PROFILE 👤 - Icons only, dark navy blue #0A1931 inactive, orange when selected
                HunarBottomNavIconOnlyItem(
                    contentDescription = "Profile",
                    outlineIconRes = R.drawable.ic_hunar_nav_profile,
                    activeIconRes = R.drawable.ic_hunar_nav_profile_active,
                    isSelected = currentTab == HunarScreenTab.PROFILE,
                    testTag = "nav_profile",
                    onClick = { onTabSelected(HunarScreenTab.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun HunarBottomNavIconOnlyItem(
    contentDescription: String,
    outlineIconRes: Int,
    activeIconRes: Int,
    isSelected: Boolean,
    isHomeOrange: Boolean = false,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    // Requirements: Icons color dark navy blue #0A1931, active Home icon orange #FF6B00
    val activeColor = HunarOrange
    val inactiveColor = HunarNavy // #0A1931 Dark Navy Blue

    val targetColor = if (isSelected || isHomeOrange) activeColor else inactiveColor
    val iconColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 180),
        label = "nav_icon_color"
    )

    val pillBackground = if (isSelected) {
        HunarOrange.copy(alpha = 0.14f)
    } else {
        Color.Transparent
    }

    // Touch target minimum 48dp x 48dp (68dp x 50dp here for comfortable touch accessibility)
    Box(
        modifier = Modifier
            .widthIn(min = 68.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Pill highlight for selected icon (strictly icons only, no text underneath)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(pillBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    painter = painterResource(id = if (isSelected) activeIconRes else outlineIconRes),
                    contentDescription = contentDescription, // Preserved for TalkBack accessibility
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )

                // Red 4 Badge for Messages
                if (badgeCount > 0) {
                    Surface(
                        color = HunarError, // Vibrant red badge
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = 10.dp, y = (-6).dp)
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            color = HunarPureWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.5.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}
