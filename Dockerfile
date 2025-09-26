# Multi-stage Dockerfile for Recicar (Spring Boot)

# 1) Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

# Copy Gradle wrapper and build files first to leverage Docker layer cache
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Make wrapper executable and warm up dependency cache
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null || true

# Copy the rest of the source code
COPY src ./src

# Build the Spring Boot fat jar with layers enabled
RUN ./gradlew clean bootJar --no-daemon -x test

# Extract the layers
RUN java -Djarmode=layertools -jar build/libs/*-SNAPSHOT.jar extract

# 2) Runtime stage
FROM gcr.io/distroless/java17-debian12

WORKDIR /app

# Copy the layers from the build stage
COPY --from=build /workspace/dependencies/ ./
COPY --from=build /workspace/spring-boot-loader/ ./
COPY --from=build /workspace/snapshot-dependencies/ ./
COPY --from=build /workspace/application/ ./

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication" \
    TZ=UTC

# Create non-root user and set ownership for least-privilege runtime
USER nonroot:nonroot

# Expose the HTTP port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]