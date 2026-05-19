package com.weather.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementação de produção de [ILocationSource] que delega ao
 * [FusedLocationProviderClient] do Google Play Services.
 *
 * Requer permissões de localização concedidas antes de chamar os métodos.
 */
internal class FusedLocationSource @Inject constructor(
    private val client: FusedLocationProviderClient
) : ILocationSource {

    @SuppressLint("MissingPermission")
    override suspend fun ultimaLocalizacao(): Location? = client.lastLocation.await()

    @SuppressLint("MissingPermission")
    override suspend fun localizacaoAtual(): Location? =
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
}
