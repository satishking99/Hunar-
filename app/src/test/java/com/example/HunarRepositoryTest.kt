package com.example

import com.example.data.model.NotificationType
import com.example.data.model.SkillCategory
import com.example.data.repository.HunarRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HunarRepositoryTest {

    private lateinit var repository: HunarRepository

    @Before
    fun setUp() {
        repository = HunarRepository()
    }

    @Test
    fun testInitialEarnings_matchesUserReference() {
        val earnings = repository.creatorEarnings.value
        assertEquals(12450.0, earnings.totalBalanceRupees, 0.01)
        assertEquals("84.2K", earnings.totalViews)
        assertEquals("1,024h", earnings.watchTimeHours)
    }

    @Test
    fun testInitialNotifications_andMarkRead() {
        val initialList = repository.notifications.value
        assertTrue(initialList.isNotEmpty())
        assertTrue(initialList.any { it.type == NotificationType.EARNINGS })

        repository.markAllNotificationsRead()
        assertTrue(repository.notifications.value.all { it.isRead })
    }

    @Test
    fun testInterestsSelection_toggleInterests() {
        val initialInterests = repository.selectedInterests.value
        assertTrue(initialInterests.contains(SkillCategory.TECH))

        repository.toggleInterest(SkillCategory.COOKING)
        assertTrue(repository.selectedInterests.value.contains(SkillCategory.COOKING))

        repository.toggleInterest(SkillCategory.COOKING)
        assertFalse(repository.selectedInterests.value.contains(SkillCategory.COOKING))
    }

    @Test
    fun testConversations_andBlockUser() {
        val convs = repository.conversations.value
        assertTrue(convs.isNotEmpty())
        val firstId = convs.first().id

        repository.blockUser(firstId)
        assertFalse(repository.conversations.value.any { it.id == firstId })
    }

    @Test
    fun testCreatePost_addsToFeed() {
        val initialSize = repository.posts.value.size
        repository.createPost("Algorithmic Optimization", "Dynamic Programming tutorial", SkillCategory.TECH)
        assertEquals(initialSize + 1, repository.posts.value.size)
    }

    @Test
    fun testUpiWithdrawal_deductsBalanceAccurately() {
        val initialBalance = repository.creatorEarnings.value.withdrawableBalanceRupees
        val (success, message) = repository.withdrawEarnings(5000.0, "creator@okhdfcbank")
        assertTrue(success)
        assertEquals(initialBalance - 5000.0, repository.creatorEarnings.value.withdrawableBalanceRupees, 0.01)
        assertTrue(message.contains("5000.0"))
        // Check transaction record
        val latestTxn = repository.creatorEarnings.value.transactions.first()
        assertEquals(5000.0, latestTxn.amountRupees, 0.01)
        assertFalse(latestTxn.isCredit)
    }

    @Test
    fun testWithdrawal_exceedingBalance_failsGracefully() {
        val (success, message) = repository.withdrawEarnings(999999.0, "creator@okhdfcbank")
        assertFalse(success)
        assertTrue(message.contains("Invalid withdrawal amount"))
    }

    @Test
    fun testUpdatePaymentMethod_updatesUpiAndSettings() {
        val newUpi = "newcreator@oksbi"
        val success = repository.updatePaymentMethod(newUpi)
        assertTrue(success)
        assertEquals(newUpi, repository.creatorEarnings.value.upiId)
        assertEquals(newUpi, repository.creatorEarnings.value.paymentMethod.upiId)
    }

    @Test
    fun testPrivateCreatorDashboard_authorizationCheck() {
        val myUser = repository.currentUser.value
        val successRes = repository.getPrivateCreatorDashboard(myUser.id)
        assertTrue(successRes.isSuccess)

        val unauthorizedRes = repository.getPrivateCreatorDashboard("other_impostor_id")
        assertTrue(unauthorizedRes.isFailure)
    }

    @Test
    fun testInfiniteScroll_loadMorePostsIncreasesFeed() {
        val initialCount = repository.posts.value.size
        assertTrue(initialCount > 0)
        assertFalse(repository.isLoadingMore.value)

        repository.loadMorePosts()
        val afterFirstLoad = repository.posts.value.size
        assertTrue(afterFirstLoad > initialCount)

        repository.loadMorePosts()
        val afterSecondLoad = repository.posts.value.size
        assertTrue(afterSecondLoad > afterFirstLoad)
    }
}
