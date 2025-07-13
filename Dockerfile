# Stage 1: Build your Maven project
FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Stage 2: Runtime setup with LibreOffice
FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y libreoffice
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]