# Style Guide — Clima Agora (Android)

Convenções e decisões de projeto que governam todo o código Kotlin e Android do MVP.

---

## Nomenclatura

| Elemento | Convenção | Exemplo |
|----------|-----------|---------|
| Packages | `minúsculas`, sem underscores | `com.weather.data.remote` |
| Classes / Objects | `PascalCase` | `PrevisaoRepositoryImpl` |
| Interfaces | prefixo `I` + `PascalCase` | `IPrevisaoRepository`, `IBuscaRepository` |
| Funções / variáveis | `camelCase` | `carregarPrevisao()`, `horasAtraso` |
| Constantes (companion) | `UPPER_SNAKE_CASE` | `DEFAULT_RETRY_AFTER_SECONDS` |
| Arquivos de layout XML | `snake_case` | `activity_main.xml` |
| Resource IDs | `tipo_descricao` | `btn_tentar_novamente`, `tv_temperatura` |
| Arquivos Kotlin | Mesmo nome da classe principal | `HomeViewModel.kt` |

### Interfaces com prefixo `I`

Todas as interfaces de repositório e handlers levam o prefixo `I` para distinguir a interface
da implementação sem ambiguidade:

```kotlin
interface IPrevisaoRepository { ... }
class PrevisaoRepositoryImpl @Inject constructor(...) : IPrevisaoRepository
```

---

## Organização de ViewModels

**Decisão**: ViewModels são **co-localizados com sua feature**, não em package separado.

```
presentation/
├── home/
│   ├── HomeScreen.kt
│   ├── HomeViewModel.kt       ← co-localizado aqui
│   └── components/
└── search/
    ├── SearchSheet.kt
    └── SearchViewModel.kt     ← co-localizado aqui
```

**Por quê**: O projeto é single-module de tamanho MVP. Um package separado
(`presentation/viewmodels/`) acrescentaria indireção sem benefício real de organização.
Cada ViewModel só é relevante para a feature a que pertence.

---

## Resultado customizado: `AppResult<out T>`

Usar sempre `AppResult` — nunca `Result<T>` — para evitar ambiguidade com `kotlin.Result` do stdlib:

```kotlin
// ✅ Correto
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()
}

// ❌ Proibido — conflita com kotlin.Result
sealed class Result<out T> { ... }
```

---

## Feedback Háptico

Obrigatório em ações de confirmação, conforme Constitution §III:

| Ação | Tipo de feedback |
|------|-----------------|
| Pull-to-refresh confirmado | `HapticFeedbackType.LongPress` |
| Seleção de item (busca, dia) | `HapticFeedbackType.TextHandleMove` |

```kotlin
// Pull-to-refresh
val haptic = LocalHapticFeedback.current
SwipeRefresh(
    onRefresh = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.onPullToRefresh()
    }
)

// Seleção de cidade
ResultItem(
    onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        searchViewModel.selecionarCidade(cidade)
    }
)
```

---

## Feedback Transitório: Snackbar, nunca Toast

Todo feedback efêmero ao usuário deve usar `SnackbarHostState` — nunca `Toast`:

```kotlin
// ✅ Correto
val snackbarHostState = remember { SnackbarHostState() }
Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
    // ...
}
LaunchedEffect(erroMensagem) {
    erroMensagem?.let { snackbarHostState.showSnackbar(it) }
}

// ❌ Proibido
Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
```

**Por quê**: Toast é deprecated em Material Design 3 e não é acessível via TalkBack.
Snackbar é parte nativa do Scaffold MD3 e anuncia conteúdo para leitores de tela.

---

## KDoc

Obrigatório em todas as classes e funções **públicas**. Regras:

- Primeira linha: o que faz (sem sujeito — não "Esta função faz X")
- `@param` só para parâmetros não óbvios pelo nome
- `@return` se não for `Unit` e não for óbvio pelo nome
- Nunca descrever o "como" — apenas o "o que" e "por que"
- Máximo uma linha por tag

```kotlin
/**
 * Retorna o gradiente Compose para um código WMO.
 *
 * Código null ou inválido usa o gradiente de céu claro como fallback seguro (CE-05).
 *
 * @param code código WMO 0–99, ou null
 */
fun gradientForWmoCode(code: Int?): List<Color>

/**
 * Valida se o timestamp de cache ainda é válido (age < 1h).
 */
fun estaValido(timestampMs: Long): Boolean
```

---

## Logging

- Somente `Timber` — proibido `Log.d`, `Log.e`, `println`
- Nível `w` para campos nulos esperados (CE-05): `Timber.w("Campo null: temperature2m")`
- Nível `e` + exception para erros inesperados: `Timber.e(e, "Falha ao salvar cache")`

---

## Proibições explícitas

| Proibido | Substituto |
|----------|-----------|
| `Toast.makeText(...)` | `SnackbarHostState.showSnackbar(...)` |
| `LiveData` em código novo | `StateFlow` / `SharedFlow` |
| `GlobalScope` | `viewModelScope` / `lifecycleScope` |
| `Log.d` / `Log.e` | `Timber.d` / `Timber.e` |
| `Result<T>` como sealed class própria | `AppResult<T>` |
| Strings hardcoded em Composables | `stringResource(R.string.xxx)` |
| `CountDownTimer` | `viewModelScope.launch { while(...) { delay(1000L) } }` |
