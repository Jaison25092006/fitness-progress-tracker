package com.example.fittrackpro.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupRestoreHelper {

    suspend fun exportDatabaseToJson(context: Context, repository: FitnessRepository): Result<Unit> = runCatching {
        val root = JSONObject()

        // 1. Workouts
        val workoutsArray = JSONArray()
        repository.workoutsState.value.forEach {
            val obj = JSONObject().apply {
                put("exerciseName", it.exerciseName)
                put("category", it.category)
                put("sets", it.sets)
                put("reps", it.reps)
                put("weight", it.weight)
                put("workoutDate", it.workoutDate)
                put("notes", it.notes)
            }
            workoutsArray.put(obj)
        }
        root.put("workouts", workoutsArray)

        // 2. Measurements
        val measurementsArray = JSONArray()
        repository.measurementsState.value.forEach {
            val obj = JSONObject().apply {
                put("weight", it.weight)
                put("chest", it.chest)
                put("waist", it.waist)
                put("arms", it.arms)
                put("thighs", it.thighs)
                put("bodyFat", it.bodyFat)
                put("measurementDate", it.measurementDate)
            }
            measurementsArray.put(obj)
        }
        root.put("measurements", measurementsArray)

        // 3. Photos
        val photosArray = JSONArray()
        repository.progressPhotosState.value.forEach {
            val obj = JSONObject().apply {
                put("imagePath", it.imagePath)
                put("date", it.date)
                put("notes", it.notes)
            }
            photosArray.put(obj)
        }
        root.put("photos", photosArray)

        // 4. Nutrition
        val nutritionArray = JSONArray()
        repository.nutritionLogsState.value.forEach {
            val obj = JSONObject().apply {
                put("calories", it.calories)
                put("protein", it.protein)
                put("carbs", it.carbs)
                put("fat", it.fat)
                put("waterMl", it.waterMl)
                put("date", it.date)
                put("mealName", it.mealName)
            }
            nutritionArray.put(obj)
        }
        root.put("nutrition", nutritionArray)

        // Write to cache file
        val file = File(context.cacheDir, "FitTrackPro_Backup.json")
        file.writeText(root.toString(2))

        // Share file
        val uri = FileProvider.getUriForFile(context, "com.example.fittrackpro.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Database Backup"))
    }

    suspend fun importDatabaseFromJson(repository: FitnessRepository, jsonString: String): Result<Unit> = runCatching {
        val root = JSONObject(jsonString)

        // 1. Restore Workouts
        if (root.has("workouts")) {
            repository.deleteAllWorkouts()
            val array = root.getJSONArray("workouts")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val workout = WorkoutEntity(
                    exerciseName = obj.optString("exerciseName", ""),
                    category = obj.optString("category", "Strength"),
                    sets = obj.optInt("sets", 0),
                    reps = obj.optInt("reps", 0),
                    weight = obj.optDouble("weight", 0.0),
                    workoutDate = obj.optLong("workoutDate", System.currentTimeMillis()),
                    notes = obj.optString("notes", "")
                )
                repository.insertWorkout(workout)
            }
        }

        // 2. Restore Measurements
        if (root.has("measurements")) {
            repository.deleteAllMeasurements()
            val array = root.getJSONArray("measurements")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val measurement = BodyMeasurementEntity(
                    weight = obj.optDouble("weight", 0.0),
                    chest = obj.optDouble("chest", 0.0),
                    waist = obj.optDouble("waist", 0.0),
                    arms = obj.optDouble("arms", 0.0),
                    thighs = obj.optDouble("thighs", 0.0),
                    bodyFat = obj.optDouble("bodyFat", 0.0),
                    measurementDate = obj.optLong("measurementDate", System.currentTimeMillis())
                )
                repository.insertMeasurement(measurement)
            }
        }

        // 3. Restore Photos
        if (root.has("photos")) {
            repository.deleteAllProgressPhotos()
            val array = root.getJSONArray("photos")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val photo = ProgressPhotoEntity(
                    imagePath = obj.optString("imagePath", ""),
                    date = obj.optLong("date", System.currentTimeMillis()),
                    notes = obj.optString("notes", "")
                )
                repository.insertProgressPhoto(photo)
            }
        }

        // 4. Restore Nutrition
        if (root.has("nutrition")) {
            repository.deleteAllNutritionLogs()
            val array = root.getJSONArray("nutrition")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val log = NutritionEntity(
                    calories = obj.optInt("calories", 0),
                    protein = obj.optDouble("protein", 0.0),
                    carbs = obj.optDouble("carbs", 0.0),
                    fat = obj.optDouble("fat", 0.0),
                    waterMl = obj.optInt("waterMl", 0),
                    date = obj.optLong("date", System.currentTimeMillis()),
                    mealName = obj.optString("mealName", "Snack")
                )
                repository.insertNutritionLog(log)
            }
        }
    }

    suspend fun exportProgressReportToCsv(context: Context, repository: FitnessRepository): Result<Unit> = runCatching {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val csv = java.lang.StringBuilder()

        // 1. Workouts Section
        csv.append("WORKOUT LOGS\n")
        csv.append("Exercise Name,Category,Sets,Reps,Weight,Date,Notes\n")
        repository.workoutsState.value.forEach {
            val dateStr = dateFormat.format(Date(it.workoutDate))
            csv.append("\"${escapeCsv(it.exerciseName)}\",\"${escapeCsv(it.category)}\",${it.sets},${it.reps},${it.weight},\"$dateStr\",\"${escapeCsv(it.notes)}\"\n")
        }
        csv.append("\n\n")

        // 2. Measurements Section
        csv.append("BODY MEASUREMENTS LOGS\n")
        csv.append("Weight,Chest,Waist,Arms,Thighs,Body Fat %,Date\n")
        repository.measurementsState.value.forEach {
            val dateStr = dateFormat.format(Date(it.measurementDate))
            csv.append("${it.weight},${it.chest},${it.waist},${it.arms},${it.thighs},${it.bodyFat},\"$dateStr\"\n")
        }
        csv.append("\n\n")

        // 3. Nutrition Section
        csv.append("NUTRITION & WATER LOGS\n")
        csv.append("Meal Name,Calories,Protein(g),Carbs(g),Fat(g),Water(ml),Date\n")
        repository.nutritionLogsState.value.forEach {
            val dateStr = dateFormat.format(Date(it.date))
            csv.append("\"${escapeCsv(it.mealName)}\",${it.calories},${it.protein},${it.carbs},${it.fat},${it.waterMl},\"$dateStr\"\n")
        }

        // Write to CSV file in cache
        val file = File(context.cacheDir, "FitTrackPro_ProgressReport.csv")
        file.writeText(csv.toString())

        // Share file
        val uri = FileProvider.getUriForFile(context, "com.example.fittrackpro.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Progress Report (CSV)"))
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
}
