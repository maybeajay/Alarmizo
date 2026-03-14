package com.alarmizo.app.presentation.alarm

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alarmizo.app.presentation.navigation.Screen
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun AlarmScreen(navController: NavHostController) {

    val viewModel: AlarmViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = true) { }

    // current time
    val calendar = Calendar.getInstance()
    val hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
    val minute = String.format("%02d", calendar.get(Calendar.MINUTE))

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // time display
            Text(
                text = "$hour:$minute",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            )

            // alarm label
            Text(
                text = uiState.alarmLabel,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // object challenge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {

                Row (
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Text(
                        text = "To dismiss alarm",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "📸 Find a:",
                        fontSize = 16.sp
                    )
                }
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.targetObject,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // dismiss button → goes to camera
            Button(
                onClick = {
                    navController.navigate(Screen.Camera.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Take Photo to Dismiss",
                    fontSize = 16.sp
                )
            }
        }
    }
}