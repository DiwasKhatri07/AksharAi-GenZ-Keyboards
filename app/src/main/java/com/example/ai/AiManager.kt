package com.example.ai

import android.content.Context
import com.example.data.local.AiHistoryEntity
import com.example.data.local.AppDatabase
import com.example.data.preferences.KeyboardPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiManager(private val context: Context) {

    private val preferences = KeyboardPreferences(context)
    private val groqProvider = GroqAIProvider(customKeyProvider = { preferences.customApiKey })
    private val geminiProvider = GeminiAIProvider(customKeyProvider = { preferences.customApiKey })
    private val localProvider = LocalRuleAiProvider()
    private val database = AppDatabase.getInstance(context)

    suspend fun executeAi(mode: AiMode, text: String): Result<AiResult> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("No text to process"))
        }

        // Privacy guard: Never send sensitive looking data
        if (isSensitiveText(trimmed)) {
            return@withContext Result.failure(
                SecurityException("For your privacy, processing numbers, OTPs, or passwords with AI is disabled.")
            )
        }

        val customKey = preferences.customApiKey.trim()

        val cloudResult = try {
            if (customKey.startsWith("AIzaSy")) {
                // User provided a Google Gemini API Key
                val res = geminiProvider.processText(mode, trimmed)
                if (res.isSuccess) res else groqProvider.processText(mode, trimmed)
            } else {
                // Try Groq first, then Gemini
                val res = groqProvider.processText(mode, trimmed)
                if (res.isSuccess) res else geminiProvider.processText(mode, trimmed)
            }
        } catch (_: Throwable) {
            null
        }

        // Final result: if cloud succeeded use it, else seamlessly use on-device rule engine!
        val finalResult = if (cloudResult != null && cloudResult.isSuccess) {
            cloudResult
        } else {
            try {
                localProvider.processText(mode, trimmed)
            } catch (e: Throwable) {
                Result.success(
                    AiResult(
                        originalText = trimmed,
                        resultText = "$trimmed 🔥",
                        mode = mode,
                        providerName = "On-Device Engine"
                    )
                )
            }
        }

        // Save to AI History if user enabled it in settings
        if (finalResult.isSuccess && preferences.aiHistoryEnabled) {
            finalResult.getOrNull()?.let { result ->
                try {
                    database.aiHistoryDao().insert(
                        AiHistoryEntity(
                            originalText = result.originalText,
                            mode = result.mode.displayName,
                            resultText = result.resultText
                        )
                    )
                } catch (_: Throwable) {}
            }
        }

        finalResult
    }

    private fun isSensitiveText(text: String): Boolean {
        if (text.length in 4..6 && text.all { it.isDigit() }) return true
        if (text.replace(" ", "").length in 13..19 && text.replace(" ", "").all { it.isDigit() }) return true
        return false
    }
}
