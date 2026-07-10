package com.actualplatform.android.activation.development.ui.themes.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.actualplatform.activation.logging.Logger
import com.actualplatform.activation.theming.ActivationAppearance
import com.actualplatform.activation.theming.ActivationTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Manages the persistence and retrieval of [ActivationTheme] and [ActivationAppearance] settings.
 *
 * This storage handler uses [DataStore] to serialize theme and appearance data into JSON format. If
 * no [dataStore] is provided, the class operates in an "in-memory" mode, returning default values
 * and ignoring save/clear requests.
 *
 * @property dataStore An optional [DataStore] instance used to persist preferences. If null,
 *   persistence is disabled.
 */
/**
 * The exact [Json] configuration the storage persists with — file-level so tests can encode/decode
 * fixtures against the production configuration.
 */
internal val themeStorageJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    isLenient = true
}

internal class ThemesAndAppearanceStorage(private val dataStore: DataStore<Preferences>? = null) {
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

    fun appearance(): Flow<ActivationAppearance> {
        if (dataStore == null) {
            Logger.d(TAG) { "appearance: no DataStore, returning default appearance" }
            return flowOf(ActivationAppearance())
        }
        return dataStore.data
            .map { prefs -> prefs[KEY_ACTIVATION_APPEARANCE]?.decodeAppearanceOrNull() ?: ActivationAppearance() }
            .distinctUntilChanged()
    }

    suspend fun save(theme: ActivationTheme) {
        if (dataStore == null) {
            Logger.d(TAG) { "save: no DataStore, no-op" }
            return
        }
        dataStore.edit { prefs -> prefs[KEY_ACTIVATION_THEME] = themeStorageJson.encodeToString(theme.toLocal()) }
        Logger.d(TAG) { "save: persisted theme" }
    }

    suspend fun save(appearance: ActivationAppearance) {
        if (dataStore == null) {
            Logger.d(TAG) { "save: no DataStore, no-op" }
            return
        }
        dataStore.edit { prefs -> prefs[KEY_ACTIVATION_APPEARANCE] = themeStorageJson.encodeToString(appearance.toLocal()) }
        Logger.d(TAG) { "save: persisted appearance" }
    }

    suspend fun clear() {
        if (dataStore == null) {
            Logger.d(TAG) { "clear: no DataStore, no-op" }
            return
        }
        dataStore.edit { it.clear() }
        Logger.d(TAG) { "clear: removed all entries" }
    }

    suspend fun clearTheme() {
        if (dataStore == null) {
            Logger.d(TAG) { "clearTheme: no DataStore, no-op" }
            return
        }
        dataStore.edit { it.remove(KEY_ACTIVATION_THEME) }
        Logger.d(TAG) { "clearTheme: removed theme entry" }
    }

    suspend fun clearAppearance() {
        if (dataStore == null) {
            Logger.d(TAG) { "clearAppearance: no DataStore, no-op" }
            return
        }
        dataStore.edit { it.remove(KEY_ACTIVATION_APPEARANCE) }
        Logger.d(TAG) { "clearAppearance: removed appearance entry" }
    }

    private fun String.decodeThemeOrNull(): ActivationTheme? =
        runCatching { themeStorageJson.decodeFromString<com.actualplatform.android.activation.development.ui.themes.local.ActivationTheme>(this).toModel() }
            .onFailure { e ->
                Logger.d(TAG) { "decodeThemeOrNull: failed to decode theme JSON — $e" }
            }
            .getOrNull()

    private fun String.decodeAppearanceOrNull(): ActivationAppearance? =
        runCatching { themeStorageJson.decodeFromString<com.actualplatform.android.activation.development.ui.themes.local.ActivationAppearance>(this).toModel() }
            .onFailure { e ->
                Logger.d(TAG) { "decodeAppearanceOrNull: failed to decode appearance JSON — $e" }
            }
            .getOrNull()

    private companion object {
        private const val TAG = "ThemesAndAppearanceStorage"
        private val KEY_ACTIVATION_THEME = stringPreferencesKey("activation_theme")
        private val KEY_ACTIVATION_APPEARANCE = stringPreferencesKey("activation_appearance")
    }
}
