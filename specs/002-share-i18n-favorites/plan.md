   # Implementation Plan: Weather App v1.1 — Compartilhamento, i18n e Favoritos

**Branch**: `002-share-i18n-favorites` | **Data**: 2026-05-27 | **Spec**: [spec.md](spec.md)

---

## Summary

Expandir o app v1.0 com três features complementares que aumentam retenção e alcance: (1) **Favoritos** — pager horizontal com até 10 cidades, cache compartilhado com v1.0, navegação via swipe e bottom sheet; (2) **Compartilhamento** — share intent nativo com texto formatado no idioma do app; (3) **i18n PT-BR/EN-US** — localização automática por locale do dispositivo, incluindo migração das descrições WMO hardcoded para string resources. Nenhuma nova dependência externa necessária.

---

## Technical Context

**Language/Version**: Kotlin 1.9.x (100%) | Android Studio Ladybug 2024.1+ — igual v1.0

**Primary Dependencies** (sem adições novas):
- `compose-foundation` (BOM existente) — `HorizontalPager` + `PagerState`
- Room 2.6.1 — migration 1→2 para nova tabela `favoritos`
- Hilt 2.50 — novo módulo para `FavoritosRepository`
- Restante da stack da v1.0 inalterado

**Storage**: Room Database versão 2 — nova tabela `favoritos`; `PrevisaoEntity` e `HistoricoBuscaEntity` sem alteração. Sem `fallbackToDestructiveMigration` (preservar cache do usuário na atualização).

**Testing**: JUnit4 + Mockk (unitários) | Espresso (instrumentados) | Paparazzi (screenshots) — mesma suite da v1.0

**Target Platform**: Android 8.0+ (API 24) → Target API 34 — sem alteração

**Project Type**: Mobile app Android nativo — sem alteração

**Performance Goals**:
- Cold start ≤ 2s (mesmo com até 10 favoritos no pager)
- Swipe entre páginas ≥ 60fps (HorizontalPager com `beyondBoundsPageCount = 1`)
- Memória: ≤ 50MB mesmo com 10 favoritos carregados (cache sob demanda)
- APK: permanece ≤ 15MB (zero novas dependências)

**Constraints**: Offline-capable | Portrait | PT-BR (padrão) + EN-US | WCAG AA | TalkBack

**Scale/Scope**: 1 usuário/dispositivo | máximo 10 cidades favoritas | cache compartilhado

---

## Constitution Check

*GATE: Verificado pré-Phase 1. Re-verificado pós-Phase 2.*

| Princípio | Status | Observação |
|-----------|--------|------------|
| I. Kotlin 100% | ✅ PASSA | Nenhum Java novo |
| I. MVVM + Hilt + Coroutines | ✅ PASSA | `FavoritaViewModel` + `FavoritosRepositoryImpl` via Hilt |
| I. Detekt 0 erros | ✅ PASSA | Configuração existente aplica-se ao código novo |
| II. TDD obrigatório | ✅ PASSA | ≥80% ViewModel, ≥70% Repository (mesmos thresholds) |
| II. Testes unitários + instrumentados | ✅ PASSA | JUnit4 + Mockk + Espresso |
| III. Mobile-First | ✅ PASSA | HorizontalPager nativo, touch targets ≥48dp |
| III. Material Design 3 | ✅ PASSA | `BottomSheetScaffold`, `IconButton` MD3 |
| III. TalkBack | ✅ PASSA | `contentDescription` obrigatório em todos os novos elementos |
| IV. APK ≤ 15MB | ✅ PASSA | Sem novas dependências externas |
| IV. Cold start ≤ 2s | ✅ PASSA | Pager usa lazy loading; página 0 carrega primeiro |
| IV. Sem memory leaks | ✅ PASSA | `Flow` com `collectAsStateWithLifecycle`, LeakCanary em dev |
| V. Estrutura de projeto padrão | ✅ PASSA | Novos arquivos seguem estrutura por camada da v1.0 |
| V. StateFlow + Navigation Compose | ✅ PASSA | `PagerState` complementa (não substitui) StateFlow |
| V. Timber (sem Log.d) | ✅ PASSA | Logging via Timber em todos os novos componentes |

