package com.example.ips_ta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Floor0Screen(ratio: Float) {
    val sizeLantai = 40f/5;
    val sizeLantaiKantin = 60f/5;
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Gray,
            // PENTING !!!!!!
            size = Size( 56 * sizeLantaiKantin + 72 * sizeLantai, 39*sizeLantaiKantin * ratio),
            style = Stroke(width = 1.dp.toPx())
        )

        val path = Path()
        // AD 17,18
        path.moveTo(0 * sizeLantaiKantin + 72 * sizeLantai, 3 * sizeLantaiKantin * ratio)
        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 3 * sizeLantaiKantin * ratio)
        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 16 * sizeLantaiKantin * ratio)
        path.lineTo(5 * sizeLantaiKantin + 72 * sizeLantai, 16 * sizeLantaiKantin * ratio)
        path.lineTo(5 * sizeLantaiKantin + 72 * sizeLantai, 22 * sizeLantaiKantin * ratio)
        path.lineTo(0 * sizeLantaiKantin + 72 * sizeLantai, 22 * sizeLantaiKantin * ratio)
        path.close()

        // AD 19,20,21
        path.moveTo(0 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)
        path.lineTo(0 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)
        path.close()

        // AD 22
        path.moveTo(9 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
        path.close()

        // AD 23
        path.moveTo(26 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
        path.lineTo(56 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
        path.lineTo(56 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)



        // AD 06
        path.moveTo(8 * sizeLantai * ratio, 0 * sizeLantai * ratio)
        path.lineTo(8 * sizeLantai * ratio, 15 * sizeLantai * ratio)
        path.lineTo(17 * sizeLantai * ratio, 15 * sizeLantai * ratio)
        path.lineTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
        path.close()

        // AD 07
        path.moveTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
        path.lineTo(45 * sizeLantai * ratio, 0 * sizeLantai * ratio)
        path.lineTo(45 * sizeLantai * ratio, 15 * sizeLantai * ratio)
        path.lineTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
        path.close()

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx())
        )



    }
}