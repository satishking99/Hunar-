package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.data.repository.HunarRepository
import com.example.ui.components.*
import com.example.ui.components.HunarScreenTab
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.robot.RobotAssistantManager
import com.example.ui.robot.RobotAssistantOverlay
import com.example.ui.robot.RobotCommandSheet

class MainActivity : ComponentActivity() {
    private val repository = HunarRepository()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeViewModel.initPreferences(this)
        enableEdgeToEdge()

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val highContrastTypography by themeViewModel.highContrastTypography.collectAsStateWithLifecycle()

            HunarTheme(
                themeMode = themeMode,
                highContrastTypography = highContrastTypography
            ) {
                HunarMainApp(
                    repository = repository,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

@Composable
fun HunarMainApp(
    repository: HunarRepository,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()

    // Theme States
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val reducedMotion by themeViewModel.reducedMotion.collectAsStateWithLifecycle()
    val highContrastTypography by themeViewModel.highContrastTypography.collectAsStateWithLifecycle()

    // Effective dark state: true if theme is DARK, or if SYSTEM and system is dark
    val isDarkActive = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }

    // State collections from Repository
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val creatorEarnings by repository.creatorEarnings.collectAsStateWithLifecycle()
    val strikeStatus by repository.strikeStatus.collectAsStateWithLifecycle()
    val selectedCategory by repository.selectedCategory.collectAsStateWithLifecycle()
    val selectedInterests by repository.selectedInterests.collectAsStateWithLifecycle()
    val currentLanguage by repository.currentLanguage.collectAsStateWithLifecycle()
    val posts by repository.posts.collectAsStateWithLifecycle()
    val isLoadingMore by repository.isLoadingMore.collectAsStateWithLifecycle()
    val reels by repository.reels.collectAsStateWithLifecycle()
    val stories by repository.stories.collectAsStateWithLifecycle()
    val notifications by repository.notifications.collectAsStateWithLifecycle()
    val conversations by repository.conversations.collectAsStateWithLifecycle()
    val commentsMap by repository.comments.collectAsStateWithLifecycle()
    val skills by repository.skillsProficiencies.collectAsStateWithLifecycle()
    val projects by repository.projectPortfolios.collectAsStateWithLifecycle()
    val accomplishmentsSummary by repository.accomplishmentsSummary.collectAsStateWithLifecycle()
    val portfolioStatusFilter by repository.portfolioFilterStatus.collectAsStateWithLifecycle()
    val portfolioSearchQuery by repository.portfolioSearchQuery.collectAsStateWithLifecycle()
    val userProfile by repository.userProfile.collectAsStateWithLifecycle()

    // Main Navigation State
    var currentTab by remember { mutableStateOf(HunarScreenTab.HOME) }

    // Smart Personal AI Robot Assistant Manager
    val robotManager = remember { RobotAssistantManager() }

    // Screen Overlays / Separate Pages
    var isNotificationsPageOpen by remember { mutableStateOf(false) }
    var isSettingsPageOpen by remember { mutableStateOf(false) }

    // Dialog & Sheet States
    var showRulesModal by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLiveModal by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var activeStoryToView by remember { mutableStateOf<Story?>(null) }
    var activePostForComments by remember { mutableStateOf<String?>(null) }
    var activePostForTips by remember { mutableStateOf<Post?>(null) }
    var activeReelForTips by remember { mutableStateOf<Reel?>(null) }

    // Unread counts
    val unreadNotificationsCount = remember(notifications) { notifications.count { !it.isRead } }
    val unreadMessagesCount = remember(conversations) { conversations.sumOf { it.unreadCount } }

    // Quick toggle helper
    val onQuickToggleTheme: () -> Unit = {
        themeViewModel.toggleTheme(isSystemDark)
        val nextModeText = if (isDarkActive) "White Theme activated (#FFFFFF)" else "Black Theme activated (#000000)"
        Toast.makeText(context, nextModeText, Toast.LENGTH_SHORT).show()
    }

    // Handle back button: if in notifications, settings, or secondary screen, return to Home feed
    BackHandler(enabled = isNotificationsPageOpen || isSettingsPageOpen || currentTab != HunarScreenTab.HOME) {
        if (isNotificationsPageOpen) {
            isNotificationsPageOpen = false
        } else if (isSettingsPageOpen) {
            isSettingsPageOpen = false
        } else {
            currentTab = HunarScreenTab.HOME
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isNotificationsPageOpen) {
            // Separate Notifications Page opened exclusively from the 🔔 bell on the Home page
            NotificationsScreen(
                notifications = notifications,
                onBackClick = { isNotificationsPageOpen = false },
                onMarkAllAsRead = {
                    repository.markAllNotificationsRead()
                    Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                },
                onNotificationClick = { id ->
                    repository.markNotificationRead(id)
                }
            )
        } else if (isSettingsPageOpen) {
            // SETTINGS PAGE - Full White Screen #FFFFFF
            SettingsScreen(
                currentUser = currentUser,
                currentLanguage = currentLanguage,
                isDarkBlueTheme = isDarkActive,
                onBackClick = { isSettingsPageOpen = false },
                onLanguageClick = { showLanguageDialog = true },
                onThemeToggle = { shouldBeDark ->
                    themeViewModel.setThemeMode(if (shouldBeDark) AppThemeMode.DARK else AppThemeMode.LIGHT)
                    val msg = if (shouldBeDark) "Dark Blue (#0A1931) theme activated" else "Light (#FFFFFF) theme activated"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                robotManager = robotManager,
                onNavigate = { tab ->
                    isSettingsPageOpen = false
                    currentTab = tab
                },
                onOpenAccountDetails = {
                    Toast.makeText(context, "Account: ${currentUser.name} (${currentUser.handle})", Toast.LENGTH_SHORT).show()
                },
                onOpenPrivacyDetails = {
                    showRulesModal = true
                },
                onLogoutClick = {
                    isSettingsPageOpen = false
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (currentTab == HunarScreenTab.HOME) {
                    HunarHomeTopBar(
                        selectedLanguage = currentLanguage,
                        unreadNotificationsCount = unreadNotificationsCount,
                        currentTheme = themeMode,
                        isDarkActive = isDarkActive,
                        onLanguageClick = { showLanguageDialog = true },
                        onThemeToggleClick = onQuickToggleTheme,
                        onNotificationBellClick = { isNotificationsPageOpen = true },
                        onLiveClick = { showLiveModal = true }
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { currentTab = HunarScreenTab.HOME }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Feed",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = when (currentTab) {
                                        HunarScreenTab.REELS_SEARCH -> "Discover & Reels"
                                        HunarScreenTab.MESSAGES -> "Messages & Chats"
                                        HunarScreenTab.PROFILE -> "Profile & Hub"
                                        else -> "Feed"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Theme toggle button at top right corner
                            IconButton(
                                onClick = onQuickToggleTheme,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkActive) "Switch to White Theme" else "Switch to Black Theme",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            },
            // Fixed Bottom Navigation Bar at the bottom of the screen (Horizontal, 4 icons, no text labels)
            bottomBar = {
                HunarBottomBar(
                    currentTab = currentTab,
                    unreadMessagesCount = if (unreadMessagesCount > 0) unreadMessagesCount else 4,
                    onTabSelected = { tab ->
                        isSettingsPageOpen = false
                        isNotificationsPageOpen = false
                        currentTab = tab
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Crossfade(targetState = currentTab, label = "main_tabs_crossfade") { tab ->
                    when (tab) {
                        // PAGE 1 — HOME 🏠 (Full-Screen Infinite Scroll Feed)
                        HunarScreenTab.HOME -> {
                            HomeScreen(
                                posts = posts,
                                stories = stories,
                                selectedCategory = selectedCategory,
                                strikeStatus = strikeStatus,
                                isLoadingMore = isLoadingMore,
                                onLoadMore = { repository.loadMorePosts() },
                                onCategorySelected = { repository.setCategory(it) },
                                onSavePost = { repository.toggleSavePost(it) },
                                onViewRulesModal = { showRulesModal = true },
                                onOpenStory = { activeStoryToView = it },
                                onOpenCreatePost = { showCreatePostDialog = true },
                                onOpenLiveStreaming = { showLiveModal = true }
                            )
                        }

                        // PAGE 2 — REELS + SEARCH 🔍
                        HunarScreenTab.REELS_SEARCH -> {
                            ReelsSearchScreen(
                                reels = reels,
                                allCategories = SkillCategory.entries,
                                selectedInterests = selectedInterests,
                                isDarkActive = isDarkActive,
                                onToggleInterest = { repository.toggleInterest(it) },
                                onSaveReel = { repository.toggleSaveReel(it) },
                                onThemeToggleClick = onQuickToggleTheme
                            )
                        }

                        // PAGE 3 — MESSAGES 💬
                        HunarScreenTab.MESSAGES -> {
                            MessagesScreen(
                                conversations = conversations,
                                isDarkActive = isDarkActive,
                                onBlockUser = { repository.blockUser(it) },
                                onReportUser = { Toast.makeText(context, "Report submitted for moderation", Toast.LENGTH_SHORT).show() },
                                onThemeToggleClick = onQuickToggleTheme
                            )
                        }

                        // PAGE 4 — PROFILE 👤 & CREATOR DASHBOARD 💰
                        HunarScreenTab.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentUser,
                                earnings = creatorEarnings,
                                posts = posts,
                                reels = reels,
                                skills = skills,
                                projects = projects,
                                summary = accomplishmentsSummary,
                                selectedStatusFilter = portfolioStatusFilter,
                                searchQuery = portfolioSearchQuery,
                                onStatusFilterChange = { repository.filterPortfolioByStatus(it) },
                                onSearchQueryChange = { repository.searchPortfolio(it) },
                                onAddProject = { repository.addProject(it) },
                                onAddSkill = { repository.addSkill(it) },
                                onEndorseSkill = { repository.endorseSkill(it) },
                                isDarkActive = isDarkActive,
                                onWithdrawSubmit = { amount, upiId ->
                                    val (success, msg) = repository.withdrawEarnings(amount, upiId)
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                },
                                onUpdatePaymentMethod = { newUpiId ->
                                    repository.updatePaymentMethod(newUpiId)
                                },
                                onOpenSettings = { isSettingsPageOpen = true },
                                onOpenLiveStream = { showLiveModal = true },
                                onLanguageClick = { showLanguageDialog = true },
                                onLogoutClick = {
                                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                },
                                onThemeToggleClick = onQuickToggleTheme
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODALS, SHEETS & DIALOGS
    // ==========================================

    // Quick Post Creation Dialog
    if (showCreatePostDialog) {
        var postTitle by remember { mutableStateOf("") }
        var postCaption by remember { mutableStateOf("") }
        var postCategory by remember { mutableStateOf(SkillCategory.TECH) }

        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = {
                Text(
                    text = "Share Your Pure Talent 🇮🇳",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Real skills only. No vulgarity, no low-effort lip-syncs.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        label = { Text("Talent Title (e.g. Handmade Pottery)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = postCaption,
                        onValueChange = { postCaption = it },
                        label = { Text("Description & Technique") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (postTitle.isNotBlank()) {
                            repository.createPost(postTitle, postCaption, postCategory)
                            showCreatePostDialog = false
                            Toast.makeText(context, "Post uploaded! Verified on HUNAR merit feed.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Publish Post", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Theme & Accessibility Dialog
    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = themeMode,
            reducedMotion = reducedMotion,
            highContrast = highContrastTypography,
            onThemeSelect = { themeViewModel.setThemeMode(it) },
            onReducedMotionToggle = { themeViewModel.setReducedMotion(it) },
            onHighContrastToggle = { themeViewModel.setHighContrastTypography(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    // 4 Golden Rules Modal
    if (showRulesModal) {
        GoldenRulesModal(onDismiss = { showRulesModal = false })
    }

    // Indian Language Selector Dialog
    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onSelectLanguage = { repository.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Live Stream Modal
    if (showLiveModal) {
        LiveStreamModal(onDismiss = { showLiveModal = false })
    }

    // Story Viewer Modal
    activeStoryToView?.let { story ->
        StoryViewerModal(
            story = story,
            onDismiss = { activeStoryToView = null }
        )
    }

    // Comments Sheet
    activePostForComments?.let { postId ->
        val list = commentsMap[postId] ?: emptyList()
        CommentsSheet(
            postId = postId,
            commentsList = list,
            onDismiss = { activePostForComments = null },
            onAddComment = { text ->
                repository.addComment(postId, text)
            }
        )
    }

    // Post Tip / Shabaashi Sheet
    activePostForTips?.let { post ->
        TipShabaashiSheet(
            creatorName = post.creator.name,
            onDismiss = { activePostForTips = null },
            onSendTip = { amount ->
                repository.sendShabaashiTip(post.id, amount)
            }
        )
    }

    // Reel Tip / Shabaashi Sheet
    activeReelForTips?.let { reel ->
        TipShabaashiSheet(
            creatorName = reel.creator.name,
            onDismiss = { activeReelForTips = null },
            onSendTip = { amount ->
                repository.sendReelTip(reel.id, amount)
            }
        )
    }

    // ==========================================
    // SMART PERSONAL AI ROBOT ASSISTANT (Continuous 5-min Screen Perimeter Walk & Interactions)
    // ==========================================
    RobotAssistantOverlay(
        manager = robotManager,
        onOpenSettings = { isSettingsPageOpen = true }
    )

    if (robotManager.isCommandSheetOpen) {
        RobotCommandSheet(
            manager = robotManager,
            onNavigate = { tab ->
                isSettingsPageOpen = false
                isNotificationsPageOpen = false
                currentTab = tab
            },
            onOpenSettings = {
                isSettingsPageOpen = true
            },
            onDismiss = {
                robotManager.isCommandSheetOpen = false
            }
        )
    }
}
}
