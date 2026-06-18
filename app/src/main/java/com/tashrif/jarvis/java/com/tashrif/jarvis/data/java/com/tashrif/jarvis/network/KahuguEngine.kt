package com.tashrif.jarvis.network

import com.tashrif.jarvis.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class KahuguEngine {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        Wewe ni Jarvis, msaidizi binafsi wa kidijitali wa mtumiaji.
        Ongea kwa lugha aliyotumia mtumiaji (Kiswahili au Kiingereza).
        Kuwa mfupi, wa moja kwa moja, na wa kitaalamu.
    """.trimIndent()

    private val modelName = "gemini-2.0-flash"

    suspend fun ask(
        userMessage: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentsArray = JSONArray()
            for ((role, content) in conversationHistory) {
                val geminiRole = if (role == "assistant") "model" else "user"
                contentsArray.put(JSONObject().apply {
                    put("role", geminiRole)
                    put("parts", JSONArray().put(JSONObject().put("text", content)))
                })
            }
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            })

            val bodyJson = JSONObject().apply {
                put("system_instruction",
                    JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
                put("contents", contentsArray)
            }

            val body = bodyJson.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/" +
                "$modelName:generateContent?key=${BuildConfig.GEMINI_API_KEY}"

            val request = Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("Gemini error ${response.code}: $responseBody"))
                }
                val json = JSONObject(responseBody)
                val parts = json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                val text = StringBuilder()
                for (i in 0 until parts.length()) {
                    text.append(parts.getJSONObject(i).optString("text"))
                }
                Result.success(text.toString())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
