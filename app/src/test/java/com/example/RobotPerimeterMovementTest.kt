package com.example

import com.example.ui.robot.RobotMovementCalculator
import org.junit.Assert.*
import org.junit.Test

class RobotPerimeterMovementTest {

    private val screenWidth = 412f
    private val screenHeight = 892f
    private val robotSize = 42f
    private val insetLeft = 18f
    private val insetRight = 18f
    private val insetTop = 58f
    private val insetBottom = 96f
    private val cornerRadius = 24f

    @Test
    fun testStartAtTopLeft_headingRight() {
        val pos = RobotMovementCalculator.calculatePosition(
            elapsedMillis = 0L,
            lapDurationSeconds = 120f,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight,
            robotSizeDp = robotSize,
            insetLeftDp = insetLeft,
            insetRightDp = insetRight,
            insetTopDp = insetTop,
            insetBottomDp = insetBottom,
            cornerRadiusDp = cornerRadius
        )

        assertEquals("TOP_EDGE", pos.segmentName)
        assertEquals(0f, pos.headingAngle, 0.01f)
        assertEquals(insetTop, pos.yDp, 0.01f)
        assertTrue(pos.xDp >= insetLeft)
    }

    @Test
    fun testVisitsAllFourCornersInClockwiseOrder() {
        val totalMs = 120_000L
        val visitedSegments = mutableSetOf<String>()

        // Sample every 500ms across the full 120s lap
        for (ms in 0L..totalMs step 500L) {
            val pos = RobotMovementCalculator.calculatePosition(
                elapsedMillis = ms,
                lapDurationSeconds = 120f,
                screenWidthDp = screenWidth,
                screenHeightDp = screenHeight,
                robotSizeDp = robotSize,
                insetLeftDp = insetLeft,
                insetRightDp = insetRight,
                insetTopDp = insetTop,
                insetBottomDp = insetBottom,
                cornerRadiusDp = cornerRadius
            )
            visitedSegments.add(pos.segmentName)
        }

        assertTrue("Must visit TOP_EDGE", visitedSegments.contains("TOP_EDGE"))
        assertTrue("Must visit CORNER_TOP_RIGHT", visitedSegments.contains("CORNER_TOP_RIGHT"))
        assertTrue("Must visit RIGHT_EDGE", visitedSegments.contains("RIGHT_EDGE"))
        assertTrue("Must visit CORNER_BOTTOM_RIGHT", visitedSegments.contains("CORNER_BOTTOM_RIGHT"))
        assertTrue("Must visit BOTTOM_EDGE", visitedSegments.contains("BOTTOM_EDGE"))
        assertTrue("Must visit CORNER_BOTTOM_LEFT", visitedSegments.contains("CORNER_BOTTOM_LEFT"))
        assertTrue("Must visit LEFT_EDGE", visitedSegments.contains("LEFT_EDGE"))
        assertTrue("Must visit CORNER_TOP_LEFT", visitedSegments.contains("CORNER_TOP_LEFT"))
    }

    @Test
    fun testExact120SecondsDuration_returnsToExactStart() {
        val posStart = RobotMovementCalculator.calculatePosition(
            elapsedMillis = 0L,
            lapDurationSeconds = 120f,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight
        )

        val posLap1 = RobotMovementCalculator.calculatePosition(
            elapsedMillis = 120_000L,
            lapDurationSeconds = 120f,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight
        )

        val posLap2 = RobotMovementCalculator.calculatePosition(
            elapsedMillis = 240_000L,
            lapDurationSeconds = 120f,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight
        )

        val posLap5 = RobotMovementCalculator.calculatePosition(
            elapsedMillis = 600_000L,
            lapDurationSeconds = 120f,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight
        )

        assertEquals(posStart.xDp, posLap1.xDp, 0.05f)
        assertEquals(posStart.yDp, posLap1.yDp, 0.05f)
        assertEquals(posStart.headingAngle, posLap1.headingAngle, 0.05f)

        assertEquals(posStart.xDp, posLap2.xDp, 0.05f)
        assertEquals(posStart.yDp, posLap2.yDp, 0.05f)

        assertEquals(posStart.xDp, posLap5.xDp, 0.05f)
        assertEquals(posStart.yDp, posLap5.yDp, 0.05f)
    }

