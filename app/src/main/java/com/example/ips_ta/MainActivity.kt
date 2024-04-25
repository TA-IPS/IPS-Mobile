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
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ips_ta.stepdetector.AccelSensorDetector
import com.example.ips_ta.stepdetector.OrientationDetector
import com.example.ips_ta.stepdetector.StepListener
import com.example.ips_ta.stepdetector.StepPosition
import com.example.ips_ta.stepdetector.TrajectoryCanvas
import com.example.ips_ta.stepdetector.TrajectoryViewModel
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.math.cos
import kotlin.math.sin
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

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.HIGH_SAMPLING_RATE_SENSORS),
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

    var isCounting by remember { mutableStateOf(false) }
    val trajectoryViewModel: TrajectoryViewModel = viewModel()
    var stepDetector: AccelSensorDetector? = AccelSensorDetector(context)
    var orientation by remember { mutableFloatStateOf(0f) }
    var stepCount by remember { mutableIntStateOf(0) }
    var orientationDetector: OrientationDetector = OrientationDetector(LocalContext.current) { azimuth ->
        orientation = azimuth
    }

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
                    UserIcon(userX = userX, userY = userY, userDirection = userDirection , ratio = ratio, trajectoryViewModel = trajectoryViewModel)

                }
//                TrajectoryCanvas(trajectoryViewModel = trajectoryViewModel)
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
                Button(onClick = {
                    isCounting = !isCounting
                    if (isCounting) {
                        stepDetector?.registerListener(object : StepListener {
                            override fun onStep(count: Int) {
                                Log.d("Tes", "userX: $userX userY: $userY")
                                stepCount += count
                                val orientationRadians = Math.toRadians(orientation.toDouble())
                                val deltaX = sin(orientationRadians) * 50f
                                val deltaY = -cos(orientationRadians) * 50f

                                userX += deltaX.toFloat()
                                userY += deltaY.toFloat()
                                userDirection = orientation

                                Log.d("Tes", "ORIENTATION: $orientation userX: $userX userY: $userY")
//                                trajectoryViewModel.addStep(orientation)
                            }
                        })
                        orientationDetector.start()
//                        trajectoryViewModel.addStep(orientation)
                        Log.d("Debug", "isCounting toggled On: $stepDetector")
                    } else {
                        Log.d("Debug", "isCounting toggled Check: $stepDetector")
                        stepDetector?.unregisterListener()
                        Log.d("Debug", "isCounting toggled OFF JING: $stepDetector")
                    }
                }) {
                    Text(text = if (isCounting) "Stop Counting" else "Start Counting")
                }
            }

        }

    }
}

