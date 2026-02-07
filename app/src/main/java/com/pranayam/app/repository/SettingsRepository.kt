package com.pranayam.app.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val MATCH_NOTIFICATIONS = booleanPreferencesKey("match_notifications")
        val MESSAGE_NOTIFICATIONS = booleanPreferencesKey("message_notifications")
        val SHOW_ONLINE_STATUS = booleanPreferencesKey("show_online_status")
        val SHOW_DISTANCE = booleanPreferencesKey("show_distance")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    val matchNotifications: Flow<Boolean> = context.dataStore.data.map { it[Keys.MATCH_NOTIFICATIONS] ?: true }
    val messageNotifications: Flow<Boolean> = context.dataStore.data.map { it[Keys.MESSAGE_NOTIFICATIONS] ?: true }
    val showOnlineStatus: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_ONLINE_STATUS] ?: true }
    val showDistance: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_DISTANCE] ?: true }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    suspend fun setNotificationsEnabled(value: Boolean) { context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = value } }
    suspend fun setMatchNotifications(value: Boolean) { context.dataStore.edit { it[Keys.MATCH_NOTIFICATIONS] = value } }
    suspend fun setMessageNotifications(value: Boolean) { context.dataStore.edit { it[Keys.MESSAGE_NOTIFICATIONS] = value } }
    suspend fun setShowOnlineStatus(value: Boolean) { context.dataStore.edit { it[Keys.SHOW_ONLINE_STATUS] = value } }
    suspend fun setShowDistance(value: Boolean) { context.dataStore.edit { it[Keys.SHOW_DISTANCE] = value } }
    suspend fun setDarkMode(value: Boolean) { context.dataStore.edit { it[Keys.DARK_MODE] = value } }
}
