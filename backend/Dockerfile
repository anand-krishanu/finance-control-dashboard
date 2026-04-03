# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy the wrapper and project config
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Resolve dependencies first to cache them
RUN ./mvnw dependency:go-offline

# Copy the source and build it
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
