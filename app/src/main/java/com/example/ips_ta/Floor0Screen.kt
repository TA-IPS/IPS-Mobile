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
    fun line(length: Float): Float {
        return length * ratio
    }

    fun createSquare(path: Path, xStart: Float, yStart:Float, xEnd: Float, yEnd: Float) {
        path.moveTo(line(xStart), line(yStart))
        path.lineTo(line(xEnd), line(yStart))
        path.lineTo(line(xEnd), line(yEnd))
        path.lineTo(line(xStart), line(yEnd))
        path.close()
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Gray,
            // PENTING !!!!!!
            size = Size(6710f * ratio, 2970f * ratio),
            style = Stroke(width = 1.dp.toPx())
        )

        val path = Path()

        // AD 04
        path.moveTo(line(775f), line(70f))
        path.lineTo(line(1610f), line(70f))
        path.lineTo(line(1610f), line(840f))
        path.lineTo(line(775f), line(840f))
        path.close()

        // AD 03
        path.moveTo(line(0f), line(840f))
        path.lineTo(line(1610f), line(840f))
        path.lineTo(line(1610f), line(1300f))
        path.lineTo(line(540f), line(1300f))
        path.lineTo(line(540f), line(1500f))
        path.lineTo(line(0f), line(1500f))
        path.close()

        // AD 01
        path.moveTo(line(0f), line(1500f))
        path.lineTo(line(1010f), line(1500f))
        path.lineTo(line(1010f), line(2970f))
        path.lineTo(line(0f), line(2970f))
        path.close()

        // AD 02
        path.moveTo(line(1010f), line(1500f))
        path.lineTo(line(1610f), line(1500f))
        path.lineTo(line(1610f), line(2400f))
        path.lineTo(line(1010f), line(2400f))
        path.close()

        // AD 06
        path.moveTo(line(1910f), line(70f))
        path.lineTo(line(2260f), line(70f))
        path.lineTo(line(2260f), line(840f))
        path.lineTo(line(1910f), line(840f))
        path.close()

        // AD 07
        path.moveTo(line(2260f), line(70f))
        path.lineTo(line(3410f), line(70f))
        path.lineTo(line(3410f), line(840f))
        path.lineTo(line(2260f), line(840f))
        path.close()

        // AD 17
        path.moveTo(line(3410f), line(70f))
        path.lineTo(line(4010f), line(70f))
        path.lineTo(line(4010f), line(840f))
        path.lineTo(line(3410f), line(840f))
        path.close()

        // AD 05
        createSquare(path, 1910f, 840f, 2810f, 1500f)

        // AD 08
        createSquare(path, 2810f, 840f, 3410f, 1500f)

        // AD 18
        createSquare(path, 3410f, 840f, 3710f, 1200f)

        // Mushola
        createSquare(path, 4010f, 0f, 4910f, 1200f)

        // Panel
        createSquare(path, 2210f, 1800f, 2510f, 2100f)

        // Lift 1
        createSquare(path, 2510f, 1800f, 2810f, 2100f)

        // Lift 2
        createSquare(path, 2810f, 1800f, 3110f, 2100f)

        // Tangga
        createSquare(path, 3110f, 1800f, 3710f, 2100f)

        // Pantry
        createSquare(path, 2210f, 2250f, 2460f, 2400f)

        // Toilet
        createSquare(path, 2460f, 2250f, 3710f, 2550f)

        // Pengisi toilet
        createSquare(path, 3510f, 2100f, 3710f, 2250f)

        // AD 20
        createSquare(path, 3710f, 1800f, 4010f, 2550f)

        // AD 19
        createSquare(path, 4010f, 1800f, 4610f, 2550f)

        // AD 21
        createSquare(path, 4610f, 1800f, 4910f, 2550f)

        // Kantin
        createSquare(path, 4910f, 0f, 6710f, 2550f)

        // pintu depan
        path.moveTo(line(1610f), line(2400f))
        path.lineTo(line(2210f), line(2400f))
        path.close()

        // teras depan
        path.moveTo(line(1010f), line(2970f))
        path.lineTo(line(2210f), line(2400f))
        path.close()

        // pintu belakang
        path.moveTo(line(1610f), line(70f))
        path.lineTo(line(1910f), line(70f))
        path.close()



