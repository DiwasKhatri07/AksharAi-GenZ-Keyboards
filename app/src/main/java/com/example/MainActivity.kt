package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.ai.AiManager
import com.example.data.local.AppDatabase
import com.example.data.preferences.KeyboardPreferences
import com.example.ui.screens.AboutPrivacyScreen
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.ClipboardPhrasesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.screens.TypingSettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferences: KeyboardPreferences
    private lateinit var database: AppDatabase
    private lateinit var aiManager: AiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = KeyboardPreferences(this)
        database = AppDatabase.getInstance(this)
        aiManager = AiManager(this)

        setContent {
            MyApplicationTheme {
                MainAppScreen(
                    preferences = preferences,
                    database = database,
                    aiManager = aiManager
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    preferences: KeyboardPreferences,
    database: AppDatabase,
    aiManager: AiManager
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // If on a secondary tab, Back button navigates back to Home tab
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Palette, contentDescription = "Themes") },
                    label = { Text("Themes", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_themes")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Keyboard, contentDescription = "Typing") },
                    label = { Text("Typing", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_typing")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.ContentPaste, contentDescription = "Clips") },
                    label = { Text("Clips", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_clips")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
                    label = { Text("AI", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_ai")
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_about")
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selectedTab) {
            0 -> HomeScreen(
                preferences = preferences,
                database = database,
                aiManager = aiManager,
                onNavigateToThemes = { selectedTab = 1 },
                onNavigateToAi = { selectedTab = 4 },
                modifier = modifier
            )
            1 -> ThemesScreen(preferences = preferences, modifier = modifier)
            2 -> TypingSettingsScreen(preferences = preferences, modifier = modifier)
            3 -> ClipboardPhrasesScreen(database = database, preferences = preferences, modifier = modifier)
            4 -> AiStudioScreen(aiManager = aiManager, preferences = preferences, modifier = modifier)
            5 -> AboutPrivacyScreen(modifier = modifier)
        }
    }
}
