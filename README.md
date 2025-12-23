# TCP Chat App

Hey, this is my Java Core project for learning TCP sockets and client-server stuff. Made it during late-night coding session. Works fine!

## What it does
- TCP chat over sockets (blocking IO)
- Login/register with password hashing
- Send messages `/msg hello`, see history `/history`
- Change user `/change_profile`
- Pure Java 17, no Spring, just core

## How to run (Java)
```bash
git clone <repo>
cd java-core-chat-app
./gradlew build

# Terminal 1
./gradlew :server:run

# Terminal 2  
./gradlew :client:run
```

Login -> type `/msg hi` -> `/history` -> `/exit`

## Docker (if you want)
```bash
# Server
docker build --target server-runner -t tcp-chat-server .
docker run -p 8080:8080 tcp-chat-server

# Client  
docker build --target client-runner -t tcp-chat-client .
docker run -it --network host tcp-chat-client
```

## Project structure
```
.
├── common/     # models, DTOs
├── client/     # ClientApp.java
├── server/     # ServerApp.java  
└── Dockerfile  # multi-stage build
```

## Tech
- Java 17 Core
- Gradle multi-module
- Gson for JSON
- TCP sockets
- SHA256 password hash
- In-memory storage (users/messages)

## Logs look like
```
[main] INFO SERVER - Server started on port 8080
[Thread-1] INFO AUTH - LOGIN user123 success
[Thread-1] INFO SERVICE - user123: "hello world"
```

## TODO
- NIO for 10k connections
- Real DB
- Broadcast messages to all users

**For Java interviews - shows sockets + JSON protocol + auth** 🚀
