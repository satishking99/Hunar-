package com.example.ui.robot

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Realistic 3D Cute Robot Canvas with articulated walking legs, arms,
 * animated blinking LED visor, glowing antenna light, and realistic drop shadow.
 */
@Composable
fun CuteRobot3DCanvas(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    headingAngle: Float = 0f, // Direction of motion in degrees (0 = right, 90 = down, 180 = left, 270 = up)
    walkPhase: Float = 0f, // Walking leg oscillation phase
    isRobotModeOn: Boolean = false,
    mood: RobotMood = RobotMood.WALKING
) {
    // Subtle continuous idle breathing and antenna glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "robot_glow_transition")
    val antennaPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "antenna_pulse"
    )

    // Eye blink cycle
    val eyeBlink by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                1.0f at 0
                1.0f at 3100
                0.05f at 3250
                1.0f at 3400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "eye_blink"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val w = this.size.width
        val h = this.size.height
        val centerX = w / 2f
        val centerY = h / 2f

        // Draw soft contact shadow on screen surface (underneath robot)
        val shadowW = w * 0.7f
        val shadowH = h * 0.22f
        val shadowY = h * 0.82f
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.35f),
                    Color.Black.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(centerX, shadowY),
                radius = shadowW / 2f
            ),
            topLeft = Offset(centerX - shadowW / 2f, shadowY - shadowH / 2f),
            size = Size(shadowW, shadowH)
        )

        // Rotate canvas towards direction of heading with a slight tilt towards screen
        rotate(degrees = headingAngle, pivot = Offset(centerX, centerY)) {
            drawRealisticRobotBody(
                w = w,
                h = h,
                walkPhase = walkPhase,
                antennaPulse = antennaPulse,
                eyeBlink = eyeBlink,
                isRobotModeOn = isRobotModeOn,
                mood = mood
            )
        }
    }
}

