package com.example.ips_ta.stepdetector
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import kotlin.math.cos
import kotlin.math.sin

class TrajectoryViewModel : ViewModel() {
    var stepPositions = mutableStateListOf(StepPosition(0f, 0f))
        private set

    fun addStep(orientationDegrees: Float, stepLengthCm: Float = 70f) {
        // Convert degrees to radians for the math functions
        val orientationRadians = Math.toRadians(orientationDegrees.toDouble())

        val lastPosition = stepPositions.last()
        val deltaX = cos(orientationRadians) * stepLengthCm
        val deltaY = sin(orientationRadians) * stepLengthCm

        val newPosition = StepPosition(
            x = lastPosition.x + deltaX.toFloat(),
            y = lastPosition.y + deltaY.toFloat()
        )
        Log.d("TrajectoryViewModel", "Orientation: $orientationDegrees, New Position: $newPosition")
        stepPositions.add(newPosition)
    }
}
