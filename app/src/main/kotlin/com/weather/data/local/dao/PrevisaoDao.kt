package com.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weather.data.local.entity.PrevisaoEntity

/** DAO para operações de cache de previsão do tempo. */
@Dao
interface PrevisaoDao {

    /**
     * Insere ou substitui a previsão para uma localização.
     *
     * Conflito na PK substitui o registro existente (upsert semântico via REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirOuSubstituir(previsao: PrevisaoEntity)

    /** Retorna a previsão em cache para o ID de localização, ou null se não houver. */
    @Query("SELECT * FROM previsoes WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): PrevisaoEntity?

    /**
     * Remove registros cujo [PrevisaoEntity.timestampAtualizado] seja anterior ao threshold.
     *
     * Usado por [LimpezaCacheWorker] para remover entradas com mais de 7 dias.
     *
     * @param threshold epoch millis — registros com timestamp < threshold são deletados
     */
    @Query("DELETE FROM previsoes WHERE timestamp_atualizado < :threshold")
    suspend fun deleteWhereTimestampOlderThan(threshold: Long)
}
