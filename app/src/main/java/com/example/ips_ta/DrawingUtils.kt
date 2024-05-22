package com.example.ips_ta

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint as AndroidPaint
import androidx.compose.ui.unit.dp


fun createSquare(
    drawScope: DrawScope,
    density: Float,
    path: Path, xStart: Float, yStart: Float, xEnd: Float, yEnd: Float,
    label: String, ratio: Float
) {
    path.reset()
    path.moveTo(xStart * ratio, yStart * ratio)
    path.lineTo(xEnd * ratio, yStart * ratio)
    path.lineTo(xEnd * ratio, yEnd * ratio)
    path.lineTo(xStart * ratio, yEnd * ratio)
    path.close()

    drawScope.drawPath(
        path = path,
        color = Color.Black,
        style = Stroke(width = 2f)
    )

    val textPaint = AndroidPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 10 * density
        textAlign = AndroidPaint.Align.CENTER
    }

    val centerX = (xStart + xEnd) / 2 * ratio
    val centerY = (yStart + yEnd) / 2 * ratio

    drawScope.drawContext.canvas.nativeCanvas.drawText(label, centerX, centerY, textPaint)
}

fun DrawScope.drawLabel(centerX: Float, centerY: Float, label: String) {
    val textPaint = AndroidPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 10.dp.toPx() // Convert dp to pixel, assuming LocalDensity is available
        textAlign = AndroidPaint.Align.CENTER
    }
    drawContext.canvas.nativeCanvas.drawText(label, centerX, centerY, textPaint)
}
