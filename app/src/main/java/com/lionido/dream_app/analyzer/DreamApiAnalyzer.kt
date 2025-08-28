package com.lionido.dream_app.analyzer

import android.content.Context
import com.lionido.dream_app.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class DreamApiAnalyzer(private val context: Context) {
    private val client = OkHttpClient()
    private val apiKey: String by lazy {
        context.getString(R.string.openrouter_api_key)
    }

    fun interpretDream(dreamText: String, callback: (String?) -> Unit) {
        val url = "https://openrouter.ai/api/v1/chat/completions"
        val prompt = """
            Поясни сон как психолог и мифолог, анализируй ключевые образы:
            $dreamText
        """.trimIndent()

        val bodyJson = JSONObject().apply {
            put("model", "openrouter/auto")
            put("messages", listOf(mapOf("role" to "user", "content" to prompt)))
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaType(),
            bodyJson.toString()
        )

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val text = try {
                    val json = JSONObject(result ?: "")
                    val choices = json.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        choices.getJSONObject(0).getJSONObject("message").getString("content")
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
                callback(text)
            }
        })
    }
}
