package com.suji.accountbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.data.repository.AccountBookRepository
import com.suji.accountbook.data.repository.CategoryInitializer
import com.suji.accountbook.data.repository.CategoryRepository
import com.suji.accountbook.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class RecordWithCategory(
    val record: RecordEntity,
    val category: CategoryEntity?
)

data class HomeUiState(
    val selectedAccountBookId: Long = -1,
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val monthStartTime: Long = getMonthStartTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)),
    val monthEndTime: Long = getMonthEndTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH))
)

private fun getMonthStartTime(year: Int, month: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getMonthEndTime(year: Int, month: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
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
    private val categoryRepository: CategoryRepository,
    private val categoryInitializer: CategoryInitializer
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val accountBooks: StateFlow<List<AccountBookEntity>> = accountBookRepository
        .getAllAccountBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recordsWithCategory: StateFlow<List<RecordWithCategory>> = _uiState.flatMapLatest { state ->
        flow {
            val accountBook = if (state.selectedAccountBookId == -1L) {
                accountBookRepository.getDefaultAccountBook()
            } else {
                accountBookRepository.getAccountBookById(state.selectedAccountBookId)
            }
            emit(accountBook)
        }.flatMapLatest { accountBook ->
            if (accountBook != null) {
                combine(
                    recordRepository.getRecordsByDateRange(
                        accountBookId = accountBook.id,
                        startTime = state.monthStartTime,
                        endTime = state.monthEndTime
                    ),
                    categoryRepository.getCategoriesByAccountBook(accountBook.id)
                ) { records, categories ->
                    val categoryMap = categories.associateBy { it.id }
                    records.map { record ->
                        RecordWithCategory(
                            record = record,
                            category = record.categoryId?.let { categoryMap[it] }
                        )
                    }
                }
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

    fun previousMonth() {
        val currentState = _uiState.value
        var newMonth = currentState.currentMonth - 1
        var newYear = currentState.currentYear
        
        if (newMonth < 0) {
            newMonth = 11
            newYear -= 1
        }
        
        _uiState.value = currentState.copy(
            currentYear = newYear,
            currentMonth = newMonth,
            monthStartTime = getMonthStartTime(newYear, newMonth),
            monthEndTime = getMonthEndTime(newYear, newMonth)
        )
    }

    fun nextMonth() {
        val currentState = _uiState.value
        var newMonth = currentState.currentMonth + 1
        var newYear = currentState.currentYear
        
        if (newMonth > 11) {
            newMonth = 0
            newYear += 1
        }
        
        _uiState.value = currentState.copy(
            currentYear = newYear,
            currentMonth = newMonth,
            monthStartTime = getMonthStartTime(newYear, newMonth),
            monthEndTime = getMonthEndTime(newYear, newMonth)
        )
    }

    fun goToCurrentMonth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        
        _uiState.value = _uiState.value.copy(
            currentYear = year,
            currentMonth = month,
            monthStartTime = getMonthStartTime(year, month),
            monthEndTime = getMonthEndTime(year, month)
        )
    }

    fun selectMonth(year: Int, month: Int) {
        _uiState.value = _uiState.value.copy(
            currentYear = year,
            currentMonth = month,
            monthStartTime = getMonthStartTime(year, month),
            monthEndTime = getMonthEndTime(year, month)
        )
    }
}
