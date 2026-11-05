package ru.maxgog.pagebook.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.domain.repository.RecentEmojiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentEmojiRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : RecentEmojiRepository {

    override fun getRecentEmojis(): Flow<List<String>> = dataStore.data.map { prefs ->
        decode(prefs[RECENT_EMOJIS_KEY])
    }

    override suspend fun addRecentEmoji(emoji: String) {
        if (emoji.isBlank()) return
        dataStore.edit { prefs ->
            val current = decode(prefs[RECENT_EMOJIS_KEY])
            val updated = (listOf(emoji) + current.filter { it != emoji }).take(MAX_RECENT)
            prefs[RECENT_EMOJIS_KEY] = updated.joinToString(DELIMITER)
        }
    }

    private fun decode(raw: String?): List<String> =
        raw?.split(DELIMITER)?.filter { it.isNotBlank() } ?: emptyList()

    companion object {
        private const val DELIMITER = "|"
        private const val MAX_RECENT = 20
        private val RECENT_EMOJIS_KEY = stringPreferencesKey("recent_emojis")
    }
}