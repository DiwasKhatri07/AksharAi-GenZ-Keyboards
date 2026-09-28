package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

enum class LanguageMode(val displayName: String, val badge: String) {
    NEPALI("Nepali", "नेपाली"),
    NEPINGLISH("Nepinglish", "NEP-ENG"),
    ENGLISH("English", "ENG")
}

enum class AutocorrectAggressiveness {
    LOW,
    MEDIUM,
    HIGH
}

enum class ClipboardRetention(val hours: Long, val label: String) {
    NEVER(-1, "Keep Forever"),
    ONE_HOUR(1, "1 Hour"),
    ONE_DAY(24, "24 Hours"),
    SEVEN_DAYS(168, "7 Days")
}

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("aayo_keyboard_prefs", Context.MODE_PRIVATE)

    var currentLanguage: LanguageMode
        get() {
            val name = prefs.getString(KEY_CURRENT_LANG, LanguageMode.NEPINGLISH.name)
            return try {
                LanguageMode.valueOf(name ?: LanguageMode.NEPINGLISH.name)
            } catch (e: Exception) {
                LanguageMode.NEPINGLISH
            }
        }
        set(value) = prefs.edit().putString(KEY_CURRENT_LANG, value.name).apply()

    var hapticFeedbackEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC, value).apply()

    var hapticIntensity: Int // 1 = Low, 2 = Medium, 3 = High
        get() = prefs.getInt(KEY_HAPTIC_INTENSITY, 2)
        set(value) = prefs.edit().putInt(KEY_HAPTIC_INTENSITY, value).apply()

    var soundFeedbackEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, false)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var soundVolume: Float
        get() = prefs.getFloat(KEY_SOUND_VOLUME, 0.5f)
        set(value) = prefs.edit().putFloat(KEY_SOUND_VOLUME, value).apply()

    var autocorrectEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTOCORRECT, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTOCORRECT, value).apply()

    var autocorrectAggressiveness: AutocorrectAggressiveness
        get() {
            val name = prefs.getString(KEY_AUTOCORRECT_AGGRESSIVENESS, AutocorrectAggressiveness.MEDIUM.name)
            return try {
                AutocorrectAggressiveness.valueOf(name ?: AutocorrectAggressiveness.MEDIUM.name)
            } catch (e: Exception) {
                AutocorrectAggressiveness.MEDIUM
            }
        }
        set(value) = prefs.edit().putString(KEY_AUTOCORRECT_AGGRESSIVENESS, value.name).apply()

    var suggestionsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SUGGESTIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_SUGGESTIONS, value).apply()

    var emojiSuggestionsEnabled: Boolean
        get() = prefs.getBoolean(KEY_EMOJI_SUGGESTIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_EMOJI_SUGGESTIONS, value).apply()

    var selectedThemeId: String
        get() = prefs.getString(KEY_THEME_ID, "dark") ?: "dark"
        set(value) = prefs.edit().putString(KEY_THEME_ID, value).apply()

    var showNumberRow: Boolean
        get() = prefs.getBoolean(KEY_NUMBER_ROW, true)
        set(value) = prefs.edit().putBoolean(KEY_NUMBER_ROW, value).apply()

    var keyRadiusDp: Int
        get() = prefs.getInt(KEY_RADIUS_DP, 8)
        set(value) = prefs.edit().putInt(KEY_RADIUS_DP, value).apply()

    var keyHeightDp: Int
        get() = prefs.getInt(KEY_HEIGHT_DP, 48)
        set(value) = prefs.edit().putInt(KEY_HEIGHT_DP, value).apply()

    var spacebarSwipeCursorEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPACE_CURSOR, true)
        set(value) = prefs.edit().putBoolean(KEY_SPACE_CURSOR, value).apply()

    var doubleSpacePeriodEnabled: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_SPACE_PERIOD, true)
        set(value) = prefs.edit().putBoolean(KEY_DOUBLE_SPACE_PERIOD, value).apply()

    var clipboardRetention: ClipboardRetention
        get() {
            val name = prefs.getString(KEY_CLIPBOARD_RETENTION, ClipboardRetention.NEVER.name)
            return try {
                ClipboardRetention.valueOf(name ?: ClipboardRetention.NEVER.name)
            } catch (e: Exception) {
                ClipboardRetention.NEVER
            }
        }
        set(value) = prefs.edit().putString(KEY_CLIPBOARD_RETENTION, value.name).apply()

    var aiHistoryEnabled: Boolean
        get() = prefs.getBoolean(KEY_AI_HISTORY, false)
        set(value) = prefs.edit().putBoolean(KEY_AI_HISTORY, value).apply()

    var preferredAiMode: String
        get() = prefs.getString(KEY_AI_MODE, "GEN_Z") ?: "GEN_Z"
        set(value) = prefs.edit().putString(KEY_AI_MODE, value).apply()

    var customApiKey: String
        get() = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_API_KEY, value).apply()

    companion object {
        private const val KEY_CUSTOM_API_KEY = "key_custom_api_key"
        private const val KEY_CURRENT_LANG = "key_current_lang"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_HAPTIC_INTENSITY = "key_haptic_intensity"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_SOUND_VOLUME = "key_sound_volume"
        private const val KEY_AUTOCORRECT = "key_autocorrect"
        private const val KEY_AUTOCORRECT_AGGRESSIVENESS = "key_autocorrect_aggressiveness"
        private const val KEY_SUGGESTIONS = "key_suggestions"
        private const val KEY_EMOJI_SUGGESTIONS = "key_emoji_suggestions"
        private const val KEY_THEME_ID = "key_theme_id"
        private const val KEY_NUMBER_ROW = "key_number_row"
        private const val KEY_RADIUS_DP = "key_radius_dp"
        private const val KEY_HEIGHT_DP = "key_height_dp"
        private const val KEY_SPACE_CURSOR = "key_space_cursor"
        private const val KEY_DOUBLE_SPACE_PERIOD = "key_double_space_period"
        private const val KEY_CLIPBOARD_RETENTION = "key_clipboard_retention"
        private const val KEY_AI_HISTORY = "key_ai_history"
        private const val KEY_AI_MODE = "key_ai_mode"
    }
}
