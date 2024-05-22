package com.example.ips_ta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import android.graphics.Paint.Style
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText

@Composable
fun Floor0Screen(ratio: Float) {
    val density = LocalDensity.current.density  // Get current density

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
//        createSquare(path, 775f, 70f, 1610f, 840f)
        createSquare(this, density, path, 775f, 70f, 1610f, 840f, "AD 04", ratio)
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
        drawLabel(line((1610f + 0f)/2), line((840f+1300f)/2), "AD 02")

//        // AD 01
//        createSquare(this, density, path, 0f, 1500f, 1010f, 2970f, "AD 01", ratio)
//
//        // AD 02
//        createSquare(this, density, path, 1010f, 1500f, 1610f, 2400f, "AD 02", ratio)
//
//        // AD 06
//        createSquare(this, density, path, 1910f, 70f, 2260f, 840f, "AD 06", ratio)
//
//        // AD 07
//        createSquare(this, density, path, 2260f, 70f, 3410f, 840f, "AD 07", ratio)
//
//        // AD 17
//        createSquare(this, density, path, 3410f, 70f, 4010f, 840f, "AD 17", ratio)
//
//        // AD 05
//        createSquare(this, density, path, 1910f, 840f, 2810f, 1500f, "AD 05", ratio)
//
//        // AD 08
//        createSquare(this, density, path, 2810f, 840f, 3410f, 1500f, "AD 08", ratio)
//
//        // AD 18
//        createSquare(this, density, path, 3410f, 840f, 3710f, 1200f, "AD 18", ratio)
//
//        // Mushola
//        createSquare(this, density, path, 4010f, 0f, 4910f, 1200f, "Mushola", ratio)
//
//        // Panel
//        createSquare(this, density,path, 2210f, 1800f, 2510f, 2100f, "Panel", ratio)
//
//        // Lift 1
//        createSquare(this, density,path, 2510f, 1800f, 2810f, 2100f, "Lift 1", ratio)
//
//        // Lift 2
//        createSquare(this, density,path, 2810f, 1800f, 3110f, 2100f, "Lift 2", ratio)
//
//        // Tangga
//        createSquare(this, density,path, 3110f, 1800f, 3710f, 2100f, "Tangga", ratio)
//
//        // Pantry
//        createSquare(this, density,path, 2210f, 2250f, 2460f, 2400f, "Pantry", ratio)
//
//        // Toilet
//        createSquare(this, density,path, 2460f, 2250f, 3710f, 2550f, "Toilet", ratio)
//
//        // Pengisi toilet
//        createSquare(this, density,path, 3510f, 2100f, 3710f, 2250f, "Toilet", ratio)
//
//        // AD 20
//        createSquare(this, density,path, 3710f, 1800f, 4010f, 2550f, "AD 20", ratio)
//
//        // AD 19
//        createSquare(this, density,path, 4010f, 1800f, 4610f, 2550f, "AD 19", ratio)
//
//        // AD 21
//        createSquare(this, density,path, 4610f, 1800f, 4910f, 2550f, "AD 21", ratio)

        // AD 02
        createSquare(path, 1010f, 1500f, 1610f, 2400f)
        drawLabel(line((1010f + 1610f)/2), line((1500f+2400f)/2), "AD 02")

        // AD 06
        createSquare(path, 1910f, 70f, 2260f, 840f)
        drawLabel(line((1910f + 2260f)/2), line((70f + 840f)/2), "AD 06")

        // AD 07
        createSquare(path, 2260f, 70f, 3410f, 840f)
        drawLabel(line((2260f + 3410f)/2), line((70f + 840f)/2), "AD 07")

        // AD 17
        createSquare(path, 3410f, 70f, 4010f, 840f)
        drawLabel(line((3410f + 4010f)/2), line((70f + 840f)/2), "AD 17")

        // AD 05
        createSquare(path, 1910f, 840f, 2810f, 1500f)
        drawLabel(line((1910f + 2810f)/2), line((840f + 1500f)/2), "AD 05")

        // AD 08
        createSquare(path, 2810f, 840f, 3410f, 1500f)
        drawLabel(line((2810f + 3410f)/2), line((840f + 1500f)/2), "AD 08")

        // AD 18
        createSquare(path, 3410f, 840f, 3710f, 1200f)
        drawLabel(line((3410f + 3710f)/2), line((840f + 1200f)/2), "AD 18")

        // Mushola
        createSquare(path, 4010f, 0f, 4910f, 1200f)
        drawLabel(line((4010f + 4910f)/2), line((0f + 1200f)/2), "Mushola")

        // Panel
        createSquare(path, 2210f, 1800f, 2510f, 2100f)
        drawLabel(line((2210f + 2510f)/2), line((1800f + 2100f)/2), "Panel")

        // Lift 1
        createSquare(path, 2510f, 1800f, 2810f, 2100f)
        drawLabel(line((2510f + 2810f)/2), line((1800f + 2100f)/2), "Lift 1")

        // Lift 2
        createSquare(path, 2810f, 1800f, 3110f, 2100f)
        drawLabel(line((2810f + 3110f)/2), line((1800f + 2100f)/2), "Lift 2")

        // Tangga
        createSquare(path, 3110f, 1800f, 3710f, 2100f)
        drawLabel(line((3110f + 3710f)/2), line((1800f + 2100f)/2), "Tangga")

        // Pantry
        createSquare(path, 2210f, 2250f, 2460f, 2400f)
        drawLabel(line((2210f + 2460f)/2), line((2250f + 2400f)/2), "Pantry")

        // Toilet
        createSquare(path, 2460f, 2250f, 3710f, 2550f)
        drawLabel(line((2460f + 3710f)/2), line((2250f + 2550f)/2), "Toilet")

        // Pengisi toilet
        createSquare(path, 3510f, 2100f, 3710f, 2250f)
        drawLabel(line((3510f + 3710f)/2), line((2100f + 2250f)/2), "Pengisi Toilet")

        // AD 20
        createSquare(path, 3710f, 1800f, 4010f, 2550f)
        drawLabel(line((3710f + 4010f)/2), line((1800f + 2550f)/2), "AD 20")

        // AD 19
        createSquare(path, 4010f, 1800f, 4610f, 2550f)
        drawLabel(line((4010f + 4610f)/2), line((1800f + 2550f)/2), "AD 19")

        // AD 21
        createSquare(path, 4610f, 1800f, 4910f, 2550f)
        drawLabel(line((4610f + 4910f)/2), line((1800f + 2550f)/2), "AD 21")

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

        val centerX = line((4910f + 6710f) / 2)
        val centerY = line(2550f / 2)
        drawLabel(centerX, centerY, "Kantin")

        // pintu depan
        path.moveTo(line(1610f), line(2400f))
        path.lineTo(line(2210f), line(2400f))
        path.close()

        val frontDoorCenterX = line((1610f + 2210f) / 2)
        val frontDoorCenterY = line(2400f) - 20.dp.toPx()  // Adjust for visibility
        drawLabel(frontDoorCenterX, frontDoorCenterY, "Pintu Depan")

        // teras depan
        path.moveTo(line(1010f), line(2970f))
        path.lineTo(line(2210f), line(2400f))
        path.close()

        val terraceCenterX = line((1010f + 2210f) / 2)
        val terraceCenterY = line((2970f + 2400f) / 2)
        drawLabel(terraceCenterX, terraceCenterY, "Teras Depan")

        // pintu belakang
        path.moveTo(line(1610f), line(70f))
        path.lineTo(line(1910f), line(70f))
        path.close()

        val backDoorCenterX = line((1610f + 1910f) / 2)
        val backDoorCenterY = line(70f) + 20.dp.toPx()  // Adjust for visibility
        drawLabel(backDoorCenterX, backDoorCenterY, "Pintu Belakang")

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