package com.alarmizo.app.domain.usecase.alarm

import com.alarmizo.app.data.model.Alarm
import com.alarmizo.app.data.repository.AlarmRepository
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
){

    suspend operator fun invoke(alarm: Alarm){
        repository.updateAlarm(alarm)
    }
}