package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.PerformanceGraph
import com.example.ui.components.TalentBadge
import com.example.ui.theme.*

/**
 * PAGE 4 — PROFILE 👤
 * Includes:
 * - Profile photo, Username, Bio
 * - Followers, Following, Likes
 * - Posts, Reels, LIVE history
 * - Skills/interests tags
 * - Verification status & Merit Score
 * - Creator Dashboard embedded tab (Views, Watch time, Followers growth, Engagement, Estimated earnings, Tips, LIVE earnings, Brand earnings, Withdrawals)
 * - Settings
 */
@Composable
fun ProfileScreen(
    currentUser: User,
    earnings: CreatorEarnings,
    posts: List<Post>,
    reels: List<Reel>,
    savedPosts: List<Post> = emptyList(),
    savedReels: List<Reel> = emptyList(),
    strikeStatus: StrikeStatus = StrikeStatus(),
    skills: List<SkillProficiency> = emptyList(),
    projects: List<ProjectPortfolio> = emptyList(),
    summary: DataDrivenAccomplishmentsSummary = DataDrivenAccomplishmentsSummary(),
    userProfile: UserProfile = UserProfile(),
    selectedStatusFilter: ProjectStatus? = null,
    searchQuery: String = "",
    onStatusFilterChange: (ProjectStatus?) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onAddProject: (ProjectPortfolio) -> Unit = {},
    onAddSkill: (SkillProficiency) -> Unit = {},
    onEndorseSkill: (String) -> Unit = {},
    onWithdrawProfileSubmit: (Double, String) -> Unit = { _, _ -> },
    onEndorseUserProfileSkill: (String) -> Unit = {},
    isDarkActive: Boolean = false,
    onWithdrawSubmit: (Double, String) -> Unit,
    onUpdatePaymentMethod: (String) -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenLiveStream: () -> Unit,
    onViewRulesModal: () -> Unit = {},
    onToggleFollow: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onThemeToggleClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isCreatorDashboardOpen by remember { mutableStateOf(false) }
    var isSkillsPortfolioOpen by remember { mutableStateOf(false) }
    var isUserProfileViewOpen by remember { mutableStateOf(false) }
    var creatorDashboardInitialTab by remember { mutableStateOf(0) }

    var selectedProfileTab by remember { mutableStateOf(0) } // 0 = Works, 1 = Reels, 2 = Saved (Private), 3 = LIVE History, 4 = Creator Dashboard

    // 3-dot dropdown menu state
    var showMenu by remember { mutableStateOf(false) }

    // Dialog states for WhatsApp style menu items
    var showMyProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Withdraw dialog state
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("${earnings.totalBalanceRupees.toInt()}") }
    var upiIdText by remember { mutableStateOf(earnings.upiId) }

    BackHandler(enabled = isUserProfileViewOpen || isSkillsPortfolioOpen || isCreatorDashboardOpen) {
        if (isUserProfileViewOpen) {
            isUserProfileViewOpen = false
        } else if (isSkillsPortfolioOpen) {
            isSkillsPortfolioOpen = false
        } else if (isCreatorDashboardOpen) {
            isCreatorDashboardOpen = false
        }
    }

    when {
        isUserProfileViewOpen -> {
            UserProfileScreen(
                userProfile = userProfile,
                onWithdrawSubmit = onWithdrawProfileSubmit,
                onEndorseSkill = onEndorseUserProfileSkill,
                onBackClick = { isUserProfileViewOpen = false }
            )
        }
        isSkillsPortfolioOpen -> {
            SkillsPortfolioDashboardScreen(
                currentUser = currentUser,
                skills = skills,
                projects = projects,
                summary = summary,
                selectedStatusFilter = selectedStatusFilter,
                searchQuery = searchQuery,
                onStatusFilterChange = onStatusFilterChange,
                onSearchQueryChange = onSearchQueryChange,
                onAddProject = onAddProject,
                onAddSkill = onAddSkill,
                onEndorseSkill = onEndorseSkill,
                onBackClick = { isSkillsPortfolioOpen = false }
            )
        }
        isCreatorDashboardOpen -> {
            CreatorDashboardScreen(
                currentUser = currentUser,
                earnings = earnings,
                strikeStatus = strikeStatus,
                onWithdrawSubmit = onWithdrawSubmit,
                onUpdatePaymentMethod = onUpdatePaymentMethod,
                initialTab = creatorDashboardInitialTab,
                skills = skills,
                projects = projects,
                summary = summary,
                selectedStatusFilter = selectedStatusFilter,
                searchQuery = searchQuery,
                onStatusFilterChange = onStatusFilterChange,
                onSearchQueryChange = onSearchQueryChange,
                onAddProject = onAddProject,
                onAddSkill = onAddSkill,
                onEndorseSkill = onEndorseSkill,
                onViewRulesModal = onViewRulesModal,
                onBackClick = { isCreatorDashboardOpen = false }
            )
        }
        else -> {
            LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Username + 3-Dot Menu
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentUser.handle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HunarNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (currentUser.isSkillVerified) {
                        Surface(
                            color = HunarSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✓ VERIFIED TALENT",
                                color = HunarSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top right corner: Vertical 3 dots ⋮ icon (dark navy blue #0A1931) with WhatsApp-style dropdown
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("profile_more_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Options",
                            tint = HunarNavy, // #0A1931 Dark Navy Blue
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 3 dots dropdown menu: White background, shadow, rounded corners
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = HunarPureWhite,
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HunarCardLightGreyBorder),
                        modifier = Modifier
                            .widthIn(min = 220.dp)
                            .testTag("profile_dropdown_menu")
                    ) {
                        // 1. My Profile (person icon)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Merit Profile",
                                    color = HunarNavy,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                isUserProfileViewOpen = true
                            },
                            modifier = Modifier.testTag("menu_item_my_profile")
                        )

                        // 2. Settings (gear icon)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Settings",
                                    color = HunarNavy,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            },
                            modifier = Modifier.testTag("menu_item_settings")
                        )

                        // 3. Language / भाषा (language icon) - NEW
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Language / भाषा",
                                    color = HunarNavy,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = HunarOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onLanguageClick()
                            },
                            modifier = Modifier.testTag("menu_item_language")
                        )

                        // 4. Privacy (lock icon)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Privacy",
                                    color = HunarNavy,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                showPrivacyDialog = true
                            },
                            modifier = Modifier.testTag("menu_item_privacy")
                        )

                        // 5. Help (question icon)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Help",
                                    color = HunarNavy,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = HunarNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                showHelpDialog = true
                            },
                            modifier = Modifier.testTag("menu_item_help")
                        )

                        HorizontalDivider(
                            color = HunarCardLightGreyBorder,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // 6. Logout (logout icon)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Logout",
                                    color = HunarError,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = HunarError,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onLogoutClick()
                            },
                            modifier = Modifier.testTag("menu_item_logout")
                        )
                    }
                }
            }
        }

        // Profile Bio & Header Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Photo
                    if (currentUser.avatarDrawableRes != null) {
                        Image(
                            painter = painterResource(id = currentUser.avatarDrawableRes),
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, HunarOrange, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(HunarOrange.copy(alpha = 0.15f))
                                .border(2.dp, HunarOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentUser.avatarEmoji, fontSize = 36.sp)
                        }
                    }

                    // Privacy-First Stat Counters (No public counts for Likes, Views, Followers, Following)
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatColumn(count = "${posts.size}", label = "Works")
                        ProfileStatColumn(count = "${currentUser.talentScore}", label = "Merit Score")
                        ProfileStatColumn(count = "Private 🔒", label = "Network")
                    }
                }

                // Privacy Protection Notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HunarNavy.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = HunarNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Followers, following lists & popularity metrics are strictly private.",
                            fontSize = 11.5.sp,
                            color = HunarNavy,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Name & Verified Badge
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TalentBadge(score = currentUser.talentScore, category = currentUser.primarySkill)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Skills & Interests Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentUser.interests.forEach { interest ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${interest.icon} ${interest.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Profile Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            creatorDashboardInitialTab = 0
                            isCreatorDashboardOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HunarOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Protected Dashboard 🔒", fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onOpenLiveStream,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HunarError.copy(alpha = 0.15f),
                            contentColor = HunarError
                        )
                    ) {
                        Text("Go LIVE 🔴", fontWeight = FontWeight.Bold)
                    }
                }

                // 🌟 Merit-First User Profile Hero Banner (Talent > Glamour)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isUserProfileViewOpen = true }
                        .testTag("banner_user_profile_merit"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarOrange),
                    border = BorderStroke(1.dp, HunarOrangeDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Merit Profile • Score ${userProfile.talentMeritScore}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = HunarNavy
                                    ) {
                                        Text(
                                            text = "MERIT FIRST",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Talent > Glamour • ${userProfile.skills.size} Skills • ${userProfile.achievements.size} Achievements • ₹${userProfile.earnings.totalEarningsRupees.toInt()}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Merit Profile",
                            tint = Color.White
                        )
                    }
                }

                // Data-Driven Accomplishments Hero Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSkillsPortfolioOpen = true }
                        .testTag("banner_skills_portfolio_hub"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = HunarNavy),
                    border = BorderStroke(1.dp, HunarNavyBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HunarOrange,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FolderSpecial,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Skills & Portfolio Hub",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = HunarSuccess.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "DATA-DRIVEN",
                                            color = HunarSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "0 Prod Defects • ₹48.5L ROI • ${projects.size} Deliverables",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Portfolio",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Profile Content Tabs (Portfolio, Works, Reels, Saved (Private), LIVE History, Creator Dashboard)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedProfileTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = HunarOrange,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedProfileTab == 0,
                    onClick = {
                        isSkillsPortfolioOpen = true
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(15.dp))
                            Text("Portfolio (${projects.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = false,
                    onClick = {
                        isUserProfileViewOpen = true
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(15.dp))
                            Text("Merit Profile 🎖️", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedProfileTab == 1,
                    onClick = { selectedProfileTab = 1 },
                    text = { Text("Works (${posts.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedProfileTab == 2,
                    onClick = { selectedProfileTab = 2 },
                    text = { Text("Reels (${reels.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedProfileTab == 3,
                    onClick = { selectedProfileTab = 3 },
                    text = { Text("🔖 Saved (${savedPosts.size + savedReels.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedProfileTab == 4,
                    onClick = { selectedProfileTab = 4 },
                    text = { Text("LIVE History", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedProfileTab == 5,
                    onClick = {
                        creatorDashboardInitialTab = 1
                        isCreatorDashboardOpen = true
                    },
                    text = { Text("💰 Earnings", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        when (selectedProfileTab) {
            0, 1 -> {
                // User Works
                if (posts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No works published yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(posts, key = { it.id }) { post ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = post.skillCategory.icon, fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = post.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${post.timeAgo} • ${post.skillCategory.displayName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HunarOrange.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Skill Verified",
                                        color = HunarOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // User Reels
                if (reels.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No reels published yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(reels, key = { it.id }) { reel ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "🎬", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reel.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${reel.verifiedSkillHighlight} • ${reel.skillCategory.displayName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HunarOrange.copy(alpha = 0.12f)
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
                        }
                    }
                }
            }
            2 -> {
                // 🔖 Saved (Private Collection - Only the account owner can access)
                val allSaved = savedPosts + savedReels.map { reel ->
                    Post(
                        id = reel.id,
                        creator = reel.creator,
                        title = reel.title,
                        caption = reel.caption,
                        skillCategory = reel.skillCategory,
                        drawableRes = reel.drawableRes,
                        timeAgo = "Saved Reel",
                        isSaved = true
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HunarNavy.copy(alpha = 0.05f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = HunarNavy, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Private Collection — Only you can access your saved videos & photos.",
                                fontSize = 12.sp,
                                color = HunarNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (allSaved.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔖", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No saved content yet",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap the Save icon on any post or reel to store it here privately.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(allSaved, key = { "saved_${it.id}" }) { savedItem ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = null,
                                    tint = HunarOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = savedItem.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "By ${savedItem.creator.name} • ${savedItem.skillCategory.displayName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "Private",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // LIVE History
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiveHistoryCard("🔴 Interactive Robotics Q&A", "2 days ago", "Verified Live Session", "₹1,250 Tips")
                        LiveHistoryCard("🔴 Live Competitive Coding Sprint", "1 week ago", "Verified Live Session", "₹2,100 Tips")
                        LiveHistoryCard("🔴 Machine Learning Roadmap", "2 weeks ago", "Verified Live Session", "₹1,800 Tips")
                    }
                }
            }
            4 -> {
                // CREATOR DASHBOARD 💰
                item {
                    LaunchedEffect(Unit) {
                        creatorDashboardInitialTab = 1
                        isCreatorDashboardOpen = true
                    }
                }
            }
        }
    }

    // UPI Withdrawal Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text(
                    text = "Withdraw Creator Earnings",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Instant 24x7 payout to any UPI ID or verified bank account.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Amount (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = upiIdText,
                        onValueChange = { upiIdText = it },
                        label = { Text("UPI ID (e.g. name@upi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && upiIdText.isNotBlank()) {
                            onWithdrawSubmit(amt, upiIdText)
                            showWithdrawDialog = false
                        } else {
                            Toast.makeText(context, "Please enter valid amount and UPI ID", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Submit Payout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // 1. My Profile Modal Dialog
    if (showMyProfileDialog) {
        AlertDialog(
            onDismissRequest = { showMyProfileDialog = false },
            containerColor = HunarPureWhite,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = HunarNavy)
                    Text("My Profile", color = HunarNavy, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Creator: ${currentUser.name}", fontWeight = FontWeight.Bold, color = HunarNavy)
                    Text("Handle: ${currentUser.handle}", color = HunarTextSecondaryLight)
                    Text("Skill Title: ${currentUser.verifiedSkillTitle}", color = HunarNavy)
                    Text("Bio: ${currentUser.bio}", color = HunarTextSecondaryLight, fontSize = 13.sp)
                    Text("Talent Merit Score: ${currentUser.talentScore}/100", color = HunarOrange, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMyProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    // 2. Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = HunarPureWhite,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = HunarNavy)
                    Text("Privacy & Security", color = HunarNavy, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Your account is Skill-Verified.", color = HunarNavy, fontSize = 13.sp)
                    Text("• Encrypted direct messaging with verified creators.", color = HunarNavy, fontSize = 13.sp)
                    Text("• Zero adult content or zero-effort reposting strictly moderated.", color = HunarNavy, fontSize = 13.sp)
                    Text("• Payout UPI data is protected under banking encryption standards.", color = HunarNavy, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Got It", color = Color.White)
                }
            }
        )
    }

    // 3. Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = HunarPureWhite,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = HunarNavy)
                    Text("Help & Support", color = HunarNavy, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Need assistance with your talent posts or monetization?", color = HunarNavy, fontWeight = FontWeight.Medium)
                    Text("• How to earn: Upload pure talent videos & receive Shabaashi tips via UPI.", color = HunarTextSecondaryLight, fontSize = 12.5.sp)
                    Text("• 3-Strike rules: Avoid vulgarity or plain lip-syncing.", color = HunarTextSecondaryLight, fontSize = 12.5.sp)
                    Text("• Support: support@hunar.in", color = HunarOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
        }
    }
}

@Composable
fun ProfileStatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = HunarSuccess
            )
        }
    }
}

@Composable
fun RevenueBreakdownRow(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = HunarOrange
        )
    }
}

@Composable
fun LiveHistoryCard(title: String, date: String, viewers: String, tips: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "$date • $viewers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                color = HunarSuccess.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = tips,
                    color = HunarSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
