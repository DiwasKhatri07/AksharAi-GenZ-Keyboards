package com.example.engine.nepinglish

data class SlangEntry(
    val term: String,
    val devanagari: String,
    val meaning: String,
    val category: String,
    val exampleSentence: String
)

object GenZSlangDictionary {

    val SLANG_ENTRIES = listOf(
        SlangEntry("bro", "ब्रो", "Brother / friend", "Friends", "k cha bro?"),
        SlangEntry("yar", "यार", "Buddy / dude", "Friends", "haina yar testo haina"),
        SlangEntry("dami", "दामी", "Awesome / fire / cool", "Gen-Z", "foto dami cha bro!"),
        SlangEntry("babaal", "बबाल", "Insane / epic", "Gen-Z", "aaja ko event babal thiyo"),
        SlangEntry("khatra", "खतरा", "Dangerous / top-tier", "Gen-Z", "khatra vibe cha yaha"),
        SlangEntry("sahii", "सहि", "Right on / agreed / based", "Gen-Z", "sahii kura garyau"),
        SlangEntry("chill", "चिल", "Relaxed / calm", "Gen-Z", "chill ma basa na bro"),
        SlangEntry("vibe", "भाइब", "Atmosphere / feel", "Gen-Z", "aaja ko vibe dami cha"),
        SlangEntry("fr", "fr", "For real / sachikai", "Gen-Z", "fr bro no cap"),
        SlangEntry("idk", "idk", "I don't know / thaha chaina", "Gen-Z", "idk k garne aaja"),
        SlangEntry("lmao", "lmao", "Laughing my a** off", "Funny", "lmao kasto funny bro"),
        SlangEntry("sigma", "सिग्मा", "Lone wolf / alpha chad", "Gen-Z", "full sigma rule follow"),
        SlangEntry("based", "बेस्ड", "True to oneself / respectable", "Gen-Z", "based opinion bro"),
        SlangEntry("sus", "सस", "Suspicious / shanka", "Gen-Z", "kasto sus kura garyau"),
        SlangEntry("hau", "हौ", "Nepali emphasis tag", "Nepali", "kasto garo hau"),
        SlangEntry("kya", "क्या", "What / expressive tag", "Nepali", "kya ramro thau"),
        SlangEntry("po", "पो", "Unexpectedly", "Nepali", "timi po aayau"),
        SlangEntry("ni", "नि", "Isn't it / friendly tag", "Nepali", "thik cha ni?"),
        SlangEntry("ta", "त", "Particle of emphasis", "Nepali", "ma ta jadina"),
        SlangEntry("guff", "गफ", "Chatter / talks", "Friends", "ekchin guff garam na"),
        SlangEntry("chiya", "चिया", "Tea / hangout", "Hangout", "chiya khana jaam bro"),
        SlangEntry("momo", "मोमो", "Nepali soul food", "Food", "c-momo khana jaam")
    )

    val PRESET_PHRASES = listOf(
        "K cha bro?",
        "Ma ta chill ma chu.",
        "Khana khayau?",
        "Chiya khana jaam na bro!",
        "Dami foto xa yrr! 🔥",
        "Babal vibe cha aaja.",
        "Ekchin ma call garxu la.",
        "Ma aaudai xu, 5 min wait gara.",
        "Tension naleu bro, sab thik huncha.",
        "Namaste 🙏 sanchai hunuhunchha?",
        "Dhanyabad sathi! ❤️",
        "Literally no cap bro! 😂",
        "Aaja ko plan k cha?",
        "Ghar pugera msg gara la."
    )

    fun findMatches(query: String): List<SlangEntry> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return SLANG_ENTRIES.take(5)
        return SLANG_ENTRIES.filter {
            it.term.startsWith(q) || it.meaning.lowercase().contains(q)
        }
    }
}
