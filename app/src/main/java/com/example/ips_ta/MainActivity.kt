package com.example.ips_ta

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ips_ta.stepdetector.AccelSensorDetector
import com.example.ips_ta.stepdetector.OrientationDetector
import com.example.ips_ta.stepdetector.StepListener
import com.example.ips_ta.stepdetector.TrajectoryViewModel
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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







@OptIn(ExperimentalMaterial3Api::class)
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
            @SuppressLint("MissingPermission")
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

//    Timer().schedule(object : TimerTask() {
//        override fun run() {
//            Log.v("Timer", "Scanning")
//            wifiManager.startScan()
//        }
//    }, 6000)

    wifiManager.startScan()


    var top by remember { mutableFloatStateOf(0f) }
    var left by remember { mutableFloatStateOf(0f) }
    var ratio by remember { mutableFloatStateOf(0.375f) }

    var userX by remember { mutableFloatStateOf(150f) }
    var userY by remember { mutableFloatStateOf(150f) }
    var userLantai by remember { mutableIntStateOf(1) }
    var lantai by remember { mutableIntStateOf(1) }
    var userDirection by remember { mutableFloatStateOf(180f) }

    var showDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    val floorLabels = listOf("D", "1", "2", "3", "4", "5", "6")

    var isLocalizationMode by remember { mutableStateOf(true) }
    var isRegistrationMode by remember { mutableStateOf(false) }
    var isMLMode by remember { mutableStateOf(false) }

    var isPersonFocused by remember { mutableStateOf(false) }
    var isUserIconShown by remember { mutableStateOf(false) }
    var currentCondition by remember { mutableStateOf("") }
    var isFetching by remember { mutableStateOf(true) }
    var confidenceList by remember { mutableStateOf("")}

    var isScanningActive by remember { mutableStateOf(true) }
    var isPdrActive by remember { mutableStateOf(false) }
    val trajectoryViewModel: TrajectoryViewModel = viewModel()
    var stepDetector: AccelSensorDetector? = AccelSensorDetector(context)
    var stepListener: StepListener? = null
    var orientation by remember { mutableFloatStateOf(0f) }
    var stepCount by remember { mutableIntStateOf(0) }
    var orientationDetector = OrientationDetector(LocalContext.current) { azimuth ->
        orientation = azimuth
    }
    var isPdrResultTaken by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset(-300f, 0f)) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        ratio *= zoomChange
        offset += offsetChange
        isPersonFocused = false
    }
    var isFirstPrediction by remember { mutableStateOf(true) }
    var belakang by remember { mutableStateOf(false) }
    var mlUserIcon by remember { mutableStateOf<List<Prediction>>(emptyList()) }

    var isInitialScan by remember { mutableStateOf(false) }
    var isPeriodicScanning by remember { mutableStateOf(false) }
    var requireImmediateScan by remember { mutableStateOf(false) }
    var predictionLists by remember { mutableStateOf<List<PredictionList>>(emptyList())}
    val minimumPredictionCount = 3

    var grids by remember { mutableStateOf<Grids?>(null) }
    var isGridsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(isGridsLoaded) {
        if (grids == null) {
            try {
                Log.v("Grids", "mencoba ngambil grid")
                grids = getRecordedGrids()
                Log.v("Grids", grids.toString())
            } catch (e: Exception) {
                Log.e("Fetching Grid", "Error: ${e.message}")
                Toast.makeText(context, "Error Fetching Grid: ${e.message}", Toast.LENGTH_SHORT).show()
                isGridsLoaded = !isGridsLoaded
            }
        }
    }

    LaunchedEffect(wifiList) {
        if (wifiList.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    if (isLocalizationMode) {
                        // Kasus fetch banyak & cepet buat initial location
                        if (isInitialScan && predictionLists.size < minimumPredictionCount) {
                            val apList = getApAssignment(wifiList)
                            val predictions = getPrediction(apList)
                            predictionLists += listOf(predictions)

                            Log.v("Prediksi", "Dapet prediksi ke-${predictionLists.size} dari ml service")
                        } else if (!isInitialScan && !isPeriodicScanning && predictionLists.size < minimumPredictionCount) {
                            Log.v("Scan", "pindah ke scanning cepet")
                            val apList = getApAssignment(wifiList)
                            val predictions = getPrediction(apList)
                            predictionLists += listOf(predictions)

                            // Panggil lagi algoritma lu disini. Kalo udah kelar, set lagi isPeriodicScanning jadi true
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error: ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(isLocalizationMode, isScanningActive, isPdrActive) {
        if (isLocalizationMode) {
            predictionLists = emptyList()

            currentCondition = "Determining Location"
            isFetching = true

            isPdrResultTaken = false

            isPeriodicScanning = false
            isInitialScan = true
        }
    }

    // Buat handle initial scan di awal awal
    if (isInitialScan && predictionLists.size == minimumPredictionCount) {
        Log.v("Initial scan", "Masuk judging prediction yang ada")

        isInitialScan = false

        val coordinates = determineBestPrediction(predictionLists)
        isUserIconShown = true
        userX = coordinates.first.toFloat()
        userY = coordinates.second.toFloat()
        userLantai = coordinates.third.toInt()

        if (isPdrActive) {
            isPdrResultTaken = true
        }

        isFetching = false
        currentCondition = "X: ${userX.toInt()} Y: ${userY.toInt()} Lt: $userLantai"

        predictionLists = emptyList()
        isPeriodicScanning = true
    }

    if (!isInitialScan && predictionLists.size == minimumPredictionCount) {

        val candidateCoordinate = determineBestPrediction(predictionLists)
        val result = compareToPrediction(candidateCoordinate, grids!!, userX, userY, userLantai)

        if (result != null) {
            isUserIconShown = true
            userX = result.first
            userY = result.second
            userLantai = result.third
        }

        if (isPdrActive) {
            isPdrResultTaken = true
        }

        isFetching = false
        currentCondition = "X: ${userX.toInt()} Y: ${userY.toInt()} Lt: $userLantai"

        predictionLists = emptyList()
        isPeriodicScanning = true
    }

    // Ketika mulai masuk ke scan tiap 10 detik
    LaunchedEffect(isPeriodicScanning) {
        if (isPeriodicScanning && isScanningActive) {
            while (isPeriodicScanning) {
                Log.v("Scan", "ngelakuin periodic scanning")
                delay(10000)

                    try {
                        val apList = getApAssignment(wifiList)
                        val predictions = getPrediction(apList)
                        predictionLists += listOf(predictions)
                        isPeriodicScanning = false
                        /*
                            Lakuin algoritmalu disini, kalo misalkan mau jadi scan tiap 1.5 detik
                            set isPeriodicScanning nya jadi false.
                        */

                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error: ${e.message}")
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
        }

    }

//    LaunchedEffect(isLocalizationMode, isScanningActive, isPdrActive) {
//        if (isLocalizationMode) {
//            currentCondition = "Determining Location"
//            isFetching = true
//            isPdrResultTaken = false
//
//            val minimumSteps = 4
//            var isUncertain = false
//            var isInitialScan = true
//
//            delay(5000) // Delay for 5 seconds
//            do {
//                CoroutineScope(Dispatchers.Main).launch {
//                    try {
//                        // Kalo initial scan, PDR gak aktif, atau step yang dilakuin banyak bakal scan
//                        if (!(stepCount < minimumSteps && isPdrActive && !isInitialScan)) {
//                            isFetching = true
//                            currentCondition = "Determining Location"
//
//                            Log.v("WifiList", "ngambil wifi")
//                            val apList = getApAssignment(wifiList)
//                            val predictions = getPrediction(apList)
//                            val coordinates = processPrediction(predictions, userX, userY, userLantai, isPdrActive, isUncertain, isInitialScan)
//                            stepCount = 0
//
//                            if (coordinates != null) {
//                                isUserIconShown = true
//                                userX = coordinates.x.toFloat()
//                                userY = coordinates.y.toFloat()
//                                userLantai = coordinates.z.toInt()
//
//                                if (isPdrActive) {
//                                    isPdrResultTaken = true
//                                }
//
//                                isInitialScan = false
//
//                                if (isUncertain) {
//                                    Toast.makeText(context, "Due to previous uncertainty, new location is updated", Toast.LENGTH_SHORT).show()
//                                }
//
//                                isUncertain = false
//                            } else {
//                                if (!isInitialScan) {
//                                    Toast.makeText(context, "New location is far, continuing in PDR", Toast.LENGTH_SHORT).show()
//                                    isUncertain = true
//
//                                    if (isPdrActive) {
//                                        isPdrResultTaken = true
//                                    }
//                                } else {
//                                    Toast.makeText(context, "Confidence too close, re-scanning", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            confidenceList = ""
//                            for (prediction in predictions.data) {
//                                confidenceList += "${prediction.x}, ${prediction.y}, ${prediction.z}: ${prediction.confidence}\n"
//                            }
//                            currentCondition = "X: ${userX.toInt()} Y: ${userY.toInt()} Lt: $userLantai"
//                        // Kalo step countnya dikit, lanjut pdr
//                        } else {
//                            isPdrResultTaken = true
//                            Toast.makeText(context, "Few movement detected, continuing in PDR", Toast.LENGTH_SHORT).show()
//                        }
//                        isFetching = false
//                    } catch (e: Exception) {
//                        Log.e("MainActivity", "Error: ${e.message}")
//                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                delay(5000) // Delay for 5 seconds
//            } while (isScanningActive)
//        }
//    }

    LaunchedEffect(isMLMode, isScanningActive) {
        if (isMLMode) {
            currentCondition = "Determining Location"
            isUserIconShown = false
            isFetching = true

            do {
                CoroutineScope(Dispatchers.Main).launch {
                    isFetching = true
                    currentCondition = "Determining Location"
                    try {
                        val predictions = getMultiPrediction(getApAssignment(wifiList))
                        mlUserIcon = predictions
                        isFetching = false
                        currentCondition = "ML Mode"
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error: ${e.message}")
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                delay(5000) // Delay for 5 seconds
            } while (isScanningActive)
        }
    }

    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentCondition,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { showSettings = true}
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Show Settings")
            }
        }
        if (isFetching) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = confidenceList,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
//        Text(
//            text = "Step Count: $stepCount",
//            style = MaterialTheme.typography.bodySmall,
//            textAlign = TextAlign.Center
//        )
//        Text(
//            text = wifiList.fold("") { acc, scanResult -> "$acc${scanResult.BSSID} ${scanResult.level}\n" },
//            style = MaterialTheme.typography.bodySmall,
//            textAlign = TextAlign.Center
//        )
        if (showSettings) {
            SettingsDialog(
                isLocalization = isLocalizationMode,
                isRegistration = isRegistrationMode,
                isMlTest = isMLMode,
                isPdrActive = isPdrActive,
                isScanningActive = isScanningActive,
            ) {
                pdrActive, scanningActive, localization, registration, mlTest ->
                isPdrActive = pdrActive
                if (!pdrActive) {
                    isPdrResultTaken = false
                }
                isScanningActive = scanningActive
                isLocalizationMode = localization
                isRegistrationMode = registration
                isMLMode = mlTest
                showSettings = false

                if (isPdrActive) {
                    stepDetector?.registerListener(object : StepListener {
                        override fun onStep(count: Int) {
                            if (isPdrResultTaken) {
                                Log.d("Tes", "userX: $userX userY: $userY")
                                stepCount += count
                                val orientationRadians = Math.toRadians(orientation.toDouble())
                                val deltaX = sin(orientationRadians) * 50f
                                val deltaY = -cos(orientationRadians) * 50f

                                userX += deltaX.toFloat()
                                userY += deltaY.toFloat()
                                userDirection = orientation

                                Log.d(
                                    "Tes",
                                    "ORIENTATION: $orientation userX: $userX userY: $userY"
                                )
//                                trajectoryViewModel.addStep(orientation)
                            }
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
            }
        }

        if (showDialog) {
            WifiListDialog(wifiList = wifiList, onClose = { showDialog = false }, floor = lantai, x = userX.toInt(), y = userY.toInt())
        }

        if (showInfo) {
            InfoDialog(mlUserIcon = mlUserIcon, onClose = { showInfo = false })
        }

        Box(
            Modifier
                .fillMaxSize()
                .transformable(state = state)
        ) {
            Box(
                Modifier
                    .graphicsLayer(
                        scaleX = ratio,
                        scaleY = ratio,
                        translationX = offset.x,
                        translationY = offset.y
                    )

            ) {
                if (lantai == userLantai && isUserIconShown && isLocalizationMode) {
                    UserIcon(userX = userX, userY = userY, userDirection = userDirection , ratio = ratio, trajectoryViewModel = trajectoryViewModel, isMlMode = false, mlModel = "")
                }
                if (isRegistrationMode) {
                    isFetching = false
                    currentCondition = "Registration Mode"
                    userLantai = lantai
                    UserIcon(userX = userX, userY = userY, userDirection = userDirection , ratio = ratio, trajectoryViewModel = trajectoryViewModel, isMlMode = false, mlModel = "")
                }
                if (isMLMode && mlUserIcon.isNotEmpty()) {
                    val locations: MutableMap<List<Int>, MutableList<String>> = mutableMapOf()
                    for ((index, prediction) in mlUserIcon.withIndex()) {
                        val coordinates = prediction.predicted_location.split(",")
                        locations.getOrPut(listOf(coordinates[0].toInt(), coordinates[1].toInt(), coordinates[2].toInt())) {
                            mutableListOf()
                        }.add(getModelName(index))
                    }
                    for ((coordinates, models) in locations) {
                        if (coordinates[2] == lantai) {
                            UserIcon(
                                userX = coordinates[0].toFloat(),
                                userY = coordinates[1].toFloat(),
                                userDirection = orientation,
                                ratio = ratio,
                                trajectoryViewModel = trajectoryViewModel,
                                isMlMode = true,
                                mlModel = models.joinToString(", ")
                            )
                        }
                    }
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

            if (isPersonFocused) {
                ratio = 0.6f
                offset = Offset(-userX * ratio + 1000, -userY * ratio + 800)
                lantai = userLantai
            }

            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                floorLabels.forEachIndexed { index, label ->
                    if (index == lantai) {
                        Button(
                            onClick = {
                                lantai = index
                                isPersonFocused = false
                            },
                            shape = RectangleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        ) {
                            Text(label)
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                lantai = index
                                isPersonFocused = false
                            },
                            shape = RectangleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            }
            if (isLocalizationMode) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {
                            isPersonFocused = true
//                            ratio = 0.6f
//                            offset = Offset(-userX * ratio + 1000, -userY * ratio + 800)
//                            lantai = userLantai
                        },
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "List")
                    }
                }
            }
            if (isMLMode) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {
                            showInfo = true
                        },
                    ) {
                        Icon(Icons.Default.List, contentDescription = "List")
                    }
                }
            }
            if (isRegistrationMode) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        Modifier
                            .padding(start = 64.dp, bottom = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { userY -= 500; userDirection = 0f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_double_arrow_up_foreground),
                                    contentDescription = "Move Double Up"
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { userY -= 100; userDirection = 0f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_arrow_up_foreground),
                                    contentDescription = "Move Up"
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { userX -= 500; userDirection = 270f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_double_arrow_left_foreground),
                                    contentDescription = "Move Double Left"
                                )
                            }
                            IconButton(
                                onClick = { userX -= 100; userDirection = 270f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_arrow_left_foreground),
                                    contentDescription = "Move Left"
                                )
                            }
                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Show Modal")
                            }
                            IconButton(
                                onClick = { userX+=100; userDirection = 90f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_arrow_right_foreground),
                                    contentDescription = "Move Right"
                                )
                            }
                            IconButton(
                                onClick = { userX += 500; userDirection = 90f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_double_arrow_right_foreground),
                                    contentDescription = "Move Double Right"
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { userY += 100 ; userDirection = 180f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_arrow_down_foreground),
                                    contentDescription = "Move Down"
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { userY += 500; userDirection = 180f; },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.keyboard_double_arrow_down_foreground),
                                    contentDescription = "Move Double Down"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun UserIcon( userX: Float, userY: Float, userDirection: Float, ratio: Float, trajectoryViewModel: TrajectoryViewModel, isMlMode: Boolean, mlModel: String){
    Canvas(modifier = Modifier.fillMaxSize()
    ) {
        val userCenter = Offset(userX * ratio, userY * ratio)
        rotate(userDirection, pivot = userCenter){
            // Draw user
            drawCircle(
                color = Color.Blue,
                center = Offset(userX * ratio, userY * ratio),
                radius = 30f * ratio
            )

            val path = Path()
            path.moveTo((userX + 30f) * ratio, (userY-30f) * ratio)
            path.lineTo((userX + 0f) * ratio, (userY-50f) * ratio)
            path.lineTo((userX - 30f) * ratio, (userY-30f) * ratio)

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

        if (isMlMode) {
            val rectSize = 40f * ratio
            val rectTopRight = Offset(userCenter.x + 20f * ratio, userCenter.y - 20f * ratio)
            val rectBottomLeft = Offset(rectTopRight.x - rectSize, rectTopRight.y + rectSize)

            // Draw text
            val text = mlModel
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    text,
                    rectBottomLeft.x + (50f * ratio),
                    rectBottomLeft.y - (20f * ratio), // Adjust Y coordinate for text alignment
                    Paint().apply {
                        color = Color.Black.toArgb()
                        textSize = 32.sp.toPx() * ratio// Set text size
                    }
                )
            }
        }
    }
}

@Composable
fun WifiListDialog(wifiList: List<ScanResult>, onClose: () -> Unit, floor: Int, x: Int, y: Int) {
    val apValues = mutableMapOf<String, Float?>()
    var attempt by remember { mutableIntStateOf(0) }
    var isAuto by remember { mutableStateOf(false) }

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
                            Constants.AP131 -> { apValues["ap131"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP132 -> { apValues["ap132"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP133 -> { apValues["ap133"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP134 -> { apValues["ap134"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP135 -> { apValues["ap135"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP136 -> { apValues["ap136"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP137 -> { apValues["ap137"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP138 -> { apValues["ap138"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP139 -> { apValues["ap139"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP140 -> { apValues["ap140"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP141 -> { apValues["ap141"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP142 -> { apValues["ap142"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP143 -> { apValues["ap143"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP144 -> { apValues["ap144"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP145 -> { apValues["ap145"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP146 -> { apValues["ap146"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP147 -> { apValues["ap147"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP148 -> { apValues["ap148"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP149 -> { apValues["ap149"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP150 -> { apValues["ap150"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP151 -> { apValues["ap151"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP152 -> { apValues["ap152"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP153 -> { apValues["ap153"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP154 -> { apValues["ap154"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP155 -> { apValues["ap155"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP156 -> { apValues["ap156"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP157 -> { apValues["ap157"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP158 -> { apValues["ap158"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP159 -> { apValues["ap159"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP160 -> { apValues["ap160"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP161 -> { apValues["ap161"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP162 -> { apValues["ap162"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP163 -> { apValues["ap163"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP164 -> { apValues["ap164"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP165 -> { apValues["ap165"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP166 -> { apValues["ap166"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP167 -> { apValues["ap167"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP168 -> { apValues["ap168"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP169 -> { apValues["ap169"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP170 -> { apValues["ap170"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP171 -> { apValues["ap171"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP172 -> { apValues["ap172"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP173 -> { apValues["ap173"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP174 -> { apValues["ap174"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP175 -> { apValues["ap175"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP176 -> { apValues["ap176"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP177 -> { apValues["ap177"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP178 -> { apValues["ap178"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP179 -> { apValues["ap179"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP180 -> { apValues["ap180"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP181 -> { apValues["ap181"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
                            Constants.AP182 -> { apValues["ap182"] = 10.0.pow(item.level/10.0).toFloat() ; isExist = true }
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

                Text(text = "Total Attempt: $attempt")

                Row() {
                    Button(
                        enabled = !isAuto,
                        onClick = {
                            attempt++
                            postFingerprint(apValues, floor, x, y, unregisteredBssid, attempt)
                        },
                    ) {
                        Text(text = "Save")
                    }
                    Button(
                        onClick = { isAuto = !isAuto },
                    ) {
                        Text(text = if (isAuto) "Stop" else "Auto")
                    }
                    if (isAuto) {
                        CircularProgressIndicator()

                    }

                    LaunchedEffect(apValues) {
                        if (isAuto) {
                            attempt++
                            postFingerprint(apValues, floor, x, y, unregisteredBssid, attempt)
                            Log.v("apvalues", apValues.toString())
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Close")
                }
            }
        }
    }
}

@Composable
fun InfoDialog(mlUserIcon: List<Prediction>, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ML Model Comparison",
                    style = MaterialTheme.typography.headlineMedium, color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column() {
                    mlUserIcon.forEachIndexed { index, it ->
                        val coordinates = it.predicted_location.split(",")
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = getModelName(index))
                            Text(text = "Lantai ${coordinates[2]} X: ${coordinates[0]} Y: ${coordinates[1]}")
                        }
                    }
                }
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Close")
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(isLocalization: Boolean, isRegistration: Boolean, isMlTest: Boolean, isPdrActive: Boolean, isScanningActive: Boolean, onSettings: (Boolean, Boolean, Boolean, Boolean, Boolean) -> Unit) {
    val selectedOption = remember { mutableIntStateOf(0) }
    val isPdrActiveState = remember { mutableStateOf(isPdrActive) }
    val isScanningActiveState = remember { mutableStateOf(isScanningActive) }
    val isPdrButtonEnabled = remember { mutableStateOf(isLocalization)}

    if (isLocalization) {
        selectedOption.intValue = 0
    } else if (isRegistration) {
        selectedOption.intValue = 1
    } else if (isMlTest) {
        selectedOption.intValue = 2
    }

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Settings")
        },
        text = {
            Column {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "PDR")
                    Switch(
                        checked = isPdrActiveState.value,
                        onCheckedChange = { isPdrActiveState.value = !isPdrActiveState.value },
                        enabled = isPdrButtonEnabled.value
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Predict Location")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isScanningActiveState.value,
                        onCheckedChange = { isScanningActiveState.value = !isScanningActiveState.value }
                    )
                }
                Divider()
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Localization Mode")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = selectedOption.intValue == 0,
                        onClick = {
                            selectedOption.intValue = 0
                            isPdrButtonEnabled.value = true
                            isPdrActiveState.value = true
                        }
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "WiFi Registration Mode")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = selectedOption.intValue == 1,
                        onClick = {
                            selectedOption.intValue = 1
                            isPdrButtonEnabled.value = false
                            isPdrActiveState.value = false
                        }
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "ML Test Mode")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = selectedOption.intValue == 2,
                        onClick = {
                            selectedOption.intValue = 2
                            isPdrButtonEnabled.value = false
                            isPdrActiveState.value = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (selectedOption.intValue) {
                        0 -> onSettings(isPdrActiveState.value, isScanningActiveState.value, true, false, false)
                        1 -> onSettings(isPdrActiveState.value, isScanningActiveState.value, false, true, false)
                        2 -> onSettings(isPdrActiveState.value, isScanningActiveState.value, false, false, true)
                    }
                },
            ) {
                Text(text = "Save")
            }
        }
    )
}

fun getModelName(index: Int): String {
    return when (index) {
        0 -> "RF"
        1 -> "SVM"
        2 -> "KNN"
        3 -> "GNB"
        else -> "CNN"
    }
}

fun postFingerprint(apValues: Map<String, Float?>, floor: Int, x: Int, y: Int, unregisteredBssid: Set<Wifi>, attempt: Int) {
    val fingerprint = Fingerprint(
        attempt = attempt,
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
        ap141 = apValues["ap141"],
        ap142 = apValues["ap142"],
        ap143 = apValues["ap143"],
        ap144 = apValues["ap144"],
        ap145 = apValues["ap145"],
        ap146 = apValues["ap146"],
        ap147 = apValues["ap147"],
        ap148 = apValues["ap148"],
        ap149 = apValues["ap149"],
        ap150 = apValues["ap150"],
        ap151 = apValues["ap151"],
        ap152 = apValues["ap152"],
        ap153 = apValues["ap153"],
        ap154 = apValues["ap154"],
        ap155 = apValues["ap155"],
        ap156 = apValues["ap156"],
        ap157 = apValues["ap157"],
        ap158 = apValues["ap158"],
        ap159 = apValues["ap159"],
        ap160 = apValues["ap160"],
        ap161 = apValues["ap161"],
        ap162 = apValues["ap162"],
        ap163 = apValues["ap163"],
        ap164 = apValues["ap164"],
        ap165 = apValues["ap165"],
        ap166 = apValues["ap166"],
        ap167 = apValues["ap167"],
        ap168 = apValues["ap168"],
        ap169 = apValues["ap169"],
        ap170 = apValues["ap170"],
        ap171 = apValues["ap171"],
        ap172 = apValues["ap172"],
        ap173 = apValues["ap173"],
        ap174 = apValues["ap174"],
        ap175 = apValues["ap175"],
        ap176 = apValues["ap176"],
        ap177 = apValues["ap177"],
        ap178 = apValues["ap178"],
        ap179 = apValues["ap179"],
        ap180 = apValues["ap180"],
        ap181 = apValues["ap181"],
        ap182 = apValues["ap182"],
        wifi = unregisteredBssid
    )
    CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.BEApiService.postFingerprint(fingerprint)
//        RetrofitInstance.mlApiService.predict(fingerprint)
    }
}

fun getApAssignment(wifiList: List<ScanResult>): AccessPoint {
    val apValues = mutableMapOf<String, Float?>()

    for (item in wifiList) {
        when (item.BSSID.uppercase()) {
            Constants.AP1 -> {
                apValues["ap1"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP2 -> {
                apValues["ap2"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP3 -> {
                apValues["ap3"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP4 -> {
                apValues["ap4"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP5 -> {
                apValues["ap5"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP6 -> {
                apValues["ap6"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP7 -> {
                apValues["ap7"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP8 -> {
                apValues["ap8"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP9 -> {
                apValues["ap9"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP10 -> {
                apValues["ap10"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP11 -> {
                apValues["ap11"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP12 -> {
                apValues["ap12"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP13 -> {
                apValues["ap13"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP14 -> {
                apValues["ap14"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP15 -> {
                apValues["ap15"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP16 -> {
                apValues["ap16"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP17 -> {
                apValues["ap17"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP18 -> {
                apValues["ap18"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP19 -> {
                apValues["ap19"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP20 -> {
                apValues["ap20"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP21 -> {
                apValues["ap21"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP22 -> {
                apValues["ap22"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP23 -> {
                apValues["ap23"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP24 -> {
                apValues["ap24"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP25 -> {
                apValues["ap25"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP26 -> {
                apValues["ap26"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP27 -> {
                apValues["ap27"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP28 -> {
                apValues["ap28"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP29 -> {
                apValues["ap29"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP30 -> {
                apValues["ap30"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP31 -> {
                apValues["ap31"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP32 -> {
                apValues["ap32"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP33 -> {
                apValues["ap33"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP34 -> {
                apValues["ap34"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP35 -> {
                apValues["ap35"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP36 -> {
                apValues["ap36"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP37 -> {
                apValues["ap37"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP38 -> {
                apValues["ap38"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP39 -> {
                apValues["ap39"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP40 -> {
                apValues["ap40"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP41 -> {
                apValues["ap41"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP42 -> {
                apValues["ap42"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP43 -> {
                apValues["ap43"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP44 -> {
                apValues["ap44"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP45 -> {
                apValues["ap45"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP46 -> {
                apValues["ap46"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP47 -> {
                apValues["ap47"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP48 -> {
                apValues["ap48"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP49 -> {
                apValues["ap49"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP50 -> {
                apValues["ap50"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP51 -> {
                apValues["ap51"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP52 -> {
                apValues["ap52"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP53 -> {
                apValues["ap53"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP54 -> {
                apValues["ap54"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP55 -> {
                apValues["ap55"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP56 -> {
                apValues["ap56"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP57 -> {
                apValues["ap57"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP58 -> {
                apValues["ap58"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP59 -> {
                apValues["ap59"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP60 -> {
                apValues["ap60"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP61 -> {
                apValues["ap61"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP62 -> {
                apValues["ap62"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP63 -> {
                apValues["ap63"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP64 -> {
                apValues["ap64"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP65 -> {
                apValues["ap65"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP66 -> {
                apValues["ap66"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP67 -> {
                apValues["ap67"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP68 -> {
                apValues["ap68"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP69 -> {
                apValues["ap69"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP70 -> {
                apValues["ap70"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP71 -> {
                apValues["ap71"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP72 -> {
                apValues["ap72"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP73 -> {
                apValues["ap73"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP74 -> {
                apValues["ap74"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP75 -> {
                apValues["ap75"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP76 -> {
                apValues["ap76"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP77 -> {
                apValues["ap77"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP78 -> {
                apValues["ap78"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP79 -> {
                apValues["ap79"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP80 -> {
                apValues["ap80"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP81 -> {
                apValues["ap81"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP82 -> {
                apValues["ap82"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP83 -> {
                apValues["ap83"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP84 -> {
                apValues["ap84"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP85 -> {
                apValues["ap85"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP86 -> {
                apValues["ap86"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP87 -> {
                apValues["ap87"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP88 -> {
                apValues["ap88"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP89 -> {
                apValues["ap89"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP90 -> {
                apValues["ap90"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP91 -> {
                apValues["ap91"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP92 -> {
                apValues["ap92"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP93 -> {
                apValues["ap93"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP94 -> {
                apValues["ap94"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP95 -> {
                apValues["ap95"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP96 -> {
                apValues["ap96"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP97 -> {
                apValues["ap97"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP98 -> {
                apValues["ap98"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP99 -> {
                apValues["ap99"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP100 -> {
                apValues["ap100"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP101 -> {
                apValues["ap101"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP102 -> {
                apValues["ap102"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP103 -> {
                apValues["ap103"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP104 -> {
                apValues["ap104"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP105 -> {
                apValues["ap105"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP106 -> {
                apValues["ap106"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP107 -> {
                apValues["ap107"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP108 -> {
                apValues["ap108"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP109 -> {
                apValues["ap109"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP110 -> {
                apValues["ap110"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP111 -> {
                apValues["ap111"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP112 -> {
                apValues["ap112"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP113 -> {
                apValues["ap113"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP114 -> {
                apValues["ap114"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP115 -> {
                apValues["ap115"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP116 -> {
                apValues["ap116"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP117 -> {
                apValues["ap117"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP118 -> {
                apValues["ap118"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP119 -> {
                apValues["ap119"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP120 -> {
                apValues["ap120"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP121 -> {
                apValues["ap121"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP122 -> {
                apValues["ap122"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP123 -> {
                apValues["ap123"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP124 -> {
                apValues["ap124"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP125 -> {
                apValues["ap125"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP126 -> {
                apValues["ap126"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP127 -> {
                apValues["ap127"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP128 -> {
                apValues["ap128"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP129 -> {
                apValues["ap129"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP130 -> {
                apValues["ap130"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP131 -> {
                apValues["ap131"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP132 -> {
                apValues["ap132"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP133 -> {
                apValues["ap133"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP134 -> {
                apValues["ap134"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP135 -> {
                apValues["ap135"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP136 -> {
                apValues["ap136"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP137 -> {
                apValues["ap137"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP138 -> {
                apValues["ap138"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP139 -> {
                apValues["ap139"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP140 -> {
                apValues["ap140"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP141 -> {
                apValues["ap141"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP142 -> {
                apValues["ap142"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP143 -> {
                apValues["ap143"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP144 -> {
                apValues["ap144"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP145 -> {
                apValues["ap145"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP146 -> {
                apValues["ap146"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP147 -> {
                apValues["ap147"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP148 -> {
                apValues["ap148"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP149 -> {
                apValues["ap149"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP150 -> {
                apValues["ap150"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP151 -> {
                apValues["ap151"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP152 -> {
                apValues["ap152"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP153 -> {
                apValues["ap153"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP154 -> {
                apValues["ap154"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP155 -> {
                apValues["ap155"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP156 -> {
                apValues["ap156"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP157 -> {
                apValues["ap157"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP158 -> {
                apValues["ap158"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP159 -> {
                apValues["ap159"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP160 -> {
                apValues["ap160"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP161 -> {
                apValues["ap161"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP162 -> {
                apValues["ap162"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP163 -> {
                apValues["ap163"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP164 -> {
                apValues["ap164"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP165 -> {
                apValues["ap165"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP166 -> {
                apValues["ap166"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP167 -> {
                apValues["ap167"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP168 -> {
                apValues["ap168"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP169 -> {
                apValues["ap169"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP170 -> {
                apValues["ap170"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP171 -> {
                apValues["ap171"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP172 -> {
                apValues["ap172"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP173 -> {
                apValues["ap173"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP174 -> {
                apValues["ap174"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP175 -> {
                apValues["ap175"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP176 -> {
                apValues["ap176"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP177 -> {
                apValues["ap177"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP178 -> {
                apValues["ap178"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP179 -> {
                apValues["ap179"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP180 -> {
                apValues["ap180"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP181 -> {
                apValues["ap181"] = 10.0.pow(item.level / 10.0).toFloat()
            }

            Constants.AP182 -> {
                apValues["ap182"] = 10.0.pow(item.level / 10.0).toFloat()
            }
        }
    }

    return AccessPoint(
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
        ap141 = apValues["ap141"],
        ap142 = apValues["ap142"],
        ap143 = apValues["ap143"],
        ap144 = apValues["ap144"],
        ap145 = apValues["ap145"],
        ap146 = apValues["ap146"],
        ap147 = apValues["ap147"],
        ap148 = apValues["ap148"],
        ap149 = apValues["ap149"],
        ap150 = apValues["ap150"],
        ap151 = apValues["ap151"],
        ap152 = apValues["ap152"],
        ap153 = apValues["ap153"],
        ap154 = apValues["ap154"],
        ap155 = apValues["ap155"],
        ap156 = apValues["ap156"],
        ap157 = apValues["ap157"],
        ap158 = apValues["ap158"],
        ap159 = apValues["ap159"],
        ap160 = apValues["ap160"],
        ap161 = apValues["ap161"],
        ap162 = apValues["ap162"],
        ap163 = apValues["ap163"],
        ap164 = apValues["ap164"],
        ap165 = apValues["ap165"],
        ap166 = apValues["ap166"],
        ap167 = apValues["ap167"],
        ap168 = apValues["ap168"],
        ap169 = apValues["ap169"],
        ap170 = apValues["ap170"],
        ap171 = apValues["ap171"],
        ap172 = apValues["ap172"],
        ap173 = apValues["ap173"],
        ap174 = apValues["ap174"],
        ap175 = apValues["ap175"],
        ap176 = apValues["ap176"],
        ap177 = apValues["ap177"],
        ap178 = apValues["ap178"],
        ap179 = apValues["ap179"],
        ap180 = apValues["ap180"],
        ap181 = apValues["ap181"],
        ap182 = apValues["ap182"],
        apAmount = apValues.size
    )
}


fun getBelakang(currentX: Float, currentY: Float, x: Float, y: Float, orientation: Float): Boolean {
    // Convert the orientation angle to radians
    val orientationRadians = Math.toRadians(orientation.toDouble())

    // Calculate the angle to the new coordinates from the current coordinates
    val angleToNewCoords = Math.atan2((y - currentY).toDouble(), (x - currentX).toDouble())

    // Normalize the orientation and angle to new coordinates
    val normalizedOrientation = (Math.toDegrees(orientationRadians) % 360 + 360) % 360
    val normalizedAngleToNewCoords = (Math.toDegrees(angleToNewCoords) % 360 + 360) % 360

    // Compute the angle difference
    var angleDifference = normalizedAngleToNewCoords - normalizedOrientation
    if (angleDifference > 180) angleDifference -= 360
    if (angleDifference < -180) angleDifference += 360

    // Determine if the new coordinates are behind
    val isBehind = angleDifference > 90 || angleDifference < -90

    // Log the current coordinates, new coordinates, orientation, and whether it's behind
    println("Current Coordinates: ($currentX, $currentY)")
    println("New Coordinates: ($x, $y)")
    println("Orientation: $orientation degrees")
    println("Normalized Orientation: $normalizedOrientation degrees")
    println("Angle to new coordinates: $normalizedAngleToNewCoords degrees")
    println("Normalized Angle Difference: $angleDifference degrees")
    println("Is behind: $isBehind")

    return isBehind
}

fun processPrediction(predictions: PredictionList, userX: Float, userY: Float, userLantai: Int, isPdrActive: Boolean, isUncertain: Boolean, isInitialScan: Boolean): PredictionNew? {
    // Variabel buat atur ambang batas
    val confidenceThreshold = 0.2
    val distanceTreshold = 1500.0

    // Kalo lagi mode wifi only return aja tertinggi
    if (!isPdrActive) {
        return predictions.data[0]
    }

    // Itung perbandingan confidence semua titik dengan titik confidence tertinggi
    val predictionToConsider = mutableListOf(predictions.data[0])
    for (i in 1..<predictions.data.size) {
        if (predictions.data[0].confidence - predictions.data[i].confidence <= confidenceThreshold ) {
            predictionToConsider.add(predictions.data[i])
        } else {
            break
        }
    }

    // Case kalo initial scan banyak prediksi yang mirip
    if (isInitialScan && predictionToConsider.size > 1) {
        return null
    }

    // Cari prediksi terdekat dengan PDR pake euclidean distance
    var predictionCandidate: PredictionNew = predictions.data[0] // By default kandidat utama si index 0
    if (predictionToConsider.size > 1) {
        var nearestDistance = Integer.MAX_VALUE

        for (prediction in predictions.data) {
            val distance = sqrt((userX.minus(prediction.x.toFloat()).pow(2) + (userY.minus(prediction.y.toFloat()).pow(2))).toDouble()).toInt()
            if (distance < nearestDistance) {
                nearestDistance = distance
                predictionCandidate = prediction
            }
        }
    }

    // Kalo tadinya ragu, return aja yang baru
    if (isUncertain) {
        return predictionCandidate
    }

    // Cek apakah jarak dengan kandidat terdekat lebih dari threshold
    val distance = sqrt((userX.minus(predictionCandidate.x.toFloat()).pow(2) + (userY.minus(predictionCandidate.y.toFloat()).pow(2))).toDouble())
    if (distance > distanceTreshold && !isInitialScan) {
        return null
    }

    Log.v("prediction", predictionCandidate.toString())
    return predictionCandidate
}

fun determineBestPrediction(predictionLists: List<PredictionList>): Triple<String, String, String> {
    val coordinateConfidenceSum = mutableMapOf<Triple<String, String, String>, Float>()
    val coordinateFrequency = mutableMapOf<Triple<String, String, String>, Int>()

    Log.v("Judging prediksi", predictionLists.toString())

    for (predictionList in predictionLists) {
        for (prediction in predictionList.data) {
            val coordinates = Triple(prediction.x, prediction.y, prediction.z)
            if (coordinateConfidenceSum.containsKey(coordinates)) {
                coordinateConfidenceSum[coordinates] = coordinateConfidenceSum[coordinates]!! + prediction.confidence
                coordinateFrequency[coordinates] = coordinateFrequency[coordinates]!! + 1
            } else {
                coordinateConfidenceSum[coordinates] = prediction.confidence
                coordinateFrequency[coordinates] = 1
            }
        }
    }

    Log.v("Total confidence koordinat", coordinateConfidenceSum.toString())
    Log.v("Frekuensi koordinat", coordinateFrequency.toString())

    val combinedScores = coordinateConfidenceSum.mapValues { (prediction, confidenceSum) ->
        confidenceSum * (coordinateFrequency[prediction] ?: 1)
    }

    val normalizedCombinedScores = combinedScores.mapValues { (_, score) ->
        score / combinedScores.values.sum()
    }

    Log.v("Daftar hasil perhitungan", normalizedCombinedScores.toString())

    val trueCoordinate = normalizedCombinedScores.maxByOrNull { it.value }?.key

    return trueCoordinate!!
}

fun compareToPrediction(prediction: Triple<String, String, String>, grids: Grids, userX: Float, userY: Float, lantai: Int): Triple<Float, Float, Int>? {
    val x = prediction.first.toFloat()
    val y = prediction.second.toFloat()
    val z = prediction.third.toInt()

    if (lantai != z) {
        Log.v("Algoritma", "Prediksi lantai baru, pindah langsung")
        return Triple(x, y, z)
    }

    val distanceToPrediction = sqrt((userX.minus(x).pow(2) + (userY.minus(y).pow(2))))

    val gridFloor: List<Coordinate> = when (lantai) {
        0 -> grids.zero
        1 -> grids.one
        2 -> grids.two
        3 -> grids.three
        else -> grids.zero
    }

    for (grid in gridFloor) {
        val gridX = grid.x.toFloat()
        val gridY = grid.y.toFloat()
        val gridDistance = sqrt((userX.minus(gridX).pow(2) + (userY.minus(gridY).pow(2))))
        if (gridDistance < distanceToPrediction) {
            Log.v("Algoritma", "Ada grid yang lebih deket, pindah ke prediksi")
            return Triple(gridX, gridY, z)
        }
    }

    Log.v("algoritma", "Gak ada yang deket, lanjut pdr")

    return null
}
suspend fun getPrediction(apValues: AccessPoint): PredictionList {
    try {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.mlApiService.predictRfV2(apValues)
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "Error: ${e.message}")
        throw e
    }
}

suspend fun getMultiPrediction(apValues: AccessPoint): List<Prediction> {
    try {
        return withContext(Dispatchers.IO) {
            val predictionRfDeferred = async { RetrofitInstance.mlApiService.predictRf(apValues) }
            val predictionSvmDeferred = async { RetrofitInstance.mlApiService.predictSvm(apValues) }
            val predictionKnnDeferred = async { RetrofitInstance.mlApiService.predictKnn(apValues) }
            val predictionGnbDeferred = async { RetrofitInstance.mlApiService.predictGnb(apValues) }
            val predictionCnnDeferred = async { RetrofitInstance.mlApiService.predictCnn(apValues) }

            listOf(
                predictionRfDeferred.await(),
                predictionSvmDeferred.await(),
                predictionKnnDeferred.await(),
                predictionGnbDeferred.await(),
                predictionCnnDeferred.await()
            )
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "Error: ${e.message}")
        throw e
    }
}

suspend fun getRecordedGrids(): Grids {
    try {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.BEApiService.getGrid()
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "Error: ${e.message}")
        throw e
    }
}





