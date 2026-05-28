# Research: Weather App v1.1 — Compartilhamento, i18n e Favoritos

**Gerado por**: `/speckit-plan` | **Data**: 2026-05-27 | **Plano**: [plan.md](plan.md)

---

## Decisão 1: HorizontalPager para navegação entre cidades

**Decision**: `androidx.compose.foundation.pager.HorizontalPager` + `PagerState` (Compose Foundation)

**Rationale**:
- Já faz parte do `compose-foundation` incluído no BOM existente — zero dependência nova
- API estável desde Compose 1.4; o `accompanist-pager` (predecessor) foi descontinuado
- `PagerState` é `remember`able e integra nativamente com `LazyRow`/indicadores
- Suporta swipe gesture sem conflito com pull-to-refresh (eixos diferentes)

**Alternatives considered**:
- `accompanist-pager`: descontinuado, migrado para Foundation
- Navigation Compose (NavHost): seria overcomplexidade — não há deep links entre cidades
- `ViewPager2` + XML: incompatível com Compose-first approach da Constitution

**Integration point**: `HomeScreen.kt` substitui o conteúdo por `HorizontalPager { page -> WeatherPageContent(pages[page]) }`. Estado do pager fica no `HomeViewModel`.

---

## Decisão 2: i18n — Abordagem de Localização Android

**Decision**: String Resources (`res/values/strings.xml` + `res/values-en/strings.xml`) com `LocalConfiguration.current` no Compose para formatação de data/hora.

**Rationale**:
- Android seleciona automaticamente o `values-en/` folder quando o dispositivo está em `en-US` (sem código runtime extra)
- `Locale.getDefault()` retorna o locale do sistema — usado em `DateTimeFormatter` para formato 12h/24h
- WMO descriptions migradas de `WmoMapper.kt` (hardcoded PT-BR) para string resources com IDs como `wmo_code_0`, `wmo_code_1`, etc. — mantém mapeamento por código inteiro
- `Context.getString(resId)` funciona em qualquer camada com acesso a Context; para ViewModels usa-se `Application` context via Hilt

**Alternatives considered**:
- `AppCompatDelegate.setDefaultNightMode` + custom locale override: API frágil no Android 13+, deprecated
- i18n via hardcoded `when(locale)` blocks: DRY violation, duplicação de lógica
- `DataStore` com idioma salvo manualmente: desnecessário — Android gerencia locale automaticamente

**String resource naming convention**:
- Labels UI: `str_label_humidade`, `str_label_vento`, etc.
- Mensagens: `str_msg_offline`, `str_msg_sem_conexao`, etc.
- Erros: `str_erro_localizacao`, `str_erro_api`, etc.
- WMO: `wmo_0`, `wmo_1`, ..., `wmo_99` (usando código como sufixo inteiro)
- Datas: formatting via `DateTimeFormatter.ofPattern(getString(R.string.formato_data), Locale.getDefault())`

**Date/time format strategy**:
- PT-BR: `"EEE, dd 'de' MMM"` → "Sáb, 17 de Mai" | hora: `"HH:mm"` → "14:00"
- EN-US: `"EEE, MMM d"` → "Sat, May 17" | hora: `"h:mm a"` → "2:00 PM"
- Ambos definidos em `strings.xml` como `formato_data` e `formato_hora` (valores diferentes por locale)

---

## Decisão 3: Favoritos — Room Migration Strategy

**Decision**: Incrementar `DATABASE_VERSION` de 1 para 2 com `Migration(1, 2)` adicionando a tabela `favoritos`. Sem `fallbackToDestructiveMigration`.

**Rationale**:
- Usuários da v1.0 têm dados de cache em `previsoes` e histórico em `historico_busca` — perder esses dados seria UX negativa
- A migration é simples (`CREATE TABLE favoritos ...`) — sem risco de falha
- Room valida o schema em runtime (com `exportSchema = true`) — detecta divergências em testes

