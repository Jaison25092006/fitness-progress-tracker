package com.example.fittrackpro.ui.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrackpro.data.BackupRestoreHelper
import com.example.fittrackpro.data.DefaultFitnessRepository
import com.example.fittrackpro.data.FitnessDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onThemeChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("fittrackpro_prefs", Context.MODE_PRIVATE) }

    // Lazy load the repository for backup/restore operations
    val fitnessRepository = remember {
        val db = FitnessDatabase.getDatabase(context)
        DefaultFitnessRepository(
            db.workoutDao(),
            db.bodyMeasurementDao(),
            db.progressPhotoDao(),
            db.nutritionDao(),
            coroutineScope
        )
    }

    var themePref by remember { mutableStateOf(prefs.getString("theme_preference", "system") ?: "system") }
    var unitPref by remember { mutableStateOf(prefs.getString("unit_preference", "KG") ?: "KG") }

    var themeMenuExpanded by remember { mutableStateOf(false) }
    var unitMenuExpanded by remember { mutableStateOf(false) }

    // Launcher for file import/restore
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val jsonString = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            inputStream.bufferedReader().use { it.readText() }
                        }
                    }
                    if (jsonString != null) {
                        val result = BackupRestoreHelper.importDatabaseFromJson(fitnessRepository, jsonString)
                        result.fold(
                            onSuccess = {
                                Toast.makeText(context, "Database Restored Successfully!", Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Import failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preferences",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            // Theme Preference Setting
            SettingsDropdownRow(
                icon = Icons.Filled.DarkMode,
                title = "App Theme",
                subtitle = when (themePref) {
                    "light" -> "Force Light Mode"
                    "dark" -> "Force Dark Mode"
                    else -> "Follow System Default"
                },
                expanded = themeMenuExpanded,
                onRowClick = { themeMenuExpanded = true },
                onDismiss = { themeMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("System Default") },
                    onClick = {
                        themePref = "system"
                        prefs.edit().putString("theme_preference", "system").apply()
                        onThemeChanged("system")
                        themeMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Light Mode") },
                    onClick = {
                        themePref = "light"
                        prefs.edit().putString("theme_preference", "light").apply()
                        onThemeChanged("light")
                        themeMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Dark Mode") },
                    onClick = {
                        themePref = "dark"
                        prefs.edit().putString("theme_preference", "dark").apply()
                        onThemeChanged("dark")
                        themeMenuExpanded = false
                    }
                )
            }

            // Weight Units Preference Setting
            SettingsDropdownRow(
                icon = Icons.Filled.Scale,
                title = "Weight Units",
                subtitle = if (unitPref == "KG") "Metric (Kilograms)" else "Imperial (Pounds)",
                expanded = unitMenuExpanded,
                onRowClick = { unitMenuExpanded = true },
                onDismiss = { unitMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Kilograms (KG)") },
                    onClick = {
                        unitPref = "KG"
                        prefs.edit().putString("unit_preference", "KG").apply()
                        themeMenuExpanded = false
                        unitMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Pounds (LBS)") },
                    onClick = {
                        unitPref = "LBS"
                        prefs.edit().putString("unit_preference", "LBS").apply()
                        themeMenuExpanded = false
                        unitMenuExpanded = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Data Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            // Backup database (JSON)
            SettingsActionRow(
                icon = Icons.Filled.Backup,
                title = "Backup Data",
                subtitle = "Export database logs to local file storage"
            ) {
                coroutineScope.launch {
                    val result = BackupRestoreHelper.exportDatabaseToJson(context, fitnessRepository)
                    result.onFailure {
                        Toast.makeText(context, "Backup failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            // Restore database (JSON)
            SettingsActionRow(
                icon = Icons.Filled.RestartAlt,
                title = "Restore Data",
                subtitle = "Import database logs from a previous backup file"
            ) {
                filePickerLauncher.launch("application/json")
            }

            // Export Progress Report (CSV)
            SettingsActionRow(
                icon = Icons.Filled.Share,
                title = "Export Progress Report",
                subtitle = "Share formatted CSV summarizing workouts, weight, water"
            ) {
                coroutineScope.launch {
                    val result = BackupRestoreHelper.exportProgressReportToCsv(context, fitnessRepository)
                    result.onFailure {
                        Toast.makeText(context, "Export failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "About",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            // About App Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("FitTrack Pro", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Version 1.0.0 (Production)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "An offline-first, Hilt-configured fitness application featuring dynamic dashboards, camera trackers, and MPAndroidChart progress visualizers.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDropdownRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    expanded: Boolean,
    onRowClick: () -> Unit,
    onDismiss: () -> Unit,
    dropdownContent: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1.5f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                dropdownContent()
            }
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
