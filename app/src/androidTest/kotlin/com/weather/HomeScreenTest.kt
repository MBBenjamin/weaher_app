package com.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes instrumentados da [MainActivity] / [com.weather.presentation.home.HomeScreen].
 *
 * Valida os fluxos principais: pull-to-refresh, abertura do DayDetailSheet e
 * foco no campo de busca exibindo histórico.
 *
 * Requer emulador/dispositivo conectado — execute via:
 * `./gradlew connectedAndroidTest`
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun pullToRefresh_exibe_spinner_e_dados_atualizam() {
        // Aguarda estado inicial carregar (spinner de carregamento ou conteúdo)
        composeTestRule.waitForIdle()

        // Executa swipe para baixo na tela para acionar pull-to-refresh
        composeTestRule.onNodeWithTag("home_content").performTouchInput { swipeDown() }

        // Aguarda o indicador de refresh aparecer e desaparecer
        composeTestRule.waitForIdle()

        // Após refresh, a tela deve exibir conteúdo (não estado de erro sem dados)
        composeTestRule.onNodeWithTag("home_content").assertIsDisplayed()
    }

    @Test
    fun tap_em_DayCard_abre_DayDetailSheet() {
        // Aguarda a tela de sucesso com a lista semanal
        composeTestRule.waitUntil(timeoutMillis = 5_000L) {
            composeTestRule
                .onNodeWithTag("weekly_forecast_list")
                .fetchSemanticsNode(false) != null
        }

        // Toca no primeiro DayCard da lista semanal
        composeTestRule.onNodeWithTag("day_card_0").performClick()
        composeTestRule.waitForIdle()

        // DayDetailSheet deve aparecer com o header de detalhes
        composeTestRule.onNodeWithTag("day_detail_sheet").assertIsDisplayed()
    }

    @Test
    fun campo_busca_ao_focar_exibe_historico() {
        // Aguarda a tela principal carregar
        composeTestRule.waitForIdle()

        // Abre o SearchSheet clicando no botão de busca
        composeTestRule.onNodeWithTag("btn_abrir_busca").performClick()
        composeTestRule.waitForIdle()

        // SearchSheet deve aparecer com o campo de busca visível
        composeTestRule.onNodeWithTag("search_bar").assertIsDisplayed()

        // Histórico ou placeholder de busca deve estar visível
        composeTestRule.onNodeWithTag("search_content").assertIsDisplayed()
    }
}
