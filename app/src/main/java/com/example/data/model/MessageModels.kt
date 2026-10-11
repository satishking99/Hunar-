package com.example.data.model

data class ConversationItem(
    val id: String,
    val participantName: String,
    val participantAvatar: String,
    val isGroup: Boolean = false,
    val groupTitle: String? = null,
    val isOnline: Boolean = false,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isRequest: Boolean = false, // Message requests
    val skillTag: String? = null,
    val isBlocked: Boolean = false
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isMe: Boolean,
    val text: String,
    val timestamp: String,
    val messageType: ChatMessageType = ChatMessageType.TEXT,
    val mediaDescription: String? = null,
    val voiceDurationSeconds: Int? = null
)

enum class ChatMessageType {
    TEXT,
    PHOTO,
    VIDEO,
    VOICE
}