**Violações**: Nenhuma. ✅ Constitution-compliant.

---

## Project Structure

### Documentação (esta feature)

```text
specs/002-share-i18n-favorites/
├── plan.md              ← Este arquivo
├── research.md          ← Decisões técnicas (Phase 0)
├── data-model.md        ← Modelo de dados v1.1 (Phase 1)
├── quickstart.md        ← Guia de desenvolvimento (Phase 1)
├── contracts/
│   └── ui-contracts.md  ← Contratos de UI novos e modificados
├── checklists/
│   └── requirements.md
└── tasks.md             ← Gerado por /speckit-tasks
```

### Source Code (modificações e adições)

```text
app/src/main/kotlin/com/weather/

  [NOVOS]
  presentation/home/
  ├── WeatherPager.kt                    ← pager horizontal raiz
  └── components/
      ├── PagerIndicator.kt              ← dots de página
      ├── FavoritosBottomSheet.kt        ← navegação rápida
      └── ShareButton.kt                 ← botão de compartilhar

  presentation/search/components/
  └── FavoritoIconButton.kt              ← ♥ nos resultados de busca

  domain/model/
  ├── CidadeFavorita.kt                  ← modelo de domínio
  └── PaginaCidade.kt                    ← sealed class (LocalizacaoAtual | Favorita)

  domain/repository/
  └── IFavoritosRepository.kt            ← interface

  domain/usecase/
  ├── GerenciarFavoritosUseCase.kt       ← add/remove/list/check
  └── CompartilharPrevisaoUseCase.kt     ← gera TextoCompartilhamento

  data/local/entity/
  └── FavoritaCidadeEntity.kt            ← Room entity

  data/local/dao/
  └── FavoritaCidadeDao.kt               ← queries + Flow

  data/repository/
  └── FavoritosRepositoryImpl.kt

  utils/
  └── CompartilhamentoBuilder.kt         ← puro, testável sem Android

  [MODIFICADOS]
  presentation/home/HomeScreen.kt        ← wrapper pager + novos TopAppBar actions
  presentation/home/HomeViewModel.kt     ← pager state + favoritos state
  presentation/search/SearchSheet.kt     ← FavoritoIconButton nos resultados
  data/local/AppDatabase.kt             ← versão 2 + MIGRATION_1_2
  utils/WmoMapper.kt                     ← usar Context.getString(R.string.wmo_XX)

  [NOVOS — resources]
  res/values/strings.xml                 ← todas as strings PT-BR (inclui WMO)
  res/values-en/strings.xml              ← todas as strings EN-US (inclui WMO)
```

**Structure Decision**: Single-module Android (sem mudança). Novos arquivos respeitam a estrutura por camada da v1.0 (presentation / domain / data / utils).

---

## Complexity Tracking

Nenhuma violação de Constitution detectada. Sem justificativas necessárias.

---

## Fases de Implementação

### Phase 1: i18n Infrastructure — Semana 1 (3 SP)

**Motivação**: i18n é transversal — afeta todos os textos do app incluindo WMO descriptions. Fazer primeiro evita retrabalho nas fases seguintes.

1. Criar `res/values/strings.xml` — migrar todas as strings PT-BR hardcoded do código existente
2. Criar `res/values-en/strings.xml` — tradução EN-US de todas as strings PT-BR
3. Atualizar `WmoMapper.kt` — substituir `when(code) { 0 -> "Céu Limpo" ... }` por `context.getString(R.string.wmo_0)` etc. (parametrizar `Context` ou `Resources`)
4. Atualizar `DateFormatter.kt` — usar `Locale.getDefault()` para formato de data/hora (24h PT-BR, 12h EN-US)
5. Testes unitários: `I18nTest` — verificar que cada locale retorna string correta

**DoD**: Mudar dispositivo para EN-US → 100% da UI exibida em inglês (incluindo WMO descriptions e formatos de data/hora)

---

### Phase 2: Favorites Data Layer — Semana 1-2 (4 SP)

