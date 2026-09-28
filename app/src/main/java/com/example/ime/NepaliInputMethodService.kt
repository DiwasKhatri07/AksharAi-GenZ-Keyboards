package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class NepaliInputMethodService : LifecycleInputMethodService(), TextToSpeech.OnInitListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var preferences: KeyboardPreferences
    private lateinit var database: AppDatabase
    private lateinit var aiManager: AiManager

    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    // IME State
    private var currentLanguage by mutableStateOf(LanguageMode.NEPINGLISH)
    private var editorAction by mutableIntStateOf(EditorInfo.IME_ACTION_NONE)
    private var composingWord by mutableStateOf("")
    private var previousWord by mutableStateOf("")
    private var suggestions by mutableStateOf<List<SuggestionItem>>(emptyList())
    private val clipboardList = MutableStateFlow<List<ClipboardEntity>>(emptyList())

    // AI Panel State
    private var isAiLoading by mutableStateOf(false)
    private var aiResult by mutableStateOf<AiResult?>(null)
    private var aiErrorMessage by mutableStateOf<String?>(null)
    private var lastAiReplacedText by mutableStateOf<String?>(null)
    private var aiSourceText by mutableStateOf("")

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        captureSystemClipboard()
    }

    override fun onCreate() {
        super.onCreate()
        preferences = KeyboardPreferences(this)
        database = AppDatabase.getInstance(this)
        aiManager = AiManager(this)

        currentLanguage = preferences.currentLanguage
        vibrator = getSystemService(Vibrator::class.java)
        audioManager = getSystemService(AudioManager::class.java)
        textToSpeech = TextToSpeech(this, this)

        // Clipboard listener
        val cm = getSystemService(ClipboardManager::class.java)
        cm?.addPrimaryClipChangedListener(clipListener)

        // Observe Room clipboard items
        serviceScope.launch {
            database.clipboardDao().getRecentClips(15).collectLatest { list ->
                clipboardList.value = list
            }
        }
    }

    override fun onCreateInputView(): View {
        ensureLifecycleActive()

        // Attach ViewTree Owners to decorView if available
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        val composeView = ComposeView(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(
                androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@NepaliInputMethodService)
            )
            setViewTreeLifecycleOwner(this@NepaliInputMethodService)
            setViewTreeViewModelStoreOwner(this@NepaliInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@NepaliInputMethodService)

            setContent {
                val theme = KeyboardThemes.getThemeById(preferences.selectedThemeId)
                val clips by clipboardList.collectAsState()

                KeyboardRootView(
                    currentLanguage = currentLanguage,
                    suggestions = suggestions,
                    theme = theme,
                    editorAction = editorAction,
                    showNumberRow = preferences.showNumberRow,
                    keyHeightDp = preferences.keyHeightDp,
                    keyRadiusDp = preferences.keyRadiusDp,
                    clipboardEntries = clips,
                    aiSourceText = aiSourceText.ifBlank { getTargetTextForAi() },
                    isAiLoading = isAiLoading,
                    aiResult = aiResult,
                    aiErrorMessage = aiErrorMessage,
                    onKeyTyped = { handleKeyTyped(it) },
                    onBackspace = { handleBackspace() },
                    onEnter = { handleEnter() },
                    onSpace = { handleSpace() },
                    onCursorMove = { delta -> moveCursor(delta) },
                    onSuggestionClick = { item -> commitSuggestion(item) },
                    onLanguageSwitch = { cycleLanguage() },
                    onVoiceClick = { startVoiceInput() },
                    onTtsClick = { speakCurrentText() },
                    onSettingsClick = { openSettings() },
                    onAiModeSelect = { mode -> runAiTransformation(mode) },
                    onAiInsert = { text ->
                        currentInputConnection?.commitText(text, 1)
                        clearComposing()
                    },
                    onAiReplace = { text ->
                        val target = getTargetTextForAi()
                        lastAiReplacedText = target
                        if (target.isNotBlank()) {
                            currentInputConnection?.deleteSurroundingText(target.length, 0)
                        }
                        currentInputConnection?.commitText(text, 1)
                        clearComposing()
                    },
                    onAiCopy = { text ->
                        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        cm?.setPrimaryClip(ClipData.newPlainText("AI Text", text))
                        Toast.makeText(this@NepaliInputMethodService, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onAiRegenerate = {
                        aiResult?.mode?.let { runAiTransformation(it) }
                    },
                    onAiUndo = {
                        lastAiReplacedText?.let { original ->
                            currentInputConnection?.commitText(original, 1)
                            lastAiReplacedText = null
                        }
                    },
                    onClipPaste = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onClipPinToggle = { clip ->
                        serviceScope.launch(Dispatchers.IO) {
                            database.clipboardDao().update(clip.copy(isPinned = !clip.isPinned))
                        }
                    },
                    onClipDelete = { clip ->
                        serviceScope.launch(Dispatchers.IO) {
                            database.clipboardDao().delete(clip)
                        }
                    },
                    onClipClearAll = {
                        serviceScope.launch(Dispatchers.IO) {
                            database.clipboardDao().clearUnpinned()
                        }
                    },
                    onSelectAll = {
                        currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
                    },
                    onCopy = {
                        currentInputConnection?.performContextMenuAction(android.R.id.copy)
                        Toast.makeText(this@NepaliInputMethodService, "Copied", Toast.LENGTH_SHORT).show()
                    },
                    onCut = {
                        currentInputConnection?.performContextMenuAction(android.R.id.cut)
                        Toast.makeText(this@NepaliInputMethodService, "Cut", Toast.LENGTH_SHORT).show()
                    },
                    onPaste = {
                        currentInputConnection?.performContextMenuAction(android.R.id.paste)
                    },
                    onSwipeRecognized = { candidates ->
                        if (candidates.isNotEmpty()) {
                            val top = candidates.first()
                            val textToCommit = when (currentLanguage) {
                                LanguageMode.NEPALI -> top.transliterated
                                LanguageMode.NEPINGLISH -> if (preferences.autocorrectEnabled) top.transliterated else top.word
                                LanguageMode.ENGLISH -> top.word
                            }
                            currentInputConnection?.commitText("$textToCommit ", 1)
                            previousWord = top.word
                            clearComposing()

                            // Update suggestion bar with alternative swipe options
                            suggestions = candidates.map { cand ->
                                SuggestionItem(
                                    displayText = if (currentLanguage == LanguageMode.NEPALI) cand.transliterated else cand.word,
                                    commitText = if (currentLanguage == LanguageMode.NEPALI) cand.transliterated else cand.word,
                                    secondaryHint = if (currentLanguage == LanguageMode.NEPINGLISH) cand.transliterated else null
                                )
                            }
                            playFeedback()
                        }
                    }
                )
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        (lifecycle as androidx.lifecycle.LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_START)
        (lifecycle as androidx.lifecycle.LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val inputType = info?.inputType ?: 0
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION
        val isPassword = variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                ((inputType and EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_NUMBER && variation == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)

        editorAction = info?.imeOptions ?: EditorInfo.IME_ACTION_NONE
        clearComposing()

        if (isPassword) {
            suggestions = emptyList()
            aiSourceText = ""
            return
        }

        refreshSuggestions()
        captureSystemClipboard()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        (lifecycle as androidx.lifecycle.LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        clearComposing()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        cm?.removePrimaryClipChangedListener(clipListener)
    }

    private fun handleKeyTyped(text: String) {
        playFeedback(AudioManager.FX_KEYPRESS_STANDARD)
        val ic = currentInputConnection ?: return

        when (currentLanguage) {
            LanguageMode.NEPINGLISH -> {
                // Accumulate composing word for Nepinglish Roman typing
                composingWord += text
                ic.setComposingText(composingWord, 1)
                refreshSuggestions()
            }
            LanguageMode.NEPALI -> {
                // Direct Devanagari character input
                ic.commitText(text, 1)
                composingWord += text
                refreshSuggestions()
            }
            LanguageMode.ENGLISH -> {
                // Direct English character input
                composingWord += text
                ic.setComposingText(composingWord, 1)
                refreshSuggestions()
            }
        }
    }

    private fun handleBackspace() {
        playFeedback(AudioManager.FX_KEYPRESS_DELETE)
        val ic = currentInputConnection ?: return

        if (composingWord.isNotEmpty()) {
            composingWord = composingWord.dropLast(1)
            if (composingWord.isEmpty()) {
                ic.finishComposingText()
            } else {
                ic.setComposingText(composingWord, 1)
            }
            refreshSuggestions()
        } else {
            ic.deleteSurroundingText(1, 0)
            updateContextFromInput()
            refreshSuggestions()
        }
    }

    private fun handleSpace() {
        playFeedback(AudioManager.FX_KEYPRESS_SPACEBAR)
        val ic = currentInputConnection ?: return

        // If autocorrect is on and we have suggestions
        if (composingWord.isNotEmpty()) {
            val best = suggestions.firstOrNull()
            if (best != null && best.isAutocorrect && preferences.autocorrectEnabled) {
                ic.commitText(best.commitText + " ", 1)
            } else {
                ic.commitText("$composingWord ", 1)
            }
            previousWord = composingWord
            clearComposing()
        } else {
            ic.commitText(" ", 1)
        }
        refreshSuggestions()
    }

    private fun handleEnter() {
        playFeedback(AudioManager.FX_KEYPRESS_RETURN)
        val ic = currentInputConnection ?: return

        if (composingWord.isNotEmpty()) {
            ic.commitText(composingWord, 1)
            clearComposing()
        }

        if (editorAction and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE &&
            editorAction and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_UNSPECIFIED
        ) {
            ic.performEditorAction(editorAction and EditorInfo.IME_MASK_ACTION)
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun commitSuggestion(item: SuggestionItem) {
        playFeedback()
        val ic = currentInputConnection ?: return

        if (composingWord.isNotEmpty()) {
            ic.commitText(item.commitText + " ", 1)
            previousWord = item.displayText
            clearComposing()
        } else {
            ic.commitText(item.commitText, 1)
        }
        refreshSuggestions()
    }

    private fun moveCursor(delta: Int) {
        val ic = currentInputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(1000, 0) ?: ""
        val textAfter = ic.getTextAfterCursor(1000, 0) ?: ""
        val curPos = textBefore.length
        val newPos = (curPos + delta).coerceIn(0, textBefore.length + textAfter.length)
        ic.setSelection(newPos, newPos)
    }

    private fun cycleLanguage() {
        val next = when (currentLanguage) {
            LanguageMode.NEPINGLISH -> LanguageMode.NEPALI
            LanguageMode.NEPALI -> LanguageMode.ENGLISH
            LanguageMode.ENGLISH -> LanguageMode.NEPINGLISH
        }
        currentLanguage = next
        preferences.currentLanguage = next
        clearComposing()
        refreshSuggestions()
        Toast.makeText(this, "Language: ${next.displayName}", Toast.LENGTH_SHORT).show()
    }

    private fun clearComposing() {
        composingWord = ""
        currentInputConnection?.finishComposingText()
    }

    private fun refreshSuggestions() {
        aiSourceText = getTargetTextForAi()
        if (!preferences.suggestionsEnabled) {
            suggestions = emptyList()
            return
        }
        suggestions = PredictionEngine.getSuggestions(
            composing = composingWord,
            previousWord = previousWord,
            mode = currentLanguage
        )
    }

    private fun updateContextFromInput() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val tokens = before.trim().split(" ")
        previousWord = tokens.lastOrNull() ?: ""
    }

    private fun getTargetTextForAi(): String {
        val ic = currentInputConnection ?: return ""
        val selected = ic.getSelectedText(0)?.toString()
        if (!selected.isNullOrBlank()) return selected

        if (composingWord.isNotBlank()) return composingWord

        val before = ic.getTextBeforeCursor(100, 0)?.toString()?.trim() ?: ""
        return before.split("\n").lastOrNull() ?: before
    }

    private fun runAiTransformation(mode: AiMode) {
        val text = getTargetTextForAi()
        if (text.isBlank()) {
            aiErrorMessage = "Please type or select text to transform with AI."
            return
        }

        isAiLoading = true
        aiErrorMessage = null
        aiResult = null

        serviceScope.launch {
            val result = aiManager.executeAi(mode, text)
            isAiLoading = false
            if (result.isSuccess) {
                aiResult = result.getOrNull()
            } else {
                aiErrorMessage = result.exceptionOrNull()?.message ?: "AI transformation failed"
            }
        }
    }

    private fun startVoiceInput() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(this)) {
                Toast.makeText(this, "Speech recognition unavailable on this device", Toast.LENGTH_SHORT).show()
                return
            }

            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val langTag = if (currentLanguage == LanguageMode.NEPALI) "ne-NP" else "en-US"
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak in Nepali or English...")
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@NepaliInputMethodService, "🎤 Listening...", Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull()
                    if (!spoken.isNullOrBlank()) {
                        currentInputConnection?.commitText("$spoken ", 1)
                    }
                }
                override fun onError(error: Int) {
                    Toast.makeText(this@NepaliInputMethodService, "Voice input error ($error)", Toast.LENGTH_SHORT).show()
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakCurrentText() {
        val target = getTargetTextForAi()
        if (target.isBlank()) {
            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show()
            return
        }

        textToSpeech?.let { tts ->
            // Try Nepali locale, fallback to default/English
            val nepaliLocale = Locale.forLanguageTag("ne-NP")
            val res = tts.setLanguage(nepaliLocale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.language = Locale.US
            }
            tts.speak(target, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.forLanguageTag("ne-NP")
        }
    }

    private fun openSettings() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun captureSystemClipboard() {
        try {
            val cm = getSystemService(ClipboardManager::class.java) ?: return
            val clip = cm.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()?.trim()
                if (!text.isNullOrBlank() && text.length < 500) {
                    serviceScope.launch(Dispatchers.IO) {
                        val existing = database.clipboardDao().findByText(text)
                        if (existing == null) {
                            database.clipboardDao().insert(ClipboardEntity(text = text))
                        }
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun playFeedback(soundType: Int = AudioManager.FX_KEYPRESS_STANDARD) {
        if (preferences.hapticFeedbackEnabled) {
            val duration = when (preferences.hapticIntensity) {
                1 -> 12L
                2 -> 25L
                3 -> 45L
                else -> 25L
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator?.vibrate(android.os.VibrationEffect.createOneShot(duration, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(duration)
                }
            } catch (_: Exception) {}
        }

        if (preferences.soundFeedbackEnabled) {
            try {
                audioManager?.playSoundEffect(soundType, preferences.soundVolume)
            } catch (_: Exception) {}
        }
    }
}
