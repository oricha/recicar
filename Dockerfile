# Multi-stage Dockerfile for Recicar (Spring Boot)
# Build with JDK + Gradle, run on slim JRE

# 1) Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

# Copy Gradle wrapper and build files first to leverage Docker layer cache
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./

# Make wrapper executable and optionally warm dependency cache
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null || true

# Copy the rest of the source code
COPY .. .

# Build the Spring Boot fat jar (skip tests to speed up image builds)
RUN ./gradlew clean bootJar --no-daemon -x test

# Ensure a single predictable jar name for the runtime stage
RUN cp build/libs/*-SNAPSHOT.jar app.jar || cp build/libs/*.jar app.jar

# 2) Runtime stage
FROM eclipse-temurin:17-jre

# Install necessary packages for production
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication" \
    TZ=UTC

WORKDIR /app

# Copy the built application jar from the build stage
COPY --from=build /workspace/app.jar /app/app.jar

# Create SSL certificate directory for database connections
RUN mkdir -p /app/ssl

# Create non-root user and set ownership for least-privilege runtime
RUN addgroup --system app && adduser --system --ingroup app --home /app app \
    && chown -R app:app /app
USER app

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose the HTTP port
EXPOSE 8080

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dserver.port=$SERVER_PORT -jar /app/app.jar"]
