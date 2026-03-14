package com.alarmizo.app.data.mapper

import com.alarmizo.app.data.local.entity.AlarmEntity
import com.alarmizo.app.data.model.Alarm

fun AlarmEntity.toAlarm(): Alarm {
    return Alarm(
        id = id,
        label = label,
        hour = hour,
        minutes = minutes,
        isEnabled = isEnabled,
        objectChallenge = objectChallenge
    )
}

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        label = label,
        hour = hour,
        minutes = minutes,
        isEnabled = isEnabled,
        objectChallenge = objectChallenge
    )
}