    @Test
    fun testNaturalCornerTurns_headingAnglesAreSmoothAndBounded() {
        val totalMs = 120_000L
        var prevHeading = 0f

        for (ms in 0L..totalMs step 200L) {
            val pos = RobotMovementCalculator.calculatePosition(
                elapsedMillis = ms,
                lapDurationSeconds = 120f,
                screenWidthDp = screenWidth,
                screenHeightDp = screenHeight
            )

            assertTrue("Heading must be within [0, 360)", pos.headingAngle in 0f..360f)

            when (pos.segmentName) {
                "TOP_EDGE" -> assertEquals(0f, pos.headingAngle, 0.01f)
                "CORNER_TOP_RIGHT" -> assertTrue(pos.headingAngle in 0f..90f)
                "RIGHT_EDGE" -> assertEquals(90f, pos.headingAngle, 0.01f)
                "CORNER_BOTTOM_RIGHT" -> assertTrue(pos.headingAngle in 90f..180f)
                "BOTTOM_EDGE" -> assertEquals(180f, pos.headingAngle, 0.01f)
                "CORNER_BOTTOM_LEFT" -> assertTrue(pos.headingAngle in 180f..270f)
                "LEFT_EDGE" -> assertEquals(270f, pos.headingAngle, 0.01f)
                "CORNER_TOP_LEFT" -> assertTrue(pos.headingAngle in 270f..360f || pos.headingAngle == 0f)
            }
            prevHeading = pos.headingAngle
        }
    }

    @Test
    fun testBoundarySafety_robotNeverExceedsScreenMargins() {
        val testScreens = listOf(
            Pair(360f, 640f),
            Pair(412f, 892f),
            Pair(600f, 1024f),
            Pair(800f, 1280f)
        )

        for ((w, h) in testScreens) {
            val xMin = insetLeft
            val xMax = w - insetRight - robotSize
            val yMin = insetTop
            val yMax = h - insetBottom - robotSize

            for (progress in listOf(0f, 0.1f, 0.25f, 0.35f, 0.5f, 0.65f, 0.75f, 0.85f, 0.99f)) {
                val pos = RobotMovementCalculator.calculatePositionFromProgress(
                    progress = progress,
                    screenWidthDp = w,
                    screenHeightDp = h,
                    robotSizeDp = robotSize,
                    insetLeftDp = insetLeft,
                    insetRightDp = insetRight,
                    insetTopDp = insetTop,
                    insetBottomDp = insetBottom,
                    cornerRadiusDp = cornerRadius
                )

                assertTrue("xDp (${pos.xDp}) must be >= xMin ($xMin)", pos.xDp >= xMin - 0.01f)
                assertTrue("xDp (${pos.xDp}) must be <= xMax ($xMax)", pos.xDp <= xMax + 0.01f)
                assertTrue("yDp (${pos.yDp}) must be >= yMin ($yMin)", pos.yDp >= yMin - 0.01f)
                assertTrue("yDp (${pos.yDp}) must be <= yMax ($yMax)", pos.yDp <= yMax + 0.01f)
            }
        }
    }

    @Test
    fun testWalkingLegCycle_oscillatesContinuously() {
        val pos1 = RobotMovementCalculator.calculatePosition(0L, screenWidthDp = 400f, screenHeightDp = 800f)
        val pos2 = RobotMovementCalculator.calculatePosition(225L, screenWidthDp = 400f, screenHeightDp = 800f)

        assertNotEquals(pos1.legPhase, pos2.legPhase)
        assertTrue(pos1.legPhase in 0f..(2 * Math.PI.toFloat() + 0.01f))
        assertTrue(pos2.legPhase in 0f..(2 * Math.PI.toFloat() + 0.01f))
    }
}
