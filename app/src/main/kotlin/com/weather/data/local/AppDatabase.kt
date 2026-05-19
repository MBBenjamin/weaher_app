package com.weather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weather.data.local.dao.HistoricoBuscaDao
import com.weather.data.local.dao.PrevisaoDao
import com.weather.data.local.entity.HistoricoBuscaEntity
import com.weather.data.local.entity.PrevisaoEntity

/**
 * Banco de dados Room do app.
 *
 * Para exportar o schema, adicione ao `build.gradle.kts` do módulo app:
 * ```kotlin
 * ksp {
 *     arg("room.schemaLocation", "$projectDir/schemas")
 * }
 * ```
 *
 * Versão 1 — única migration no MVP. Futuras versões devem fornecer [Migration] explícita.
 */
@Database(
    entities = [PrevisaoEntity::class, HistoricoBuscaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO para operações de cache da previsão do tempo. */
    abstract fun previsaoDao(): PrevisaoDao

    /** DAO para o histórico de cidades buscadas. */
    abstract fun historicoBuscaDao(): HistoricoBuscaDao
}
