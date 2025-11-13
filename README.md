# **Módulo 4 — Kafka Cluster + Producer/Consumers (Komfort Chain)**

O **Módulo 4** da suíte **Komfort Chain** implementa uma arquitetura completa de **mensageria distribuída**, utilizando um **cluster Kafka com três brokers**, um **serviço produtor** e **dois consumidores independentes**.

O módulo demonstra, na prática, os fundamentos de **event-driven architecture**, **processamento assíncrono**, **alta disponibilidade** e **escalabilidade horizontal**, garantindo que todas as mensagens publicadas pelo produtor sejam consumidas simultaneamente pelas duas aplicações consumidoras.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo4/actions/workflows/full-ci.yml)
[![Producer — SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo4_producer\&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Komfort-chain_modulo4_producer)
[![Consumer A — SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo4_consumerA\&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Komfort-chain_modulo4_consumerA)
[![Consumer B — SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo4_consumerB\&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Komfort-chain_modulo4_consumerB)
![Java 21](https://img.shields.io/badge/Java-21-red)
![Spring Boot 3.5.7](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)
![Kafka](https://img.shields.io/badge/Apache_Kafka-7.5.1-black)

---

## **Tecnologias Utilizadas**

| Categoria             | Tecnologias / Ferramentas                 |
| --------------------- | ----------------------------------------- |
| **Linguagem**         | Java 21                                   |
| **Frameworks**        | Spring Boot 3.5.7 • Spring Kafka          |
| **Mensageria**        | Kafka Cluster (3 Brokers) + Zookeeper     |
| **Log/Monitoramento** | Logback GELF → Graylog 5.1                |
| **Build**             | Maven Wrapper (`mvnw`)                    |
| **Testes**            | JUnit 5 + Spring Boot Test                |
| **Análise Estática**  | SonarCloud + OWASP Dependency-Check       |
| **Containerização**   | Docker • Docker Compose                   |
| **Arquitetura**       | Event-Driven • Clean Architecture • SOLID |

---

## **Arquitetura**

O Módulo 4 é composto por:

* **Producer Service** → publica mensagens no tópico `mensagens`
* **Consumer A** → consome todas as mensagens
* **Consumer B** → consome todas as mensagens
* **Kafka Cluster** → 3 brokers com replicação e 5 partições
* **Graylog** → visualização unificada dos logs
* **SonarQube** (opcional) → análise estática local

### Fluxo Arquitetural

```
Client → Producer API → Kafka (3 brokers, 5 partitions)
                              ├── Consumer A
                              └── Consumer B
```

Cada consumidor pertence a **grupos distintos**, garantindo que ambos processem todas as mensagens.

---

## **Estrutura do Projeto**

```bash
modulo4/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml     # Build, testes, análise, OWASP e Docker Hub
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
```

---

## **Execução Local**

### **1. Clonar o repositório**

```bash
git clone https://github.com/Komfort-chain/modulo4.git
cd modulo4
```

### **2. Construir os artefatos**

```bash
cd producer-service && ./mvnw clean package -DskipTests
cd ../consumer-a && ./mvnw clean package -DskipTests
cd ../consumer-b && ./mvnw clean package -DskipTests
cd ..
```

### **3. Subir toda a arquitetura**

```bash
docker compose up --build -d
```

### **4. Verificar containers ativos**

```bash
docker ps
```

**Serviços esperados:**

| Serviço   | Porta | Descrição                        |
| --------- | ----- | -------------------------------- |
| Producer  | 8080  | API REST para publicar mensagens |
| Kafka 1   | 9092  | Broker principal                 |
| Kafka 2   | 9093  | Broker secundário                |
| Kafka 3   | 9094  | Broker secundário                |
| Graylog   | 9009  | Logs centralizados               |
| Zookeeper | 2181  | Coordenação do cluster Kafka     |

---

## **Endpoints (Producer API)**

### **Enviar mensagem**

```http
POST http://localhost:8080/producer/enviar
Content-Type: application/json
```

### **Body**

```json
{
  "mensagem": "Mensagem enviada pelo Producer"
}
```

### **Resposta**

```json
Mensagem enviada!
```

---

## **Testes via Kafka CLI**

O módulo fornece testes manuais usando Kafka CLI sem precisar acessar containers internos.

### **Produzir mensagens**

```bash
docker run -it --network modulo4_net confluentinc/cp-kafka:7.5.1 \
  kafka-console-producer --broker-list kafka1:9092 --topic mensagens
```

### **Consumir mensagens**

```bash
docker run -it --network modulo4_net confluentinc/cp-kafka:7.5.1 \
  kafka-console-consumer --bootstrap-server kafka1:9092 \
  --topic mensagens --from-beginning
```

Ideal para demonstrar o funcionamento independente dos consumers.

---

## **Logs e Monitoramento**

### **Visualizar logs dos consumidores**

```bash
docker logs -f consumer-a
docker logs -f consumer-b
```

### **Saída esperada**

```
[Consumer A] Mensagem recebida: Mensagem enviada pelo Producer
[Consumer B] Mensagem recebida: Mensagem enviada pelo Producer
```

### **Graylog (opcional)**

Acesse:

```
http://localhost:9009
```

Todos os serviços enviam logs via GELF.

---

## **Pipeline CI/CD**

O workflow `full-ci.yml` executa:

1. Build + testes dos três microserviços
2. Testes de integração com Kafka (service container)
3. Análise estática com **SonarCloud**
4. Varredura OWASP Dependency-Check
5. Build e push das imagens Docker para o Docker Hub

---

## **Imagens Docker (Docker Hub)**

| Serviço    | Repositório Docker Hub        |
| ---------- | ----------------------------- |
| Producer   | `magyodev/modulo4-producer`   |
| Consumer A | `magyodev/modulo4-consumer-a` |
| Consumer B | `magyodev/modulo4-consumer-b` |

Versionamento automático:

* `latest`
* `<run_number>`

---

## **Diagrama Simplificado**

```
               ┌────────────────┐
               │ Producer API   │
               └───────┬────────┘
                       │ envia
                       ▼
            ┌────────────────────────┐
            │   Kafka Cluster (3x)   │
            │  5 partitions / HA     │
            └───────┬────────┬──────┘
                    │        │
                    ▼        ▼
         ┌────────────────┐  ┌────────────────┐
         │  Consumer A    │  │  Consumer B    │
         └────────────────┘  └────────────────┘
```

---

## **Contribuição**

1. Faça um **fork** do repositório
2. Crie uma branch: `feature/nova-funcionalidade`
3. Realize os commits (semânticos)
4. Abra um Pull Request para a branch `main`

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**
- [GitHub](https://github.com/MagyoDev) 
- [Docker Hub](https://hub.docker.com/u/magyodev) 
- [E-mail](mailto:magyodev@gmail.com)

