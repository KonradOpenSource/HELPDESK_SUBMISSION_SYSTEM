# BACKEND - COMPREHENSIVE DOCUMENTATION

## 🏗️ ARCHITECTURE

### 📋 Technologies

- **Java 21** - Latest LTS version
- **Spring Boot 3.x** - Application framework
- **Spring Security 6.x** - Security and authorization
- **Spring Data JPA** - Data access
- **PostgreSQL 15** - Database
- **Flyway** - Database migrations
- **JWT (JSON Web Tokens)** - Authentication
- **Maven** - Dependency management
- **JUnit 5 + Mockito** - Unit tests

### 🏛️ Project Structure

```
backend/
├── src/main/java/com/helpdesk/
│   ├── config/            # Security Configuration
│   ├── controller/        # REST API Controllers
│   ├── service/             # Business Logic Layer
│   ├── repository/          # Data Access Layer
│   ├── entity/              # JPA Entities
│   ├── dto/                 # Data Transfer Objects
│   ├── mapper/              # Entity-DTO Mapping
│   ├── security/           # Security Configuration
│   └── exception/           # Exception Handling
├── src/main/resources/
│   ├── application.yml      # Configuration
│   └── db/migration/        # Flyway Migrations
└── src/test/                # Unit Tests
```

## 🔌 REST API ENDPOINTS

### 🔐 Authentication Endpoints

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@helpdesk.com",
    "firstName": "Admin",
    "lastName": "User",
    "role": "ADMIN"
  }
}
```

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

```http
GET /api/auth/me
Authorization: Bearer {token}
```

### 🎫 Ticket Endpoints

```http
POST /api/tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Login Issue",
  "description": "Cannot login to system",
  "priority": "HIGH",
  "categoryId": 1
}
```

```http
GET /api/tickets?page=0&size=10&sort=createdAt,desc
Authorization: Bearer {token}
```

```http
GET /api/tickets/{id}
Authorization: Bearer {token}
```

```http
PUT /api/tickets/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "IN_PROGRESS",
  "assignedToId": 2
}
```

```http
PUT /api/tickets/{id}/assign?assignedToId=2
Authorization: Bearer {token}
```

### 📂 Category Endpoints

```http
GET /api/categories
Authorization: Bearer {token}
```

```http
POST /api/categories
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "New Category",
  "description": "Description"
}
```

## ✅ DATA VALIDATION

### 🛡️ Bean Validation Annotations

```java
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

### 📝 Validation Rules

- **Username**: 3-50 characters, required, unique
- **Email**: Email format, required, unique
- **Password**: Minimum 6 characters, required
- **Title**: Max 200 characters, required
- **Description**: Minimum 10 characters, required
- **Priority**: Required (LOW, MEDIUM, HIGH, CRITICAL)
- **Category**: Required, ID > 0

## 🚨 GLOBAL EXCEPTION HANDLING

