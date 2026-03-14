package com.alarmizo.app.presentation.camera

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmizo.app.service.AlarmService
import com.alarmizo.app.util.DetectionResult
import com.alarmizo.app.util.ObjectDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val objectDetector: ObjectDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun setTargetObject(target: String) {
        _uiState.value = _uiState.value.copy(targetObject = target)
    }

    fun analyzePhoto(bitmap: Bitmap, context: Context) {
        viewModelScope.launch(Dispatchers.Default) {

            // 1. set detecting state
            _uiState.value = _uiState.value.copy(
                detectionResult = DetectionResult.Detecting
            )

            // 2. run detection
            val result = objectDetector.detect(bitmap)

            // 3. check if match
            when (result) {
                is DetectionResult.Success -> {
                    val isMatch = objectDetector.isMatch(
                        result.label,
                        _uiState.value.targetObject
                    )
                    if (isMatch) {
                        // ✅ correct object — stop alarm
                        stopAlarmService(context)
                        _uiState.value = _uiState.value.copy(
                            detectionResult = result,
                            isAlarmDismissed = true
                        )
                    } else {
                        // ❌ wrong object — try again
                        _uiState.value = _uiState.value.copy(
                            detectionResult = DetectionResult.Failure(
                                "Wrong object! Found: ${result.label}. Try again!"
                            )
                        )
                    }
                }
                is DetectionResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        detectionResult = result
                    )
                }
                else -> Unit
            }
        }
    }

    private fun stopAlarmService(context: Context) {
        val intent = Intent(context, AlarmService::class.java)
        context.stopService(intent)
    }

    fun resetDetection() {
        _uiState.value = _uiState.value.copy(
            detectionResult = DetectionResult.Idle
        )
    }
}

data class CameraUiState(
    val targetObject: String = "Water Bottle",
    val detectionResult: DetectionResult = DetectionResult.Idle,
    val isAlarmDismissed: Boolean = false
)
