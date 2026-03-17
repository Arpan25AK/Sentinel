# 🚀 Project Sentinel: Event-Driven Security & Notification Microservice

**Sentinel** is a highly scalable, fault-tolerant, event-driven notification microservice built with Spring Boot and Apache Kafka. It demonstrates enterprise-level backend architecture by handling asynchronous message processing, intelligent security routing, and reliable email delivery using Spring Mail and dynamic HTML Thymeleaf templates.

## ✨ Key Enterprise Features

  * **Event-Driven Architecture:** Decouples the API gateway from the heavy lifting of third-party network calls (SMTP) using an Apache Kafka message broker.
  * **Intelligent Security Routing (The Bouncer):** Dynamically routes events based on the payload. Trusted devices (`Windows`, `MacBook`, `Android`) trigger a silent audit log, while unknown devices instantly trigger a `SUSPICIOUS_LOGIN` HTML email alert.
  * **High Concurrency & Scalability:** Utilizes Spring Kafka concurrency and topic partitioning to process multiple messages simultaneously across isolated worker threads.
  * **Fault Tolerance & Reliability:** Implements a robust **Retry Mechanism** (3 attempts with a 2-second backoff) and a dynamically routed **Dead Letter Queue (DLQ)** to prevent data loss during external API (Gmail) outages.
  * **Dynamic HTML Templating:** Uses Thymeleaf to safely inject dynamic user data from Kafka payloads into beautifully styled HTML emails.
  * **Observability & API Docs:** Features out-of-the-box **Swagger UI** for interactive API testing and **Spring Boot Actuator** for live health and metric monitoring.

## 🛠️ Tech Stack

  * **Language:** Java 21
  * **Framework:** Spring Boot 3.5.x
  * **Message Broker:** Apache Kafka & Zookeeper (Dockerized)
  * **Email & Templating:** Spring Boot Mail (JavaMailSender), Thymeleaf
  * **Documentation & Monitoring:** SpringDoc OpenAPI (Swagger), Spring Boot Actuator
  * **Build Tool:** Maven

-----

## 🚦 Prerequisites

Before you begin, ensure you have the following installed:

  * [Java 21](https://adoptium.net/) or higher
  * [Docker & Docker Compose](https://www.docker.com/)
  * [Maven](https://maven.apache.org/)
  * A Gmail account with an **App Password** generated (Standard passwords will be blocked by Google).

-----

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
To enable simultaneous multi-threading (3 concurrent listeners), the `user-events` topic needs multiple partitions. Run this command inside your Kafka container:

```bash
docker exec sentinel-kafka kafka-topics --alter --topic user-events --partitions 3 --bootstrap-server localhost:9092
```

**4. Run the Application**
Start the application via your IDE or using Maven:

```bash
mvn spring-boot:run
```

-----

## 🔍 Observability & Documentation

Once the application is running on `http://localhost:8081`, you can access the enterprise dashboards:

  * **Swagger UI (Interactive API Docs):** [http://localhost:8081/swagger-ui.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui.html)
  * **Actuator Health Check:** [http://localhost:8081/actuator/health](https://www.google.com/search?q=http://localhost:8081/actuator/health)
  * **Actuator Live Metrics:** [http://localhost:8081/actuator/metrics](https://www.google.com/search?q=http://localhost:8081/actuator/metrics)

-----

## 🧪 Testing the API

You can use the built-in Swagger UI, Postman, or cURL to test the endpoints.

### 1\. User Registration (Welcome Email)

Sends a welcome HTML email to the new user.

  * **POST** `http://localhost:8081/kafka/test/signup`

<!-- end list -->

```json
{
  "username": "Arpan25",
  "email": "your_email@gmail.com",
  "deviceType": "macbook"
}
```

### 2\. Normal Login (Silent Audit Log)

If the device is a known, trusted device (`windows`, `macbook`, or `android`), the backend will safely audit the login to the console without spamming the user's email inbox.

  * **POST** `http://localhost:8081/kafka/test/login`

<!-- end list -->

```json
{
  "username": "Arpan25",
  "email": "your_email@gmail.com",
  "deviceType": "windows"
}
```

### 3\. Suspicious Login (Triggers Security Alert)

If a hacker attempts to log in with an unknown device (or a missing/empty device payload), the API acts as a bouncer, flagging it as `SUSPICIOUS_LOGIN` and instantly firing an HTML security warning to the user.

  * **POST** `http://localhost:8081/kafka/test/login`

<!-- end list -->

```json
{
  "username": "Arpan25",
  "email": "your_email@gmail.com",
  "deviceType": "Unknown Linux PC in Russia"
}
```

-----

## 🧰 Helpful Kafka Docker Commands

If you want to look under the hood of the Kafka broker while the application is running, use these commands:

**List all active topics (including the DLQ):**

```bash
docker exec sentinel-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

**Read messages parked safely in the Dead Letter Queue (DLQ):**

```bash
docker exec sentinel-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events.DLQ --from-beginning
```
