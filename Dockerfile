# Stage 1: Build your Java app using Gradle
FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Setup runtime environment with LibreOffice
FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y libreoffice
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]