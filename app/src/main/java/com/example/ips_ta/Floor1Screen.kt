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
fun Floor1Screen(ratio: Float) {
    val sizeLantai = 40f;
    val sizeLantaiKantin = 60f;
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Gray,
            // PENTING !!!!!!
            size = Size(115 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio),
            style = Stroke(width = 1.dp.toPx())
        )


        val path = Path()
        //A1.01
        path.moveTo(16 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(29 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(29 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.lineTo(16 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.close()

        //A1.02
        path.moveTo(29 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(42 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(42 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.lineTo(29 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.close()

        //A1.03
        path.moveTo(42 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(52 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(52 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.lineTo(42 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.close()

        //A1.04
        path.moveTo(52 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.lineTo(52 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.close()

        // Bem ristek
        path.moveTo(65 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(80 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(80 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.lineTo(65 * sizeLantaiKantin * ratio, 42 * sizeLantai * ratio)
        path.close()

        // A1.07
        path.moveTo(90 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(102.5f * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(102.5f * sizeLantaiKantin * ratio, 18.5f * sizeLantai * ratio)
        path.lineTo(90 * sizeLantaiKantin * ratio, 18.5f * sizeLantai * ratio)
        path.close()

        // A1.08
        path.moveTo(102.5f * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(115 * sizeLantaiKantin * ratio, 0 * ratio)
        path.lineTo(115 * sizeLantaiKantin * ratio, 18.5f * sizeLantai * ratio)
        path.lineTo(102.5f * sizeLantaiKantin * ratio, 18.5f * sizeLantai * ratio)
        path.close()

        // A1.09
        path.moveTo(95 * sizeLantaiKantin * ratio, 26 * sizeLantai * ratio)
        path.lineTo(115 * sizeLantaiKantin * ratio, 26 * sizeLantai * ratio)
        path.lineTo(115 * sizeLantaiKantin * ratio, 50 * sizeLantai * ratio)
        path.lineTo(95 * sizeLantaiKantin * ratio, 50 * sizeLantai * ratio)
        path.close()

        // A1.10
        path.moveTo(115 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(90 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(90 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.lineTo(115 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.close()

        // A1.11
        path.moveTo(80 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.lineTo(80 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.close()

        // Tangga Darurat
        path.moveTo(62 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(56 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(56 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.close()

        // Lift
        path.moveTo(56 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(45 * sizeLantaiKantin * ratio , 50* sizeLantai * ratio)
        path.lineTo(45 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.lineTo(56 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.close()

        // Sebelah Lift
        path.moveTo(45 * sizeLantaiKantin * ratio, 50* sizeLantai * ratio)
        path.lineTo(40 * sizeLantaiKantin * ratio , 50* sizeLantai * ratio)
        path.lineTo(40 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.lineTo(45 * sizeLantaiKantin * ratio, 59 * sizeLantai * ratio)
        path.close()

        // Plaza
        path.moveTo(40 * sizeLantaiKantin * ratio , 50* sizeLantai * ratio)
        path.lineTo(16 * sizeLantaiKantin * ratio , 50* sizeLantai * ratio)
        path.lineTo(16 * sizeLantaiKantin * ratio, 0* sizeLantai * ratio)
        path.lineTo(0 * sizeLantaiKantin * ratio, 0* sizeLantai * ratio)
        path.lineTo(0 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.lineTo(40 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)

        // Kawasan Toilet
        path.moveTo(56 * sizeLantaiKantin * ratio, 59* sizeLantai * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 59* sizeLantai * ratio)
        path.lineTo(62 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.lineTo(40 * sizeLantaiKantin * ratio, 65 * sizeLantai * ratio)
        path.lineTo(40 * sizeLantaiKantin * ratio, 62 * sizeLantai * ratio)
        path.lineTo(56 * sizeLantaiKantin * ratio, 62 * sizeLantai * ratio)
        path.close()


        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}