# Build stage: using Maven and JDK to compile the application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy Maven configuration file first (to leverage Docker layer caching)
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Runtime stage: lightweight JDK image for running the app
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /build/target/Nesta-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8082

# Define the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