### @ControllerAdvice

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(new ValidationErrorResponse(...), HttpStatus.BAD_REQUEST);
    }
}
```

### 📋 Supported Exceptions

- **MethodArgumentNotValidException** - @Valid validation errors
- **EntityNotFoundException** - Entity not found
- **BadCredentialsException** - Invalid login credentials
- **AccessDeniedException** - Insufficient permissions
- **IllegalArgumentException** - Invalid arguments
- **RuntimeException** - Runtime errors
- **Exception** - Global errors

### 📤 Error Response Format

```json
{
  "timestamp": "2024-01-25 18:55:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/auth/register",
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid"
  }
}
```

## 🔄 DTO MAPPING

### 📝 Entity → DTO Mapper

```java
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.USER);
        return user;
    }
}
```

### 🎯 DTO Pattern Benefits

- **Separation of Concerns** - Separation of presentation and data layers
- **Security** - Hiding sensitive fields (passwords)
- **Performance** - Selective data loading
- **Validation** - API-specific validations
- **API Stability** - API immutability with entity changes

## 🧪 UNIT TESTS

### 📊 Service Layer Tests

```java
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_Success() {
        // Given
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Test Ticket");
        request.setDescription("Test Description");
        request.setPriority(Ticket.Priority.MEDIUM);
        request.setCategoryId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        // When
        TicketDto result = ticketService.createTicket(request, 1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Ticket", result.getTitle());
        verify(ticketRepository).save(any(Ticket.class));
    }
}
```

### 🧪 Test Coverage

- **UserService** - User CRUD operations
- **TicketService** - Ticket management
- **CategoryService** - Category management
- **JwtTokenProvider** - Token generation and validation
- **AuthService** - Authentication and registration

### 📈 Mocking Strategy

- **@Mock** - External dependencies
- **@InjectMocks** - Mock injection
- **when().thenReturn()** - Behavior definition
- **verify()** - Interaction verification

## 🔐 JWT FILTERS

### 🛡️ JwtAuthenticationFilter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) {

        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### 🔑 JWT Token Provider

```java
@Component
public class JwtTokenProvider {

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
```

## 🔒 SPRING SECURITY CONFIG

### 🛡️ Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler));

        return http.build();
    }
}
```

### 🎯 Role-Based Access Control

```java
@PreAuthorize("hasRole('USER')")
public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody CreateTicketRequest request) {
    // Tylko użytkownicy mogą tworzyć zgłoszenia
}

@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public ResponseEntity<TicketDto> updateTicket(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request) {
    // Tylko agenci i admini mogą aktualizować zgłoszenia
}

@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
    // Tylko admini mogą usuwać zgłoszenia
}
```

## 🗄️ POSTGRESQL CONFIG

### 📋 Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/helpdesk
    username: helpdesk
    password: helpdesk123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate # We use Flyway for migrations
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

### 🔧 Connection Pooling

- **HikariCP** - Default Spring Boot connection pool
- **Maximum Pool Size**: 10
- **Minimum Idle**: 5
- **Connection Timeout**: 30000ms
- **Idle Timeout**: 600000ms

## 🚀 FLYWAY MIGRATIONS

### 📁 Migration Structure

```
src/main/resources/db/migration/
├── V1__Create_initial_tables.sql
├── V2__Add_indexes.sql
├── V3__Add_audit_tables.sql
└── V4__Add_user_preferences.sql
```

### 🔄 Migration Process

```sql
-- V1__Create_initial_tables.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ⚙️ Flyway Configuration

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    baseline-version: 1
    validate-on-migrate: true
```

## 📊 PERFORMANCE MONITORING

### 📈 Metrics

- **Response Time** - Endpoint response time
- **Database Connections** - Number of active connections
- **Memory Usage** - JVM memory usage
- **CPU Usage** - Processor load
- **Cache Hit Ratio** - Cache efficiency

### 🔍 Logging

```yaml
logging:
  level:
    com.helpdesk: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## 🚀 DEPLOYMENT

### 🐳 Docker Configuration

```dockerfile
FROM openjdk:21-jdk-slim

COPY target/helpdesk-backend.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 📋 Environment Variables

```bash
DATABASE_URL=jdbc:postgresql://postgres:5432/helpdesk
DATABASE_USERNAME=helpdesk
DATABASE_PASSWORD=helpdesk123
JWT_SECRET=mySecretKey
JWT_EXPIRATION_MS=86400000
```

## 🔧 BEST PRACTICES

### 📝 Code Quality

- **Clean Architecture** - Separation of concerns
- **SOLID Principles** - Single Responsibility, Open/Closed, etc.
- **DRY Principle** - Don't Repeat Yourself
- **KISS Principle** - Keep It Simple, Stupid
- **TDD** - Test-Driven Development

### 🛡️ Security

- **Input Validation** - Validate all input data
- **SQL Injection Prevention** - Parameterized queries
- **XSS Protection** - Data sanitization
- **CSRF Protection** - Spring Security CSRF
- **Rate Limiting** - Request rate limiting

### 📊 Performance

- **Database Indexing** - Optimal indexes
- **Connection Pooling** - Efficient connection management
- **Caching** - Redis for frequently used data
- **Lazy Loading** - On-demand loading
- **Pagination** - Large dataset pagination

---

## 🎯 SUMMARY

The Helpdesk backend was built using the latest technologies and best practices:

- **✅ REST API** with full CRUD
- **✅ Data validation** with Bean Validation
- **✅ Global exception handling** with @ControllerAdvice
- **✅ DTO mapping** with clean architecture
- **✅ Unit tests** with JUnit 5 + Mockito
- **✅ JWT filters** with Spring Security
- **✅ PostgreSQL config** with optimization
- **✅ Flyway migrations** with version control

The system is production-ready and scalable! 🚀