private fun DrawScope.drawRealisticRobotBody(
    w: Float,
    h: Float,
    walkPhase: Float,
    antennaPulse: Float,
    eyeBlink: Float,
    isRobotModeOn: Boolean,
    mood: RobotMood
) {
    val centerX = w / 2f
    val centerY = h / 2f

    // Walking leg displacement (sinusoidal swing)
    val legSwingLeft = sin(walkPhase.toDouble()).toFloat() * (h * 0.12f)
    val legSwingRight = -sin(walkPhase.toDouble()).toFloat() * (h * 0.12f)
    val legLiftLeft = if (legSwingLeft > 0) (legSwingLeft * 0.4f) else 0f
    val legLiftRight = if (legSwingRight > 0) (legSwingRight * 0.4f) else 0f

    // Arm swing (counter-balancing legs)
    val armSwingLeft = -legSwingLeft * 0.7f
    val armSwingRight = -legSwingRight * 0.7f

    // 1. LEGS & FEET (Articulated 3D boots)
    val legW = w * 0.15f
    val legH = h * 0.28f
    val leftLegX = centerX - w * 0.22f
    val rightLegX = centerX + w * 0.07f
    val legBaseY = centerY + h * 0.14f

    // Left Leg
    drawLegWithBoot(
        x = leftLegX,
        y = legBaseY - legLiftLeft,
        width = legW,
        height = legH,
        swingOffset = legSwingLeft,
        isLeft = true
    )

    // Right Leg
    drawLegWithBoot(
        x = rightLegX,
        y = legBaseY - legLiftRight,
        width = legW,
        height = legH,
        swingOffset = legSwingRight,
        isLeft = false
    )

    // 2. ARMS (Left & Right cute mechanical rounded joints)
    val armW = w * 0.12f
    val armH = h * 0.24f
    val armY = centerY - h * 0.05f

    // Left Arm
    drawArmWithJoint(
        x = centerX - w * 0.38f,
        y = armY + armSwingLeft,
        width = armW,
        height = armH,
        isLeft = true
    )

    // Right Arm
    drawArmWithJoint(
        x = centerX + w * 0.26f,
        y = armY + armSwingRight,
        width = armW,
        height = armH,
        isLeft = false
    )

    // 3. TORSO (Rounded 3D capsule with metallic shading and core badge)
    val torsoW = w * 0.52f
    val torsoH = h * 0.40f
    val torsoX = centerX - torsoW / 2f
    val torsoY = centerY - h * 0.06f

    // Torso 3D Metallic Gradient
    val torsoBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE2E8F0), // Titanium highlight
            Color(0xFFCBD5E1),
            Color(0xFF94A3B8), // Mid shadow
            Color(0xFF64748B)  // Deep shadow edge
        ),
        start = Offset(torsoX, torsoY),
        end = Offset(torsoX + torsoW, torsoY + torsoH)
    )

    drawRoundRect(
        brush = torsoBrush,
        topLeft = Offset(torsoX, torsoY),
        size = Size(torsoW, torsoH),
        cornerRadius = CornerRadius(torsoW * 0.32f, torsoW * 0.32f)
    )

    // Torso border highlight
    drawRoundRect(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(torsoX, torsoY),
        size = Size(torsoW, torsoH),
        cornerRadius = CornerRadius(torsoW * 0.32f, torsoW * 0.32f),
        style = Stroke(width = 1.2f)
    )

    // Core Power Arc / Hunar Badge on Chest
    val coreColor = if (isRobotModeOn) Color(0xFF10B981) else Color(0xFFFF9933) // Emerald when Mode ON, Hunar Saffron otherwise
    val coreGlow = Brush.radialGradient(
        colors = listOf(
            coreColor.copy(alpha = 0.9f),
            coreColor.copy(alpha = 0.4f),
            Color.Transparent
        ),
        center = Offset(centerX, torsoY + torsoH * 0.55f),
        radius = torsoW * 0.22f
    )
    drawCircle(
        brush = coreGlow,
        radius = torsoW * 0.18f,
        center = Offset(centerX, torsoY + torsoH * 0.55f)
    )
    drawCircle(
        color = coreColor,
        radius = torsoW * 0.08f,
        center = Offset(centerX, torsoY + torsoH * 0.55f)
    )

    // 4. NECK COLLAR
    val neckW = torsoW * 0.45f
    val neckH = h * 0.06f
    drawRoundRect(
        color = Color(0xFF475569),
        topLeft = Offset(centerX - neckW / 2f, torsoY - neckH * 0.7f),
        size = Size(neckW, neckH),
        cornerRadius = CornerRadius(neckH / 2f, neckH / 2f)
    )

    // 5. HEAD & VISOR (Cute curved dome with digital eyes)
    val headW = w * 0.60f
    val headH = h * 0.42f
    val headX = centerX - headW / 2f
    val headY = centerY - h * 0.46f

    // 3D Head Ceramic Shell
    val headBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFE2E8F0),
            Color(0xFFCBD5E1),
            Color(0xFF94A3B8)
        ),
        start = Offset(headX, headY),
        end = Offset(headX + headW, headY + headH)
    )

    drawRoundRect(
        brush = headBrush,
        topLeft = Offset(headX, headY),
        size = Size(headW, headH),
        cornerRadius = CornerRadius(headW * 0.42f, headW * 0.42f)
    )

    // Soft head rim
    drawRoundRect(
        color = Color.White.copy(alpha = 0.6f),
        topLeft = Offset(headX, headY),
        size = Size(headW, headH),
        cornerRadius = CornerRadius(headW * 0.42f, headW * 0.42f),
        style = Stroke(width = 1.2f)
    )

    // 6. VISOR / DIGITAL FACE SCREEN (Curved glossy black glass)
    val visorW = headW * 0.78f
    val visorH = headH * 0.55f
    val visorX = centerX - visorW / 2f
    val visorY = headY + headH * 0.28f

    drawRoundRect(
        color = Color(0xFF0F172A), // Deep obsidian glass
        topLeft = Offset(visorX, visorY),
        size = Size(visorW, visorH),
        cornerRadius = CornerRadius(visorW * 0.28f, visorW * 0.28f)
    )

    // Glass reflection glare on top of visor
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            startY = visorY,
            endY = visorY + visorH * 0.4f
        ),
        topLeft = Offset(visorX + 2f, visorY + 2f),
        size = Size(visorW - 4f, visorH * 0.4f),
        cornerRadius = CornerRadius(visorW * 0.2f, visorW * 0.2f)
    )

    // 7. DIGITAL LED EYES (Cyan expressive glowing eyes that blink)
    val eyeColor = when (mood) {
        RobotMood.ALERT -> Color(0xFFF43F5E)
        RobotMood.HAPPY -> Color(0xFF38BDF8)
        else -> if (isRobotModeOn) Color(0xFF34D399) else Color(0xFF38BDF8)
    }

    val eyeSpacing = visorW * 0.26f
    val eyeRadiusX = visorW * 0.12f
    val eyeRadiusY = (visorH * 0.22f) * eyeBlink.coerceAtLeast(0.08f)
    val eyeCenterY = visorY + visorH * 0.52f

    // Left Eye
    drawOval(
        color = eyeColor,
        topLeft = Offset(centerX - eyeSpacing - eyeRadiusX, eyeCenterY - eyeRadiusY),
        size = Size(eyeRadiusX * 2f, eyeRadiusY * 2f)
    )
    // Left Eye Soft Glow
    drawCircle(
        color = eyeColor.copy(alpha = 0.35f),
        radius = eyeRadiusX * 1.5f,
        center = Offset(centerX - eyeSpacing, eyeCenterY)
    )

    // Right Eye
    drawOval(
        color = eyeColor,
        topLeft = Offset(centerX + eyeSpacing - eyeRadiusX, eyeCenterY - eyeRadiusY),
        size = Size(eyeRadiusX * 2f, eyeRadiusY * 2f)
    )
    // Right Eye Soft Glow
    drawCircle(
        color = eyeColor.copy(alpha = 0.35f),
        radius = eyeRadiusX * 1.5f,
        center = Offset(centerX + eyeSpacing, eyeCenterY)
    )

    // 8. ANTENNA & GLOWING LED BEACON
    val antennaStemW = w * 0.04f
    val antennaStemH = h * 0.15f
    val antennaStemX = centerX - antennaStemW / 2f
    val antennaStemY = headY - antennaStemH

    drawRoundRect(
        color = Color(0xFF64748B),
        topLeft = Offset(antennaStemX, antennaStemY),
        size = Size(antennaStemW, antennaStemH),
        cornerRadius = CornerRadius(antennaStemW / 2f, antennaStemW / 2f)
    )

    // Antenna Glowing Tip (Sphere with pulsating corona)
    val beaconRadius = w * 0.065f
    val beaconCenter = Offset(centerX, antennaStemY)
    val beaconColor = if (isRobotModeOn) Color(0xFF10B981) else Color(0xFF0EA5E9)

    // Corona aura
    drawCircle(
        color = beaconColor.copy(alpha = 0.35f * antennaPulse),
        radius = beaconRadius * (1.2f + 0.5f * antennaPulse),
        center = beaconCenter
    )
    // Core sphere
    drawCircle(
        color = beaconColor,
        radius = beaconRadius,
        center = beaconCenter
    )
    // Core highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = beaconRadius * 0.35f,
        center = Offset(beaconCenter.x - beaconRadius * 0.3f, beaconCenter.y - beaconRadius * 0.3f)
    )
}

