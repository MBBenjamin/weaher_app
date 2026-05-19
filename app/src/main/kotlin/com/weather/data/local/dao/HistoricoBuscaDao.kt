package com.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weather.data.local.entity.HistoricoBuscaEntity

/** DAO para o histórico de cidades buscadas (máx. 5 entradas, ordenado por mais recente). */
@Dao
interface HistoricoBuscaDao {

    /**
     * Retorna todas as entradas ordenadas da mais recente para a mais antiga.
     *
     * Usado pelo SearchSheet para exibir o histórico ao abrir a busca.
     */
    @Query("SELECT * FROM historico_busca ORDER BY buscado_em DESC")
    suspend fun getAllOrderedByBuscadoEmDesc(): List<HistoricoBuscaEntity>

    /**
     * Insere a cidade, substituindo registro existente com mesmo id.
     *
     * Para upsert correto (mover cidade existente para o topo), use [upsert] em vez
     * desta função diretamente — ele remove a entrada anterior pelo nome+país antes
     * de inserir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(entrada: HistoricoBuscaEntity)

    /**
     * Remove a entrada de cidade com mesmo nome e país, se existir.
     *
     * Chamado por [upsert] antes de reinserir para atualizar o timestamp e mover para o topo.
     */
    @Query("DELETE FROM historico_busca WHERE nome_cidade = :nomeCidade AND pais = :pais")
    suspend fun deletarPorNomeEPais(nomeCidade: String, pais: String)

    /**
     * Remove entradas além das [max] mais recentes (por [HistoricoBuscaEntity.buscadoEm]).
     *
     * Mantém o histórico limitado a no máximo [max] entradas.
     *
     * @param max número máximo de entradas a manter (default 5)
     */
    @Query(
        """
        DELETE FROM historico_busca
        WHERE id NOT IN (
            SELECT id FROM historico_busca
            ORDER BY buscado_em DESC
            LIMIT :max
        )
        """
    )
    suspend fun deleteExcetoMaisRecentes(max: Int = 5)

    /**
     * Insere ou atualiza a entrada de uma cidade no histórico.
     *
     * Se a cidade já existe (mesmo nome e país), remove o registro antigo antes de
     * inserir o novo, garantindo que ela suba para o topo da lista.
     * Após a inserção, remove entradas além do limite máximo.
     */
    suspend fun upsert(entrada: HistoricoBuscaEntity) {
        deletarPorNomeEPais(entrada.nomeCidade, entrada.pais)
        inserir(entrada)
        deleteExcetoMaisRecentes(max = 5)
    }
}
