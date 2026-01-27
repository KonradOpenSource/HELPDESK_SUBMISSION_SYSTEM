# FRONTEND - COMPREHENSIVE DOCUMENTATION

## 🎨 ARCHITECTURE

### 📋 Technologies

- **Angular 17+** - Frontend framework
- **Angular Material 17+** - UI Component Library
- **TypeScript 5.x** - Static typing
- **RxJS 7.x** - Reactive Programming
- **Angular Router** - Routing and lazy loading
- **Angular Forms** - Reactive Forms
- **SCSS** - CSS Preprocessor

### 🏛️ Project Structure

```
frontend/src/app/
├── components/              # Reusable Components
│   ├── login/              # Login Component
│   ├── register/           # Register Component
│   ├── dashboard/          # Dashboard Component
│   ├── ticket-list/        # Ticket List Component
│   ├── ticket-details/     # Ticket Details Component
│   ├── create-ticket/      # Create Ticket Component
│   └── admin-panel/        # Admin Panel Component
├── services/               # Business Logic
│   ├── auth.service.ts     # Authentication Service
│   ├── ticket.service.ts   # Ticket Service
│   └── category.service.ts # Category Service
├── guards/                 # Route Guards
│   ├── auth.guard.ts       # Authentication Guard
│   └── role.guard.ts       # Role-based Guard
├── interceptors/           # HTTP Interceptors
│   └── jwt.interceptor.ts  # JWT Interceptor
├── models/                 # TypeScript Models
│   ├── user.model.ts       # User Model
│   ├── ticket.model.ts     # Ticket Model
│   └── category.model.ts   # Category Model
├── shared/                 # Shared Components
│   ├── header/             # Header Component
│   ├── sidebar/            # Sidebar Component
│   └── footer/             # Footer Component
└── environments/           # Environment Configs
    ├── environment.ts      # Development
    └── environment.prod.ts # Production
```

## 🎨 ANGULAR MATERIAL UI

### 📦 Material Design Components

```typescript
// app.module.ts
import { NgModule } from "@angular/core";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatRadioModule } from "@angular/material/radio";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatTabsModule } from "@angular/material/tabs";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatMenuModule } from "@angular/material/menu";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatBadgeModule } from "@angular/material/badge";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";

@NgModule({
  imports: [
    BrowserAnimationsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatRadioModule,
    MatSlideToggleModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatTabsModule,
    MatExpansionModule,
    MatMenuModule,
    MatToolbarModule,
    MatSidenavModule,
    MatBadgeModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
})
export class AppModule {}
```

### 🎨 Custom Theme

```scss
// styles.scss
@import "~@angular/material/theming";

@include mat-core();

$helpdesk-primary: mat-palette($mat-indigo);
$helpdesk-accent: mat-palette($mat-pink, A200, A100, A400);
$helpdesk-warn: mat-palette($mat-red);
$helpdesk-theme: mat-light-theme(
  $helpdesk-primary,
  $helpdesk-accent,
  $helpdesk-warn
);

@include angular-material-theme($helpdesk-theme);

// Custom styles
.success-snackbar {
  background-color: #4caf50 !important;
  color: white !important;
}

.error-snackbar {
  background-color: #f44336 !important;
  color: white !important;
}

.warning-snackbar {
  background-color: #ff9800 !important;
  color: white !important;
}
```

## 🧩 COMPONENTS

### 🔑 Login Component

```typescript
@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.loginForm = this.fb.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    const { username, password } = this.loginForm.value;

    this.authService.login(username, password).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.authService.saveUser(response.user);
        this.snackBar.open("Login successful!", "Close", {
          duration: 3000,
          panelClass: "success-snackbar",
        });
        this.router.navigate(["/dashboard"]);
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open("Invalid credentials", "Close", {
          duration: 3000,
          panelClass: "error-snackbar",
        });
      },
    });
  }
}
```

### 📝 Register Component

```typescript
@Component({
  selector: "app-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.registerForm = this.fb.group(
      {
        username: [
          "",
          [
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(50),
          ],
        ],
        email: ["", [Validators.required, Validators.email]],
        password: ["", [Validators.required, Validators.minLength(6)]],
        confirmPassword: ["", [Validators.required]],
        firstName: ["", [Validators.required, Validators.maxLength(50)]],
        lastName: ["", [Validators.required, Validators.maxLength(50)]],
      },
      { validator: this.passwordMatchValidator },
    );
  }

  passwordMatchValidator(form: FormGroup): { [key: string]: boolean } | null {
    const password = form.get("password")?.value;
    const confirmPassword = form.get("confirmPassword")?.value;
    return password !== confirmPassword ? { passwordMismatch: true } : null;
  }
}
```

