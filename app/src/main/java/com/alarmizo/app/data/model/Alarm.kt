package com.alarmizo.app.data.model

data class Alarm(
    val id: Int = 0,
    val label: String?,
    val hour: Int,
    val minutes: Int,
    val isEnabled: Boolean,
    val objectChallenge: String
)