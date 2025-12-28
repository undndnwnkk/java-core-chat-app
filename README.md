# TCP Chat Application: A Java Core Odyssey

A high-performance, multi-threaded chat system built to master the depths of Java Core. This project covers everything from basic Socket I/O to Java 21 Virtual Threads and containerized PostgreSQL persistence.

## Core Features

*   **Asynchronous Communication:** Bi-directional messaging using a dedicated reader thread on the client side to prevent UI blocking during input.
*   **Virtual Threads (Project Loom):** The server utilizes Java 21's `VirtualThreadPerTaskExecutor`, allowing it to handle thousands of concurrent connections with minimal overhead.
*   **Persistence Layer:** PostgreSQL integration via JDBC and HikariCP connection pooling.
*   **Security:** Passwords are never stored in plain text. Implementation uses BCrypt with salt.
*   **Protocol:** Structured JSON-based request/response exchange using Google GSON.
*   **Containerization:** Full Docker Compose orchestration for the server and database.

## Tech Stack

*   **Language:** Java 21 (LTS)
*   **Concurrency:** Virtual Threads, Concurrent Collections, Atomic Variables.
*   **Database:** PostgreSQL 16.
*   **Tools:** Gradle, HikariCP, GSON, BCrypt.
*   **Testing:** JUnit 5, Mockito (Unit testing the service layer).
*   **DevOps:** Docker, Multi-stage Dockerfiles.

## Project Evolution (Release History)

### v0.1.0 — The "Hello World" Era
*   Basic Blocking I/O implementation.
*   Single-threaded server (one client at a time, very lonely).

### v0.2.0 — The Multithreaded Breakthrough
*   Transitioned to Platform Threads (`new Thread()`).
*   Implemented Server Broadcast logic: messages from one client are sent to all connected users.
*   Introduced thread-safe collections to prevent the infamous `ConcurrentModificationException`.

### v0.3.0 — Infrastructure & JSON
*   Moved logic from hardcoded strings to JSON objects.
*   Separated the project into clear Service/Repository layers.
*   Client-side refactoring to allow simultaneous reading and writing.

### v1.0.0 — The Final Boss (Current)
*   **Virtual Threads:** Replaced heavy platform threads with lightweight virtual threads.
*   **Persistence:** Replaced `InMemory` storage with PostgreSQL.
*   **Dockerized:** Implemented `docker-compose.yml` for seamless deployment.
*   **Graceful Shutdown:** Implemented Runtime Shutdown Hooks to notify clients before the server goes offline.

## Architecture

1.  **Model Layer:** POJOs for Users, Messages, and Requests.
2.  **Repository Layer:** JDBC-based persistence with SQL Injection protection.
3.  **Service Layer:** Business logic (Authentication, Session management, Message processing).
4.  **Application Layer:** Socket handling and thread management.

## Setup and Running

### Prerequisites
*   Docker & Docker Desktop installed.
*   Java 21 JDK (if running outside Docker).

### Running the Server
The server and database are orchestrated via Docker Compose:
```bash
docker-compose up --build
```
The database will automatically initialize schemas via `init.sql` on the first run.

### Running the Client
The client should be run in interactive mode. Open multiple terminal windows to simulate different users:
```bash
docker build --target client-runner -t tcp-chat-client .
docker run -it --network host tcp-chat-client
```

## Testing
Run the unit test suite (Service layer mocks) via Gradle:
```bash
./gradlew test
```
---
*Note: No race conditions were harmed in the making of this project (mostly).*