//        // AD 04 & 03
//        path.moveTo(length(0, 0, ratio), length(0, 0, ratio))
//        path.lineTo(length(0,27, ratio), length(0, 0, ratio))
//        path.lineTo(length(0,27, ratio), length(3, 26, ratio))
//        path.lineTo(length(0, 0, ratio), length(3, 26, ratio))
//        path.close()
//
//        // AD 06 & 07
//        path.moveTo(length(0, 35, ratio), length(0, 0, ratio))
//        path.lineTo(length(0, 72, ratio), length(0, 0, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 16, ratio))
//        path.lineTo(length(0, 35, ratio), length(3, 16, ratio))
//        path.close()
//
//        // AD 05, 08, 09, asumsi mereka lebar nya sama kek AD 06
//        path.moveTo(length(0, 35, ratio), length(3, 16, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 16, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 32, ratio))
//        path.lineTo(length(0, 35, ratio), length(3, 32, ratio))
//        path.close()
//
//        // AD 01 & 02
//        path.moveTo(length(0, 0, ratio), length(3, 32, ratio))
//        path.lineTo(length(0,27, ratio), length(3, 32, ratio))
//        path.lineTo(length(0,27, ratio), length(39, 0, ratio))
//        path.lineTo(length(0, 0, ratio), length(39, 0, ratio))
//        path.close()
//
//        // AD10 & lift & pintu tangga
//        path.moveTo(length(0, 41, ratio), length(3, 39, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 39, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 47, ratio))
//        path.lineTo(length(0, 41, ratio), length(3, 47, ratio))
//        path.close()
//
//        // toilet dkk
//        path.moveTo(length(0, 41, ratio), length(3, 50, ratio))
//        path.lineTo(length(0, 72, ratio), length(3, 50, ratio))
//        path.lineTo(length(0, 72, ratio), length(39, 0, ratio))
//        path.lineTo(length(0, 41, ratio), length(39, 0, ratio))
//        path.close()
//
//        // AD 17
//        path.moveTo(length(0, 72, ratio), length(0, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(0, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(16, 0, ratio))
//        path.lineTo(length(5, 72, ratio), length(16, 0, ratio))
//        path.lineTo(length(5, 72, ratio), length(14, 0, ratio))
//        path.lineTo(length(0, 72, ratio), length(14, 0, ratio))
//        path.close()
//
//        // AD 18
//        path.moveTo(length(0, 72, ratio), length(14, 0, ratio))
//        path.lineTo(length(5, 72, ratio), length(14, 0, ratio))
//        path.lineTo(length(5, 72, ratio), length(23, 0, ratio))
//        path.lineTo(length(0, 72, ratio), length(23, 0, ratio))
//        path.close()
//
//        // AD 22
//        path.moveTo(length(9, 72, ratio), length(0, 0, ratio))
//        path.lineTo(length(24, 72, ratio), length(0, 0, ratio))
//        path.lineTo(length(24, 72, ratio), length(19, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(19, 0, ratio))
//        path.close()
//
//        // AD 20
//        path.moveTo(length(5, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(39, 0, ratio))
//        path.lineTo(length(5, 72, ratio), length(39, 0, ratio))
//        path.close()
//
//        // AD 19
//        path.moveTo(length(9, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(17, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(17, 72, ratio), length(39, 0, ratio))
//        path.lineTo(length(9, 72, ratio), length(39, 0, ratio))
//        path.close()
//
//        // AD 21
//        path.moveTo(length(17, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(24, 72, ratio), length(29, 0, ratio))
//        path.lineTo(length(24, 72, ratio), length(39, 0, ratio))
//        path.lineTo(length(17, 72, ratio), length(39, 0, ratio))
//        path.close()


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

//fun length(totalLantaiKantin: Int, totalLantai: Int, ratio: Float): Float {
//    val sizeLantai = 40f;
//    val sizeLantaiKantin = 60f;
//    return (totalLantaiKantin * sizeLantaiKantin + totalLantai * sizeLantai) * ratio
//}

fun length(size: Float, ratio: Float): Float {
    return size * ratio
}