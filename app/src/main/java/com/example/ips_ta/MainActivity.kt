package com.example.ips_ta

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                MapScreen()
            }
        }
    }
}







@Composable
fun MapScreen() {
    val context = LocalContext.current
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var wifiList by remember { mutableStateOf<List<ScanResult>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    // Register BroadcastReceiver to listen for scan results
    val wifiScanReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    // Scan was successful, update wifiList
                    wifiList = wifiManager.scanResults
                } else {
                    // Scan failed, handle failure
                    println("Wi-Fi scan failed")
                }
            }
        }
    }
    val intentFilter = IntentFilter().apply {
        addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    }
    DisposableEffect(context) {
        context.registerReceiver(wifiScanReceiver, intentFilter)
        onDispose {
            context.unregisterReceiver(wifiScanReceiver)
        }
    }
    // Launch coroutine to scan for Wi-Fi networks every 2 seconds
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(2000) // Delay for 2 seconds
//            val success = wifiManager.startScan()
//            if (!success) {
//                // Scan failure handling
//                println("Wi-Fi scan failed")
//            }
//        }
//    }

    Timer().schedule(object : TimerTask() {
        override fun run() {
            wifiManager.startScan()
        }
    }, 2000)





    var top by remember { mutableFloatStateOf(0f) }
    var left by remember { mutableFloatStateOf(0f) }
    var ratio by remember { mutableFloatStateOf(1f) }

    var userX by remember { mutableFloatStateOf(150f) }
    var userY by remember { mutableFloatStateOf(150f) }
    var userLantai by remember { mutableIntStateOf(1) }
    var lantai by remember { mutableIntStateOf(1) }
    var userDirection by remember { mutableFloatStateOf(180f) }

    var showDialog by remember { mutableStateOf(false) }
//    val screenList = listOf(Floor0Screen(ratio),Floor1Screen(ratio),Floor2Screen(ratio))
    val floorLabels = listOf("Dasar", "Lantai 1", "Lantai 2", "Lantai 3", "Lantai 4", "Lantai 5", "Lantai 6")


    Column {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = { ratio++ },
                enabled = true,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { ratio-- },
                enabled = ratio > 1,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Zoom Out")
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { top=-userY*ratio+800 ; left =-userX*ratio + 525; },
                modifier = Modifier.size(48.dp)) {

                Icon(Icons.Default.LocationOn, contentDescription = "Center")
            }

            // User Gerak
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userY-=100 ; userDirection = 0f; },
                enabled = userY > 100f,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
            }
            // User Gerak
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userY+=100 ; userDirection = 180f; },
                enabled = userY + 100< 600f,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userX+=100 ; userDirection = 90f; },
                enabled = userX + 100< 800f,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Move Right")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userX-=100 ; userDirection = 270f; },
                enabled = userX > 100f,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Move Left")
            }
        }
        Text(text = floorLabels[lantai], style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

        if (showDialog) {
            WifiListDialog(wifiList = wifiList, onClose = { showDialog = false })
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        top += dragAmount.y
                        left += dragAmount.x
                    }
                }
        ) {
            Box(
                Modifier
                    .offset { IntOffset(left.toInt(), top.toInt()) }

            ) {
                // Cek Jika Level 0 tampilkan Floor0Screen, Level 1 tampilkan Floor1Screen
                if(userLantai == lantai){
                    UserIcon(userX = userX, userY = userY, userDirection = userDirection , ratio = ratio)
                }

                when(lantai){
                    0 -> Floor0Screen(ratio = ratio)
                    1 -> Floor1Screen(ratio = ratio)
                    2 -> Floor2Screen(ratio = ratio)
                    3 -> Floor3Screen(ratio = ratio)
                    4 -> Floor4Screen(ratio = ratio)
                    5 -> Floor5Screen(ratio = ratio)
                    6 -> Floor6Screen(ratio = ratio)
                }
            }


            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Show Modal")
                }
                floorLabels.forEachIndexed { index, label ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { lantai = index },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(100.dp)
                            .height(40.dp)
                    ) {
                        Text(label)
                    }
                }
            }

        }

    }
}

@Composable
fun UserIcon( userX: Float, userY: Float, userDirection: Float, ratio: Float){
    Canvas(modifier = Modifier.fillMaxSize()
    ) {
        val userCenter = Offset(userX * ratio, userY * ratio)
        rotate(userDirection, pivot = userCenter){
            // Draw user
            drawCircle(
                color = Color.Blue,
                center = Offset(userX * ratio, userY * ratio),
                radius = 15f * ratio
            )

            val path = Path()
            path.moveTo((userX + 15f) * ratio, (userY-15f) * ratio)
            path.lineTo((userX + 0f) * ratio, (userY-25f) * ratio)
            path.lineTo((userX - 15f) * ratio, (userY-15f) * ratio)

            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}










@Composable
fun WifiListDialog(wifiList: List<ScanResult>, onClose: () -> Unit ) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Wi-Fi Information",
                    style = MaterialTheme.typography.headlineMedium, color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = "Pada Grid 1 Lantai 1",
                    style = MaterialTheme.typography.bodyLarge, color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                LazyColumn(
                    userScrollEnabled = true,
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                ) {
                    items(wifiList) { item ->
                        Column {
                            Text(text = "SSID: ${item.SSID}")
                            Text(text = "BSSID: ${item.BSSID}")
                            Text(text = "RSSI: ${item.level}")
                            Divider()
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Close")
                }
            }
        }
    }
}

