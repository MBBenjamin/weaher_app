package com.weather.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.weather.data.location.FusedLocationSource
import com.weather.data.location.ILocationSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que provê as dependências de localização:
 * [FusedLocationProviderClient] e a ligação [ILocationSource] → [FusedLocationSource].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    internal abstract fun bindLocationSource(impl: FusedLocationSource): ILocationSource

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
    }
}
