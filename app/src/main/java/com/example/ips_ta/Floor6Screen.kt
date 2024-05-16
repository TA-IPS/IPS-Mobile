package com.example.ips_ta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Floor6Screen(ratio: Float) {

    Canvas(modifier = Modifier.fillMaxSize()) {
//        drawRect(
//            color = Color.Gray,
//            size = Size(800f * ratio, 600f * ratio),
//            style = Stroke(width = 1.dp.toPx())
//        )

        val path = Path()
        path.moveTo(0 * ratio, 0 * ratio)
        path.lineTo(0 * ratio, 1300 * ratio)

        drawPath(
            path = path,
            color = Color.Red,
            style = Stroke(width = 2.dp.toPx())
        )

        drawRect(
            color = Color.Blue,
            topLeft = Offset(200f, 100f) * ratio,
            size = Size(100f, 100f) * ratio,
            style = Stroke(width = 2.dp.toPx())
        )

        drawRect(
            color = Color.Blue,
            topLeft = Offset(0f, 0f) * ratio,
            size = Size(100f, 100f) * ratio,
            style = Stroke(width = 2.dp.toPx())
        )

        drawRect(
            color = Color.Blue,
            topLeft = Offset(200f, 0f) * ratio,
            size = Size(100f, 100f) * ratio,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}