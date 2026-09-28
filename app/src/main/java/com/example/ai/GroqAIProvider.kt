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

class GroqAIProvider(private val customKeyProvider: (() -> String)? = null) : AIProvider {

    override val providerName: String = "Groq (llama-3.1-8b-instant)"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    override suspend fun processText(mode: AiMode, text: String): Result<AiResult> =
        withContext(Dispatchers.IO) {
            val apiKey = getApiKey()
            if (apiKey.isBlank() || apiKey == "your_groq_api_key_here") {
                return@withContext Result.failure(
                    IllegalStateException("Groq API Key not configured. Configure GROQ_API_KEY in Secrets or use Local Engine.")
                )
            }

            val systemPrompt = buildSystemPrompt(mode)

            try {
                val payload = JSONObject().apply {
                    put("model", "llama-3.1-8b-instant")
                    val messages = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", text)
                        })
                    }
                    put("messages", messages)
                    put("temperature", 0.7)
                    put("max_tokens", 400)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    return@withContext Result.failure(
                        Exception("Groq API error (${response.code}): ${response.message}")
                    )
                }

                val jsonResponse = JSONObject(responseBody)
                val choices = jsonResponse.getJSONArray("choices")
                if (choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val content = message.getString("content").trim()
                    return@withContext Result.success(
                        AiResult(
                            originalText = text,
                            resultText = cleanOutput(content),
                            mode = mode,
                            providerName = providerName
                        )
                    )
                }

                Result.failure(Exception("Empty choices in Groq response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getApiKey(): String {
        val userProvided = customKeyProvider?.invoke()?.trim() ?: ""
        if (userProvided.isNotBlank()) return userProvided

        return try {
            BuildConfig.GROQ_API_KEY.trim()
        } catch (e: Throwable) {
            ""
        }
    }

    private fun buildSystemPrompt(mode: AiMode): String {
        return when (mode) {
            AiMode.GEN_Z ->
                "You are a Gen-Z Nepali assistant. Rewrite the user's input into cool Romanized Nepali (Nepinglish) or English slang using expressions like 'bro', 'dami', 'vibe', 'no cap', 'fr fr', 'lmao', 'chill', 'babbal'. Keep it catchy and under 2 sentences. Return only the transformed text."
            AiMode.SIGMA ->
                "You are a Sigma male assistant. Rewrite the user's input in a stoic, hyper-confident, focused grindset tone. Add 🗿🍷 emojis appropriately. Return only the transformed text."
            AiMode.CHAD ->
                "Rewrite the user's input as an absolute Gigachad: ultra-confident, positive, respectful yet unstoppable. Return only the transformed text."
            AiMode.FUNNY ->
                "Rewrite the user's input to make it hilarious and witty, with a funny Nepali or international comedic spin and laughing emojis. Return only the transformed text."
            AiMode.MEME ->
                "Rewrite the input using internet meme references, funny phrasing, and 💀 / 😂 emojis. Return only the transformed text."
            AiMode.SAVAGE ->
                "Give a witty, sharp, sassy and savage comeback or roast based on the user's text. Keep it playful and not genuinely abusive. Return only the transformed text."
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
