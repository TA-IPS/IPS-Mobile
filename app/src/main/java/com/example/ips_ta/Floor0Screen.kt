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
    val sizeLantai = 40f;
    val sizeLantaiKantin = 60f;
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Gray,
            // PENTING !!!!!!
            size = Size( (54 * sizeLantaiKantin + 72 * sizeLantai) * ratio, 39*sizeLantaiKantin * ratio),
            style = Stroke(width = 1.dp.toPx())
        )

        val path = Path()

        // AD 04 & 03
        path.moveTo(length(0, 0, ratio), length(0, 0, ratio))
        path.lineTo(length(0,27, ratio), length(0, 0, ratio))
        path.lineTo(length(0,27, ratio), length(3, 26, ratio))
        path.lineTo(length(0, 0, ratio), length(3, 26, ratio))
        path.close()

        // AD 06 & 07
        path.moveTo(length(0, 35, ratio), length(0, 0, ratio))
        path.lineTo(length(0, 72, ratio), length(0, 0, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 16, ratio))
        path.lineTo(length(0, 35, ratio), length(3, 16, ratio))
        path.close()

        // AD 05, 08, 09, asumsi mereka lebar nya sama kek AD 06
        path.moveTo(length(0, 35, ratio), length(3, 16, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 16, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 32, ratio))
        path.lineTo(length(0, 35, ratio), length(3, 32, ratio))
        path.close()

        // AD 01 & 02
        path.moveTo(length(0, 0, ratio), length(3, 32, ratio))
        path.lineTo(length(0,27, ratio), length(3, 32, ratio))
        path.lineTo(length(0,27, ratio), length(39, 0, ratio))
        path.lineTo(length(0, 0, ratio), length(39, 0, ratio))
        path.close()

        // AD10 & lift & pintu tangga
        path.moveTo(length(0, 41, ratio), length(3, 39, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 39, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 47, ratio))
        path.lineTo(length(0, 41, ratio), length(3, 47, ratio))
        path.close()

        // toilet dkk
        path.moveTo(length(0, 41, ratio), length(3, 50, ratio))
        path.lineTo(length(0, 72, ratio), length(3, 50, ratio))
        path.lineTo(length(0, 72, ratio), length(39, 0, ratio))
        path.lineTo(length(0, 41, ratio), length(39, 0, ratio))
        path.close()

        // AD 17
        path.moveTo(length(0, 72, ratio), length(0, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(0, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(16, 0, ratio))
        path.lineTo(length(5, 72, ratio), length(16, 0, ratio))
        path.lineTo(length(5, 72, ratio), length(14, 0, ratio))
        path.lineTo(length(0, 72, ratio), length(14, 0, ratio))
        path.close()

        // AD 18
        path.moveTo(length(0, 72, ratio), length(14, 0, ratio))
        path.lineTo(length(5, 72, ratio), length(14, 0, ratio))
        path.lineTo(length(5, 72, ratio), length(23, 0, ratio))
        path.lineTo(length(0, 72, ratio), length(23, 0, ratio))
        path.close()

        // AD 22
        path.moveTo(length(9, 72, ratio), length(0, 0, ratio))
        path.lineTo(length(24, 72, ratio), length(0, 0, ratio))
        path.lineTo(length(24, 72, ratio), length(19, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(19, 0, ratio))
        path.close()

        // AD 20
        path.moveTo(length(5, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(39, 0, ratio))
        path.lineTo(length(5, 72, ratio), length(39, 0, ratio))
        path.close()

        // AD 19
        path.moveTo(length(9, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(17, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(17, 72, ratio), length(39, 0, ratio))
        path.lineTo(length(9, 72, ratio), length(39, 0, ratio))
        path.close()

        // AD 21
        path.moveTo(length(17, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(24, 72, ratio), length(29, 0, ratio))
        path.lineTo(length(24, 72, ratio), length(39, 0, ratio))
        path.lineTo(length(17, 72, ratio), length(39, 0, ratio))
        path.close()


//        // AD 17,18
//        path.moveTo(0 * sizeLantaiKantin + 72 * sizeLantai, 3 * sizeLantaiKantin * ratio)
//        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 3 * sizeLantaiKantin * ratio)
//        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 16 * sizeLantaiKantin * ratio)
//        path.lineTo(5 * sizeLantaiKantin + 72 * sizeLantai, 16 * sizeLantaiKantin * ratio)
//        path.lineTo(5 * sizeLantaiKantin + 72 * sizeLantai, 22 * sizeLantaiKantin * ratio)
//        path.lineTo(0 * sizeLantaiKantin + 72 * sizeLantai, 22 * sizeLantaiKantin * ratio)
//        path.close()
//
//        // AD 19,20,21
//        path.moveTo(0 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)
//        path.lineTo(0 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)
//        path.close()
//
//        // AD 22
//        path.moveTo(9 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
//        path.lineTo(9 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
//        path.close()
//
//        // AD 23
//        path.moveTo(26 * sizeLantaiKantin + 72 * sizeLantai, 19 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
//        path.lineTo(56 * sizeLantaiKantin + 72 * sizeLantai, 0 * sizeLantaiKantin * ratio)
//        path.lineTo(56 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 39 * sizeLantaiKantin * ratio)
//        path.lineTo(26 * sizeLantaiKantin + 72 * sizeLantai, 29 * sizeLantaiKantin * ratio)
//
//
//
//        // AD 06
//        path.moveTo(8 * sizeLantai * ratio, 0 * sizeLantai * ratio)
//        path.lineTo(8 * sizeLantai * ratio, 15 * sizeLantai * ratio)
//        path.lineTo(17 * sizeLantai * ratio, 15 * sizeLantai * ratio)
//        path.lineTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
//        path.close()
//
//        // AD 07
//        path.moveTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
//        path.lineTo(45 * sizeLantai * ratio, 0 * sizeLantai * ratio)
//        path.lineTo(45 * sizeLantai * ratio, 15 * sizeLantai * ratio)
//        path.lineTo(17 * sizeLantai * ratio, 0 * sizeLantai * ratio)
//        path.close()

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx())
        )



    }
}

fun length(totalLantaiKantin: Int, totalLantai: Int, ratio: Float): Float {
    val sizeLantai = 40f;
    val sizeLantaiKantin = 60f;
    return (totalLantaiKantin * sizeLantaiKantin + totalLantai * sizeLantai) * ratio
}