@Composable
fun UserIcon( userX: Float, userY: Float, userDirection: Float, ratio: Float, trajectoryViewModel: TrajectoryViewModel){
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

            // PDR
//            trajectoryViewModel.addFirstStepCoordinates(userX * ratio, userY * ratio)
//            val start = trajectoryViewModel.stepPositions.first()
//            path.moveTo(start.x, start.y)

//            trajectoryViewModel.stepPositions.forEach { position ->
//                path.lineTo(position.x, position.y)
//            }

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

                val unregisteredBssid = mutableSetOf<Wifi>()

                LazyColumn(
                    userScrollEnabled = true,
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                ) {
                    items(wifiList) { item ->
                        var isExist = false

                        when (item.BSSID.uppercase()) {
                            Constants.AP1 -> { apValues["ap1"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP2 -> { apValues["ap2"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP3 -> { apValues["ap3"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP4 -> { apValues["ap4"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP5 -> { apValues["ap5"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP6 -> { apValues["ap6"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP7 -> { apValues["ap7"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP8 -> { apValues["ap8"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP9 -> { apValues["ap9"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP10 -> { apValues["ap10"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP11 -> { apValues["ap11"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP12 -> { apValues["ap12"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP13 -> { apValues["ap13"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP14 -> { apValues["ap14"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP15 -> { apValues["ap15"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP16 -> { apValues["ap16"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP17 -> { apValues["ap17"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP18 -> { apValues["ap18"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP19 -> { apValues["ap19"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP20 -> { apValues["ap20"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP21 -> { apValues["ap21"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP22 -> { apValues["ap22"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP23 -> { apValues["ap23"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP24 -> { apValues["ap24"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP25 -> { apValues["ap25"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP26 -> { apValues["ap26"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP27 -> { apValues["ap27"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP28 -> { apValues["ap28"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP29 -> { apValues["ap29"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP30 -> { apValues["ap30"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP31 -> { apValues["ap31"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP32 -> { apValues["ap32"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP33 -> { apValues["ap33"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP34 -> { apValues["ap34"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP35 -> { apValues["ap35"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP36 -> { apValues["ap36"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP37 -> { apValues["ap37"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP38 -> { apValues["ap38"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP39 -> { apValues["ap39"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP40 -> { apValues["ap40"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP41 -> { apValues["ap41"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP42 -> { apValues["ap42"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP43 -> { apValues["ap43"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP44 -> { apValues["ap44"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP45 -> { apValues["ap45"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP46 -> { apValues["ap46"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP47 -> { apValues["ap47"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP48 -> { apValues["ap48"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP49 -> { apValues["ap49"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP50 -> { apValues["ap50"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP51 -> { apValues["ap51"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP52 -> { apValues["ap52"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP53 -> { apValues["ap53"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP54 -> { apValues["ap54"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP55 -> { apValues["ap55"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP56 -> { apValues["ap56"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP57 -> { apValues["ap57"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP58 -> { apValues["ap58"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP59 -> { apValues["ap59"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP60 -> { apValues["ap60"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP61 -> { apValues["ap61"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP62 -> { apValues["ap62"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP63 -> { apValues["ap63"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP64 -> { apValues["ap64"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP65 -> { apValues["ap65"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP66 -> { apValues["ap66"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP67 -> { apValues["ap67"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP68 -> { apValues["ap68"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP69 -> { apValues["ap69"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP70 -> { apValues["ap70"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP71 -> { apValues["ap71"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP72 -> { apValues["ap72"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP73 -> { apValues["ap73"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP74 -> { apValues["ap74"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP75 -> { apValues["ap75"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP76 -> { apValues["ap76"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP77 -> { apValues["ap77"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP78 -> { apValues["ap78"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP79 -> { apValues["ap79"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP80 -> { apValues["ap80"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP81 -> { apValues["ap81"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP82 -> { apValues["ap82"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP83 -> { apValues["ap83"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP84 -> { apValues["ap84"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP85 -> { apValues["ap85"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP86 -> { apValues["ap86"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP87 -> { apValues["ap87"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP88 -> { apValues["ap88"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP89 -> { apValues["ap89"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP90 -> { apValues["ap90"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP91 -> { apValues["ap91"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP92 -> { apValues["ap92"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP93 -> { apValues["ap93"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP94 -> { apValues["ap94"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP95 -> { apValues["ap95"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP96 -> { apValues["ap96"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP97 -> { apValues["ap97"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP98 -> { apValues["ap98"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP99 -> { apValues["ap99"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP100 -> { apValues["ap100"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP101 -> { apValues["ap101"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP102 -> { apValues["ap102"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP103 -> { apValues["ap103"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP104 -> { apValues["ap104"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP105 -> { apValues["ap105"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP106 -> { apValues["ap106"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP107 -> { apValues["ap107"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP108 -> { apValues["ap108"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP109 -> { apValues["ap109"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP110 -> { apValues["ap110"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP111 -> { apValues["ap111"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP112 -> { apValues["ap112"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP113 -> { apValues["ap113"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP114 -> { apValues["ap114"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP115 -> { apValues["ap115"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP116 -> { apValues["ap116"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP117 -> { apValues["ap117"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP118 -> { apValues["ap118"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP119 -> { apValues["ap119"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP120 -> { apValues["ap120"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP121 -> { apValues["ap121"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP122 -> { apValues["ap122"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP123 -> { apValues["ap123"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP124 -> { apValues["ap124"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP125 -> { apValues["ap125"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP126 -> { apValues["ap126"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP127 -> { apValues["ap127"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP128 -> { apValues["ap128"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP129 -> { apValues["ap129"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP130 -> { apValues["ap130"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                        }

                        if (isExist) {
                            Column {
                                Text(text = "SSID: ${item.SSID}")
                                Text(text = "BSSID: ${item.BSSID}")
                                Text(text = "RSSI: ${item.level}")
                                Divider()
                            }
                        } else {
                            unregisteredBssid.add(Wifi(item.BSSID, item.SSID))
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
                        ap20 = apValues["ap20"],
                        ap21 = apValues["ap21"],
                        ap22 = apValues["ap22"],
                        ap23 = apValues["ap23"],
                        ap24 = apValues["ap24"],
                        ap25 = apValues["ap25"],
                        ap26 = apValues["ap26"],
                        ap27 = apValues["ap27"],
                        ap28 = apValues["ap28"],
                        ap29 = apValues["ap29"],
                        ap30 = apValues["ap30"],
                        ap31 = apValues["ap31"],
                        ap32 = apValues["ap32"],
                        ap33 = apValues["ap33"],
                        ap34 = apValues["ap34"],
                        ap35 = apValues["ap35"],
                        ap36 = apValues["ap36"],
                        ap37 = apValues["ap37"],
                        ap38 = apValues["ap38"],
                        ap39 = apValues["ap39"],
                        ap40 = apValues["ap40"],
                        ap41 = apValues["ap41"],
                        ap42 = apValues["ap42"],
                        ap43 = apValues["ap43"],
                        ap44 = apValues["ap44"],
                        ap45 = apValues["ap45"],
                        ap46 = apValues["ap46"],
                        ap47 = apValues["ap47"],
                        ap48 = apValues["ap48"],
                        ap49 = apValues["ap49"],
                        ap50 = apValues["ap50"],
                        ap51 = apValues["ap51"],
                        ap52 = apValues["ap52"],
                        ap53 = apValues["ap53"],
                        ap54 = apValues["ap54"],
                        ap55 = apValues["ap55"],
                        ap56 = apValues["ap56"],
                        ap57 = apValues["ap57"],
                        ap58 = apValues["ap58"],
                        ap59 = apValues["ap59"],
                        ap60 = apValues["ap60"],
                        ap61 = apValues["ap61"],
                        ap62 = apValues["ap62"],
                        ap63 = apValues["ap63"],
                        ap64 = apValues["ap64"],
                        ap65 = apValues["ap65"],
                        ap66 = apValues["ap66"],
                        ap67 = apValues["ap67"],
                        ap68 = apValues["ap68"],
                        ap69 = apValues["ap69"],
                        ap70 = apValues["ap70"],
                        ap71 = apValues["ap71"],
                        ap72 = apValues["ap72"],
                        ap73 = apValues["ap73"],
                        ap74 = apValues["ap74"],
                        ap75 = apValues["ap75"],
                        ap76 = apValues["ap76"],
                        ap77 = apValues["ap77"],
                        ap78 = apValues["ap78"],
                        ap79 = apValues["ap79"],
                        ap80 = apValues["ap80"],
                        ap81 = apValues["ap81"],
                        ap82 = apValues["ap82"],
                        ap83 = apValues["ap83"],
                        ap84 = apValues["ap84"],
                        ap85 = apValues["ap85"],
                        ap86 = apValues["ap86"],
                        ap87 = apValues["ap87"],
                        ap88 = apValues["ap88"],
                        ap89 = apValues["ap89"],
                        ap90 = apValues["ap90"],
                        ap91 = apValues["ap91"],
                        ap92 = apValues["ap92"],
                        ap93 = apValues["ap93"],
                        ap94 = apValues["ap94"],
                        ap95 = apValues["ap95"],
                        ap96 = apValues["ap96"],
                        ap97 = apValues["ap97"],
                        ap98 = apValues["ap98"],
                        ap99 = apValues["ap99"],
                        ap100 = apValues["ap100"],
                        ap101 = apValues["ap101"],
                        ap102 = apValues["ap102"],
                        ap103 = apValues["ap103"],
                        ap104 = apValues["ap104"],
                        ap105 = apValues["ap105"],
                        ap106 = apValues["ap106"],
                        ap107 = apValues["ap107"],
                        ap108 = apValues["ap108"],
                        ap109 = apValues["ap109"],
                        ap110 = apValues["ap110"],
                        ap111 = apValues["ap111"],
                        ap112 = apValues["ap112"],
                        ap113 = apValues["ap113"],
                        ap114 = apValues["ap114"],
                        ap115 = apValues["ap115"],
                        ap116 = apValues["ap116"],
                        ap117 = apValues["ap117"],
                        ap118 = apValues["ap118"],
                        ap119 = apValues["ap119"],
                        ap120 = apValues["ap120"],
                        ap121 = apValues["ap121"],
                        ap122 = apValues["ap122"],
                        ap123 = apValues["ap123"],
                        ap124 = apValues["ap124"],
                        ap125 = apValues["ap125"],
                        ap126 = apValues["ap126"],
                        ap127 = apValues["ap127"],
                        ap128 = apValues["ap128"],
                        ap129 = apValues["ap129"],
                        ap130 = apValues["ap130"],
                        ap131 = apValues["ap131"],
                        ap132 = apValues["ap132"],
                        ap133 = apValues["ap133"],
                        ap134 = apValues["ap134"],
                        ap135 = apValues["ap135"],
                        ap136 = apValues["ap136"],
                        ap137 = apValues["ap137"],
                        ap138 = apValues["ap138"],
                        ap139 = apValues["ap139"],
                        ap140 = apValues["ap140"],
                        wifi = unregisteredBssid
                    )
                    Log.v("wifis", unregisteredBssid.toString())
                    CoroutineScope(Dispatchers.IO).launch {
                        RetrofitInstance.apiService.postFingerprint(fingerprint)
                    }
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

