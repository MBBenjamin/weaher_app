package com.weather.utils

import com.weather.R
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes unitários do [WmoMapper].
 *
 * Verificam que os recursos de drawable e string corretos são retornados
 * para códigos WMO conhecidos, inválidos e nulos.
 */
class WmoMapperTest {

    @Test
    fun wmo_codigo_0_retorna_descricao_Ceu_Limpo() {
        assertEquals(R.string.weather_clear_sky, WmoMapper.descricaoWMO(0))
        assertEquals(R.drawable.ic_wmo_clear, WmoMapper.iconeWMO(0))
    }

    @Test
    fun wmo_codigo_95_retorna_descricao_Tempestade() {
        assertEquals(R.string.weather_thunderstorm, WmoMapper.descricaoWMO(95))
        assertEquals(R.drawable.ic_wmo_thunderstorm, WmoMapper.iconeWMO(95))
    }

    @Test
    fun wmo_codigo_999_invalido_retorna_descricao_default() {
        // Código inválido → fallback para névoa (fog) como código neutro (CE-05)
        assertEquals(R.string.weather_unknown, WmoMapper.descricaoWMO(999))
        assertEquals(R.drawable.ic_wmo_fog, WmoMapper.iconeWMO(999))
    }
}
