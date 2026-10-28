# LootVault — Sistema de Inventário RPG com Microsserviços

Projeto da disciplina de Sistemas Distribuídos. Três microsserviços Spring Boot independentes, cada um com o seu próprio banco MySQL, que conversam entre si por HTTP/REST usando `RestTemplate` (mesmo padrão do exemplo `book-service` → `cambio-service` da aula).

## Tecnologias

As mesmas da aula: Java 21, Maven, Spring Boot 3.5.8, Spring Web, Spring Boot Actuator, Spring Data JPA, MySQL Driver e Flyway.

## Microsserviços

| Serviço | Porta | Banco | Tabelas |
|---|---|---|---|
| `personagens-service` | 8081 | `personagens_service` | `jogador` 1 → N `personagem` (FK `personagem.jogador_id`) |
| `inventario-service` | 8082 | `inventario_service` | `item` 1 → N `slot_inventario` (FK `slot_inventario.item_id`) |
| `loja-service` | 8083 | `loja_service` | `oferta` 1 → N `compra` (FK `compra.oferta_id`) |

Um serviço **nunca** acessa o banco de outro. Os campos `personagem_id` (no inventário e na loja) e `item_id` (na loja) são referências lógicas, consultadas pela API do outro serviço, e não chaves estrangeiras entre bancos.

## Estrutura de pacotes (igual nos três serviços)

```
com.lootvault.<servico>
├── controller   -> endpoints REST (@RestController)
├── model        -> entidades JPA (@Entity)
├── repository   -> interfaces JpaRepository
├── exceptions   -> exceções do sistema + ExceptionResponse (JSON de erro)
├── handler      -> CustomizedResponseEntityExceptionHandler (tratamento central de erros)
└── response     -> (só na loja) cópias das classes dos outros serviços, sem annotations de persistência
```

Os scripts do banco ficam em `src/main/resources/db/migration` de cada serviço:
- `V1__...sql` e `V2__...sql`: criam as tabelas;
- `V3__Populate_Tables.sql`: popula os dados de teste.

## Como executar

O projeto tem um servidor MySQL próprio em Docker (`lootvault-mysql`, usuário `root`, senha `admin123`), exposto na porta **3307** do computador para não conflitar com outro MySQL que já esteja na 3306. Não é preciso criar os bancos à mão: o Flyway cria os schemas (`spring.flyway.schemas`), as tabelas e os dados ao subir cada serviço.

### Opção 1: tudo no Docker (mais fácil)

Na raiz do projeto:

```
docker compose up --build
```

Sobe o MySQL e os três microsserviços (8081, 8082 e 8083). Comandos úteis:

```
docker compose stop loja-service      # desliga só a Loja (demonstração de falha)
docker compose start loja-service     # liga de novo
docker compose down                   # desliga tudo (os dados continuam no volume)
docker compose down -v                # desliga tudo e APAGA o banco (volta aos dados iniciais)
```

### Opção 2: só o banco no Docker e os serviços no Spring Tools Suite

```
docker compose up -d mysql
```

Depois importe as três pastas no Spring Tools Suite (*File → Import → Existing Maven Projects*) e inicie pelo Boot Dashboard, ou rode `mvn spring-boot:run` dentro de cada pasta. Ordem sugerida: `personagens-service`, `inventario-service` e por último `loja-service`. O `application.properties` já aponta para `localhost:3307`.

Para subir uma segunda instância de um serviço (como na aula), use *Duplicate config* e o VM argument `-Dserver.port=XXXX`. O campo `environment` das respostas mostra qual porta atendeu.

### Como os serviços se encontram

A Loja lê o endereço dos outros serviços do `application.properties` (`url.personagens` e `url.inventario`, que valem `localhost`). No Docker, o `docker-compose.yml` troca esses valores pelo nome dos containers (`http://personagens-service:8081`, `http://inventario-service:8082`).

## Dados de teste

| Personagem | Jogador | Moedas | Inventário inicial |
|---|---|---|---|
| 1 - Kael, o Guerreiro | Felipe | 200 | 3x Poção de Vida, 1x Escudo de Madeira |
| 2 - Lyra, a Arqueira | Felipe | 150 | 1x Poção de Vida |
| 3 - Thorn, o Mago | Mariana | 30 | 2x Poção de Vida |

| Oferta | Item | Preço | Ativa |
|---|---|---|---|
| 1 | Espada Longa | 50 | sim |
| 2 | Poção de Vida | 10 | sim |
| 3 | Arco Élfico | 120 | sim |
| 4 | Cajado Arcano | 300 | sim |
| 5 | Elmo do Dragão | 999 | não |

