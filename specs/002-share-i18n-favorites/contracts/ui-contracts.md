# UI Contracts: Weather App v1.1

**Gerado por**: `/speckit-plan` | **Data**: 2026-05-27 | **Plano**: [../plan.md](../plan.md)

> Contratos dos novos composables e modificações em composables existentes para v1.1.
> Contratos da v1.0 permanecem em [../../001-weather-app-mvp/contracts/ui-contracts.md](../../001-weather-app-mvp/contracts/ui-contracts.md).

---

## Novos Composables

### WeatherPager

**Arquivo**: `presentation/home/WeatherPager.kt`

**Responsabilidade**: Pager horizontal que envolve o conteúdo de previsão. Substitui a estrutura atual de `HomeScreen` como raiz do conteúdo scrollável.

```kotlin
@Composable
fun WeatherPager(
    paginas: List<PaginaCidade>,           // [LocalizacaoAtual, Favorita("RJ"), ...]
    pagerState: PagerState,               // hoisted — gerenciado pelo HomeViewModel
    previsaoPorPagina: (PaginaCidade) -> HomeUiState, // mapeia página → state
    onCompartilharClick: () -> Unit,
    onFavoritarClick: (Boolean) -> Unit,  // true = adicionar, false = remover
    onFavoritosMenuClick: () -> Unit,     // abre FavoritosBottomSheet
    modifier: Modifier = Modifier
)
```

**Comportamento**:
- Renderiza `HorizontalPager` com `beyondBoundsPageCount = 1` para pré-carregar página adjacente
- `userScrollEnabled = true`; gestos de swipe não conflitam com pull-to-refresh (eixo Y)
- Cada página recebe o mesmo `WeatherPageContent` com state independente
- Dots indicator exibido acima do conteúdo apenas quando `paginas.size > 1`

**Estados visuais**:
- 0 favoritos: sem dots, sem swipe (idêntico à v1.0)
- 1-10 favoritos: dots + swipe habilitado

---

### PagerIndicator

**Arquivo**: `presentation/home/components/PagerIndicator.kt`

**Responsabilidade**: Indicador de páginas (dots) integrado à TopAppBar.

```kotlin
@Composable
fun PagerIndicator(
    totalPaginas: Int,
    paginaAtual: Int,
    modifier: Modifier = Modifier
)
```

**Especificação visual**:
- Dot ativo: 8dp × 8dp, cor `colorPrimary`
- Dot inativo: 6dp × 6dp, cor `colorOnSurface` com 30% alpha
- Espaçamento entre dots: 6dp
- Animação: `animateFloatAsState` no tamanho/opacidade ao mudar página
- Exibido apenas quando `totalPaginas > 1`

---

### FavoritosBottomSheet

**Arquivo**: `presentation/home/components/FavoritosBottomSheet.kt`

**Responsabilidade**: Bottom sheet de navegação rápida entre todas as cidades.

```kotlin
@Composable
fun FavoritosBottomSheet(
    cidadeAtual: String,                  // nome da localização atual (ex: "São Paulo, SP")
    favoritos: List<CidadeFavorita>,      // em ordem alfabética
    paginaAtualIndex: Int,
    onCidadeSelecionada: (Int) -> Unit,   // índice no pager
    onDismiss: () -> Unit,
    sheetState: SheetState
)
```

**Layout de cada item**:
```
┌──────────────────────────────────────────┐
│ 📍 São Paulo, SP          [ativa ✓]      │  ← localização atual
│ ─────────────────────────────────────── │
│ ⭐ Curitiba, PR                          │  ← favorito
│ ⭐ Rio de Janeiro, RJ                    │
│ ⭐ Salvador, BA                          │
│ ─────────────────────────────────────── │
│ [+ Gerenciar favoritos]                  │  ← futura v1.2
└──────────────────────────────────────────┘
```

**Comportamento**:
- Item ativo tem fundo `colorPrimary` com 10% alpha + checkmark à direita
- Tap em item: `onCidadeSelecionada(index)` + `onDismiss()`
- Swipe down ou tap fora: `onDismiss()`
- Max height: 60% da tela (scrollável se muitas cidades)
- Touch target: ≥ 48dp por item (Constitution III)

---

### FavoritoIconButton

**Arquivo**: `presentation/search/components/FavoritoIconButton.kt`

**Responsabilidade**: Ícone de coração nos resultados de busca para favoritar/desfavoritar.

```kotlin
@Composable
fun FavoritoIconButton(
    isFavorito: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean = true,            // false quando limite de 10 atingido
    modifier: Modifier = Modifier
)
```

