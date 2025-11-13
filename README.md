# Módulo 4 — Kafka Cluster + Producer/Consumers (Komfort Chain)

O **Módulo 4** da suíte **Komfort Chain** implementa um ecossistema completo de **mensageria distribuída** utilizando Apache Kafka.  
A arquitetura inclui:

- Um **serviço produtor**  
- Dois **consumidores independentes**  
- Um **cluster Kafka com três brokers** e **cinco partições**  
- Integração com logs estruturados via **Graylog**  
- Pipeline CI/CD completo com **SonarCloud**, **OWASP Dependency-Check** e **Docker Hub**

Este módulo demonstra, na prática, os princípios de **Event-Driven Architecture**, **alta disponibilidade**, **resiliência**, **escalabilidade horizontal** e **processamento assíncrono**.

---

## Status do Projeto

[![Full CI/CD](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml)

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo4&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Komfort-chain_modulo4)

[![Docker Hub - Producer](https://img.shields.io/badge/DockerHub-magyodev/modulo4--producer-blue)](https://hub.docker.com/r/magyodev/modulo4-producer)
[![Docker Hub - Consumer A](https://img.shields.io/badge/DockerHub-magyodev/modulo4--consumer--a-blue)](https://hub.docker.com/r/magyodev/modulo4-consumer-a)
[![Docker Hub - Consumer B](https://img.shields.io/badge/DockerHub-magyodev/modulo4--consumer--b-blue)](https://hub.docker.com/r/magyodev/modulo4-consumer-b)

![Java 21](https://img.shields.io/badge/Java-21-red)
![Spring Boot 3.5.7](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)
![Kafka](https://img.shields.io/badge/Apache_Kafka-7.5.1-black)

---

## Tecnologias Utilizadas

| Categoria            | Tecnologias / Ferramentas                     |
|---------------------|------------------------------------------------|
| **Linguagem**       | Java 21                                        |
| **Frameworks**      | Spring Boot 3.5.7 • Spring Kafka               |
| **Mensageria**      | Kafka Cluster (3 Brokers) + Zookeeper          |
| **Arquitetura**     | Event-Driven • Clean Architecture • SOLID      |
| **Testes**          | JUnit 5 • Spring Boot Test                     |
| **Logs**            | Logback GELF → Graylog 5.1                     |
| **Build**           | Maven Wrapper (`mvnw`)                         |
| **Análise Estática**| SonarCloud                                     |
| **Segurança**       | OWASP Dependency-Check                         |
| **Containerização** | Docker • Docker Compose                        |

---

## Arquitetura

O módulo é composto pelos seguintes serviços:

- **Producer Service** → publica mensagens no tópico `mensagens`
- **Consumer A** → consome todas as mensagens
- **Consumer B** → consome todas as mensagens
- **Kafka Cluster (Broker 1, 2 e 3)** → replicação e tolerância a falhas
- **Graylog (opcional)** → logs centralizados

### Fluxo Arquitetural

```

Client → Producer API → Kafka Cluster (3 brokers / 5 partitions)
├── Consumer A
└── Consumer B

````

Ambos os consumidores utilizam **Group IDs diferentes**, garantindo consumo **independente** e processamento integral das mensagens.

---

## Estrutura do Projeto

```bash
modulo4/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml       # Build + Test + SonarCloud + OWASP + Docker Hub
│   └── release.yml
│
├── producer-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/cabos/producer_service/
│       ├── application/service/KafkaProducerService.java
│       ├── domain/MessagePayload.java
│       └── presentation/controller/ProducerController.java
│
├── consumer-a/
│   ├── Dockerfile
│   └── src/main/java/com/cabos/consumer_a/
│       ├── application/ConsumerAService.java
│       └── infrastructure/listener/MessageListener.java
│
└── consumer-b/
    ├── Dockerfile
    └── src/main/java/com/cabos/consumer_b/
        ├── application/ConsumerBService.java
        └── infrastructure/listener/MessageListener.java
````

---

## Execução Local

### 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo4.git
cd modulo4
```

### 2. Gerar os artefatos

```bash
cd producer-service && ./mvnw clean package -DskipTests
cd ../consumer-a && ./mvnw clean package -DskipTests
cd ../consumer-b && ./mvnw clean package -DskipTests
cd ..
```

### 3. Subir a arquitetura completa

```bash
docker compose up --build -d
```

### 4. Verificar containers ativos

```bash
docker ps
```

---

## Endpoints (Producer API)

### Enviar mensagem

```http
POST http://localhost:8080/producer/enviar
Content-Type: application/json
```

### Body

```json
{
  "mensagem": "Mensagem enviada pelo Producer"
}
```

### Resposta

```json
Mensagem enviada!
```

---

## Testes via Kafka CLI

### Produzir mensagens

```bash
docker run -it --network modulo4_net confluentinc/cp-kafka:7.5.1 \
  kafka-console-producer --broker-list kafka1:9092 --topic mensagens
```

### Consumir mensagens

```bash
docker run -it --network modulo4_net confluentinc/cp-kafka:7.5.1 \
  kafka-console-consumer --bootstrap-server kafka1:9092 \
  --topic mensagens --from-beginning
```

---

## Logs e Monitoramento

### Logs dos consumidores

```bash
docker logs -f consumer-a
docker logs -f consumer-b
```

Saída esperada:

```
[Consumer A] Mensagem recebida: Mensagem enviada pelo Producer
[Consumer B] Mensagem recebida: Mensagem enviada pelo Producer
```

### Graylog (opcional)

```
http://localhost:9009
```

---

## Pipeline CI/CD — GitHub Actions

O módulo conta com um pipeline completo de **DevSecOps**, estruturado em três estágios principais:

---

### 🔹 1. Build, Testes e Análise (`full-ci.yml`)

Executado em **push** e **pull request**, realiza:

* Build dos três serviços
* Testes automatizados
* Testes de integração com Kafka
* Análise estática com **SonarCloud**
* Upload de relatórios de cobertura
* Garantia da qualidade antes de qualquer merge

**Status:**

[![Full CI/CD](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml)

---

### 🔹 2. Segurança — OWASP Dependency-Check

* Scans automáticos de vulnerabilidades nos três microserviços
* Fallback inteligente caso o NVD esteja indisponível
* Upload dos relatórios como Artifact
* Conformidade com boas práticas de **DevSecOps**

---

### 🔹 3. Build & Push das Imagens Docker — Docker Hub

* Build automatizado das imagens
* Tags `latest` e por execução (`run_number`)
* Publicação dos serviços:

```
magyodev/modulo4-producer
magyodev/modulo4-consumer-a
magyodev/modulo4-consumer-b
```

---

### Tabela de Workflows

| Workflow     | Função                                          | Evento                 |
| ------------ | ----------------------------------------------- | ---------------------- |
| full-ci.yml  | Build + Testes + SonarCloud + OWASP + DockerHub | push / pull_request    |
| OWASP Scan   | Varredura de vulnerabilidades                   | Integrado ao CI        |
| Docker Build | Build e publicação de imagens Docker            | Após pipeline completo |

---

## Imagens Docker

| Serviço    | Docker Hub Repository                                                                                        |
| ---------- | ------------------------------------------------------------------------------------------------------------ |
| Producer   | [https://hub.docker.com/r/magyodev/modulo4-producer](https://hub.docker.com/r/magyodev/modulo4-producer)     |
| Consumer A | [https://hub.docker.com/r/magyodev/modulo4-consumer-a](https://hub.docker.com/r/magyodev/modulo4-consumer-a) |
| Consumer B | [https://hub.docker.com/r/magyodev/modulo4-consumer-b](https://hub.docker.com/r/magyodev/modulo4-consumer-b) |

---

## Diagrama Simplificado

```
              ┌────────────────┐
              │ Producer API   │
              └───────┬────────┘
                      │ envia
                      ▼
         ┌──────────────────────────────┐
         │     Kafka Cluster (3 brokers)│
         │  Replication + 5 partitions  │
         └─────────┬──────────┬────────┘
                   │          │
                   ▼          ▼
       ┌────────────────┐   ┌────────────────┐
       │   Consumer A   │   │   Consumer B   │
       └────────────────┘   └────────────────┘
```

---

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch: `feature/minha-melhoria`
3. Realize commits semânticos
4. Envie um Pull Request para `main`

---

## Autor

**Alan de Lima Silva (MagyoDev)**
- GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
- Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
- E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)

