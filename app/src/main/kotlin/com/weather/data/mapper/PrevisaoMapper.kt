package com.weather.data.mapper

import com.weather.data.local.entity.PrevisaoEntity
import com.weather.data.remote.dto.PrevisaoResponseDto
import com.weather.domain.model.DadosAtuais
import com.weather.domain.model.DadosDiarios
import com.weather.domain.model.DadosHorarios
import com.weather.domain.model.DiaDados
import com.weather.domain.model.HoraDados
import com.weather.domain.model.Previsao
import com.weather.utils.DateFormatter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.math.roundToInt

private val mapperJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun PrevisaoResponseDto.toDomain(nomeLocalidade: String): Previsao {
    val now = System.currentTimeMillis()
    val todayIso = daily.time.firstOrNull() ?: ""

    val atual = DadosAtuais(
        temperaturaC = current.temperature2m
            .also { if (it == null) Timber.w("PrevisaoMapper: temperature2m null, using fallback 0f") }
            ?: 0f,
        sensacaoTermicaC = current.apparentTemperature
            .also { if (it == null) Timber.w("PrevisaoMapper: apparentTemperature null, using fallback 0f") }
            ?: 0f,
        umidadePercent = current.relativeHumidity2m
            .also { if (it == null) Timber.w("PrevisaoMapper: relativeHumidity2m (current) null, using fallback 0") }
            ?: 0,
        velocidadeVentoKmh = current.windSpeed10m
            .also { if (it == null) Timber.w("PrevisaoMapper: windSpeed10m (current) null, using fallback 0f") }
            ?: 0f,
        direcaoVentoGraus = current.windDirection10m
            .also { if (it == null) Timber.w("PrevisaoMapper: windDirection10m (current) null, using fallback 0") }
            ?: 0,
        codigoWMO = current.weatherCode
            .also { if (it == null) Timber.w("PrevisaoMapper: weatherCode (current) null, using fallback 45") }
            ?: 45,
        horaAtualizado = current.time
    )

    val horas = hourly.time.mapIndexed { i, timeStr ->
        HoraDados(
            dataIso = timeStr.substringBefore('T'),
            hora = DateFormatter.formatarHora(timeStr),
            temperaturaC = hourly.temperature2m.getOrNull(i) ?: 0f,
            precipitacaoMm = hourly.precipitation.getOrNull(i) ?: 0f,
            codigoWMO = hourly.weatherCode.getOrNull(i) ?: 45,
            umidadePercent = hourly.relativeHumidity2m.getOrNull(i) ?: 0,
            velocidadeVentoKmh = hourly.windSpeed10m.getOrNull(i) ?: 0f,
            direcaoVentoGraus = hourly.windDirection10m.getOrNull(i) ?: 0
        )
    }

    val dias = daily.time.mapIndexed { i, dateIso ->
        DiaDados(
            data = DateFormatter.formatarDiaSemana(dateIso),
            dataIso = dateIso,
            temperaturaMaxC = daily.temperature2mMax.getOrNull(i) ?: 0f,
            temperaturaMinC = daily.temperature2mMin.getOrNull(i) ?: 0f,
            probChuvaPercent = daily.precipitationProbabilityMax.getOrNull(i) ?: 0,
            velocidadeMaxVentoKmh = daily.windSpeed10mMax.getOrNull(i) ?: 0f,
            direcaoDominanteVentoGraus = daily.windDirection10mDominant.getOrNull(i) ?: 0,
            umidadeMaxPercent = daily.relativeHumidity2mMax.getOrNull(i) ?: 0,
            codigoWMO = daily.weatherCode.getOrNull(i) ?: 45,
            eHoje = dateIso == todayIso
        )
    }

    return Previsao(
        latitude = latitude,
        longitude = longitude,
        nomeLocalidade = nomeLocalidade,
        fusoHorario = timezone,
        atual = atual,
        horario = DadosHorarios(horas),
        diario = DadosDiarios(dias),
        timestampAtualizado = now
    )
}

fun PrevisaoEntity.toDomain(): Previsao =
    mapperJson.decodeFromString<Previsao>(dadosJson)

fun Previsao.toEntity(nomeLocalidade: String): PrevisaoEntity {
    val latStr = (latitude * 100).roundToInt().let { it / 100.0 }
    val lonStr = (longitude * 100).roundToInt().let { it / 100.0 }
    val id = "$latStr,$lonStr"
    val now = System.currentTimeMillis()
    return PrevisaoEntity(
        id = id,
        latitude = latitude,
        longitude = longitude,
        nomeLocalidade = nomeLocalidade,
        tempAtual = atual.temperaturaC,
        sensacaoTermica = atual.sensacaoTermicaC,
        umidade = atual.umidadePercent,
        velocidadeVento = atual.velocidadeVentoKmh,
        direcaoVento = atual.direcaoVentoGraus,
        codigoWmo = atual.codigoWMO,
        dadosJson = mapperJson.encodeToString(this),
        timestampAtualizado = timestampAtualizado,
        criadoEm = now
    )
}
