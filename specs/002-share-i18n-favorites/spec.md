# Feature Specification: Weather App v1.1 — Compartilhamento, Internacionalização e Favoritos

**Feature Branch**: `002-share-i18n-favorites`

**Created**: 2026-05-27

**Status**: Draft

---

## Visão Geral

Versão 1.1 expande o app com três funcionalidades complementares:

1. **Compartilhamento de previsão** — usuário compartilha condições atuais via apps de mensagem/e-mail
2. **Internacionalização (i18n)** — suporte a Inglês (EUA) além do PT-BR padrão
3. **Cidades Favoritas** — gerenciamento de até 10 cidades com acesso rápido

Estas features aumentam o valor de retenção e alcance do app sem adicionar dependências externas novas.

---

## User Scenarios & Testing

### User Story 1 — Gerenciar Cidades Favoritas (Priority: P1)

O usuário salva cidades que consulta frequentemente (ex: cidade natal, cidade de trabalho) e alterna entre elas com o mesmo nível de experiência que tem com sua localização atual.

**Why this priority**: Favoritos é a feature de maior impacto em retenção — elimina o atrito de redigitar cidades frequentes e é o principal motivo pelo qual usuários escolhem um app de clima específico.

**Independent Test**: Pode ser testado completamente adicionando "Rio de Janeiro" como favorito, fechando e reabrindo o app, e verificando que a cidade persiste e exibe dados de clima.

**Acceptance Scenarios**:

1. **Dado** que o usuário visualiza os resultados de busca de "Curitiba, PR", **quando** toca o ícone de coração/estrela ao lado do resultado, **então** "Curitiba, PR" é adicionada à lista de favoritos com confirmação visual (toast "Adicionada aos favoritos").

2. **Dado** que o usuário tem 3 cidades favoritas, **quando** abre o painel de favoritos, **então** vê as 3 cidades em ordem alfabética ascendente, cada uma com temperatura atual, ícone WMO e descrição resumida.

3. **Dado** que o usuário tem 10 cidades favoritas (limite máximo), **quando** tenta adicionar uma 11ª cidade, **então** vê mensagem: "Limite de 10 favoritos atingido. Remova uma cidade para adicionar outra."

4. **Dado** que o usuário está visualizando uma cidade favorita, **quando** faz swipe para a esquerda no card da cidade, **então** vê botão "Remover" vermelho e ao confirmar a cidade é removida dos favoritos.

5. **Dado** que o app está offline e o usuário abre um favorito com cache válido (< 24h), **quando** visualiza a cidade, **então** vê os dados em cache com badge 🔴 "OFFLINE" — mesmo comportamento da localização atual.

---

### User Story 2 — Alternar Entre Localização Atual e Favoritos (Priority: P1)

O usuário navega fluidamente entre sua localização atual e cidades favoritas usando um pager horizontal (estilo carrossel), mantendo a mesma experiência visual em todas as cidades.

**Why this priority**: A navegação entre cidades é inseparável do valor dos favoritos — sem uma UX clara de alternância, o recurso perde utilidade prática.

**Independent Test**: Pode ser testado adicionando 2 favoritos e verificando que o pager exibe 3 telas (localização atual + 2 favoritas) com swipe horizontal suave entre elas.

**Acceptance Scenarios**:

1. **Dado** que o usuário tem 2 cidades favoritas, **quando** abre o app, **então** vê a tela principal da sua localização atual como primeira página; indicadores de página (dots) no topo mostram que há 3 páginas no total.

2. **Dado** que o usuário está na tela de localização atual, **quando** faz swipe para a direita, **então** transita suavemente para a primeira cidade favorita (em ordem alfabética), com toda a experiência visual idêntica (previsão atual, horária, 7 dias).

3. **Dado** que o usuário tem 0 favoritos, **quando** abre o app, **então** não há indicadores de página e não há swipe horizontal disponível — comportamento idêntico à v1.0.

4. **Dado** que o usuário está em qualquer página do pager, **quando** toca no ícone de lista/menu de favoritos, **então** um bottom sheet exibe todas as cidades (localização atual + favoritas) para navegação direta sem swipe sequencial.

---

### User Story 3 — Compartilhar Previsão Atual (Priority: P2)

