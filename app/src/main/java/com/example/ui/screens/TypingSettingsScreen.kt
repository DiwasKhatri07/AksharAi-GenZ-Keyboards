package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AutocorrectAggressiveness
import com.example.data.preferences.KeyboardPreferences
import com.example.data.preferences.LanguageMode

@Composable
fun TypingSettingsScreen(
    preferences: KeyboardPreferences,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    var selectedLang by remember { mutableStateOf(preferences.currentLanguage) }
    var autocorrectEnabled by remember { mutableStateOf(preferences.autocorrectEnabled) }
    var aggressiveness by remember { mutableStateOf(preferences.autocorrectAggressiveness) }
    var suggestionsEnabled by remember { mutableStateOf(preferences.suggestionsEnabled) }
    var emojiSuggestionsEnabled by remember { mutableStateOf(preferences.emojiSuggestionsEnabled) }
    var showNumberRow by remember { mutableStateOf(preferences.showNumberRow) }
    var spaceSwipeEnabled by remember { mutableStateOf(preferences.spacebarSwipeCursorEnabled) }
    var doubleSpacePeriod by remember { mutableStateOf(preferences.doubleSpacePeriodEnabled) }

    var hapticEnabled by remember { mutableStateOf(preferences.hapticFeedbackEnabled) }
    var hapticIntensity by remember { mutableIntStateOf(preferences.hapticIntensity) }
    var soundEnabled by remember { mutableStateOf(preferences.soundFeedbackEnabled) }
    var soundVolume by remember { mutableFloatStateOf(preferences.soundVolume) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Typing & Keyboard Preferences",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 1. Language Mode Selector
        SectionHeader("PRIMARY TYPING LANGUAGE")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Select your default input mode. You can also cycle languages anytime with the 🌐 key.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageMode.values().forEach { mode ->
                        FilterChip(
                            selected = selectedLang == mode,
                            onClick = {
                                selectedLang = mode
                                preferences.currentLanguage = mode
                            },
                            label = { Text("${mode.badge} - ${mode.displayName}", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // 2. Suggestions & Autocorrect
        SectionHeader("SMART SUGGESTIONS & AUTOCORRECT")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ToggleRow(
                    title = "Word Suggestions Bar",
                    subtitle = "Shows 3-slot candidate bar with Nepinglish & Devanagari transliteration",
                    checked = suggestionsEnabled,
                    onCheckedChange = {
                        suggestionsEnabled = it
                        preferences.suggestionsEnabled = it
                    }
                )

                ToggleRow(
                    title = "Emoji Predictions",
                    subtitle = "Predicts matching emojis based on word context (e.g. maya -> ❤️)",
                    checked = emojiSuggestionsEnabled,
                    onCheckedChange = {
                        emojiSuggestionsEnabled = it
                        preferences.emojiSuggestionsEnabled = it
                    }
                )

                ToggleRow(
                    title = "Autocorrect",
                    subtitle = "Automatically corrects common Roman Nepali typos on space",
                    checked = autocorrectEnabled,
                    onCheckedChange = {
                        autocorrectEnabled = it
                        preferences.autocorrectEnabled = it
                    }
                )

                if (autocorrectEnabled) {
                    Text(
                        text = "Autocorrect Aggressiveness",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AutocorrectAggressiveness.values().forEach { level ->
                            FilterChip(
                                selected = aggressiveness == level,
                                onClick = {
                                    aggressiveness = level
                                    preferences.autocorrectAggressiveness = level
                                },
                                label = { Text(level.name) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Layout & Gestures
        SectionHeader("LAYOUT & GESTURES")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ToggleRow(
                    title = "Dedicated Number Row",
                    subtitle = "Displays numbers 1-0 or Nepali numerals ०-९ at the top of the keyboard",
                    checked = showNumberRow,
                    onCheckedChange = {
                        showNumberRow = it
                        preferences.showNumberRow = it
                    }
                )

                ToggleRow(
                    title = "Spacebar Cursor Swipe",
                    subtitle = "Swipe left or right across the spacebar to glide the cursor precisely",
                    checked = spaceSwipeEnabled,
                    onCheckedChange = {
                        spaceSwipeEnabled = it
                        preferences.spacebarSwipeCursorEnabled = it
                    }
                )

                ToggleRow(
                    title = "Double-Space Period",
                    subtitle = "Tapping spacebar twice rapidly inserts a period and space",
                    checked = doubleSpacePeriod,
                    onCheckedChange = {
                        doubleSpacePeriod = it
                        preferences.doubleSpacePeriodEnabled = it
                    }
                )
            }
        }

        // 4. Haptic & Sound Feedback
        SectionHeader("HAPTIC & SOUND FEEDBACK")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ToggleRow(
                    title = "Vibrate on Keypress",
                    subtitle = "Subtle tactile haptic click when tapping keys",
                    checked = hapticEnabled,
                    onCheckedChange = {
                        hapticEnabled = it
                        preferences.hapticFeedbackEnabled = it
                        if (it) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                )

                if (hapticEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vibration Intensity", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            when (hapticIntensity) {
                                1 -> "Low"
                                2 -> "Medium"
                                else -> "High"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = hapticIntensity.toFloat(),
                        onValueChange = {
                            hapticIntensity = it.toInt()
                            preferences.hapticIntensity = hapticIntensity
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        },
                        valueRange = 1f..3f,
                        steps = 1
                    )
                }

                ToggleRow(
                    title = "Sound on Keypress",
                    subtitle = "Play subtle audio tick on keypress",
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                        preferences.soundFeedbackEnabled = it
                    }
                )

                if (soundEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sound Volume", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("${(soundVolume * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = soundVolume,
                        onValueChange = {
                            soundVolume = it
                            preferences.soundVolume = soundVolume
                        },
                        valueRange = 0.1f..1.0f
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp
    )
}

@Composable
fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
