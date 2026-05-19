package com.weather.utils

/**
 * Verifica o estado de validade de um cache baseado em timestamp.
 *
 * Estados:
 * - **Válido**: age < 1h → usar sem revalidar
 * - **Expirado**: 1h ≤ age < 7d → exibir cache e tentar revalidar em background
 * - **Obsoleto**: age ≥ 7d → candidato à exclusão pelo [LimpezaCacheWorker]
 */
object CacheValidator {

    private const val UMA_HORA_MS = 3_600_000L
    private const val SETE_DIAS_MS = 604_800_000L

    /**
     * Retorna `true` se o cache for válido (age < 1h).
     *
     * @param timestampMs epoch millis da última atualização do cache
     */
    fun estaValido(timestampMs: Long): Boolean =
        ageMs(timestampMs) < UMA_HORA_MS

    /**
     * Retorna `true` se o cache estiver expirado mas ainda recuperável (1h ≤ age < 7d).
     *
     * @param timestampMs epoch millis da última atualização do cache
     */
    fun estaExpirado(timestampMs: Long): Boolean {
        val age = ageMs(timestampMs)
        return age >= UMA_HORA_MS && age < SETE_DIAS_MS
    }

    /**
     * Retorna `true` se o cache for obsoleto (age ≥ 7d) e deve ser removido.
     *
     * @param timestampMs epoch millis da última atualização do cache
     */
    fun estaObsoleto(timestampMs: Long): Boolean =
        ageMs(timestampMs) >= SETE_DIAS_MS

    /**
     * Calcula quantas horas se passaram desde o timestamp fornecido.
     *
     * @param timestampMs epoch millis da última atualização do cache
     * @return número inteiro de horas (truncado)
     */
    fun calcularHorasAtraso(timestampMs: Long): Int =
        (ageMs(timestampMs) / 3_600_000L).toInt()

    /** Retorna o timestamp a partir do qual registros são considerados obsoletos. */
    fun thresholdObsoleto(): Long = System.currentTimeMillis() - SETE_DIAS_MS

    private fun ageMs(timestampMs: Long): Long = System.currentTimeMillis() - timestampMs
}
