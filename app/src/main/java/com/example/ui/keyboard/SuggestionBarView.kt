package com.example.ui.keyboard

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.LanguageMode
import com.example.engine.emoji.DetectedSentiment
import com.example.engine.prediction.SuggestionItem
import com.example.ui.theme.KeyboardThemeColors

@Composable
fun SuggestionBarView(
    suggestions: List<SuggestionItem>,
    currentLanguage: LanguageMode,
    theme: KeyboardThemeColors,
    modifier: Modifier = Modifier,
    sentiment: DetectedSentiment? = null,
    onEmojiSelect: ((String) -> Unit)? = null,
    onSuggestionClick: (SuggestionItem) -> Unit,
    onAiClick: () -> Unit,
    onClipboardClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onTtsClick: () -> Unit,
    onEmojiClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    // iOS-Style Quick Edit Actions
    onSelectAll: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onCut: (() -> Unit)? = null,
    onPaste: (() -> Unit)? = null
) {
    var showIosEditBar by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(theme.suggestionBarBackground)
    ) {
        // Dynamic Sentiment Emoji Strip
        DynamicSentimentEmojiStrip(
            sentiment = sentiment,
            theme = theme,
            onEmojiClick = { onEmojiSelect?.invoke(it) }
        )

        // iOS-style Blue Action Bar (toggleable or top toolbar)
        AnimatedVisibility(visible = showIosEditBar, enter = fadeIn(), exit = fadeOut()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF007AFF), Color(0xFF0A84FF), Color(0xFF5856D6))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IosBarItem(icon = Icons.Default.SelectAll, label = "Select All") {
                    onSelectAll?.invoke()
                    showIosEditBar = false
                }
                IosBarDivider()
                IosBarItem(icon = Icons.Default.ContentCopy, label = "Copy") {
                    onCopy?.invoke()
                    showIosEditBar = false
                }
                IosBarDivider()
                IosBarItem(icon = Icons.Default.ContentCut, label = "Cut") {
                    onCut?.invoke()
                    showIosEditBar = false
                }
                IosBarDivider()
                IosBarItem(icon = Icons.Default.ContentPaste, label = "Paste") {
                    onPaste?.invoke()
                    showIosEditBar = false
                }
                IosBarDivider()
                IosBarItem(icon = Icons.Default.AutoAwesome, label = "AI ✨") {
                    onAiClick()
                    showIosEditBar = false
                }
            }
        }

        // Top Toolbar: Quick Action Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Language Badge / Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLanguageClick() }
                    .background(theme.keyActionBackground.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = theme.accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = currentLanguage.badge,
                    color = theme.keyTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Tool Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // iOS Edit Bar toggle pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (showIosEditBar) Color(0xFF007AFF) else theme.keyActionBackground.copy(alpha = 0.5f))
                        .clickable { showIosEditBar = !showIosEditBar }
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Edit 📋",
                        fontSize = 11.sp,
                        color = if (showIosEditBar) Color.White else theme.keyTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // AI Sparkle Button with glow
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(theme.accentColor.copy(alpha = 0.22f))
                        .clickable { onAiClick() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Tools",
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onClipboardClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Clipboard",
                        tint = theme.keySubTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onEmojiClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = "Emojis",
                        tint = Color(0xFFFFB703),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = theme.keySubTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onTtsClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Text to Speech",
                        tint = theme.keySubTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = theme.keySubTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Suggestions Row: 3 items equally partitioned
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (suggestions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aayo Keyboard ✨ Smart Typing",
                        color = theme.keySubTextColor.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            } else {
                suggestions.take(3).forEachIndexed { index, suggestion ->
                    val isCenter = index == 1 || (suggestions.size == 1 && index == 0)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isCenter && suggestion.isAutocorrect)
                                    theme.accentColor.copy(alpha = 0.18f)
                                else if (isCenter)
                                    theme.keyActionBackground.copy(alpha = 0.6f)
                                else
                                    Color.Transparent
                            )
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = suggestion.displayText,
                                color = if (isCenter) theme.accentColor else theme.suggestionTextColor,
                                fontSize = if (suggestion.displayText.length > 8) 13.sp else 15.sp,
                                fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                            if (suggestion.secondaryHint != null) {
                                Text(
                                    text = suggestion.secondaryHint,
                                    color = theme.keySubTextColor,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Vertical divider between suggestions
                    if (index < suggestions.take(3).size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(theme.keySubTextColor.copy(alpha = 0.25f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IosBarItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IosBarDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(14.dp)
            .background(Color.White.copy(alpha = 0.35f))
    )
}
