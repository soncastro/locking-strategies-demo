# Locking Strategies Demo

Projeto de demonstração de controle de concorrência com lock otimista e lock pessimista em uma API REST com Spring Boot e PostgreSQL.

## Tecnologias utilizadas

- Java 17
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA
- Flyway
- PostgreSQL 18.1
- Docker Compose
- k6
- Maven Wrapper (`./mvnw`)

## O que a aplicação demonstra

O projeto expõe endpoints para comparar diferentes abordagens de concorrência:

- débito sem proteção adicional
- débito com `synchronized`
- débito com lock pessimista
- débito com lock otimista
- um cenário fictício de aferição de pneu com e sem proteção contra inconsistência

## Subindo a infraestrutura com Docker Compose

O arquivo `docker-compose.yaml` sobe um banco PostgreSQL com a configuração esperada pela aplicação.

```bash
docker compose up
```

Serviço iniciado:

- PostgreSQL na porta `5432`
- database: `locking-strategies-demo-db`
- user: `locking-strategies-demo-user`
- password: `locking-strategies-demo-password`

Para parar a infraestrutura:

```bash
docker compose down
```

Se quiser remover também o volume do banco:

```bash
docker compose down -v
```

## Executando a aplicação

Com o banco em execução, inicie a API com o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

A aplicação usa por padrão:

- API base: `http://localhost:8080/locking-strategies-demo`
- PostgreSQL: `localhost:5432`

Ao subir, o Flyway cria as tabelas e dados iniciais. O endpoint de reset também recoloca o cenário-base para os testes:

- conta `1` com saldo `100`
- pneu `1`
- uma aferição inicial do pneu `1` com vida `2`

## Executando os testes `test_*` com k6

Os scripts `test_*` ficam na raiz do projeto e fazem chamadas HTTP diretamente para a API.

Exemplo de execução:

```bash
k6 run test_reset.js
k6 run test_debit.js
k6 run test_debit-synchronized.js
k6 run test_debit-pessimistic-locking.js
k6 run test_debit-optimistic-locking.js
k6 run test_cenario-ficticio-afericao-com-inconsistencia.js
k6 run test_cenario-ficticio-afericao-sem-inconsistencia.js
```

Antes de executar qualquer cenário concorrente, é recomendado rodar:

```bash
k6 run test_reset.js
```

## Qual endpoint cada teste chama

### `test_reset.js`

- Faz `POST` para `/locking-strategies-demo/reset`
- Executa uma única requisição para restaurar o estado inicial do cenário

### `test_debit.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/debit/1/80.00`
- `/locking-strategies-demo/debit/1/40.00`
- Demonstra o cenário sem proteção específica contra condição de corrida

### `test_debit-synchronized.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/debit-synchronized/1/80.00`
- `/locking-strategies-demo/debit-synchronized/1/40.00`
- Demonstra serialização usando `synchronized` na aplicação

### `test_debit-pessimistic-locking.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/debit-pessimistic-locking/1/80.00`
- `/locking-strategies-demo/debit-pessimistic-locking/1/40.00`
- Demonstra uso de lock pessimista no banco com `FOR UPDATE`

### `test_debit-optimistic-locking.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/debit-optimistic-locking/1/80.00`
- `/locking-strategies-demo/debit-optimistic-locking/1/40.00`
- Demonstra uso de versão para lock otimista

### `test_cenario-ficticio-afericao-com-inconsistencia.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/cenario-ficticio-afericao-pneu-com-inconsistencia/1/3`
- `/locking-strategies-demo/cenario-ficticio-afericao-pneu-com-inconsistencia/1/2`
- Demonstra um cenário em que duas gravações concorrentes podem gerar inconsistência lógica

### `test_cenario-ficticio-afericao-sem-inconsistencia.js`

- Faz `POST` concorrente para:
- `/locking-strategies-demo/cenario-ficticio-afericao-pneu-sem-inconsistencia/1/3`
- `/locking-strategies-demo/cenario-ficticio-afericao-pneu-sem-inconsistencia/1/2`
- Demonstra o mesmo cenário protegido por lock pessimista na tabela de pneu