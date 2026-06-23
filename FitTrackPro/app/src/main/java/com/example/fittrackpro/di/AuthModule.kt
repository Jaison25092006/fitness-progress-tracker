package com.example.fittrackpro.di

import android.content.Context
import com.example.fittrackpro.data.AuthRepository
import com.example.fittrackpro.data.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        externalScope: CoroutineScope
    ): AuthRepository {
        return DefaultAuthRepository(context, externalScope)
    }
}
