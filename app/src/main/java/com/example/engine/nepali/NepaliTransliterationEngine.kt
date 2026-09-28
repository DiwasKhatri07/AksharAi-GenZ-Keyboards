package com.example.engine.nepali

import java.util.Locale

object NepaliTransliterationEngine {

    // High frequency Roman Nepali -> Devanagari dictionary for instant 100% accurate match
    private val directWordMap = mapOf(
        "namaste" to "नमस्ते",
        "namaskar" to "नमस्कार",
        "nepal" to "नेपाल",
        "nepali" to "नेपाली",
        "k" to "के",
        "ke" to "के",
        "cha" to "छ",
        "chha" to "छ",
        "xa" to "छ",
        "chaina" to "छैन",
        "chhaina" to "छैन",
        "xaina" to "छैन",
        "kasto" to "कस्तो",
        "timi" to "तिमी",
        "tmi" to "तिमी",
        "timilai" to "तिमीलाई",
        "tmilai" to "तिमीलाई",
        "timro" to "तिम्रो",
        "tmro" to "तिम्रो",
        "tapai" to "तपाईं",
        "tapailai" to "तपाईंलाई",
        "tapaiiko" to "तपाईंको",
        "ma" to "म",
        "maa" to "मा",
        "malai" to "मलाई",
        "mlai" to "मलाई",
        "mero" to "मेरो",
        "hamro" to "हाम्रो",
        "hami" to "हामी",
        "hamilai" to "हामीलाई",
        "khana" to "खाना",
        "khayau" to "खायौ",
        "khayeu" to "खायौ",
        "khanchu" to "खान्छु",
        "khanxu" to "खान्छु",
        "ghar" to "घर",
        "gharma" to "घरमा",
        "aaja" to "आज",
        "aja" to "आज",
        "bholi" to "भोलि",
        "asti" to "अस्ति",
        "ramro" to "राम्रो",
        "rmro" to "राम्रो",
        "dami" to "दामी",
        "ekdam" to "एकदम",
        "khatra" to "खतरा",
        "sathi" to "साथी",
        "sathiharu" to "साथीहरू",
        "bhetau" to "भेटौं",
        "bhetaula" to "भेटौँला",
        "janchu" to "जान्छु",
        "janxu" to "जान्छु",
        "garchu" to "गर्छु",
        "garxu" to "गर्छु",
        "gardai" to "गर्दै",
        "jadai" to "जाँदै",
        "auda" to "आउँदा",
        "audai" to "आउँदै",
        "dhanyabad" to "धन्यवाद",
        "subhapravat" to "शुभप्रभात",
        "subharatri" to "शुभरात्री",
        "subhakamana" to "शुभकामना",
        "ho" to "हो",
        "haina" to "होइन",
        "hoina" to "होइन",
        "huss" to "हुस्",
        "hunchha" to "हुन्छ",
        "huncha" to "हुन्छ",
        "hunxa" to "हुन्छ",
        "la" to "ल",
        "laa" to "ला",
        "babaal" to "बबाल",
        "babal" to "बबाल",
        "churot" to "चुरोट",
        "chiya" to "चिया",
        "momo" to "मोमो",
        "paisa" to "पैसा",
        "rupiya" to "रुपियाँ",
        "kina" to "किन",
        "kasari" to "कसरी",
        "kaha" to "कहाँ",
        "kahile" to "कहिल्यै",
        "kati" to "कति",
        "ko" to "को",
        "lai" to "लाई",
        "le" to "ले",
        "bata" to "बाट",
        "sanga" to "सँग",
        "pani" to "पनि",
        "ani" to "अनि",
        "tara" to "तर",
        "thaha" to "थाहा",
        "maya" to "माया",
        "samjhana" to "सम्झना",
        "bistarai" to "बिस्तारै",
        "chito" to "छिटो",
        "parkha" to "पर्ख",
        "bhaneko" to "भनेको",
        "bhan" to "भन्",
        "thik" to "ठिक",
        "yar" to "यार",
        "bro" to "ब्रो",
        "hau" to "हौ",
        "kya" to "क्या"
    )

    // Multi-char consonants mapping for algorithmic fallback
    private val consonantsMulti = listOf(
        "chh" to "छ", "kh" to "ख", "gh" to "घ", "ch" to "च", "jh" to "झ",
        "th" to "थ", "dh" to "ध", "ph" to "फ", "bh" to "भ", "sh" to "श",
        "shh" to "ष", "ng" to "ङ", "ny" to "ञ", "gy" to "ज्ञ", "tr" to "त्र",
        "ksh" to "क्ष", "shr" to "श्र"
    )

