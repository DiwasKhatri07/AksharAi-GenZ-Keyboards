package com.example.engine.nepinglish

import java.util.Locale

object NepinglishEngine {

    // Common Roman Nepali slang & standard variants mapping
    // key -> standard Roman representation, Devanagari equivalent, and meaning
    private val variationMap = mapOf(
        "xa" to "cha",
        "chha" to "cha",
        "xau" to "chau",
        "chhau" to "chau",
        "xaina" to "chaina",
        "chhaina" to "chaina",
        "tmi" to "timi",
        "tmro" to "timro",
        "tmilai" to "timilai",
        "mlai" to "malai",
        "hrm" to "heram",
        "garxu" to "garchu",
        "garchhu" to "garchu",
        "janxu" to "janchu",
        "janchhu" to "janchu",
        "khanxu" to "khanchu",
        "khanchhu" to "khanchu",
        "khayeu" to "khayau",
        "rmro" to "ramro",
        "dami" to "dami",
        "ekdm" to "ekdam",
        "khtr" to "khatra",
        "hunxa" to "huncha",
        "hunchha" to "huncha",
        "vayo" to "bhayeko",
        "bho" to "bhayo",
        "bhayeo" to "bhayo",
        "yrr" to "yar",
        "broo" to "bro",
        "brooo" to "bro",
        "kya" to "kya",
        "hoina" to "haina"
    )

    // Contextual sentence predictions (previous word -> list of next words)
    private val bigramPredictions = mapOf(
        "k" to listOf("cha", "gardai", "ho", "bhaneko"),
        "ke" to listOf("cha", "gardai", "ho", "bhaneko"),
        "timi" to listOf("kaha", "kasto", "k", "ghar"),
        "tmi" to listOf("kaha", "kasto", "k", "ghar"),
        "ma" to listOf("ghar", "aaja", "bholi", "pani", "office"),
        "malai" to listOf("thaha", "man", "maya", "bata"),
        "khana" to listOf("khayau", "khanchu", "pakaune"),
        "ramro" to listOf("cha", "xa", "lagyo", "manchhe"),
        "dami" to listOf("cha", "lagyo", "bro", "bichar"),
        "ekdam" to listOf("ramro", "mitho", "dami", "sahii"),
        "kasto" to listOf("cha", "lagyo", "bhaneko"),
        "ghar" to listOf("jadai", "janchu", "ma", "bata"),
        "aaja" to listOf("bhetne", "k", "office", "ghar"),
        "chiya" to listOf("khana", "khane", "bhetau"),
        "momo" to listOf("khane", "khana", "dami"),
        "chill" to listOf("vibe", "gara", "bhayera"),
        "vibe" to listOf("cha", "dami", "ekdam")
    )

    /**
     * Normalizes a Roman Nepali token to its canonical form
     */
    fun normalizeToken(token: String): String {
        val lower = token.trim().lowercase(Locale.ROOT)
        return variationMap[lower] ?: lower
    }

    /**
     * Predicts next possible Nepinglish words given the preceding word
     */
    fun predictNextWords(previousWord: String): List<String> {
        val clean = previousWord.trim().lowercase(Locale.ROOT)
        val normalized = normalizeToken(clean)
        return bigramPredictions[clean] ?: bigramPredictions[normalized] ?: emptyList()
    }

    /**
     * Suggests corrections or completions for an in-progress Roman Nepali word
     */
    fun getCompletions(prefix: String): List<String> {
        val clean = prefix.trim().lowercase(Locale.ROOT)
        if (clean.isEmpty()) return emptyList()

        val results = mutableListOf<String>()

        // Check if prefix maps to a normalized standard
        variationMap[clean]?.let { results.add(it) }

        // Find words in variationMap or bigram dataset matching prefix
        variationMap.keys.filter { it.startsWith(clean) && !results.contains(it) }.take(2)
            .forEach { results.add(it) }

        return results
    }
}
