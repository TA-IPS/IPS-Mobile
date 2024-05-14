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
fun Floor4Screen(ratio: Float) {
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

        // A405
        path.moveTo(line(834f), line(-177.5f))
        path.lineTo(line(1610f), line(-177.5f))
        path.lineTo(line(1610f), line(440f))
        path.lineTo(line(1310f), line(440f))
        path.lineTo(line(1310f), line(240f))
        path.lineTo(line(834f), line(240f))
        path.close()

        // A404
        createSquare(path, 834f, 240f, 1310f, 640f)

        // A403
        createSquare(path, 834f, 640f, 1310f, 1150f)

        // A402
        createSquare(path, 834f, 1150f, 1310f, 1500f)

        // A401
        createSquare(path, 834f, 1500f, 1310f, 1800f)

        // A406
        createSquare(path, 1610f, -177.5f, 2210f, 900f)

        // A407
        createSquare(path, 1610f, 900f, 2210f, 1200f)

        // A408
        createSquare(path, 1610f, 1200f, 2210f, 1500f)

        // A409
        createSquare(path, 2210f, -177.5f, 3410f, 1500f)

        // A409 lain
        createSquare(path, 3410f, -177.5f, 4010f, 1500f)

        // A412
        createSquare(path, 4010f, -177.5f, 4360f, 590f)

        // A411
        createSquare(path, 4010f, 590f, 4360f, 1200f)

        // A410
        createSquare(path, 4010f, 1200f, 4360f, 1500f)

        // A413
        createSquare(path, 4660f, -177.5f, 4910f, 590f)

        // A414
        createSquare(path, 4660f, 590f, 4910f, 1200f)

        // A415
        createSquare(path, 4660f, 1200f, 4910f, 1500f)

        // A416
        createSquare(path, 4910f, -177.5f, 5260f, 590f)

        // A417
        createSquare(path, 4910f, 590f, 5260f, 1200f)

        // A418
        createSquare(path, 4910f, 1200f, 5260f, 1500f)

        // A419
        createSquare(path, 5560f, -177.5f, 5810f, 590f)

        // A420
        createSquare(path, 5560f, 590f, 5810f, 1200f)

        // A421
        createSquare(path, 5560f, 1200f, 5810f, 1500f)

        // A422
        createSquare(path, 5810f, -177.5f, 6160f, 240f)

        // A423
        createSquare(path, 5810f, 240f, 6160f, 590f)

        // A424
        createSquare(path, 5810f, 590f, 6160f, 895f)

        // A425
        createSquare(path, 5810f, 895f, 6160f, 1200f)

        // A426
        createSquare(path, 5810f, 1200f, 6160f, 1500f)

        // A427
        createSquare(path, 6360f, -177.5f, 6710f, 240f)

        // A428
        createSquare(path, 6360f, 240f, 6710f, 590f)

        // A429
        createSquare(path, 6360f, 590f, 6710f, 895f)

        // A430
        createSquare(path, 6360f, 895f, 6710f, 1200f)

        // A431
        createSquare(path, 6360f, 1200f, 6710f, 1500f)

        // A441
        createSquare(path, 3710f, 1800f, 4010f, 2550f)

        // A439
        createSquare(path, 4010f, 1800f, 4460f, 2550f)

        // A438
        createSquare(path, 4460f, 1800f, 4910f, 2550f)

        // A437
        createSquare(path, 4910f, 1800f, 5260f, 2100f)

        // A436
        createSquare(path, 4910f, 2100f, 5210f, 2550f)

        // A435
        createSquare(path, 5210f, 2250f, 5510f, 2550f)

        // A434
        createSquare(path, 5510f, 2250f, 5810f, 2550f)

        // A433
        createSquare(path, 5810f, 2100f, 6110f, 2550f)

        // A432
        createSquare(path, 5760f, 1800f, 6110f, 2100f)

        // void bawah
        path.moveTo(line(6110f), line(1800f))
        path.lineTo(line(6710f), line(1800f))
        path.close()

        path.moveTo(line(6710f), line(1800f))
        path.lineTo(line(6710f), line(1500f))
        path.close()

        // void atas
        path.moveTo(line(4360f), line(-177.5f))
        path.lineTo(line(6360f), line(-177.5f))
        path.close()

        // Jalan toilet
        path.moveTo(line(1310f), line(1800f))
        path.lineTo(line(1910f), line(1800f))
        path.close()

        path.moveTo(line(1910f), line(1800f))
        path.lineTo(line(1910f), line(2250f))
        path.close()

        path.moveTo(line(1910f), line(2250f))
        path.lineTo(line(2460f), line(2250f))
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