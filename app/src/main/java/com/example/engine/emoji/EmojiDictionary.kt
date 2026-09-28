package com.example.engine.emoji

import java.util.Locale

data class EmojiCategory(
    val title: String,
    val icon: String,
    val emojis: List<String>
)

object EmojiDictionary {

    private val contextEmojiMap = mapOf(
        "happy" to listOf("😊", "🥰", "✨", "🎉"),
        "khushi" to listOf("😊", "😄", "💖", "✨"),
        "love" to listOf("❤️", "🥰", "😘", "💕"),
        "maya" to listOf("❤️", "🥰", "😘", "💖"),
        "haha" to listOf("😂", "🤣", "😭", "💀"),
        "hehe" to listOf("🤭", "😆", "✨", "😜"),
        "lol" to listOf("😂", "💀", "🤣", "😭"),
        "lmao" to listOf("💀", "🤣", "😭", "😂"),
        "funny" to listOf("😂", "🤣", "🤡", "💀"),
        "party" to listOf("🎉", "🥳", "🍾", "🔥"),
        "bhoj" to listOf("🎉", "🍲", "🍛", "🥳"),
        "good morning" to listOf("☀️", "🌅", "☕", "🌸"),
        "bihan" to listOf("☀️", "🌅", "☕", "🌻"),
        "good night" to listOf("🌙", "😴", "✨", "💤"),
        "rati" to listOf("🌙", "🌌", "😴", "💤"),
        "ramro" to listOf("❤️", "🔥", "😍", "✨"),
        "dami" to listOf("🔥", "⚡", "😎", "💥"),
        "khatra" to listOf("🔥", "⚡", "💣", "🤯"),
        "nepal" to listOf("🇳🇵", "🏔️", "🚩", "🙏"),
        "nepali" to listOf("🇳🇵", "🏔️", "🙏", "❤️"),
        "namaste" to listOf("🙏", "🌸", "✨", "😊"),
        "dhanyabad" to listOf("🙏", "❤️", "✨", "💐"),
        "sad" to listOf("🥺", "😢", "💔", "😭"),
        "dukha" to listOf("🥺", "💔", "😢", "🥀"),
        "bro" to listOf("🤝", "🫂", "👊", "🔥"),
        "sathi" to listOf("🤝", "🫂", "🫶", "❤️"),
        "chill" to listOf("😎", "🏖️", "🍹", "✌️"),
        "vibe" to listOf("✨", "🎶", "🔮", "🔥"),
        "sigma" to listOf("🗿", "🍷", "🕶️", "🗿"),
        "chad" to listOf("🗿", "💪", "🍷", "👑"),
        "fire" to listOf("🔥", "🧨", "💥", "⚡"),
        "chiya" to listOf("☕", "🫖", "🍪", "✨"),
        "momo" to listOf("🥟", "🍲", "😋", "🥢"),
        "khana" to listOf("🍛", "🍚", "🍲", "😋"),
        "paisa" to listOf("💰", "💵", "🤑", "💸"),
        "phone" to listOf("📱", "📞", "📲", "💬"),
        "sleep" to listOf("😴", "💤", "🛌", "🌙"),
        "congrats" to listOf("🎉", "👏", "🥳", "💐"),
        "bday" to listOf("🎂", "🎉", "🎈", "🎁"),
        "birthday" to listOf("🎂", "🎁", "🎉", "🥳")
    )

    val CATEGORIES = listOf(
        EmojiCategory("Smileys", "😀", listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😭", "😉",
            "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😋", "😛",
            "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐", "🤨",
            "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "😮‍💨", "🤥", "😌",
            "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧",
            "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "🥸", "😎", "🤓"
        )),
        EmojiCategory("Gen-Z", "🔥", listOf(
            "🔥", "💀", "🗿", "🍷", "😭", "🤡", "🤌", "✨", "👀", "💅",
            "🧢", "🚫🧢", "👑", "🫡", "🫠", "🫣", "🫢", "🤝", "🫶", "❤️‍🔥",
            "💯", "⚡", "💥", "🚀", "💣", "👽", "👾", "🕶️", "🦾", "✌️"
        )),
        EmojiCategory("Nepal", "🇳🇵", listOf(
            "🇳🇵", "🏔️", "⛰️", "🙏", "🚩", "🪷", "🌸", "☕", "🫖", "🥟",
            "🍛", "🍚", "🍲", "🐅", "🦏", "🦚", "🦅", "🕉️", "☸️", "🪙"
        )),
        EmojiCategory("Gestures", "👍", listOf(
            "👍", "👎", "👊", "✊", "🤛", "🤜", "👏", "🙌", "👐", "🤲",
            "🤝", "🙏", "✍️", "💪", "🦾", "🦵", "🦶", "👂", "👃", "🧠",
            "🫀", "🫁", "🦷", "🦴", "👀", "👁️", "👅", "👄", "🫦", "👶"
        )),
        EmojiCategory("Heart & Mood", "❤️", listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
            "💟", "💌", "💋", "🫂", "💐", "🌹", "🥀", "🌺", "🌸", "🌻"
        )),
        EmojiCategory("Food & Hangout", "☕", listOf(
            "☕", "🫖", "🥟", "🍲", "🍛", "🍚", "🍜", "🍝", "🍕", "🍔",
            "🍟", "🌭", "🥪", "🌮", "🌯", "🥙", "🥗", "🍿", "🧈", "🍳",
            "🧇", "🥞", "🧀", "🍗", "🍖", "🍰", "🎂", "🧁", "🍩", "🍫"
        )),
        EmojiCategory("Flags & Objects", "🎉", listOf(
            "🇳🇵", "🇺🇸", "🇬🇧", "🇮🇳", "🇯🇵", "🇰🇷", "🇨🇦", "🇦🇺", "🇩🇪", "🇫🇷",
            "🎉", "🎊", "🎈", "🎁", "🏆", "🥇", "🥈", "🥉", "⚽", "🏏",
            "🎮", "🕹️", "📱", "💻", "⌚", "📷", "📸", "🎧", "🎵", "🎶"
        ))
    )

    fun getEmojisForWord(word: String): List<String> {
        val clean = word.trim().lowercase(Locale.ROOT)
        return contextEmojiMap[clean] ?: emptyList()
    }

    fun searchEmojis(query: String): List<String> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return CATEGORIES.first().emojis

        // Check context map
        val results = mutableListOf<String>()
        contextEmojiMap.filter { it.key.contains(q) || q.contains(it.key) }
            .values.forEach { results.addAll(it) }

        if (results.isEmpty()) {
            // Return popular emojis
            return CATEGORIES[0].emojis.take(15)
        }
        return results.distinct().take(20)
    }
}
