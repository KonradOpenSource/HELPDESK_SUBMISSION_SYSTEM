FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar
COPY target/helpdesk-backend-1.0.0.jar app.jar

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -g 1001 appuser && useradd -r -u 1001 -g appuser appuser

# Create directories for logs and uploads
RUN mkdir -p /app/logs /app/uploads && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
