package com.alarmizo.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.alarmizo.app.presentation.navigation.NavGraph
import com.alarmizo.app.presentation.navigation.Screen
import com.alarmizo.app.service.AlarmService
import com.alarmizo.app.ui.theme.AlarmizoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        var alarmId = intent.getIntExtra("ALARM_ID", -1)
        var alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Alarm"

        // app opened via icon while alarm is firing — read from prefs
        if (alarmId == -1) {
            val prefs = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            alarmId = prefs.getInt("active_alarm_id", -1)
            alarmLabel = prefs.getString("active_alarm_label", "Alarm") ?: "Alarm"
        }

        Log.d("MainActivity", "alarmId: $alarmId label: $alarmLabel")

        // stop service if normal launch with no active alarm
        if (alarmId == -1) {
            stopService(Intent(this, AlarmService::class.java))
        }

        val startDestination = if (alarmId != -1) Screen.Alarm.route
        else Screen.Home.route

        setContent {
            AlarmizoTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}