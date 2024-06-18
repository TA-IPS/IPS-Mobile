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
fun Floor5Screen(ratio: Float) {
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

        // A501
        createSquare(path, 829f, 1800f, 1910f, 2550f)
        drawLabel(line((829f + 1910f)/2), line((1800f + 2550f)/2), "A501")

        // A502
        createSquare(path, 829f, 1200f, 1910f, 1800f)
        drawLabel(line((829f + 1910f)/2), line((1200f + 1800f)/2), "A502")

        // A503
        createSquare(path, 1010f, 0f, 1910f, 1200f)
        drawLabel(line((1010f + 1910f)/2), line((0f + 1200f)/2), "A503")

        // A504
        createSquare(path, 2210f, 420f, 3110f, 1500f)
        drawLabel(line((2210f + 3110f)/2), line((420f + 1500f)/2), "A504")

        // Panel (A523)
        createSquare(path, 2210f, 1800f, 2510f, 2100f)
        drawLabel(line((2210f + 2510f)/2), line((1800f + 2100f)/2), "Panel")

        // Tangga
        createSquare(path, 3110f, 1800f, 3710f, 2100f)
        drawLabel(line((3110f + 3710f)/2), line((1800f + 2100f)/2), "Tangga")

        // Toilet
        createSquare(path, 2460f, 2250f, 3710f, 2550f)
        drawLabel(line((2460f + 3710f)/2), line((2250f + 2550f)/2), "Toilet")

        // Pengisi toilet
        createSquare(path, 3510f, 2100f, 3710f, 2250f)

        // Lift 1
        createSquare(path, 2510f, 1800f, 2810f, 2100f)
        drawLabel(line((2510f + 2810f)/2), line((1800f + 2100f)/2), "Lift 1")

        // Lift 2
        createSquare(path, 2810f, 1800f, 3110f, 2100f)
        drawLabel(line((2810f + 3110f)/2), line((1800f + 2100f)/2), "Lift 2")

        // A521
        createSquare(path, 3710f, 1500f, 4885f, 2550f)
        drawLabel(line((3710f + 4885f)/2), line((1500f + 2550f)/2), "A521")

        // A520
        createSquare(path, 4885f, 1500f, 5785f, 2550f)
        drawLabel(line((4885f + 5785f)/2), line((1500f + 2550f)/2), "A520")

        // A519
        createSquare(path, 5785f, 1500f, 6685f, 2550f)
        drawLabel(line((5785f + 6685f)/2), line((1500f + 2550f)/2), "A519")

        // A517
        createSquare(path, 4885f, -217f, 5785f, 1200f)
        drawLabel(line((4885f + 5785f)/2), line((-217 + 1200f)/2), "A517")

        // A518
        createSquare(path, 5785f, -217f, 6685f, 1200f)
        drawLabel(line((5785f + 6685f)/2), line((-217 + 1200f)/2), "A518")

        // A508
        createSquare(path, 3660f, 900f, 4010f, 1200f)
        drawLabel(line((3660f + 4010f)/2), line((900f + 1200f)/2), "A508")

        // A507
        createSquare(path, 3660f, 600f, 4010f, 900f)
        drawLabel(line((3660f + 4010f)/2), line((600f + 900f)/2), "A507")

        // A506
        createSquare(path, 3660f, 300f, 4010f, 600f)
        drawLabel(line((3660f + 4010f)/2), line((300f + 600f)/2), "A506")

        // A505
        createSquare(path, 3660f, 0f, 4010f, 300f)
        drawLabel(line((3660f + 4010f)/2), line((0f + 300f)/2), "A505")

        // A512
        createSquare(path, 4010f, 900f, 4360f, 1200f)
        drawLabel(line((4010f + 4360f)/2), line((900f + 1200f)/2), "A512")

        // A511
        createSquare(path, 4010f, 600f, 4360f, 900f)
        drawLabel(line((4010f + 4360f)/2), line((600f + 900f)/2), "A511")

        // A510
        createSquare(path, 4010f, 300f, 4360f, 600f)
        drawLabel(line((4010f + 4360f)/2), line((300f + 600f)/2), "A510")

        // A509
        createSquare(path, 4010f, 0f, 4360f, 300f)
        drawLabel(line((4010f + 4360f)/2), line((0f + 300f)/2), "A509")

        // A516
        createSquare(path, 4560f, 900f, 4885f, 1200f)
        drawLabel(line((4560f + 4885f)/2), line((900f + 1200f)/2), "A516")

        // A515
        createSquare(path, 4560f, 600f, 4885f, 900f)
        drawLabel(line((4560f + 4885f)/2), line((600f + 900f)/2), "A515")

        // A514
        createSquare(path, 4560f, 300f, 4885f, 600f)
        drawLabel(line((4560f + 4885f)/2), line((300f + 600f)/2), "A514")

        // A513
        createSquare(path, 4560f, 0f, 4885f, 300f)
        drawLabel(line((4560f + 4885f)/2), line((0f + 300f)/2), "A513")

        // void
        path.moveTo(line(1910f), line(0f))
        path.lineTo(line(3360f), line(0f))
        path.close()

        path.moveTo(line(3360f), line(0f))
        path.lineTo(line(3360f), line(-217f))
        path.close()

        path.moveTo(line(3360f), line(-217f))
        path.lineTo(line(6685f), line(-217f))
        path.close()

        path.moveTo(line(6685f), line(1200f))
        path.lineTo(line(6685f), line(1500f))
        path.close()

        path.moveTo(line(1910f), line(2550f))
        path.lineTo(line(2460f), line(2550f))
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