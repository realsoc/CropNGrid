package com.realsoc.cropngrid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

interface PreferencesRepository {
    fun getLogGranted(): Flow<Boolean>

    suspend fun setLogGranted(logGranted: Boolean)
}

class PreferencesRepositoryImpl( private val context: Context,
    coroutineScope: CoroutineScope
):
    PreferencesRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val KEY_LOG_GRANTED = booleanPreferencesKey("log_granted")

    private val _logGranted: SharedFlow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_LOG_GRANTED] ?: true
    }.shareIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    override fun getLogGranted(): Flow<Boolean> {
        return _logGranted
    }

    override suspend fun setLogGranted(logGranted: Boolean) {
        context.dataStore.edit { settings ->
            settings[KEY_LOG_GRANTED] = logGranted
        }
    }
}