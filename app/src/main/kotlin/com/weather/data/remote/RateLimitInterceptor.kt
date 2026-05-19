package com.weather.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exceção lançada quando a API retorna HTTP 429 (Too Many Requests).
 *
 * @param retryAfterSeconds segundos a aguardar antes de tentar novamente.
 *   Lido do header `Retry-After`; se ausente ou inválido, usa 60s como padrão (CE-03).
 */
class RateLimitException(val retryAfterSeconds: Int) : Exception(
    "Rate limit atingido. Tente novamente em ${retryAfterSeconds}s."
)

/**
 * OkHttp Interceptor que intercepta respostas HTTP 429 e lança [RateLimitException].
 *
 * O valor de `retryAfterSeconds` é extraído do header `Retry-After`.
 * Se o header estiver ausente ou contiver um valor não numérico, usa 60s como padrão
 * conforme CE-03 da spec.
 */
@Singleton
class RateLimitInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == HTTP_TOO_MANY_REQUESTS) {
            val retryAfter = response.header(HEADER_RETRY_AFTER)
                ?.toIntOrNull()
                ?: DEFAULT_RETRY_AFTER_SECONDS

            response.close()
            throw RateLimitException(retryAfterSeconds = retryAfter)
        }

        return response
    }

    private companion object {
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val HEADER_RETRY_AFTER = "Retry-After"
        const val DEFAULT_RETRY_AFTER_SECONDS = 60
    }
}
