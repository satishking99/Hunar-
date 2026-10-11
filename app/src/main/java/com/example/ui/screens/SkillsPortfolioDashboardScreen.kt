package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

/**
 * COMPOSABLE DASHBOARD UI: SKILLS & PROJECT PORTFOLIOS
 *
 * Core Principles:
 * 1. PRIORITIZE DATA-DRIVEN ACCOMPLISHMENTS:
 *    Highlights measured benchmark gains (-42.8% latency, 99.98% crash-free rate,
 *    ₹14.2L client cost savings, 96.4% test coverage) over superficial vanity metrics.
 * 2. REJECT VANITY METRICS:
 *    Excludes popularity vanity metrics (follower counts, empty likes, fake hype)
 *    in favor of verified engineering results, peer code review acceptance, and SLA adherence.
 * 3. COMPREHENSIVE PROJECT CASE STUDIES:
 *    Provides deep architectural breakdowns, problem statements, deliverables,
 *    and cryptographic verification audit IDs.
 * 4. INTERACTIVE CREATOR WORKFLOW:
 *    Allows creators to filter by status/category, search, log new measurable
 *    accomplishments, inspect audit trails, and export a verified skill dossier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsPortfolioDashboardScreen(
    currentUser: User,
    skills: List<SkillProficiency>,
    projects: List<ProjectPortfolio>,
    summary: DataDrivenAccomplishmentsSummary,
    selectedStatusFilter: ProjectStatus?,
    searchQuery: String,
    onStatusFilterChange: (ProjectStatus?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onAddProject: (ProjectPortfolio) -> Unit,
    onAddSkill: (SkillProficiency) -> Unit,
    onEndorseSkill: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Active Tab in Dashboard: 0 = Projects & Case Studies, 1 = Skills Matrix & Audits
    var dashboardSubTab by remember { mutableStateOf(0) }

    // Dialog States
    var selectedProjectForDetails by remember { mutableStateOf<ProjectPortfolio?>(null) }
    var selectedSkillForAudit by remember { mutableStateOf<SkillProficiency?>(null) }
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showExportDossierDialog by remember { mutableStateOf(false) }
    var showVanityVsDataInfoDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBackClick()
    }

    // Filter projects based on status and search query
    val filteredProjects = remember(projects, selectedStatusFilter, searchQuery) {
        projects.filter { project ->
            val matchesStatus = selectedStatusFilter == null || project.status == selectedStatusFilter
            val matchesQuery = searchQuery.isBlank() ||
                    project.title.contains(searchQuery, ignoreCase = true) ||
                    project.tagline.contains(searchQuery, ignoreCase = true) ||
                    project.techStack.any { it.contains(searchQuery, ignoreCase = true) } ||
                    project.problemStatement.contains(searchQuery, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("skills_portfolio_dashboard_screen"),
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
                                    .testTag("portfolio_back_btn")
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
                                        text = "Skills & Portfolio Hub",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = HunarSuccess.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "DATA-VERIFIED",
                                            color = HunarSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Zero Vanity Metrics • Real Engineering Deliverables",
                                    fontSize = 11.5.sp,
                                    color = HunarOrangeDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Philosophy Info Dialog
                            IconButton(
                                onClick = { showVanityVsDataInfoDialog = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_info_vanity_vs_data")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = "Philosophy Info",
                                    tint = HunarNavyLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Export Verified Dossier
                            IconButton(
                                onClick = { showExportDossierDialog = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_export_dossier")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export Dossier",
                                    tint = HunarOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Top Sub-Tabs: [ 🚀 Project Portfolios (5) ] [ 🎯 Skill Proficiencies (5) ]
                    TabRow(
                        selectedTabIndex = dashboardSubTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = HunarOrange,
                        divider = {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    ) {
                        Tab(
                            selected = dashboardSubTab == 0,
                            onClick = { dashboardSubTab = 0 },
                            modifier = Modifier.testTag("tab_projects_showcase"),
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderSpecial,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Project Portfolios (${projects.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = dashboardSubTab == 1,
                            onClick = { dashboardSubTab = 1 },
                            modifier = Modifier.testTag("tab_skills_matrix"),
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Skill Matrix (${skills.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (dashboardSubTab == 0) {
                        showAddProjectDialog = true
                    } else {
                        showAddSkillDialog = true
                    }
                },
                containerColor = HunarOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = if (dashboardSubTab == 0) Icons.Default.AddCircleOutline else Icons.Default.StarOutline,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = if (dashboardSubTab == 0) "Log Accomplishment" else "Add Verified Skill",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .testTag("fab_add_portfolio_entry")
            )
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
            // 1. DATA-DRIVEN ACCOMPLISHMENTS HERO BANNER
            // ==========================================
            item {
                DataAccomplishmentsHeroCard(
                    user = currentUser,
                    summary = summary,
                    onInfoClick = { showVanityVsDataInfoDialog = true }
                )
            }

            // ==========================================
            // 2. 4 HIGH-DENSITY QUANTITATIVE KPI TILES
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Shield,
                        iconColor = HunarSuccess,
                        value = "0 Defects",
                        label = "Prod Defect Streak",
                        subtext = "${summary.zeroCriticalDefectsStreakDays}d zero criticals"
                    )
                    KpiTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TrendingUp,
                        iconColor = HunarOrange,
                        value = summary.totalClientValueRupees,
                        label = "Verified ROI",
                        subtext = "Client cost reduction"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        iconColor = HunarNavyLight,
                        value = "${summary.averageSlaAdherence}%",
                        label = "SLA Compliance",
                        subtext = "${summary.totalProductionDeployments} live deployments"
                    )
                    KpiTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.VerifiedUser,
                        iconColor = Color(0xFF8B5CF6),
                        value = summary.crashFreeRate,
                        label = "Crash-Free Rate",
                        subtext = "${summary.averageTestCoverage} test coverage"
                    )
                }
            }

            // ==========================================
            // 3. TAB CONTENT
            // ==========================================
            if (dashboardSubTab == 0) {
                // PROJECTS & CASE STUDIES TAB
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Verified Projects & Deliverables",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${filteredProjects.size} of ${projects.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_projects_input"),
                            placeholder = { Text("Search by tech stack, problem or title...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = HunarOrange)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HunarOrange,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        // Status Filter Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedStatusFilter == null,
                                onClick = { onStatusFilterChange(null) },
                                label = { Text("All Statuses (${projects.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HunarNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                            ProjectStatus.entries.forEach { status ->
                                val count = projects.count { it.status == status }
                                FilterChip(
                                    selected = selectedStatusFilter == status,
                                    onClick = { onStatusFilterChange(status) },
                                    label = { Text("${status.displayName} ($count)") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HunarOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredProjects.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAltOff,
                                    contentDescription = null,
                                    tint = HunarTextMuted,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "No projects matching criteria",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Try clearing filters or search query to see all verified deliverables.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = {
                                        onStatusFilterChange(null)
                                        onSearchQueryChange("")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredProjects, key = { it.id }) { project ->
                        ProjectPortfolioCard(
                            project = project,
                            onDeepDiveClick = { selectedProjectForDetails = project },
                            onShareClick = {
                                copyToClipboard(
                                    context,
                                    "HUNAR Verified Project: ${project.title}\n" +
                                            "Audit ID: ${project.verifiableAuditId}\n" +
                                            "Key Metrics: ${project.impactMetrics.joinToString { "${it.label}: ${it.value}" }}"
                                )
                                Toast.makeText(context, "Project verification link copied!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

            } else {
                // SKILLS MATRIX TAB
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Verified Skill Proficiencies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Skill scores are calibrated against audited project repos, benchmark suites, and verified peer code reviews.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(skills, key = { it.id }) { skill ->
                    SkillProficiencyCard(
                        skill = skill,
                        onInspectAudit = { selectedSkillForAudit = skill },
                        onEndorse = {
                            onEndorseSkill(skill.id)
                            Toast.makeText(context, "Endorsement registered for ${skill.name}!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Bottom Spacing for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // ========================================================
    // MODALS & DIALOGS
    // ========================================================

    // 1. Deep Dive Case Study & Audit Modal
    selectedProjectForDetails?.let { project ->
        ProjectCaseStudyModal(
            project = project,
            onDismiss = { selectedProjectForDetails = null },
            onCopyAudit = {
                copyToClipboard(
                    context,
                    "Cryptographic Audit ID: ${project.verifiableAuditId}\nProject: ${project.title}\nRating: ${project.peerReviewRating}/5.0"
                )
                Toast.makeText(context, "Cryptographic audit certificate copied!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 2. Skill Audit Trail Modal
    selectedSkillForAudit?.let { skill ->
        SkillAuditModal(
            skill = skill,
            onDismiss = { selectedSkillForAudit = null }
        )
    }

    // 3. Log Accomplishment Dialog
    if (showAddProjectDialog) {
        AddProjectAccomplishmentDialog(
            onDismiss = { showAddProjectDialog = false },
            onSave = { newProject ->
                onAddProject(newProject)
                showAddProjectDialog = false
                Toast.makeText(context, "Project '${newProject.title}' logged with verified metrics!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // 4. Add Skill Dialog
    if (showAddSkillDialog) {
        AddSkillDialog(
            onDismiss = { showAddSkillDialog = false },
            onSave = { newSkill ->
                onAddSkill(newSkill)
                showAddSkillDialog = false
                Toast.makeText(context, "Skill '${newSkill.name}' added to proficiency matrix!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 5. Export Dossier Dialog
    if (showExportDossierDialog) {
        ExportDossierModal(
            user = currentUser,
            summary = summary,
            projects = projects,
            skills = skills,
            onDismiss = { showExportDossierDialog = false },
            onCopy = { text ->
                copyToClipboard(context, text)
                Toast.makeText(context, "Verified Skill Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 6. Vanity vs Data-Driven Philosophy Info Dialog
    if (showVanityVsDataInfoDialog) {
        VanityVsDataPhilosophyDialog(onDismiss = { showVanityVsDataInfoDialog = false })
    }
}

/**
 * Hero banner contrasting Data-Driven Accomplishments against Vanity Metrics.
 */
