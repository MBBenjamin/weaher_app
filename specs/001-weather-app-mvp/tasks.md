# Tasks: Weather App Android MVP v1.0

**Input**: Design documents de `/specs/001-weather-app-mvp/`

**Spec**: [spec.md](spec.md) | **Plano**: [plan.md](plan.md) | **Modelo**: [data-model.md](data-model.md) | **Contratos**: [contracts/](contracts/)

**Abordagem**: TDD — testes escritos e falhando ANTES da implementação em cada fase.

**Total**: 39 tarefas | **Prazo**: 5-6 semanas | **Story Points**: ~35-38 SP

**Issues resolvidas (acumulado 3 rodadas de /speckit-analyze)**: KDoc, Firebase Phase 1, Crashlytics, LeakCanary, Docs, HTTP 429, NetworkMonitor, AppResult, DayDetailSheet/LocationHandler split, Firebase Phase 5, interfaces `I`-prefixo, WMO drawables, T012 test 3, RateLimitBanner, strings.xml, google-services.json, @AddTrace, SharedPreferences, ViewModel co-location, AppResult naming | **3ª rodada**: C1 (spec RNF-05 coverage %), C2 (toast→Snackbar x6 em spec), H1 (CE-04 deep link T032), H2 (haptic feedback T018/T035/T005), M1 (CountDownTimer→Flow T019), M2 (Retry-After fallback 60s T003), M3 (VectorDrawable decision note T009), M4 (UnidadesDto T006), CE-05 (null values T006/T013)

---

## Formato: `[ID] [P?] [Story?] Descrição com caminho do arquivo`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependências não concluídas)
- **[Story]**: User Story associada (US1–US6)
- **🔴 TDD**: Tarefa de testes — DEVE falhar antes da implementação correspondente
- Caminho base código: `app/src/main/kotlin/com/weather/`
- Caminho base testes: `app/src/test/kotlin/com/weather/`

---

## Checklist de Progresso Geral

> Atualizar este checklist ao concluir cada tarefa.

| Fase | Tarefas | Concluídas | Status |
|------|---------|------------|--------|
| Phase 1 — Setup & Firebase | T001–T005 | 5/5 | ✅ |
| Phase 2 — Data Layer | T006–T014 | 9/9 | ✅ |
| Phase 3 — US1 Previsão Atual | T015–T020 | 6/6 | ✅ |
| Phase 4 — US2 Previsão Horária | T021–T024 | 4/4 | ✅ |
| Phase 5 — US3 Previsão 7 Dias | T025–T029 | 5/5 | ✅ |
| Phase 6 — US4 Localização Híbrida | T030–T032 | 3/3 | ✅ |
| Phase 7 — US5 Busca Manual | T033–T035 | 3/3 | ✅ |
| Phase 8 — US6 Offline & Sync | T036–T037 | 2/2 | ✅ |
| Phase 9 — Polish & Release | T038–T039 | 2/2 | ✅ |
| **TOTAL** | | **39/39** | ✅ |

---

## Phase 1: Setup & Firebase

**Objetivo**: Projeto compilando com Firebase ativo desde o início, tema correto, CI verde, documentação base criada.

**DoD da fase**: `./gradlew assembleDebug` → BUILD SUCCESSFUL; Firebase Crashlytics visível no console ✅

