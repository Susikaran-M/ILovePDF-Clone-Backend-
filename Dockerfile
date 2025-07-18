#Building Maven project
FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

#Runtime setup with LibreOffice + Ghostscript
FROM openjdk:17-jdk-slim

# 🛠️ Install LibreOffice and Ghostscript
RUN apt-get update && apt-get install -y \
    libreoffice \
    ghostscript \
 && apt-get clean

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]