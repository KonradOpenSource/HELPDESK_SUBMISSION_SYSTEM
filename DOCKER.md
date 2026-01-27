# 🐳 DOCKER - COMPLETE CONFIGURATION

## 📋 Overview

Complete Docker configuration for the Helpdesk system with:

- **PostgreSQL 15** - Database
- **Spring Boot Backend** - REST API
- **Angular Frontend** - Web application
- **Nginx** - Reverse proxy
- **Docker Network** - Inter-container communication
- **Volumes** - Persistent data

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (Angular)     │◄──►│  (Spring Boot)  │◄──►│   (Database)    │
│   Port: 4200    │    │   Port: 8080    │    │   Port: 5432    │
│   Nginx         │    │   Java 21       │    │   Alpine Linux  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Helpdesk Network│
                    │  172.20.0.0/16  │
                    └─────────────────┘
```

## 📁 File Structure

```
├── .env                           # Environment variables
├── .dockerignore                  # Files ignored by Docker
├── docker-compose.yml             # Main Docker Compose configuration
├── backend/
│   ├── Dockerfile                 # Backend configuration
│   └── src/main/resources/
│       └── application-docker.yml # Spring configuration for Docker
├── frontend/
│   ├── Dockerfile                 # Frontend configuration
│   └── nginx.conf                 # Nginx configuration
├── database/
│   ├── init.sql                   # Database initialization script
│   └── init-data/                 # Initial data
├── scripts/
│   ├── docker-start.sh            # Startup script
│   ├── docker-stop.sh             # Stop script
│   └── docker-logs.sh            # Logs script
└── logs/                          # Application logs
```

## ⚙️ Configuration

### **1. Environment Variables (.env)**

```bash
# Database Configuration
POSTGRES_DB=helpdesk
POSTGRES_USER=helpdesk
POSTGRES_PASSWORD=helpdesk123
POSTGRES_HOST=postgres
POSTGRES_PORT=5432

# Backend Configuration
BACKEND_PORT=8080
BACKEND_HOST=0.0.0.0
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/helpdesk
SPRING_DATASOURCE_USERNAME=helpdesk
SPRING_DATASOURCE_PASSWORD=helpdesk123
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000

# Frontend Configuration
FRONTEND_PORT=4200
FRONTEND_HOST=0.0.0.0

# Network Configuration
NETWORK_NAME=helpdesk-network

# Volume Configuration
POSTGRES_VOLUME_NAME=postgres_data
```

### **2. Docker Compose (docker-compose.yml)**

```yaml
version: "3.8"

services:
  postgres:
    image: postgres:15-alpine
    container_name: helpdesk-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "${POSTGRES_PORT}:5432"
    volumes:
      - ${POSTGRES_VOLUME_NAME}:/var/lib/postgresql/data
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    networks:
      - ${NETWORK_NAME}
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
      target: production
    container_name: helpdesk-backend
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
      SERVER_PORT: ${BACKEND_PORT}
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "${BACKEND_PORT}:${BACKEND_PORT}"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - ${NETWORK_NAME}
    healthcheck:
      test:
        [
          "CMD",
          "curl",
          "-f",
          "http://localhost:${BACKEND_PORT}/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      target: production
    container_name: helpdesk-frontend
    restart: unless-stopped
    environment:
      NODE_ENV: production
      API_URL: http://localhost:${BACKEND_PORT}/api
    ports:
      - "${FRONTEND_PORT}:80"
    depends_on:
      backend:
        condition: service_healthy
    networks:
      - ${NETWORK_NAME}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  ${POSTGRES_VOLUME_NAME}:
    driver: local
    name: ${POSTGRES_VOLUME_NAME}
  backend-uploads:
    driver: local
    name: helpdesk-uploads

networks:
  ${NETWORK_NAME}:
    driver: bridge
    name: ${NETWORK_NAME}
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### **3. Backend Dockerfile**

```dockerfile
# Build stage
FROM openjdk:21-jdk-slim as build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Production stage
FROM openjdk:21-jre-slim as production

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/helpdesk-backend-1.0.0.jar app.jar

# Create directories for logs and uploads
RUN mkdir -p /app/logs /app/uploads && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
```

### **4. Frontend Dockerfile**

```dockerfile
# Build stage
FROM node:18-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine as production

# Install curl for health checks
RUN apk add --no-cache curl

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built application
COPY --from=build /app/dist/helpdesk-frontend /usr/share/nginx/html

# Create non-root user
RUN addgroup -g 1001 -S nginx && \
    adduser -S -D -H -u 1001 -h /var/cache/nginx -s /sbin/nologin -G nginx -g nginx nginx

# Change ownership of nginx directories
RUN chown -R nginx:nginx /var/cache/nginx /var/log/nginx /etc/nginx/conf.d
RUN chown -R nginx:nginx /usr/share/nginx/html

USER nginx

EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:80 || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

### **5. Nginx Configuration**

```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    server {
        listen       80;
        server_name  localhost;

        # Frontend static files
        location / {
            root   /usr/share/nginx/html;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;

            # Cache static assets
            location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
                expires 1y;
                add_header Cache-Control "public, immutable";
            }
        }

        # Backend API proxy
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # CORS headers
            add_header Access-Control-Allow-Origin *;
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization";

            # Handle preflight requests
            if ($request_method = 'OPTIONS') {
                add_header Access-Control-Allow-Origin *;
                add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
                add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization";
                add_header Access-Control-Max-Age 1728000;
                add_header Content-Type 'text/plain; charset=utf-8';
                add_header Content-Length 0;
                return 204;
            }
        }

        # Health check endpoint
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
}
```

## 🚀 Management Scripts

### **1. System Start**

```bash
# Run the entire system
./scripts/docker-start.sh

