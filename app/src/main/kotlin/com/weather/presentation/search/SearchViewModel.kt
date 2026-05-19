package com.weather.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.domain.model.CidadeSugestao
import com.weather.domain.repository.IBuscaRepository
import com.weather.domain.usecase.BuscarCidadesUseCase
import com.weather.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel do painel de busca de cidades (US5).
 *
 * Gerencia [SearchUiState] com debounce de 500ms, emite [cidadeSelecionadaEvent]
 * como [SharedFlow] para o [HomeScreen] carregar a previsão da cidade selecionada.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val buscarCidadesUseCase: BuscarCidadesUseCase,
    private val buscaRepository: IBuscaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _cidadeSelecionadaEvent = MutableSharedFlow<CidadeSugestao>()

    /** Emitido ao selecionar uma cidade — observado pelo [HomeScreen] para carregar previsão. */
    val cidadeSelecionadaEvent: SharedFlow<CidadeSugestao> = _cidadeSelecionadaEvent.asSharedFlow()

    private val _query = MutableStateFlow("")

    init {
        observarQuery()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observarQuery() {
        viewModelScope.launch {
            _query
                .debounce(500L)
                .filter { it.length >= 2 }
                .flatMapLatest { query ->
                    flow {
                        emit(SearchUiState.Carregando)
                        val historico = buscaRepository.obterHistorico()
                        when (val result = buscarCidadesUseCase(query)) {
                            is AppResult.Success ->
                                emit(SearchUiState.Resultados(result.data, historico))
                            is AppResult.Error ->
                                emit(SearchUiState.Erro(result.message))
                            AppResult.Loading -> Unit
                        }
                    }
                }
                .collect { _uiState.value = it }
        }
    }

    /** Atualiza a query e reseta para Idle quando vazia ou muito curta. */
    fun onQueryChange(query: String) {
        _query.value = query
        if (query.length < 2) {
            _uiState.value = SearchUiState.Idle
        }
    }

    /**
     * Salva a cidade no histórico, emite [cidadeSelecionadaEvent] e recarrega o histórico.
     *
     * O [HomeScreen] observa [cidadeSelecionadaEvent] para acionar [HomeViewModel.carregarPrevisao].
     */
    fun selecionarCidade(cidade: CidadeSugestao) {
        viewModelScope.launch {
            buscaRepository.salvarNoBusca(cidade)
            _cidadeSelecionadaEvent.emit(cidade)
        }
    }

    /** Carrega o histórico local e atualiza o estado para exibição no painel. */
    fun carregarHistorico() {
        viewModelScope.launch {
            val historico = buscaRepository.obterHistorico()
            val sugestoesAtuais = (_uiState.value as? SearchUiState.Resultados)?.sugestoes
                ?: emptyList()
            _uiState.value = SearchUiState.Resultados(sugestoesAtuais, historico)
        }
    }
}
