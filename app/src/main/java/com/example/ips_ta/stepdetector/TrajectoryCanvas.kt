package com.example.ips_ta.stepdetector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrajectoryCanvas(trajectoryViewModel: TrajectoryViewModel = viewModel()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            val start = trajectoryViewModel.stepPositions.first()
            moveTo(start.x, start.y)

            trajectoryViewModel.stepPositions.forEach { position ->
                lineTo(position.x, position.y)
            }
        }

        drawPath(path, color = Color.Blue, style = Stroke(width = 10f))
    }
}
