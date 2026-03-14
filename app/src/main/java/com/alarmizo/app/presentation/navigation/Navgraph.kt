package com.alarmizo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alarmizo.app.presentation.alarm.AlarmScreen
import com.alarmizo.app.presentation.camera.CameraScreen
import com.alarmizo.app.presentation.home.HomeScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween


@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination.toString(),
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Camera.route) { CameraScreen(navController) }
        composable(Screen.Alarm.route) { AlarmScreen(navController) }
    }
}