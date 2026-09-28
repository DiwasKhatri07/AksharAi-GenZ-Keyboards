package com.example.ai

import com.example.engine.emoji.EmojiDictionary
import com.example.engine.nepali.NepaliTransliterationEngine
import com.example.engine.nepinglish.NepinglishEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalRuleAiProvider : AIProvider {

    override val providerName: String = "On-Device Offline Engine"

    override suspend fun processText(mode: AiMode, text: String): Result<AiResult> =
        withContext(Dispatchers.Default) {
            val input = text.trim()
            if (input.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Input text is empty"))
            }

            val transformed = when (mode) {
                AiMode.GEN_Z -> {
                    var t = input
                        .replace(Regex("(?i)\\bhi\\b|\\bhello\\b"), "k cha bro")
                        .replace(Regex("(?i)\\bgood\\b|\\bnice\\b"), "dami")
                        .replace(Regex("(?i)\\breally\\b|\\bvery\\b"), "ekdam")
                        .replace(Regex("(?i)\\bawesome\\b|\\bcool\\b"), "babal")
                        .replace(Regex("(?i)\\byes\\b"), "sahii ho fr")
                        .replace(Regex("(?i)\\bfriend\\b"), "sathi / bro")
                    if (!t.contains("bro", ignoreCase = true) && !t.contains("fr")) {
                        t = "$t fr no cap bro 😭🔥"
                    } else {
                        t = "$t 🔥"
                    }
                    t
                }

                AiMode.SIGMA -> {
                    "Stay focused on the grind. $input. No distractions. 🗿🍷"
                }

                AiMode.CHAD -> {
                    "$input. Absolutely based and unstoppable. 💪👑"
                }

                AiMode.FUNNY -> {
                    "$input 😂 bro thinks he is the main character 💀"
                }

                AiMode.MEME -> {
                    "bro really said: \"$input\" 💀😭 tell me you didn't fr"
                }

                AiMode.SAVAGE -> {
                    "Nice story, $input... did you get that from WhatsApp University? ⚡😂"
                }

                AiMode.RESPECTFUL -> {
                    "नमस्ते, $input । यहाँको दिन शुभ रहोस् । 🙏"
                }

                AiMode.PROFESSIONAL -> {
                    "Regarding your note: $input. Please let me know how you would like to proceed. Best regards."
                }

                AiMode.EMOJIFY -> {
                    val words = input.split(" ")
                    val sb = StringBuilder()
                    for (w in words) {
                        sb.append(w).append(" ")
                        val emojis = EmojiDictionary.getEmojisForWord(w)
                        if (emojis.isNotEmpty()) {
                            sb.append(emojis.first()).append(" ")
                        }
                    }
                    val res = sb.toString().trim()
                    if (res == input) "$input ✨❤️🔥" else res
                }

                AiMode.EXPAND -> {
                    "$input. I wanted to make sure we're on the same page and discuss this thoroughly when you're free."
                }

                AiMode.SHORTEN -> {
                    val words = input.split(" ")
                    if (words.size > 5) {
                        words.take(4).joinToString(" ") + "..."
                    } else {
                        input
                    }
                }

                AiMode.TRANSLATE_NEPALI -> {
                    val words = input.split(" ")
                    val devWords = words.map { word ->
                        NepaliTransliterationEngine.transliterateWord(word)
                    }
                    devWords.joinToString(" ")
                }

                AiMode.TRANSLATE_ENGLISH -> {
                    val norm = NepinglishEngine.normalizeToken(input)
                    when {
                        input.contains("k cha", ignoreCase = true) || input.contains("k xa", ignoreCase = true) -> "How are you? / What's up?"
                        input.contains("khana khayau", ignoreCase = true) -> "Did you have food?"
                        input.contains("namaste", ignoreCase = true) -> "Greetings / Hello!"
                        input.contains("ramro cha", ignoreCase = true) -> "It is good / very nice."
                        input.contains("dhanyabad", ignoreCase = true) -> "Thank you very much!"
                        input.contains("timi", ignoreCase = true) -> "You"
                        input.contains("ma ghar jadai", ignoreCase = true) -> "I am going home."
                        else -> "Translation: $norm"
                    }
                }

                AiMode.TRANSLATE_NEPINGLISH -> {
                    val words = input.split(" ")
                    words.joinToString(" ") { NepinglishEngine.normalizeToken(it) }
                }

                AiMode.REPLY -> {
                    when {
                        input.contains("k cha", ignoreCase = true) || input.contains("kasto", ignoreCase = true) -> "Sab thik cha bro, timro k cha?"
                        input.contains("khana", ignoreCase = true) -> "Aah khaye bro, timle khayau?"
                        input.contains("kaha", ignoreCase = true) -> "Ma ta ghar mai chu bro, k vayo ra?"
                        else -> "Sahii ho bro! La pachi kura garam la 👍"
                    }
                }

                AiMode.CAPTION -> {
                    "✨ $input ✨ • Vibe check passed 📸🔥 #NepalVibes #GenZ #ChillVibe #DamiLife 🇳🇵"
                }

                AiMode.GRAMMAR -> {
                    val fixed = NepinglishEngine.normalizeToken(input)
                    fixed.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }

            Result.success(
                AiResult(
                    originalText = input,
                    resultText = transformed,
                    mode = mode,
                    providerName = providerName
                )
            )
        }
}
