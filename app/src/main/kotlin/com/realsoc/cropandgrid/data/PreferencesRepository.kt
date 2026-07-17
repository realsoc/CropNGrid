package com.realsoc.cropandgrid.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import java.io.IOException
import javax.inject.Inject

interface PreferencesRepository {
    fun getLogGranted(): Flow<Boolean>

    suspend fun setLogGranted(logGranted: Boolean)
}

// Top-level so a second repository instance cannot create a second DataStore for the same file
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    coroutineScope: CoroutineScope
):
    PreferencesRepository {

    private val KEY_LOG_GRANTED = booleanPreferencesKey("log_granted")

    private val _logGranted: SharedFlow<Boolean> = context.dataStore.data
        .catch { throwable ->
            // An unreadable preferences file would otherwise kill the app-wide sharing scope
            if (throwable is IOException) {
                Log.e("CropNGrid", "Failed to read preferences", throwable)
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
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