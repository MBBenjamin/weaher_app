package com.weather.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.weather.R

/**
 * Mapeia códigos WMO para recursos de drawable e string do app.
 *
 * Código null (CE-05) usa 45 (névoa) como fallback neutro e seguro.
 * Código inválido (fora de 0–99) usa [R.drawable.ic_wmo_fog] e [R.string.weather_unknown].
 */
object WmoMapper {

    /**
     * Retorna o drawable correspondente ao código WMO.
     *
     * @param code código WMO 0–99, ou null (usa fallback 45)
     */
    @DrawableRes
    fun iconeWMO(code: Int?): Int = when (code ?: FALLBACK_CODE) {
        0 -> R.drawable.ic_wmo_clear
        1 -> R.drawable.ic_wmo_partly_cloudy
        2 -> R.drawable.ic_wmo_partly_cloudy
        3 -> R.drawable.ic_wmo_cloudy
        45, 48 -> R.drawable.ic_wmo_fog
        51, 53, 55 -> R.drawable.ic_wmo_drizzle
        56, 57 -> R.drawable.ic_wmo_drizzle
        61, 63, 65 -> R.drawable.ic_wmo_rain
        66, 67 -> R.drawable.ic_wmo_rain
        71, 73, 75 -> R.drawable.ic_wmo_snow
        77 -> R.drawable.ic_wmo_snow
        80, 81, 82 -> R.drawable.ic_wmo_showers
        85, 86 -> R.drawable.ic_wmo_snow
        95 -> R.drawable.ic_wmo_thunderstorm
        96, 99 -> R.drawable.ic_wmo_thunderstorm
        else -> R.drawable.ic_wmo_fog
    }

    /**
     * Retorna o ID de string correspondente ao código WMO (PT-BR).
     *
     * Retorna um [StringRes] para compatibilidade com TalkBack via `stringResource()`.
     *
     * @param code código WMO 0–99, ou null (usa fallback 45)
     */
    @StringRes
    fun descricaoWMO(code: Int?): Int = when (code ?: FALLBACK_CODE) {
        0 -> R.string.weather_clear_sky
        1 -> R.string.weather_mainly_clear
        2 -> R.string.weather_partly_cloudy
        3 -> R.string.weather_overcast
        45 -> R.string.weather_fog
        48 -> R.string.weather_depositing_rime_fog
        51 -> R.string.weather_drizzle_light
        53 -> R.string.weather_drizzle_moderate
        55 -> R.string.weather_drizzle_dense
        56 -> R.string.weather_freezing_drizzle_light
        57 -> R.string.weather_freezing_drizzle_dense
        61 -> R.string.weather_rain_slight
        63 -> R.string.weather_rain_moderate
        65 -> R.string.weather_rain_heavy
        66 -> R.string.weather_freezing_rain_light
        67 -> R.string.weather_freezing_rain_heavy
        71 -> R.string.weather_snow_slight
        73 -> R.string.weather_snow_moderate
        75 -> R.string.weather_snow_heavy
        77 -> R.string.weather_snow_grains
        80 -> R.string.weather_showers_slight
        81 -> R.string.weather_showers_moderate
        82 -> R.string.weather_showers_violent
        85 -> R.string.weather_snow_showers_slight
        86 -> R.string.weather_snow_showers_heavy
        95 -> R.string.weather_thunderstorm
        96 -> R.string.weather_thunderstorm_hail_slight
        99 -> R.string.weather_thunderstorm_hail_heavy
        else -> R.string.weather_unknown
    }

    /** Código WMO usado quando o valor recebido da API é null (CE-05). */
    private const val FALLBACK_CODE = 45
}
