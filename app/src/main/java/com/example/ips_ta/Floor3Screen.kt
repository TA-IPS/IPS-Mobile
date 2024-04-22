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
fun Floor3Screen(ratio: Float) {
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
            size = Size(6710f * ratio, 2970f * ratio),
            style = Stroke(width = 1.dp.toPx())
        )
        val path = Path()

        // Lift 1
        createSquare(path, 2510f, 1800f, 2810f, 2100f)

        // Lift 2
        createSquare(path, 2810f, 1800f, 3110f, 2100f)

        // Tangga
        createSquare(path, 3110f, 1800f, 3710f, 2100f)

        // Tembok kiri
        path.moveTo(line(2510f), line(1800f))
        path.lineTo(line(2510f), line(1500f))
        path.close()

        path.moveTo(line(2510f), line(1500f))
        path.lineTo(line(3410f), line(1500f))
        path.close()

        path.moveTo(line(3410f), line(1500f))
        path.lineTo(line(3410f), line(1200f))
        path.close()

        // A301
        createSquare(path, 3410f, -179f, 4010f, 1200f)

        // A302
        createSquare(path, 4010f, -179f, 4910f, 1200f)

        // A303
        createSquare(path, 4910f, -179f, 5810f, 1200f)

        // A304
        createSquare(path, 5810f, -179f, 6710f, 1200f)

        // A306
        createSquare(path, 3710f, 1500f, 4910f, 2557f)

        // A305
        createSquare(path, 4910f, 1500f, 6110f, 2557f)

        // Tembok kanan
        path.moveTo(line(6110f), line(1500f))
        path.lineTo(line(6710f), line(1500f))
        path.close()

        path.moveTo(line(6710f), line(1200f))
        path.lineTo(line(6710f), line(2557f))

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}