O usuário compartilha as condições meteorológicas atuais de qualquer cidade (localização atual ou favorita) via WhatsApp, Telegram, e-mail, SMS ou qualquer app instalado no dispositivo.

**Why this priority**: Compartilhamento é uma feature de crescimento viral (word-of-mouth) com implementação simples. Não bloqueia nenhuma outra feature.

**Independent Test**: Pode ser testado tocando no botão de compartilhar na tela principal e verificando que o seletor de apps do sistema abre com o texto correto pré-formatado.

**Acceptance Scenarios**:

1. **Dado** que o usuário está na tela principal (qualquer cidade), **quando** toca no ícone de compartilhar (no canto superior direito), **então** o seletor de apps do sistema operacional abre com o seguinte texto pré-formatado:
   ```
   🌤️ São Paulo, SP
   Agora: 24°C — Parcialmente Nublado
   Sensação: 22°C | Umidade: 65% | Vento: 15 km/h
   📅 Sáb: ☀️ 27°/18° | Dom: 🌧️ 23°/17° | Seg: ⛅ 25°/19°
   
   Via Weather App
   ```

2. **Dado** que o usuário compartilha a previsão, **quando** o seletor de apps abre, **então** todos os apps instalados no dispositivo que aceitam texto aparecem como destino (WhatsApp, Telegram, Gmail, SMS, Twitter/X, etc.) — sem restrição de destino.

3. **Dado** que o usuário cancela o seletor de apps (pressiona "Voltar"), **quando** retorna ao app, **então** o app está exatamente no mesmo estado que estava antes — sem efeito colateral.

4. **Dado** que o app está no idioma Inglês, **quando** o usuário compartilha, **então** o texto é gerado no idioma atual do app:
   ```
   🌤️ São Paulo, SP
   Now: 24°C — Partly Cloudy
   Feels like: 22°C | Humidity: 65% | Wind: 15 km/h
   📅 Sat: ☀️ 27°/18° | Sun: 🌧️ 23°/17° | Mon: ⛅ 25°/19°
   
   Via Weather App
   ```

---

### User Story 4 — Usar App em Inglês (Priority: P2)

O usuário cujo dispositivo está configurado em Inglês (EUA) vê toda a interface do app automaticamente em inglês, incluindo descrições WMO, labels de UI, mensagens de erro e datas.

**Why this priority**: Internacionalização expande o público-alvo sem requerer ação do usuário — o app simplesmente responde ao idioma do sistema operacional.

**Independent Test**: Pode ser testado mudando o idioma do dispositivo para "English (US)" e abrindo o app, verificando que toda a UI muda para inglês sem reinicialização manual.

**Acceptance Scenarios**:

1. **Dado** que o dispositivo está configurado em "English (United States)", **quando** o usuário abre o app, **então** toda a interface está em inglês: labels ("Humidity", "Wind", "Feels like"), mensagens de erro ("No connection. Using cached data."), datas ("Saturday, May 17") e descrições WMO ("Partly Cloudy", "Thunderstorm").

2. **Dado** que o dispositivo está em qualquer idioma diferente de Inglês (EUA), **quando** o usuário abre o app, **então** toda a interface é exibida em PT-BR (comportamento padrão).

3. **Dado** que o app está em Inglês e o usuário visualiza a previsão horária, **quando** lê os labels, **então** as horas seguem formato 12h (ex: "2:00 PM") e as datas seguem formato MM/DD (ex: "May 17").

4. **Dado** que o dispositivo muda de idioma enquanto o app está em background, **quando** o usuário retorna ao app, **então** a UI atualiza para o novo idioma sem necessidade de reiniciar o app.

---

### Edge Cases

- **Favorito sem dados em cache e offline**: Exibir tela de erro específica "Sem dados para [Cidade]. Conecte-se para carregar." com botão "Tentar novamente".
- **Favorito adicionado via localização atual**: Se o usuário tentar salvar sua localização atual como favorito (coordenadas próximas a < 1km de um favorito existente), exibir mensagem "Esta cidade já está nos seus favoritos".
- **Texto de compartilhamento muito longo**: Se o nome da cidade for muito longo (> 30 chars), truncar com "..." para manter o texto legível.
- **Cache de favorito expirado (> 1h) mas app offline**: Exibir dados expirados com badge "OFFLINE" e timestamp relativo real ("Dados de há 3h").
- **Remoção de favorito durante visualização**: Se o usuário remove a cidade que está visualizando no momento, o pager retorna automaticamente para a primeira página (localização atual).
- **Idioma não suportado (ex: Espanhol)**: App usa PT-BR como fallback — nunca exibir strings de sistema misturadas.

