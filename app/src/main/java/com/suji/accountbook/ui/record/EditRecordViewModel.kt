package com.suji.accountbook.ui.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val recordId: Long = savedStateHandle.get<Long>("recordId") ?: -1
}
