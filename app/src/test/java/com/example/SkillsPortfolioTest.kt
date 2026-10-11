package com.example

import com.example.data.model.*
import com.example.data.repository.HunarRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SkillsPortfolioTest {

    private lateinit var repository: HunarRepository

    @Before
    fun setUp() {
        repository = HunarRepository()
    }

    @Test
    fun testInitialSkillsProficiencies_containsAuditedBenchmarks() {
        val skills = repository.skillsProficiencies.value
        assertTrue("Skills list should not be empty", skills.isNotEmpty())

        val androidSkill = skills.find { it.name.contains("Android", ignoreCase = true) }
        assertNotNull("Android Architecture skill should exist", androidSkill)
        assertTrue("Mastery should be high for lead architect", androidSkill!!.masteryPercentage >= 90)
        assertTrue("Years of experience should be positive", androidSkill.yearsExperience > 4f)
        assertTrue("Should have audited artifacts", androidSkill.auditedArtifactsCount >= 10)
        assertTrue("Should have verifiable ID", androidSkill.verificationId.startsWith("HUNAR-SKL"))
        assertTrue("Key strengths must be detailed", androidSkill.keyStrengths.isNotEmpty())
    }

    @Test
    fun testInitialProjectPortfolios_prioritizesDataDrivenAccomplishments() {
        val projects = repository.projectPortfolios.value
        assertTrue("Projects list should not be empty", projects.isNotEmpty())

        // Every project must have measurable impact metrics (data-driven accomplishment)
        projects.forEach { project ->
            assertTrue("Project ${project.title} must have measurable impact metrics", project.impactMetrics.isNotEmpty())
            assertTrue("Project ${project.title} must have a verifiable audit ID", project.verifiableAuditId.isNotEmpty())
            assertTrue("Project ${project.title} must have deliverables", project.deliverables.isNotEmpty())
            assertTrue("Project ${project.title} must have peer/client review rating", project.peerReviewRating > 4.5f)
        }

        // Verify top AutoSLAM project metrics
        val slamProject = projects.find { it.id == "proj_1" }
        assertNotNull("AutoSLAM project should exist", slamProject)
        assertEquals(ProjectStatus.PRODUCTION_DEPLOYED, slamProject!!.status)
        assertTrue("Should report latency reduction metric", slamProject.impactMetrics.any { it.label.contains("Latency") })
        assertTrue("Should report crash-free uptime", slamProject.impactMetrics.any { it.label.contains("Crash-Free") })
    }

    @Test
    fun testFilterPortfolioByStatus() {
        val allProjects = repository.projectPortfolios.value
        assertEquals(null, repository.portfolioFilterStatus.value)

        repository.filterPortfolioByStatus(ProjectStatus.PRODUCTION_DEPLOYED)
        assertEquals(ProjectStatus.PRODUCTION_DEPLOYED, repository.portfolioFilterStatus.value)

        val filtered = allProjects.filter { it.status == repository.portfolioFilterStatus.value }
        assertTrue("Filtered list should only have production deployed projects", filtered.all { it.status == ProjectStatus.PRODUCTION_DEPLOYED })

        repository.filterPortfolioByStatus(null)
        assertNull("Status filter should be reset to null", repository.portfolioFilterStatus.value)
    }

    @Test
    fun testSearchPortfolio() {
        val allProjects = repository.projectPortfolios.value
        val query = "AutoSLAM"
        repository.searchPortfolio(query)
        assertEquals(query, repository.portfolioSearchQuery.value)

        val matches = allProjects.filter { it.title.contains(query, ignoreCase = true) }
        assertEquals(1, matches.size)
        assertEquals("proj_1", matches.first().id)
    }

    @Test
    fun testAddProject_incrementsProductionDeployments() {
        val initialSummary = repository.accomplishmentsSummary.value
        val initialCount = repository.projectPortfolios.value.size

        val newProject = ProjectPortfolio(
            id = "proj_test_999",
            title = "Autonomous Drone Collision Interlock",
            tagline = "Sub-10ms optical flow obstacle abort trigger",
            category = SkillCategory.TECH,
            timeline = "2024",
            status = ProjectStatus.PRODUCTION_DEPLOYED,
            role = "Safety Systems Engineer",
            teamSize = 2,
            impactMetrics = listOf(
                MeasurableMetric("Abort Latency", "8.4 ms", "P99 trigger time", true),
                MeasurableMetric("Zero Near-Miss Incidents", "100%", "2,000 test flight runs", true)
            ),
            problemStatement = "Drones failed to abort fast enough when approaching unexpected high-tension wires.",
            architecturalSolution = "Direct hardware interrupt hook via RTOS timer triggering motor counter-torque.",
            techStack = listOf("C++ NDK", "Embedded RTOS", "OpenCV"),
            deliverables = listOf("Safety Firmware v1.0", "HIL Test Suite"),
            verifiableAuditId = "AUDIT-DRONE-999"
        )

        repository.addProject(newProject)

        val updatedProjects = repository.projectPortfolios.value
        assertEquals(initialCount + 1, updatedProjects.size)
        assertEquals(newProject.id, updatedProjects.first().id)

        val updatedSummary = repository.accomplishmentsSummary.value
        assertEquals(initialSummary.totalProductionDeployments + 1, updatedSummary.totalProductionDeployments)
    }

    @Test
    fun testAddSkill_incrementsVerifiedSkillsCount() {
        val initialSummary = repository.accomplishmentsSummary.value
        val initialSkills = repository.skillsProficiencies.value.size

        val newSkill = SkillProficiency(
            id = "skl_test_999",
            name = "Rust Embedded Firmware",
            category = SkillCategory.TECH,
            masteryPercentage = 87,
            yearsExperience = 2.0f,
            projectsCompleted = 5,
            verifiedEndorsements = 14,
            auditedArtifactsCount = 4,
            keyStrengths = listOf("Memory Safety", "Zero Panics", "Direct Register Access"),
            benchmarkHighlight = "Zero memory safety incidents across 500K cycles",
            verificationId = "HUNAR-SKL-RUST-111"
        )

        repository.addSkill(newSkill)

        val updatedSkills = repository.skillsProficiencies.value
        assertEquals(initialSkills + 1, updatedSkills.size)

        val updatedSummary = repository.accomplishmentsSummary.value
        assertEquals(initialSummary.verifiedSkillsCount + 1, updatedSummary.verifiedSkillsCount)
    }

    @Test
    fun testEndorseSkill_incrementsEndorsements() {
        val initialEndorsements = repository.skillsProficiencies.value.first().verifiedEndorsements
        val skillId = repository.skillsProficiencies.value.first().id

        repository.endorseSkill(skillId)

        val updatedEndorsements = repository.skillsProficiencies.value.first().verifiedEndorsements
        assertEquals(initialEndorsements + 1, updatedEndorsements)
    }

    @Test
    fun testAccomplishmentsSummary_prioritizesDataOverVanity() {
        val summary = repository.accomplishmentsSummary.value
        assertTrue("Zero defect streak must be verified", summary.zeroCriticalDefectsStreakDays > 300)
        assertTrue("SLA adherence should be high", summary.averageSlaAdherence > 95f)
        assertTrue("Crash-free rate should be high", summary.crashFreeRate.contains("99.9"))
        assertTrue("Client value should be quantified", summary.totalClientValueRupees.contains("Lakhs"))
    }
}
