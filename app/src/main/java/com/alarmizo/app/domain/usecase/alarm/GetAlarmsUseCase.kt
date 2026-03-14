package com.alarmizo.app.domain.usecase.alarm

import com.alarmizo.app.data.model.Alarm
import com.alarmizo.app.data.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsUseCase @Inject constructor (
    private val repository: AlarmRepository
){
    operator fun invoke(): Flow<List<Alarm>> {
        return  repository.getAllAlarms();
    }
}