package com.weather.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utilitários de formatação de datas e timestamps para exibição em PT-BR.
 *
 * Todas as funções são puras — sem estado, sem side effects.
 */
object DateFormatter {

    private val LOCALE_PT_BR = Locale("pt", "BR")

    private val FORMATTER_DIA_SEMANA = DateTimeFormatter.ofPattern("EEE, d MMM", LOCALE_PT_BR)
    private val FORMATTER_HORA = DateTimeFormatter.ofPattern("HH:mm", LOCALE_PT_BR)

    /**
     * Formata uma data ISO8601 no padrão curto PT-BR.
     *
     * @param iso data no formato "yyyy-MM-dd", ex: "2026-05-17"
     * @return ex: "Sex, 17 mai"
     */
    fun formatarDiaSemana(iso: String): String =
        LocalDate.parse(iso).format(FORMATTER_DIA_SEMANA)
            .replaceFirstChar { it.uppercase() }

    /**
     * Extrai e formata a hora de um datetime ISO8601.
     *
     * @param iso datetime no formato "yyyy-MM-ddTHH:mm", ex: "2026-05-17T14:30"
     * @return ex: "14:00"
     */
    fun formatarHora(iso: String): String =
        LocalDateTime.parse(iso).format(FORMATTER_HORA)

    /**
     * Formata um timestamp em epoch millis como tempo relativo em PT-BR.
     *
     * @param timestampMs epoch millis
     * @return "Atualizado agora", "Atualizado há 5 min", "Atualizado há 2h", etc.
     */
    fun formatarTimestampRelativo(timestampMs: Long): String {
        val diffMs = System.currentTimeMillis() - timestampMs
        val diffMin = (diffMs / 60_000).toInt()
        val diffH = (diffMs / 3_600_000).toInt()

        return when {
            diffMin < 1 -> "Atualizado agora"
            diffMin < 60 -> "Atualizado há $diffMin min"
            else -> "Atualizado há ${diffH}h"
        }
    }

    /**
     * Converte epoch millis para [LocalDate] no fuso especificado.
     *
     * @param timestampMs epoch millis
     * @param zoneId fuso horário, ex: "America/Sao_Paulo"
     */
    fun toLocalDate(timestampMs: Long, zoneId: String = "America/Sao_Paulo"): LocalDate =
        Instant.ofEpochMilli(timestampMs)
            .atZone(ZoneId.of(zoneId))
            .toLocalDate()
}
