package com.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para o histórico de cidades buscadas pelo usuário.
 *
 * Regras de negócio:
 * - Máximo 5 entradas (as mais antigas são removidas quando ultrapassado)
 * - Cidades duplicadas têm [buscadoEm] atualizado e sobem para o topo
 * - Ordenação por [buscadoEm] DESC ao exibir no SearchSheet
 */
@Entity(tableName = "historico_busca")
data class HistoricoBuscaEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "nome_cidade")
    val nomeCidade: String,

    /** Estado/província (admin1). Ex: "São Paulo". */
    @ColumnInfo(name = "estado")
    val estado: String,

    @ColumnInfo(name = "pais")
    val pais: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    /** Epoch millis da última vez que o usuário selecionou esta cidade. */
    @ColumnInfo(name = "buscado_em")
    val buscadoEm: Long
)
