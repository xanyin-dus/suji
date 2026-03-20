package com.suji.accountbook.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isDarkModeFlow = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, false))
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow.asStateFlow()
    
    private val _isAutoRecordEnabledFlow = MutableStateFlow(prefs.getBoolean(KEY_AUTO_RECORD, false))
    val isAutoRecordEnabledFlow: StateFlow<Boolean> = _isAutoRecordEnabledFlow.asStateFlow()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
            _isDarkModeFlow.value = value
        }

    var isAutoRecordEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RECORD, false)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_RECORD, value).apply()
            _isAutoRecordEnabledFlow.value = value
        }

    var aiApiKey: String
        get() = prefs.getString(KEY_AI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_AI_API_KEY, value).apply()

    var aiApiEndpoint: String
        get() = prefs.getString(KEY_AI_API_ENDPOINT, DEFAULT_API_ENDPOINT) ?: DEFAULT_API_ENDPOINT
        set(value) = prefs.edit().putString(KEY_AI_API_ENDPOINT, value).apply()

    var selectedAccountBookId: Long
        get() = prefs.getLong(KEY_SELECTED_ACCOUNT_BOOK, -1)
        set(value) = prefs.edit().putLong(KEY_SELECTED_ACCOUNT_BOOK, value).apply()

    var lastBackupTime: Long
        get() = prefs.getLong(KEY_LAST_BACKUP, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_BACKUP, value).apply()

    companion object {
        private const val PREFS_NAME = "suji_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_AUTO_RECORD = "auto_record"
        private const val KEY_AI_API_KEY = "ai_api_key"
        private const val KEY_AI_API_ENDPOINT = "ai_api_endpoint"
        private const val KEY_SELECTED_ACCOUNT_BOOK = "selected_account_book"
        private const val KEY_LAST_BACKUP = "last_backup"
        private const val DEFAULT_API_ENDPOINT = "https://api.openai.com"
    }
}
