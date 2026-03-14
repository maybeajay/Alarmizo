package com.alarmizo.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alarmizo.app.data.local.db.dao.AlarmDao
import com.alarmizo.app.data.local.entity.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1,
    exportSchema = false
)

abstract class AlarmDatabase: RoomDatabase(){
    abstract fun alarmDao(): AlarmDao
}