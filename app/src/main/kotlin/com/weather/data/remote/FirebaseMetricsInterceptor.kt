package com.weather.data.remote

import com.google.firebase.perf.FirebasePerformance
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor que registra métricas de HTTP no Firebase Performance Monitoring.
 *
 * Para cada request, abre um [HttpMetric] com URL e método HTTP, registra o response code
 * e o tamanho do payload, e finaliza a métrica no bloco `finally` para garantir que
 * traces incompletos (timeout, exceção) também sejam reportados.
 */
@Singleton
class FirebaseMetricsInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        val metric = FirebasePerformance.getInstance().newHttpMetric(url, method)
        metric.start()

        return try {
            val response = chain.proceed(request)
            metric.setHttpResponseCode(response.code)

            val contentLength = response.body?.contentLength() ?: -1L
            if (contentLength > 0) {
                metric.setResponsePayloadSize(contentLength)
            }

            response
        } finally {
            metric.stop()
        }
    }
}
