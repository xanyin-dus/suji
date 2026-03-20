package com.suji.accountbook.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class EditRecordUiState(
    val type: RecordType = RecordType.EXPENSE,
    val amount: String = "",
    val remark: String = "",
    val date: String = "",
    val categoryId: Long? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditRecordUiState())
    val uiState: StateFlow<EditRecordUiState> = _uiState.asStateFlow()

    private val _record = MutableStateFlow<RecordEntity?>(null)
    val record: StateFlow<RecordEntity?> = _record.asStateFlow()

    private var currentRecordId: Long = -1
    private var currentAccountBookId: Long = -1
    private var currentDateMillis: Long = System.currentTimeMillis()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun loadRecord(recordId: Long) {
        if (recordId <= 0) return
        
        currentRecordId = recordId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val record = recordRepository.getRecordById(recordId)
            if (record != null) {
                _record.value = record
                currentAccountBookId = record.accountBookId
                currentDateMillis = record.date
                
                _uiState.update {
                    it.copy(
                        type = record.type,
                        amount = record.amount.toString(),
                        remark = record.remark,
                        date = dateFormat.format(Date(record.date)),
                        categoryId = record.categoryId,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "记录不存在"
                    )
                }
            }
        }
    }

    fun setType(type: RecordType) {
        _uiState.update { it.copy(type = type) }
    }

    fun setAmount(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(amount = amount) }
        }
    }

    fun setRemark(remark: String) {
        _uiState.update { it.copy(remark = remark) }
    }

    fun saveRecord() {
        val amount = _uiState.value.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "请输入有效金额") }
            return
        }

        viewModelScope.launch {
            val currentRecord = _record.value
            if (currentRecord != null) {
                val updatedRecord = currentRecord.copy(
                    type = _uiState.value.type,
                    amount = amount,
                    remark = _uiState.value.remark,
                    categoryId = _uiState.value.categoryId,
                    updatedAt = System.currentTimeMillis()
                )
                
                recordRepository.updateRecord(updatedRecord)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun deleteRecord() {
        viewModelScope.launch {
            val currentRecord = _record.value
            if (currentRecord != null) {
                recordRepository.deleteRecord(currentRecord)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
