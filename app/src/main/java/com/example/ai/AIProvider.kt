package com.example.ai

enum class AiMode(val displayName: String, val emoji: String, val description: String) {
    GEN_Z("Gen-Z Style", "🔥", "Slang, no cap, fr fr, casual bro vibe"),
    SIGMA("Sigma Rule", "🗿", "Stoic, grindset, based, ultra confident"),
    CHAD("Gigachad", "💪", "Bold, unapologetic, supreme aura"),
    FUNNY("Humorous", "😂", "Witty, hilarious comedy twist"),
    MEME("Meme Vibes", "💀", "Internet meme humor and references"),
    SAVAGE("Savage Roast", "⚡", "Sharp, witty, sassy comeback"),
    RESPECTFUL("Respectful", "🙏", "Polite Nepali Namaste/formal tone"),
    PROFESSIONAL("Professional", "💼", "Clean, polished business communication"),
    EMOJIFY("Emojify", "✨", "Enrich with expressive emojis"),
    EXPAND("Expand", "📝", "Elaborate with thoughtful detail"),
    SHORTEN("Shorten", "✂️", "Concise, punchy, TL;DR"),
    TRANSLATE_NEPALI("To Nepali", "🇳🇵", "Translate to pure Devanagari Nepali"),
    TRANSLATE_ENGLISH("To English", "🌐", "Translate to fluent English"),
    TRANSLATE_NEPINGLISH("To Nepinglish", "🇳🇵", "Translate to Romanized Nepali"),
    REPLY("Smart Reply", "💬", "Craft a context-aware response"),
    CAPTION("Vibe Caption", "📸", "Trendy Nepali Gen-Z social media caption"),
    GRAMMAR("Grammar Fix", "✍️", "Fix spelling, transliteration and phrasing")
}

data class AiResult(
    val originalText: String,
    val resultText: String,
    val mode: AiMode,
    val providerName: String
)

interface AIProvider {
    val providerName: String
    suspend fun processText(mode: AiMode, text: String): Result<AiResult>
}
