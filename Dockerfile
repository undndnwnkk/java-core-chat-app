FROM eclipse-temurin:21 AS builder
WORKDIR /app
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build

FROM eclipse-temurin:21 AS runner
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 80

CMD ["java", "-jar", "app.jar"]