## Endpoints

### personagens-service (8081)
| Método | URL | Descrição |
|---|---|---|
| GET | `/personagens-service/{id}` | Consulta personagem e moedas |
| GET | `/personagens-service/jogador/{id}` | Consulta jogador |
| GET | `/personagens-service/jogador/{id}/personagens` | Personagens de um jogador |
| POST | `/personagens-service/{id}/debitar/{valor}` | Debita moedas (usado pela loja) |
| POST | `/personagens-service/{id}/creditar/{valor}` | Devolve moedas (compensação) |

### inventario-service (8082)
| Método | URL | Descrição |
|---|---|---|
| GET | `/inventario-service/itens` | Catálogo de itens |
| GET | `/inventario-service/item/{id}` | Consulta um item |
| GET | `/inventario-service/{personagemId}` | Slots do inventário do personagem |
| POST | `/inventario-service/{personagemId}/adicionar/{itemId}` | Adiciona item (empilha ou usa a 1ª posição livre de 0 a 9) |
| PUT | `/inventario-service/{personagemId}/mover/{origem}/{destino}` | Move item entre posições |
| DELETE | `/inventario-service/{personagemId}/remover/{posicao}` | Remove 1 unidade do item |

### loja-service (8083)
| Método | URL | Descrição |
|---|---|---|
| GET | `/loja-service/ofertas` | Ofertas ativas (com nome do item vindo do inventário) |
| GET | `/loja-service/oferta/{id}` | Consulta uma oferta |
| POST | `/loja-service/comprar/{personagemId}/{ofertaId}` | Realiza a compra |
| GET | `/loja-service/compra/{id}` | Situação de uma compra |
| GET | `/loja-service/compras/{personagemId}` | Histórico de compras |

## Fluxo da compra

```
POST /loja-service/comprar/1/1
  1. Loja grava a compra como PENDENTE
  2. Loja -> POST personagens-service/1/debitar/50
  3. Loja -> POST inventario-service/1/adicionar/1
  4a. Tudo certo           -> compra CONCLUIDA
  4b. Falhou no passo 2    -> compra FALHOU (nenhuma moeda foi debitada)
  4c. Falhou no passo 3    -> Loja -> POST personagens-service/1/creditar/50 (devolve as moedas) e compra FALHOU
```

Operações em bancos diferentes não formam uma transação SQL única; por isso a compra guarda a situação (`PENDENTE`, `CONCLUIDA`, `FALHOU`, `ERRO_REEMBOLSO`) e usa compensação.

## Tratamento de erros

Todas as exceções passam pelo `CustomizedResponseEntityExceptionHandler` e retornam um JSON:

```json
{ "timestamp": "...", "message": "Saldo insuficiente: ...", "details": "uri=/personagens-service/3/debitar/300" }
```

| Status | Exceção | Exemplo |
|---|---|---|
| 404 | `ResourceNotFoundException` | personagem, item, oferta ou compra inexistente |
| 400 | `InvalidValueException` | valor negativo, posição fora do inventário, oferta inativa |
| 409 | `ConflictException` | saldo insuficiente, posição ocupada, inventário cheio |
| 503 | `ServiceUnavailableException` | outro microsserviço desligado |
| 500 | `Exception` | qualquer erro não previsto |

## Roteiro de demonstração

Use um cliente HTTP (Postman, Insomnia) ou `curl`.

1. `GET http://localhost:8081/personagens-service/1` → Kael com 200 moedas.
2. `GET http://localhost:8082/inventario-service/1` → inventário inicial.
3. `GET http://localhost:8083/loja-service/ofertas` → lista de ofertas.
4. `POST http://localhost:8083/loja-service/comprar/1/1` → compra a espada (`CONCLUIDA`).
5. Repetir os passos 1 e 2: Kael agora tem 150 moedas e a Espada Longa na posição 2.
6. `POST http://localhost:8083/loja-service/comprar/3/4` → Thorn tem 30 moedas: erro **409** e compra `FALHOU`.
7. Desligar o `inventario-service` e fazer `POST http://localhost:8083/loja-service/comprar/1/2` → erro **503**, compra `FALHOU` e as moedas são devolvidas (conferir no passo 1).
8. Desligar só o `loja-service`: os passos 1 e 2 continuam funcionando.
9. No MySQL, mostrar que existem três bancos separados: `personagens_service`, `inventario_service` e `loja_service`. No HeidiSQL, conecte em `localhost`, porta `3307`, usuário `root`, senha `admin123`; ou pelo terminal:
   ```
   docker exec -it lootvault-mysql mysql -uroot -padmin123 -e "SHOW DATABASES;"
   ```