### 📊 Dashboard Component

```typescript
@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  currentUser: User | null = null;
  stats = {
    totalTickets: 0,
    newTickets: 0,
    inProgressTickets: 0,
    resolvedTickets: 0,
    myTickets: 0,
    assignedToMe: 0,
  };

  recentTickets: Ticket[] = [];
  isLoading = true;

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;

    // Load statistics using RxJS
    this.ticketService
      .getTicketStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.stats = stats;
        },
        error: (error) => {
          console.error("Error loading stats:", error);
        },
      });

    // Load recent tickets
    this.ticketService
      .getTickets(0, 5, "createdAt", "desc")
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.recentTickets = response.content;
          this.isLoading = false;
        },
        error: (error) => {
          console.error("Error loading recent tickets:", error);
          this.isLoading = false;
        },
      });
  }
}
```

## 📡 RXJS OBSERVABLES

### 🔀 Reactive Programming Pattern

```typescript
// ticket.service.ts
@Injectable({
  providedIn: "root",
})
export class TicketService {
  private ticketsSubject = new BehaviorSubject<Ticket[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  tickets$ = this.ticketsSubject.asObservable();
  loading$ = this.loadingSubject.asObservable();

  constructor(private http: HttpClient) {}

  getTickets(page: number = 0, size: number = 10): Observable<TicketPage> {
    this.loadingSubject.next(true);

    return this.http
      .get<TicketPage>(`${this.API_URL}?page=${page}&size=${size}`)
      .pipe(
        map((response) => {
          this.ticketsSubject.next(response.content);
          return response;
        }),
        catchError((error) => {
          console.error("Error loading tickets:", error);
          return throwError(error);
        }),
        finalize(() => {
          this.loadingSubject.next(false);
        }),
      );
  }

  // Real-time updates
  subscribeToTicketUpdates(): Observable<Ticket> {
    return new Observable<Ticket>((observer) => {
      const eventSource = new EventSource(`${this.API_URL}/stream`);

      eventSource.onmessage = (event) => {
        const ticket = JSON.parse(event.data);
        observer.next(ticket);
      };

      eventSource.onerror = (error) => {
        observer.error(error);
      };

      return () => {
        eventSource.close();
      };
    });
  }
}
```

### 🔄 Operators Usage

```typescript
// Component with multiple RxJS operators
ngOnInit(): void {
  this.ticketService.getTickets()
    .pipe(
      // Transform data
      map(response => response.content),
      // Filter tickets
      filter(tickets => tickets.length > 0),
      // Handle errors
      catchError(error => {
        this.showError('Failed to load tickets');
        return of([]);
      }),
      // Auto-unsubscribe
      takeUntil(this.destroy$)
    )
    .subscribe(tickets => {
      this.tickets = tickets;
    });

  // Combine multiple observables
  combineLatest([
    this.ticketService.getTickets(),
    this.authService.getCurrentUser$(),
    this.categoryService.getCategories()
  ]).pipe(
    map(([tickets, user, categories]) => ({
      tickets,
      user,
      categories
    })),
    takeUntil(this.destroy$)
  ).subscribe(({ tickets, user, categories }) => {
    this.processData(tickets, user, categories);
  });
}
```

## 🛡️ GUARDS

### 🔐 Authentication Guard

```typescript
@Injectable({
  providedIn: "root",
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    // Not logged in, redirect to login page with return URL
    this.router.navigate(["/login"], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }
}
```

### 👥 Role-based Guard

```typescript
@Injectable({
  providedIn: "root",
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean {
    const expectedRoles = route.data["roles"] as string[];

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(["/login"]);
      return false;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(["/login"]);
      return false;
    }

    const hasRequiredRole = expectedRoles.some(
      (role) => user.role === role || this.authService.hasRole(role),
    );

    if (!hasRequiredRole) {
      this.router.navigate(["/unauthorized"]);
      return false;
    }

    return true;
  }
}
```

### 🛣️ Route Configuration with Guards

```typescript
const routes: Routes = [
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "register",
    component: RegisterComponent,
  },
  {
    path: "dashboard",
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },
  {
    path: "tickets",
    component: TicketListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: "admin",
    component: AdminPanelComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ["ADMIN"] },
  },
  {
    path: "agent",
    component: AgentPanelComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ["ADMIN", "AGENT"] },
  },
];
```

## 🔌 JWT INTERCEPTOR

### 🛡️ HTTP Interceptor for JWT

