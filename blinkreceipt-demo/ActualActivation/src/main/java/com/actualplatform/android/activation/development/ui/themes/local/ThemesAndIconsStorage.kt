package com.actualplatform.android.activation.development.ui.themes.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.actualplatform.activation.logging.Logger
import com.actualplatform.activation.theming.ActivationTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Manages the persistence and retrieval of [ActivationTheme] settings.
 *
 * This storage handler uses [DataStore] to serialize theme data into JSON format. If no [dataStore]
 * is provided, the class operates in an "in-memory" mode, returning default values and ignoring
 * save/clear requests.
 *
 * @property dataStore An optional [DataStore] instance used to persist preferences. If null,
 *   persistence is disabled.
 */
internal class ThemesAndIconsStorage(private val dataStore: DataStore<Preferences>? = null) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    init {
        Logger.d(TAG) {
            val persisted = dataStore != null
            "initialized (persistence=${if (persisted) "DataStore" else "in-memory"})"
        }
    }

    fun themes(): Flow<ActivationTheme> {
        if (dataStore == null) {
            Logger.d(TAG) { "themes: no DataStore, returning default theme" }
            return flowOf(ActivationTheme())
        }
        return dataStore.data
            .map { prefs -> prefs[KEY_ACTIVATION_THEME]?.decodeThemeOrNull() ?: ActivationTheme() }
            .distinctUntilChanged()
    }

    suspend fun save(theme: ActivationTheme) {
        if (dataStore == null) {
            Logger.d(TAG) { "save: no DataStore, no-op" }
            return
        }
        dataStore.edit { prefs -> prefs[KEY_ACTIVATION_THEME] = json.encodeToString(theme.toLocal()) }
        Logger.d(TAG) { "save: persisted theme" }
    }

    suspend fun clear() {
        if (dataStore == null) {
            Logger.d(TAG) { "clear: no DataStore, no-op" }
            return
        }
        dataStore.edit { it.clear() }
        Logger.d(TAG) { "clear: removed all entries" }
    }

    private fun String.decodeThemeOrNull(): ActivationTheme? =
        runCatching { json.decodeFromString<com.actualplatform.android.activation.development.ui.themes.local.ActivationTheme>(this).toModel() }
            .onFailure { e ->
                Logger.d(TAG) { "decodeThemeOrNull: failed to decode theme JSON — $e" }
            }
            .getOrNull()

    private companion object {
        private const val TAG = "ThemesAndIconsStorage"
        private val KEY_ACTIVATION_THEME = stringPreferencesKey("activation_theme")
    }
}
