package com.alarmizo.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmizo.app.data.model.Alarm
import com.alarmizo.app.domain.usecase.alarm.DeleteAlarmUseCase
import com.alarmizo.app.domain.usecase.alarm.GetAlarmsUseCase
import com.alarmizo.app.domain.usecase.alarm.InsertAlarmUseCase
import com.alarmizo.app.domain.usecase.alarm.UpdateAlarmUseCase
import com.alarmizo.app.utils.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val insertAlarmUseCase: InsertAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    init {
        loadAlarms()
    }

//    load
private fun loadAlarms() {
    viewModelScope.launch {
        getAlarmsUseCase().collect { alarms ->
            _uiState.value = _uiState.value.copy(alarms = alarms)
        }
    }
}

//    insert
    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val generatedId = insertAlarmUseCase(alarm)
            val scheduledAlarm = alarm.copy(id = generatedId.toInt())
            alarmScheduler.schedule(scheduledAlarm)
        }
    }

    // 6. delete
    fun deleteAlarm(alarm: Alarm) {
        // similar to insert
        viewModelScope.launch {
            deleteAlarmUseCase(alarm);
            alarmScheduler.cancel(alarm);
        }

    }

    // 7. update
    fun updateAlarm(alarm: Alarm) {
        // similar to insert
        viewModelScope.launch {
            updateAlarmUseCase(alarm);
            if (alarm.isEnabled) {
                alarmScheduler.schedule(alarm)
            } else {
                alarmScheduler.cancel(alarm)
            }
        }
    }



}


data class HomeUiState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)