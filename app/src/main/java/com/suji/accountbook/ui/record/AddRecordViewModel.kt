package com.suji.accountbook.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.CategoryType
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.data.repository.AccountBookRepository
import com.suji.accountbook.data.repository.CategoryRepository
import com.suji.accountbook.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddRecordUiState(
    val amount: String = "",
    val remark: String = "",
    val type: RecordType = RecordType.EXPENSE,
    val selectedCategoryId: Long? = null,
    val selectedAccountBookId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val accountBookRepository: AccountBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecordUiState())
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    val accountBooks: StateFlow<List<AccountBookEntity>> = accountBookRepository
        .getAllAccountBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<CategoryEntity>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            categoryRepository.getCategoriesByType(
                accountBookId = accountBookId,
                type = if (state.type == RecordType.EXPENSE) CategoryType.EXPENSE else CategoryType.INCOME
            )
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            val defaultBook = accountBookRepository.getDefaultAccountBook()
            if (defaultBook != null) {
                _uiState.value = _uiState.value.copy(selectedAccountBookId = defaultBook.id)
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun setRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun setType(type: RecordType) {
        _uiState.value = _uiState.value.copy(
            type = type,
            selectedCategoryId = null
        )
    }

    fun setCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun setAccountBook(accountBookId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedAccountBookId = accountBookId,
            selectedCategoryId = null
        )
    }

    fun setDate(date: Long) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun saveRecord() {
        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.amount.toDoubleOrNull()

            if (amount == null || amount <= 0) {
                _uiState.value = state.copy(error = "请输入有效金额")
                return@launch
            }

            if (state.selectedAccountBookId == null) {
                _uiState.value = state.copy(error = "请选择账本")
                return@launch
            }

            _uiState.value = state.copy(isSaving = true)

            try {
                val record = RecordEntity(
                    accountBookId = state.selectedAccountBookId,
                    categoryId = state.selectedCategoryId,
                    amount = amount,
                    type = state.type,
                    remark = state.remark,
                    date = state.date
                )

                recordRepository.insertRecord(record)
                _uiState.value = state.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "保存失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
