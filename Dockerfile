FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/Nesta-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