**Motivação**: Dados antes de UI — garante que `FavoritaViewModel` tem tudo que precisa antes de criar composables.

1. `FavoritaCidadeEntity.kt` + `FavoritaCidadeDao.kt`
2. `MIGRATION_1_2` em `AppDatabase.kt` (versão 1 → 2)
3. `IFavoritosRepository.kt` (interface)
4. `FavoritosRepositoryImpl.kt`:
   - `observarFavoritos(): Flow<List<CidadeFavorita>>`
   - `adicionarFavorito(cidade): Result<Unit>` (verifica limite 10, deduplicação)
   - `removerFavorito(id): Result<Unit>`
   - `ehFavorito(id): Boolean`
5. `GerenciarFavoritosUseCase.kt`
6. Hilt module: `FavoritosModule.kt`
7. Testes: `FavoritosRepositoryTest` ≥ 70% cobertura

**DoD**: Testes passando; Room migration verificada via `MigrationTest`

---

### Phase 3: Favorites UI — Semana 2-3 (5 SP)

1. `FavoritaViewModel.kt` com `StateFlow<FavoritasUiState>` + `pagerState`
2. `PaginaCidade.kt` sealed class
3. `WeatherPager.kt` — `HorizontalPager` com `beyondBoundsPageCount = 1`
4. `PagerIndicator.kt` — dots animados
5. `FavoritosBottomSheet.kt` — lista de cidades
6. `FavoritoIconButton.kt` — ícone ♥ nos resultados de busca
7. Modificar `HomeScreen.kt` — integrar WeatherPager + novos actions no TopAppBar
8. Modificar `SearchSheet.kt` — adicionar FavoritoIconButton por resultado
9. Testes unitários: `FavoritaViewModelTest` ≥ 80%
10. Testes instrumentados: fluxo add/remove/navigate favoritos

**DoD**: Adicionar cidade como favorito → pager exibe nova página; swipe funciona; limit 10 bloqueado

---

### Phase 4: Sharing — Semana 3 (2 SP)

1. `CompartilhamentoBuilder.kt` — função pura `buildText(previsao, strings): String`
   - Texto inclui: cidade, temp, descrição WMO, sensação, umidade, vento, próximos 3 dias, "Via Weather App"
   - Truncate nome se > 30 chars
2. `CompartilharPrevisaoUseCase.kt` — orquestra builder + share intent
3. `ShareButton.kt` — composable com `Icons.Default.Share`
4. Integrar `ShareButton` no TopAppBar de `HomeScreen.kt`
5. Testes unitários: `CompartilhamentoBuilderTest` — verificar PT-BR e EN-US

**DoD**: Tocar em share → chooser abre com texto correto no idioma atual; funciona offline

---

### Phase 5: Polish & Testing — Semana 4 (2 SP)

1. Screenshot tests Paparazzi:
   - `WeatherPager` com 3 páginas (light + dark placeholder para v1.2)
   - `FavoritosBottomSheet` com 5 favoritos
   - `SearchSheet` com FavoritoIconButton ativo/inativo
2. TalkBack: testar todos os novos elementos com `contentDescription`
3. Performance: verificar que pager com 10 favoritos mantém ≥ 60fps e ≤ 50MB
4. Device testing em 2 devices (API 24 + API 34)
5. Verificar APK size: `./gradlew bundleRelease` → ≤ 15MB
6. Detekt + Android Lint: 0 erros, 0 avisos

**DoD**: CI verde; TalkBack validado; APK size ≤ 15MB; crash rate 0 em testes

---

## Artefatos Gerados

| Artefato | Arquivo | Status |
|---------|---------|--------|
| Este plano | [plan.md](plan.md) | ✅ |
| Pesquisa técnica | [research.md](research.md) | ✅ |
| Modelo de dados | [data-model.md](data-model.md) | ✅ |
| Contratos UI | [contracts/ui-contracts.md](contracts/ui-contracts.md) | ✅ |
| Quickstart dev | [quickstart.md](quickstart.md) | ✅ |
| Tasks breakdown | tasks.md | ⏳ `/speckit-tasks` |
