package com.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes instrumentados do fluxo de busca de cidades ([com.weather.presentation.search.SearchSheet]).
 *
 * Valida o debounce de 600ms e o fechamento do sheet após seleção.
 *
 * Requer emulador/dispositivo conectado — execute via:
 * `./gradlew connectedAndroidTest`
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun digitar_cidade_aguarda_600ms_debounce_e_resultados_aparecem() {
        // Aguarda tela principal e abre busca
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("btn_abrir_busca").performClick()
        composeTestRule.waitForIdle()

        // Digita "São Paulo" no campo de busca
        composeTestRule.onNodeWithTag("search_bar").performTextInput("São Paulo")

        // Aguarda debounce de 600ms + tempo de resposta (máx 2s)
        composeTestRule.mainClock.advanceTimeBy(600L)
        composeTestRule.waitForIdle()

        // Resultados devem aparecer na lista
        composeTestRule.onNodeWithTag("search_results_list").assertIsDisplayed()
    }

    @Test
    fun tap_em_resultado_fecha_sheet_e_atualiza_previsao() {
        // Aguarda tela principal e abre busca
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("btn_abrir_busca").performClick()
        composeTestRule.waitForIdle()

        // Digita e aguarda resultados
        composeTestRule.onNodeWithTag("search_bar").performTextInput("São Paulo")
        composeTestRule.mainClock.advanceTimeBy(600L)
        composeTestRule.waitForIdle()

        // Toca no primeiro resultado
        composeTestRule.onNodeWithTag("search_result_0").performClick()
        composeTestRule.waitForIdle()

        // Sheet deve fechar — SearchBar não deve estar mais visível
        composeTestRule.onNodeWithTag("search_bar").assertDoesNotExist()

        // HomeScreen deve estar visível com conteúdo atualizado
        composeTestRule.onNodeWithTag("home_content").assertIsDisplayed()
    }
}
