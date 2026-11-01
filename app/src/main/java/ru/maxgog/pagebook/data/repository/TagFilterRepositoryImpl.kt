package ru.maxgog.pagebook.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.TagFilterModule
import ru.maxgog.pagebook.domain.model.TagFilterState
import ru.maxgog.pagebook.domain.repository.TagFilterRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagFilterRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TagFilterRepository {

    override fun getFilter(module: TagFilterModule): Flow<TagFilterState> =
        dataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { prefs ->
                val ids = prefs[stringPreferencesKey(idsKey(module))]
                    ?.split(',')
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.toSet()
                    ?: emptySet()
                val mode = prefs[stringPreferencesKey(modeKey(module))]
                    ?.let { runCatching { FilterMode.valueOf(it) }.getOrNull() }
                    ?: FilterMode.OR
                TagFilterState(tagIds = ids, mode = mode)
            }

    override suspend fun setFilter(module: TagFilterModule, state: TagFilterState) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(idsKey(module))] = state.tagIds.joinToString(",")
            prefs[stringPreferencesKey(modeKey(module))] = state.mode.name
        }
    }

    override suspend fun clearFilter(module: TagFilterModule) {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(idsKey(module)))
            prefs.remove(stringPreferencesKey(modeKey(module)))
        }
    }

    private fun idsKey(module: TagFilterModule) = "tag_filter_${module.name.lowercase()}_ids"
    private fun modeKey(module: TagFilterModule) = "tag_filter_${module.name.lowercase()}_mode"
}