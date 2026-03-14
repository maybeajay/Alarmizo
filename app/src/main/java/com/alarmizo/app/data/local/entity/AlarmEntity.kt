package com.alarmizo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String?,
    val hour: Int = 0,
    val minutes: Int = 0,
    val isEnabled: Boolean = false,
    val objectChallenge: String = ""
)