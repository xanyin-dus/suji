package com.suji.accountbook.ui.theme

import androidx.lifecycle.ViewModel
import com.suji.accountbook.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkModeFlow
    
    fun setDarkMode(enabled: Boolean) {
        preferencesManager.isDarkMode = enabled
    }
    
    fun toggleDarkMode() {
        setDarkMode(!isDarkMode.value)
    }
}
