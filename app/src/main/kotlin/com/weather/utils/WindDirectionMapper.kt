package com.weather.utils

/**
 * Converte direção do vento em graus para cardinal PT-BR.
 *
 * Faixas:
 * - 0–22° e 338–360° → "N"
 * - 23–67°  → "NE"
 * - 68–112° → "E"
 * - 113–157° → "SE"
 * - 158–202° → "S"
 * - 203–247° → "SO"
 * - 248–292° → "O"
 * - 293–337° → "NO"
 */
object WindDirectionMapper {

    /**
     * Retorna a direção cardinal para um ângulo em graus.
     *
     * @param graus ângulo 0–360 (valores fora do intervalo são normalizados via módulo)
     */
    fun paraCardinal(graus: Int): String {
        val normalizado = ((graus % 360) + 360) % 360
        return when (normalizado) {
            in 0..22 -> "N"
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SO"
            in 248..292 -> "O"
            in 293..337 -> "NO"
            else -> "N" // 338–360
        }
    }
}
