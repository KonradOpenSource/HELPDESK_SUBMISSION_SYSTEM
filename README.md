# Helpdesk Ticket System

The Helpdesk Ticket System is a comprehensive solution for managing customer support requests, designed to streamline and optimize support operations for businesses of any size. It addresses common challenges such as lost inquiries, slow response times, and lack of accountability by centralizing all customer requests in one place. Through automated ticket assignment, real‑time tracking, and clear performance reporting, the system enables efficient resource allocation, ensures SLA compliance, and significantly improves customer satisfaction by accelerating response and resolution times.


💻 Tech Stack:



Angular_Design is developed using following technologies:



![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)   ![SASS](https://img.shields.io/badge/SASS-hotpink.svg?style=for-the-badge&logo=SASS&logoColor=white) ![Angular](https://img.shields.io/badge/angular-%23DD0031.svg?style=for-the-badge&logo=angular&logoColor=white) ![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)  ![RxJS](https://img.shields.io/badge/rxjs-%23B7178C.svg?style=for-the-badge&logo=reactivex&logoColor=white) Angular Material   ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)


Testing:

![Vitest](https://img.shields.io/badge/-Vitest-252529?style=for-the-badge&logo=vitest&logoColor=FCC72B)
[![](https://visitcount.itsvg.in/api?id=ggggf&icon=0&color=0)](https://visitcount.itsvg.in)
JUnit 



## 🎯 Problem Solved

**Business Challenge:** Companies struggle with managing customer support requests efficiently, leading to:

- Lost customer inquiries
- Poor response times
- Lack of accountability
- Inefficient resource allocation
- No performance metrics

**Our Solution:** A centralized ticket management system that:

- Captures all customer requests in one place
- Automates ticket assignment and prioritization
- Provides real-time tracking and reporting
- Enables SLA compliance monitoring
- Improves customer satisfaction through faster response times

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (Angular)     │◄──►│  (Spring Boot)  │◄──►│   (Database)    │
│   Port: 4200    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Nginx Proxy   │
                    │   Port: 80/443  │
                    └─────────────────┘
```

## 💻 Technology Stack

### Frontend

- **Angular 17+** - Modern web framework
- **Angular Material** - UI component library
- **TypeScript 5.x** - Type-safe JavaScript
- **RxJS** - Reactive programming
- **SCSS** - CSS preprocessing

### Backend

- **Java 21** - Latest LTS version
- **Spring Boot 3.x** - Application framework
- **Spring Security 6.x** - Authentication & authorization
- **Spring Data JPA** - Database access
- **JWT** - Token-based authentication
- **Maven** - Build management

### Database

- **PostgreSQL 15** - Primary database
- **Flyway** - Database migrations

### Infrastructure

- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Reverse proxy

## 👥 User Roles & Access Control

### Default Accounts

| Role  | Username | Password   | Access Level            |
| ----- | -------- | ---------- | ----------------------- |
| Admin | `admin`  | `admin123` | Full system access      |
| Agent | `agent`  | `agent123` | Ticket management       |
| User  | `user`   | `user123`  | Create/view own tickets |

### Role Capabilities

**👤 USER (Customer)**

- Create new support tickets
- View and comment on own tickets
- Attach files to tickets
- Track ticket status

**🎯 AGENT (Support Staff)**

- View and manage all tickets
- Assign tickets to agents
- Update ticket status and priority
- Add internal notes and comments
- Access performance reports

**👑 ADMIN (Administrator)**

- Full system administration
- User and role management
- Category and priority configuration
- System monitoring and reports
- Database management

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 18+ (for development)
- Java 21+ (for development)
- Maven 3.8+ (for development)

### Production Deployment (Docker)

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd helpdesk-system
   ```

2. **Start the system**

   ```bash
   # Step 1: Start PostgreSQL database
   docker run -d --name helpdesk-postgres \
     -e POSTGRES_DB=helpdesk \
     -e POSTGRES_USER=helpdesk \
     -e POSTGRES_PASSWORD=helpdesk123 \
     -p 5432:5432 \
     postgres:15-alpine

   # Step 2: Start Backend (in new terminal)
   cd backend
   mvn spring-boot:run

   # Step 3: Start Frontend (in another terminal)
   cd frontend
   npm install
   ng serve
   ```

3. **Access the application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080
   - Admin Dashboard: http://localhost:4200/admin

### Development Setup

1. **Start Database**

   ```bash
   docker run -d --name helpdesk-postgres \
     -e POSTGRES_DB=helpdesk \
     -e POSTGRES_USER=helpdesk \
     -e POSTGRES_PASSWORD=helpdesk123 \
     -p 5432:5432 \
     postgres:15-alpine
   ```

2. **Start Backend**

   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   ng serve
   ```

## 🧪 Testing

### Backend Tests

```bash
cd backend

# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest
```

**Test Coverage Areas:**

- User authentication and registration
- Ticket CRUD operations
- Role-based access control
- JWT token validation
- Database operations

### Frontend Tests

```bash
cd frontend

# Run unit tests
ng test

# Run with coverage
ng test --code-coverage

# Run end-to-end tests
ng e2e
```

**Test Coverage Areas:**

- Component rendering
- Form validation
- Authentication flows
- API integration
- User interactions

## 📊 Key Features

### Ticket Management

- **Multi-channel ticket creation** (web, email, API)
- **Automatic ticket assignment** based on skills and workload
- **Priority management** (Low, Medium, High, Critical)
- **SLA tracking** with automated escalations
- **File attachments** with virus scanning
- **Internal notes** and customer communications

### Workflow Automation

- **Smart routing** to appropriate departments
- **Escalation rules** for overdue tickets
- **Automated notifications** via email and SMS
- **Template responses** for common issues
- **Time tracking** and performance metrics

### Reporting & Analytics

- **Real-time dashboard** with key metrics
- **Custom reports** and data export
- **SLA compliance** monitoring
- **Agent performance** tracking
- **Customer satisfaction** surveys

### Security & Compliance

- **Role-based access control** (RBAC)
- **Two-factor authentication** (2FA)
- **Data encryption** at rest and in transit
- **Audit logging** for all activities
- **GDPR compliance** features

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the root directory:

```bash
# Database Configuration
POSTGRES_DB=helpdesk
POSTGRES_USER=helpdesk
POSTGRES_PASSWORD=helpdesk123

# Backend Configuration
BACKEND_PORT=8080
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Frontend Configuration
FRONTEND_PORT=4200
API_URL=http://localhost:8080
```

### Database Setup

The system uses Flyway for automatic database migrations:

```bash
# View migrations
cd backend/src/main/resources/db/migration

# Run migrations manually
mvn flyway:migrate
```

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
DELETE /api/auth/logout
```

### Ticket Endpoints

```http
GET    /api/tickets
POST   /api/tickets
GET    /api/tickets/{id}
PUT    /api/tickets/{id}
DELETE /api/tickets/{id}
POST   /api/tickets/{id}/comments
```

### Admin Endpoints

```http
GET    /api/admin/users
POST   /api/admin/users
PUT    /api/admin/users/{id}
GET    /api/admin/categories
POST   /api/admin/categories
```

## 🛠️ Troubleshooting

### Common Issues

**Port conflicts**

```bash
# Check occupied ports
lsof -i :8080
lsof -i :4200
lsof -i :5432
```

**Database connection issues**

```bash
# Test database connection
docker exec -it helpdesk-postgres psql -U helpdesk -d helpdesk
```

**Build failures**

```bash
# Clean and rebuild
mvn clean install
npm clean install
```

### Logs

```bash
# Application logs
docker-compose logs -f

# Backend logs
tail -f backend/logs/application.log

# Frontend logs
ng serve --verbose
```

## 📈 Performance Optimization

### Backend

- **Connection pooling** with HikariCP
- **Query optimization** with proper indexing
- **Caching** with Redis (optional)
- **Lazy loading** for large datasets

### Frontend

- **Lazy loading** of modules
- **OnPush change detection** strategy
- **Bundle optimization** with tree-shaking
- **Image optimization** and lazy loading

## 📄 License
This project is licensed under the MIT License.
