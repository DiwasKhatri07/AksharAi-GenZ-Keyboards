package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class KeyboardThemeColors(
    val id: String,
    val name: String,
    val background: Color,
    val keyBackground: Color,
    val keyActionBackground: Color,
    val keyTextColor: Color,
    val keySubTextColor: Color,
    val accentColor: Color,
    val suggestionBarBackground: Color,
    val suggestionTextColor: Color,
    val keyBorderColor: Color = Color.Transparent
)

object KeyboardThemes {

    val IOS_LIGHT = KeyboardThemeColors(
        id = "ios_light",
        name = "iOS Clean Blue",
        background = Color(0xFFD1D5DB),
        keyBackground = Color(0xFFFFFFFF),
        keyActionBackground = Color(0xFFB0B7C3),
        keyTextColor = Color(0xFF111827),
        keySubTextColor = Color(0xFF6B7280),
        accentColor = Color(0xFF007AFF), // Apple iOS Blue
        suggestionBarBackground = Color(0xFFE5E7EB),
        suggestionTextColor = Color(0xFF1F2937),
        keyBorderColor = Color(0x22000000)
    )

    val DARK = KeyboardThemeColors(
        id = "dark",
        name = "Modern Dark",
        background = Color(0xFF1E1E2E),
        keyBackground = Color(0xFF2A2B3D),
        keyActionBackground = Color(0xFF383A52),
        keyTextColor = Color(0xFFF1F5F9),
        keySubTextColor = Color(0xFF94A3B8),
        accentColor = Color(0xFF818CF8),
        suggestionBarBackground = Color(0xFF181825),
        suggestionTextColor = Color(0xFFE2E8F0)
    )

    val AMOLED = KeyboardThemeColors(
        id = "amoled",
        name = "Pure AMOLED",
        background = Color(0xFF000000),
        keyBackground = Color(0xFF121212),
        keyActionBackground = Color(0xFF222222),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFF777777),
        accentColor = Color(0xFF3B82F6),
        suggestionBarBackground = Color(0xFF0A0A0A),
        suggestionTextColor = Color(0xFFCCCCCC),
        keyBorderColor = Color(0xFF262626)
    )

    val NEPAL_CRIMSON = KeyboardThemeColors(
        id = "nepal",
        name = "Nepal Heritage",
        background = Color(0xFF180A12),
        keyBackground = Color(0xFF2A0E1C),
        keyActionBackground = Color(0xFF003893), // Nepal Blue
        keyTextColor = Color(0xFFFFF0F5),
        keySubTextColor = Color(0xFFE08DAB),
        accentColor = Color(0xFFDC143C), // Nepal Crimson
        suggestionBarBackground = Color(0xFF0F040A),
        suggestionTextColor = Color(0xFFFDE8EF),
        keyBorderColor = Color(0x33DC143C)
    )

    val GEN_Z_VIBE = KeyboardThemeColors(
        id = "genz",
        name = "Gen-Z Cyber Glow",
        background = Color(0xFF0D0221),
        keyBackground = Color(0xFF1D0E3F),
        keyActionBackground = Color(0xFF35126B),
        keyTextColor = Color(0xFF00F5D4),
        keySubTextColor = Color(0xFFB5179E),
        accentColor = Color(0xFFF72585),
        suggestionBarBackground = Color(0xFF060010),
        suggestionTextColor = Color(0xFF00F5D4),
        keyBorderColor = Color(0x44F72585)
    )

    val OCEAN = KeyboardThemeColors(
        id = "ocean",
        name = "Deep Ocean Teal",
        background = Color(0xFF0A192F),
        keyBackground = Color(0xFF172A45),
        keyActionBackground = Color(0xFF203A60),
        keyTextColor = Color(0xFFE6F1FF),
        keySubTextColor = Color(0xFF8892B0),
        accentColor = Color(0xFF64FFDA),
        suggestionBarBackground = Color(0xFF061122),
        suggestionTextColor = Color(0xFFCCD6F6)
    )

    val MIDNIGHT = KeyboardThemeColors(
        id = "midnight",
        name = "Midnight Slate",
        background = Color(0xFF0F172A),
        keyBackground = Color(0xFF1E293B),
        keyActionBackground = Color(0xFF334155),
        keyTextColor = Color(0xFFF8FAFC),
        keySubTextColor = Color(0xFF94A3B8),
        accentColor = Color(0xFF38BDF8),
        suggestionBarBackground = Color(0xFF0B1120),
        suggestionTextColor = Color(0xFFE2E8F0)
    )

    val ALL_PRESETS = listOf(IOS_LIGHT, DARK, AMOLED, NEPAL_CRIMSON, GEN_Z_VIBE, OCEAN, MIDNIGHT)

    fun getThemeById(id: String): KeyboardThemeColors {
        return ALL_PRESETS.find { it.id == id } ?: DARK
    }
}
