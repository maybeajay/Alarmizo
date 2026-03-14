package com.alarmizo.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.alarmizo.app.MainActivity
import com.alarmizo.app.R
import androidx.core.content.edit

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_ALARM_ID = "ALARM_ID"
        const val EXTRA_ALARM_LABEL = "ALARM_LABEL"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getIntExtra(EXTRA_ALARM_ID, -1) ?: -1
        val alarmLabel = intent?.getStringExtra(EXTRA_ALARM_LABEL) ?: "Alarm"

        // save to prefs FIRST before anything else
        getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE).edit()
            .putInt("active_alarm_id", alarmId)
            .putString("active_alarm_label", alarmLabel)
            .apply()

        acquireWakeLock()

        // ✅ use correct foreground service type for Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                buildNotification(alarmId, alarmLabel),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, buildNotification(alarmId, alarmLabel))
        }

        playAlarmSound()
//        launchAlarmScreen(alarmId, alarmLabel)

        return START_NOT_STICKY
    }
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Alarmizo::AlarmWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // max 10 minutes
        }
    }

    private fun playAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, alarmUri)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            isLooping = true  // keeps ringing until dismissed
            prepare()
            start()
        }
    }

    private fun launchAlarmScreen(alarmId: Int, alarmLabel: String) {
        android.util.Log.d("AlarmService", "Launching screen with id: $alarmId")
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_LABEL, alarmLabel)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(fullScreenIntent)
    }

    private fun buildNotification(alarmId: Int, alarmLabel: String): Notification {
        android.util.Log.d("AlarmService", "Building notification for id: $alarmId")
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("ALARM_ID", alarmId)          // ✅ add extras here too
            putExtra("ALARM_LABEL", alarmLabel)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            alarmId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notfi)
            .setContentTitle("Alarmizo")
            .setContentText(alarmLabel)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setOngoing(true)
            .build()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarmizo alarm notifications"
                setBypassDnd(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
//            .edit() { clear() }
        // stop sound
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null

        // release wake lock
        wakeLock?.apply {
            if (isHeld) release()
        }
        wakeLock = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}