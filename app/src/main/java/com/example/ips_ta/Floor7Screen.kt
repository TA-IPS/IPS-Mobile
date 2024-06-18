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
fun Floor7Screen(ratio: Float) {
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

        // A716
        createSquare(path, 3710f, 1493f, 4870f , 2550f)
        drawLabel(line((3710f + 4870f)/2), line((1493f + 2550f)/2), "A716")

        // A715
        createSquare(path, 4870f, 1493f, 5770f , 2550f)
        drawLabel(line((4870f + 5770f)/2), line((1493f + 2550f)/2), "A715")

        // A714
        createSquare(path, 5770f, 1493f, 6670f , 2550f)
        drawLabel(line((5770f + 6670f)/2), line((1493f + 2550f)/2), "A714")

        // A712
        createSquare(path, 4870f, -190f, 5770f , 1193f)
        drawLabel(line((4870f + 5770f)/2), line((-190f + 1193f)/2), "A712")

        // A713
        createSquare(path, 5770f, -190f, 6670f , 1193f)
        drawLabel(line((5770f + 6670f)/2), line((-190f + 1193f)/2), "A713")

        // A711
        createSquare(path, 4270f, -190f, 4870f , 1193f)
        drawLabel(line((4270f + 4870f)/2), line((-190f + 1193f)/2), "A711")

        // A707
        createSquare(path, 3920f, -190f, 4270f , 293f)
        drawLabel(line((3920f + 4270f)/2), line((-190f + 293f)/2), "A707")

        // A708
        createSquare(path, 3920f, 293f, 4270f , 593f)
        drawLabel(line((3920f + 4270f)/2), line((293f + 593f)/2), "A708")

        // A709
        createSquare(path, 3920f, 593f , 4270f , 893f)
        drawLabel(line((3920f + 4270f)/2), line((593f + 893f)/2), "A709")

        // A710
        createSquare(path, 3920f, 893f, 4270f , 1193f)
        drawLabel(line((3920f + 4270f)/2), line((893 + 1193f)/2), "A710")


        // A703
        createSquare(path, 3395f, -190f, 3720f , 293f)
        drawLabel(line((3395f + 3720f)/2), line((-190f + 293f)/2), "A703")

        // A704
        createSquare(path, 3395f, 293f, 3720f , 593f)
        drawLabel(line((3395f + 3720f)/2), line((293f + 593f)/2), "A704")

        // A705
        createSquare(path, 3395f, 593f , 3720f , 893f)
        drawLabel(line((3395f + 3720f)/2), line((593f + 893f)/2), "A705")

        // A716
        createSquare(path, 3395f, 893f, 3720f , 1193f)
        drawLabel(line((3395f + 3720f)/2), line((893 + 1193f)/2), "A706")

        // A702
        createSquare(path, 790f, -190f, 3395f , 1493f)
        drawLabel(line((790f + 3395f)/2), line((-190f + 1493f)/2), "A702")

        // A701
        createSquare(path, 790f, 1493f, 1871f , 2550f)
        drawLabel(line((790f + 1871f)/2), line((1493f + 2550f)/2), "A701")

        // Garis A701 - Toilet
        path.moveTo(line(1871f), line(2550f))
        path.lineTo(line(2460f), line(2550f))
        path.close()

        // Garis A703 - A707
        path.moveTo(line(3720f), line(-190f))
        path.lineTo(line(3920f), line(-190f))
        path.close()

        // Garis A713 - A714
        path.moveTo(line(6670f), line(1193f))
        path.lineTo(line(6670f), line(1493f))
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