    private val consonantsSingle = mapOf(
        'k' to "क", 'g' to "ग", 'j' to "ज", 't' to "त", 'd' to "द",
        'n' to "न", 'p' to "प", 'f' to "फ", 'b' to "ब", 'm' to "म",
        'y' to "य", 'r' to "र", 'l' to "ल", 'w' to "व", 'v' to "व",
        's' to "स", 'h' to "ह", 'x' to "क्ष"
    )

    private val vowelsIndependent = listOf(
        "aa" to "आ", "ee" to "ई", "ii" to "ई", "oo" to "ऊ", "uu" to "ऊ",
        "ai" to "ऐ", "au" to "औ", "a" to "अ", "i" to "इ", "u" to "उ",
        "e" to "ए", "o" to "ओ"
    )

    private val matraMap = listOf(
        "aa" to "ा", "ee" to "ी", "ii" to "ी", "oo" to "ू", "uu" to "ू",
        "ai" to "ै", "au" to "ौ", "a" to "", "i" to "ि", "u" to "ु",
        "e" to "े", "o" to "ो"
    )

    /**
     * Transliterates a single romanized word to Devanagari.
     */
    fun transliterateWord(input: String): String {
        val clean = input.trim().lowercase(Locale.ROOT)
        if (clean.isEmpty()) return ""

        // Check dictionary first
        directWordMap[clean]?.let { return it }

        // Algorithmic phonetic transliteration
        return algorithmicTransliterate(clean)
    }

    /**
     * Returns top 3 Devanagari candidate suggestions for a given Roman Nepali input.
     */
    fun getTransliterationCandidates(input: String): List<String> {
        val clean = input.trim().lowercase(Locale.ROOT)
        if (clean.isEmpty()) return emptyList()

        val results = mutableListOf<String>()

        // 1. Direct match
        directWordMap[clean]?.let { results.add(it) }

        // 2. Algorithmic candidate
        val algo = algorithmicTransliterate(clean)
        if (!results.contains(algo)) {
            results.add(algo)
        }

        // 3. Prefix matching from high-frequency dictionary
        val prefixMatches = directWordMap.filter { it.key.startsWith(clean) && it.value != algo && !results.contains(it.value) }
            .values.take(3 - results.size)
        results.addAll(prefixMatches)

        return results.take(3)
    }

    private fun algorithmicTransliterate(input: String): String {
        val sb = StringBuilder()
        var i = 0
        val len = input.length
        var prevWasConsonant = false

        while (i < len) {
            val remaining = input.substring(i)

            // 1. Check for matras if previous was a consonant
            if (prevWasConsonant) {
                var matraFound = false
                for ((vowelStr, matra) in matraMap) {
                    if (remaining.startsWith(vowelStr)) {
                        sb.append(matra)
                        i += vowelStr.length
                        prevWasConsonant = false
                        matraFound = true
                        break
                    }
                }
                if (matraFound) continue

                // If no vowel followed consonant, consonant naturally ends or has halanta if next is consonant
                prevWasConsonant = false
            }

            // 2. Check independent vowels
            var vowelFound = false
            for ((vowelStr, devVowel) in vowelsIndependent) {
                if (remaining.startsWith(vowelStr)) {
                    sb.append(devVowel)
                    i += vowelStr.length
                    vowelFound = true
                    prevWasConsonant = false
                    break
                }
            }
            if (vowelFound) continue

            // 3. Check multi-char consonants
            var consonantFound = false
            for ((consStr, devCons) in consonantsMulti) {
                if (remaining.startsWith(consStr)) {
                    sb.append(devCons)
                    i += consStr.length
                    prevWasConsonant = true
                    consonantFound = true
                    break
                }
            }
            if (consonantFound) continue

            // 4. Check single-char consonant
            val ch = input[i]
            val devCons = consonantsSingle[ch]
            if (devCons != null) {
                sb.append(devCons)
                i++
                prevWasConsonant = true
                continue
            }

            // Fallback: keep char as is (numbers, punctuation, symbols)
            sb.append(ch)
            i++
            prevWasConsonant = false
        }

        return sb.toString()
    }
}