# Or manually
docker-compose up -d
```

### **2. System Stop**

```bash
# Stop the system
./scripts/docker-stop.sh

# Stop and remove volumes
./scripts/docker-stop.sh --remove-volumes

# Stop and remove everything
./scripts/docker-stop.sh --remove-volumes --remove-images
```

### **3. Logs**

```bash
# Show all logs
./scripts/docker-logs.sh

# Follow logs live
./scripts/docker-logs.sh -f

# Specific service logs
./scripts/docker-logs.sh backend -f

# Last 50 lines of logs
./scripts/docker-logs.sh --tail 50

# Logs from last hour
./scripts/docker-logs.sh --since 1h
```

## 🔧 Management

### **Health Checks**

```bash
# Check container status
docker-compose ps

# Check service health
curl http://localhost:4200/health
curl http://localhost:8080/actuator/health
```

### **Volumes**

```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect postgres_data

# Backup volume
docker run --rm -v postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz -C /data .
```

### **Network**

```bash
# List networks
docker network ls

# Inspect network
docker network inspect helpdesk-network

# Connect to container
docker exec -it helpdesk-postgres psql -U helpdesk -d helpdesk
```

## 📊 Monitoring

### **Resource Usage**

```bash
# Container resource usage
docker stats

# Detailed container information
docker inspect helpdesk-backend
```

### **Logs**

```bash
# System logs
docker-compose logs

# Logs from last 24h
docker-compose logs --since 24h

# Backend logs
docker-compose logs backend
```

## 🛠️ Troubleshooting

### **Common Issues**

1. **Port conflicts**

   ```bash
   # Check occupied ports
   lsof -i :8080
   lsof -i :4200
   lsof -i :5432
   ```

2. **Container not starting**

   ```bash
   # Check logs
   docker-compose logs [service_name]

   # Check status
   docker-compose ps -a
   ```

3. **Database connection issues**

   ```bash
   # Check database connection
   docker exec -it helpdesk-postgres psql -U helpdesk -d helpdesk -c "SELECT version();"
   ```

4. **Memory issues**
   ```bash
   # Check memory usage
   docker stats --no-stream
   ```

### **Debug Mode**

```bash
# Run with additional logs
docker-compose up --build

# Run single service
docker-compose up backend

# Run with debug mode
docker-compose -f docker-compose.debug.yml up
```

## 🔄 Updates

### **Update Images**

```bash
# Download latest images
docker-compose pull

# Rebuild local images
docker-compose build --no-cache

# Run with new images
docker-compose up -d
```

### **Backup and Restore**

```bash
# Database backup
docker exec helpdesk-postgres pg_dump -U helpdesk helpdesk > backup.sql

# Database restore
docker exec -i helpdesk-postgres psql -U helpdesk helpdesk < backup.sql

# Full system backup
docker run --rm -v helpdesk-postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/full-backup.tar.gz -C /data .
```

## 🌐 System Access

After startup, the system will be available at:

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Backend Health**: http://localhost:8080/actuator/health
- **PostgreSQL**: localhost:5432

## 🔒 Security

### **Security Best Practices**

1. **Non-root users** - All containers run as non-root users
2. **Health checks** - Service status monitoring
3. **Network isolation** - Dedicated Docker network
4. **Volume encryption** - Volume encryption (optional)
5. **Resource limits** - Resource limits (optional)

### **Environment Variables**

- Passwords are stored in `.env` (not in repository)
- JWT secret is unique and sufficiently long
- Dedicated database users are used

---

**Docker provides complete, isolated, and easy-to-manage infrastructure for the Helpdesk system!** 🐳
