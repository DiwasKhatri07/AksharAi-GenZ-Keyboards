package com.example.engine.emoji

import java.util.Locale

enum class DetectedSentiment(val label: String, val badge: String, val emojis: List<String>) {
    LOVE("Love & Affection", "❤️ Maya", listOf("❤️", "🥰", "😘", "💖", "🥺", "💕", "😍")),
    JOY("Celebration & Fire", "🔥 Dami", listOf("🔥", "🎉", "⚡", "🥳", "✨", "😎", "🤩")),
    LAUGHTER("Humor & Memes", "😂 Haha", listOf("😂", "🤣", "💀", "😭", "🤪", "😹", "😆")),
    COOL("Sigma & Attitude", "🗿 Sigma", listOf("🗿", "😎", "🕶️", "👑", "🤙", "💯", "🦾")),
    RESPECT("Respect & Peace", "🙏 Namaste", listOf("🙏", "🇳🇵", "🏔️", "🤝", "🚩", "✨", "🌺")),
    HANGOUT("Nepali Vibes & Food", "☕ Hangout", listOf("☕", "🥟", "🍲", "🍕", "🥤", "⛺", "🎸")),
    SAD("Empathy & Care", "🥺 Care", listOf("🥺", "🫂", "💔", "😢", "😔", "🕊️", "🥀")),
    AGREEMENT("Affirmation & Chill", "✅ Sahi", listOf("✅", "👍", "👌", "💯", "🙌", "🎯", "👏"))
}

object SentimentEmojiEngine {

    private val LOVE_KEYWORDS = setOf(
        "maya", "love", "pyaro", "pyari", "miss", "cute", "baby", "darling", "mutu",
        "heart", "sathi", "care", "crush", "sweet", "lovely", "hug", "kiss"
    )

    private val JOY_KEYWORDS = setOf(
        "dami", "khatra", "babaal", "babal", "fire", "happy", "party", "congrats",
        "badhai", "wah", "good", "great", "awesome", "celebrate", "ramro", "changa",
        "excited", "bhet", "khusi", "win", "champion", "dhamaka"
    )

    private val LAUGHTER_KEYWORDS = setOf(
        "haha", "hahaha", "lol", "lmao", "rofl", "jpt", "guff", "funny", "joke",
        "comedy", "hasi", "haso", "mula", "crazy", "lata", "bakwas"
    )

    private val COOL_KEYWORDS = setOf(
        "sigma", "chad", "based", "swag", "attitude", "budo", "pro", "boss", "king",
        "flex", "aura", "don", "solti", "hero", "boker"
    )

    private val RESPECT_KEYWORDS = setOf(
        "namaste", "namaskar", "hajur", "tapai", "dhanyabad", "thanks", "nepal",
        "nepali", "pranam", "respect", "sir", "guru", "aashirwad", "peace"
    )

    private val HANGOUT_KEYWORDS = setOf(
        "chiya", "momo", "khana", "bhat", "chowmein", "coffee", "hangout", "ghumna",
        "pokhara", "ktm", "kathmandu", "dharan", "butwal", "cafe", "party", "plan"
    )

    private val SAD_KEYWORDS = setOf(
        "dukha", "sad", "cry", "tension", "gahro", "thakai", "tired", "bimar",
        "sick", "hurt", "upset", "sorry", "rona", "pida", "eklo", "alone"
    )

    private val AGREEMENT_KEYWORDS = setOf(
        "thik", "thikcha", "sahi", "sahii", "huncha", "ok", "okay", "done",
        "agreed", "sure", "yes", "ho", "honi", "pakka", "right", "confirmed"
    )

    /**
     * Fast on-device zero-allocation sentiment classifier for the current typed text.
     * Returns DetectedSentiment or null if text has neutral or insufficient sentiment.
     */
    fun analyzeSentence(text: String): DetectedSentiment? {
        if (text.isBlank()) return null

        val tokens = text.lowercase(Locale.ROOT)
            .split(Regex("[\\s,!.?:\";]+"))
            .filter { it.length >= 2 }

        if (tokens.isEmpty()) return null

        var loveScore = 0
        var joyScore = 0
        var laughScore = 0
        var coolScore = 0
        var respectScore = 0
        var hangoutScore = 0
        var sadScore = 0
        var agreeScore = 0

        for (token in tokens) {
            if (LOVE_KEYWORDS.contains(token)) loveScore += 2
            if (JOY_KEYWORDS.contains(token)) joyScore += 2
            if (LAUGHTER_KEYWORDS.contains(token)) laughScore += 2
            if (COOL_KEYWORDS.contains(token)) coolScore += 2
            if (RESPECT_KEYWORDS.contains(token)) respectScore += 2
            if (HANGOUT_KEYWORDS.contains(token)) hangoutScore += 2
            if (SAD_KEYWORDS.contains(token)) sadScore += 2
            if (AGREEMENT_KEYWORDS.contains(token)) agreeScore += 1
        }

        val scores = listOf(
            loveScore to DetectedSentiment.LOVE,
            joyScore to DetectedSentiment.JOY,
            laughScore to DetectedSentiment.LAUGHTER,
            coolScore to DetectedSentiment.COOL,
            respectScore to DetectedSentiment.RESPECT,
            hangoutScore to DetectedSentiment.HANGOUT,
            sadScore to DetectedSentiment.SAD,
            agreeScore to DetectedSentiment.AGREEMENT
        )

        val best = scores.maxByOrNull { it.first }
        return if (best != null && best.first >= 2) best.second else null
    }
}
