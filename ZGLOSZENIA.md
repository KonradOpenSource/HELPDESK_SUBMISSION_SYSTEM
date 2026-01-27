# TICKETS - DETAILED DOCUMENTATION

## 📋 TICKET STRUCTURE

### 🔧 Basic Fields

- **title** - Ticket title (max 200 characters)
- **description** - Detailed problem description (text field)
- **priority** - Ticket priority
- **status** - Status in workflow
- **categoryId** - Category identifier
- **assignedToId** - Assigned agent (optional)

### 🎯 Priorities

```typescript
type Priority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
```

- **LOW** - Low priority, standard inquiries
- **MEDIUM** - Medium priority, work-impacting problems
- **HIGH** - High priority, critical problems
- **CRITICAL** - Urgent, system unavailable, critical failures

### 🔄 Status Workflow

```typescript
type TicketStatus = "NEW" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
```

#### 📈 Status Flow:

1. **NEW** → **IN_PROGRESS** (assignment to agent)
2. **IN_PROGRESS** → **RESOLVED** (problem resolution)
3. **RESOLVED** → **CLOSED** (closure confirmation)
4. **IN_PROGRESS** → **CLOSED** (direct closure)

## 🔐 ROLE VALIDATION FOR STATUS CHANGES

### 👤 USER (User)

- ✅ Can create tickets (status NEW)
- ✅ Can add comments to own tickets
- ❌ Cannot change statuses
- ❌ Cannot assign agents

### 🎯 AGENT (Support Agent)

- ✅ Can change status: NEW → IN_PROGRESS
- ✅ Can change status: IN_PROGRESS → RESOLVED
- ✅ Can change status: IN_PROGRESS → CLOSED
- ✅ Can assign tickets to self and other agents
- ✅ Can edit all ticket fields

### 👑 ADMIN (Administrator)

- ✅ Full AGENT permissions
- ✅ Can change status: RESOLVED → IN_PROGRESS (reopening)
- ✅ Can delete tickets
- ✅ Can change any fields

## 📊 STATUS CHANGE HISTORY (AUDIT LOG)

### 🗃️ TicketHistory Structure

