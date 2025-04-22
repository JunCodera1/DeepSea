    package com.example.deepsea.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension để tạo DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_IS_FIRST_LOGIN = booleanPreferencesKey("is_first_login")
    }

    suspend fun saveFirstLoginStatus(isFirstLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_FIRST_LOGIN] = isFirstLogin
        }
    }


    suspend fun saveAuthToken(token: String, username: String, userId: Long, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USERNAME] = username
            preferences[KEY_USER_ID] = userId
            preferences[KEY_EMAIL] = email
        }
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME]
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val email: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMAIL]
    }

    val isFirstLogin: Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[KEY_IS_FIRST_LOGIN] ?: true }

    // Xóa dữ liệu khi đăng xuất
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_USERNAME)
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_EMAIL)
        }
    }
}