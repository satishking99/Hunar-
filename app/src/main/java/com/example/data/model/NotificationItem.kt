package com.example.data.model

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val actionUrl: String? = null
)

enum class NotificationType(val icon: String, val badgeColorHex: Long) {
    FOLLOWER("👤", 0xFFFF7A1A),
    FOLLOW_REQUEST("🤝", 0xFF2E6F8E),
    ACCEPTED_FOLLOW("✅", 0xFF22C55E),
    LIKE("❤️", 0xFFEF4444),
    COMMENT("💬", 0xFFFF9800),
    SHARE("🔄", 0xFF63567D),
    MENTION("🏷️", 0xFF387352),
    MESSAGE("✉️", 0xFF2E6F8E),
    LIVE("🔴", 0xFFEF4444),
    EARNINGS("💰", 0xFF22C55E),
    CHALLENGE("🏆", 0xFFFFB703),
    PLATFORM_ANNOUNCEMENT("📢", 0xFFFF7A1A),
    SECURITY_ALERT("🛡️", 0xFFBA4545)
}
