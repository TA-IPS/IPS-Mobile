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
fun Floor6Screen(ratio: Float) {
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
//            size = Size(800f * ratio, 600f * ratio),
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

        // A602
        createSquare(path, 1010f, 0f, 2510f, 1200f)
        drawLabel(line((1010f + 2510f)/2), line((0f + 1200f)/2), "A602")

        // A601
        createSquare(path, 829f, 1200f, 1860f, 2550f)
        drawLabel(line((829f + 1860f)/2), line((1200f + 2550f)/2), "A601")

        // A603
        createSquare(path, 3660f, -217f, 4010f, 300f)
        drawLabel(line((3660f + 4010f)/2), line((-217f + 300f)/2), "A603")

        // A604
        createSquare(path, 3660f, 300f, 4010f, 600f)
        drawLabel(line((3660f + 4010f)/2), line((300f + 600f)/2), "A604")

        // A605
        createSquare(path, 3660f, 600f, 4010f, 900f)
        drawLabel(line((3660f + 4010f)/2), line((600f + 900f)/2), "A605")

        // A606
        createSquare(path, 3660f, 900f, 4010f, 1200f)
        drawLabel(line((3660f + 4010f)/2), line((900f + 1200f)/2), "A606")

        // A607
        createSquare(path, 4010f, -217f, 4910f, 1200f)
        drawLabel(line((4010f + 4910f)/2), line((-217f + 1200f)/2), "A607")

        // A608
        createSquare(path, 4910f, -217f, 5810f, 1200f)
        drawLabel(line((4910f + 5810f)/2), line((-217f + 1200f)/2), "A608")

        // A609
        createSquare(path, 5810f, -217f, 6710f, 1200f)
        drawLabel(line((5810f + 6710f)/2), line((-217f + 1200f)/2), "A609")

        // A613
        createSquare(path, 3900f, 1500f, 4310f, 1800f)
        drawLabel(line((3900f + 4310f)/2), line((1500f + 1800f)/2), "A613")

        // A614
        createSquare(path, 3900f, 1800f, 4310f, 2100f)
        drawLabel(line((3900f + 4310f)/2), line((1800f + 2100f)/2), "A614")

        // A615
        createSquare(path, 3710f, 2100f, 4310f, 2550f)
        drawLabel(line((3710f + 4310f)/2), line((2100f + 2550f)/2), "A615")

        // A612
        createSquare(path, 4310f, 1500f, 4910f, 2550f)
        drawLabel(line((4310f + 4910f)/2), line((1500f + 2550f)/2), "A612")

        // A611
        createSquare(path, 4910f, 1500f, 5810f, 2550f)
        drawLabel(line((4910f + 5810f)/2), line((1500f + 2550f)/2), "A611")

        // A610
        createSquare(path, 5810f, 1500f, 6710f, 2550f)
        drawLabel(line((5810f + 6710f)/2), line((1500f + 2550f)/2), "A610")

        // void
        path.moveTo(line(2510f), line(0f))
        path.lineTo(line(3398f), line(0f))
        path.close()

        path.moveTo(line(3398f), line(-217f))
        path.lineTo(line(3398f), line(1200f))
        path.close()

        path.moveTo(line(3398f), line(-217f))
        path.lineTo(line(3660f), line(-217f))
        path.close()

        path.moveTo(line(3398f), line(1200f))
        path.lineTo(line(3110f), line(1200f))
        path.close()

        path.moveTo(line(3110f), line(1200f))
        path.lineTo(line(3110f), line(400f))
        path.close()

        path.moveTo(line(3110f), line(400f))
        path.lineTo(line(2510f), line(400f))
        path.close()

        path.moveTo(line(2510f), line(400f))
        path.lineTo(line(2510f), line(0f))
        path.close()

        path.moveTo(line(1860f), line(2550f))
        path.lineTo(line(2460f), line(2550f))
        path.close()

        path.moveTo(line(6710f), line(1200f))
        path.lineTo(line(6710f), line(1500f))


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