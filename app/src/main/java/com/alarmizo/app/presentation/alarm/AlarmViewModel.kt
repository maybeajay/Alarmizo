package com.alarmizo.app.presentation.alarm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    fun setAlarmData(alarmId: Int, alarmLabel: String) {
        _uiState.value = _uiState.value.copy(
            alarmId = alarmId,
            alarmLabel = alarmLabel
        )
    }
}

data class AlarmUiState(
    val alarmId: Int = -1,
    val alarmLabel: String = "Alarm",
    val targetObject: String = "Water Bottle" // will come from Firebase later
)