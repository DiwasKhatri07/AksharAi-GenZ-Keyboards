package com.example.engine.swipe

import androidx.compose.ui.geometry.Offset
import com.example.data.preferences.LanguageMode
import com.example.engine.nepali.NepaliTransliterationEngine
import com.example.engine.nepinglish.GenZSlangDictionary
import com.example.engine.nepinglish.NepinglishEngine
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.min

data class SwipePoint(
    val x: Float,
    val y: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class SwipeCandidate(
    val word: String,
    val transliterated: String,
    val confidence: Float
)

object SwipeTypingEngine {

    // Comprehensive Nepinglish and common word vocabulary for swipe recognition
    private val SWIPE_LEXICON: List<String> by lazy {
        val list = mutableSetOf(
            // Greetings & Common Phrases
            "namaste", "namaskar", "kcha", "kasto", "sanchai", "thikcha", "thik",
            "hajur", "tapai", "timi", "mero", "timro", "hamro", "usko", "sabai",
            // Slang & Gen-Z
            "dami", "khatra", "babaal", "babbal", "sahii", "sahi", "chill", "vibe",
            "bro", "sathi", "yar", "yaar", "guff", "jpt", "budo", "solti", "maya",
            "fuchhe", "fuchhi", "noob", "pro", "sigma", "based", "chad",
            // Verbs & Actions
            "khana", "khayau", "khaye", "khanchu", "garchu", "garyau", "gare", "garne",
            "aayo", "aaye", "aauchhu", "jane", "gaye", "gayau", "bhaneko", "bhanyo",
            "socheko", "dekhe", "dekhyo", "baseko", "suteko", "uthyo", "huncha",
            "hudaina", "chaina", "cha", "theyo", "thiyo", "hune", "garnu", "bhanda",
            // Places & Nouns
            "nepal", "ktm", "kathmandu", "pokhara", "dharan", "butwal", "momo", "chiya",
            "bhat", "ghar", "college", "school", "office", "kam", "paisa", "mobile",
            // Common English
            "hello", "hey", "what", "where", "when", "why", "how", "good", "great",
            "love", "like", "awesome", "cool", "nice", "today", "tomorrow", "night",
            "morning", "please", "thanks", "thank", "welcome", "happy", "smile"
        )
        // Add all terms from GenZ dictionary
        GenZSlangDictionary.SLANG_ENTRIES.forEach { list.add(it.term.lowercase(Locale.ROOT)) }
        list.toList()
    }

    // Standard QWERTY normalized key center positions (0f to 1f relative space)
    val QWERTY_LAYOUT_NORMALIZED = mapOf(
        'q' to Offset(0.05f, 0.17f), 'w' to Offset(0.15f, 0.17f), 'e' to Offset(0.25f, 0.17f),
        'r' to Offset(0.35f, 0.17f), 't' to Offset(0.45f, 0.17f), 'y' to Offset(0.55f, 0.17f),
        'u' to Offset(0.65f, 0.17f), 'i' to Offset(0.75f, 0.17f), 'o' to Offset(0.85f, 0.17f),
        'p' to Offset(0.95f, 0.17f),

        'a' to Offset(0.10f, 0.50f), 's' to Offset(0.20f, 0.50f), 'd' to Offset(0.30f, 0.50f),
        'f' to Offset(0.40f, 0.50f), 'g' to Offset(0.50f, 0.50f), 'h' to Offset(0.60f, 0.50f),
        'j' to Offset(0.70f, 0.50f), 'k' to Offset(0.80f, 0.50f), 'l' to Offset(0.90f, 0.50f),

        'z' to Offset(0.20f, 0.83f), 'x' to Offset(0.30f, 0.83f), 'c' to Offset(0.40f, 0.83f),
        'v' to Offset(0.50f, 0.83f), 'b' to Offset(0.60f, 0.83f), 'n' to Offset(0.70f, 0.83f),
        'm' to Offset(0.80f, 0.83f)
    )

    /**
     * Maps raw swipe coordinates normalized to [0..1] range into top candidate words.
     */
    fun recognizeSwipe(
        points: List<SwipePoint>,
        keyboardWidth: Float,
        keyboardHeight: Float,
        mode: LanguageMode
    ): List<SwipeCandidate> {
        if (points.size < 3 || keyboardWidth <= 0f || keyboardHeight <= 0f) return emptyList()

        // Normalize points to [0..1]
        val normalizedPoints = points.map {
            Offset(
                x = (it.x / keyboardWidth).coerceIn(0f, 1f),
                y = (it.y / keyboardHeight).coerceIn(0f, 1f)
            )
        }

        val startOffset = normalizedPoints.first()
        val endOffset = normalizedPoints.last()

        val startChar = findClosestKey(startOffset) ?: return emptyList()
        val endChar = findClosestKey(endOffset) ?: return emptyList()

        // Downsample points to identify trajectory
        val sampledPoints = sampleTrajectory(normalizedPoints, 20)

        val candidates = mutableListOf<SwipeCandidate>()

        for (word in SWIPE_LEXICON) {
            val lower = word.lowercase(Locale.ROOT)
            if (lower.length < 2) continue

            // Word must start and end close to the gesture endpoints
            val wordStart = lower.first()
            val wordEnd = lower.last()

            val startDist = keyDistance(wordStart, startChar)
            val endDist = keyDistance(wordEnd, endChar)

            // Allow near-miss on start and end keys (adjacent key tolerance)
            if (startDist > 0.18f || endDist > 0.18f) continue

            // Check if the characters of the word appear as an ordered subsequence along the path
            val subScore = matchSubsequenceScore(lower, sampledPoints)
            if (subScore < 0) continue

            // Compute total spatial trajectory distance error
            val pathError = computePathError(lower, sampledPoints)
            val totalScore = 1.0f - (pathError * 0.7f + startDist * 0.15f + endDist * 0.15f)

            if (totalScore > 0.35f) {
                val transliterated = when (mode) {
                    LanguageMode.NEPALI, LanguageMode.NEPINGLISH ->
                        NepaliTransliterationEngine.transliterateWord(lower)
                    LanguageMode.ENGLISH -> lower
                }

                candidates.add(
                    SwipeCandidate(
                        word = lower,
                        transliterated = transliterated,
                        confidence = totalScore
                    )
                )
            }
        }

        // Sort descending by confidence score
        return candidates.sortedByDescending { it.confidence }.take(5)
    }

    private fun findClosestKey(offset: Offset): Char? {
        var minDistance = Float.MAX_VALUE
        var closestKey: Char? = null

        for ((char, pos) in QWERTY_LAYOUT_NORMALIZED) {
            val dist = hypot(offset.x - pos.x, offset.y - pos.y)
            if (dist < minDistance) {
                minDistance = dist
                closestKey = char
            }
        }
        return closestKey
    }

    private fun keyDistance(c1: Char, c2: Char): Float {
        if (c1 == c2) return 0f
        val p1 = QWERTY_LAYOUT_NORMALIZED[c1] ?: return 1f
        val p2 = QWERTY_LAYOUT_NORMALIZED[c2] ?: return 1f
        return hypot(p1.x - p2.x, p1.y - p2.y)
    }

    private fun sampleTrajectory(points: List<Offset>, targetCount: Int): List<Offset> {
        if (points.size <= targetCount) return points
        val step = points.size.toFloat() / targetCount
        return (0 until targetCount).map { i ->
            val idx = (i * step).toInt().coerceIn(0, points.size - 1)
            points[idx]
        }
    }

    private fun matchSubsequenceScore(word: String, path: List<Offset>): Float {
        var pathIdx = 0
        for (char in word) {
            val keyPos = QWERTY_LAYOUT_NORMALIZED[char] ?: return -1f
            var found = false

            while (pathIdx < path.size) {
                val dist = hypot(path[pathIdx].x - keyPos.x, path[pathIdx].y - keyPos.y)
                pathIdx++
                if (dist < 0.22f) {
                    found = true
                    break
                }
            }
            if (!found) return -1f
        }
        return 1.0f
    }

    private fun computePathError(word: String, path: List<Offset>): Float {
        var totalDist = 0f
        for (char in word) {
            val keyPos = QWERTY_LAYOUT_NORMALIZED[char] ?: continue
            var minDistToPath = Float.MAX_VALUE
            for (p in path) {
                val dist = hypot(p.x - keyPos.x, p.y - keyPos.y)
                if (dist < minDistToPath) {
                    minDistToPath = dist
                }
            }
            totalDist += minDistToPath
        }
        return totalDist / word.length
    }
}
