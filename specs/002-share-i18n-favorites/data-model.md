# Data Model: Weather App v1.1 — Favoritos, i18n, Compartilhamento

**Gerado por**: `/speckit-plan` | **Data**: 2026-05-27 | **Plano**: [plan.md](plan.md)

> **Nota**: Este documento cobre apenas as adições/modificações da v1.1.
> Para o modelo base da v1.0, ver [../001-weather-app-mvp/data-model.md](../001-weather-app-mvp/data-model.md).

---

## Visão Geral das Adições

```
┌─────────────────────────────────────────────────────────────────┐
│                     CAMADA DE DADOS v1.1                        │
│                                                                 │
│  [Room Local DB — existente v1.0]                               │
│  PrevisaoEntity (previsoes) ──────── sem alteração              │
│  HistoricoBuscaEntity (historico_busca) ── sem alteração        │
│                                                                 │
│  [Room Local DB — NOVO v1.1]                                    │
│  FavoritaCidadeEntity (favoritos) ─────── NOVO                  │
│                          │                                      │
│                          │ id = "lat_2d,lon_2d"                 │
│                          ↓                                      │
│  PrevisaoEntity ◄─────── cache compartilhado (sem cópia)        │
│                                                                 │
│  [Domain Layer — NOVO]                                          │
│  CidadeFavorita ──────── modelo de domínio para favoritos       │
│  PaginaCidade ─────────── sealed class (LocalizacaoAtual | Fav)│
│  TextoCompartilhamento ── gerado sob demanda, não persistido    │
│                                                                 │
│  [Resources — NOVO]                                             │
│  res/values/strings.xml ─── PT-BR (padrão, inclui WMO)         │
│  res/values-en/strings.xml ─ EN-US (inclui WMO em inglês)      │
└─────────────────────────────────────────────────────────────────┘
```

---

## Room Migration: Versão 1 → 2

