package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAIProvider(private val customKeyProvider: (() -> String)? = null) : AIProvider {

    override val providerName: String = "Gemini Flash (gemini-2.5-flash)"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    override suspend fun processText(mode: AiMode, text: String): Result<AiResult> =
        withContext(Dispatchers.IO) {
            val apiKey = getApiKey()
            if (apiKey.isBlank() || apiKey == "your_gemini_api_key_here") {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API Key not configured.")
                )
            }

            val systemInstruction = buildSystemPrompt(mode)

            try {
                val payload = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "$systemInstruction\n\nUser message: $text")
                                })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 350)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    return@withContext Result.failure(
                        Exception("Gemini error (${response.code}): ${response.message}")
                    )
                }

                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        val textResult = parts.getJSONObject(0).getString("text").trim()
                        return@withContext Result.success(
                            AiResult(
                                originalText = text,
                                resultText = cleanOutput(textResult),
                                mode = mode,
                                providerName = providerName
                            )
                        )
                    }
                }

                Result.failure(Exception("Empty candidate in Gemini response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getApiKey(): String {
        val userKey = customKeyProvider?.invoke()?.trim() ?: ""
        if (userKey.startsWith("AIzaSy")) return userKey

        return try {
            val key = BuildConfig.GEMINI_API_KEY.trim()
            if (key != "your_gemini_api_key_here") key else ""
        } catch (_: Throwable) {
            ""
        }
    }

    private fun buildSystemPrompt(mode: AiMode): String {
        return when (mode) {
            AiMode.GEN_Z ->
                "You are an expert Nepali Gen-Z typing assistant. Rewrite the user's input into trendy, catchy Romanized Nepali (Nepinglish) or English slang using terms like 'bro', 'dami', 'vibe', 'no cap', 'fr fr', 'lmao', 'chill', 'babbal'. Keep it under 2 sentences. Return only the transformed text."
            AiMode.SIGMA ->
                "You are a Sigma male assistant. Rewrite the user's input in a stoic, hyper-confident, focused grindset tone. Add 🗿🍷 emojis appropriately. Return only the transformed text."
            AiMode.CHAD ->
                "Rewrite the user's input as an absolute Gigachad: ultra-confident, positive, respectful yet unstoppable. Return only the transformed text."
            AiMode.FUNNY ->
                "Rewrite the user's input to make it hilarious and witty, with a funny Nepali or youth comedic twist and laughing emojis. Return only the transformed text."
            AiMode.MEME ->
                "Rewrite the input using internet meme references, funny phrasing, and 💀 / 😂 emojis. Return only the transformed text."
            AiMode.SAVAGE ->
                "Give a witty, sharp, sassy and savage comeback or roast based on the user's text. Keep it playful and not abusive. Return only the transformed text."
            AiMode.RESPECTFUL ->
                "Rewrite the user's input in polite, respectful Nepali or English, using 'Namaste', 'Hajur', 'Tapai', 'Dhanyabad'. Return only the transformed text."
            AiMode.PROFESSIONAL ->
                "Rewrite the input into polished, professional business English suitable for work communications. Return only the transformed text."
            AiMode.EMOJIFY ->
                "Enrich the user's input by adding expressive and context-relevant emojis after key words and phrases. Do not delete original words. Return only the emojified text."
            AiMode.EXPAND ->
                "Expand and elaborate on the user's input into a complete, well-expressed thought with warmth and detail. Return only the expanded text."
            AiMode.SHORTEN ->
                "Condense and shorten the user's input into a punchy, concise single line or phrase. Return only the shortened text."
            AiMode.TRANSLATE_NEPALI ->
                "Translate the input accurately into natural Devanagari Nepali script. Return ONLY the Devanagari translation."
            AiMode.TRANSLATE_ENGLISH ->
                "Translate the input (whether in Nepali Devanagari or Roman Nepinglish) into fluent natural English. Return ONLY the English translation."
            AiMode.TRANSLATE_NEPINGLISH ->
                "Translate or transliterate the input into popular Romanized Nepali (Nepinglish) as commonly used by youth in chats (e.g. 'k cha bro', 'khana khayau?'). Return ONLY the Nepinglish translation."
            AiMode.REPLY ->
                "Generate a quick, friendly, and natural chat reply to the user's message in mixed Nepali-English style. Return ONLY the reply text."
            AiMode.CAPTION ->
                "Create a catchy and viral social media caption (Instagram / TikTok) with Nepali Gen-Z vibe, emojis, and hashtags. Return only the caption."
            AiMode.GRAMMAR ->
                "Correct any typos, misspellings, or grammatical mistakes in the user's sentence (Nepali transliteration or English). Return only the corrected sentence."
        }
    }

    private fun cleanOutput(text: String): String {
        var cleaned = text.trim()
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length >= 2) {
            cleaned = cleaned.substring(1, cleaned.length - 1).trim()
        }
        return cleaned
    }
}
