package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.local.SavedPhraseEntity
import com.example.data.local.UserWordEntity
import com.example.data.preferences.ClipboardRetention
import com.example.data.preferences.KeyboardPreferences
import com.example.engine.nepinglish.GenZSlangDictionary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AayoKeyboardApp : Application() {

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this)
        val preferences = KeyboardPreferences(this)

        // Seed initial Gen-Z phrases and user dictionary
        appScope.launch {
            if (database.savedPhraseDao().getCount() == 0) {
                val seedPhrases = GenZSlangDictionary.PRESET_PHRASES.map { phrase ->
                    SavedPhraseEntity(
                        category = "Popular Gen-Z",
                        phrase = phrase,
                        isGenZ = true
                    )
                }
                database.savedPhraseDao().insertAll(seedPhrases)

                // Seed popular custom words
                database.userWordDao().insert(UserWordEntity(word = "Diwas", shortcut = "dw"))
                database.userWordDao().insert(UserWordEntity(word = "Namaste", shortcut = "nm"))
                database.userWordDao().insert(UserWordEntity(word = "Kathmandu", shortcut = "ktm"))
                database.userWordDao().insert(UserWordEntity(word = "Pokhara", shortcut = "pkr"))
            }

            // Clean up old clipboard items if retention policy is set
            val retention = preferences.clipboardRetention
            if (retention != ClipboardRetention.NEVER) {
                val threshold = System.currentTimeMillis() - (retention.hours * 60 * 60 * 1000)
                database.clipboardDao().deleteOlderThan(threshold)
            }
        }
    }
}
