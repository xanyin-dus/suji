package com.suji.accountbook.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _isDarkMode = MutableStateFlow(preferencesManager.isDarkMode)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    init {
        viewModelScope.launch {
            _isDarkMode.value = preferencesManager.isDarkMode
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.isDarkMode = enabled
            _isDarkMode.value = enabled
        }
    }
    
    fun toggleDarkMode() {
        setDarkMode(!_isDarkMode.value)
    }
}
