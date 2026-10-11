package com.example.data.model

data class CreatorEarnings(
    val totalBalanceRupees: Double = 12450.0,
    val availableBalanceRupees: Double = 12450.0,
    val pendingEarningsRupees: Double = 4350.0, // Quality audit holding period
    val withdrawableBalanceRupees: Double = 12450.0,
    val totalEarningsRupees: Double = 16800.0, // Confirmed + Pending
    val totalLikes: Int = 28450,
    val totalViews: String = "148.5K",
    val totalComments: Int = 1840,
    val totalShares: Int = 3210,
    val totalSaves: Int = 4680,
    val watchTimeHours: String = "1,420h",
    val averageWatchDuration: String = "3m 48s",
    val followersGrowth: String = "+2,480 this month",
    val engagementRate: String = "9.4%",
    val earningsGrowth: String = "+18% this month",
    val viewsGrowth: String = "+14% this week",
    val watchTimeGrowth: String = "+11% this month",
    val topCategoryName: String = "Technology",
    val topCategoryStat: String = "34% of views • +24% growth",
    val avgTalentScore: Double = 94.2,
    val monthlyGrowthPercentage: String = "+18% this month",
    val viewMonetizationRupees: Double = 6900.0,
    val tipsReceivedRupees: Double = 2350.0,
    val liveEarningsRupees: Double = 1800.0,
    val brandSponsorshipRupees: Double = 1400.0,
    val estimatedEarningsRupees: Double = 16800.0,
    val penaltyZeroEarningLossRupees: Double = 0.0,
    val upiId: String = "arjun.hunar@okhdfcbank",
    val paymentMethod: PaymentMethodSettings = PaymentMethodSettings(),
    val transactions: List<EarningsTransaction> = defaultTransactionsList,
    val withdrawalHistory: List<WithdrawalRecord> = listOf(
        WithdrawalRecord("WTH-9821", 5000.0, "arjun.hunar@okhdfcbank", "Completed", "Yesterday, 4:15 PM"),
        WithdrawalRecord("WTH-7412", 3200.0, "arjun.hunar@okhdfcbank", "Completed", "28 Sep, 11:30 AM"),
        WithdrawalRecord("WTH-4310", 2500.0, "arjun.hunar@okhdfcbank", "Completed", "15 Sep, 02:10 PM"),
        WithdrawalRecord("WTH-1029", 1750.0, "arjun.hunar@okhdfcbank", "Completed", "01 Sep, 10:45 AM")
    ),
    val videoAnalytics: List<VideoPerformanceAnalytics> = listOf(
        VideoPerformanceAnalytics(
            id = "vid_1",
            title = "Self-Balancing Rover with SLAM Navigation",
            type = "Video",
            datePublished = "3 days ago",
            viewsCount = 48200,
            likesCount = 9420,
            commentsCount = 680,
            sharesCount = 1204,
            savesCount = 1840,
            watchTimeHours = "640h",
            averageWatchDuration = "4m 12s",
            audienceRetentionPercent = 91,
            estimatedEarningsRupees = 7100.0
        ),
        VideoPerformanceAnalytics(
            id = "vid_2",
            title = "Calisthenics Form Check & Core Strength",
            type = "Video",
            datePublished = "5 days ago",
            viewsCount = 38400,
            likesCount = 3840,
            commentsCount = 214,
            sharesCount = 512,
            savesCount = 980,
            watchTimeHours = "312h",
            averageWatchDuration = "3m 25s",
            audienceRetentionPercent = 86,
            estimatedEarningsRupees = 4250.0
        ),
        VideoPerformanceAnalytics(
            id = "vid_3",
            title = "Quick watercolor tip 🎨",
            type = "Reel",
            datePublished = "1 week ago",
            viewsCount = 142000,
            likesCount = 24800,
            commentsCount = 512,
            sharesCount = 2890,
            savesCount = 3450,
            watchTimeHours = "720h",
            averageWatchDuration = "52s",
            audienceRetentionPercent = 94,
            estimatedEarningsRupees = 8400.0
        ),
        VideoPerformanceAnalytics(
            id = "vid_4",
            title = "Microcontroller Low-Power Deep Sleep Mode",
            type = "Video",
            datePublished = "2 weeks ago",
            viewsCount = 22100,
            likesCount = 2150,
            commentsCount = 142,
            sharesCount = 380,
            savesCount = 620,
            watchTimeHours = "184h",
            averageWatchDuration = "3m 05s",
            audienceRetentionPercent = 88,
            estimatedEarningsRupees = 2450.0
        )
    )
)

data class VideoPerformanceAnalytics(
    val id: String,
    val title: String,
    val type: String,
    val datePublished: String,
    val viewsCount: Int,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val savesCount: Int,
    val watchTimeHours: String,
    val averageWatchDuration: String,
    val audienceRetentionPercent: Int,
    val estimatedEarningsRupees: Double
)

data class WithdrawalRecord(
    val id: String,
    val amountRupees: Double,
    val upiOrBank: String,
    val status: String,
    val date: String
)

data class DailyPerformance(
    val day: String,
    val earningsRupees: Int,
    val watchHours: Int,
    val talentScore: Int
)

