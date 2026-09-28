package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiManager
import com.example.ai.AiMode
import com.example.ai.AiResult
import com.example.data.local.AppDatabase
import com.example.data.local.ClipboardEntity
import com.example.data.preferences.KeyboardPreferences
import com.example.data.preferences.LanguageMode
import com.example.engine.prediction.PredictionEngine
import com.example.engine.prediction.SuggestionItem
import com.example.ui.keyboard.KeyboardRootView
import com.example.ui.theme.KeyboardThemes
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    preferences: KeyboardPreferences,
    database: AppDatabase,
    aiManager: AiManager,
    onNavigateToThemes: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isKeyboardEnabled by remember { mutableStateOf(false) }
    var isKeyboardSelected by remember { mutableStateOf(false) }

    // Live typing sandbox state
    var typedText by remember { mutableStateOf("") }
    var composingWord by remember { mutableStateOf("") }
    var previousWord by remember { mutableStateOf("") }
    var currentLanguage by remember { mutableStateOf(preferences.currentLanguage) }
    var suggestions by remember { mutableStateOf<List<SuggestionItem>>(emptyList()) }

    // AI state for sandbox
    var isAiLoading by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<AiResult?>(null) }
    var aiErrorMessage by remember { mutableStateOf<String?>(null) }
    var lastReplacedText by remember { mutableStateOf<String?>(null) }

    val clips by database.clipboardDao().getRecentClips(15).collectAsState(initial = emptyList())
    val theme = remember(preferences.selectedThemeId) {
        KeyboardThemes.getThemeById(preferences.selectedThemeId)
    }

    // Refresh suggestions function
    fun refreshSuggestions() {
        suggestions = PredictionEngine.getSuggestions(
            composing = composingWord,
            previousWord = previousWord,
            mode = currentLanguage
        )
    }

    // Check system IME status
    fun checkImeStatus() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledMethods = imm.enabledInputMethodList
        val packageName = context.packageName

        isKeyboardEnabled = enabledMethods.any { it.packageName == packageName }

        val defaultIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: ""
        isKeyboardSelected = defaultIme.contains(packageName)
    }

    LaunchedEffect(Unit) {
        checkImeStatus()
        refreshSuggestions()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E1035), Color(0xFF3B1259), Color(0xFF6B1D73))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Akshar AI",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🇳🇵", fontSize = 20.sp)
                        }
                        Text(
                            text = "Nepali + Nepinglish Gesture Swipe AI Keyboard",
                            color = Color(0xFFE9D5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentLanguage.badge,
                            color = Color(0xFF67E8F9),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(
                        label = if (isKeyboardEnabled) "Enabled in Settings ✓" else "Enable in Settings",
                        isActive = isKeyboardEnabled
                    )
                    StatusBadge(
                        label = if (isKeyboardSelected) "System IME Active ✓" else "Select as Default IME",
                        isActive = isKeyboardSelected
                    )
                }
            }
        }

        // Live Typing Arena & Embedded Keyboard
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header of Playground
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Typing Test & Interactive Sandbox",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                if (typedText.isNotBlank()) {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Akshar AI Text", typedText))
                                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = {
                                typedText = ""
                                composingWord = ""
                                refreshSuggestions()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Text Display Arena
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    val displayText = typedText + (if (composingWord.isNotEmpty()) "[$composingWord]" else "")
                    if (displayText.isEmpty()) {
                        Text(
                            text = "Start typing below to test Nepali, Nepinglish transliteration, and Gen-Z AI tools...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = displayText,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Word count badge in bottom right
                    Text(
                        text = "${typedText.split(" ").filter { it.isNotBlank() }.size} words",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick sample test prompts to paste
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SamplePill(label = "k cha bro?") {
                        typedText = it
                        composingWord = ""
                        refreshSuggestions()
                    }
                    SamplePill(label = "namaste 🙏") {
                        typedText = it
                        composingWord = ""
                        refreshSuggestions()
                    }
                    SamplePill(label = "khana khayau?") {
                        typedText = it
                        composingWord = ""
                        refreshSuggestions()
                    }
                    SamplePill(label = "dami vibe 🔥") {
                        typedText = it
                        composingWord = ""
                        refreshSuggestions()
                    }
                    SamplePill(label = "म घर जाँदै छु") {
                        typedText = it
                        composingWord = ""
                        refreshSuggestions()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // EMBEDDED KEYBOARD COMPONENT
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, theme.accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                ) {
                    KeyboardRootView(
                        currentLanguage = currentLanguage,
                        suggestions = suggestions,
                        theme = theme,
                        editorAction = EditorInfo.IME_ACTION_DONE,
                        showNumberRow = preferences.showNumberRow,
                        keyHeightDp = preferences.keyHeightDp,
                        keyRadiusDp = preferences.keyRadiusDp,
                        clipboardEntries = clips,
                        aiSourceText = typedText.ifBlank { composingWord },
                        isAiLoading = isAiLoading,
                        aiResult = aiResult,
                        aiErrorMessage = aiErrorMessage,
                        onKeyTyped = { char ->
                            when (currentLanguage) {
                                LanguageMode.NEPINGLISH, LanguageMode.ENGLISH -> {
                                    composingWord += char
                                    refreshSuggestions()
                                }
                                LanguageMode.NEPALI -> {
                                    typedText += char
                                    refreshSuggestions()
                                }
                            }
                        },
                        onBackspace = {
                            if (composingWord.isNotEmpty()) {
                                composingWord = composingWord.dropLast(1)
                                refreshSuggestions()
                            } else if (typedText.isNotEmpty()) {
                                typedText = typedText.dropLast(1)
                                refreshSuggestions()
                            }
                        },
                        onEnter = {
                            if (composingWord.isNotEmpty()) {
                                typedText += "$composingWord\n"
                                composingWord = ""
                            } else {
                                typedText += "\n"
                            }
                            refreshSuggestions()
                        },
                        onSpace = {
                            if (composingWord.isNotEmpty()) {
                                val best = suggestions.firstOrNull()
                                if (best != null && best.isAutocorrect && preferences.autocorrectEnabled) {
                                    typedText += "${best.commitText} "
                                } else {
                                    typedText += "$composingWord "
                                }
                                previousWord = composingWord
                                composingWord = ""
                            } else {
                                typedText += " "
                            }
                            refreshSuggestions()
                        },
                        onCursorMove = { delta ->
                            Toast.makeText(context, if (delta > 0) "Cursor Right →" else "← Cursor Left", Toast.LENGTH_SHORT).show()
                        },
                        onSuggestionClick = { item ->
                            if (composingWord.isNotEmpty()) {
                                typedText += "${item.commitText} "
                                previousWord = item.displayText
                                composingWord = ""
                            } else {
                                typedText += item.commitText
                            }
                            refreshSuggestions()
                        },
                        onLanguageSwitch = {
                            val next = when (currentLanguage) {
                                LanguageMode.NEPINGLISH -> LanguageMode.NEPALI
                                LanguageMode.NEPALI -> LanguageMode.ENGLISH
                                LanguageMode.ENGLISH -> LanguageMode.NEPINGLISH
                            }
                            currentLanguage = next
                            preferences.currentLanguage = next
                            composingWord = ""
                            refreshSuggestions()
                            Toast.makeText(context, "Switched to ${next.displayName}", Toast.LENGTH_SHORT).show()
                        },
                        onVoiceClick = {
                            Toast.makeText(context, "🎤 Voice typing ready in system mode", Toast.LENGTH_SHORT).show()
                        },
                        onTtsClick = {
                            Toast.makeText(context, "🔊 Text to speech ready in system mode", Toast.LENGTH_SHORT).show()
                        },
                        onSettingsClick = onNavigateToThemes,
                        onAiModeSelect = { mode ->
                            val target = (typedText.ifBlank { composingWord }).trim()
                            if (target.isBlank()) {
                                aiErrorMessage = "Please type something first to transform with AI."
                            } else {
                                isAiLoading = true
                                aiErrorMessage = null
                                aiResult = null
                                scope.launch {
                                    val res = aiManager.executeAi(mode, target)
                                    isAiLoading = false
                                    if (res.isSuccess) {
                                        aiResult = res.getOrNull()
                                    } else {
                                        aiErrorMessage = res.exceptionOrNull()?.message ?: "AI failed"
                                    }
                                }
                            }
                        },
                        onAiInsert = { text ->
                            typedText += " $text"
                            composingWord = ""
                        },
                        onAiReplace = { text ->
                            lastReplacedText = typedText
                            typedText = text
                            composingWord = ""
                        },
                        onAiCopy = { text ->
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("AI Text", text))
                            Toast.makeText(context, "Copied AI result!", Toast.LENGTH_SHORT).show()
                        },
                        onAiRegenerate = {
                            aiResult?.mode?.let { mode ->
                                val target = (typedText.ifBlank { composingWord }).trim()
                                isAiLoading = true
                                scope.launch {
                                    val res = aiManager.executeAi(mode, target)
                                    isAiLoading = false
                                    aiResult = res.getOrNull()
                                }
                            }
                        },
                        onAiUndo = {
                            lastReplacedText?.let {
                                typedText = it
                                lastReplacedText = null
                            }
                        },
                        onClipPaste = { text ->
                            typedText += text
                        },
                        onClipPinToggle = { clip ->
                            scope.launch {
                                database.clipboardDao().update(clip.copy(isPinned = !clip.isPinned))
                            }
                        },
                        onClipDelete = { clip ->
                            scope.launch {
                                database.clipboardDao().delete(clip)
                            }
                        },
                        onClipClearAll = {
                            scope.launch {
                                database.clipboardDao().clearUnpinned()
                            }
                        },
                        onSelectAll = {
                            Toast.makeText(context, "Selected: \"$typedText\"", Toast.LENGTH_SHORT).show()
                        },
                        onCopy = {
                            if (typedText.isNotBlank()) {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Akshar AI Text", typedText))
                                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCut = {
                            if (typedText.isNotBlank()) {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Akshar AI Text", typedText))
                                typedText = ""
                                Toast.makeText(context, "Cut to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onPaste = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = cm.primaryClip
                            if (clip != null && clip.itemCount > 0) {
                                typedText += clip.getItemAt(0).text ?: ""
                            }
                        },
                        onSwipeRecognized = { candidates ->
                            if (candidates.isNotEmpty()) {
                                val top = candidates.first()
                                val textToCommit = when (currentLanguage) {
                                    LanguageMode.NEPALI -> top.transliterated
                                    LanguageMode.NEPINGLISH -> if (preferences.autocorrectEnabled) top.transliterated else top.word
                                    LanguageMode.ENGLISH -> top.word
                                }
                                typedText += "$textToCommit "
                                previousWord = top.word
                                composingWord = ""
                                suggestions = candidates.map { cand ->
                                    SuggestionItem(
                                        displayText = if (currentLanguage == LanguageMode.NEPALI) cand.transliterated else cand.word,
                                        commitText = if (currentLanguage == LanguageMode.NEPALI) cand.transliterated else cand.word,
                                        secondaryHint = if (currentLanguage == LanguageMode.NEPINGLISH) cand.transliterated else null
                                    )
                                }
                                Toast.makeText(context, "Swiped: $textToCommit ✍️", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Setup & Activation Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "System-Wide Keyboard Setup",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enable Akshar AI GenZ Keyboard to use it across your Android apps.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isKeyboardEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isKeyboardEnabled) Icons.Default.CheckCircle else Icons.Default.Keyboard,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isKeyboardEnabled) "1. Enabled ✓" else "1. Enable IME", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showInputMethodPicker()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isKeyboardSelected) "2. Selected ✓" else "2. Switch to Akshar", fontSize = 11.sp)
                    }
                }
            }
        }

        // Quick Feature Highlights
        Text(
            text = "KEYBOARD HIGHLIGHTS & SHORTCUTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.Palette,
                title = "Themes (${KeyboardThemes.ALL_PRESETS.size})",
                description = "Dark, Light, AMOLED, Nepal Crimson, Cyber",
                onClick = onNavigateToThemes,
                modifier = Modifier.weight(1f)
            )

            FeatureCard(
                icon = Icons.Default.AutoAwesome,
                title = "AI Studio Tools",
                description = "Groq llama-3.1-8b-instant Gen-Z transformer",
                onClick = onNavigateToAi,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusBadge(label: String, isActive: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0xFF10B981).copy(alpha = 0.25f) else Color(0xFFEF4444).copy(alpha = 0.25f))
            .border(
                1.dp,
                if (isActive) Color(0xFF10B981) else Color(0xFFEF4444),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isActive) Color(0xFF10B981) else Color(0xFFF87171),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SamplePill(label: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick(label) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
