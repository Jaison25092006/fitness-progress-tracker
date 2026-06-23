package com.example.fittrackpro.di

import android.content.Context
import android.content.SharedPreferences
import com.example.fittrackpro.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFitnessDatabase(@ApplicationContext context: Context): FitnessDatabase {
        return FitnessDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(db: FitnessDatabase): WorkoutDao = db.workoutDao()

    @Provides
    @Singleton
    fun provideBodyMeasurementDao(db: FitnessDatabase): BodyMeasurementDao = db.bodyMeasurementDao()

    @Provides
    @Singleton
    fun provideProgressPhotoDao(db: FitnessDatabase): ProgressPhotoDao = db.progressPhotoDao()

    @Provides
    @Singleton
    fun provideNutritionDao(db: FitnessDatabase): NutritionDao = db.nutritionDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("fittrackpro_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideFitnessRepository(
        workoutDao: WorkoutDao,
        bodyMeasurementDao: BodyMeasurementDao,
        progressPhotoDao: ProgressPhotoDao,
        nutritionDao: NutritionDao,
        externalScope: CoroutineScope
    ): FitnessRepository {
        return DefaultFitnessRepository(
            workoutDao,
            bodyMeasurementDao,
            progressPhotoDao,
            nutritionDao,
            externalScope
        )
    }
}