data class PaymentMethodSettings(
    val upiId: String = "arjun.hunar@okhdfcbank",
    val bankAccountHolder: String = "Arjun Verma",
    val bankAccountNumber: String = "••••••••4812",
    val bankIfsc: String = "HDFC0001248",
    val bankName: String = "HDFC Bank Ltd",
    val isVerified: Boolean = true
)

enum class TransactionType(val label: String, val emoji: String) {
    WITHDRAWAL("Payout / Withdrawal", "🏦"),
    VIEW_MONETIZATION("View Monetization", "👁️"),
    SHABAASHI_TIP("Viewer Shabaashi Tip", "💎"),
    LIVE_SESSION("Live Stream Q&A", "🔴"),
    SPONSORSHIP("Brand Challenge Bonus", "🏆")
}

data class EarningsTransaction(
    val id: String,
    val title: String,
    val type: TransactionType,
    val amountRupees: Double,
    val isCredit: Boolean,
    val status: String, // "Completed", "Processing", "Pending", "Failed"
    val date: String,
    val referenceId: String,
    val paymentTarget: String = "UPI • arjun.hunar@okhdfcbank",
    val sourceOrNotes: String = ""
)

val defaultTransactionsList = listOf(
    EarningsTransaction(
        id = "TXN-9821",
        title = "Instant UPI Payout to arjun.hunar@okhdfcbank",
        type = TransactionType.WITHDRAWAL,
        amountRupees = 5000.0,
        isCredit = false,
        status = "Completed",
        date = "Yesterday, 4:15 PM",
        referenceId = "UTR-8392019482",
        paymentTarget = "UPI • arjun.hunar@okhdfcbank",
        sourceOrNotes = "Immediate 24x7 IMPS Payout successfully credited"
    ),
    EarningsTransaction(
        id = "TXN-8842",
        title = "STEM Hardware Monetization (Rover Project)",
        type = TransactionType.VIEW_MONETIZATION,
        amountRupees = 3200.0,
        isCredit = true,
        status = "Completed",
        date = "03 Oct, 08:30 PM",
        referenceId = "REF-29481039",
        paymentTarget = "Hunar Creator Pool",
        sourceOrNotes = "48.2K verified STEM tutorial views (Merit score 96/100)"
    ),
    EarningsTransaction(
        id = "TXN-7412",
        title = "Instant UPI Payout to arjun.hunar@okhdfcbank",
        type = TransactionType.WITHDRAWAL,
        amountRupees = 3200.0,
        isCredit = false,
        status = "Completed",
        date = "28 Sep, 11:30 AM",
        referenceId = "UTR-7102948194",
        paymentTarget = "UPI • arjun.hunar@okhdfcbank",
        sourceOrNotes = "Immediate 24x7 IMPS Payout successfully credited"
    ),
    EarningsTransaction(
        id = "TXN-6310",
        title = "Viewer Shabaashi Tips (58 Supporters)",
        type = TransactionType.SHABAASHI_TIP,
        amountRupees = 1450.0,
        isCredit = true,
        status = "Completed",
        date = "22 Sep, 06:40 PM",
        referenceId = "REF-10928471",
        paymentTarget = "In-App Viewer Tips",
        sourceOrNotes = "Community appreciation for open-source CAD blueprints"
    ),
    EarningsTransaction(
        id = "TXN-5120",
        title = "Verified Live Robotics Code Sprint",
        type = TransactionType.LIVE_SESSION,
        amountRupees = 1800.0,
        isCredit = true,
        status = "Completed",
        date = "18 Sep, 09:15 PM",
        referenceId = "REF-98172635",
        paymentTarget = "Live Session Pool",
        sourceOrNotes = "1,400 live participants, zero lip-sync penalty"
    ),
    EarningsTransaction(
        id = "TXN-4310",
        title = "Instant UPI Payout to arjun.hunar@okhdfcbank",
        type = TransactionType.WITHDRAWAL,
        amountRupees = 2500.0,
        isCredit = false,
        status = "Completed",
        date = "15 Sep, 02:10 PM",
        referenceId = "UTR-6192837465",
        paymentTarget = "UPI • arjun.hunar@okhdfcbank",
        sourceOrNotes = "Immediate 24x7 IMPS Payout successfully credited"
    ),
    EarningsTransaction(
        id = "TXN-3010",
        title = "Calisthenics Form Check Ad Revenue",
        type = TransactionType.VIEW_MONETIZATION,
        amountRupees = 2100.0,
        isCredit = true,
        status = "Completed",
        date = "10 Sep, 01:20 PM",
        referenceId = "REF-83746192",
        paymentTarget = "Hunar Creator Pool",
        sourceOrNotes = "Fitness verified guide • 38.4K genuine views"
    ),
    EarningsTransaction(
        id = "TXN-1029",
        title = "Instant UPI Payout to arjun.hunar@okhdfcbank",
        type = TransactionType.WITHDRAWAL,
        amountRupees = 1750.0,
        isCredit = false,
        status = "Completed",
        date = "01 Sep, 10:45 AM",
        referenceId = "UTR-5192837461",
        paymentTarget = "UPI • arjun.hunar@okhdfcbank",
        sourceOrNotes = "Immediate 24x7 IMPS Payout successfully credited"
    )
)
