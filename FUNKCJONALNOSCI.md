# HELPDESK SYSTEM FUNCTIONALITY

## 🎭 USER ROLES

### 👤 USER (User)

- **Capabilities:**
  - Register new account
  - Login to system
  - Create new tickets
  - View own tickets
  - Add comments to own tickets
  - Attach files to tickets
  - Edit own profile

- **Limitations:**
  - Cannot see other users' tickets
  - Cannot assign tickets
  - No access to admin panel

### 🎯 AGENT (Support Agent)

- **USER + Capabilities:**
  - View all tickets in system
  - Assign tickets to self
  - Update ticket statuses
  - Add comments to any tickets
  - Search and filter tickets
  - Access statistics and reports
  - Manage categories (read-only)

- **Special permissions:**
  - Can see all tickets
  - Can change statuses and priorities
  - Can assign tickets to other agents

### 👑 ADMIN (Administrator)

- **AGENT + Capabilities:**
  - Full user management
  - Create and delete accounts
  - Change user roles
  - Category management (CRUD)
  - Delete tickets
  - Full system access

- **Special permissions:**
  - Unlimited access to all functions
  - System configuration management

## 🔐 AUTHORIZATION AND SECURITY

### 📝 Registration

- **Registration form:**
  - Username (unique, 3-50 characters)
  - Email (unique, format validation)
  - Password (minimum 6 characters, BCrypt encrypted)
  - First and last name

- **Validation:**
  - Username and email uniqueness
  - Email format validation
  - Minimum password length
  - Bot protection

### 🔑 Login

- **Login process:**
  - Username and password
  - Server-side validation
  - JWT token generation
  - Token storage in localStorage

- **JWT Token:**
  - Validity: 24 hours
  - Contains username and role
  - HMAC-SHA512 signed
  - Automatic refresh

### 🛡️ Role-Based Access Control (RBAC)

#### 🔒 Backend Security

- **Spring Security + JWT:**
  - Request filtering based on roles
  - `@PreAuthorize` annotations on endpoints
  - Custom UserDetailsService
  - JWT Authentication Filter

- **Protected endpoints:**
  ```java
  @PreAuthorize("hasRole('USER')")
  @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
  @PreAuthorize("hasRole('ADMIN')")
  ```

#### 🔒 Frontend Security

- **Route Guards:**
  - AuthGuard - protects against unauthorized access
  - RoleGuard - protects against role-based access
  - Redirect to login for unauthorized users

- **HTTP Interceptors:**
  - Automatic JWT token addition
  - 401/403 error handling
  - Redirect to login on token expiration

## 📋 TICKET FUNCTIONALITY

### 🎯 Creating Ticket (USER)

- **Form contains:**
  - Title (max 200 characters)
  - Description (text field)
  - Priority (LOW, MEDIUM, HIGH, URGENT)
  - Category (selection from list)
  - Attachments (files)

- **Automations:**
  - Status: OPEN
  - Assignment: none
  - Creation date: current

### 📊 Ticket Management (AGENT/ADMIN)

#### 🔍 Search and Filtering

- **Available filters:**
  - Title (text search)
  - Status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
  - Priority (LOW, MEDIUM, HIGH, URGENT)
  - Category
  - Assigned to
  - Date range

#### 📈 Ticket Statuses

1. **OPEN** - New ticket
2. **IN_PROGRESS** - In progress
3. **RESOLVED** - Resolved
4. **CLOSED** - Closed
5. **REOPENED** - Reopened

#### 🎯 Priorities

- **LOW** - Low priority
- **MEDIUM** - Medium priority
- **HIGH** - High priority
- **URGENT** - Urgent

### 💬 Comments

- **Functionality:**
  - Add comments to tickets
  - Automatic timestamp
  - Comment author information
  - Delete own comments (USER)
  - Comment management (AGENT/ADMIN)

### 📎 Attachments

- **Supported functions:**
  - File upload
  - File metadata (name, size, type)
  - Ticket association
  - Download attachments

## 📊 DASHBOARD AND STATISTICS

### 📈 Statistics (AGENT/ADMIN)

- **Available data:**
  - Number of tickets by status
  - Number of tickets by priority
  - Number of tickets by category
  - Agent statistics
  - Response time

### 🎯 Dedicated Views

- **USER:**
  - My tickets
  - My ticket statistics
  - Activity history

- **AGENT:**
  - Assigned to me
  - All tickets
  - Team statistics

- **ADMIN:**
  - Full system view
  - User management
  - System configuration

## 🔧 TICKET CATEGORIES

### 📂 Category Management

- **Access:**
  - USER: Read category list
  - AGENT: Read category list
  - ADMIN: Full CRUD (Create, Read, Update, Delete)

- **Default categories:**
  1. Technical Support - Technical problems
  2. Account Issues - Account problems
  3. Billing - Payments and subscriptions
  4. General Inquiry - General questions

## 🔄 WORKFLOW

### 📝 Typical Scenario (USER)

1. **Registration** → Email confirmation
2. **Login** → JWT token
3. **Create ticket** → Status OPEN
4. **Track status** → Real-time updates
5. **Add comments** → Communication with agent

### 🎯 Typical Scenario (AGENT)

1. **Login** → Panel access
2. **Review tickets** → Filter and search
3. **Assign ticket** → Status IN_PROGRESS
4. **Solve problem** → Comments and updates
5. **Close ticket** → Status RESOLVED/CLOSED

### 👑 Typical Scenario (ADMIN)

1. **User management** → Create agent accounts
2. **Category configuration** → Customize as needed
3. **System monitoring** → Statistics and reports
4. **Problem solving** → Technical support

## 🛡️ DATA PROTECTION

### 🔒 Personal Data Protection

- **Password encryption:** BCrypt
- **Tokens:** JWT with limited lifetime
- **Validation:** Client and server side
- **CORS:** Configuration for secure communication

### 🚨 Attack Protection

- **SQL Injection:** Spring Data JPA (parameterized queries)
- **XSS:** Angular sanitization
- **CSRF:** Spring Security protection
- **Rate Limiting:** Implementation possible

## 📱 RESPONSIVENESS

### 💻 Compatibility

- **Desktop:** Chrome, Firefox, Safari, Edge
- **Mobile:** Responsive design
- **Tablet:** Optimized view

### 🎨 UI/UX

- **Angular Material:** Consistent design system
- **Dark/Light Mode:** Configurable
- **Accessibility:** WCAG 2.1 compliance
- **Internationalization:** Multi-language support

---

## 🚀 SYSTEM STARTUP

### 🐳 Docker (Production)

```bash
docker-compose up --build
```

### 🔧 Development

```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && ng serve

# Database
docker-compose up postgres -d
```

### 🔑 Default Accounts

- **Admin:** username `admin`, password `admin123`
- **Agent:** username `agent`, password `agent123`

The system is ready for deployment and fully functional according to business requirements.
