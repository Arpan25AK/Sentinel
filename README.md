# 🚀 Project Sentinel

**Sentinel** is a highly scalable, fault-tolerant, event-driven notification microservice built with Spring Boot and Apache Kafka. It demonstrates enterprise-level backend architecture, handling asynchronous message processing, intelligent routing, and reliable email delivery using Spring Mail and Thymeleaf templates.

## ✨ Key Features

* **Event-Driven Architecture:** Decouples the API gateway/producer from the heavy lifting of third-party network calls (SMTP) using Apache Kafka.
* **Intelligent Routing (Path Splitting):** Dynamically routes events based on the `eventType` payload (e.g., sending a Welcome HTML email for `SIGNUP`, and a Security Alert for `LOGIN`).
* **High Concurrency & Scalability:** Utilizes Spring Kafka concurrency and topic partitioning to process multiple messages simultaneously across isolated worker threads.
* **Fault Tolerance & Reliability:** Implements a robust **Retry Mechanism** (3 attempts with a 2-second backoff) and a dynamically routed **Dead Letter Queue (DLQ)** to prevent data loss during external API outages.
* **Dynamic HTML Templating:** Uses Thymeleaf to inject dynamic user data from Kafka payloads into beautifully styled HTML emails.

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.5.x
* **Message Broker:** Apache Kafka & Zookeeper (Dockerized)
* **Email & Templating:** Spring Boot Mail (JavaMailSender), Thymeleaf
* **Build Tool:** Maven

---

## 🚦 Prerequisites

Before you begin, ensure you have the following installed:

* [Java 21](https://adoptium.net/) or higher
* [Docker & Docker Compose](https://www.docker.com/)
* [Maven](https://maven.apache.org/)
* A Gmail account with an **App Password** generated (Standard passwords will be blocked by Google).

---

## ⚙️ Local Setup & Installation

**1. Start the Kafka Cluster**
Navigate to the root directory containing the `docker-compose.yml` file and start the Kafka and Zookeeper containers:

```bash
docker compose up -d

```

**2. Configure Environment Variables**
To keep credentials secure, this project uses environment variables. Before running the Spring Boot application, set the following variables in your IDE (e.g., IntelliJ Run Configuration) or system environment:

* `MAIL_USERNAME`: Your full Gmail address (e.g., `youremail@gmail.com`)
* `MAIL_PASSWORD`: Your 16-character Google App Password

**3. Optimize Kafka Partitions (For Concurrency)**
To enable simultaneous multi-threading, the `user-events` topic needs multiple partitions. Run this command to configure the topic for 3 concurrent lanes:

```bash
docker exec sentinel-kafka kafka-topics --alter --topic user-events --partitions 3 --bootstrap-server localhost:9092

```

**4. Run the Application**
Start the application via your IDE or using Maven:

```bash
mvn spring-boot:run

```

---

## 🧪 Testing the API

You can use Postman, Insomnia, or cURL to test the endpoints running on `http://localhost:8081`.

### 1. Standard Event Trigger

Sends a single notification event to the Kafka queue.

**POST** `/api/test/send`

```json
{
  "username": "johndoe",
  "email": "johndoe@example.com",
  "eventType": "SIGNUP"
}

```

*(Change `eventType` to `"LOGIN"` to trigger the security routing path!)*

### 2. The Concurrency "Burst" Test

Fires 15 events into the Kafka broker in a single millisecond using partition keys to demonstrate load distribution and concurrent multi-threading across worker nodes.

**POST** `/api/test/burst`

```json
{
  "username": "johndoe",
  "email": "johndoe@example.com",
  "eventType": "SIGNUP"
}

```

---

## 🧰 Helpful Kafka Docker Commands

If you want to look under the hood of the Kafka broker while the application is running, use these commands:

**List all active topics (including the DLQ):**

```bash
docker exec sentinel-kafka kafka-topics --bootstrap-server localhost:9092 --list

```

**Read messages parked in the Dead Letter Queue (DLQ):**

```bash
docker exec sentinel-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events.DLQ --from-beginning

```