---

## Requirements

### Functional Requirements — Favoritos

- **FR-FAV-001**: O sistema DEVE permitir salvar até 10 cidades como favoritas por dispositivo.
- **FR-FAV-002**: O sistema DEVE persistir a lista de favoritos entre sessões do app (fechamento e abertura).
- **FR-FAV-003**: A lista de favoritos DEVE ser exibida em ordem alfabética ascendente pelo nome da cidade.
- **FR-FAV-004**: O usuário DEVE poder adicionar uma cidade aos favoritos a partir dos resultados de busca.
- **FR-FAV-005**: O usuário DEVE poder remover um favorito via gesto de swipe-left no card + confirmação.
- **FR-FAV-006**: O sistema DEVE bloquear adição de duplicatas (mesma cidade já favoritada).
- **FR-FAV-007**: O sistema DEVE manter cache independente para cada cidade favorita, com a mesma política de expiração de 1 hora da localização atual.
- **FR-FAV-008**: O sistema DEVE atualizar os dados de todas as cidades favoritas abertas (uma de cada vez) quando o app entra em foreground e o cache está expirado.
- **FR-FAV-009**: O pager horizontal DEVE exibir: localização atual (sempre primeira) + favoritas em ordem alfabética.
- **FR-FAV-010**: O bottom sheet de navegação DEVE listar todas as cidades (localização atual + favoritas) para acesso direto.
- **FR-FAV-011**: O ícone de favorito DEVE refletir o estado atual (cheio = favoritado, vazio = não favoritado) ao visualizar resultados de busca.

### Functional Requirements — Compartilhamento

- **FR-SHR-001**: O sistema DEVE oferecer botão de compartilhamento acessível na tela principal de qualquer cidade.
- **FR-SHR-002**: O compartilhamento DEVE usar o seletor de apps nativo do sistema operacional (sem restrição de destino).
- **FR-SHR-003**: O texto compartilhado DEVE incluir: nome da cidade, temperatura atual, descrição WMO, sensação térmica, umidade, velocidade do vento, e resumo dos próximos 3 dias (dia, ícone emoji, máx/mín).
- **FR-SHR-004**: O texto compartilhado DEVE ser gerado no idioma atual do app.
- **FR-SHR-005**: O texto compartilhado DEVE incluir atribuição "Via Weather App" ao final.
- **FR-SHR-006**: O compartilhamento DEVE funcionar offline (usando dados em cache).

### Functional Requirements — Internacionalização

- **FR-I18N-001**: O app DEVE detectar automaticamente o idioma do dispositivo e aplicar PT-BR ou EN-US conforme correspondência.
- **FR-I18N-002**: PT-BR DEVE ser o idioma padrão para qualquer idioma de dispositivo diferente de Inglês (EUA).
- **FR-I18N-003**: Todas as strings de UI DEVEM ser traduzidas: labels, mensagens de erro, mensagens de sucesso, placeholders, badges e datas.
- **FR-I18N-004**: Todas as descrições WMO (mapeamento de código → texto) DEVEM ser traduzidas para EN-US.
- **FR-I18N-005**: Formato de data DEVE seguir convenção do idioma: PT-BR usa "Sáb, 17 de Mai" / EN-US usa "Sat, May 17".
- **FR-I18N-006**: Formato de hora DEVE seguir convenção do idioma: PT-BR usa 24h ("14:00") / EN-US usa 12h ("2:00 PM").
- **FR-I18N-007**: O app DEVE atualizar o idioma da UI sem reinicialização quando o idioma do sistema muda enquanto o app está em background.

### Key Entities

- **CidadeFavorita**: Representa uma cidade salva pelo usuário. Atributos: nome da cidade, estado/região, país, latitude, longitude, timestamp de adição, posição na ordenação alfabética.
- **CachePrevisao**: Cache de dados meteorológicos por coordenadas (já existente na v1.0; compartilhado entre localização atual e favoritos).
- **TextoCompartilhamento**: Representação formatada das condições atuais para compartilhamento externo. Gerado sob demanda, não persistido.

---

