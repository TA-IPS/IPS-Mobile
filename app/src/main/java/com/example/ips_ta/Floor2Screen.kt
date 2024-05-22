package com.example.ips_ta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Floor2Screen(ratio: Float) {
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
//            size = Size(6710f * ratio, 2970f * ratio),
//            style = Stroke(width = 1.dp.toPx())
//        )
        val path = Path()

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

// Toilet
        createSquare(path, 2460f, 2250f, 3710f, 2550f)
        drawLabel(line((2460f + 3710f)/2), line((2250f + 2550f)/2), "Toilet")

// Pengisi toilet
        createSquare(path, 3510f, 2100f, 3710f, 2250f)
        drawLabel(line((3510f + 3710f)/2), line((2100f + 2250f)/2), "Pengisi Toilet")

// Auditorium A201
        path.moveTo(line(1610f), line(-295.5f))
        path.lineTo(line(3410f), line(-295.5f))
        path.lineTo(line(3410f), line(1500f))
        path.lineTo(line(1610f), line(1500f))
        path.lineTo(line(1610f), line(1200f))
        path.lineTo(line(1910f), line(1200f))
        path.lineTo(line(1910f), line(0f))
        path.lineTo(line(1610f), line(0f))
        path.close()
        drawLabel(line((1610f + 3410f)/2), line((0f + 1500f)/2), "Auditorium A201")

// Plaza
        path.moveTo(line(1610f), line(-295.5f))
        path.lineTo(line(706f), line(-295.5f))
        path.close()

        path.moveTo(line(706f), line(-295.5f))
        path.lineTo(line(706f), line(1296f))
        path.close()

        path.moveTo(line(706f), line(1296f))
        path.lineTo(line(1010f), line(1296f))
        path.close()

        path.moveTo(line(1010f), line(1296f))
        path.lineTo(line(1010f), line(2400f))
        path.close()

        path.moveTo(line(1010f), line(2400f))
        path.lineTo(line(2460f), line(2400f))
        path.close()
        drawLabel(line((706f + 2460f)/2), line((-295.5f + 2400f)/2), "Plaza")

// A205
        createSquare(path, 3410f, -179f, 4010f, 1500f)
        drawLabel(line((3410f + 4010f)/2), line((-179f + 1500f)/2), "A205")

// A206
        createSquare(path, 4010f, -179f, 4910f, 1500f)
        drawLabel(line((4010f + 4910f)/2), line((-179f + 1500f)/2), "A206")

// Void
        path.moveTo(line(4910f), line(1500f))
        path.lineTo(line(5510f), line(1500f))
        path.close()
        path.moveTo(line(5510f), line(1500f))
        path.lineTo(line(5510f), line(630f))
        path.close()
        drawLabel(line((4910f + 5510f)/2), line((630f + 1500f)/2), "Void Area")

// A207
        createSquare(path, 5210f, -179f, 6710f, 630f)
        drawLabel(line((5210f + 6710f)/2), line((-179f + 630f)/2), "A207")

// A208
        createSquare(path, 5810f, 630f, 6710f, 1500f)
        drawLabel(line((5810f + 6710f)/2), line((630f + 1500f)/2), "A208")

// A210
        createSquare(path, 3710f, 1800f, 4910f, 2550f)
        drawLabel(line((3710f + 4910f)/2), line((1800f + 2550f)/2), "A210")

// A209
        createSquare(path, 4910f, 1800f, 6110f, 2550f)
        drawLabel(line((4910f + 6110f)/2), line((1800f + 2550f)/2), "A209")


        // Pojok kanan bawah
        path.moveTo(line(6710f), line(1500f))
        path.lineTo(line(6710f), line(2550f))
        path.close()

        path.moveTo(line(6710f), line(2550f))
        path.lineTo(line(6110f), line(2550f))
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