**AppDatabase**: `version` atualizado de `1` para `2`

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `favoritos` (
                `id` TEXT NOT NULL,
                `nome_cidade` TEXT NOT NULL,
                `estado` TEXT NOT NULL,
                `pais` TEXT NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                `adicionado_em` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_favoritos_nome_cidade` ON `favoritos` (`nome_cidade`)"
        )
    }
}
```

---

## Nova Entidade Room: FavoritaCidadeEntity

**Tabela**: `favoritos`

| Campo           | Tipo SQL         | Tipo Kotlin | Nullable | Descrição                                                |
|-----------------|------------------|-------------|----------|----------------------------------------------------------|
| `id`            | TEXT PRIMARY KEY | String      | Não      | `"lat_2d,lon_2d"` — mesmo formato de `PrevisaoEntity.id` |
| `nome_cidade`   | TEXT             | String      | Não      | Ex: "Curitiba"                                           |
| `estado`        | TEXT             | String      | Não      | Ex: "Paraná"                                             |
| `pais`          | TEXT             | String      | Não      | Ex: "Brasil"                                             |
| `latitude`      | REAL             | Double      | Não      | Latitude com precisão completa                           |
| `longitude`     | REAL             | Double      | Não      | Longitude com precisão completa                          |
| `adicionado_em` | INTEGER          | Long        | Não      | Epoch millis de quando foi favoritado                    |

**Índices**:
- `PRIMARY KEY (id)` — lookup e deduplicação por coordenadas
- `INDEX idx_nome_cidade (nome_cidade)` — ordenação alfabética eficiente

**Regras de validação**:
- `id` nunca null/vazio; formato `"-23.55,-46.63"` (2 casas decimais)
- Máximo 10 registros na tabela (verificado na camada de repositório antes de `INSERT`)
- `latitude` entre -90.0 e 90.0; `longitude` entre -180.0 e 180.0

```kotlin
@Entity(
    tableName = "favoritos",
    indices = [Index(value = ["nome_cidade"])]
)
data class FavoritaCidadeEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "nome_cidade") val nomeCidade: String,
    val estado: String,
    val pais: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "adicionado_em") val adicionadoEm: Long
)
```

---

## DAO: FavoritaCidadeDao

```kotlin
@Dao
interface FavoritaCidadeDao {
    @Query("SELECT * FROM favoritos ORDER BY nome_cidade ASC")
    fun observarTodos(): Flow<List<FavoritaCidadeEntity>>

    @Query("SELECT * FROM favoritos ORDER BY nome_cidade ASC")
    suspend fun listarTodos(): List<FavoritaCidadeEntity>

    @Query("SELECT COUNT(*) FROM favoritos")
    suspend fun contarTodos(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE id = :id)")
    suspend fun existe(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(favorito: FavoritaCidadeEntity)

    @Query("DELETE FROM favoritos WHERE id = :id")
    suspend fun remover(id: String)
}
```

---

## Modelos de Domínio (Novos)

### CidadeFavorita

```kotlin
data class CidadeFavorita(
    val id: String,           // "lat_2d,lon_2d"
    val nomeCidade: String,
    val estado: String,
    val pais: String,
    val latitude: Double,
    val longitude: Double,
    val adicionadoEm: Long
) {
    val nomeCompleto: String get() = "$nomeCidade, $estado"
}
```

### PaginaCidade (sealed class — estado do pager)

```kotlin
sealed class PaginaCidade {
    object LocalizacaoAtual : PaginaCidade()
    data class Favorita(val cidade: CidadeFavorita) : PaginaCidade()

    val nomeExibicao: String get() = when (this) {
        is LocalizacaoAtual -> "" // ViewModel injeta o nome real via GPS
        is Favorita -> cidade.nomeCompleto
    }
}
```

### TextoCompartilhamento

Gerado sob demanda pelo `CompartilhamentoBuilder` — não persistido.

```kotlin
data class TextoCompartilhamento(
    val texto: String,  // String formatada pronta para share intent
    val cidade: String  // Usada para subject de e-mail (opcional)
)
```

---

## Estado de UI: HomeUiState (extensão v1.1)

Adicionar campos ao `HomeUiState` existente:

```kotlin
data class HomeUiState(
    // ... campos existentes da v1.0 ...

    // NOVO v1.1
    val paginas: List<PaginaCidade> = listOf(PaginaCidade.LocalizacaoAtual),
    val paginaAtualIndex: Int = 0,
    val cidadeAtualEhFavorita: Boolean = false,
    val mostrarBottomSheetFavoritos: Boolean = false,
    val limiteAtingido: Boolean = false  // true quando favoritos == 10
)
```

---

## String Resources — Estrutura i18n

### Categorias de strings a criar/migrar

| Categoria              | Prefixo  | Exemplos                                                    |
|------------------------|----------|-------------------------------------------------------------|
| Labels UI              | `lbl_`   | `lbl_umidade`, `lbl_vento`, `lbl_sensacao`                  |
| Mensagens transitórias | `msg_`   | `msg_offline`, `msg_sem_conexao`, `msg_favorito_adicionado` |
| Erros                  | `err_`   | `err_localizacao`, `err_api`, `err_sem_cache`               |
| Placeholders           | `hint_`  | `hint_busca`, `hint_cidade`                                 |
| Badges                 | `badge_` | `badge_offline`, `badge_refinando`                          |
| Descrições WMO         | `wmo_`   | `wmo_0`, `wmo_1`, ..., `wmo_99`                             |
| Formatos               | `fmt_`   | `fmt_data`, `fmt_hora`, `fmt_temp_atualizada`               |
| Compartilhamento       | `share_` | `share_agora`, `share_sensacao`, `share_via`                |

### Mapeamento WMO → String Resource IDs

| Código WMO | ID Resource | PT-BR                          | EN-US                           |
|------------|-------------|--------------------------------|---------------------------------|
| 0          | `wmo_0`     | "Céu Limpo"                    | "Clear Sky"                     |
| 1          | `wmo_1`     | "Predominantemente Limpo"      | "Mainly Clear"                  |
| 2          | `wmo_2`     | "Parcialmente Nublado"         | "Partly Cloudy"                 |
| 3          | `wmo_3`     | "Nublado"                      | "Overcast"                      |
| 45         | `wmo_45`    | "Nevoeiro"                     | "Fog"                           |
| 48         | `wmo_48`    | "Nevoeiro com Gelo"            | "Icy Fog"                       |
| 51         | `wmo_51`    | "Garoa Leve"                   | "Light Drizzle"                 |
| 53         | `wmo_53`    | "Garoa Moderada"               | "Moderate Drizzle"              |
| 55         | `wmo_55`    | "Garoa Intensa"                | "Dense Drizzle"                 |
| 61         | `wmo_61`    | "Chuva Fraca"                  | "Slight Rain"                   |
| 63         | `wmo_63`    | "Chuva Moderada"               | "Moderate Rain"                 |
| 65         | `wmo_65`    | "Chuva Forte"                  | "Heavy Rain"                    |
| 71         | `wmo_71`    | "Neve Fraca"                   | "Slight Snow"                   |
| 73         | `wmo_73`    | "Neve Moderada"                | "Moderate Snow"                 |
| 75         | `wmo_75`    | "Neve Forte"                   | "Heavy Snow"                    |
| 80         | `wmo_80`    | "Pancadas de Chuva Fracas"     | "Slight Rain Showers"           |
| 81         | `wmo_81`    | "Pancadas de Chuva Moderadas"  | "Moderate Rain Showers"         |
| 82         | `wmo_82`    | "Pancadas de Chuva Fortes"     | "Violent Rain Showers"          |
| 95         | `wmo_95`    | "Tempestade"                   | "Thunderstorm"                  |
| 96         | `wmo_96`    | "Tempestade com Granizo Leve"  | "Thunderstorm with Slight Hail" |
| 99         | `wmo_99`    | "Tempestade com Granizo Forte" | "Thunderstorm with Heavy Hail"  |

> **Nota**: Apenas os WMO codes efetivamente usados pela Open-Meteo API são mapeados. Codes intermediários (ex: 56, 57, 66, 67, etc.) são incluídos na lista completa no arquivo de recursos, mas o subset acima cobre ≥ 95% dos casos reais.

---

## Diagrama de Relacionamentos (v1.1 completo)

```
FavoritaCidadeEntity
    id (PK) ─────────────────────────────┐
    nome_cidade                          │ mesmo formato de chave
    estado                               │
    pais                                 ↓
    latitude              PrevisaoEntity
    longitude                 id (PK) ←──────── cache compartilhado
    adicionado_em             dados_json
                              timestamp_atualizado

HistoricoBuscaEntity (inalterado)
    id (PK AUTOINCREMENT)
    nome_cidade
    buscado_em

[String Resources]
    values/strings.xml        ← PT-BR (padrão)
    values-en/strings.xml     ← EN-US
        ↑
        WmoMapper.kt mapeia código → R.string.wmo_XX
```
