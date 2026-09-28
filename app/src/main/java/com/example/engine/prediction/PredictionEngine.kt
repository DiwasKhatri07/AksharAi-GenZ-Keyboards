package com.example.engine.prediction

import com.example.data.preferences.LanguageMode
import com.example.engine.emoji.EmojiDictionary
import com.example.engine.nepali.NepaliTransliterationEngine
import com.example.engine.nepinglish.GenZSlangDictionary
import com.example.engine.nepinglish.NepinglishEngine
import java.util.Locale

data class SuggestionItem(
    val displayText: String,
    val commitText: String,
    val isAutocorrect: Boolean = false,
    val secondaryHint: String? = null
)

object PredictionEngine {

    // Common English dictionary words
    private val englishCommonWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us"
    )

    fun getSuggestions(
        composing: String,
        previousWord: String,
        mode: LanguageMode,
        userCustomWords: List<String> = emptyList()
    ): List<SuggestionItem> {
        val trimmed = composing.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        // 1. If currently no composing word, offer next-word bigram predictions or quick emojis
        if (lower.isEmpty()) {
            if (previousWord.isNotEmpty()) {
                // Check if previous word has emoji context
                val emojis = EmojiDictionary.getEmojisForWord(previousWord)
                if (emojis.isNotEmpty()) {
                    return emojis.take(3).map {
                        SuggestionItem(displayText = "$it $previousWord", commitText = "$it ")
                    }
                }

                val nextWords = NepinglishEngine.predictNextWords(previousWord)
                if (nextWords.isNotEmpty()) {
                    return nextWords.take(3).map {
                        SuggestionItem(displayText = it, commitText = "$it ")
                    }
                }
            }
            // Default quick starts with popular Nepali emojis
            return when (mode) {
                LanguageMode.NEPALI -> listOf(
                    SuggestionItem("नमस्ते 🙏", "नमस्ते "),
                    SuggestionItem("के छ", "के छ "),
                    SuggestionItem("नेपाल 🇳🇵", "नेपाल ")
                )
                LanguageMode.NEPINGLISH -> listOf(
                    SuggestionItem("k cha bro? 🔥", "k cha bro "),
                    SuggestionItem("namaste 🙏", "namaste "),
                    SuggestionItem("dami ✨", "dami ")
                )
                LanguageMode.ENGLISH -> listOf(
                    SuggestionItem("Hey 👋", "Hey "),
                    SuggestionItem("What's up", "What's up "),
                    SuggestionItem("Love it ❤️", "Love it ")
                )
            }
        }

        val suggestions = mutableListOf<SuggestionItem>()

        // 2. Check user custom dictionary first
        userCustomWords.filter { it.lowercase().startsWith(lower) }.take(2).forEach {
            suggestions.add(SuggestionItem(it, it, secondaryHint = "Saved"))
        }

        // 3. Context Emojis check - if user typed something that matches emojis (e.g. "maya", "nepal", "haha")
        val wordEmojis = EmojiDictionary.getEmojisForWord(lower)

        // 4. Mode-specific prediction
        when (mode) {
            LanguageMode.NEPALI -> {
                val devCandidates = NepaliTransliterationEngine.getTransliterationCandidates(lower)
                devCandidates.forEach {
                    if (suggestions.none { s -> s.commitText == it }) {
                        suggestions.add(SuggestionItem(displayText = it, commitText = it))
                    }
                }
                if (wordEmojis.isNotEmpty() && suggestions.size < 3) {
                    val em = wordEmojis.first()
                    suggestions.add(SuggestionItem(displayText = "$trimmed $em", commitText = "$trimmed $em"))
                }
            }

            LanguageMode.NEPINGLISH -> {
                val normalized = NepinglishEngine.normalizeToken(lower)
                val devanagari = NepaliTransliterationEngine.transliterateWord(lower)

                // 1st slot: Roman typed or normalized form
                if (normalized != lower) {
                    suggestions.add(SuggestionItem(displayText = normalized, commitText = normalized, isAutocorrect = true, secondaryHint = "Standard"))
                } else {
                    suggestions.add(SuggestionItem(displayText = trimmed, commitText = trimmed))
                }

                // 2nd slot: The Devanagari transliteration candidate (e.g. "timi" -> "तिमी", "namaste" -> "नमस्ते")
                if (devanagari.isNotEmpty() && devanagari != lower) {
                    suggestions.add(SuggestionItem(displayText = devanagari, commitText = devanagari, secondaryHint = "नेपाली"))
                }

                // 3rd slot: Emoji match if available, or slang completion
                if (wordEmojis.isNotEmpty()) {
                    val em = wordEmojis.first()
                    suggestions.add(SuggestionItem(displayText = "$em ${wordEmojis.drop(1).take(1).firstOrNull() ?: ""}".trim(), commitText = "$trimmed $em", secondaryHint = "Emoji"))
                } else {
                    val completions = NepinglishEngine.getCompletions(lower)
                    for (comp in completions) {
                        if (suggestions.none { it.commitText == comp }) {
                            suggestions.add(SuggestionItem(displayText = comp, commitText = comp))
                            break
                        }
                    }
                }
            }

            LanguageMode.ENGLISH -> {
                // Exact typed
                suggestions.add(SuggestionItem(displayText = trimmed, commitText = trimmed))

                if (wordEmojis.isNotEmpty()) {
                    val em = wordEmojis.first()
                    suggestions.add(SuggestionItem(displayText = "$trimmed $em", commitText = "$trimmed $em", secondaryHint = "Emoji"))
                }

                // English dictionary prefix matches
                val matches = englishCommonWords.filter { it.startsWith(lower) && it != lower }
                matches.take(2).forEach {
                    if (suggestions.size < 3) {
                        suggestions.add(SuggestionItem(displayText = it, commitText = it))
                    }
                }
            }
        }

        // Fill remaining slots from GenZ slang
        if (suggestions.size < 3) {
            val genz = GenZSlangDictionary.findMatches(lower)
            for (entry in genz) {
                if (suggestions.none { it.commitText == entry.term }) {
                    suggestions.add(SuggestionItem(displayText = entry.term, commitText = entry.term, secondaryHint = entry.meaning))
                    if (suggestions.size >= 3) break
                }
            }
        }

        return suggestions.take(3)
    }
}
