package com.weather.di

import com.weather.data.location.LocationHandlerImpl
import com.weather.data.repository.BuscaRepositoryImpl
import com.weather.data.repository.PrevisaoRepositoryImpl
import com.weather.domain.location.ILocationHandler
import com.weather.domain.repository.IBuscaRepository
import com.weather.domain.repository.IPrevisaoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrevisaoRepository(impl: PrevisaoRepositoryImpl): IPrevisaoRepository

    @Binds
    @Singleton
    abstract fun bindBuscaRepository(impl: BuscaRepositoryImpl): IBuscaRepository

    @Binds
    @Singleton
    abstract fun bindLocationHandler(impl: LocationHandlerImpl): ILocationHandler
}