- [x] T001 Criar estrutura Android: `gradle/libs.versions.toml` com todas as dependências (Compose BOM 2024.02.00, Hilt 2.50, Room 2.6.1, Retrofit 2.9.0, OkHttp 4.12.0, Kotlinx Serialization 1.6.3, Vico 1.13.0, Coil 2.5.0, Timber 5.0.1, Play Services Location 21.0.1, Firebase BoM, Firebase Performance 20.5.0, Firebase Crashlytics 18.6.0, WorkManager 2.9.0, LeakCanary 2.12 como `debugImplementation`, JUnit4 4.13.2, Mockk 1.13.9, Espresso 3.5.1, Paparazzi 1.3.2); `app/build.gradle.kts` (plugins: android-application, kotlin-android, hilt, ksp, kotlin-serialization, google-services, firebase-crashlytics, paparazzi); `app/AndroidManifest.xml` (permissões: INTERNET, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_NETWORK_STATE); `app/src/main/res/values/strings.xml` (strings PT-BR: `app_name`, mensagens de erro: `error_no_internet`, `error_location_denied`, `error_generic`, `error_rate_limit`, labels de UI: `label_refresh`, `label_search_hint`, `label_try_again`, `label_search_another_city`, `label_loading`, `label_offline`, `label_location_approx`, textos de acessibilidade TalkBack para todos os elementos interativos, `data_credit` = "Dados: open-meteo.com" — todos os composables devem referenciar via `stringResource` em vez de strings hardcoded)
- [x] T002 Implementar `app/src/main/kotlin/com/weather/WeatherApplication.kt` (`@HiltAndroidApp`, `Timber.plant(DebugTree())` em DEBUG, `FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)`, `LeakCanary` habilitado automaticamente via debugImplementation) + `app/src/main/kotlin/com/weather/MainActivity.kt` (`@AndroidEntryPoint`, `setContent { WeatherTheme { HomeScreen() } }`) + criar `google-services.json` com estrutura JSON Firebase mínima válida (valores fictícios para dev: `package_name: "com.weather"`, `project_id: "weather-app-dev"`, `api_key` com string no formato UUID qualquer) e adicionar instrução no README: "Substituir `google-services.json` pelo arquivo real do Firebase Console antes do release"
- [x] T003 [P] Configurar Firebase Performance Monitoring: Firebase SDK auto-instrumenta cold start via trace `_app_start` (não requer código adicional); criar `app/src/main/kotlin/com/weather/data/remote/FirebaseMetricsInterceptor.kt` (OkHttp Interceptor que inicia `FirebasePerformance.getInstance().newHttpMetric(url, method)` para cada request, registra response code e payload size via `httpMetric.stop()` no bloco `finally`) + adicionar trace customizado `"weather_data_first_render"` em `HomeScreen.kt` (T018): iniciar trace ao entrar em `HomeUiState.Carregando`, parar ao transitar para `HomeUiState.Sucesso` pela primeira vez; criar `app/src/main/kotlin/com/weather/data/remote/RateLimitInterceptor.kt` (OkHttp Interceptor que captura HTTP 429 e lança `RateLimitException(retryAfterSeconds: Int)` — valor lido do header `Retry-After`; se header ausente ou inválido, usar **60s como padrão** conforme CE-03 da spec; resolve issue I2 de edge case §6)
- [x] T004 [P] Implementar Design System em `app/src/main/kotlin/com/weather/presentation/theme/`: `Color.kt` (WeatherColors: primary=#0288D1, surface=#FAFAFA, background=#F0F4F8, onSurface=#1A1A2E, tempMax=#EF5350, tempMin=#42A5F5, precip=#29B6F6@40%; fun gradientForWmoCode(code): List<Color> para ensolarado/nublado/chuva/tempestade) + `Typography.kt` (displayLarge 57sp Bold, headlineMedium 28sp SemiBold, titleLarge 22sp Medium, bodyLarge 16sp Regular, labelSmall 11sp Regular) + `Shape.kt` (card 16dp, mainCard 24dp, chip 12dp) + `Theme.kt` (WeatherTheme MaterialTheme wrapper). KDoc em todas as funções e objetos públicos.
- [x] T005 [P] Configurar quality gates e documentação: `detekt.yml` (maxCyclomaticComplexity=10, maxLineLength=120, excludes para gerados por KSP/Hilt) + `.github/workflows/ci.yml` (jobs: detekt lint, `./gradlew test`, `./gradlew assembleDebug`) + `README.md` (pré-requisitos Android Studio 2024.1+, SDK API 24-34, JDK 17, setup steps, build variants debug/release, comandos gradlew principais) + `CONTRIBUTING.md` (conventional commits: feat/fix/test/refactor/docs/chore, KDoc guide com exemplos, padrões Android, processo de code review) + `docs/STYLE_GUIDE.md` (nomenclatura completa: packages minúsculas, classes PascalCase, interfaces com prefixo `I` como `IPrevisaoRepository`, métodos camelCase, constantes UPPER_SNAKE_CASE, layout snake_case; **decisão de organização**: ViewModels são co-localizados com sua feature (`presentation/home/HomeViewModel.kt`) e não em package separado — projeto single-module de tamanho MVP não justifica a separação extra; sealed class de resultado customizado nomeada `AppResult<out T>` — não `Result<T>` — para evitar ambiguidade com `kotlin.Result` do stdlib; **feedback háptico obrigatório** em ações de confirmação: usar `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)` em pull-to-refresh e `HapticFeedbackType.TextHandleMove` em seleção de item — conforme Constitution III; **feedback transitório via Snackbar** (`SnackbarHostState`) — nunca `Toast` — conforme Constitution III)

**Checkpoint Phase 1**: App abre tela em branco. Firebase Crashlytics ativo. CI verde. Documentação base criada. Atualizar checklist.

---

## Phase 2: Foundational — Data Layer (Blocking)

**Objetivo**: Repositórios funcionando com cache, tratamento de erros e testes ≥70%.

**⚠️ CRÍTICO**: Nenhuma user story pode começar até esta fase estar completa.

**DoD da fase**: `./gradlew test` → todos os testes de repositório passando ✅

- [x] T006 [P] Implementar DTOs em `app/src/main/kotlin/com/weather/data/remote/dto/`: `PrevisaoResponseDto.kt` + `DadosAtuaisDto.kt` + `DadosHorariosDto.kt` + `DadosDiariosDto.kt` + `UnidadesDto.kt` (usado em `PrevisaoResponseDto` como `currentUnits`, `hourlyUnits`, `dailyUnits` — campos de unidade dos valores retornados pela API) — todos `@Serializable` com `@SerialName` mapeando snake_case da API para camelCase conforme data-model.md e `contracts/open-meteo-forecast.md`; `GeocodingResponseDto.kt` + `CidadeDto.kt` conforme `contracts/open-meteo-geocoding.md`; todos os campos anuláveis marcados com `?` (CE-05: campos críticos como `temperature2m`, `weatherCode` são nullable — tratamento de null no mapper T013)
- [x] T007 [P] Implementar interfaces Retrofit: `app/src/main/kotlin/com/weather/data/remote/OpenMeteoApi.kt` (suspend GET forecast com query params: latitude, longitude, current, hourly, daily, timezone="America/Sao_Paulo", forecast_days=7 — conforme spec §5.2) + `app/src/main/kotlin/com/weather/data/remote/GeocodingApi.kt` (suspend GET search com name, count=5, language="pt" — conforme spec §5.2)
- [x] T008 [P] Implementar persistência Room em `app/src/main/kotlin/com/weather/data/local/`: `entity/PrevisaoEntity.kt` (campos do data-model.md, `@Index(value=["timestamp_atualizado"])`) + `entity/HistoricoBuscaEntity.kt` + `dao/PrevisaoDao.kt` (`@Insert(onConflict=REPLACE)`, `@Query` para select por id, deleteWhereTimestampOlderThan(threshold: Long)) + `dao/HistoricoBuscaDao.kt` (upsert via insert+delete, deleteExcetoMaisRecentes(max=5), getAllOrderedByBuscadoEmDesc) + `AppDatabase.kt` (Room.databaseBuilder, versão 1, exportSchema=true)
- [x] T009 [P] Implementar utilitários de mapeamento em `app/src/main/kotlin/com/weather/utils/`: `WmoMapper.kt` (`when(code)` cobrindo todos os grupos WMO: 0 clear sky, 1-3 partly/cloudy/overcast, 45-48 fog, 51-57 drizzle, 61-67 rain, 71-77 snow, 80-82 showers, 85-86 snow showers, 95-99 thunderstorm → `@DrawableRes iconRes: Int` + `descricao: String` em PT-BR; valor default para código inválido) + `WindDirectionMapper.kt` (0-22°/338-360°→"N", 23-67°→"NE", 68-112°→"E", 113-157°→"SE", 158-202°→"S", 203-247°→"SO", 248-292°→"O", 293-337°→"NO") + criar ícones WMO VectorDrawable em `app/src/main/res/drawable/`: `ic_wmo_clear.xml` (sol), `ic_wmo_partly_cloudy.xml` (sol+nuvem), `ic_wmo_cloudy.xml` (nuvem), `ic_wmo_fog.xml` (névoa), `ic_wmo_drizzle.xml` (chuvisco), `ic_wmo_rain.xml` (chuva), `ic_wmo_snow.xml` (neve), `ic_wmo_showers.xml` (pancadas), `ic_wmo_thunderstorm.xml` (tempestade) — VectorDrawable 24dp com `android:tint="?attr/colorPrimary"`; `WmoMapper.iconeWMO()` referencia via `R.drawable.ic_wmo_*`; `WmoMapper.descricaoWMO()` retorna ID de string via `R.string.weather_*` para TalkBack correto; **decisão de design**: ícones WMO implementados como VectorDrawable (não emoji) — garante escalabilidade por densidade dp, tinting MD3 via `colorPrimary` e acessibilidade TalkBack via `contentDescription`; para código WMO null (CE-05), usar código **45** como fallback (fog — neutro e seguro). KDoc em todas as funções públicas.
- [x] T010 [P] Implementar utilitários de infraestrutura em `app/src/main/kotlin/com/weather/utils/`: `DateFormatter.kt` (formatarDiaSemana→"Sex, 17 mai", formatarHora→"14:00", formatarTimestampRelativo→"há X minutos/horas" com `Locale("pt","BR")`) + `CacheValidator.kt` (`estaValido(ts)`: age<1h; `estaExpirado(ts)`: 1h≤age<7d; `estaObsoleto(ts)`: age≥7d; `calcularHorasAtraso(ts): Int`) + `NetworkMonitor.kt` (`StateFlow<Boolean> isOnline` via `ConnectivityManager.registerNetworkCallback` + `callbackFlow`, iniciado em `@Singleton`) + `ResultWrapper.kt` (sealed class `AppResult<out T>`: `Success(data: T)`, `Error(message: String, exception: Throwable? = null)`, `Loading` — nomeada `AppResult` para evitar conflito com `kotlin.Result` do stdlib; resolve issue A1). KDoc em todas as classes e funções públicas.
- [x] T011 Implementar módulos DI em `app/src/main/kotlin/com/weather/di/`: `NetworkModule.kt` (`@Module @InstallIn(SingletonComponent)`, Retrofit para forecast `https://api.open-meteo.com/`, Retrofit para geocoding `https://geocoding-api.open-meteo.com/`, OkHttp com `HttpLoggingInterceptor` + `RateLimitInterceptor` + `FirebaseMetricsInterceptor`) + `DatabaseModule.kt` (Room AppDatabase `@Singleton`) + `RepositoryModule.kt` (`@Binds IPrevisaoRepository→PrevisaoRepositoryImpl`, `@Binds IBuscaRepository→BuscaRepositoryImpl`, `@Binds ILocationHandler→LocationHandlerImpl`)
- [x] T012 🔴 TDD — Escrever testes que DEVEM FALHAR em `app/src/test/kotlin/com/weather/repository/PrevisaoRepositoryTest.kt`: (1) `cache_valido_nao_requisita_api` (mockk DAO retorna cache com age<1h → API nunca chamada), (2) `cache_expirado_requisita_api_e_salva_no_room`, (3) `offline_retorna_Result_Success_com_previsao_de_cache`, (4) `sem_cache_e_offline_retorna_Result_Error`, (5) `forceRefresh_true_ignora_cache_e_chama_api`, (6) `api_retorna_RateLimitException_resulta_em_Result_Error_com_mensagem_rate_limit`. Verificar que TODOS FALHAM antes de prosseguir.
- [x] T013 Implementar: `app/src/main/kotlin/com/weather/domain/repository/IPrevisaoRepository.kt` (interface) + `app/src/main/kotlin/com/weather/data/mapper/PrevisaoMapper.kt` (funções puras: `PrevisaoResponseDto.toDomain()`, `PrevisaoEntity.toDomain()`, `Previsao.toEntity(nomeLocalidade)`; CE-05 null values no mapper: `temperature2m ?: 0f`, `weatherCode ?: 45` (fog como código neutro), `relativeHumidity2m ?: 0`, `windSpeed10m ?: 0f`; logar campos null via `Timber.w("Campo null: $campo")`) + `app/src/main/kotlin/com/weather/data/repository/PrevisaoRepositoryImpl.kt` (cache 1h via CacheValidator, mappers, NetworkMonitor para offline, captura RateLimitException → AppResult.Error). KDoc em todas as classes e funções públicas. Executar T012 → todos PASSAM. Atualizar checklist.
- [x] T014 🔴 TDD — Escrever testes que DEVEM FALHAR em `app/src/test/kotlin/com/weather/repository/BuscaRepositoryTest.kt`: (1) `geocoding_retorna_lista_CidadeSugestao_mapeada`, (2) `historico_salva_e_recupera_ate_5_entradas`, (3) `historico_nao_duplica_cidade_existente`, (4) `historico_move_cidade_existente_para_o_topo`. Verificar FALHAM. Então implementar: `app/src/main/kotlin/com/weather/domain/repository/IBuscaRepository.kt` (interface) + `app/src/main/kotlin/com/weather/data/repository/BuscaRepositoryImpl.kt` (geocoding via GeocodingApi, mapper `CidadeDto.toDomain()`, HistoricoBuscaDao com regra max-5). KDoc. Executar T014 → PASSAM. Atualizar checklist.

**Checkpoint Phase 2**: Repositórios retornam dados corretos. HTTP 429 tratado. Testes ≥70% passando. Atualizar checklist.

---

## Phase 3: US1 — Previsão Atual

**Goal**: Usuário abre app e vê temperatura, ícone WMO, umidade, vento e timestamp da sua localização em ≤2s.

**Independent Test**: Mock do repositório retorna `Previsao` → `HomeUiState.Sucesso` exibe todos os campos de `DadosAtuais` corretamente.

**DoD da fase**: `CurrentWeatherCard` renderiza dados reais em device/emulador ✅

### Testes TDD — US1 ⚠️ Escrever ANTES, verificar que FALHAM

- [x] T015 🔴 TDD — Escrever testes que DEVEM FALHAR em `app/src/test/kotlin/com/weather/home/HomeViewModelTest.kt`: (1) `obterPrevisao_sucesso_emite_HomeUiState_Sucesso_com_dadosAtuais_corretos`, (2) `obterPrevisao_erro_sem_cache_emite_HomeUiState_Erro`, (3) `obterPrevisao_offline_com_cache_emite_Sucesso_com_isOffline_true`, (4) `timestamp_relativo_atualiza_a_cada_minuto_sem_chamar_api`, (5) `pull_to_refresh_chama_api_ignorando_cache_1h`. Verificar que FALHAM.

### Implementação — US1

- [x] T016 [P] [US1] Implementar modelos de domínio em `app/src/main/kotlin/com/weather/domain/model/`: `DadosAtuais.kt` + `DadosHorarios.kt` + `HoraDados.kt` + `DadosDiarios.kt` + `DiaDados.kt` + `Previsao.kt` + `CidadeSugestao.kt` + `HistoricoBusca.kt` + `HomeUiState.kt` (sealed class: `Carregando`, `Sucesso(previsao, nomeLocalidade, isLocalizacaoAproximada, isOffline, timestampRelativo, horasAtraso)`, `Erro(mensagem, temCache, previsaoCache?)`, `SemPermissao`) + `SearchUiState.kt` (sealed class: `Idle`, `Carregando`, `Resultados(sugestoes, historico)`, `Erro(mensagem)`). KDoc em todas as classes.
- [x] T017 [US1] Implementar `app/src/main/kotlin/com/weather/domain/usecase/ObterPrevisaoUseCase.kt` (recebe lat: Double, lon: Double, forceRefresh: Boolean, delega para IPrevisaoRepository, retorna AppResult<Previsao>) + `app/src/main/kotlin/com/weather/presentation/home/HomeViewModel.kt` (`@HiltViewModel`, `StateFlow<HomeUiState>` iniciando em Carregando, `fun carregarPrevisao(lat, lon, forceRefresh=false)`, ticker de timestamp em `viewModelScope.launch { while(true) { delay(60_000); atualizarTimestamp() } }`, `fun onPullToRefresh()`). KDoc. Executar T015 → todos PASSAM. Atualizar checklist.
- [x] T018 [P] [US1] Implementar `app/src/main/kotlin/com/weather/presentation/home/HomeScreen.kt` (Scaffold MD3, `SwipeRefresh { ... }` chamando `homeViewModel.onPullToRefresh()` + `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)` ao confirmar o gesto de refresh, coleta `homeViewModel.uiState` com `collectAsStateWithLifecycle`, `when(uiState)` distribui para cada composable filho; ao observar `SemPermissaoDefinitiva` exibir botão "Abrir configurações" com Intent para app settings — ver T032) + `app/src/main/kotlin/com/weather/presentation/home/components/CurrentWeatherCard.kt` (Box com gradiente dinâmico `gradientForWmoCode(codigoWMO)`, ícone WMO ≥80dp, temperatura `displayLarge`, sensação térmica, umidade %, velocidade vento km/h + direção cardinal via WindDirectionMapper, timestamp relativo, `contentDescription` completo para TalkBack: "Temperatura: 24°C, Parcialmente Nublado, Sensação 22°C, Umidade 65%, Vento 15km/h Oeste"). Altura do card ~60% via `fillMaxHeight(0.6f)`. Snackbar via `SnackbarHostState` para erros transitórios (RF-07.6, CE-01) — substituir qualquer Toast por Snackbar.
- [x] T019 [US1] Implementar `app/src/main/kotlin/com/weather/presentation/home/components/LocationBadge.kt` (Surface com ícone de localização animado + texto "📍 Localização aproximada · refinando...", visível quando `isLocalizacaoAproximada=true`) + `app/src/main/kotlin/com/weather/presentation/home/components/OfflineBadge.kt` (Surface vermelha com ícone wifi_off + "🔴 OFFLINE · Dados de há X horas", visível quando `isOffline=true`) + `app/src/main/kotlin/com/weather/presentation/home/components/RateLimitBanner.kt` (Surface laranja com ícone `warning`, texto "⚠️ Limite de requisições · Tentando novamente em Xs" com countdown regressivo; adicionar `rateLimitSecondsRemaining: Int? = null` a `HomeUiState.Erro` definido em T016; no `HomeViewModel.kt` ao capturar `RateLimitException` iniciar countdown via `viewModelScope.launch { var r = retryAfterSeconds; while (r > 0) { _uiState.update { it.copy(rateLimitSecondsRemaining = r) }; delay(1000L); r-- }; _uiState.update { it.copy(rateLimitSecondsRemaining = null) }; carregarPrevisao() }` (coroutine, não CountDownTimer); desabilitar botão "Tentar novamente" enquanto `rateLimitSecondsRemaining != null` via `enabled = rateLimitSecondsRemaining == null`). `contentDescription` TalkBack em todos. Integrar condicionalmente no `HomeScreen.kt`. ⚠️ Decisão aplicada: RateLimitBanner fixo (padrão OfflineBadge) em vez de Snackbar efêmero, para que o countdown permaneça visível sem interação do usuário.
- [x] T020 [US1] Implementar `app/src/main/kotlin/com/weather/presentation/home/components/LoadingSkeleton.kt` (animação shimmer Compose para placeholder do CurrentWeatherCard, visível quando `HomeUiState.Carregando`) + `app/src/main/kotlin/com/weather/presentation/home/components/ErrorScreen.kt` (mensagem de erro amigável PT-BR, botão "Tentar novamente" → chama ViewModel, botão "Buscar outra cidade" → abre SearchSheet; `contentDescription` TalkBack em todos). Integrar nos estados corretos do `HomeScreen.kt`. Atualizar checklist.

**Checkpoint Phase 3**: Previsão atual exibida em ≤2s. TalkBack funciona. Shimmer visível no carregamento. Atualizar checklist.

---

## Phase 4: US2 — Previsão Horária (24h)

**Goal**: Usuário vê gráfico Vico de temperatura/precipitação hora-a-hora com scroll automático para "Agora" e bottom sheet de detalhe ao tocar.

**Independent Test**: Mockar `List<HoraDados>` com 24 entradas → `HourlyForecastSection` renderiza gráfico e cards sem crash.

**DoD da fase**: Gráfico Vico renderiza smooth, tap em hora abre `HourDetailSheet` em <300ms ✅

### Testes TDD — US2

- [x] T021 🔴 TDD — Adicionar testes em `app/src/test/kotlin/com/weather/home/HomeViewModelTest.kt` e criar `app/src/test/kotlin/com/weather/utils/WmoMapperTest.kt`: (1) `filtrar_horas_por_dataIso_hoje_retorna_exatamente_24_entradas`, (2) `filtrar_horas_dia_selecionado_retorna_24_entradas_do_dia_correto`, (3) `wmo_codigo_0_retorna_descricao_Ceu_Limpo`, (4) `wmo_codigo_95_retorna_descricao_Tempestade`, (5) `wmo_codigo_999_invalido_retorna_descricao_default`. Verificar que FALHAM.

### Implementação — US2

- [x] T022 [US2] Adicionar ao `HomeViewModel.kt`: `fun filtrarHorasDoDia(horas: List<HoraDados>, dataIso: String): List<HoraDados>` filtrando por `dataIso`; expor `horasDoDia: StateFlow<List<HoraDados>>` derivado de `homeUiState` filtrando pelo dia atual (`LocalDate.now(ZoneId.of(fusoHorario)).toString()`). Executar T021 → PASS.
- [x] T023 [US2] Implementar `app/src/main/kotlin/com/weather/presentation/home/components/HourlyForecastSection.kt` (TabRow MD3 com abas "Gráfico" e "Listagem"; aba Gráfico: Vico `CartesianChartView` com `rememberCartesianChart(LineLayer(color=tempMax), ColumnLayer(color=precip@40%))`, `VerticalLine` tracejada na hora atual via `rememberExtraLinesComponent`, scroll automático com `LazyListState.animateScrollToItem(indexAtual)`; aba Listagem: `LazyRow` horizontal com `HourCard` — hora, WMO ícone, temperatura, precipitação mm; `contentDescription` em cada `HourCard`). Integrar no `HomeScreen.kt`.
- [x] T024 [US2] Implementar `app/src/main/kotlin/com/weather/presentation/detail/HourDetailSheet.kt` (`ModalBottomSheet` MD3: drag handle, hora selecionada, ícone WMO 64dp, temperatura, umidade %, velocidade vento km/h, direção cardinal + graus, descrição WMO completa; fecha com swipe down ou tap fora; `contentDescription` em cada campo). Integrar: tap em `HourCard` chama `showHourDetail(horaDados)` que exibe o sheet. Atualizar checklist.

**Checkpoint Phase 4**: Gráfico e listagem horária funcionando. Bottom sheet em <300ms. Atualizar checklist.

---

## Phase 5: US3 — Previsão de 7 Dias

**Goal**: Usuário vê 7 cards diários com máx/mín e pode abrir modal com swipe entre dias e tabs "Horário"/"Índices".

**Independent Test**: Mockar `List<DiaDados>` (7 itens) → `WeeklyForecastList` exibe 7 cards; tap no primeiro abre `DayDetailSheet` com dados do dia correto.

**DoD da fase**: 7 cards renderizados, swipe entre dias e ambas as tabs funcionando ✅

### Testes TDD — US3

- [x] T025 🔴 TDD — Adicionar testes em `app/src/test/kotlin/com/weather/home/HomeViewModelTest.kt`: (1) `primeiro_dia_da_lista_tem_eHoje_true`, (2) `navegarDia_positivo_incrementa_diaSelecionadoIndex`, (3) `navegarDia_positivo_no_ultimo_dia_nao_avanca`, (4) `navegarDia_negativo_no_primeiro_dia_nao_recua`, (5) `abrirDia_filtra_horas_do_dia_correto`. Verificar que FALHAM.

### Implementação — US3

- [x] T026 [US3] Adicionar ao `HomeViewModel.kt`: `private val _diaSelecionadoIndex = MutableStateFlow(0)`, `fun abrirDia(index: Int) { _diaSelecionadoIndex.value = index }`, `fun navegarDia(delta: Int) { val novo = (_diaSelecionadoIndex.value + delta).coerceIn(0, 6); _diaSelecionadoIndex.value = novo }`, `val horasDoDiaSelecionado: StateFlow<List<HoraDados>>` derivado de `_diaSelecionadoIndex` + `homeUiState`. KDoc. Executar T025 → PASS.
- [x] T027 [US3] Implementar `app/src/main/kotlin/com/weather/presentation/home/components/WeeklyForecastList.kt` (`LazyColumn` com 7 `DayCard`s; cada card: dia semana + data PT-BR via `DateFormatter`, ícone WMO, descrição, temperatura máx em `colorTempMax`, temperatura mín em `colorTempMin`, probabilidade chuva %, vento máx km/h; primeiro card com `Badge("HOJE")` e cor de fundo distinta; tap chama `homeViewModel.abrirDia(index)`; `contentDescription` completo; full-width `fillMaxWidth()`). Integrar no `HomeScreen.kt`.
- [x] T028 [US3] Implementar estrutura do `app/src/main/kotlin/com/weather/presentation/detail/DayDetailSheet.kt` (`ModalBottomSheet` MD3: cabeçalho com data grande + ícone WMO 80dp + max/min; Row de navegação com IconButton "‹" e "›" (desabilitados nos limites 0 e 6); `HorizontalPager(pageCount=2)` com `HorizontalPagerIndicator`; `TabRow` com abas "Horário" e "Índices"; drag handle; `contentDescription` em botões de navegação). KDoc na classe.
- [x] T029 [US3] Implementar conteúdo das abas em `DayDetailSheet.kt`: aba "Horário" (`LazyColumn` com `HourCard` para cada `HoraDados` do `horasDoDiaSelecionado` — hora, ícone WMO, temperatura, precipitação mm); aba "Índices" (Surface cards: umidade máx de `DiaDados.umidadeMaxPercent`, vento máx de `DiaDados.velocidadeMaxVentoKmh`, direção dominante de `DiaDados.direcaoDominanteVentoGraus` + cardinal via WindDirectionMapper). Integrar sheet com tap em `WeeklyForecastList`. Atualizar checklist.

**Checkpoint Phase 5**: 7 dias renderizados. Swipe e ambas as tabs funcionando. Atualizar checklist.

---

## Phase 6: US4 — Localização Híbrida

**Goal**: App obtém localização Network em <500ms, exibe previsão em <2s e refina silenciosamente com GPS em background.

**Independent Test**: Mockar `FusedLocationProviderClient` retornando Network location → `HomeViewModel` emite `Sucesso` com `isLocalizacaoAproximada=true`. Mockar GPS chegando → badge desaparece.

**DoD da fase**: App abre e exibe dados em <2s no emulador, badge aparece/desaparece corretamente ✅

### Testes TDD — US4

- [x] T030 🔴 TDD — Criar `app/src/test/kotlin/com/weather/location/LocationHandlerTest.kt` com testes que DEVEM FALHAR: (1) `network_provider_emite_localizacao_com_isApproximate_true`, (2) `gps_refinement_timeout_30s_cancela_sem_emitir_erro_ao_usuario`, (3) `lastLocation_null_chama_getCurrentLocation_com_timeout_5s`, (4) `coordenadas_lat_fora_de_range_lanca_IllegalArgumentException`, (5) `permissao_negada_emite_LocationResult_PermissionDenied`. Verificar que FALHAM.

### Implementação — US4

- [x] T031 [US4] Implementar `app/src/main/kotlin/com/weather/domain/location/ILocationHandler.kt` (interface: `fun observarLocalizacao(): Flow<LocationResult>`; sealed class `LocationResult`: `Success(lat, lon, isApproximate)`, `GpsRefinement(lat, lon)`, `PermissionDenied`, `LocationFailed`) + `app/src/main/kotlin/com/weather/data/location/LocationHandlerImpl.kt` (emit Network coords via `lastLocation` se não-null; senão `getCurrentLocation(Priority.HIGH_ACCURACY)` com timeout 5s via `withTimeout`; em background coroutine GPS refinement com timeout 30s; validação: require(-90.0 ≤ lat ≤ 90.0 && -180.0 ≤ lon ≤ 180.0)) + `app/src/main/kotlin/com/weather/di/LocationModule.kt` (@Provides FusedLocationProviderClient). KDoc. Executar T030 → PASS.
- [x] T032 [US4] Integrar `ILocationHandler` no `HomeViewModel.kt`: no `init` lançar `viewModelScope.launch { locationHandler.observarLocalizacao().collect { result -> when(result) { is Success → carregarPrevisao(lat, lon) + setIsApproximate(true); is GpsRefinement → se delta>100m re-carregarPrevisao + setIsApproximate(false); is PermissionDenied → emit SemPermissao; is LocationFailed → emit Erro com mensagem PT-BR } } }`; calcular delta com distância Haversine simplificada; implementar CE-04: usar `SharedPreferences("location_prefs")` para armazenar `"denial_count"` (Int) — incrementar ao emitir `SemPermissao`, resetar ao conceder permissão; adicionar `SemPermissaoDefinitiva` a `HomeUiState.kt` (definido em T016) e emitir quando `denial_count ≥ 3`; no `HomeScreen.kt` (T018) ao observar `SemPermissaoDefinitiva`, exibir botão "Abrir configurações" que lança `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))`. Atualizar checklist.

**Checkpoint Phase 6**: Localização híbrida funciona. Badges aparecem/desaparecem sem flicker. Atualizar checklist.

---

## Phase 7: US5 — Busca Manual de Cidades

**Goal**: Usuário digita cidade, vê até 5 sugestões (debounce 500ms) e seleciona para atualizar a previsão. Histórico persiste entre restarts.

**Independent Test**: Mockar `IBuscaRepository.buscarCidades("São Paulo")` → `SearchViewModel` emite `SearchUiState.Resultados` com lista; selecionar item notifica `HomeViewModel`.

**DoD da fase**: Busca retorna resultados em <2s, histórico persiste entre restarts ✅

### Testes TDD — US5

- [x] T033 🔴 TDD — Criar `app/src/test/kotlin/com/weather/search/SearchViewModelTest.kt` com testes que DEVEM FALHAR: (1) `debounce_500ms_dispara_exatamente_uma_requisicao_ao_digitar_rapido`, (2) `busca_valida_emite_SearchUiState_Resultados_com_lista`, (3) `selecionar_cidade_adiciona_ao_historico_e_emite_SharedFlow`, (4) `historico_exibe_apenas_as_5_mais_recentes`, (5) `cidade_duplicada_no_historico_move_para_o_topo`. Verificar que FALHAM.

### Implementação — US5

- [x] T034 [US5] Implementar `app/src/main/kotlin/com/weather/domain/usecase/BuscarCidadesUseCase.kt` (recebe query: String, retorna `AppResult<List<CidadeSugestao>>` via IBuscaRepository) + `app/src/main/kotlin/com/weather/presentation/search/SearchViewModel.kt` (`@HiltViewModel`, `StateFlow<SearchUiState>`, `private val _query = MutableStateFlow("")` com `.debounce(500L).filter { it.length >= 2 }.flatMapLatest { buscarCidadesUseCase(it) }` coletado em `viewModelScope`, `SharedFlow<CidadeSelecionada> cidadeSelecionadaEvent` para notificar `HomeViewModel`, `fun onQueryChange(query)`, `fun selecionarCidade(cidade)`, `fun carregarHistorico()`). KDoc. Executar T033 → PASS.
- [x] T035 [US5] Implementar `app/src/main/kotlin/com/weather/presentation/search/SearchSheet.kt` (`ModalBottomSheet` MD3: `SearchBar` sempre visível com placeholder "Buscar cidade...", ao ganhar foco exibe `LazyColumn` com até 5 `HistoryItem`s (ícone relógio + nome); ao digitar exibe `LazyColumn` com `ResultItem`s (nome Bold + estado + país); tap em item chama `searchViewModel.selecionarCidade()` + `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.TextHandleMove)` para confirmar seleção, fecha teclado, fecha sheet; `contentDescription` TalkBack em todos os elementos interativos). Observar `cidadeSelecionadaEvent` no `HomeScreen.kt` para atualizar a previsão. Atualizar checklist.

**Checkpoint Phase 7**: Busca e histórico funcionando. Debounce correto (1 requisição ao digitar rápido). Atualizar checklist.

---

## Phase 8: US6 — Offline & Sincronização

**Goal**: App exibe cache offline com badge, auto-reconecta quando online e limpa cache >7 dias via WorkManager.

**Independent Test**: Mockar `NetworkMonitor.isOnline = false` com cache → `HomeUiState.Sucesso.isOffline = true`. Mockar reconexão → dados frescos, badge desaparece.

**DoD da fase**: Fluxo offline completo testado, WorkManager agendado em device ✅

### Testes TDD — US6

- [x] T036 🔴 TDD — Adicionar testes em `app/src/test/kotlin/com/weather/repository/PrevisaoRepositoryTest.kt` e `app/src/test/kotlin/com/weather/home/HomeViewModelTest.kt`: (1) `quando_reconecta_e_cache_expirado_viewmodel_dispara_sync_automatico`, (2) `pull_to_refresh_offline_emite_Result_Error_sem_crash`, (3) `cache_expirado_e_offline_emite_Sucesso_com_isOffline_true_e_horasAtraso_calculado`, (4) `LimpezaCacheWorker_deleta_apenas_registros_com_timestamp_mais_antigo_que_7_dias`. Verificar que FALHAM.

### Implementação — US6

- [x] T037 [US6] Implementar `app/src/main/kotlin/com/weather/data/worker/LimpezaCacheWorker.kt` (`@HiltWorker`, `doWork()` chama `previsaoDao.deleteWhereTimestampOlderThan(now - 7.days.inWholeMilliseconds)`, logs via Timber, retorna `Result.success()`); registrar em `WeatherApplication.kt` com `WorkManager.getInstance(this).enqueueUniquePeriodicWork("cache_cleanup", ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequestBuilder<LimpezaCacheWorker>(7, TimeUnit.DAYS).build())`; integrar auto-reconnect no `HomeViewModel.kt` (`viewModelScope.launch { networkMonitor.isOnline.drop(1).filter { it }.collect { if (cacheExpirado) carregarPrevisao(forceRefresh=false) } }`). KDoc. Executar T036 → PASS. Atualizar checklist.

**Checkpoint Phase 8**: Offline completo. WorkManager agendado. Auto-reconexão funciona. Atualizar checklist.

---

## Phase 9: Polish & Release

**Objetivo**: Cobertura ≥80% ViewModels, APK ≤15MB, crash rate 0, CI 100% verde.

**DoD da fase**: `./gradlew bundleRelease` → APK ≤15MB; Firebase mostra startup <2s; CI verde ✅

- [x] T038 Executar suite de testes automatizados e acessibilidade: (1) `./gradlew test jacocoTestReport` → verificar coverage ≥80% HomeViewModel+SearchViewModel, ≥70% Repositories, ≥60% Composables; (2) criar `app/src/androidTest/kotlin/com/weather/HomeScreenTest.kt` (Espresso: pull-to-refresh exibe spinner → dados atualizam, tap em DayCard abre DayDetailSheet, campo busca ao focar exibe histórico); (3) criar `app/src/androidTest/kotlin/com/weather/SearchTest.kt` (Espresso: digitar "São Paulo" aguarda 600ms debounce → resultados aparecem, tap em resultado fecha sheet e atualiza previsão); (4) criar `app/src/test/kotlin/com/weather/screenshots/HomeScreenScreenshots.kt` (Paparazzi: `portrait_light_theme()`, `portrait_offline_state()`); (5) executar TalkBack manual nos 2 devices — registrar resultado ("✅ TalkBack validado em [device] API [X]") em `CONTRIBUTING.md`
- [x] T039 Release e performance: (1) adicionar ProGuard/R8 rules em `app/proguard-rules.pro` (`-keep` para classes `@Serializable`, `@Entity`, geradas pelo Hilt, Firebase SDK); (2) `./gradlew bundleRelease` → confirmar APK ≤15MB; (3) Android Profiler: cold start ≤2s, scroll em HourlyForecastSection ≥50fps, memory idle ≤50MB; (4) testar em 2 devices: Motorola G13 (API 31, baixo custo) + Pixel 8 (API 34, alto padrão); (5) verificar LeakCanary zero leaks no build debug; (6) forçar crash não-fatal via `FirebaseCrashlytics.getInstance().recordException(RuntimeException("teste"))` → confirmar evento no Firebase console; (7) adicionar `Text("Dados: open-meteo.com")` estilizado com `labelSmall` no footer do `HomeScreen.kt`; (8) criar `CHANGELOG.md` com release notes da v1.0 (features, dependências, performance targets atingidos). Atualizar checklist final.

**Checkpoint Phase 9**: Todos os critérios de aceitação validados. DoD completo. App pronto para release. ✅

---

## Dependencies & Execution Order

### Dependências entre Fases

- **Phase 1 (Setup)**: Nenhuma — começa imediatamente
- **Phase 2 (Data Layer)**: Depende de Phase 1 — **BLOQUEIA todas as user stories**
- **Phase 3–8 (User Stories)**: Todas dependem de Phase 2; rodam sequencialmente por prioridade
- **Phase 9 (Polish)**: Depende de todas as fases anteriores

### Dependências entre User Stories

- **US1 (Phase 3)**: Pode iniciar após Phase 2 — nenhuma dependência
- **US2 (Phase 4)**: Depende de US1 (reutiliza `HomeViewModel` + `HomeScreen`)
- **US3 (Phase 5)**: Depende de US1 (reutiliza `HomeViewModel` + `HomeScreen`)
- **US4 (Phase 6)**: Depende de US1 (integra `LocationHandler` no `HomeViewModel`)
- **US5 (Phase 7)**: Depende de US1 (notifica `HomeViewModel` via `SharedFlow`)
- **US6 (Phase 8)**: Depende de US1 e Phase 2 (estende repositório e ViewModel)

### Regra TDD dentro de cada fase

1. Tarefa 🔴 TDD: escrever testes → verificar que **FALHAM** (se passarem, o teste está errado)
2. Tarefa de implementação: codificar até os testes **PASSAREM**
3. Confirmar cobertura com `./gradlew test`
4. Atualizar checklist de progresso geral

### Oportunidades de Paralelismo

- **Phase 1**: T003, T004 e T005 podem rodar em paralelo após T001+T002
- **Phase 2**: T006, T007, T008, T009 e T010 podem rodar em paralelo (arquivos distintos)
- **Phase 3**: T016 e T018 podem rodar em paralelo após T015 estar escrito

---

## Parallel Example: Phase 2

```
# Após T001 e T002, iniciar em paralelo:
T006: DTOs Retrofit                  (data/remote/dto/)
T007: Interfaces OpenMeteoApi        (data/remote/)
T008: Room entities + DAOs           (data/local/)
T009: WmoMapper + WindDirectionMapper (utils/)
T010: DateFormatter + CacheValidator + NetworkMonitor + ResultWrapper (utils/)

# Todos independentes — arquivos completamente distintos
# T011 aguarda T006-T010 para configurar DI com as implementações corretas
```

---

## Implementation Strategy

### MVP First (US1 — Previsão Atual)

1. Completar Phase 1: Setup (Firebase ativo desde o início)
2. Completar Phase 2: Data Layer (CRÍTICO — bloqueia tudo)
3. Completar Phase 3: US1 Previsão Atual
4. **PARAR E VALIDAR**: App abre, exibe temperatura e condições em ≤2s
5. Deploy para teste interno

### Incremental Delivery

| Fase | Entrega | Valor |
|------|---------|-------|
| Phase 1+2 | Foundation | Infraestrutura testável |
| Phase 3 | **MVP ✅** | App funcional mínimo |
| Phase 4 | Previsão Horária | Gráfico Vico + detalhe por hora |
| Phase 5 | Previsão 7 Dias | Visão semanal completa |
| Phase 6 | Localização | UX automática sem digitação |
| Phase 7 | Busca Manual | Fallback e multi-cidade |
| Phase 8 | Offline | Resiliência offline-first |
| Phase 9 | Release | App pronto para Play Store |

---

## Notes

- **KDoc obrigatório** em todas as classes e funções públicas — verificar com Detekt
- **Conventional commits**: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:` — ver CONTRIBUTING.md
- **LeakCanary**: habilitado automaticamente via `debugImplementation` — verificar console em dev
- **Firebase Crashlytics**: ativo desde Phase 1 — qualquer crash em teste aparece no console
- Sempre executar `./gradlew test` após implementar cada tarefa TDD antes de prosseguir
- Atualizar a tabela "Checklist de Progresso Geral" ao concluir cada tarefa
- `[P]` = arquivos diferentes, sem dependências — podem rodar em paralelo
- Commit por tarefa ou grupo lógico (conventional commit)
- Parar em qualquer checkpoint para validar a story independentemente
