package com.example.fittrackpro.ui.main

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.fittrackpro.Main
import com.example.fittrackpro.Settings
import com.example.fittrackpro.data.BodyMeasurementEntity
import com.example.fittrackpro.data.NutritionEntity
import com.example.fittrackpro.data.ProgressPhotoEntity
import com.example.fittrackpro.data.WorkoutEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material3.TextButton
import com.example.fittrackpro.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (androidx.navigation3.runtime.NavKey) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dashboardStats by viewModel.dashboardStats.collectAsState()
    val nutritionStats by viewModel.nutritionStats.collectAsState()
    val workouts by viewModel.workoutsState.collectAsState()
    val measurements by viewModel.measurementsState.collectAsState()
    val photos by viewModel.progressPhotosState.collectAsState()
    val nutritionLogs by viewModel.nutritionLogsState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Dashboard", "Workouts", "Nutrition", "Analytics", "Photos")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FitTrack Pro", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { onItemClick(Settings) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            val icon = when (index) {
                                0 -> Icons.Filled.Dashboard
                                1 -> Icons.Filled.FitnessCenter
                                2 -> Icons.Filled.Opacity
                                3 -> Icons.Filled.Analytics
                                else -> Icons.Filled.PhotoLibrary
                            }
                            Icon(icon, contentDescription = title)
                        },
                        label = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardTab(
                    stats = dashboardStats,
                    nutrition = nutritionStats,
                    unit = viewModel.getWeightUnit()
                )
                1 -> WorkoutsTab(
                    workouts = workouts,
                    onAddWorkout = { viewModel.insertWorkout(it) },
                    onDeleteWorkout = { viewModel.deleteWorkout(it) }
                )
                2 -> NutritionTab(
                    logs = nutritionLogs,
                    stats = nutritionStats,
                    onAddLog = { viewModel.insertNutritionLog(it) },
                    onDeleteLog = { viewModel.deleteNutritionLog(it) }
                )
                3 -> AnalyticsTab(
                    workouts = workouts,
                    measurements = measurements,
                    nutrition = nutritionLogs,
                    unit = viewModel.getWeightUnit(),
                    onAddMeasurement = { viewModel.insertMeasurement(it) },
                    onDeleteMeasurement = { viewModel.deleteMeasurement(it) }
                )
                4 -> PhotosTab(
                    photos = photos,
                    onAddPhoto = { viewModel.insertProgressPhoto(it) },
                    onDeletePhoto = { viewModel.deleteProgressPhoto(it) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TABS IMPLEMENTATION
// -------------------------------------------------------------

@Composable
fun DashboardTab(
    stats: DashboardStats,
    nutrition: NutritionStats,
    unit: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick statistics grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                icon = Icons.Filled.Scale,
                title = "Weight",
                value = "${stats.currentWeight} $unit",
                subtitle = "Change: ${if (stats.weightChange >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", stats.weightChange)} $unit",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.FitnessCenter,
                title = "Workouts",
                value = "${stats.totalWorkouts}",
                subtitle = "7 Days: ${stats.weeklyProgressCount} logged",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                icon = Icons.Filled.LocalFireDepartment,
                title = "Est. Burned",
                value = "${stats.caloriesBurned.toInt()} kcal",
                subtitle = "Estimated active energy",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Speed,
                title = "Hydration Today",
                value = "${nutrition.todayWaterMl} ml",
                subtitle = "Goal: ${nutrition.targetWaterMl} ml",
                modifier = Modifier.weight(1f)
            )
        }

        // Circular progress indicators card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedProgressRing(
                            progressValue = nutrition.todayCalories.toFloat(),
                            targetValue = nutrition.targetCalories.toFloat(),
                            activeColor = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Calories", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${nutrition.todayCalories} / ${nutrition.targetCalories} kcal", fontSize = 11.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedProgressRing(
                            progressValue = nutrition.todayWaterMl.toFloat(),
                            targetValue = nutrition.targetWaterMl.toFloat(),
                            activeColor = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Water Intake", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${nutrition.todayWaterMl} / ${nutrition.targetWaterMl} ml", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Recent Activity Feed
        Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))

        if (stats.recentActivities.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "No logged activities yet. Click on the tabs below to start logging!",
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stats.recentActivities.forEach { activity ->
                    ActivityItem(activity)
                }
            }
        }
    }
}

