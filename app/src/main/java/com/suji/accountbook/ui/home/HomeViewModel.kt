package com.suji.accountbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.data.repository.AccountBookRepository
import com.suji.accountbook.data.repository.CategoryInitializer
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
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.map
data class HomeUiState(
    val selectedAccountBookId: Long = -1,
    val monthStartTime: Long = getMonthStartTime(),
    val monthEndTime: Long = getMonthEndTime()
)

private fun getMonthStartTime(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getMonthEndTime(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountBookRepository: AccountBookRepository,
    private val recordRepository: RecordRepository,
    private val categoryInitializer: CategoryInitializer
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val accountBooks: StateFlow<List<AccountBookEntity>> = accountBookRepository
        .getAllAccountBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<RecordEntity>> = _uiState.flatMapLatest { state ->
        flow {
            val accountBook = if (state.selectedAccountBookId == -1L) {
                accountBookRepository.getDefaultAccountBook()
            } else {
                accountBookRepository.getAccountBookById(state.selectedAccountBookId)
            }
            emit(accountBook)
        }.flatMapLatest { accountBook ->
            if (accountBook != null) {
                recordRepository.getRecordsByDateRange(
                    accountBookId = accountBook.id,
                    startTime = state.monthStartTime,
                    endTime = state.monthEndTime
                )
            } else {
                flowOf(emptyList())
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalIncome: StateFlow<Double> = _uiState.flatMapLatest { state ->
        flow {
            val book = if (state.selectedAccountBookId == -1L) {
                accountBookRepository.getDefaultAccountBook()
            } else {
                accountBookRepository.getAccountBookById(state.selectedAccountBookId)
            }
            emit(book)
        }.flatMapLatest { book ->
            if (book != null) {
                // 👇 这里加了 .map { it ?: 0.0 }
                recordRepository.getTotalByType(
                    accountBookId = book.id,
                    type = RecordType.INCOME,
                    startTime = state.monthStartTime,
                    endTime = state.monthEndTime
                ).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalExpense: StateFlow<Double> = _uiState.flatMapLatest { state ->
        flow {
            val book = if (state.selectedAccountBookId == -1L) {
                accountBookRepository.getDefaultAccountBook()
            } else {
                accountBookRepository.getAccountBookById(state.selectedAccountBookId)
            }
            emit(book)
        }.flatMapLatest { book ->
            if (book != null) {
                // 👇 这里也加了 .map { it ?: 0.0 }
                recordRepository.getTotalByType(
                    accountBookId = book.id,
                    type = RecordType.EXPENSE,
                    startTime = state.monthStartTime,
                    endTime = state.monthEndTime
                ).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        viewModelScope.launch {
            val defaultBook = accountBookRepository.getDefaultAccountBook()
            if (defaultBook == null) {
                val newBookId = accountBookRepository.insertAccountBook(
                    AccountBookEntity(
                        name = "默认账本",
                        isDefault = true
                    )
                )
                categoryInitializer.initializeDefaultCategories(newBookId)
            } else {
                categoryInitializer.initializeDefaultCategories(defaultBook.id)
            }
        }
    }

    fun selectAccountBook(id: Long) {
        _uiState.value = _uiState.value.copy(selectedAccountBookId = id)
    }
}
