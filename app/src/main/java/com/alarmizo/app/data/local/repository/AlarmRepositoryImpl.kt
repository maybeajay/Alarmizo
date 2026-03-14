package com.alarmizo.app.data.local.repository

import com.alarmizo.app.data.local.db.dao.AlarmDao
import com.alarmizo.app.data.repository.AlarmRepository
import javax.inject.Inject
import com.alarmizo.app.data.mapper.toAlarm
import com.alarmizo.app.data.mapper.toEntity
import com.alarmizo.app.data.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { entities ->
            entities.map { it.toAlarm() }
        }
    }

    override suspend fun getAlarmById(id: Int): Alarm? {
        return alarmDao.getAlarmById(id)?.toAlarm()
    }

    override suspend fun insertAlarm(alarm: Alarm):Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }
}