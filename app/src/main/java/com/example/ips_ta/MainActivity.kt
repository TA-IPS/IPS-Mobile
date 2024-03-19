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
import androidx.activity.compose.setContent
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random


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

    var top by remember { mutableFloatStateOf(0f) }
    var left by remember { mutableFloatStateOf(0f) }
    var ratio by remember { mutableFloatStateOf(0.2f) }

    var userX by remember { mutableFloatStateOf(150f) }
    var userY by remember { mutableFloatStateOf(150f) }
    var userLantai by remember { mutableIntStateOf(1) }
    var lantai by remember { mutableIntStateOf(1) }
    var userDirection by remember { mutableFloatStateOf(180f) }

    var showDialog by remember { mutableStateOf(false) }
//    val screenList = listOf(Floor0Screen(ratio),Floor1Screen(ratio),Floor2Screen(ratio))
    val floorLabels = listOf("Dasar", "Lantai 1", "Lantai 2", "Lantai 3", "Lantai 4", "Lantai 5", "Lantai 6")

    var isScanning by remember { mutableStateOf(false) }
    var isPersonFocused by remember { mutableStateOf(false) }
    var isUserIconShown by remember { mutableStateOf(false) }
    var currentCondition by remember { mutableStateOf("") }

    val randomX = List(60) { (it + 1) * 100 }
    val randomY = List(20) { (it + 1) * 100 }
    val randomZ = List(2) { (it) }
    LaunchedEffect(isScanning) {
        if (!isScanning) {
            isPersonFocused = true
            currentCondition = "Determining Location"

            while (true) {
                delay(3000) // Delay for 2 seconds
                //tembak api
                val chosenX = randomX[Random.nextInt(randomX.size)]
                val chosenY = randomY[Random.nextInt(randomY.size)]
                val chosenZ = randomZ[Random.nextInt(randomZ.size)]

                isUserIconShown = true
                userX = chosenX.toFloat()
                userY = chosenY.toFloat()
                userLantai = chosenZ

                currentCondition = "${floorLabels[chosenZ]} x: $chosenX y: $chosenY"

                if (isPersonFocused) {
                    ratio = 0.6f
                    top=-userY * ratio + 800
                    left =-userX * ratio + 525
                    lantai = chosenZ
                }
            }
        }
    }

    Timer().schedule(object : TimerTask() {
        override fun run() {
            wifiManager.startScan()
        }
    }, 2000)


    Column {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = { ratio += 0.2f },
                enabled = true,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { ratio -= 0.2f },
                enabled = ratio > 0.201f,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Zoom Out")
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { ratio = 0.6f; top=-userY*ratio+800 ; left =-userX*ratio + 525; isPersonFocused = true},
                modifier = Modifier.size(48.dp)) {

                Icon(Icons.Default.LocationOn, contentDescription = "Center")
            }

            // User Gerak
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userY-=100 ; userDirection = 0f;  Log.d("User", "User Y: $userY, User X: $userX")},
                enabled = userY > 100f && isScanning,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
            }
            // User Gerak
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userY+=100 ; userDirection = 180f; },
                enabled = userY + 100 < 2340f && isScanning,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userX+=100 ; userDirection = 90f; },
                enabled = userX + 100 < 6120f && isScanning,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Move Right")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { userX-=100 ; userDirection = 270f; },
                enabled = userX > 100f && isScanning,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Move Left")
            }
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = floorLabels[lantai],
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Switch(
                checked = isScanning,
                onCheckedChange = {
                    isScanning = it
                    if (!isScanning) {
                        isUserIconShown = false
                    } else {
                        currentCondition = "Mode input data"
                    }
                }
            )
        }
        Text(
            text = currentCondition,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        if (showDialog) {
            WifiListDialog(wifiList = wifiList, onClose = { showDialog = false }, floor = lantai, x = userX.toInt(), y = userY.toInt())
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        top += dragAmount.y
                        left += dragAmount.x
                        isPersonFocused = false
                    }
                }
        ) {
            Box(
                Modifier
                    .offset { IntOffset(left.toInt(), top.toInt()) }

            ) {
                // Cek Jika Level 0 tampilkan Floor0Screen, Level 1 tampilkan Floor1Screen
                if(lantai == userLantai && isUserIconShown){
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
                if (isScanning) {
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Show Modal")
                    }
                }
                floorLabels.forEachIndexed { index, label ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { lantai = index
                                  userLantai = index
                                  isPersonFocused = false },
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
fun WifiListDialog(wifiList: List<ScanResult>, onClose: () -> Unit, floor: Int, x: Int, y: Int) {
    val apValues = mutableMapOf<String, Float?>()

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
                Text(text = "Pada Grid x:$x y:$y Lantai $floor",
                    style = MaterialTheme.typography.bodyLarge, color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                LazyColumn(
                    userScrollEnabled = true,
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                ) {
                    items(wifiList) { item ->


                        when (item.BSSID) {
                            Constants.AP1 -> apValues["ap1"] = item.level.toFloat()
                            Constants.AP2 -> apValues["ap2"] = item.level.toFloat()
                            Constants.AP3 -> apValues["ap3"] = item.level.toFloat()
                            Constants.AP4 -> apValues["ap4"] = item.level.toFloat()
                            Constants.AP5 -> apValues["ap5"] = item.level.toFloat()
                            Constants.AP6 -> apValues["ap6"] = item.level.toFloat()
                            Constants.AP7 -> apValues["ap7"] = item.level.toFloat()
                            Constants.AP8 -> apValues["ap8"] = item.level.toFloat()
                            Constants.AP9 -> apValues["ap9"] = item.level.toFloat()
                            Constants.AP10 -> apValues["ap10"] = item.level.toFloat()
                            Constants.AP11 -> apValues["ap11"] = item.level.toFloat()
                            Constants.AP12 -> apValues["ap12"] = item.level.toFloat()
                            Constants.AP13 -> apValues["ap13"] = item.level.toFloat()
                            Constants.AP14 -> apValues["ap14"] = item.level.toFloat()
                            Constants.AP15 -> apValues["ap15"] = item.level.toFloat()
                            Constants.AP16 -> apValues["ap16"] = item.level.toFloat()
                            Constants.AP17 -> apValues["ap17"] = item.level.toFloat()
                            Constants.AP18 -> apValues["ap18"] = item.level.toFloat()
                            Constants.AP19 -> apValues["ap19"] = item.level.toFloat()
                            Constants.AP20 -> apValues["ap20"] = item.level.toFloat()
                        }

                        Column {
                            Text(text = "SSID: ${item.SSID}")
                            Text(text = "BSSID: ${item.BSSID}")
                            Text(text = "RSSI: ${item.level}")
                            Divider()
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val fingerprint = Fingerprint(
                        attempt = 1,
                        x = x,
                        y = y,
                        z = floor,
                        ap1 = apValues["ap1"],
                        ap2 = apValues["ap2"],
                        ap3 = apValues["ap3"],
                        ap4 = apValues["ap4"],
                        ap5 = apValues["ap5"],
                        ap6 = apValues["ap6"],
                        ap7 = apValues["ap7"],
                        ap8 = apValues["ap8"],
                        ap9 = apValues["ap9"],
                        ap10 = apValues["ap10"],
                        ap11 = apValues["ap11"],
                        ap12 = apValues["ap12"],
                        ap13 = apValues["ap13"],
                        ap14 = apValues["ap14"],
                        ap15 = apValues["ap15"],
                        ap16 = apValues["ap16"],
                        ap17 = apValues["ap17"],
                        ap18 = apValues["ap18"],
                        ap19 = apValues["ap19"],
                        ap20 = apValues["ap20"]
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        RetrofitInstance.apiService.postFingerprint(fingerprint)
                    }
                    Log.v("Fingerprint", apValues.toString())
                },
                    modifier = Modifier.fillMaxWidth()) {
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

