package com.alarmizo.app.di

import android.content.Context
import com.alarmizo.app.util.ObjectDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetectorModule {

    @Provides
    @Singleton
    fun provideObjectDetector(
        @ApplicationContext context: Context
    ): ObjectDetector {
        return ObjectDetector(context)
    }
}