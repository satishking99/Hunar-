package com.example.ui.robot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

/**
 * Smart Personal AI Robot Assistant Overlay:
 * - Continuously walks along all four edges of the visible Hunar screen in a clockwise rectangular loop.
 * - Exact path: TOP LEFT → TOP RIGHT → BOTTOM RIGHT → BOTTOM LEFT → TOP LEFT.
 * - Exact 120-second (2 minutes) lap duration, with each side taking proportional time (~30s base).
 * - Smooth rounded corner turns with heading angle rotation (0° -> 90° -> 180° -> 270° -> 0°).
 * - Continuous leg & counterbalanced arm walking animation.
 * - Stays visible across all Hunar screens without duplicating or resetting.
 * - 100% transparent background with zero speech bubbles or greeting banners.
 * - Non-interfering: Only tapping the robot itself triggers the command sheet; all other screen touches pass through.
 */
@Composable
fun RobotAssistantOverlay(
    manager: RobotAssistantManager,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Vsync-synchronized display loop (~60fps) for smooth, battery-efficient animation
    var currentFrameTimeMillis by remember {
        mutableLongStateOf(
            try {
                val uptime = android.os.SystemClock.uptimeMillis()
                if (uptime > 0L) uptime else System.currentTimeMillis()
            } catch (_: Throwable) {
                System.currentTimeMillis()
            }
        )
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis { frameTime ->
                currentFrameTimeMillis = frameTime
            }
        }
    }

    val elapsedMillis = currentFrameTimeMillis - manager.walkStartUptimeMillis
    val robotSizeDp = 42.dp

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat().coerceAtLeast(120f)
    val screenHeightDp = configuration.screenHeightDp.toFloat().coerceAtLeast(120f)

    // Calculate exact screen coordinates along the rectangular perimeter
    val trackPos = remember(
        elapsedMillis,
        manager.config.lapDurationSeconds,
        screenWidthDp,
        screenHeightDp
    ) {
        RobotMovementCalculator.calculatePosition(
            elapsedMillis = elapsedMillis,
            lapDurationSeconds = manager.config.lapDurationSeconds,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            robotSizeDp = robotSizeDp.value,
            insetLeftDp = 18f,
            insetRightDp = 18f,
            insetTopDp = 58f,
            insetBottomDp = 96f,
            cornerRadiusDp = 24f
        )
    }

    // Cute 3D Animated Robot — only occupies its own 42dp bounds, never blocks screen touches or bottom navigation
    Box(
        modifier = modifier
            .offset(x = trackPos.xDp.dp, y = trackPos.yDp.dp)
            .size(robotSizeDp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                manager.isCommandSheetOpen = true
            }
            .testTag("cute_floating_robot")
    ) {
        CuteRobot3DCanvas(
            size = robotSizeDp,
            headingAngle = trackPos.headingAngle,
            walkPhase = trackPos.legPhase,
            isRobotModeOn = manager.config.isRobotModeOn,
            mood = if (manager.config.isRobotModeOn) RobotMood.HAPPY else RobotMood.WALKING
        )
    }
}
