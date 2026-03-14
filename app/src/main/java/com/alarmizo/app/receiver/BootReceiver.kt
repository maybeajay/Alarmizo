package com.alarmizo.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver: BroadcastReceiver(){
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // reschedule all alarms from Room DB
        }
    }

}