package com.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que persiste a previsão do tempo para uma localização.
 *
 * A chave primária é `"lat_2d,lon_2d"` — coordenadas arredondadas a 2 casas decimais —
 * garantindo que localizações próximas compartilhem cache e evitando registros duplicados
 * para o mesmo ponto geográfico.
 *
 * O campo [dadosJson] armazena o snapshot completo da resposta da API em JSON para
 * permitir reconstituição offline sem perda de dados.
 */
@Entity(
    tableName = "previsoes",
    indices = [Index(value = ["timestamp_atualizado"])]
)
data class PrevisaoEntity(

    /** Chave composta `"lat_2d,lon_2d"`, ex: `"-23.55,-46.63"`. */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    /** Ex: "São Paulo, SP". Exibido no badge de localização. */
    @ColumnInfo(name = "nome_localidade")
    val nomeLocalidade: String,

    @ColumnInfo(name = "temp_atual")
    val tempAtual: Float,

    @ColumnInfo(name = "sensacao_termica")
    val sensacaoTermica: Float,

    /** 0–100 %. */
    @ColumnInfo(name = "umidade")
    val umidade: Int,

    @ColumnInfo(name = "velocidade_vento")
    val velocidadeVento: Float,

    /** 0–359 graus. */
    @ColumnInfo(name = "direcao_vento")
    val direcaoVento: Int,

    /** Código WMO 0–99. */
    @ColumnInfo(name = "codigo_wmo")
    val codigoWmo: Int,

    /** Snapshot JSON completo da resposta da API — necessário para reconstituição offline. */
    @ColumnInfo(name = "dados_json")
    val dadosJson: String,

    /** Epoch millis da última atualização. Usado pelo [CacheValidator]. */
    @ColumnInfo(name = "timestamp_atualizado")
    val timestampAtualizado: Long,

    /** Epoch millis da criação do registro. */
    @ColumnInfo(name = "criado_em")
    val criadoEm: Long
)
