# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the POM file first to leverage Docker cache
COPY pom.xml .
COPY src /app/src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/challenge-app-*.jar /app/challenge-app.jar
COPY --from=build /app/src/main/resources/db /app/resources/db

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables (override in docker-compose or at runtime)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/customer_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=password
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Run the application
ENTRYPOINT ["java", "-jar", "challenge-app.jar"]