```sql
CREATE TABLE ticket_history (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id),
    old_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_by BIGINT NOT NULL REFERENCES users(id),
    change_reason TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 📝 Recorded Information

- **ticket_id** - Ticket identifier
- **old_status** - Previous status
- **new_status** - New status
- **changed_by** - Who made the change
- **change_reason** - Change reason (optional)
- **changed_at** - Change time

### 🔍 Audit Log Functions

- Automatic saving of every status change
- Complete change history for each ticket
- Filtering by date and user
- Non-deletable records (data integrity)

## ⏱️ SLA (SERVICE LEVEL AGREEMENT)

### 📋 Response Times by Priority

| Priority | Response Time | Resolution Time |
| -------- | ------------- | --------------- |
| LOW      | 24 hours      | 5 business days |
| MEDIUM   | 8 hours       | 2 business days |
| HIGH     | 2 hours       | 1 business day  |
| CRITICAL | 30 minutes    | 4 hours         |

### 🧮 Time Calculation

```java
public Duration calculateResolutionTime(Ticket ticket) {
    if (ticket.getCreatedAt() != null && ticket.getResolvedAt() != null) {
        return Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt());
    }
    return null;
}
```

### 📊 SLA Metrics

- **First Response Time** - First response time
- **Resolution Time** - Total resolution time
- **SLA Compliance** - Percentage of tickets within SLA
- **Average Handle Time** - Average handling time

## 🚨 ESCALATIONS

### 🔧 Escalation Logic (Backend)

```java
public boolean shouldEscalate(Ticket ticket) {
    // Automatyczna eskalacja CRITICAL
    if (ticket.getPriority() == Ticket.Priority.CRITICAL &&
        ticket.getStatus() == Ticket.TicketStatus.NEW) {
        return true;
    }

    Duration timeSinceCreation = Duration.between(ticket.getCreatedAt(), LocalDateTime.now());

    // HIGH priorytet - 2 godziny
    if (ticket.getPriority() == Ticket.Priority.HIGH && timeSinceCreation.toHours() > 2) {
        return true;
    }

    // MEDIUM priorytet - 8 godzin
    if (ticket.getPriority() == Ticket.Priority.MEDIUM && timeSinceCreation.toHours() > 8) {
        return true;
    }

    return false;
}
```

### 📈 Escalation Process

1. **Automatic detection** - Check every 15 minutes
2. **Priority change** - To CRITICAL
3. **Notification** - Email to admin and senior agents
4. **History save** - Audit log of escalation
5. **Assignment** - To least loaded agent

### 🎯 Escalation Rules

- **CRITICAL** - Immediate escalation
- **HIGH** - Escalation after 2 hours without response
- **MEDIUM** - Escalation after 8 hours
- **LOW** - No automatic escalation

## 📝 MANAGEMENT FUNCTIONS

### ➕ Creating Tickets

```typescript
// Frontend - Reactive Form
createTicketForm = this.fb.group({
  title: ["", [Validators.required, Validators.maxLength(200)]],
  description: ["", [Validators.required]],
  priority: ["MEDIUM", Validators.required],
  categoryId: [null, Validators.required],
});
```

### ✏️ Editing Tickets

- **USER** - Can edit only own tickets (only NEW)
- **AGENT** - Can edit all fields
- **ADMIN** - Full edit permissions

### 🔄 Status Change with Validation

```java
@Transactional
public TicketDto updateTicketStatus(Long ticketId, TicketStatus newStatus, User changedBy, String reason) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException("Ticket not found"));

    // Walidacja uprawnień
    validateStatusChange(ticket, newStatus, changedBy);

    // Zapis historii
    recordStatusChange(ticket, newStatus, changedBy, reason);

    // Aktualizacja statusu
    ticket.setStatus(newStatus);

    // Automatyczna eskalacja
    if (shouldEscalate(ticket)) {
        escalateTicket(ticketId);
    }

    return ticketMapper.toDto(ticketRepository.save(ticket));
}
```

## 💬 TICKET COMMENTS

### 📝 Comment Structure

```typescript
interface Comment {
  id: number;
  content: string;
  ticketId: number;
  userId: number;
  username: string;
  createdAt: string;
}
```

### 🔧 Comment Functions

- **Adding** - All users
- **Editing** - Only comment author
- **Deleting** - Author + AGENT/ADMIN
- **Notifications** - Email for new comments

## 📊 DASHBOARD AND STATISTICS

### 📈 Dashboard Views

- **USER Dashboard**:
  - My tickets
  - My ticket status
  - Average response time

- **AGENT Dashboard**:
  - Assigned to me
  - New tickets
  - Performance statistics
  - Escalations

- **ADMIN Dashboard**:
  - Full system statistics
  - SLA charts
  - Agent reports
  - System configuration

### 📊 Metrics and KPI

```java
// Example metrics
- Number of tickets by status
- Number of tickets by priority
- Average resolution time
- SLA compliance percentage
- Number of escalations
- Agent performance
```

## 🔍 FILTERING AND SEARCHING

### 🔍 Full-text Search

```typescript
searchTickets(filters: TicketFilters): Observable<Page<TicketDto>> {
  const params = new HttpParams()
    .set('title', filters.title || '')
    .set('status', filters.status || '')
    .set('priority', filters.priority || '')
    .set('categoryId', filters.categoryId?.toString() || '')
    .set('assignedToId', filters.assignedToId?.toString() || '')
    .set('page', filters.page.toString())
    .set('size', filters.size.toString());

  return this.http.get<Page<TicketDto>>(`${this.API_URL}/search`, { params });
}
```

### 🏷️ Available Filters

- **Title** - Text search
- **Status** - Dropdown with options
- **Priority** - Multi-select
- **Category** - Dropdown
- **Assigned to** - Agent list
- **Date range** - Creation/resolution date
- **My tickets** - User filter

## 🔄 WORKFLOW

### 📝 Scenario 1: Standard Ticket

1. **USER** creates ticket → status NEW
2. **SYSTEM** assigns ticket ID and timestamp
3. **AGENT** reviews new tickets
4. **AGENT** assigns to self → status IN_PROGRESS
5. **SYSTEM** saves change in audit log
6. **AGENT** adds comment/question
7. **USER** responds to comment
8. **AGENT** resolves problem → status RESOLVED
9. **SYSTEM** calculates resolution time (SLA)
10. **USER** confirms resolution → status CLOSED

### 🚨 Scenario 2: Escalation

1. **USER** creates HIGH priority ticket
2. **SYSTEM** sets status NEW
3. **2 hours** - no agent response
4. **SYSTEM** automatically escalates → priority CRITICAL
5. **SYSTEM** sends notification to admin
6. **ADMIN** assigns to senior agent
7. **AGENT** resolves problem

### 📊 Scenario 3: Reporting

1. **ADMIN** generates monthly report
2. **SYSTEM** retrieves data from ticket_history
3. **SYSTEM** calculates SLA metrics
4. **SYSTEM** creates charts and statistics
5. **ADMIN** exports report to PDF/Excel

## 🛡️ PROTECTIONS AND VALIDATIONS

### 🔒 Backend Security

```java
@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public ResponseEntity<TicketDto> updateTicketStatus(...) {
    // Walidacja status transition
    validateStatusTransition(oldStatus, newStatus, userRole);

    // Audit log
    recordStatusChange(ticket, newStatus, currentUser, reason);

    // SLA check
    checkSLAViolation(ticket);

    // Escalation check
    if (shouldEscalate(ticket)) {
        escalateTicket(ticket.getId());
    }
}
```

### ✅ Frontend Validations

```typescript
// Reactive form validators
this.ticketForm = this.fb.group(
  {
    title: ["", [Validators.required, Validators.maxLength(200)]],
    description: ["", [Validators.required, Validators.minLength(10)]],
    priority: ["MEDIUM", Validators.required],
    categoryId: [null, Validators.required],
  },
  { validators: statusTransitionValidator },
);
```

## 📱 NOTIFICATIONS AND INTEGRATIONS

### 📧 Email Notifications

- **New ticket** - To all agents
- **Assignment** - To assigned agent
- **Comment** - To ticket participants
- **Escalation** - To admin and senior agents
- **Status RESOLVED** - To reporting user

### 🔔 System Notifications

- Real-time updates via WebSocket
- Push notifications for mobile
- Dashboard alerts for critical tickets

---

## 🚀 SUMMARY

The ticket system was designed with focus on:

- **Full auditability** - Every change is recorded
- **Automation** - SLA and escalations work automatically
- **Scalability** - Flexible architecture
- **Security** - Role-based access control
- **Usability** - Intuitive workflow and dashboard

The system is ready for deployment and capable of handling large ticket volumes while maintaining high SLA standards.
