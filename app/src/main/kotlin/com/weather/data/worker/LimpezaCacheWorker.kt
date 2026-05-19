package com.weather.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weather.data.local.dao.PrevisaoDao
import com.weather.utils.CacheValidator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Worker que remove entradas de cache com mais de 7 dias.
 *
 * Agendado com [androidx.work.PeriodicWorkRequest] no [com.weather.WeatherApplication]
 * com frequência semanal. Usa `@HiltWorker` para injeção de [PrevisaoDao].
 */
@HiltWorker
class LimpezaCacheWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    internal val previsaoDao: PrevisaoDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val threshold = CacheValidator.thresholdObsoleto()
        previsaoDao.deleteWhereTimestampOlderThan(threshold)
        Timber.d("LimpezaCacheWorker: registros com mais de 7 dias removidos (threshold=$threshold)")
        return Result.success()
    }
}
