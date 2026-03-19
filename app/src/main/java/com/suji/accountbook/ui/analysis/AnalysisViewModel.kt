package com.suji.accountbook.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suji.accountbook.data.local.dao.CategoryStatistic
import com.suji.accountbook.data.local.dao.DailyStatistic
import com.suji.accountbook.data.local.dao.MonthlyStatistic
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.data.repository.AccountBookRepository
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
import java.util.Calendar
import javax.inject.Inject

data class AnalysisUiState(
    val selectedAccountBookId: Long? = null,
    val selectedTimeRange: TimeRange = TimeRange.MONTH,
    val customStartTime: Long? = null,
    val customEndTime: Long? = null
)

enum class TimeRange {
    WEEK, MONTH, YEAR, CUSTOM
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountBookRepository: AccountBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    val accountBooks: StateFlow<List<AccountBookEntity>> = accountBookRepository
        .getAllAccountBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun getTimeRangeMillis(timeRange: TimeRange): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        return when (timeRange) {
            TimeRange.WEEK -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                Pair(calendar.timeInMillis, endTime)
            }
            TimeRange.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.timeInMillis, endTime)
            }
            TimeRange.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.timeInMillis, endTime)
            }
            TimeRange.CUSTOM -> {
                Pair(
                    _uiState.value.customStartTime ?: 0L,
                    _uiState.value.customEndTime ?: endTime
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenseCategoryStats: StateFlow<List<CategoryStatistic>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            val (startTime, endTime) = getTimeRangeMillis(state.selectedTimeRange)
            recordRepository.getCategoryStatistics(
                accountBookId = accountBookId,
                type = RecordType.EXPENSE,
                startTime = startTime,
                endTime = endTime
            )
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val incomeCategoryStats: StateFlow<List<CategoryStatistic>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            val (startTime, endTime) = getTimeRangeMillis(state.selectedTimeRange)
            recordRepository.getCategoryStatistics(
                accountBookId = accountBookId,
                type = RecordType.INCOME,
                startTime = startTime,
                endTime = endTime
            )
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyStats: StateFlow<List<MonthlyStatistic>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            val (startTime, endTime) = getTimeRangeMillis(state.selectedTimeRange)
            recordRepository.getMonthlyStatistics(
                accountBookId = accountBookId,
                startTime = startTime,
                endTime = endTime
            )
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyStats: StateFlow<List<DailyStatistic>> = _uiState.flatMapLatest { state ->
        val accountBookId = state.selectedAccountBookId
        if (accountBookId != null) {
            val (startTime, endTime) = getTimeRangeMillis(state.selectedTimeRange)
            recordRepository.getDailyStatistics(
                accountBookId = accountBookId,
                startTime = startTime,
                endTime = endTime
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

    fun selectAccountBook(id: Long) {
        _uiState.value = _uiState.value.copy(selectedAccountBookId = id)
    }

    fun selectTimeRange(timeRange: TimeRange) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = timeRange)
    }

    fun setCustomTimeRange(startTime: Long, endTime: Long) {
        _uiState.value = _uiState.value.copy(
            selectedTimeRange = TimeRange.CUSTOM,
            customStartTime = startTime,
            customEndTime = endTime
        )
    }
}
