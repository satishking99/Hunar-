package com.example.ui.robot

import com.example.data.model.SkillCategory

/**
 * Configuration and State for the Smart Personal AI Robot Assistant.
 */
data class RobotConfig(
    val isRobotModeOn: Boolean = false,
    val voiceCommandsEnabled: Boolean = true,
    val lapDurationSeconds: Float = 120f, // Exact 2-minute lap (120 seconds)
    val isTwoMinuteLapLocked: Boolean = true,
    val isFiveMinuteLapLocked: Boolean = false,
    val speedMultiplier: Float = 1.0f,
    val isClockwise: Boolean = true,
    val screenNavigationAllowed: Boolean = true,
    val messagingPermissionGranted: Boolean = true,
    val appOpeningPermissionGranted: Boolean = true,
    val touchInterceptionActive: Boolean = false // When Robot Mode is ON, intercepts ordinary touches unless STOP pressed
)

enum class RobotActionType {
    SEND_MESSAGE,
    FIND_SPORTS_VIDEOS,
    OPEN_APP,
    NAVIGATE_SCREEN,
    VOICE_COMMAND,
    STOP_ROBOT,
    ADJUST_SPEED
}

data class PendingAction(
    val id: String,
    val type: RobotActionType,
    val title: String,
    val description: String,
    val recipientOrTarget: String,
    val messagePayload: String = "",
    val extraData: String = ""
)

data class SportsVideo(
    val id: String,
    val title: String,
    val sportCategory: String,
    val creatorName: String,
    val duration: String,
    val description: String,
    val talentScore: Int,
    val thumbnailUrl: String
)

data class SupportedAppItem(
    val id: String,
    val name: String,
    val packageName: String,
    val iconDescription: String,
    val intentAction: String = ""
)

data class RobotContact(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val handle: String,
    val avatarEmoji: String
)

enum class RobotMood {
    IDLE,
    WALKING,
    THINKING,
    TALKING,
    HAPPY,
    ALERT
}
