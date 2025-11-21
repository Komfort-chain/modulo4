# **Módulo 4 — Kafka Cluster + Producer e Consumers (Komfort Chain)**

O **Módulo 4** da suíte **Komfort Chain** apresenta um ecossistema completo de **mensageria distribuída**, formado por um **Producer**, dois **Consumers independentes** e um **cluster Kafka** com **três brokers**.
Ele demonstra, na prática, como implementar **Event-Driven Architecture**, escalabilidade horizontal, tolerância a falhas e processamento assíncrono seguro e eficiente.

Assim como nos demais módulos, toda a estrutura segue **Clean Architecture**, **SOLID**, pipelines padronizados de **CI/CD**, análise estática com **SonarCloud**, verificação de vulnerabilidades com **OWASP Dependency-Check**, monitoramento via **Graylog** e empacotamento com **Docker**.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo4\&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo4)

**Docker Hub (imagens oficiais):**
[Producer](https://hub.docker.com/r/magyodev/modulo4-producer) •
[Consumer A](https://hub.docker.com/r/magyodev/modulo4-consumer-a) •
[Consumer B](https://hub.docker.com/r/magyodev/modulo4-consumer-b)

---

## **Tecnologias Utilizadas**

| Categoria        | Ferramentas / Tecnologias                              |
| ---------------- | ------------------------------------------------------ |
| Linguagem        | Java 21                                                |
| Framework        | Spring Boot 3.5.7 • Spring Kafka                       |
| Mensageria       | Apache Kafka (Cluster com 3 brokers) + Zookeeper       |
| Logs             | Logback GELF → Graylog                                 |
| Testes           | JUnit 5 • Spring Boot Test                             |
| Build            | Maven Wrapper (mvnw)                                   |
| Análise Estática | SonarCloud                                             |
| Segurança        | OWASP Dependency-Check                                 |
| Containerização  | Docker e Docker Compose                                |
| Arquitetura      | Event-Driven Architecture • Clean Architecture • SOLID |

---

# **Arquitetura Geral**

O módulo é composto por quatro microserviços leves, cada um com responsabilidade bem definida:

* **Producer Service** → recebe requisições REST e publica mensagens no tópico `mensagens`.
* **Consumer A** → consome 100% das mensagens.
* **Consumer B** → também consome 100% das mensagens.
* **Kafka Cluster (3 brokers)** → garante replicação, tolerância a falhas e distribuição entre partições.

### **Fluxo Arquitetural**

```
Cliente → Producer API → Kafka Cluster (3 brokers, 5 partitions)
                  ├── Consumer A (grupo-a)
                  └── Consumer B (grupo-b)
```

Por usarem **Group IDs diferentes**, ambos recebem todas as mensagens, garantindo dois fluxos independentes — prática comum em sistemas de monitoramento, processamento paralelo, auditoria ou replicação de dados.

---

# **Organização das Pastas e Justificativa da Estrutura**

A estrutura segue exatamente o mesmo padrão dos módulos anteriores:

```bash
modulo4/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml
│   └── release.yml
│
├── producer-service/
├── consumer-a/
└── consumer-b/
```

A divisão por microserviço é essencial, pois cada aplicação possui:

* ciclo próprio de build,
* dependências isoladas,
* Dockerfile independente,
* pipelines individuais,
* versionamento próprio.

## **1. `producer-service/` — Serviço Publicador**

```
producer-service/
├── application/service/       # Lógica de envio de mensagens
├── domain/                    # DTO de entrada
└── presentation/controller/   # Endpoints REST
```

### **Principais arquivos**

| Arquivo                | Função                                              |
| ---------------------- | --------------------------------------------------- |
| `KafkaProducerService` | Envia mensagens ao Kafka. Isola a lógica de envio.  |
| `MessagePayload`       | Modelo de entrada enviado pela API.                 |
| `ProducerController`   | Endpoint `/producer/enviar`, ponto de entrada HTTP. |

**Por que essa divisão?**
Separa o endpoint HTTP (presentation), a regra de publicação (application) e o modelo (domain). Esse isolamento segue exatamente Clean Architecture e mantém o produtor fácil de testar e evoluir.

---

## **2. `consumer-a/` — Consumidor A**

```
consumer-a/
├── application/ConsumerAService.java
└── infrastructure/listener/MessageListener.java
```

### **MessageListener**

* escuta eventos do tópico `mensagens`;
* pertence à camada infrastructure, pois depende de Kafka;
* delega para `ConsumerAService`, que contém a lógica de negócio.

### **ConsumerAService**

* camada application;
* ponto seguro para aplicar qualquer lógica futura (persistência, transformação etc.).

---

## **3. `consumer-b/` — Consumidor B**

Estrutura idêntica ao consumer A, mantendo padronização entre microserviços:

```
consumer-b/
├── application/ConsumerBService.java
└── infrastructure/listener/MessageListener.java
```

### Por que dois consumidores?

* **Mesma mensagem, duas finalidades diferentes**.
  Ex.: um faz auditoria, outro faz análise de dados.

* Mantém o módulo didático para demonstrar padrões reais de EDA.

---

# **Por que esses nomes?**

A nomenclatura segue **consistência com os módulos anteriores**:

| Nome                        | Justificativa                                                |
| --------------------------- | ------------------------------------------------------------ |
| `producer-service`          | Microserviço dedicado a publicação de eventos.               |
| `consumer-a` / `consumer-b` | Consumidores independentes em grupos separados.              |
| `KafkaProducerService`      | Aplica SRP: uma classe = uma responsabilidade.               |
| `MessageListener`           | Nome padrão do Spring Kafka para classes reativas a eventos. |
| `MessagePayload`            | Indica dado recebido externamente antes da publicação.       |

Os nomes seguem convenções do ecossistema Spring, Kafka e Clean Architecture.

---

# **Estrutura Completa do Projeto**

```bash
modulo4/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml
│   └── release.yml
│
├── producer-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/cabos/producer_service/
│       ├── application/service/KafkaProducerService.java
│       ├── domain/MessagePayload.java
│       └── presentation/controller/ProducerController.java
│
├── consumer-a/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/cabos/consumer_a/
│       ├── application/ConsumerAService.java
│       └── infrastructure/listener/MessageListener.java
│
└── consumer-b/
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/com/cabos/consumer_b/
        ├── application/ConsumerBService.java
        └── infrastructure/listener/MessageListener.java
```

---

# **Execução Local**

## 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo4.git
cd modulo4
```

## 2. Build dos serviços

```bash
cd producer-service && ./mvnw clean package -DskipTests
cd ../consumer-a && ./mvnw clean package -DskipTests
cd ../consumer-b && ./mvnw clean package -DskipTests
cd ..
```

## 3. Subir toda a stack

```bash
docker compose up --build -d
```

---

# **Producer API — Endpoints**

### Enviar mensagem

```
POST http://localhost:8080/producer/enviar
```

### Body

```json
{
  "mensagem": "Teste do Producer"
}
```

---

# **Logs dos Consumers**

```bash
docker logs -f consumer-a
docker logs -f consumer-b
```

Saída esperada:

```
Consumer A recebeu: Teste do Producer
Consumer B recebeu: Teste do Producer
```

---

# **Pipeline CI/CD**

O módulo possui um pipeline completo composto por dois workflows:

### **1. full-ci.yml**

Executado a cada commit/pull request.
Realiza:

* build dos três microserviços
* testes unitários
* análise SonarCloud
* OWASP Dependency-Check
* build + push das imagens Docker

### **2. release.yml**

Executado ao criar uma tag `vX.Y.Z`.

* gera changelog
* cria release no GitHub
* envia artefatos
* publica imagens versionadas no Docker Hub

---

# **Imagens Oficiais Docker**

| Serviço    | Repositório                   |
| ---------- | ----------------------------- |
| Producer   | `magyodev/modulo4-producer`   |
| Consumer A | `magyodev/modulo4-consumer-a` |
| Consumer B | `magyodev/modulo4-consumer-b` |

Tags disponíveis:

* `latest`
* `${run_number}`
* `vX.Y.Z`

---

# **Diagrama Simplificado**

```
Cliente
   │
   ▼
┌────────────────┐
│ Producer API   │
└───────┬────────┘
        │ publica
        ▼
┌──────────────────────────────┐
│  Kafka Cluster (3 brokers)   │
│  5 partitions + replicação   │
└─────────┬──────────┬────────┘
          │          │
          ▼          ▼
  Consumer A     Consumer B
```

---

# **Contribuição**

1. Faça um fork
2. Crie uma branch: `feature/minha-melhoria`
3. Use commits semânticos
4. Envie um Pull Request

---

# **Autor**

**Alan de Lima Silva (MagyoDev)**
* GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
* Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
* E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)