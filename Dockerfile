FROM eclipse-temurin:21 AS server-builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :server:clean :server:build -x test

FROM eclipse-temurin:21 AS server-runner
WORKDIR /app
COPY --from=server-builder /app/server/build/libs/server-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21 AS client-builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :client:clean :client:build -x test

FROM eclipse-temurin:21 AS client-runner
WORKDIR /app
COPY --from=client-builder /app/client/build/libs/client-fat.jar app.jar
CMD ["java", "-jar", "app.jar"]
