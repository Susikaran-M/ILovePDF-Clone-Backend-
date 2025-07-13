FROM openjdk:17-jdk
RUN apt-get update && apt-get install -y libreoffice
WORKDIR /app
COPY target/ILovePDF-Clone-Backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]