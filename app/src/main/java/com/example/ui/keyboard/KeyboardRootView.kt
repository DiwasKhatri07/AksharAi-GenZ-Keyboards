package com.example.ui.keyboard

import android.view.inputmethod.EditorInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiMode
import com.example.ai.AiResult
import com.example.data.local.ClipboardEntity
import com.example.data.preferences.LanguageMode
import com.example.engine.nepali.DevanagariLayouts
import com.example.engine.prediction.SuggestionItem
import com.example.engine.swipe.SwipeCandidate
import com.example.engine.swipe.SwipePoint
import com.example.engine.swipe.SwipeTypingEngine
import com.example.ui.theme.KeyboardThemeColors

enum class ActivePanel {
    NONE,
    AI,
    CLIPBOARD,
    EMOJI
}

enum class KeyboardSubLayout {
    ALPHA,
    SYMBOLS,
    MORE_SYMBOLS
}

@Composable
fun KeyboardRootView(
    currentLanguage: LanguageMode,
    suggestions: List<SuggestionItem>,
    theme: KeyboardThemeColors,
    editorAction: Int,
    showNumberRow: Boolean,
    keyHeightDp: Int,
    keyRadiusDp: Int,
    clipboardEntries: List<ClipboardEntity>,
    aiSourceText: String,
    isAiLoading: Boolean,
    aiResult: AiResult?,
    aiErrorMessage: String?,
    modifier: Modifier = Modifier,
    onKeyTyped: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onCursorMove: (Int) -> Unit, // -1 for left, 1 for right
    onSuggestionClick: (SuggestionItem) -> Unit,
    onLanguageSwitch: () -> Unit,
    onVoiceClick: () -> Unit,
    onTtsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    // AI callbacks
    onAiModeSelect: (AiMode) -> Unit,
    onAiInsert: (String) -> Unit,
    onAiReplace: (String) -> Unit,
    onAiCopy: (String) -> Unit,
    onAiRegenerate: () -> Unit,
    onAiUndo: () -> Unit,
    // Clipboard callbacks
    onClipPaste: (String) -> Unit,
    onClipPinToggle: (ClipboardEntity) -> Unit,
    onClipDelete: (ClipboardEntity) -> Unit,
    onClipClearAll: () -> Unit,
    // iOS Quick Edit callbacks
    onSelectAll: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onCut: (() -> Unit)? = null,
    onPaste: (() -> Unit)? = null,
    // Gesture swipe typing callback
    onSwipeRecognized: ((List<SwipeCandidate>) -> Unit)? = null
) {
    var activePanel by remember { mutableStateOf(ActivePanel.NONE) }
    var subLayout by remember { mutableStateOf(KeyboardSubLayout.ALPHA) }
    var isShifted by remember { mutableStateOf(false) }

    // Swipe gesture tracking state
    var keyboardWidth by remember { mutableFloatStateOf(0f) }
    var keyboardHeight by remember { mutableFloatStateOf(0f) }
    var swipePoints by remember { mutableStateOf<List<SwipePoint>>(emptyList()) }
    var isSwiping by remember { mutableStateOf(false) }

    // Dynamic sentiment analysis of current text buffer
    val detectedSentiment = remember(aiSourceText) {
        com.example.engine.emoji.SentimentEmojiEngine.analyzeSentence(aiSourceText)
    }

    // QWERTY Rows for English & Nepinglish
    val qwertyRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val qwertyRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val qwertyRow3 = listOf("z", "x", "c", "v", "b", "n", "m")
    val numberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val qwertyRow2Symbols = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
    val qwertyRow3Symbols = listOf("*", "\"", "'", ":", ";", "!", "?")

    val nepaliNumberRow = DevanagariLayouts.NEPALI_NUMBERS

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(theme.background)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Suggestion Bar
            SuggestionBarView(
                suggestions = suggestions,
                currentLanguage = currentLanguage,
                theme = theme,
                sentiment = detectedSentiment,
                onEmojiSelect = { onKeyTyped(it) },
                onSuggestionClick = onSuggestionClick,
                onAiClick = {
                    activePanel = if (activePanel == ActivePanel.AI) ActivePanel.NONE else ActivePanel.AI
                },
                onClipboardClick = {
                    activePanel = if (activePanel == ActivePanel.CLIPBOARD) ActivePanel.NONE else ActivePanel.CLIPBOARD
                },
                onVoiceClick = onVoiceClick,
                onTtsClick = onTtsClick,
                onEmojiClick = {
                    activePanel = if (activePanel == ActivePanel.EMOJI) ActivePanel.NONE else ActivePanel.EMOJI
                },
                onLanguageClick = onLanguageSwitch,
                onSettingsClick = onSettingsClick,
                onSelectAll = onSelectAll,
                onCopy = onCopy,
                onCut = onCut,
                onPaste = onPaste
            )

            // Panel view or Keys view
            AnimatedContent(
                targetState = activePanel,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "panel_switcher"
            ) { panel ->
                when (panel) {
                    ActivePanel.AI -> {
                        AiPanelBottomSheet(
                            sourceText = aiSourceText,
                            theme = theme,
                            isLoading = isAiLoading,
                            aiResult = aiResult,
                            errorMessage = aiErrorMessage,
                            onModeSelect = onAiModeSelect,
                            onInsertResult = {
                                onAiInsert(it)
                                activePanel = ActivePanel.NONE
                            },
                            onReplaceResult = {
                                onAiReplace(it)
                                activePanel = ActivePanel.NONE
                            },
                            onCopyResult = onAiCopy,
                            onRegenerate = onAiRegenerate,
                            onUndo = onAiUndo,
                            onClose = { activePanel = ActivePanel.NONE }
                        )
                    }

                    ActivePanel.CLIPBOARD -> {
                        ClipboardPanelView(
                            clips = clipboardEntries,
                            theme = theme,
                            onClipClick = {
                                onClipPaste(it)
                                activePanel = ActivePanel.NONE
                            },
                            onPinToggle = onClipPinToggle,
                            onDeleteClip = onClipDelete,
                            onClearAll = onClipClearAll,
                            onClose = { activePanel = ActivePanel.NONE }
                        )
                    }

                    ActivePanel.EMOJI -> {
                        EmojiPickerView(
                            theme = theme,
                            onEmojiSelected = { onKeyTyped(it) },
                            onBackspace = onBackspace,
                            onBackToKeyboard = { activePanel = ActivePanel.NONE }
                        )
                    }

                    ActivePanel.NONE -> {
                        // Main Keyboard Layout with Gesture Swipe Detection
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords ->
                                    keyboardWidth = coords.size.width.toFloat()
                                    keyboardHeight = coords.size.height.toFloat()
                                }
                                .pointerInput(subLayout, currentLanguage) {
                                    if (subLayout == KeyboardSubLayout.ALPHA) {
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                isSwiping = true
                                                swipePoints = listOf(SwipePoint(offset.x, offset.y))
                                            },
                                            onDrag = { change, _ ->
                                                change.consume()
                                                swipePoints = swipePoints + SwipePoint(change.position.x, change.position.y)
                                            },
                                            onDragEnd = {
                                                isSwiping = false
                                                if (swipePoints.size >= 4 && keyboardWidth > 0f) {
                                                    val candidates = SwipeTypingEngine.recognizeSwipe(
                                                        points = swipePoints,
                                                        keyboardWidth = keyboardWidth,
                                                        keyboardHeight = keyboardHeight,
                                                        mode = currentLanguage
                                                    )
                                                    if (candidates.isNotEmpty()) {
                                                        onSwipeRecognized?.invoke(candidates)
                                                    }
                                                }
                                            },
                                            onDragCancel = {
                                                isSwiping = false
                                                swipePoints = emptyList()
                                            }
                                        )
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                            ) {
                            // 1. Optional Number Row
                            if (showNumberRow && subLayout == KeyboardSubLayout.ALPHA) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    val numbers = if (currentLanguage == LanguageMode.NEPALI) nepaliNumberRow else numberRow
                                    numbers.forEach { num ->
                                        KeyboardKey(
                                            text = num,
                                            theme = theme,
                                            keyHeightDp = keyHeightDp - 6,
                                            keyRadiusDp = keyRadiusDp,
                                            modifier = Modifier.weight(1f),
                                            onKeyClick = { onKeyTyped(num) }
                                        )
                                    }
                                }
                            }

                            // 2. Main Character Layouts
                            when (subLayout) {
                                KeyboardSubLayout.ALPHA -> {
                                    if (currentLanguage == LanguageMode.NEPALI) {
                                        // Native Devanagari Layout
                                        val row1 = if (isShifted) DevanagariLayouts.SHIFT_ROW_1 else DevanagariLayouts.PRIMARY_ROW_1
                                        val row2 = if (isShifted) DevanagariLayouts.SHIFT_ROW_2 else DevanagariLayouts.PRIMARY_ROW_2
                                        val row3 = if (isShifted) DevanagariLayouts.SHIFT_ROW_3 else DevanagariLayouts.PRIMARY_ROW_3
                                        val rowBottom = if (isShifted) DevanagariLayouts.SHIFT_ROW_BOTTOM else DevanagariLayouts.PRIMARY_ROW_BOTTOM

                                        // Row 1
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            row1.forEach { k ->
                                                KeyboardKey(text = k, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(k) })
                                            }
                                        }
                                        // Row 2
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            row2.forEach { k ->
                                                KeyboardKey(text = k, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(k) })
                                            }
                                        }
                                        // Row 3: Shift + Consonants + Backspace
                                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            KeyboardKey(
                                                icon = Icons.Default.ArrowUpward,
                                                theme = theme,
                                                isActionKey = true,
                                                isActive = isShifted,
                                                keyHeightDp = keyHeightDp,
                                                keyRadiusDp = keyRadiusDp,
                                                modifier = Modifier.weight(1.3f),
                                                onKeyClick = { isShifted = !isShifted }
                                            )
                                            row3.forEach { k ->
                                                KeyboardKey(text = k, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(k) })
                                            }
                                            KeyboardKey(
                                                icon = Icons.AutoMirrored.Filled.Backspace,
                                                theme = theme,
                                                isActionKey = true,
                                                keyHeightDp = keyHeightDp,
                                                keyRadiusDp = keyRadiusDp,
                                                modifier = Modifier.weight(1.3f),
                                                onKeyClick = onBackspace
                                            )
                                        }
                                        // Row 4 (Devanagari matras / signs)
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            rowBottom.forEach { k ->
                                                KeyboardKey(text = k, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(k) })
                                            }
                                        }
                                    } else {
                                        // QWERTY Layout (English & Nepinglish)
                                        // Row 1
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            qwertyRow1.forEachIndexed { idx, k ->
                                                val char = if (isShifted) k.uppercase() else k
                                                KeyboardKey(
                                                    text = char,
                                                    subText = numberRow[idx],
                                                    theme = theme,
                                                    keyHeightDp = keyHeightDp,
                                                    keyRadiusDp = keyRadiusDp,
                                                    modifier = Modifier.weight(1f),
                                                    onKeyClick = {
                                                        onKeyTyped(char)
                                                        if (isShifted) isShifted = false
                                                    },
                                                    onKeyLongClick = { onKeyTyped(numberRow[idx]) }
                                                )
                                            }
                                        }

                                        // Row 2
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                            Spacer(modifier = Modifier.weight(0.5f))
                                            qwertyRow2.forEachIndexed { idx, k ->
                                                val char = if (isShifted) k.uppercase() else k
                                                val symbol = qwertyRow2Symbols.getOrNull(idx)
                                                KeyboardKey(
                                                    text = char,
                                                    subText = symbol,
                                                    theme = theme,
                                                    keyHeightDp = keyHeightDp,
                                                    keyRadiusDp = keyRadiusDp,
                                                    modifier = Modifier.weight(1f),
                                                    onKeyClick = {
                                                        onKeyTyped(char)
                                                        if (isShifted) isShifted = false
                                                    },
                                                    onKeyLongClick = if (symbol != null) { { onKeyTyped(symbol) } } else null
                                                )
                                            }
                                            Spacer(modifier = Modifier.weight(0.5f))
                                        }

                                        // Row 3: Shift + Z-M + Backspace
                                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            KeyboardKey(
                                                icon = Icons.Default.ArrowUpward,
                                                theme = theme,
                                                isActionKey = true,
                                                isActive = isShifted,
                                                keyHeightDp = keyHeightDp,
                                                keyRadiusDp = keyRadiusDp,
                                                modifier = Modifier.weight(1.4f),
                                                onKeyClick = { isShifted = !isShifted }
                                            )
                                            qwertyRow3.forEachIndexed { idx, k ->
                                                val char = if (isShifted) k.uppercase() else k
                                                val symbol = qwertyRow3Symbols.getOrNull(idx)
                                                KeyboardKey(
                                                    text = char,
                                                    subText = symbol,
                                                    theme = theme,
                                                    keyHeightDp = keyHeightDp,
                                                    keyRadiusDp = keyRadiusDp,
                                                    modifier = Modifier.weight(1f),
                                                    onKeyClick = {
                                                        onKeyTyped(char)
                                                        if (isShifted) isShifted = false
                                                    },
                                                    onKeyLongClick = if (symbol != null) { { onKeyTyped(symbol) } } else null
                                                )
                                            }
                                            KeyboardKey(
                                                icon = Icons.AutoMirrored.Filled.Backspace,
                                                theme = theme,
                                                isActionKey = true,
                                                keyHeightDp = keyHeightDp,
                                                keyRadiusDp = keyRadiusDp,
                                                modifier = Modifier.weight(1.4f),
                                                onKeyClick = onBackspace
                                            )
                                        }
                                    }
                                }

                                KeyboardSubLayout.SYMBOLS -> {
                                    // Symbols layout 1
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        DevanagariLayouts.SYMBOLS_ROW_1.forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        DevanagariLayouts.SYMBOLS_ROW_2.forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        KeyboardKey(text = "=/<", theme = theme, isActionKey = true, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1.4f), onKeyClick = { subLayout = KeyboardSubLayout.MORE_SYMBOLS })
                                        DevanagariLayouts.SYMBOLS_ROW_3.forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                        KeyboardKey(icon = Icons.AutoMirrored.Filled.Backspace, theme = theme, isActionKey = true, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1.4f), onKeyClick = onBackspace)
                                    }
                                }

                                KeyboardSubLayout.MORE_SYMBOLS -> {
                                    // More Symbols layout
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\").forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        KeyboardKey(text = "?123", theme = theme, isActionKey = true, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1.4f), onKeyClick = { subLayout = KeyboardSubLayout.SYMBOLS })
                                        listOf("%", "©", "®", "™", "✓", "[", "]", "<", ">").forEach {
                                            KeyboardKey(text = it, theme = theme, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1f), onKeyClick = { onKeyTyped(it) })
                                        }
                                        KeyboardKey(icon = Icons.AutoMirrored.Filled.Backspace, theme = theme, isActionKey = true, keyHeightDp = keyHeightDp, keyRadiusDp = keyRadiusDp, modifier = Modifier.weight(1.4f), onKeyClick = onBackspace)
                                    }
                                }
                            }

                            // Bottom Navigation / Space / Action Row
                            BottomSpaceRow(
                                theme = theme,
                                subLayout = subLayout,
                                currentLanguage = currentLanguage,
                                editorAction = editorAction,
                                keyHeightDp = keyHeightDp,
                                keyRadiusDp = keyRadiusDp,
                                onSubLayoutToggle = {
                                    subLayout = if (subLayout == KeyboardSubLayout.ALPHA) KeyboardSubLayout.SYMBOLS else KeyboardSubLayout.ALPHA
                                },
                                onEmojiClick = { activePanel = ActivePanel.EMOJI },
                                onCommaClick = { onKeyTyped(",") },
                                onPeriodClick = { onKeyTyped(".") },
                                onSpaceClick = onSpace,
                                onCursorMove = onCursorMove,
                                onEnterClick = onEnter
                            )
                        }

                        // Animated Swipe Trail Overlay on top of keys
                        SwipeTrailOverlay(
                            points = swipePoints,
                            accentColor = theme.accentColor,
                            isSwiping = isSwiping,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
fun BottomSpaceRow(
    theme: KeyboardThemeColors,
    subLayout: KeyboardSubLayout,
    currentLanguage: LanguageMode,
    editorAction: Int,
    keyHeightDp: Int,
    keyRadiusDp: Int,
    onSubLayoutToggle: () -> Unit,
    onEmojiClick: () -> Unit,
    onCommaClick: () -> Unit,
    onPeriodClick: () -> Unit,
    onSpaceClick: () -> Unit,
    onCursorMove: (Int) -> Unit,
    onEnterClick: () -> Unit
) {
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    val actionIcon: ImageVector = when (editorAction and EditorInfo.IME_MASK_ACTION) {
        EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
        EditorInfo.IME_ACTION_SEND -> Icons.AutoMirrored.Filled.Send
        EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_NEXT -> Icons.AutoMirrored.Filled.KeyboardReturn
        EditorInfo.IME_ACTION_DONE -> Icons.Default.Check
        else -> Icons.AutoMirrored.Filled.KeyboardReturn
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ?123 / ABC switch
        KeyboardKey(
            text = if (subLayout == KeyboardSubLayout.ALPHA) "?123" else "ABC",
            theme = theme,
            isActionKey = true,
            keyHeightDp = keyHeightDp,
            keyRadiusDp = keyRadiusDp,
            modifier = Modifier.weight(1.2f),
            onKeyClick = onSubLayoutToggle
        )

        // Dedicated Emoji Key
        KeyboardKey(
            text = "😊",
            theme = theme,
            isActionKey = false,
            keyHeightDp = keyHeightDp,
            keyRadiusDp = keyRadiusDp,
            modifier = Modifier.weight(1.0f),
            onKeyClick = onEmojiClick
        )

        // Spacebar with swipe cursor gesture & language label
        Box(
            modifier = Modifier
                .weight(4.0f)
                .height(keyHeightDp.dp)
                .padding(horizontal = 2.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(keyRadiusDp.dp))
                .background(theme.keyBackground)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { _, dragAmount ->
                            dragAccumulator += dragAmount.x
                            if (dragAccumulator > 30f) {
                                onCursorMove(1) // Move cursor right
                                dragAccumulator = 0f
                            } else if (dragAccumulator < -30f) {
                                onCursorMove(-1) // Move cursor left
                                dragAccumulator = 0f
                            }
                        },
                        onDragEnd = { dragAccumulator = 0f }
                    )
                }
        ) {
            KeyboardKey(
                text = currentLanguage.displayName.uppercase(),
                theme = theme,
                keyHeightDp = keyHeightDp,
                keyRadiusDp = keyRadiusDp,
                modifier = Modifier.fillMaxWidth(),
                onKeyClick = onSpaceClick
            )
        }

        // Comma / Nepali sign
        KeyboardKey(
            text = if (currentLanguage == LanguageMode.NEPALI) "।" else ",",
            theme = theme,
            isActionKey = false,
            keyHeightDp = keyHeightDp,
            keyRadiusDp = keyRadiusDp,
            modifier = Modifier.weight(0.9f),
            onKeyClick = onCommaClick
        )

        // Period
        KeyboardKey(
            text = ".",
            theme = theme,
            keyHeightDp = keyHeightDp,
            keyRadiusDp = keyRadiusDp,
            modifier = Modifier.weight(0.9f),
            onKeyClick = onPeriodClick
        )

        // Enter / Action
        KeyboardKey(
            icon = actionIcon,
            theme = theme,
            isActive = true,
            keyHeightDp = keyHeightDp,
            keyRadiusDp = keyRadiusDp,
            modifier = Modifier.weight(1.3f),
            onKeyClick = onEnterClick
        )
    }
}
