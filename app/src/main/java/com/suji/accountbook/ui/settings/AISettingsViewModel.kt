package com.suji.accountbook.ui.settings

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
class AISettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _apiKey = MutableStateFlow(preferencesManager.aiApiKey)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _apiEndpoint = MutableStateFlow(preferencesManager.aiApiEndpoint)
    val apiEndpoint: StateFlow<String> = _apiEndpoint.asStateFlow()

    private val _isApiKeyVisible = MutableStateFlow(false)
    val isApiKeyVisible: StateFlow<Boolean> = _isApiKeyVisible.asStateFlow()

    fun setApiKey(key: String) {
        viewModelScope.launch {
            preferencesManager.aiApiKey = key
            _apiKey.value = key
        }
    }

    fun setApiEndpoint(endpoint: String) {
        viewModelScope.launch {
            preferencesManager.aiApiEndpoint = endpoint
            _apiEndpoint.value = endpoint
        }
    }

    fun toggleApiKeyVisibility() {
        _isApiKeyVisible.value = !_isApiKeyVisible.value
    }
}
