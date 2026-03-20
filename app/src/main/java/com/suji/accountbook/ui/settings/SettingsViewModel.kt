package com.suji.accountbook.ui.settings

import androidx.lifecycle.ViewModel
import com.suji.accountbook.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkModeFlow
    val isAutoRecordEnabled: StateFlow<Boolean> = preferencesManager.isAutoRecordEnabledFlow
    
    fun setDarkMode(enabled: Boolean) {
        preferencesManager.isDarkMode = enabled
    }
    
    fun setAutoRecordEnabled(enabled: Boolean) {
        preferencesManager.isAutoRecordEnabled = enabled
    }
}
