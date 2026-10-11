package com.example.ui.robot

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.components.HunarScreenTab
import com.example.data.network.HunarApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RobotAssistantManager {

    var config by mutableStateOf(RobotConfig())
        private set

    val walkStartUptimeMillis: Long = try {
        val uptime = android.os.SystemClock.uptimeMillis()
        if (uptime > 0L) uptime else System.currentTimeMillis()
    } catch (_: Throwable) {
        System.currentTimeMillis()
    }

    var speechBubbleText by mutableStateOf<String?>(null)
        private set

    var isCommandSheetOpen by mutableStateOf(false)

    var pendingAction by mutableStateOf<PendingAction?>(null)

    var sportsSearchResults by mutableStateOf<List<SportsVideo>>(emptyList())
        private set

    var isSportsResultsVisible by mutableStateOf(false)

    var isAppLauncherVisible by mutableStateOf(false)

    var lastVoiceTranscript by mutableStateOf("")

    var isListeningVoice by mutableStateOf(false)

    val contactSatish = RobotContact(
        name = "Satish Kumar",
        phoneNumber = "+91 98765 43210",
        email = "satishhp2000@gmail.com",
        handle = "@satish_creator",
        avatarEmoji = "🏏"
    )

    val supportedApps = listOf(
        SupportedAppItem("app_yt", "YouTube", "com.google.android.youtube", "Video Player"),
        SupportedAppItem("app_wa", "WhatsApp", "com.whatsapp", "Messaging"),
        SupportedAppItem("app_maps", "Google Maps", "com.google.android.apps.maps", "Navigation"),
        SupportedAppItem("app_cam", "Camera", "camera", "Device Camera", MediaStore.ACTION_IMAGE_CAPTURE),
        SupportedAppItem("app_calc", "Calculator", "calculator", "System Calculator"),
        SupportedAppItem("app_browser", "Web Browser", "browser", "Internet Browser"),
        SupportedAppItem("app_dialer", "Phone Dialer", "dialer", "Phone App"),
        SupportedAppItem("app_settings", "Device Settings", "settings", "Android Settings", Settings.ACTION_SETTINGS)
    )

    private val allSportsVideos = listOf(
        SportsVideo(
            id = "sp_1",
            title = "Fast Bowling Yorker Technique & Seam Position",
            sportCategory = "Cricket",
            creatorName = "Satish Kumar (Fast Bowler)",
            duration = "0:45",
            description = "Detailed slow-motion release analysis showing wrist snap, seam alignment, and toe-crusher trajectory.",
            talentScore = 98,
            thumbnailUrl = "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=600"
        ),
        SportsVideo(
            id = "sp_2",
            title = "Freestyle Football: Around the World & Ground Moves",
            sportCategory = "Football",
            creatorName = "Sameer Akhtar",
            duration = "0:52",
            description = "Street freestyle combo executed on gravel ground without expensive gear. Pure ball control mastery.",
            talentScore = 95,
            thumbnailUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600"
        ),
        SportsVideo(
            id = "sp_3",
            title = "Pro Kabaddi Ankle Hold & Thigh Block Drills",
            sportCategory = "Kabaddi",
            creatorName = "Raju Pehelwan",
            duration = "0:38",
            description = "Traditional clay akhada training for explosive defensive dives and balance lock against raiders.",
            talentScore = 96,
            thumbnailUrl = "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=600"
        ),
        SportsVideo(
            id = "sp_4",
            title = "Sprint Acceleration & 100m Block Start Mechanics",
            sportCategory = "Athletics",
            creatorName = "Pooja Verma (State Sprinter)",
            duration = "0:42",
            description = "Biomechanics breakdown of the first 15 steps drive phase with explosive hip extension.",
            talentScore = 94,
            thumbnailUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600"
        ),
        SportsVideo(
            id = "sp_5",
            title = "Badminton Backhand Clear & Jump Smash Timing",
            sportCategory = "Badminton",
            creatorName = "Aditya Sen",
            duration = "0:49",
            description = "Pronation technique drills to hit deep baseline clears from difficult backhand corners.",
            talentScore = 92,
            thumbnailUrl = "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600"
        ),
        SportsVideo(
            id = "sp_6",
            title = "Kushti Dhobi Pachhad Throw & Hip Twist",
            sportCategory = "Wrestling",
            creatorName = "Sultan Akhada",
            duration = "0:40",
            description = "Authentic Indian mud wrestling technique with leverage and safe landing execution.",
            talentScore = 97,
            thumbnailUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600"
        )
    )

    // Configuration modifiers
    fun toggleRobotMode(enabled: Boolean) {
        config = config.copy(
            isRobotModeOn = enabled,
            touchInterceptionActive = if (!enabled) false else config.touchInterceptionActive
        )
        speechBubbleText = if (enabled) {
            "Robot Mode ON! Ready for your voice or text commands 🦾"
        } else {
            "Robot Mode OFF. Touch control restored. Walking calmly 🚶"
        }
    }

    fun toggleVoiceCommands(enabled: Boolean) {
        config = config.copy(voiceCommandsEnabled = enabled)
    }

    fun setTwoMinuteLap(locked: Boolean) {
        config = config.copy(
            isTwoMinuteLapLocked = locked,
            lapDurationSeconds = if (locked) 120f else config.lapDurationSeconds
        )
        speechBubbleText = if (locked) "Lap locked to exactly 2 minutes (120s) ⏱️" else "Lap time customized."
    }

    fun setFiveMinuteLap(locked: Boolean) {
        config = config.copy(
            isFiveMinuteLapLocked = locked,
            lapDurationSeconds = if (locked) 300f else config.lapDurationSeconds
        )
        speechBubbleText = if (locked) "Lap locked to 5 minutes (300s) ⏱️" else "Lap time customized."
    }

    fun setSpeedMultiplier(multiplier: Float) {
        val baseDuration = 120f / multiplier.coerceIn(0.2f, 10f)
        config = config.copy(
            speedMultiplier = multiplier,
            lapDurationSeconds = baseDuration,
            isTwoMinuteLapLocked = multiplier == 1.0f
        )
        speechBubbleText = "Speed set to ${multiplier}x (${baseDuration.toInt()}s full lap)"
    }

    fun setScreenNavigationAllowed(allowed: Boolean) {
        config = config.copy(screenNavigationAllowed = allowed)
    }

    fun setMessagingPermission(granted: Boolean) {
        config = config.copy(messagingPermissionGranted = granted)
    }

    fun setAppOpeningPermission(granted: Boolean) {
        config = config.copy(appOpeningPermissionGranted = granted)
    }

    fun setTouchInterception(active: Boolean) {
        config = config.copy(touchInterceptionActive = active)
        speechBubbleText = if (active) "Robot guidance active. Press emergency STOP to restore touch." else "Normal touch restored."
    }

    fun emergencyStop() {
        config = config.copy(
            touchInterceptionActive = false
        )
        pendingAction = null
        isListeningVoice = false
        speechBubbleText = "Emergency STOP engaged! Touch control restored immediately 🛑"
    }

    fun resetToDefaults() {
        config = RobotConfig()
        pendingAction = null
        isSportsResultsVisible = false
        isAppLauncherVisible = false
        speechBubbleText = "Settings reset to defaults (5-min lap, touch restored) 🔄"
    }

    fun setBubble(text: String?) {
        speechBubbleText = text
    }

    // Natural Language Command Parser
    fun processCommand(
        query: String,
        context: Context,
        onNavigate: (HunarScreenTab) -> Unit,
        onOpenSettings: () -> Unit
    ) {
        val trimmed = query.trim().lowercase()
        lastVoiceTranscript = query

        if (trimmed.isEmpty()) return

        // Asynchronously synchronize command with backend audit engine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                HunarApiClient.processRobotCommand(query)
            } catch (e: Exception) {
                // Offline resilience preserved
            }
        }

        // 1. Emergency Stop
        if (trimmed.contains("stop") || trimmed.contains("emergency") || trimmed.contains("freeze")) {
            emergencyStop()
            Toast.makeText(context, "Emergency STOP: Robot touch control restored", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Message Satish
        if (trimmed.contains("satish") || (trimmed.contains("message") && trimmed.contains("hi")) || trimmed.contains("send message")) {
            initiateSatishMessage(context)
            return
        }

        // 3. Find Sports Videos
        if (trimmed.contains("sport") || trimmed.contains("cricket") || trimmed.contains("football") ||
            trimmed.contains("kabaddi") || trimmed.contains("athletic") || trimmed.contains("match") || trimmed.contains("game")) {
            searchSportsVideos(query)
            return
        }

        // 4. Open External Apps
        if (trimmed.startsWith("open ") || trimmed.startsWith("launch ") || trimmed.contains("app")) {
            when {
                trimmed.contains("youtube") -> prepareOpenApp("app_yt", context)
                trimmed.contains("whatsapp") -> prepareOpenApp("app_wa", context)
                trimmed.contains("maps") || trimmed.contains("google maps") -> prepareOpenApp("app_maps", context)
                trimmed.contains("camera") -> prepareOpenApp("app_cam", context)
                trimmed.contains("calculator") -> prepareOpenApp("app_calc", context)
                trimmed.contains("browser") || trimmed.contains("chrome") -> prepareOpenApp("app_browser", context)
                trimmed.contains("settings") && !trimmed.contains("hunar") -> prepareOpenApp("app_settings", context)
                trimmed.contains("phone") || trimmed.contains("dialer") -> prepareOpenApp("app_dialer", context)
                else -> {
                    isAppLauncherVisible = true
                    speechBubbleText = "Here are the supported applications I can help you open 📱"
                }
            }
            return
        }

        // 5. Navigate Hunar Screens
        if (trimmed.contains("home") || trimmed.contains("feed")) {
            requestScreenNavigation(HunarScreenTab.HOME, "Home Feed", onNavigate)
            return
        }
        if (trimmed.contains("reel") || trimmed.contains("discover") || trimmed.contains("search")) {
            requestScreenNavigation(HunarScreenTab.REELS_SEARCH, "Reels & Search", onNavigate)
            return
        }
        if (trimmed.contains("message") || trimmed.contains("chat") || trimmed.contains("inbox")) {
            requestScreenNavigation(HunarScreenTab.MESSAGES, "Messages", onNavigate)
            return
        }
        if (trimmed.contains("profile") || trimmed.contains("dashboard") || trimmed.contains("creator hub")) {
            requestScreenNavigation(HunarScreenTab.PROFILE, "Profile & Creator Hub", onNavigate)
            return
        }
        if (trimmed.contains("setting")) {
            if (config.screenNavigationAllowed) {
                onOpenSettings()
                speechBubbleText = "Navigated to Hunar Settings ⚙️"
            } else {
                speechBubbleText = "Screen navigation is turned off in Robot Settings ⚠️"
            }
            return
        }

        // 6. Mode Toggles
        if (trimmed.contains("robot on") || trimmed.contains("turn on robot") || trimmed.contains("enable robot")) {
            toggleRobotMode(true)
            return
        }
        if (trimmed.contains("robot off") || trimmed.contains("turn off robot") || trimmed.contains("disable robot")) {
            toggleRobotMode(false)
            return
        }

        // 7. Speed commands
        if (trimmed.contains("fast") || trimmed.contains("speed up")) {
            setSpeedMultiplier(2.5f)
            return
        }
        if (trimmed.contains("normal") || trimmed.contains("5 min") || trimmed.contains("five min")) {
            setFiveMinuteLap(true)
            setSpeedMultiplier(1.0f)
            return
        }

        // Default fallback friendly explanation
        speechBubbleText = "Understood: '$query'. Try asking me to 'Message Satish', 'Find sports videos', 'Open YouTube', or 'Go to Reels'!"
    }

    // Feature 1: Send Messages (Satish preparation + explicit approval confirmation)
    fun initiateSatishMessage(context: Context) {
        if (!config.messagingPermissionGranted) {
            speechBubbleText = "Messaging permission is currently disabled in Robot Settings ⚠️"
            Toast.makeText(context, "Please enable Messaging Permission in Robot Settings", Toast.LENGTH_SHORT).show()
            return
        }

        pendingAction = PendingAction(
            id = "action_satish_hi",
            type = RobotActionType.SEND_MESSAGE,
            title = "Confirm Message to Satish",
            description = "The AI Robot Assistant prepared the message 'Hi' for contact Satish Kumar.",
            recipientOrTarget = "${contactSatish.name} (${contactSatish.phoneNumber})",
            messagePayload = "Hi",
            extraData = contactSatish.handle
        )
        speechBubbleText = "Prepared message 'Hi' for Satish. Please confirm approval to send! 💬"
    }

    fun executeApprovedAction(context: Context, onNavigate: ((HunarScreenTab) -> Unit)? = null) {
        val action = pendingAction ?: return
        when (action.type) {
            RobotActionType.SEND_MESSAGE -> {
                try {
                    // Supported Android Intent: ACTION_SENDTO with smsto: scheme
                    val smsUri = Uri.parse("smsto:${contactSatish.phoneNumber.replace(" ", "")}")
                    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                        putExtra("sms_body", action.messagePayload)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(smsIntent)
                    speechBubbleText = "Opened messaging app with 'Hi' prepared for Satish! ✅"
                    Toast.makeText(context, "Dispatched message to Satish: 'Hi'", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    // Graceful fallback to general SEND intent
                    try {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, action.messagePayload)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Send 'Hi' via...").apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        speechBubbleText = "Opened app chooser for sending 'Hi' ✅"
                    } catch (e2: Exception) {
                        speechBubbleText = "Could not open messaging intent: ${e2.localizedMessage}"
                    }
                }
            }

            RobotActionType.OPEN_APP -> {
                launchAppDirect(action.extraData, context)
            }

            RobotActionType.NAVIGATE_SCREEN -> {
                // Handled in callback
            }

            else -> {}
        }
        pendingAction = null
    }

    fun cancelPendingAction() {
        pendingAction = null
        speechBubbleText = "Action canceled safely. What would you like to do next? 🤖"
    }

    // Feature 2: Find Sports Videos
    fun searchSportsVideos(query: String? = null) {
        val filter = query?.lowercase()?.trim() ?: ""
        sportsSearchResults = if (filter.isBlank() || filter == "sports" || filter == "find sports") {
            allSportsVideos
        } else {
            allSportsVideos.filter {
                it.title.lowercase().contains(filter) ||
                it.sportCategory.lowercase().contains(filter) ||
                it.description.lowercase().contains(filter) ||
                it.creatorName.lowercase().contains(filter)
            }.ifEmpty { allSportsVideos }
        }
        isSportsResultsVisible = true
        speechBubbleText = "Found ${sportsSearchResults.size} sports video masterclasses! 🏆"
    }

    fun closeSportsResults() {
        isSportsResultsVisible = false
    }

    // Feature 3: Open Supported Applications
    fun prepareOpenApp(appId: String, context: Context) {
        if (!config.appOpeningPermissionGranted) {
            speechBubbleText = "App-opening permission is disabled in Robot Settings ⚠️"
            Toast.makeText(context, "Enable App-Opening Permission in Robot Settings", Toast.LENGTH_SHORT).show()
            return
        }

        val appItem = supportedApps.find { it.id == appId } ?: return
        pendingAction = PendingAction(
            id = "action_open_${appItem.id}",
            type = RobotActionType.OPEN_APP,
            title = "Open ${appItem.name}?",
            description = "AI Robot Assistant will launch ${appItem.name} using supported Android intents.",
            recipientOrTarget = appItem.name,
            extraData = appItem.id
        )
        speechBubbleText = "Ready to launch ${appItem.name}. Confirm to proceed 📱"
    }

    private fun launchAppDirect(appId: String, context: Context) {
        val appItem = supportedApps.find { it.id == appId } ?: return
        try {
            when (appItem.id) {
                "app_yt" -> {
                    val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                        setPackage("com.google.android.youtube")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (context.packageManager.resolveActivity(ytIntent, 0) != null) {
                        context.startActivity(ytIntent)
                    } else {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    speechBubbleText = "Launched YouTube 🎬"
                }

                "app_wa" -> {
                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+919876543210&text=Hi")).apply {
                        setPackage("com.whatsapp")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (context.packageManager.resolveActivity(waIntent, 0) != null) {
                        context.startActivity(waIntent)
                        speechBubbleText = "Opened WhatsApp 💬"
                    } else {
                        Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show()
                        speechBubbleText = "WhatsApp is not installed on this device"
                    }
                }

                "app_maps" -> {
                    val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=India")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(mapsIntent)
                    speechBubbleText = "Opened Google Maps 🗺️"
                }

                "app_cam" -> {
                    val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(camIntent)
                    speechBubbleText = "Opened Camera 📸"
                }

                "app_calc" -> {
                    try {
                        val calcIntent = Intent().apply {
                            action = Intent.ACTION_MAIN
                            addCategory(Intent.CATEGORY_APP_CALCULATOR)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(calcIntent)
                        speechBubbleText = "Opened Calculator 🧮"
                    } catch (e: Exception) {
                        Toast.makeText(context, "Calculator app not found on this system", Toast.LENGTH_SHORT).show()
                        speechBubbleText = "Calculator not available on this system"
                    }
                }

                "app_browser" -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hunar.app")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(browserIntent)
                    speechBubbleText = "Opened Web Browser 🌐"
                }

                "app_dialer" -> {
                    val dialerIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(dialerIntent)
                    speechBubbleText = "Opened Phone Dialer 📞"
                }

                "app_settings" -> {
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                    speechBubbleText = "Opened Android System Settings ⚙️"
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open ${appItem.name}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            speechBubbleText = "Error opening ${appItem.name}: ${e.localizedMessage}"
        }
    }

    // Feature 4: Screen Navigation
    private fun requestScreenNavigation(tab: HunarScreenTab, tabName: String, onNavigate: (HunarScreenTab) -> Unit) {
        if (!config.screenNavigationAllowed) {
            speechBubbleText = "Screen navigation is turned off in Robot Settings ⚠️"
            return
        }
        onNavigate(tab)
        speechBubbleText = "Navigated to $tabName! 🚀"
    }
}
