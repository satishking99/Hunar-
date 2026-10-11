package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Production-ready HTTP API Client for connecting the Android Hunar Frontend
 * to the Hunar Social & AI Robot Assistant Backend.
 */
object HunarApiClient {

    // Default emulator loopback host; can be updated dynamically
    var baseUrl: String = "http://10.0.2.2:5000/api/v1"

    // Active session authentication token
    var authToken: String? = null

    /**
     * Generic asynchronous HTTP request dispatcher
     */
    suspend fun sendRequest(
        endpoint: String,
        method: String = "GET",
        jsonBody: String? = null,
        requireAuth: Boolean = true
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = if (endpoint.startsWith("http")) endpoint else "$baseUrl$endpoint"
            val url = URL(fullUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            if (requireAuth && !authToken.isNullOrBlank()) {
                conn.setRequestProperty("Authorization", "Bearer $authToken")
            }

            if (jsonBody != null && (method == "POST" || method == "PUT" || method == "PATCH")) {
                conn.doOutput = true
                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(jsonBody)
                    writer.flush()
                }
            }

            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = BufferedReader(InputStreamReader(inputStream ?: conn.inputStream)).use { reader ->
                reader.readText()
            }

            if (responseCode in 200..299) {
                Result.success(responseText)
            } else {
                Result.failure(Exception("HTTP $responseCode: $responseText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // AUTHENTICATION APIs
    // ==========================================

    suspend fun login(identifier: String, password: String):Result<JSONObject> {
        val payload = JSONObject().apply {
            put("identifier", identifier)
            put("password", password)
        }.toString()

        return sendRequest("/auth/login", method = "POST", jsonBody = payload, requireAuth = false).mapCatching { response ->
            val json = JSONObject(response)
            val data = json.getJSONObject("data")
            val tokens = data.getJSONObject("tokens")
            authToken = tokens.getString("accessToken")
            data
        }
    }

    suspend fun checkHealth(): Result<Boolean> {
        return sendRequest("/api/health", method = "GET", requireAuth = false).mapCatching { response ->
            val json = JSONObject(response)
            json.optString("status") == "healthy"
        }
    }

    // ==========================================
    // AI ROBOT ASSISTANT BACKEND APIs
    // ==========================================

    suspend fun processRobotCommand(query: String): Result<JSONObject> {
        val payload = JSONObject().apply {
            put("query", query)
        }.toString()

        return sendRequest("/robot/command", method = "POST", jsonBody = payload).mapCatching { response ->
            JSONObject(response).getJSONObject("data")
        }
    }

    suspend fun fetchSportsVideos(query: String = ""): Result<JSONArray> {
        val endpoint = if (query.isNotBlank()) "/robot/sports-videos?query=$query" else "/robot/sports-videos"
        return sendRequest(endpoint, method = "GET").mapCatching { response ->
            JSONObject(response).getJSONArray("data")
        }
    }

    suspend fun confirmRobotAction(commandId: String, approved: Boolean): Result<JSONObject> {
        val payload = JSONObject().apply {
            put("commandId", commandId)
            put("approved", approved)
        }.toString()

        return sendRequest("/robot/confirm-action", method = "POST", jsonBody = payload).mapCatching { response ->
            JSONObject(response).getJSONObject("data")
        }
    }

    suspend fun fetchRobotSettings(): Result<JSONObject> {
        return sendRequest("/robot/settings", method = "GET").mapCatching { response ->
            JSONObject(response).getJSONObject("data")
        }
    }

    suspend fun updateRobotSettings(
        isRobotModeOn: Boolean,
        lapDurationSeconds: Float,
        speedMultiplier: Float,
        voiceCommandsEnabled: Boolean,
        messagingPerm: Boolean,
        appOpeningPerm: Boolean,
        screenNavPerm: Boolean
    ): Result<JSONObject> {
        val payload = JSONObject().apply {
            put("is_robot_mode_on", if (isRobotModeOn) 1 else 0)
            put("lap_duration_seconds", lapDurationSeconds.toDouble())
            put("speed_multiplier", speedMultiplier.toDouble())
            put("voice_commands_enabled", if (voiceCommandsEnabled) 1 else 0)
            put("messaging_permission_granted", if (messagingPerm) 1 else 0)
            put("app_opening_permission_granted", if (appOpeningPerm) 1 else 0)
            put("screen_navigation_allowed", if (screenNavPerm) 1 else 0)
        }.toString()

        return sendRequest("/robot/settings", method = "PUT", jsonBody = payload).mapCatching { response ->
            JSONObject(response).getJSONObject("data")
        }
    }

    // ==========================================
    // CREATOR ANALYTICS APIs
    // ==========================================

    suspend fun fetchCreatorDashboard(): Result<JSONObject> {
        return sendRequest("/analytics/dashboard", method = "GET").mapCatching { response ->
            JSONObject(response).getJSONObject("data")
        }
    }

    suspend fun requestWithdrawal(amount: Double, upiId: String): Result<JSONObject> {
        val payload = JSONObject().apply {
            put("amount", amount)
            put("upiId", upiId)
        }.toString()

        return sendRequest("/analytics/withdraw", method = "POST", jsonBody = payload).mapCatching { response ->
            JSONObject(response)
        }
    }

    // ==========================================
    // SAVED VIDEOS & POSTS APIs
    // ==========================================

    suspend fun savePostPrivately(postId: String): Result<Boolean> {
        return sendRequest("/saved/$postId", method = "POST").mapCatching { response ->
            JSONObject(response).optBoolean("isSaved", true)
        }
    }

    suspend fun unsavePostPrivately(postId: String): Result<Boolean> {
        return sendRequest("/saved/$postId", method = "DELETE").mapCatching { response ->
            JSONObject(response).optBoolean("isSaved", false)
        }
    }
}
