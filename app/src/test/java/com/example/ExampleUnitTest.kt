package com.example

import com.example.ai.AiMode
import com.example.ai.LocalRuleAiProvider
import com.example.data.preferences.LanguageMode
import com.example.engine.emoji.EmojiDictionary
import com.example.engine.nepali.NepaliTransliterationEngine
import com.example.engine.nepinglish.NepinglishEngine
import com.example.engine.prediction.PredictionEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNepaliTransliterationEngine() {
        assertEquals("नमस्ते", NepaliTransliterationEngine.transliterateWord("namaste"))
        assertEquals("नेपाल", NepaliTransliterationEngine.transliterateWord("nepal"))
        assertEquals("दामी", NepaliTransliterationEngine.transliterateWord("dami"))
        assertEquals("तिमी", NepaliTransliterationEngine.transliterateWord("timi"))
        assertEquals("घर", NepaliTransliterationEngine.transliterateWord("ghar"))
        assertEquals("छ", NepaliTransliterationEngine.transliterateWord("cha"))
    }

    @Test
    fun testNepinglishNormalization() {
        assertEquals("cha", NepinglishEngine.normalizeToken("xa"))
        assertEquals("timi", NepinglishEngine.normalizeToken("tmi"))
        assertEquals("garchu", NepinglishEngine.normalizeToken("garxu"))
        assertEquals("chaina", NepinglishEngine.normalizeToken("xaina"))
        assertEquals("ramro", NepinglishEngine.normalizeToken("rmro"))
    }

    @Test
    fun testPredictionEngineSuggestions() {
        val suggestions = PredictionEngine.getSuggestions(
            composing = "namaste",
            previousWord = "",
            mode = LanguageMode.NEPINGLISH
        )
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.commitText.contains("नमस्ते") || it.displayText.contains("नमस्ते") })
    }

    @Test
    fun testEmojiDictionaryContext() {
        val loveEmojis = EmojiDictionary.getEmojisForWord("maya")
        assertTrue(loveEmojis.contains("❤️"))

        val laughEmojis = EmojiDictionary.getEmojisForWord("haha")
        assertTrue(laughEmojis.contains("😂"))

        val searchResult = EmojiDictionary.searchEmojis("nepal")
        assertTrue(searchResult.contains("🇳🇵"))
    }

    @Test
    fun testLocalAiTransformations() = runBlocking {
        val provider = LocalRuleAiProvider()

        val genzRes = provider.processText(AiMode.GEN_Z, "hello friend good day").getOrNull()
        assertNotNull(genzRes)
        assertTrue(genzRes!!.resultText.contains("bro") || genzRes.resultText.contains("dami") || genzRes.resultText.contains("🔥"))

        val sigmaRes = provider.processText(AiMode.SIGMA, "focus now").getOrNull()
        assertNotNull(sigmaRes)
        assertTrue(sigmaRes!!.resultText.contains("🗿"))

        val emojifyRes = provider.processText(AiMode.EMOJIFY, "happy birthday").getOrNull()
        assertNotNull(emojifyRes)
        assertTrue(emojifyRes!!.resultText.contains("🎉") || emojifyRes.resultText.contains("🎂") || emojifyRes.resultText.contains("✨"))
    }

    @Test
    fun testSwipeTypingRecognition() {
        val width = 1000f
        val height = 600f

        // Simulate swipe gesture path through "dami" (d -> a -> m -> i)
        val word = "dami"
        val points = mutableListOf<com.example.engine.swipe.SwipePoint>()
        for (char in word) {
            val norm = com.example.engine.swipe.SwipeTypingEngine.QWERTY_LAYOUT_NORMALIZED[char] ?: continue
            points.add(com.example.engine.swipe.SwipePoint(norm.x * width, norm.y * height))
        }

        val candidates = com.example.engine.swipe.SwipeTypingEngine.recognizeSwipe(
            points = points,
            keyboardWidth = width,
            keyboardHeight = height,
            mode = LanguageMode.NEPINGLISH
        )

        assertTrue(candidates.isNotEmpty())
        assertTrue(candidates.any { it.word == "dami" && it.transliterated == "दामी" })
    }
}