**Especificação visual**:
- Favoritado: `Icons.Filled.Favorite` cor `colorError` (vermelho MD3)
- Não favoritado: `Icons.Outlined.FavoriteBorder` cor `colorOnSurface` 60% alpha
- Animação: `animateColorAsState` com duração 200ms
- `contentDescription`: `stringResource(R.string.cd_favoritar_cidade)` / `R.string.cd_remover_favorito`
- Quando `enabled = false`: alpha 38%, não clicável (limite atingido e cidade não é favorita)
- Tamanho mínimo do botão: 48dp × 48dp (Constitution III)

---

### ShareButton

**Arquivo**: `presentation/home/components/ShareButton.kt`

**Responsabilidade**: Botão de compartilhamento no TopAppBar.

```kotlin
@Composable
fun ShareButton(
    onShare: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Especificação visual**:
- Ícone: `Icons.Default.Share`
- Tamanho: 24dp icon, 48dp touch target
- `contentDescription`: `stringResource(R.string.cd_compartilhar_previsao)`
- Posição: action slot do `TopAppBar`, ao lado do ícone de favoritos
- Sempre habilitado (funciona offline com dados em cache)

---

## Modificações em Composables Existentes

### HomeScreen.kt — Modificação

**Mudança**: Substituir conteúdo scrollável por `WeatherPager` quando houver favoritos.

**Lógica de renderização**:
```kotlin
if (uiState.paginas.size > 1) {
    WeatherPager(
        paginas = uiState.paginas,
        pagerState = rememberPagerState(pageCount = { uiState.paginas.size }),
        ...
    )
} else {
    // Renderização atual da v1.0 (sem pager — zero overhead)
    WeatherPageContent(uiState = uiState, ...)
}
```

**TopAppBar — novos actions**:
```
[🔍 Busca] ... [← espaço flexível] ... [🔗 Share] [⭐ Favoritar] [☰ Lista]
```
- `ShareButton` — sempre visível
- `FavoritoIconButton` — visível na página atual (não na localização atual)
- Ícone de menu `☰` (lista de cidades) — visível apenas quando `paginas.size > 1`

---

### SearchSheet.kt — Modificação

**Mudança**: Adicionar `FavoritoIconButton` em cada item de resultado de busca.

**Layout do item de resultado** (após modificação):
```
┌───────────────────────────────────────────────┐
│  São Paulo, São Paulo, Brasil          [♥]    │
│  -23.55°, -46.63°                             │
└───────────────────────────────────────────────┘
```

**Comportamento adicional**:
- Tap no ícone de coração: adiciona/remove dos favoritos sem fechar a busca
- Se limite de 10 atingido e cidade não é favorita: ícone desabilitado (alpha 38%)
- Toast "Limite de 10 favoritos atingido. Remova uma cidade para adicionar outra." quando tentar além do limite

---

## Fluxo de Navegação: Pager

```
                    ┌─────────────────┐
                    │  HomeScreen     │
                    │  (com Pager)    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              ↓              ↓              ↓
       [Localização]   [Favorito 1]   [Favorito N]
       (sempre 1ª)    (ord. alfab.)  (ord. alfab.)
              │
              │  tap em ☰
              ↓
    ┌──────────────────────┐
    │  FavoritosBottomSheet │
    │  (lista de cidades)   │
    └──────────────────────┘
              │
              │  tap em cidade
              ↓
       scroll pager para
       página selecionada
```

---

## Acessibilidade (TalkBack)

| Componente                     | contentDescription                                             |
|--------------------------------|----------------------------------------------------------------|
| `PagerIndicator`               | `"Página X de Y"`                                              |
| `FavoritoIconButton` (ativo)   | `"Remover [cidade] dos favoritos"`                             |
| `FavoritoIconButton` (inativo) | `"Adicionar [cidade] aos favoritos"`                           |
| `ShareButton`                  | `"Compartilhar previsão de [cidade]"`                          |
| Item do `FavoritosBottomSheet` | `"[cidade], [estado], [país]. Navegar para esta cidade"`       |
| Swipe do pager                 | `semantics { paneTitle = "[cidade]" }` para anuncio automático |

---

## Design Tokens Novos (v1.1)

| Token                  | Valor              | Uso                                   |
|------------------------|--------------------|---------------------------------------|
| `colorFavorito`        | `colorError` (MD3) | Ícone de coração favoritado           |
| `dotSizeAtivo`         | 8dp                | Dot do pager na página atual          |
| `dotSizeInativo`       | 6dp                | Dots do pager nas outras páginas      |
| `dotSpacing`           | 6dp                | Espaçamento entre dots                |
| `bottomSheetMaxHeight` | 0.6f (60% tela)    | Altura máxima do FavoritosBottomSheet |