```typescript
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // Add JWT token to Authorization header
    const token = this.authService.getToken();

    if (token) {
      req = this.addToken(req, token);
    }

    // Handle 401 Unauthorized responses
    return next.handle(req).pipe(
      catchError((error) => {
        if (error.status === 401) {
          // Token expired or invalid, logout user
          this.authService.logout();
        }
        return throwError(error);
      }),
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
```

### 🔧 Interceptor Registration

```typescript
// app.module.ts
@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
  ],
})
export class AppModule {}
```

## 🔄 LAZY LOADING MODULES

### 📦 Feature Modules

```typescript
// tickets.module.ts
@NgModule({
  imports: [
    CommonModule,
    TicketsRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  declarations: [
    TicketListComponent,
    TicketDetailsComponent,
    CreateTicketComponent,
  ],
  providers: [TicketService],
})
export class TicketsModule {}
```

### 🛣️ Lazy Loading Routes

```typescript
// app-routing.module.ts
const routes: Routes = [
  {
    path: "",
    redirectTo: "/dashboard",
    pathMatch: "full",
  },
  {
    path: "tickets",
    loadChildren: () =>
      import("./modules/tickets/tickets.module").then((m) => m.TicketsModule),
  },
  {
    path: "admin",
    loadChildren: () =>
      import("./modules/admin/admin.module").then((m) => m.AdminModule),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ["ADMIN"] },
  },
  {
    path: "agent",
    loadChildren: () =>
      import("./modules/agent/agent.module").then((m) => m.AgentModule),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ["ADMIN", "AGENT"] },
  },
];
```

### ⚡ Preloading Strategy

```typescript
// app.module.ts
@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules,
    }),
  ],
})
export class AppModule {}
```

## 📱 RESPONSIVE LAYOUT

### 🎨 Responsive Design with Flexbox

```scss
// dashboard.component.scss
.dashboard-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  @media (min-width: 768px) {
    flex-direction: row;
  }
}

.dashboard-sidebar {
  width: 100%;

  @media (min-width: 768px) {
    width: 250px;
    flex-shrink: 0;
  }
}

.dashboard-content {
  flex: 1;
  padding: 16px;

  @media (min-width: 768px) {
    padding: 24px;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;

  @media (min-width: 576px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 992px) {
    grid-template-columns: repeat(4, 1fr);
  }
}
```

### 📱 Mobile-First Approach

```typescript
// Responsive service
@Injectable({
  providedIn: "root",
})
export class ResponsiveService {
  private isMobile$ = new BehaviorSubject<boolean>(false);
  private isTablet$ = new BehaviorSubject<boolean>(false);
  private isDesktop$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.checkScreenSize();
    window.addEventListener("resize", () => this.checkScreenSize());
  }

  private checkScreenSize(): void {
    const width = window.innerWidth;
    this.isMobile$.next(width < 768);
    this.isTablet$.next(width >= 768 && width < 1024);
    this.isDesktop$.next(width >= 1024);
  }

  isMobile(): Observable<boolean> {
    return this.isMobile$.asObservable();
  }

  isTablet(): Observable<boolean> {
    return this.isTablet$.asObservable();
  }

  isDesktop(): Observable<boolean> {
    return this.isDesktop$.asObservable();
  }
}
```

### 🎯 Adaptive Layout

```html
<!-- dashboard.component.html -->
<div class="dashboard-container">
  <mat-sidenav-container>
    <!-- Mobile: Temporary drawer -->
    <mat-sidenav
      *ngIf="isMobile$ | async"
      mode="over"
      [(opened)]="drawerOpened"
    >
      <app-sidebar></app-sidebar>
    </mat-sidenav>

    <!-- Desktop: Permanent drawer -->
    <mat-sidenav *ngIf="isDesktop$ | async" mode="side" opened>
      <app-sidebar></app-sidebar>
    </mat-sidenav>

    <mat-sidenav-content>
      <app-header (toggleDrawer)="drawerOpened = !drawerOpened"></app-header>
      <main class="dashboard-content">
        <router-outlet></router-outlet>
      </main>
    </mat-sidenav-content>
  </mat-sidenav-container>
</div>
```

## 🎨 MATERIAL DESIGN BEST PRACTICES

### 🎨 Color Palette

```scss
// Custom color palette
$helpdesk-primary: (
  50: #e8eaf6,
  100: #c5cae9,
  200: #9fa8da,
  300: #7986cb,
  400: #5c6bc0,
  500: #3f51b5,
  600: #3949ab,
  700: #303f9f,
  800: #283593,
  900: #1a237e,
  A100: #8c9eff,
  A200: #536dfe,
  A400: #3d5afe,
  A700: #304ffe,
);

$helpdesk-accent: (
  50: #fce4ec,
  100: #f8bbd9,
  200: #f48fb1,
  300: #f06292,
  400: #ec407a,
  500: #e91e63,
  600: #d81b60,
  700: #c2185b,
  800: #ad1457,
  900: #880e4f,
  A100: #ff80ab,
  A200: #ff4081,
  A400: #f50057,
  A700: #c51162,
);
```

