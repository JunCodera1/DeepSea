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
import timber.log.Timber

// Extension to create DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_PROFILE_ID = longPreferencesKey("profile_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_IS_FIRST_LOGIN = booleanPreferencesKey("is_first_login")
        private val KEY_TOKEN_TIMESTAMP = longPreferencesKey("token_timestamp")
        private val IS_PREMIUM_KEY = booleanPreferencesKey("is_premium")
    }

    suspend fun saveFirstLoginStatus(isFirstLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_FIRST_LOGIN] = isFirstLogin
        }
        Timber.d("Saved first login status: $isFirstLogin")
    }

    suspend fun saveAuthToken(token: String, username: String, userId: Long, profileId: Long, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USERNAME] = username
            preferences[KEY_USER_ID] = userId
            preferences[KEY_EMAIL] = email
            preferences[KEY_PROFILE_ID] = profileId
            preferences[KEY_TOKEN_TIMESTAMP] = System.currentTimeMillis()
        }
        Timber.d("Saved auth token for user: $username, email: $email")
    }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PREMIUM_KEY] = isPremium
        }
        Timber.d("Set premium status: $isPremium")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    val isPremium: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PREMIUM_KEY] ?: false
    }

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME]
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val profileId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[KEY_PROFILE_ID]
    }

    val email: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMAIL]
    }

    val isFirstLogin: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_FIRST_LOGIN] ?: true
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
        Timber.d("Cleared all auth data from DataStore")
    }

    suspend fun isSessionValid(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        val token = preferences[KEY_TOKEN]
        val timestamp = preferences[KEY_TOKEN_TIMESTAMP] ?: 0L
        val expiryDuration = 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
        val isValid = token != null && (System.currentTimeMillis() - timestamp < expiryDuration)
        Timber.d("Session valid: $isValid, token: $token, timestamp: $timestamp")
        isValid
    }
}