package com.example.fittrackpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WorkoutEntity::class,
        BodyMeasurementEntity::class,
        ProgressPhotoEntity::class,
        NutritionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun nutritionDao(): NutritionDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
