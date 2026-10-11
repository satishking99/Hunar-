package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reel
import com.example.data.model.SkillCategory
import com.example.data.model.User
import com.example.ui.components.TalentBadge
import com.example.ui.theme.*

/**
 * PAGE 2 — REELS + SEARCH 🔍
 * Combines Short videos / Reels, search bar (creators & hashtags), trending items,
 * and multiple interest selection (Education, Fitness, Sports, Gaming, Art, Music,
 * Dance, Cooking, Skills & Work, Technology, Motivation, Lifestyle, Beauty with Talent).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReelsSearchScreen(
    reels: List<Reel>,
    allCategories: List<SkillCategory>,
    selectedInterests: Set<SkillCategory>,
    isDarkActive: Boolean = false,
    onToggleInterest: (SkillCategory) -> Unit,
    onSaveReel: (String) -> Unit = {},
    onThemeToggleClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchTab by remember { mutableStateOf(0) } // 0 = Trending Reels, 1 = Creators, 2 = Hashtags

    // Sample trending hashtags
    val trendingHashtags = listOf(
        "#CodingSkill", "#FitnessReal", "#ClayArtCraft", "#AcousticLive",
        "#RoboticsIndia", "#StreetWorkout", "#IndianClassicalDance", "#HunarChallenge"
    )

    // Filtered Reels based on search query and selected interests
    val filteredReels = remember(reels, searchQuery, selectedInterests) {
        reels.filter { reel ->
            val matchesCategory = selectedInterests.isEmpty() ||
                    selectedInterests.contains(SkillCategory.ALL) ||
                    selectedInterests.contains(reel.skillCategory)

            val matchesQuery = searchQuery.isBlank() ||
                    reel.title.contains(searchQuery, ignoreCase = true) ||
                    reel.caption.contains(searchQuery, ignoreCase = true) ||
                    reel.creator.name.contains(searchQuery, ignoreCase = true) ||
                    reel.creator.handle.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("reels_search_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Header Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore & Reels 🔍",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = onThemeToggleClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("reels_theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkActive) "Switch to White Theme" else "Switch to Black Theme",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search creators, skills, #hashtags...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = HunarOrange
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HunarOrange,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reels_search_input")
                )
            }
        }

        // Interest Categories Selection (Multi-select)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Talent Interests (Tap to personalize)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${selectedInterests.size} selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = HunarOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Scrollable chip row for all 13+ categories
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(allCategories.filter { it != SkillCategory.ALL }) { cat ->
                        val isSelected = selectedInterests.contains(cat)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleInterest(cat) },
                            label = {
                                Text(
                                    text = "${cat.icon} ${cat.displayName}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HunarOrange,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Sub-Tabs (Trending Reels, Creators, Hashtags)
        item {
            TabRow(
                selectedTabIndex = searchTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = HunarOrange,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = searchTab == 0,
                    onClick = { searchTab = 0 },
                    text = { Text("Trending Reels", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = searchTab == 1,
                    onClick = { searchTab = 1 },
                    text = { Text("Top Creators", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = searchTab == 2,
                    onClick = { searchTab = 2 },
                    text = { Text("Hashtags", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        when (searchTab) {
            0 -> {
                // Short Videos / Reels Feed
                if (filteredReels.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reels found matching your interests or query.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredReels, key = { it.id }) { reel ->
                        ReelCard(
                            reel = reel,
                            onSave = { onSaveReel(reel.id) }
                        )
                    }
                }
            }
            1 -> {
                // Creators Search
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        sampleCreatorsList.forEach { creator ->
                            CreatorSearchItem(creator = creator)
                        }
                    }
                }
            }
            2 -> {
                // Hashtag Search
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trendingHashtags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchQuery = tag }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "🏷️", fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = tag,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Verified Talent Trend",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Explore ➔",
                                        color = HunarOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReelCard(
    reel: Reel,
    onSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("reel_card_${reel.id}")
    ) {
        Column {
            // Header Creator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (reel.creator.avatarDrawableRes != null) {
                        Image(
                            painter = painterResource(id = reel.creator.avatarDrawableRes),
                            contentDescription = reel.creator.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HunarOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = reel.creator.avatarEmoji, fontSize = 20.sp)
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = reel.creator.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TalentBadge(score = reel.creator.talentScore, category = reel.creator.primarySkill)
                        }
                        Text(
                            text = "${reel.creator.handle} • ${reel.skillCategory.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = HunarOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Verified Talent",
                        color = HunarOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Visual Thumbnail / Video simulation box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(HunarNavy),
                contentAlignment = Alignment.Center
            ) {
                if (reel.drawableRes != null) {
                    Image(
                        painter = painterResource(id = reel.drawableRes),
                        contentDescription = reel.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = reel.skillCategory.icon, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "▶ Short Skill Video",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(HunarNavy.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Caption & Actions
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = reel.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reel.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Requirement 2 — Only Show the Save Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onSave)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("save_reel_btn_${reel.id}")
                    ) {
                        Icon(
                            imageVector = if (reel.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (reel.isSaved) "Saved" else "Save Privately",
                            tint = if (reel.isSaved) HunarOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (reel.isSaved) "Saved to Collection" else "Save Privately",
                            color = if (reel.isSaved) HunarOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Private Engagement",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorSearchItem(creator: User) {
    var isFollowing by remember { mutableStateOf(creator.isFollowing) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
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
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(HunarOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = creator.avatarEmoji, fontSize = 24.sp)
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = creator.name,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TalentBadge(score = creator.talentScore, category = creator.primarySkill)
                    }
                    Text(
                        text = "${creator.handle} • ${creator.verifiedSkillTitle}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = { isFollowing = !isFollowing },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else HunarOrange,
                    contentColor = if (isFollowing) MaterialTheme.colorScheme.onSurface else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isFollowing) "Following" else "Follow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

val sampleCreatorsList = listOf(
    User("c1", "Priya Sharma", "@priya_art", "🎨", SkillCategory.ART, 96, "Artisan", "Handmade mandala & clay sculptures"),
    User("c2", "Vikram Rathore", "@vikram_fit", "💪", SkillCategory.FITNESS, 94, "Fitness", "Calisthenics & home workout without gym"),
    User("c3", "Chef Ananya", "@ananya_rasoi", "🍳", SkillCategory.COOKING, 95, "Chef", "Authentic regional Indian recipes from scratch"),
    User("c4", "Kabir Dance", "@kabir_steps", "💃", SkillCategory.DANCE, 93, "Choreographer", "Self-choreographed classical fusion")
)
