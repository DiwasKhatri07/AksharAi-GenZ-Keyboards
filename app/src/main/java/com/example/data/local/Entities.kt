package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_entries")
data class ClipboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "user_dictionary")
data class UserWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val shortcut: String? = null,
    val language: String = "nepinglish", // nepali, nepinglish, english
    val frequency: Int = 1
)

@Entity(tableName = "saved_phrases")
data class SavedPhraseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val phrase: String,
    val nepaliTranslation: String = "",
    val isGenZ: Boolean = true
)

@Entity(tableName = "custom_themes")
data class CustomThemeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val backgroundColorHex: String,
    val keyColorHex: String,
    val keyTextColorHex: String,
    val accentColorHex: String,
    val keyRadiusDp: Int = 8,
    val backgroundImageUri: String? = null
)

@Entity(tableName = "ai_history")
data class AiHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val mode: String,
    val resultText: String,
    val timestamp: Long = System.currentTimeMillis()
)
