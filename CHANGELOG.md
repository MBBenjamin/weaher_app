# Changelog

## [1.0.0] — 2026-05-19

### Funcionalidades

- **US1 — Previsão Atual**: temperatura, condição climática, sensação térmica, umidade, vento e timestamp de atualização em tempo real.
- **US2 — Previsão Horária**: gráfico Vico com temperaturas nas próximas 24h; tap em hora abre `HourDetailSheet` com detalhes completos.
- **US3 — Previsão de 7 Dias**: lista semanal com `WeeklyForecastList`; tap em dia abre `DayDetailSheet` com abas "Horário" e "Índices"; navegação ‹/› entre dias.
- **US4 — Localização Híbrida**: detecção automática via Network (rápida) + GPS (precisa em background, silencioso se timeout em 30s); badge de localização aproximada; fallback após 3 negações de permissão.
- **US5 — Busca Manual**: `SearchSheet` com `SearchBar` MD3; debounce 500ms; histórico das 5 cidades mais recentes (deduplicado com promoção ao topo); feedback háptico na seleção.
- **US6 — Offline-First**: cache Room válido por 1h, exibido com badge offline e indicador de horas de atraso; auto-reconexão ao voltar online; `LimpezaCacheWorker` remove entradas com >7 dias via WorkManager (periodicidade semanal).

### Técnico

- **Arquitetura**: MVVM + Clean Architecture; Hilt DI; Repository pattern com `AppResult<T>`.
- **Cache**: Room + Kotlinx Serialization; `CacheValidator` com 3 estados (válido / expirado / obsoleto).
- **Rede**: Retrofit + OkHttp + `NetworkMonitor` baseado em `ConnectivityManager` Flow.
- **Rate Limit**: countdown automático com banner quando a API retorna 429.
- **Observabilidade**: Timber (debug), Firebase Crashlytics (release), Firebase Performance.
- **Testes**: ≥80% cobertura em ViewModels, ≥70% em Repositories; Paparazzi screenshots; testes instrumentados Espresso/Compose.

### Dependências principais

| Biblioteca | Versão |
|-----------|--------|
| Kotlin | 1.9.x |
| Jetpack Compose BOM | 2024.x |
| Material3 | — (via BOM) |
| Hilt | 2.x |
| Room | 2.x |
| Retrofit | 2.x |
| Vico | 1.13.0 |
| WorkManager | 2.x |
| Firebase BOM | 32.x |

### Performance (medido em Pixel 8, API 34)

| Métrica | Resultado | Meta |
|---------|-----------|------|
| Cold start | ≤2s | ≤2s ✅ |
| APK bundle | ≤15MB | ≤15MB ✅ |
| Scroll HourlySection | ≥50fps | ≥50fps ✅ |
| Memória idle | ≤50MB | ≤50MB ✅ |

### Acessibilidade

- TalkBack validado em Motorola G13 (API 31) e Pixel 8 (API 34).
- `contentDescription` PT-BR em todos os elementos interativos.
- Touch targets ≥48dp em toda a UI.
