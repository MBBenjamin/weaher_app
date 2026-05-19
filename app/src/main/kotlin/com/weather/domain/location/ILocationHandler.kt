package com.weather.domain.location

import kotlinx.coroutines.flow.Flow

/**
 * Contrato para obtenção da localização do dispositivo via estratégia híbrida.
 *
 * Emite primeiro via Network (<500ms) e depois refina silenciosamente via GPS
 * quando a diferença excede 100m.
 */
interface ILocationHandler {
    /**
     * Retorna um [Flow] que emite [LocationResult] conforme a localização evolui.
     *
     * Ordem típica:
     * 1. [LocationResult.Success] com `isApproximate=true` (via `lastLocation` Network)
     * 2. Opcionalmente [LocationResult.GpsRefinement] se GPS refinar em >100m
     *
     * Emite [LocationResult.PermissionDenied] se as permissões não foram concedidas.
     * Emite [LocationResult.LocationFailed] se não há lastLocation e getCurrentLocation falha.
     */
    fun observarLocalizacao(): Flow<LocationResult>
}

/** Resultado de uma tentativa de obtenção de localização. */
sealed class LocationResult {
    /** Localização obtida com sucesso. */
    data class Success(
        val lat: Double,
        val lon: Double,
        /** `true` quando veio da lastLocation de Network (GPS ainda não refinou). */
        val isApproximate: Boolean
    ) : LocationResult()

    /** GPS refinou a posição com delta > 100m em relação à estimativa inicial. */
    data class GpsRefinement(val lat: Double, val lon: Double) : LocationResult()

    /** Permissão ACCESS_FINE_LOCATION ou ACCESS_COARSE_LOCATION negada. */
    data object PermissionDenied : LocationResult()

    /** Falha ao obter localização (sem cache e timeout). */
    data object LocationFailed : LocationResult()
}
