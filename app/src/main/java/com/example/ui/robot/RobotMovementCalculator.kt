package com.example.ui.robot

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * High-precision, zero-drift calculation of the Hunar AI Robot Assistant's
 * continuous rectangular perimeter walking path.
 *
 * Route:
 * TOP LEFT → TOP RIGHT → BOTTOM RIGHT → BOTTOM LEFT → TOP LEFT (Clockwise Loop)
 *
 * Specifications:
 * 1. Continuous movement along all 4 edges of visible mobile screen.
 * 2. One complete round takes exactly 120 seconds (2 minutes).
 * 3. Each side duration is proportional to its actual side length (~30s base).
 * 4. Smooth, natural rounded corner turns without freezing or teleporting.
 * 5. Dynamic adaptation when viewport size or orientation changes.
 * 6. Synchronized leg oscillation and arm counterbalancing for real walking.
 */
data class RobotTrackPosition(
    val xDp: Float,
    val yDp: Float,
    val headingAngle: Float, // in degrees: 0 = right, 90 = down, 180 = left, 270 = up
    val legPhase: Float,
    val segmentName: String
)

object RobotMovementCalculator {
    const val DEFAULT_LAP_SECONDS = 120f
    const val DEFAULT_ROBOT_SIZE_DP = 42f
    const val DEFAULT_INSET_LEFT_DP = 18f
    const val DEFAULT_INSET_RIGHT_DP = 18f
    const val DEFAULT_INSET_TOP_DP = 58f
    const val DEFAULT_INSET_BOTTOM_DP = 96f
    const val DEFAULT_CORNER_RADIUS_DP = 24f
    const val STEP_CYCLE_MILLIS = 450L

    /**
     * Calculates the exact screen position, heading angle, and leg swing phase
     * based on elapsed milliseconds.
     */
    fun calculatePosition(
        elapsedMillis: Long,
        lapDurationSeconds: Float = DEFAULT_LAP_SECONDS,
        screenWidthDp: Float,
        screenHeightDp: Float,
        robotSizeDp: Float = DEFAULT_ROBOT_SIZE_DP,
        insetLeftDp: Float = DEFAULT_INSET_LEFT_DP,
        insetRightDp: Float = DEFAULT_INSET_RIGHT_DP,
        insetTopDp: Float = DEFAULT_INSET_TOP_DP,
        insetBottomDp: Float = DEFAULT_INSET_BOTTOM_DP,
        cornerRadiusDp: Float = DEFAULT_CORNER_RADIUS_DP
    ): RobotTrackPosition {
        val lapDurationMs = (lapDurationSeconds * 1000f).toLong().coerceAtLeast(1000L)
        val normalizedTime = (elapsedMillis % lapDurationMs)
        val progress = (if (normalizedTime < 0) normalizedTime + lapDurationMs else normalizedTime).toFloat() / lapDurationMs.toFloat()

        return calculatePositionFromProgress(
            progress = progress,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            robotSizeDp = robotSizeDp,
            insetLeftDp = insetLeftDp,
            insetRightDp = insetRightDp,
            insetTopDp = insetTopDp,
            insetBottomDp = insetBottomDp,
            cornerRadiusDp = cornerRadiusDp,
            elapsedMillis = elapsedMillis
        )
    }