@Composable
fun WorkoutsTab(
    workouts: List<WorkoutEntity>,
    onAddWorkout: (WorkoutEntity) -> Unit,
    onDeleteWorkout: (WorkoutEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (workouts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, Modifier.size(64.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No logged workouts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Keep track of your sets, reps, and weights by adding your first workout exercise log.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Workout History", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(workouts) { workout ->
                    WorkoutLogCard(workout, onDelete = { onDeleteWorkout(workout) })
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Workout")
        }

        if (showDialog) {
            AddWorkoutDialog(
                onDismiss = { showDialog = false },
                onConfirm = { workout ->
                    onAddWorkout(workout)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun NutritionTab(
    logs: List<NutritionEntity>,
    stats: NutritionStats,
    onAddLog: (NutritionEntity) -> Unit,
    onDeleteLog: (NutritionEntity) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Macros Goal summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Macronutrients Today", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Protein Bar
                MacroProgressLine(
                    label = "Protein",
                    currentValue = stats.todayProtein,
                    targetValue = stats.targetProtein,
                    color = MaterialTheme.colorScheme.primary,
                    unit = "g"
                )

                // Carbs Bar
                MacroProgressLine(
                    label = "Carbs",
                    currentValue = stats.todayCarbs,
                    targetValue = stats.targetCarbs,
                    color = MaterialTheme.colorScheme.secondary,
                    unit = "g"
                )

                // Fat Bar
                MacroProgressLine(
                    label = "Fat",
                    currentValue = stats.todayFat,
                    targetValue = stats.targetFat,
                    color = MaterialTheme.colorScheme.tertiary,
                    unit = "g"
                )
            }
        }

        // Quick add water log
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Log Hydration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(250, 500, 750).forEach { amount ->
                        Button(
                            onClick = {
                                val log = NutritionEntity(
                                    calories = 0,
                                    protein = 0.0,
                                    carbs = 0.0,
                                    fat = 0.0,
                                    waterMl = amount,
                                    date = System.currentTimeMillis(),
                                    mealName = "Water Intake"
                                )
                                onAddLog(log)
                                Toast.makeText(context, "+$amount ml logged!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$amount", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nutrition Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, Modifier.size(16.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Meal", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // List of meals logged today
        if (logs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No food logged today.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    NutritionLogItem(log, onDelete = { onDeleteLog(log) })
                }
            }
        }

        if (showDialog) {
            AddMealDialog(
                onDismiss = { showDialog = false },
                onConfirm = { log ->
                    onAddLog(log)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AnalyticsTab(
    workouts: List<WorkoutEntity>,
    measurements: List<BodyMeasurementEntity>,
    nutrition: List<NutritionEntity>,
    unit: String,
    onAddMeasurement: (BodyMeasurementEntity) -> Unit,
    onDeleteMeasurement: (BodyMeasurementEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Analytics & Reports", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Scale, contentDescription = null, Modifier.size(16.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Weight", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Scrollable charts section
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Weight Line Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weight Progress ($unit)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (measurements.isEmpty()) {
                        Box(modifier = Modifier.height(180.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No weight stats logged.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        WeightLineChart(measurements = measurements)
                    }
                }
            }

            // Calorie Bar Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Calorie Intake (Last 7 Days)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (nutrition.isEmpty()) {
                        Box(modifier = Modifier.height(180.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No meals logged.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        CalorieBarChart(nutrition = nutrition)
                    }
                }
            }

            // Workout Frequency Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Workout Frequency (Days)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (workouts.isEmpty()) {
                        Box(modifier = Modifier.height(180.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No workouts logged.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        WorkoutFrequencyBarChart(workouts = workouts)
                    }
                }
            }

            // Weekly & Monthly summaries card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Periodic Activity Reports", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    // Compute aggregations
                    val totalCalories = nutrition.sumOf { it.calories }
                    val avgCalories = if (nutrition.isNotEmpty()) totalCalories / nutrition.size else 0
                    val totalWorkouts = workouts.size

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("WEEKLY REPORT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Total Workouts: $totalWorkouts", fontSize = 13.sp)
                            Text("Avg Calories: $avgCalories kcal/day", fontSize = 13.sp)
                        }

                        Column {
                            Text("MONTHLY REPORT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Text("Active Sessions: $totalWorkouts", fontSize = 13.sp)
                            Text("Hydration Index: Good", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Measurements History list
            if (measurements.isNotEmpty()) {
                Text("Measurements Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    measurements.take(5).forEach { log ->
                        MeasurementHistoryItem(log, unit, onDelete = { onDeleteMeasurement(log) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddMeasurementDialog(
            onDismiss = { showDialog = false },
            onConfirm = { measurement ->
                onAddMeasurement(measurement)
                showDialog = false
            }
        )
    }
}

@Composable
fun PhotosTab(
    photos: List<ProgressPhotoEntity>,
    onAddPhoto: (ProgressPhotoEntity) -> Unit,
    onDeletePhoto: (ProgressPhotoEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBeforePhoto by remember { mutableStateOf<ProgressPhotoEntity?>(null) }
    var selectedAfterPhoto by remember { mutableStateOf<ProgressPhotoEntity?>(null) }

    // Launcher for camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            onAddPhoto(
                ProgressPhotoEntity(
                    imagePath = tempPhotoUri.toString(),
                    date = System.currentTimeMillis(),
                    notes = "Captured progress photo"
                )
            )
            Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        val directory = File(context.filesDir, "progress_photos")
                        if (!directory.exists()) {
                            directory.mkdirs()
                        }
                        val file = File(directory, "IMG_${System.currentTimeMillis()}.jpg")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        file
                    }
                    onAddPhoto(
                        ProgressPhotoEntity(
                            imagePath = Uri.fromFile(localFile).toString(),
                            date = System.currentTimeMillis(),
                            notes = "Imported gallery image"
                        )
                    )
                    Toast.makeText(context, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to save photo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progress Photos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Camera capture button
                IconButton(
                    onClick = {
                        val dir = File(context.filesDir, "progress_photos")
                        if (!dir.exists()) dir.mkdirs()
                        val file = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
                        tempPhotoUri = FileProvider.getUriForFile(context, "com.example.fittrackpro.fileprovider", file)
                        cameraLauncher.launch(tempPhotoUri!!)
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Camera", tint = Color.Black)
                }

                // Gallery upload button
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary, CircleShape)
                ) {
                    Icon(Icons.Filled.Image, contentDescription = "Gallery", tint = Color.Black)
                }
            }
        }

        // Before & After comparison card
        if (photos.size >= 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Before & After Comparison", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Drops selector row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                            var expanded1 by remember { mutableStateOf(false) }
                            val beforeText = if (selectedBeforePhoto != null) {
                                SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(selectedBeforePhoto!!.date))
                            } else "Select Before"

                            Button(
                                onClick = { expanded1 = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(beforeText, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                            DropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                                photos.forEach { photo ->
                                    DropdownMenuItem(
                                        text = { Text(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(photo.date))) },
                                        onClick = {
                                            selectedBeforePhoto = photo
                                            expanded1 = false
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                            var expanded2 by remember { mutableStateOf(false) }
                            val afterText = if (selectedAfterPhoto != null) {
                                SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(selectedAfterPhoto!!.date))
                            } else "Select After"

                            Button(
                                onClick = { expanded2 = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(afterText, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                            DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                                photos.forEach { photo ->
                                    DropdownMenuItem(
                                        text = { Text(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(photo.date))) },
                                        onClick = {
                                            selectedAfterPhoto = photo
                                            expanded2 = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Side-by-side images
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).height(160.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedBeforePhoto != null) {
                                AsyncImage(
                                    model = selectedBeforePhoto!!.imagePath,
                                    contentDescription = "Before Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text("BEFORE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.Black.copy(0.6f)).padding(4.dp))
                            } else {
                                Text("Select Photo 1", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }

                        Box(
                            modifier = Modifier.weight(1f).height(160.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedAfterPhoto != null) {
                                AsyncImage(
                                    model = selectedAfterPhoto!!.imagePath,
                                    contentDescription = "After Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text("AFTER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.Black.copy(0.6f)).padding(4.dp))
                            } else {
                                Text("Select Photo 2", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Photo Gallery Grid
        if (photos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No progress photos logged.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(photos) { photo ->
                    PhotoGridItem(photo, onDelete = { onDeletePhoto(photo) })
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT METRICS AND CARD WIDGETS
// -------------------------------------------------------------

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AnimatedProgressRing(
    progressValue: Float,
    targetValue: Float,
    activeColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (targetValue > 0) (progressValue / targetValue).coerceIn(0f, 1f) else 0f
    val animatedSweepAngle by animateFloatAsState(
        targetValue = progressRatio * 360f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(100.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            // Background ring track
            drawCircle(
                color = trackColor,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            // Active loading arc
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(progressRatio * 100).toInt()}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ActivityItem(activity: RecentActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon according to log type
            val (icon, tint) = when (activity.iconType) {
                "workout" -> Icons.Filled.FitnessCenter to MaterialTheme.colorScheme.primary
                "measurement" -> Icons.Filled.Scale to MaterialTheme.colorScheme.primary
                "nutrition" -> Icons.Filled.Opacity to MaterialTheme.colorScheme.secondary
                else -> Icons.Filled.Image to MaterialTheme.colorScheme.tertiary
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(tint.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, Modifier.size(18.dp), tint = tint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(activity.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(activity.subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(activity.date)),
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun WorkoutLogCard(workout: WorkoutEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(workout.exerciseName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.primary.copy(0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(workout.category, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Sets: ${workout.sets}", fontSize = 13.sp, color = Color.LightGray)
                    Text("Reps: ${workout.reps}", fontSize = 13.sp, color = Color.LightGray)
                    Text("Weight: ${workout.weight} kg", fontSize = 13.sp, color = Color.LightGray)
                }
                if (workout.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(workout.notes, fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun MacroProgressLine(
    label: String,
    currentValue: Double,
    targetValue: Double,
    color: Color,
    unit: String
) {
    val progressRatio = if (targetValue > 0) (currentValue / targetValue).coerceIn(0.0, 1.0).toFloat() else 0f
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("${currentValue.toInt()} / ${targetValue.toInt()} $unit", fontSize = 12.sp, color = Color.Gray)
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(progressRatio).fillPrimaryKeyHeight().clip(CircleShape).background(color)
            )
        }
    }
}

// Custom private modifier function for layout line alignment
private fun Modifier.fillPrimaryKeyHeight() = this.fillMaxWidth().height(8.dp)

@Composable
fun NutritionLogItem(log: NutritionEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isWater = log.mealName == "Water Intake"
            val icon = if (isWater) Icons.Filled.WaterDrop else Icons.Filled.ShoppingCart
            val color = if (isWater) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, Modifier.size(18.dp), tint = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(log.mealName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                val subtitle = if (isWater) "${log.waterMl} ml logged" else "${log.calories} kcal | P:${log.protein.toInt()}g, C:${log.carbs.toInt()}g, F:${log.fat.toInt()}g"
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun MeasurementHistoryItem(log: BodyMeasurementEntity, unit: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(log.measurementDate)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Wt: ${log.weight} $unit", fontSize = 12.sp, color = Color.LightGray)
                    Text("Fat: ${log.bodyFat}%", fontSize = 12.sp, color = Color.LightGray)
                    Text("Chest: ${log.chest}cm", fontSize = 12.sp, color = Color.LightGray)
                    Text("Waist: ${log.waist}cm", fontSize = 12.sp, color = Color.LightGray)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun PhotoGridItem(photo: ProgressPhotoEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.imagePath,
                contentDescription = "Progress Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Transparent overlay for text and actions
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.3f))
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(Icons.Filled.Cancel, contentDescription = "Delete", tint = Color.Red)
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            ) {
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(photo.date)),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// CHARTS WRAPPERS FOR MPANDROIDCHART
// -------------------------------------------------------------

@Composable
fun WeightLineChart(measurements: List<BodyMeasurementEntity>) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.toArgb() == CharcoalBg.toArgb()

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                isScaleXEnabled = true
                setDrawGridBackground(false)
                legend.textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                }
                axisLeft.apply {
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            // Chronological sort
            val sortedList = measurements.sortedBy { it.measurementDate }
            val entries = sortedList.mapIndexed { index, point ->
                Entry(index.toFloat(), point.weight.toFloat())
            }

            val dataSet = LineDataSet(entries, "Body Weight").apply {
                color = NeonGreen.toArgb()
                setCircleColor(NeonCyan.toArgb())
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircleHole(false)
                valueTextSize = 10f
                valueTextColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = NeonGreen.toArgb()
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(
                sortedList.map { SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(it.measurementDate)) }
            )
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(180.dp)
    )
}

@Composable
fun CalorieBarChart(nutrition: List<NutritionEntity>) {
    val isDark = MaterialTheme.colorScheme.background.toArgb() == CharcoalBg.toArgb()

    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                setFitBars(true)
                legend.textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                }
                axisLeft.apply {
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val calendar = Calendar.getInstance()
            val dayEntries = mutableMapOf<String, Int>()

            // Setup last 7 days keys
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                val key = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)
                dayEntries[key] = 0
            }

            // Aggregate nutrition logs
            nutrition.forEach { log ->
                val dayKey = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(log.date))
                if (dayEntries.containsKey(dayKey)) {
                    dayEntries[dayKey] = dayEntries[dayKey]!! + log.calories
                }
            }

            val entries = dayEntries.values.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value.toFloat())
            }

            val dataSet = BarDataSet(entries, "Calories Consumed").apply {
                color = NeonOrange.toArgb()
                valueTextSize = 10f
                valueTextColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            }

            chart.data = BarData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dayEntries.keys.toList())
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(180.dp)
    )
}

@Composable
fun WorkoutFrequencyBarChart(workouts: List<WorkoutEntity>) {
    val isDark = MaterialTheme.colorScheme.background.toArgb() == CharcoalBg.toArgb()

    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                setFitBars(true)
                legend.textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                }
                axisLeft.apply {
                    textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dayEntries = mutableMapOf<String, Int>()
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                val key = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)
                dayEntries[key] = 0
            }

            // Count workouts per day
            workouts.forEach { log ->
                val dayKey = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(log.workoutDate))
                if (dayEntries.containsKey(dayKey)) {
                    dayEntries[dayKey] = dayEntries[dayKey]!! + 1
                }
            }

            val entries = dayEntries.values.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value.toFloat())
            }

            val dataSet = BarDataSet(entries, "Workout Count").apply {
                color = NeonCyan.toArgb()
                valueTextSize = 10f
                valueTextColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            }

            chart.data = BarData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dayEntries.keys.toList())
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(180.dp)
    )
}

// -------------------------------------------------------------
// DIALOG FORMS LOGGERS
// -------------------------------------------------------------

@Composable
fun AddWorkoutDialog(onDismiss: () -> Unit, onConfirm: (WorkoutEntity) -> Unit) {
    var exerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var category by remember { mutableStateOf("Strength") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Strength", "Cardio", "Stretch", "HIIT", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Workout Exercise", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name (e.g. Bench Press)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { categoryExpanded = true }) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text("Sets") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Workout Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (exerciseName.isNotBlank() && sets.isNotBlank() && reps.isNotBlank() && weight.isNotBlank()) {
                        onConfirm(
                            WorkoutEntity(
                                exerciseName = exerciseName,
                                category = category,
                                sets = sets.toIntOrNull() ?: 1,
                                reps = reps.toIntOrNull() ?: 10,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                workoutDate = System.currentTimeMillis(),
                                notes = notes
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddMealDialog(onDismiss: () -> Unit, onConfirm: (NutritionEntity) -> Unit) {
    var mealName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Food Intake", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("Meal / Food Name (e.g. Eggs)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mealName.isNotBlank() && calories.isNotBlank()) {
                        onConfirm(
                            NutritionEntity(
                                calories = calories.toIntOrNull() ?: 0,
                                protein = protein.toDoubleOrNull() ?: 0.0,
                                carbs = carbs.toDoubleOrNull() ?: 0.0,
                                fat = fat.toDoubleOrNull() ?: 0.0,
                                waterMl = 0,
                                date = System.currentTimeMillis(),
                                mealName = mealName
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Log", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddMeasurementDialog(onDismiss: () -> Unit, onConfirm: (BodyMeasurementEntity) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var arms by remember { mutableStateOf("") }
    var thighs by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Body Measurements", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bodyFat,
                        onValueChange = { bodyFat = it },
                        label = { Text("Body Fat %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chest,
                        onValueChange = { chest = it },
                        label = { Text("Chest (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = waist,
                        onValueChange = { waist = it },
                        label = { Text("Waist (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = arms,
                        onValueChange = { arms = it },
                        label = { Text("Arms (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = thighs,
                        onValueChange = { thighs = it },
                        label = { Text("Thighs (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (weight.isNotBlank()) {
                        onConfirm(
                            BodyMeasurementEntity(
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                chest = chest.toDoubleOrNull() ?: 0.0,
                                waist = waist.toDoubleOrNull() ?: 0.0,
                                arms = arms.toDoubleOrNull() ?: 0.0,
                                thighs = thighs.toDoubleOrNull() ?: 0.0,
                                bodyFat = bodyFat.toDoubleOrNull() ?: 0.0,
                                measurementDate = System.currentTimeMillis()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
