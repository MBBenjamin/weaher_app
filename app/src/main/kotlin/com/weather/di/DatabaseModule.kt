package com.weather.di

import android.content.Context
import androidx.room.Room
import com.weather.data.local.AppDatabase
import com.weather.data.local.dao.HistoricoBuscaDao
import com.weather.data.local.dao.PrevisaoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que provê a instância do banco de dados Room e os DAOs.
 *
 * [AppDatabase] é `@Singleton` — uma única instância por processo, conforme
 * recomendação do Room para evitar múltiplas conexões com o banco.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_db"
        ).build()

    @Provides
    fun providePrevisaoDao(db: AppDatabase): PrevisaoDao = db.previsaoDao()

    @Provides
    fun provideHistoricoBuscaDao(db: AppDatabase): HistoricoBuscaDao = db.historicoBuscaDao()
}
