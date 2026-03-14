package com.alarmizo.app.presentation.navigation

sealed class Screen(val route:String){
    object Home : Screen("home")
    object Camera: Screen("camera")
    object Alarm: Screen("alarm")
}