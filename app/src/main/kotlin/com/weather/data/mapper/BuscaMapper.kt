package com.weather.data.mapper

import com.weather.data.local.entity.HistoricoBuscaEntity
import com.weather.data.remote.dto.CidadeDto
import com.weather.domain.model.CidadeSugestao
import com.weather.domain.model.HistoricoBusca

/** Mapeia um DTO de geocoding para o modelo de domínio [CidadeSugestao]. */
fun CidadeDto.toDomain(): CidadeSugestao = CidadeSugestao(
    nome = name,
    estado = admin1,
    pais = country,
    latitude = latitude,
    longitude = longitude,
    fusoHorario = timezone
)

/** Mapeia uma entidade Room para o modelo de domínio [HistoricoBusca]. */
fun HistoricoBuscaEntity.toDomain(): HistoricoBusca = HistoricoBusca(
    nomeCidade = nomeCidade,
    estado = estado.ifEmpty { null },
    pais = pais,
    latitude = latitude,
    longitude = longitude,
    buscadoEm = buscadoEm
)

/** Converte uma [CidadeSugestao] para entidade Room com timestamp atual. */
fun CidadeSugestao.toHistoricoEntity(): HistoricoBuscaEntity = HistoricoBuscaEntity(
    nomeCidade = nome,
    estado = estado ?: "",
    pais = pais,
    latitude = latitude,
    longitude = longitude,
    buscadoEm = System.currentTimeMillis()
)
