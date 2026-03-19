package com.suji.accountbook.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.repository.AccountBookRepository
import com.suji.accountbook.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecordUiState(
    val selectedAccountBookId: Long? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountBookRepository: AccountBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    val accountBooks: StateFlow<List<AccountBookEntity>> = accountBookRepository
        .getAllAccountBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<RecordEntity>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            recordRepository.getRecordsByAccountBook(accountBookId)
        } else {
            flow {
                val book = accountBookRepository.getDefaultAccountBook()
                emit(book)
            }.flatMapLatest { book ->
                if (book != null) {
                    recordRepository.getRecordsByAccountBook(book.id)
                } else {
                    flowOf(emptyList())
                }
            }
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

    fun selectAccountBook(id: Long) {
        _uiState.value = _uiState.value.copy(selectedAccountBookId = id)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun deleteRecord(record: RecordEntity) {
        viewModelScope.launch {
            recordRepository.deleteRecord(record)
        }
    }
}
