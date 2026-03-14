package com.alarmizo.app.data.repository

import com.alarmizo.app.data.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun getAlarmById(id: Int): Alarm?
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
}