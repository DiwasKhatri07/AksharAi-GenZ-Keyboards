package com.example.ui.keyboard

import android.media.AudioManager
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeyboardThemeColors

@Composable
fun KeyboardKey(
    text: String? = null,
    icon: ImageVector? = null,
    subText: String? = null,
    theme: KeyboardThemeColors,
    modifier: Modifier = Modifier,
    isActionKey: Boolean = false,
    isActive: Boolean = false,
    keyHeightDp: Int = 46,
    keyRadiusDp: Int = 8,
    soundType: Int = AudioManager.FX_KEYPRESS_STANDARD,
    onKeyClick: () -> Unit,
    onKeyLongClick: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "key_scale"
    )

    val bg = when {
        isActive -> theme.accentColor
        isActionKey -> theme.keyActionBackground
        else -> theme.keyBackground
    }

    val fg = when {
        isActive -> Color.White
        isActionKey -> theme.keyTextColor
        else -> theme.keyTextColor
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.5.dp, vertical = 3.dp)
            .height(keyHeightDp.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 1.dp else 2.dp,
                shape = RoundedCornerShape(keyRadiusDp.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(keyRadiusDp.dp))
            .background(bg)
            .border(
                width = if (theme.keyBorderColor != Color.Transparent) 1.dp else 0.dp,
                color = theme.keyBorderColor,
                shape = RoundedCornerShape(keyRadiusDp.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        try {
                            val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager
                            audioManager?.playSoundEffect(soundType, 0.7f)
                        } catch (_: Exception) {}
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onKeyClick() },
                    onLongPress = {
                        if (onKeyLongClick != null) {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            onKeyLongClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Subtext in top-right corner for long-press alternative
        if (subText != null) {
            Text(
                text = subText,
                color = theme.keySubTextColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp)
            )
        }

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text ?: "key",
                tint = fg,
                modifier = Modifier.size(20.dp)
            )
        } else if (text != null) {
            Text(
                text = text,
                color = fg,
                fontSize = if (text.length > 2) 13.sp else 18.sp,
                fontWeight = if (isActionKey || isActive) FontWeight.SemiBold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
