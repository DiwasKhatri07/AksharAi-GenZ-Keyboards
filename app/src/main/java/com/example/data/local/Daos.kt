package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_entries ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClips(): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_entries ORDER BY isPinned DESC, timestamp DESC LIMIT :limit")
    fun getRecentClips(limit: Int): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_entries WHERE text = :text LIMIT 1")
    suspend fun findByText(text: String): ClipboardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clip: ClipboardEntity): Long

    @Update
    suspend fun update(clip: ClipboardEntity)

    @Delete
    suspend fun delete(clip: ClipboardEntity)

    @Query("DELETE FROM clipboard_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_entries WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("DELETE FROM clipboard_entries")
    suspend fun clearAll()

    @Query("DELETE FROM clipboard_entries WHERE isPinned = 0 AND timestamp < :olderThanTimestamp")
    suspend fun deleteOlderThan(olderThanTimestamp: Long)
}

@Dao
interface UserWordDao {
    @Query("SELECT * FROM user_dictionary ORDER BY frequency DESC, word ASC")
    fun getAllWords(): Flow<List<UserWordEntity>>

    @Query("SELECT * FROM user_dictionary WHERE word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT 10")
    suspend fun findWordsByPrefix(prefix: String): List<UserWordEntity>

    @Query("SELECT * FROM user_dictionary WHERE shortcut = :shortcut LIMIT 3")
    suspend fun findByShortcut(shortcut: String): List<UserWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: UserWordEntity): Long

    @Delete
    suspend fun delete(word: UserWordEntity)

    @Query("DELETE FROM user_dictionary WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SavedPhraseDao {
    @Query("SELECT * FROM saved_phrases ORDER BY category ASC, id ASC")
    fun getAllPhrases(): Flow<List<SavedPhraseEntity>>

    @Query("SELECT * FROM saved_phrases WHERE category = :category ORDER BY id ASC")
    fun getPhrasesByCategory(category: String): Flow<List<SavedPhraseEntity>>

    @Query("SELECT COUNT(*) FROM saved_phrases")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phrase: SavedPhraseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<SavedPhraseEntity>)

    @Delete
    suspend fun delete(phrase: SavedPhraseEntity)
}

@Dao
interface CustomThemeDao {
    @Query("SELECT * FROM custom_themes ORDER BY id DESC")
    fun getAllThemes(): Flow<List<CustomThemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(theme: CustomThemeEntity): Long

    @Delete
    suspend fun delete(theme: CustomThemeEntity)
}

@Dao
interface AiHistoryDao {
    @Query("SELECT * FROM ai_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): Flow<List<AiHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: AiHistoryEntity): Long

    @Query("DELETE FROM ai_history")
    suspend fun clearAll()
}