private fun DrawScope.drawLegWithBoot(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    swingOffset: Float,
    isLeft: Boolean
) {
    // Upper leg thigh rod
    val legBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF64748B), Color(0xFF334155)),
        startY = y,
        endY = y + height * 0.6f
    )
    drawRoundRect(
        brush = legBrush,
        topLeft = Offset(x + swingOffset * 0.2f, y),
        size = Size(width * 0.7f, height * 0.6f),
        cornerRadius = CornerRadius(width * 0.35f, width * 0.35f)
    )

    // Rounded Boot / Foot pad
    val bootW = width * 1.35f
    val bootH = height * 0.45f
    val bootX = x - width * 0.25f + swingOffset
    val bootY = y + height * 0.55f

    val bootBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFCBD5E1),
            Color(0xFF64748B),
            Color(0xFF1E293B)
        ),
        start = Offset(bootX, bootY),
        end = Offset(bootX + bootW, bootY + bootH)
    )

    drawRoundRect(
        brush = bootBrush,
        topLeft = Offset(bootX, bootY),
        size = Size(bootW, bootH),
        cornerRadius = CornerRadius(bootH * 0.45f, bootH * 0.45f)
    )

    // Boot sole grip line
    drawLine(
        color = Color(0xFF0F172A),
        start = Offset(bootX + 2f, bootY + bootH - 1.5f),
        end = Offset(bootX + bootW - 2f, bootY + bootH - 1.5f),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawArmWithJoint(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    isLeft: Boolean
) {
    // Arm segment with soft metallic gradient
    val armBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE2E8F0),
            Color(0xFF94A3B8),
            Color(0xFF475569)
        ),
        startY = y,
        endY = y + height
    )

    drawRoundRect(
        brush = armBrush,
        topLeft = Offset(x, y),
        size = Size(width, height * 0.75f),
        cornerRadius = CornerRadius(width * 0.45f, width * 0.45f)
    )

    // Rounded robotic hand / gripper ball
    val handRadius = width * 0.65f
    val handCenter = Offset(x + width / 2f, y + height * 0.75f)

    drawCircle(
        color = Color(0xFF475569),
        radius = handRadius,
        center = handCenter
    )
    drawCircle(
        color = Color(0xFF94A3B8),
        radius = handRadius * 0.5f,
        center = handCenter
    )
}
