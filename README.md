# 🚀 Incident Management System (IMS)

A production-style backend system for real-time incident processing using Spring Boot, RabbitMQ, MongoDB, caching, and rate limiting.

---

# 📌 Overview

This system ingests signals, processes them asynchronously, groups them into incidents, and displays them in a fast UI.

---

# 🏗️ Architecture

                ┌──────────────────────┐
                │      Client/UI       │
                │  (HTMX Dashboard)    │
                └─────────┬────────────┘
                          │ HTTP
                          ▼
                ┌──────────────────────┐
                │   Spring Boot API    │
                │   /signals endpoint  │
                └─────────┬────────────┘
                          │
                          ▼
                ┌──────────────────────┐
                │   Rate Limiter       │
                │     (Bucket4j)       │
                └─────────┬────────────┘
                          │
                          ▼
                ┌──────────────────────┐
                │    RabbitMQ Queue    │
                │   (signal.queue)     │
                └─────────┬────────────┘
                          │
                          ▼
        ┌──────────────────────────────────────┐
        │        Signal Consumer               │
        │ (@RabbitListener + Prefetch)        │
        └─────────┬──────────────┬────────────┘
                  │              │
                  ▼              ▼
        ┌────────────────┐   ┌────────────────────┐
        │ Debounce Logic │   │ Alert Strategy     │
        │ (10s window)   │   │ P1/P2/P3 handling  │
        └─────────┬──────┘   └─────────┬──────────┘
                  │                    │
                  ▼                    ▼
        ┌────────────────┐   ┌────────────────────┐
        │   MongoDB      │   │ Alert Output       │
        │ work_items     │   │ PagerDuty / Email  │
        │ signals_raw    │   │ Logs               │
        └─────────┬──────┘   └────────────────────┘
                  │
                  ▼
        ┌──────────────────────┐
        │   Cache Layer        │
        │ (In-Memory Map)      │
        └─────────┬────────────┘
                  │
                  ▼
        ┌──────────────────────┐
        │    UI Controller     │
        │  (Cache First Read)  │
        └─────────┬────────────┘
                  │
                  ▼
        ┌──────────────────────┐
        │   HTMX Frontend UI   │
        │  (Real-time updates) │
        └──────────────────────┘

---

# 🎯 Key Features

🚀 High-throughput signal ingestion
⚡ Async processing (RabbitMQ)
🔁 Debounce logic (incident grouping)
🧠 Strategy pattern (severity-based alerts)
⚡ Cache-first UI (fast reads)
🛑 Rate limiting (Bucket4j)
📊 Incident lifecycle (RCA + MTTR)
🔥 Backpressure handling
🐳 Dockerized setup

---

# 🧰 Tech Stack

## Backend
    Java 21
    Spring Boot
    Spring Data MongoDB
    Spring AMQP (RabbitMQ)
## Database
    MongoDB
## Messaging
    RabbitMQ
## Caching
    In-memory (ConcurrentHashMap)
## Frontend
    HTML + HTMX
## DevOps
    Docker
    Docker Compose
## Testing
    JUnit 5

---

# ⚙️ Core Concepts

## 1️⃣ Rate Limiting

Limits incoming requests to prevent overload.

## 2️⃣ Async Queue

RabbitMQ decouples API from processing.

## 3️⃣ Debounce Logic

Groups repeated signals into one WorkItem (10 sec window).

## 4️⃣ Cache Layer

UI reads from cache → avoids DB load.

## 5️⃣ Strategy Pattern
Severity	Action
🔴 P1	    PagerDuty
🟠 P2	    Email
🟢 P3	    Log
## 6️⃣ Backpressure

Implemented at multiple levels:

API → Rate limiter
Queue → RabbitMQ buffer
Consumer → Prefetch control
Processing → Debounce

---

# 🚀 Setup Guide (From Scratch)

## 🔧 Prerequisites
Install:

    Java 21
    Maven
    Docker & Docker Compose

## 📥 Clone Project
    git clone <your-repo-url> 
    cd ims-backend

## 🐳 Run with Docker
    docker-compose down -v 
    docker-compose up --build

## 🌐 Access Services
    Service	        URL
    Backend	        http://localhost:8080
    RabbitMQ UI	    http://localhost:15672
    MongoDB	        mongodb://localhost:27017

---

# 🧪 Testing the System

## 🔹 Send Signal
```
curl -X POST http://localhost:8080/signals \ -H "Content-Type: application/json" \ -d '{ "componentId":"PAYMENT", "severity":"P1", "message":"Failure", "timestamp":"2026-05-01T10:00:00" }'
```
## 🔹 Load Test (Backpressure)

```
for i in {1..50}; do 
curl -X POST http://localhost:8080/signals \ 
-H "Content-Type: application/json" \ 
-d "{ 
    \"componentId\":\"TEST\", 
    \"severity\":\"P1\", 
    \"message\":\"Load test\", 
    \"timestamp\":\"$(date -u +"%Y-%m-%dT%H:%M:%S")\" 
}" & 
done
```

## 🔹 Expected Output
    200 200 200 ... 
    429 429 ...

# MongoDB Verification

    ```
    docker exec -it mongodb mongosh  
    use imsdb 
    show collections 
    db.work_items.find().pretty() 
    db.signals_raw.find().pretty()
    ```

# 🎨 UI
## Open:

    ```
    http://localhost:8080
    ```

## Features:
    Incident list
    RCA submission
    Real-time updates

# 🧪 Run Unit Tests

```
    ./mvnw test
```
---

# 👨‍💻 Author
MD SHADAB ALAM

# ⭐ Conclusion
    
This project demonstrates:

    - Scalable backend design
    - Real-world architecture
    - Performance optimization
    - Fault tolerance
    - Clean code practices

## 🔥 This is not a CRUD app — this is a production-grade system.
