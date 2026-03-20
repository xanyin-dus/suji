package com.suji.accountbook.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.service.AIService
import com.suji.accountbook.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TestResult(
    val isSuccess: Boolean,
    val message: String
)

@HiltViewModel
class AISettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val aiService: AIService
) : ViewModel() {

    private val _apiKey = MutableStateFlow(preferencesManager.aiApiKey)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _apiEndpoint = MutableStateFlow(preferencesManager.aiApiEndpoint)
    val apiEndpoint: StateFlow<String> = _apiEndpoint.asStateFlow()

    private val _isApiKeyVisible = MutableStateFlow(false)
    val isApiKeyVisible: StateFlow<Boolean> = _isApiKeyVisible.asStateFlow()

    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult.asStateFlow()

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting.asStateFlow()

    fun setApiKey(key: String) {
        preferencesManager.aiApiKey = key
        _apiKey.value = key
        _testResult.value = null
    }

    fun setApiEndpoint(endpoint: String) {
        preferencesManager.aiApiEndpoint = endpoint
        _apiEndpoint.value = endpoint
        _testResult.value = null
    }

    fun toggleApiKeyVisibility() {
        _isApiKeyVisible.value = !_isApiKeyVisible.value
    }

    fun testConnection() {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null

            val result = withContext(Dispatchers.IO) {
                val testPrompt = "你好"
                aiService.analyzeWithCustomPrompt(
                    apiKey = _apiKey.value,
                    apiEndpoint = _apiEndpoint.value,
                    prompt = testPrompt,
                    data = ""
                )
            }

            _testResult.value = if (result.isSuccess) {
                TestResult(isSuccess = true, message = "连接成功！AI服务配置正确。")
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "未知错误"
                TestResult(isSuccess = false, message = "连接失败：$errorMessage")
            }

            _isTesting.value = false
        }
    }
}