**Migration SQL**:
```sql
CREATE TABLE IF NOT EXISTS `favoritos` (
    `id` TEXT NOT NULL,
    `nome_cidade` TEXT NOT NULL,
    `estado` TEXT NOT NULL,
    `pais` TEXT NOT NULL,
    `latitude` REAL NOT NULL,
    `longitude` REAL NOT NULL,
    `adicionado_em` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE INDEX IF NOT EXISTS `index_favoritos_nome_cidade` ON `favoritos` (`nome_cidade`);
```

**Alternatives considered**:
- `fallbackToDestructiveMigration()`: perderia cache existente, péssima UX para usuário que atualiza o app
- Schema separado (DB separado para favoritos): duplica complexidade de gerenciamento de DB

---

## Decisão 4: Cache de Favoritos — Compartilhamento com PrevisaoEntity

**Decision**: `FavoritaCidadeEntity` é uma entidade separada que armazena apenas metadados da cidade. O cache de previsão permanece na `PrevisaoEntity` existente, keyed pelo mesmo `id = "lat_2d,lon_2d"`.

**Rationale**:
- Nenhuma duplicação de dados: `favoritos` guarda apenas a "intenção" de favoritar (coordenadas + nome)
- Quando o usuário navega para um favorito, o sistema busca `PrevisaoEntity` pelo mesmo `id`
- Se cache não existe: `PrevisaoRepository.obterPrevisao(lat, lon)` cria o cache normalmente
- TTL de 1h e limpeza de 7 dias já gerenciados pelo `PrevisaoRepository` — reutilizados sem alteração
- Remoção de favorito: apenas deleta da tabela `favoritos`; o cache em `previsoes` permanece (será limpo pelo scheduler de 7 dias ou sobrescrito na próxima busca)

**Alternatives considered**:
- `FavoritaCidadeEntity` com coluna `dados_json` própria: duplica ~5KB por favorito desnecessariamente
- Background sync de todos os favoritos (WorkManager): consome bateria sem valor percebido (usuário abre o app e sincroniza sob demanda)

---

## Decisão 5: Compartilhamento — Intent ACTION_SEND

**Decision**: `Intent.ACTION_SEND` com `type = "text/plain"` via `Context.startActivity(Intent.createChooser(...))`.

**Rationale**:
- API nativa Android sem dependência externa
- O chooser do sistema lista automaticamente todos os apps que aceitam texto (WhatsApp, Telegram, Gmail, SMS, etc.)
- Compatível com API 24+ sem flags especiais
- `Intent.FLAG_ACTIVITY_NEW_TASK` necessário quando chamado fora de Activity context — deve ser tratado no ViewModel via `Application` context ou via `LaunchedEffect` + `LocalContext`

**Share text builder**: `CompartilhamentoBuilder.kt` — objeto puro (testável sem Android) que recebe `Previsao` + strings localizadas e retorna `String`. Strings vêm via parâmetro (não via `getString()` interno) para facilitar testes unitários.

**Truncation rule**: Se `nomeLocalidade.length > 30`, truncar para `nomeLocalidade.take(28) + "…"`.

---

## Decisão 6: Sincronização de locale no Compose

**Decision**: `LocalContext.current` no nível de composable para obter strings localizadas. ViewModels recebem strings pre-formatadas via `Application.getString()` ou parâmetros injetados nos use cases.

**Rationale**:
- ViewModel não deve depender de Context diretamente (exceto `Application` via Hilt)
- Para textos dinâmicos (ex: mensagem de erro com nome da cidade), o ViewModel expõe um `sealed class UiError` e o composable mapeia para a string localizada
- Para textos estáticos (labels, placeholders), o composable usa `stringResource(R.string.xxx)` diretamente — zero lógica no ViewModel

**Configuration change handling**: Compose recompõe automaticamente quando o locale muda via `Configuration` update — comportamento padrão do Android. Nenhum código adicional necessário se não houver estado salvo com texto hardcoded.
