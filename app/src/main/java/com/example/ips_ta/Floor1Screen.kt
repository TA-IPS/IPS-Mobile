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
fun Floor1Screen(ratio: Float) {
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

        // Panel
        createSquare(path, 2210f, 1800f, 2510f, 2100f)

        // Lift 1
        createSquare(path, 2510f, 1800f, 2810f, 2100f)

        // Lift 2
        createSquare(path, 2810f, 1800f, 3110f, 2100f)

        // Tangga
        createSquare(path, 3110f, 1800f, 3710f, 2100f)

        // Toilet
        createSquare(path, 2460f, 2250f, 3710f, 2550f)

        // Pengisi toilet
        createSquare(path, 3510f, 2100f, 3710f, 2250f)

        // A101
        path.moveTo(line(775f), line(-179f))
        path.lineTo(line(1610f), line(-179f))
        path.lineTo(line(1610f), line(1500f))
        path.lineTo(line(1010f), line(1500f))
        path.lineTo(line(1010f), line(1300f))
        path.lineTo(line(775f), line(1300f))
        path.close()

        // A102
        createSquare(path, 1610f, -179f, 2210f, 1500f)

        // A103
        createSquare(path, 2210f, -179f, 3110f, 1500f)

        // A104
        createSquare(path, 3110f, -179f, 3710f, 1500f)

        // garis
        path.moveTo(line(3710f), line(-179f))
        path.lineTo(line(4010f), line(-179f))
        path.close()

        // A105
        createSquare(path, 4010f, -179f, 4910f, 660.5f)

        // A106
        createSquare(path, 4010f, 660.5f, 4910f, 1500f)

        // void
        path.moveTo(line(4910f), line(1200f))
        path.lineTo(line(5510f), line(1200f))
        path.close()
        path.moveTo(line(5510f), line(1200f))
        path.lineTo(line(5510f), line(630f))
        path.close()

        // A107
        createSquare(path, 5210f, -179f, 5990f, 630f)

        // A108
        createSquare(path, 5990f, -179f, 6710f, 630f)

        // A109
        createSquare(path, 5810f, 900f, 6710f, 1800f)

        // A111
        createSquare(path, 3710f, 1800f, 4910f, 2550f)

        // void
        createSquare(path, 4910f, 1800f, 5510f, 2550f)

        // A110
        createSquare(path, 5510f, 1800f, 6710f, 2550f)

        // A119 Teras luar
        path.moveTo(line(775f), line(840f))
        path.lineTo(line(0f), line(840f))
        path.close()

        path.moveTo(line(0f), line(840f))
        path.lineTo(line(0f), line(2970f))
        path.close()

        path.moveTo(line(0f), line(2970f))
        path.lineTo(line(1010f), line(2970f))
        path.close()

        path.moveTo(line(1010f), line(2970f))
        path.lineTo(line(2210f), line(2400f))
        path.close()

        path.moveTo(line(2210f), line(2400f))
        path.lineTo(line(2460f), line(2400f))
        path.close()

        // pintu teras luar
        path.moveTo(line(1010f), line(1500f))
        path.lineTo(line(1010f), line(1800f))
        path.close()

        path.moveTo(line(1010f), line(1800f))
        path.lineTo(line(2210f), line(1800f))

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