### 📐 Typography Scale

```scss
// Custom typography
$helpdesk-typography: mat-typography-config(
  $font-family: "Roboto, sans-serif",
  $display-4: mat-typography-level(112px, 112px, 300, $letter-spacing: -0.05em),
  $display-3: mat-typography-level(56px, 56px, 400, $letter-spacing: -0.02em),
  $display-2: mat-typography-level(45px, 48px, 400, $letter-spacing: -0.005em),
  $display-1: mat-typography-level(34px, 40px, 400),
  $headline: mat-typography-level(24px, 32px, 400),
  $title: mat-typography-level(20px, 28px, 500),
  $subheading-2: mat-typography-level(16px, 24px, 400),
  $subheading-1: mat-typography-level(15px, 24px, 500),
  $body-2: mat-typography-level(14px, 24px, 500),
  $body-1: mat-typography-level(16px, 24px, 400),
  $caption: mat-typography-level(12px, 20px, 400),
  $button: mat-typography-level(14px, 14px, 500),
  $input: mat-typography-level(16px, 24px, 400),
  $input-label: mat-typography-level(12px, 20px, 500),
);
```

### 🎯 Component Customization

```scss
// Custom Material components
::ng-deep .mat-form-field-appearance-outline {
  .mat-form-field-outline {
    border-radius: 8px;
  }

  &.mat-focused .mat-form-field-outline-thick {
    color: mat-color($helpdesk-primary, 500);
  }
}

::ng-deep .mat-raised-button {
  &.mat-primary {
    background-color: mat-color($helpdesk-primary, 500);

    &:hover {
      background-color: mat-color($helpdesk-primary, 700);
    }

    &:disabled {
      background-color: mat-color($helpdesk-primary, 100);
      color: mat-color($helpdesk-primary, 300);
    }
  }
}

::ng-deep .mat-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }
}
```

## 🚀 PERFORMANCE OPTIMIZATION

### ⚡ Lazy Loading

```typescript
// Route-level lazy loading
const routes: Routes = [
  {
    path: "tickets",
    loadChildren: () =>
      import("./modules/tickets/tickets.module").then((m) => m.TicketsModule),
  },
];

// Component-level lazy loading
@Component({
  selector: "app-heavy-component",
  template: `
    <ng-container *ngIf="componentLoaded">
      <app-heavy-content></app-heavy-content>
    </ng-container>
  `,
})
export class HeavyComponent {
  componentLoaded = false;

  constructor() {
    // Load heavy component after initial render
    setTimeout(() => {
      this.componentLoaded = true;
    }, 100);
  }
}
```

### 🔄 OnPush Change Detection

```typescript
@Component({
  selector: "app-ticket-item",
  template: `
    <mat-card>
      <h3>{{ ticket.title }}</h3>
      <p>{{ ticket.description }}</p>
    </mat-card>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TicketItemComponent {
  @Input() ticket: Ticket;

  constructor(private cdr: ChangeDetectorRef) {}

  updateTicket(ticket: Ticket): void {
    this.ticket = ticket;
    this.cdr.markForCheck();
  }
}
```

### 📦 Bundle Optimization

```typescript
// angular.json
{
  "projects": {
    "helpdesk-frontend": {
      "architect": {
        "build": {
          "options": {
            "optimization": true,
            "sourceMap": false,
            "extractCss": true,
            "namedChunks": false,
            "extractLicenses": true,
            "vendorChunk": false,
            "buildOptimizer": true,
            "budgets": [
              {
                "type": "initial",
                "maximumWarning": "2mb",
                "maximumError": "5mb"
              },
              {
                "type": "anyComponentStyle",
                "maximumWarning": "6kb",
                "maximumError": "10kb"
              }
            ]
          }
        }
      }
    }
  }
}
```

---

## 🎯 SUMMARY

The Helpdesk frontend was built with the latest technologies and best practices:

- **✅ Angular Material UI** - Complete component set
- **✅ Components** - Login, Register, Dashboard, Ticket List, Details, Create, Admin Panel
- **✅ RxJS Observables** - Reactive programming with operators
- **✅ Guards** - Auth and role-based guards
- **✅ JWT Interceptor** - Automatic token addition
- **✅ Lazy Loading** - Module loading optimization
- **✅ Responsive Layout** - Mobile-first design

The system is fully responsive, performance-optimized and production-ready! 🚀
