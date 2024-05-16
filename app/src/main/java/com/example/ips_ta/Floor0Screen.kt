package com.example.ips_ta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
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
//        drawRect(
//            color = Color.Gray,
//            // PENTING !!!!!!
//            size = Size(6710f * ratio, 2970f * ratio),
//            style = Stroke(width = 1.dp.toPx())
//        )

        val path = Path()

        // AD 04
        createSquare(path, 775f, 70f, 1610f, 840f)
//        path.moveTo(line(775f), line(70f))
//        path.lineTo(line(1610f), line(70f))
//        path.lineTo(line(1610f), line(840f))
//        path.lineTo(line(775f), line(840f))
//        path.close()

        // AD 03
        path.moveTo(line(0f), line(840f))
        path.lineTo(line(1610f), line(840f))
        path.lineTo(line(1610f), line(1300f))
        path.lineTo(line(540f), line(1300f))
        path.lineTo(line(540f), line(1500f))
        path.lineTo(line(0f), line(1500f))
        path.close()

        // AD 01
        createSquare(path, 0f, 1500f, 1010f, 2970f)

        // AD 02
        createSquare(path, 1010f, 1500f, 1610f, 2400f)

        // AD 06
        createSquare(path, 1910f, 70f, 2260f, 840f)

        // AD 07
        createSquare(path, 2260f, 70f, 3410f, 840f)

        // AD 17
        createSquare(path, 3410f, 70f, 4010f, 840f)

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
        path.moveTo(line(4910f), line(0f))
        path.lineTo(line(6710f), line(0f))
        path.close()

        path.moveTo(line(6710f), line(0f))
        path.lineTo(line(6710f), line(2550f))
        path.close()

        path.moveTo(line(6710f), line(2550f))
        path.lineTo(line(4910f), line(2550f))
        path.close()

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

        drawPath(
            path = path,
            color = Color(0x14AAD7D9),
            style = Fill
        )

        drawPath(
            path = path,
            color = Color(0xFF92C7CF),
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