@Composable
fun DataAccomplishmentsHeroCard(
    user: User,
    summary: DataDrivenAccomplishmentsSummary,
    onInfoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("data_accomplishments_hero_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = HunarNavy
        ),
        border = BorderStroke(1.dp, HunarNavyBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HunarNavy,
                            HunarNavyLight
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Top row with user badge and info button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HunarOrange,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.avatarEmoji,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Column {
                            Text(
                                text = user.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${user.handle} • Merit Score: ${summary.peerMeritScore}/100",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HunarOrange.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, HunarOrange.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onInfoClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = HunarOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Anti-Vanity",
                                color = HunarOrange,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Core statement
                Text(
                    text = "Engineering accomplishments proven by production latency, SLA delivery, zero defects, and client ROI.",
                    color = Color.White,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                )

                // Comparison bar: Rejection of vanity vs proof of data
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VANITY METRICS",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Excluded ❌ (Followers/Likes)",
                                color = HunarError.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier.height(28.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        )
                        Column {
                            Text(
                                text = "VERIFIABLE DATA",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active ✓ (Benchmarks & SLA)",
                                color = HunarSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-density KPI tile component.
 */
@Composable
fun KpiTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    subtext: String
) {
    Card(
        modifier = modifier.testTag("kpi_tile_${label.replace(" ", "_")}"),
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
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
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
                text = subtext,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Rich project card prioritizing measurable benchmarks and deliverables.
 */
@Composable
fun ProjectPortfolioCard(
    project: ProjectPortfolio,
    onDeepDiveClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Status badge, Timeline, and Share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(project.status.badgeColorHex).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(project.status.badgeColorHex).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(project.status.badgeColorHex),
                            modifier = Modifier.size(6.dp)
                        ) {}
                        Text(
                            text = project.status.displayName,
                            color = Color(project.status.badgeColorHex),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = project.timeline,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share verification",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Project Title & Role
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Role: ${project.role} • Team size: ${project.teamSize}",
                    fontSize = 11.5.sp,
                    color = HunarOrangeDark,
                    fontWeight = FontWeight.Medium
                )
            }

            // Tagline
            Text(
                text = project.tagline,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            // ==========================================
            // MEASURABLE IMPACT METRICS CHIP ROW
            // (The core data-driven accomplishment element)
            // ==========================================
            Text(
                text = "MEASURABLE DATA ACCOMPLISHMENTS:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                project.impactMetrics.take(3).forEach { metric ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = HunarSuccess.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = metric.value,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarSuccess
                            )
                            Text(
                                text = metric.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Tech Stack Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                project.techStack.forEach { tech ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = tech,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Footer: Audit Hash and Deep-Dive Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = HunarNavyMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = project.verifiableAuditId,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = HunarNavyMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onDeepDiveClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HunarOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_case_study_${project.id}")
                ) {
                    Text(
                        text = "Case Study & Audit",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Skill proficiency card showing mastery percentage, experience, and audited artifacts.
 */
@Composable
fun SkillProficiencyCard(
    skill: SkillProficiency,
    onInspectAudit: () -> Unit,
    onEndorse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("skill_card_${skill.id}"),
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
                        text = "${skill.yearsExperience} yrs experience • ${skill.projectsCompleted} completed deliverables",
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
                        text = "${skill.masteryPercentage}% Mastery",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HunarOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { skill.masteryPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = HunarOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Benchmark highlight callout
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
                        text = skill.benchmarkHighlight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Key Strengths Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                skill.keyStrengths.forEach { strength ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = strength,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Footer Actions: Endorsement & Audit Trail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${skill.verifiedEndorsements} peer endorsements • ${skill.auditedArtifactsCount} repos",
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
                        onClick = onInspectAudit,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HunarNavyLight,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Audit Trail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Deep-dive case study modal showing the full problem, architecture, and benchmark table.
 */
@Composable
fun ProjectCaseStudyModal(
    project: ProjectPortfolio,
    onDismiss: () -> Unit,
    onCopyAudit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("modal_case_study")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top row with title and close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Project Case Study",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HunarOrange
                        )
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Problem Statement Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "PROBLEM STATEMENT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarOrangeDark
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = project.problemStatement,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Architectural Solution Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ARCHITECTURAL SOLUTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HunarSuccess
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = project.architecturalSolution,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Measurable Impact Benchmarks
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "AUDITED BENCHMARKS & METRICS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            project.impactMetrics.forEach { metric ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = metric.label,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = metric.context,
                                                fontSize = 10.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = metric.value,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HunarSuccess
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Deliverables Checklist
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "VERIFIABLE DELIVERABLES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            project.deliverables.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = HunarSuccess,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Peer Review & Quote
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HunarNavy.copy(alpha = 0.05f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = HunarGold, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Peer Review Rating: ${project.peerReviewRating} / 5.0",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HunarNavy
                                    )
                                }
                                Text(
                                    text = "\"${project.clientOrPeerQuote}\"",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Cryptographic Audit Certificate Row
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
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
                                    Text(
                                        text = "AUDIT CERTIFICATE ID",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = project.verifiableAuditId,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = onCopyAudit) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Audit")
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HunarNavy)
                ) {
                    Text("Close Case Study", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Skill audit trail dialog.
 */
@Composable
fun SkillAuditModal(
    skill: SkillProficiency,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Skill Audit Certificate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HunarNavy.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = skill.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "Certificate ID: ${skill.verificationId}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = HunarNavyLight
                        )
                        Text(
                            text = "Calibrated Mastery: ${skill.masteryPercentage}% based on ${skill.projectsCompleted} deliverables and ${skill.verifiedEndorsements} peer sign-offs.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HunarOrange)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Dialog allowing creators to log a new data-driven project accomplishment.
 */
@Composable
fun AddProjectAccomplishmentDialog(
    onDismiss: () -> Unit,
    onSave: (ProjectPortfolio) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Lead Architect") }
    var metric1Label by remember { mutableStateOf("Latency Reduction") }
    var metric1Value by remember { mutableStateOf("-35%") }
    var metric2Label by remember { mutableStateOf("Uptime / SLA") }
    var metric2Value by remember { mutableStateOf("99.95%") }
    var problem by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var techStackText by remember { mutableStateOf("Kotlin, Compose, Coroutines") }
    var selectedStatus by remember { mutableStateOf(ProjectStatus.PRODUCTION_DEPLOYED) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("dialog_add_project")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Log Data-Driven Accomplishment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HunarNavy
                )
                Text(
                    text = "Record deliverables with measurable benchmarks, not vanity claims.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Project Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = tagline,
                            onValueChange = { tagline = it },
                            label = { Text("Tagline / Deliverable Summary *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Your Engineering Role") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text(
                            text = "PRIMARY MEASURABLE METRICS (DATA-DRIVEN)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HunarOrange
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = metric1Label,
                                onValueChange = { metric1Label = it },
                                label = { Text("Metric 1 Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = metric1Value,
                                onValueChange = { metric1Value = it },
                                label = { Text("Value (e.g. -40%)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = metric2Label,
                                onValueChange = { metric2Label = it },
                                label = { Text("Metric 2 Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = metric2Value,
                                onValueChange = { metric2Value = it },
                                label = { Text("Value (e.g. 99.9%)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = problem,
                            onValueChange = { problem = it },
                            label = { Text("Problem Solved & Constraints") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = solution,
                            onValueChange = { solution = it },
                            label = { Text("Architectural Solution") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = techStackText,
                            onValueChange = { techStackText = it },
                            label = { Text("Tech Stack (comma-separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            val newProject = ProjectPortfolio(
                                id = "proj_${System.currentTimeMillis()}",
                                title = title.trim(),
                                tagline = if (tagline.isBlank()) "Verified engineering deliverable" else tagline.trim(),
                                category = SkillCategory.TECH,
                                timeline = "2024",
                                status = selectedStatus,
                                role = role.ifBlank { "Contributor" },
                                teamSize = 1,
                                impactMetrics = listOf(
                                    MeasurableMetric(metric1Label.ifBlank { "Benchmark" }, metric1Value.ifBlank { "Pass" }, "Verified test", true),
                                    MeasurableMetric(metric2Label.ifBlank { "Quality" }, metric2Value.ifBlank { "100%" }, "Audit run", true)
                                ),
                                problemStatement = if (problem.isBlank()) "Resolved production performance bottleneck." else problem.trim(),
                                architecturalSolution = if (solution.isBlank()) "Implemented scalable architecture." else solution.trim(),
                                techStack = techStackText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                deliverables = listOf("Production Artifact", "Benchmark Report", "Unit Test Suite"),
                                verifiableAuditId = "AUDIT-${System.currentTimeMillis().toString().takeLast(6)}"
                            )
                            onSave(newProject)
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log & Verify", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog to add a skill proficiency.
 */
@Composable
fun AddSkillDialog(
    onDismiss: () -> Unit,
    onSave: (SkillProficiency) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mastery by remember { mutableStateOf(85) }
    var years by remember { mutableStateOf("3.0") }
    var strengths by remember { mutableStateOf("Optimization, Clean Architecture, Testing") }
    var highlight by remember { mutableStateOf("Verified top performance in benchmarks") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Add Verified Skill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Skill Name (e.g. Kotlin Coroutines Flow)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(text = "Mastery Level: $mastery%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = mastery.toFloat(),
                    onValueChange = { mastery = it.toInt() },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = HunarOrange, activeTrackColor = HunarOrange)
                )

                OutlinedTextField(
                    value = years,
                    onValueChange = { years = it },
                    label = { Text("Years of Proven Experience") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = strengths,
                    onValueChange = { strengths = it },
                    label = { Text("Key Technical Strengths (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = highlight,
                    onValueChange = { highlight = it },
                    label = { Text("Benchmark Highlight") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            val skill = SkillProficiency(
                                id = "skl_${System.currentTimeMillis()}",
                                name = name.trim(),
                                category = SkillCategory.TECH,
                                masteryPercentage = mastery,
                                yearsExperience = years.toFloatOrNull() ?: 2.5f,
                                projectsCompleted = 4,
                                verifiedEndorsements = 8,
                                auditedArtifactsCount = 3,
                                keyStrengths = strengths.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                benchmarkHighlight = highlight.trim(),
                                verificationId = "HUNAR-SKL-${System.currentTimeMillis().toString().takeLast(4)}"
                            )
                            onSave(skill)
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = HunarOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Skill", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Export dossier modal formatting the verified achievements for sharing.
 */
@Composable
fun ExportDossierModal(
    user: User,
    summary: DataDrivenAccomplishmentsSummary,
    projects: List<ProjectPortfolio>,
    skills: List<SkillProficiency>,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit
) {
    val dossierText = remember(user, summary, projects, skills) {
        buildString {
            appendLine("=== HUNAR VERIFIED SKILL & PORTFOLIO DOSSIER ===")
            appendLine("Creator: ${user.name} (${user.handle})")
            appendLine("Talent Merit Score: ${summary.peerMeritScore}/100")
            appendLine("Philosophy: Data-Driven Accomplishments • Zero Vanity Metrics")
            appendLine()
            appendLine("--- CORE PRODUCTION BENCHMARKS ---")
            appendLine("• Production Deployments: ${summary.totalProductionDeployments}")
            appendLine("• Zero Critical Defect Streak: ${summary.zeroCriticalDefectsStreakDays} days")
            appendLine("• SLA Adherence: ${summary.averageSlaAdherence}%")
            appendLine("• Crash-Free Session Rate: ${summary.crashFreeRate}")
            appendLine("• Average Automated Test Coverage: ${summary.averageTestCoverage}")
            appendLine("• Verifiable Client ROI: ${summary.totalClientValueRupees}")
            appendLine()
            appendLine("--- TOP AUDITED DELIVERABLES ---")
            projects.take(3).forEach { p ->
                appendLine("• [${p.status.displayName}] ${p.title}")
                appendLine("  Audit ID: ${p.verifiableAuditId}")
                appendLine("  Metrics: " + p.impactMetrics.joinToString("; ") { "${it.label}: ${it.value}" })
            }
            appendLine()
            appendLine("--- SKILL PROFICIENCIES ---")
            skills.take(4).forEach { s ->
                appendLine("• ${s.name}: ${s.masteryPercentage}% Mastery (${s.yearsExperience} yrs, ${s.verifiedEndorsements} endorsements)")
            }
            appendLine()
            appendLine("Verified Cryptographically on HUNAR Platform.")
        }
    }

    Dialog(onDismissRequest = onDismiss) {
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
                        text = "Verified Skill Dossier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "A shareable, unforgeable proof of your craftsmanship ready to share with clients, teams, or peer creators.",
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
                        text = dossierText,
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
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Close")
                    }
                    Button(
                        onClick = { onCopy(dossierText) },
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

/**
 * Educational dialog clarifying why HUNAR prioritizes data accomplishments over vanity metrics.
 */
@Composable
fun VanityVsDataPhilosophyDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
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
                    text = "HUNAR Philosophy: Data > Vanity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HunarNavy
                )

                Text(
                    text = "Why we reject vanity metrics on HUNAR:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HunarError.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, HunarError.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "❌ VANITY METRICS (EXCLUDED)",
                            color = HunarError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "• Raw follower counts that can be artificially inflated.\n" +
                                    "• Superficial like counts that favor appearance over engineering.\n" +
                                    "• Empty impressions that fail to reflect actual craft mastery.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HunarSuccess.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, HunarSuccess.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "✓ DATA-DRIVEN ACCOMPLISHMENTS (VERIFIED)",
                            color = HunarSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "• Latency & FPS benchmarks measured via profiling tools.\n" +
                                    "• Zero production defect streaks and SLA delivery rates.\n" +
                                    "• Audited client cost savings and code coverage.\n" +
                                    "• Verifiable deliverables and cryptographic certificates.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HunarNavy)
                ) {
                    Text("I Understand", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("HUNAR Portfolio", text)
    clipboard?.setPrimaryClip(clip)
}