## Decisões de Design Documentadas

### Favoritos — Sincronização com Cache

**Decisão escolhida**: Cache compartilhado por coordenadas (mesma tabela Room da v1.0).

Cada cidade favorita compartilha a mesma infraestrutura de cache da localização atual — um registro por coordenadas (latitude/longitude arredondados a 2 casas decimais). Quando o usuário navega para um favorito:
- Se cache válido (< 1h): exibe imediatamente
- Se cache expirado: busca dados frescos em background e exibe cache enquanto carrega
- Se offline: exibe cache expirado com badge "OFFLINE"

**Alternativas descartadas**:
- *Background sync automático para todos os favoritos*: Consome bateria e dados sem necessidade (usuário pode não abrir todos os favoritos)
- *Cache separado por favorito*: Duplica complexidade sem benefício — mesmas coordenadas já têm cache unificado

### Favoritos — UI de Navegação

**Decisão escolhida**: Pager horizontal (carrossel) com bottom sheet de acesso rápido.

Padrão adotado por Apple Weather (iOS), Samsung Weather e Weather Channel. É a UX mais intuitiva para alternância entre cidades porque:
- O usuário aprende com um único gesto (swipe)
- Não há nav bar adicional que ocupe espaço
- O bottom sheet serve como índice para pular diretamente quando há muitas cidades

**Alternativas descartadas**:
- *Tela dedicada de favoritos com lista*: Cria fricção (2 taps para ver clima de um favorito)
- *Drawer de navegação lateral*: Padrão em desuso no Android, conflita com gestos de sistema

### Favoritos — Interação com Localização Atual

**Decisão escolhida**: Localização atual é sempre a primeira página do pager, sem possibilidade de ser favoritada.

A localização atual é dinâmica (muda com o GPS), portanto é tratada como uma "cidade especial" separada da lista de favoritos estáticos. O usuário sempre começa na sua localização atual e swipa para ver favoritos.

**Regra adicional**: Se o usuário tentar favoritar uma cidade que está a menos de 1km da sua localização atual, o sistema exibe aviso "Esta região já está na sua localização atual."

---

## Success Criteria

### Measurable Outcomes

- **SC-001**: Usuários conseguem adicionar uma cidade aos favoritos em no máximo 2 toques a partir dos resultados de busca.
- **SC-002**: A navegação entre localização atual e qualquer favorito leva no máximo 1 swipe ou 2 toques.
- **SC-003**: O texto de compartilhamento é gerado e o seletor de apps abre em menos de 500ms após o toque no botão.
- **SC-004**: 100% das strings visíveis ao usuário (incluindo descrições WMO e mensagens de erro) são exibidas no idioma correto do dispositivo (PT-BR ou EN-US).
- **SC-005**: O app exibe dados corretos para todas as cidades favoritas dentro de 2 segundos após o usuário navegar para elas (dados frescos ou cache).
- **SC-006**: A lista de favoritos persiste corretamente entre 100% dos ciclos de fechamento e abertura do app.
- **SC-007**: Nenhum favorito exibe dados de temperatura de outra cidade (zero cross-contamination de cache).
- **SC-008**: A mudança de idioma do dispositivo (PT-BR ↔ EN-US) reflete na UI do app sem reinicialização do processo.

---

## Assumptions

- A unidade de temperatura permanece °C em ambos os idiomas (PT-BR e EN-US). Conversão para °F é v1.2+.
- A unidade de velocidade do vento permanece km/h em ambos os idiomas. Conversão para mph é v1.2+.
- O nome da cidade nos favoritos é o nome retornado pela API de geocoding no idioma da busca (PT-BR ou EN-US), não traduzido.
- O app não oferece opção manual de troca de idioma dentro das configurações (segue o idioma do dispositivo automaticamente). Substituição manual de idioma é v1.2+.
- Dados compartilhados são apenas texto (sem imagem/screenshot da tela). Compartilhamento como imagem é v1.2+.
- Favoritos são armazenados localmente no dispositivo; sincronização entre dispositivos (ex: via conta Google) é v1.2+.
- A ordenação alfabética de favoritos usa o nome da cidade no idioma em que foi buscada (não há re-ordenação automática ao mudar de idioma).
- O cache de um favorito é invalidado quando o usuário o remove dos favoritos (limpeza imediata dos dados locais).
