package com.weather.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.weather.data.local.dao.PrevisaoDao
import com.weather.data.worker.LimpezaCacheWorker
import com.weather.utils.CacheValidator
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Testa que [LimpezaCacheWorker] deleta apenas registros com timestamp
 * mais antigo que 7 dias, usando o threshold de [CacheValidator.thresholdObsoleto].
 */
class LimpezaCacheWorkerTest {

    private lateinit var dao: PrevisaoDao
    private lateinit var worker: LimpezaCacheWorker

    @Before
    fun setup() {
        dao = mockk(relaxUnitFun = true)
        worker = LimpezaCacheWorker(
            context = mockk(relaxed = true),
            workerParams = mockk(relaxed = true),
            previsaoDao = dao
        )
    }

    @Test
    fun LimpezaCacheWorker_deleta_apenas_registros_com_timestamp_mais_antigo_que_7_dias() = runTest {
        val result = worker.doWork()

        // Retorna sucesso
        assertEquals(ListenableWorker.Result.success(), result)

        // Chama DAO com threshold correto (≈ now - 7 dias, margem de 5s)
        val sete_dias_ms = 7L * 24 * 60 * 60 * 1000
        val thresholdEsperado = System.currentTimeMillis() - sete_dias_ms
        coVerify(exactly = 1) {
            dao.deleteWhereTimestampOlderThan(
                withArg { threshold ->
                    assert(kotlin.math.abs(threshold - thresholdEsperado) < 5_000L) {
                        "Threshold fora do intervalo esperado: $threshold vs $thresholdEsperado"
                    }
                }
            )
        }
    }
}
