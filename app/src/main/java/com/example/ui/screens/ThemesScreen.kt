package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.KeyboardPreferences
import com.example.ui.theme.KeyboardThemeColors
import com.example.ui.theme.KeyboardThemes

@Composable
fun ThemesScreen(
    preferences: KeyboardPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedThemeId by remember { mutableStateOf(preferences.selectedThemeId) }
    var keyRadius by remember { mutableIntStateOf(preferences.keyRadiusDp) }
    var keyHeight by remember { mutableIntStateOf(preferences.keyHeightDp) }

    val currentTheme = remember(selectedThemeId) {
        KeyboardThemes.getThemeById(selectedThemeId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Keyboard Themes & Customization",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Select a curated theme or adjust key dimensions to fit your style.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Live Keyboard Theme Mini-Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = currentTheme.background),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preview: ${currentTheme.name}",
                        color = currentTheme.keyTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(currentTheme.accentColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Active", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Suggestion row preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentTheme.suggestionBarBackground)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("namaste", color = currentTheme.keySubTextColor, fontSize = 12.sp)
                    Text("नमस्ते", color = currentTheme.accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("k cha", color = currentTheme.keySubTextColor, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mock key row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { char ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(keyHeight.dp.coerceIn(36.dp, 56.dp))
                                .clip(RoundedCornerShape(keyRadius.dp))
                                .background(currentTheme.keyBackground)
                                .border(1.dp, currentTheme.keyBorderColor, RoundedCornerShape(keyRadius.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(char, color = currentTheme.keyTextColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Mock spacebar row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(keyHeight.dp.coerceIn(36.dp, 56.dp))
                            .clip(RoundedCornerShape(keyRadius.dp))
                            .background(currentTheme.keyActionBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?123", color = currentTheme.keyTextColor, fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .height(keyHeight.dp.coerceIn(36.dp, 56.dp))
                            .clip(RoundedCornerShape(keyRadius.dp))
                            .background(currentTheme.keyBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("NEPINGLISH", color = currentTheme.keySubTextColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(keyHeight.dp.coerceIn(36.dp, 56.dp))
                            .clip(RoundedCornerShape(keyRadius.dp))
                            .background(currentTheme.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Enter", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Theme Palette Selection
        Text(
            text = "CURATED THEMES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KeyboardThemes.ALL_PRESETS.forEach { theme ->
                val isSelected = theme.id == selectedThemeId
                Card(
                    onClick = {
                        selectedThemeId = theme.id
                        preferences.selectedThemeId = theme.id
                        Toast.makeText(context, "Theme set to ${theme.name}", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Theme Color Palette Swatch (3 circles)
                            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(theme.background))
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(theme.keyBackground))
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(theme.accentColor))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = theme.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Key Sizing Sliders
        Text(
            text = "CUSTOMIZE KEY DIMENSIONS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Key Radius
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Corner Rounding (Radius)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("${keyRadius}dp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = keyRadius.toFloat(),
                    onValueChange = {
                        keyRadius = it.toInt()
                        preferences.keyRadiusDp = keyRadius
                    },
                    valueRange = 2f..18f,
                    steps = 8
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Key Height
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Key Height", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("${keyHeight}dp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = keyHeight.toFloat(),
                    onValueChange = {
                        keyHeight = it.toInt()
                        preferences.keyHeightDp = keyHeight
                    },
                    valueRange = 40f..56f,
                    steps = 8
                )
            }
        }
    }
}
