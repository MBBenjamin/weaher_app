package com.weather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor de conectividade de rede que expõe o estado online/offline como [StateFlow].
 *
 * Registra um [ConnectivityManager.NetworkCallback] via `callbackFlow` e converte
 * os eventos para um `StateFlow<Boolean>` compartilhado. O escopo do singleton vive
 * enquanto o processo estiver ativo — sem vazamento porque é `@Singleton`.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * `true` quando há conectividade com a internet; `false` caso contrário.
     *
     * Inicia com `true` para não bloquear operações antes do primeiro callback,
     * e transita para o estado real assim que o sistema reporta a rede.
     */
    val isOnline: StateFlow<Boolean> = callbackFlow {
        val cm = context.getSystemService(ConnectivityManager::class.java)

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(cm.isCurrentlyConnected())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                trySend(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, callback)
        trySend(cm.isCurrentlyConnected())

        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean =
        activeNetwork
            ?.let { getNetworkCapabilities(it) }
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
}