    /**
     * Calculates position and heading for a given progress fraction in [0.0, 1.0).
     */
    fun calculatePositionFromProgress(
        progress: Float,
        screenWidthDp: Float,
        screenHeightDp: Float,
        robotSizeDp: Float = DEFAULT_ROBOT_SIZE_DP,
        insetLeftDp: Float = DEFAULT_INSET_LEFT_DP,
        insetRightDp: Float = DEFAULT_INSET_RIGHT_DP,
        insetTopDp: Float = DEFAULT_INSET_TOP_DP,
        insetBottomDp: Float = DEFAULT_INSET_BOTTOM_DP,
        cornerRadiusDp: Float = DEFAULT_CORNER_RADIUS_DP,
        elapsedMillis: Long = 0L
    ): RobotTrackPosition {
        val xMin = insetLeftDp
        val xMax = max(xMin + 10f, screenWidthDp - insetRightDp - robotSizeDp)
        val yMin = insetTopDp
        val yMax = max(yMin + 10f, screenHeightDp - insetBottomDp - robotSizeDp)

        val totalW = xMax - xMin
        val totalH = yMax - yMin

        // Safe corner radius that fits inside track
        val r = min(cornerRadiusDp, min(totalW / 2f, totalH / 2f)).coerceAtLeast(0f)

        // 4 straight segments
        val lTop = max(0f, totalW - 2f * r)
        val lRight = max(0f, totalH - 2f * r)
        val lBottom = max(0f, totalW - 2f * r)
        val lLeft = max(0f, totalH - 2f * r)

        // 4 corner arcs (quarter circle: length = (PI / 2) * r)
        val lArc = (PI.toFloat() / 2f) * r

        // Total perimeter
        val perimeter = lTop + lArc + lRight + lArc + lBottom + lArc + lLeft + lArc

        if (perimeter <= 0.001f) {
            return RobotTrackPosition(
                xDp = xMin,
                yDp = yMin,
                headingAngle = 0f,
                legPhase = 0f,
                segmentName = "TOP_LEFT"
            )
        }

        // Normalized distance along perimeter [0, perimeter)
        var clampedProgress = progress % 1.0f
        if (clampedProgress < 0f) clampedProgress += 1.0f
        val distance = clampedProgress * perimeter

        // Leg walking phase
        val legPhase = ((elapsedMillis % STEP_CYCLE_MILLIS).toFloat() / STEP_CYCLE_MILLIS.toFloat()) * (2f * PI.toFloat())

        var remaining = distance

        // 1. TOP EDGE (Left to Right)
        if (remaining < lTop) {
            val fraction = if (lTop > 0f) remaining / lTop else 0f
            val x = (xMin + r) + fraction * lTop
            val y = yMin
            return RobotTrackPosition(x, y, 0f, legPhase, "TOP_EDGE")
        }
        remaining -= lTop

        // 2. TOP-RIGHT CORNER (Turning Right -> Down: 0° -> 90°)
        if (remaining < lArc) {
            val fraction = if (lArc > 0f) remaining / lArc else 0f
            val angleRad = -PI.toFloat() / 2f + fraction * (PI.toFloat() / 2f)
            val centerX = xMax - r
            val centerY = yMin + r
            val x = centerX + r * cos(angleRad)
            val y = centerY + r * sin(angleRad)
            val heading = fraction * 90f
            return RobotTrackPosition(x, y, heading, legPhase, "CORNER_TOP_RIGHT")
        }
        remaining -= lArc

        // 3. RIGHT EDGE (Top to Bottom)
        if (remaining < lRight) {
            val fraction = if (lRight > 0f) remaining / lRight else 0f
            val x = xMax
            val y = (yMin + r) + fraction * lRight
            return RobotTrackPosition(x, y, 90f, legPhase, "RIGHT_EDGE")
        }
        remaining -= lRight

        // 4. BOTTOM-RIGHT CORNER (Turning Down -> Left: 90° -> 180°)
        if (remaining < lArc) {
            val fraction = if (lArc > 0f) remaining / lArc else 0f
            val angleRad = fraction * (PI.toFloat() / 2f)
            val centerX = xMax - r
            val centerY = yMax - r
            val x = centerX + r * cos(angleRad)
            val y = centerY + r * sin(angleRad)
            val heading = 90f + fraction * 90f
            return RobotTrackPosition(x, y, heading, legPhase, "CORNER_BOTTOM_RIGHT")
        }
        remaining -= lArc

        // 5. BOTTOM EDGE (Right to Left)
        if (remaining < lBottom) {
            val fraction = if (lBottom > 0f) remaining / lBottom else 0f
            val x = (xMax - r) - fraction * lBottom
            val y = yMax
            return RobotTrackPosition(x, y, 180f, legPhase, "BOTTOM_EDGE")
        }
        remaining -= lBottom

        // 6. BOTTOM-LEFT CORNER (Turning Left -> Up: 180° -> 270°)
        if (remaining < lArc) {
            val fraction = if (lArc > 0f) remaining / lArc else 0f
            val angleRad = PI.toFloat() / 2f + fraction * (PI.toFloat() / 2f)
            val centerX = xMin + r
            val centerY = yMax - r
            val x = centerX + r * cos(angleRad)
            val y = centerY + r * sin(angleRad)
            val heading = 180f + fraction * 90f
            return RobotTrackPosition(x, y, heading, legPhase, "CORNER_BOTTOM_LEFT")
        }
        remaining -= lArc

        // 7. LEFT EDGE (Bottom to Top)
        if (remaining < lLeft) {
            val fraction = if (lLeft > 0f) remaining / lLeft else 0f
            val x = xMin
            val y = (yMax - r) - fraction * lLeft
            return RobotTrackPosition(x, y, 270f, legPhase, "LEFT_EDGE")
        }
        remaining -= lLeft

        // 8. TOP-LEFT CORNER (Turning Up -> Right: 270° -> 360°/0°)
        val fraction = (if (lArc > 0f) remaining / lArc else 0f).coerceIn(0f, 1f)
        val angleRad = PI.toFloat() + fraction * (PI.toFloat() / 2f)
        val centerX = xMin + r
        val centerY = yMin + r
        val x = centerX + r * cos(angleRad)
        val y = centerY + r * sin(angleRad)
        val heading = (270f + fraction * 90f) % 360f
        return RobotTrackPosition(x, y, heading, legPhase, "CORNER_TOP_LEFT")
    }
}
