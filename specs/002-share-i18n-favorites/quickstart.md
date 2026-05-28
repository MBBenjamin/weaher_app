# Quickstart: Weather App v1.1 — Dev Guide

**Branch**: `002-share-i18n-favorites` | **Data**: 2026-05-27

> Pré-requisito: v1.0 já compilando e rodando. Este guia cobre apenas o delta da v1.1.

---

## Setup

```bash
git checkout 002-share-i18n-favorites
git pull origin 002-share-i18n-favorites
```

Abrir no Android Studio → Sync Gradle (nenhuma nova dependência externa necessária).

---

## Testar i18n (EN-US)

**No emulador/dispositivo:**

1. Settings → System → Language & Input → Language → Add language → English (United States)
2. Mover "English (United States)" para o topo da lista
3. Reabrir o app → toda UI deve estar em inglês

**Validar:**
- Labels: "Humidity", "Wind", "Feels like"
- Horas: "2:00 PM" (não "14:00")
- Datas: "Sat, May 17" (não "Sáb, 17 de Mai")
- Descrições WMO: "Partly Cloudy" (não "Parcialmente Nublado")
- Mensagem offline: "No connection. Using cached data."

**Reverter para PT-BR:** Mover "Português (Brasil)" de volta ao topo.

---

## Testar Favoritos

### Adicionar favorito

1. Tocar no ícone de busca (🔍)
2. Digitar "Rio de Janeiro"
3. Aguardar resultados (500ms debounce)
4. Tocar no ícone de coração ♡ ao lado de "Rio de Janeiro, RJ, Brasil"
5. Toast "Adicionada aos favoritos" deve aparecer
6. O ícone muda para ♥ (cheio)

### Navegar via pager

1. Com pelo menos 1 favorito adicionado:
2. Na tela principal, dots aparecem no topo
3. Swipe para a direita → primeira cidade favorita (ordem alfabética)
4. Swipe para a esquerda → volta à localização atual

### Navegação direta via bottom sheet

1. Tocar no ícone ☰ (lista de cidades) no TopAppBar
2. FavoritosBottomSheet abre com todas as cidades
3. Tocar em qualquer cidade → pager salta diretamente para ela

### Remover favorito

1. Navegar para uma cidade favorita no pager
2. O ícone ♥ no TopAppBar está ativo (cheio)
3. Tocar nele → confirmação de remoção → cidade removida
4. O pager retorna para a localização atual automaticamente

### Testar limite

1. Adicionar 10 cidades favoritas (através da busca)
2. Tentar adicionar uma 11ª → mensagem "Limite de 10 favoritos atingido"
3. O ícone ♡ de cidades não favoritadas fica desabilitado (alpha 38%)

---

## Testar Compartilhamento

1. Tocar no ícone de compartilhar 🔗 no TopAppBar (qualquer cidade)
2. Chooser do sistema Android abre com o texto pré-formatado
3. Verificar formato PT-BR:
   ```
   🌤️ São Paulo, SP
   Agora: 24°C — Parcialmente Nublado
   Sensação: 22°C | Umidade: 65% | Vento: 15 km/h
   📅 Sáb: ☀️ 27°/18° | Dom: 🌧️ 23°/17° | Seg: ⛅ 25°/19°
   
   Via Weather App
   ```
4. Com EN-US ativo, verificar texto em inglês (mesma estrutura)
5. Pressionar "Voltar" → app retorna ao mesmo estado

**Testar offline:**
1. Ativar modo avião no dispositivo
2. Compartilhamento ainda deve funcionar (usa dados em cache)

---

## Verificar Room Migration

Ao atualizar de um build v1.0 para v1.1:

```bash
# Via ADB — verificar que as 3 tabelas existem
adb shell
run-as com.weather
sqlite3 databases/weather_database

.tables
# Deve mostrar: favoritos  historico_busca  previsoes
.schema favoritos
# Deve mostrar o CREATE TABLE correto
.quit
exit
```

Se a migração falhou, a app crasha ao abrir com `IllegalStateException: Room cannot verify the data integrity`. Verificar `MIGRATION_1_2` em `AppDatabase.kt`.

---

## Estrutura de Arquivos Novos (v1.1)

```
app/src/main/
├── kotlin/com/weather/
│   ├── presentation/home/
│   │   ├── WeatherPager.kt              ← NOVO
│   │   └── components/
│   │       ├── PagerIndicator.kt        ← NOVO
│   │       ├── FavoritosBottomSheet.kt  ← NOVO
│   │       └── ShareButton.kt           ← NOVO
│   ├── presentation/search/components/
│   │   └── FavoritoIconButton.kt        ← NOVO
│   ├── domain/model/
│   │   ├── CidadeFavorita.kt            ← NOVO
│   │   └── PaginaCidade.kt              ← NOVO
│   ├── domain/repository/
│   │   └── IFavoritosRepository.kt      ← NOVO
│   ├── domain/usecase/
│   │   ├── GerenciarFavoritosUseCase.kt ← NOVO
│   │   └── CompartilharPrevisaoUseCase.kt ← NOVO
│   ├── data/local/entity/
│   │   └── FavoritaCidadeEntity.kt      ← NOVO
│   ├── data/local/dao/
│   │   └── FavoritaCidadeDao.kt         ← NOVO
│   ├── data/repository/
│   │   └── FavoritosRepositoryImpl.kt   ← NOVO
│   └── utils/
│       └── CompartilhamentoBuilder.kt   ← NOVO
│
├── res/
│   ├── values/strings.xml               ← MODIFICADO (migrar hardcoded + WMO)
│   └── values-en/strings.xml            ← NOVO

app/src/main/kotlin/com/weather/
├── presentation/home/HomeViewModel.kt   ← MODIFICADO (pager state + favoritos)
├── presentation/home/HomeScreen.kt      ← MODIFICADO (pager wrapper)
├── presentation/search/SearchSheet.kt   ← MODIFICADO (FavoritoIconButton)
├── data/local/AppDatabase.kt            ← MODIFICADO (versão 2 + migration)
└── utils/WmoMapper.kt                   ← MODIFICADO (usar string resources)
```

---

## Comandos de Build

```bash
# Build debug
./gradlew assembleDebug

# Rodar testes unitários novos
./gradlew test --tests "com.weather.FavoritosRepositoryTest"
./gradlew test --tests "com.weather.CompartilhamentoBuilderTest"
./gradlew test --tests "com.weather.FavoritaViewModelTest"

# Verificar APK size (deve permanecer ≤ 15MB)
./gradlew bundleRelease
ls -la app/build/outputs/bundle/release/*.aab

# Lint
./gradlew lint
./gradlew detekt
```
