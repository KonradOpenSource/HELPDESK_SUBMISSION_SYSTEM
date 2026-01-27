# 🧪 TESTING - VITEST + ANGULAR

## 📋 Test Configuration

### **Installed Packages:**

```bash
npm install --save-dev vitest @vitest/ui jsdom @testing-library/angular@14 @testing-library/jest-dom @vitest/coverage-v8
```

### **Configuration Files:**

- `vitest.config.ts` - Main Vitest configuration
- `src/test-setup.ts` - Global test settings
- `package.json` - Test scripts

## 🚀 Running Tests

### **Basic Commands:**

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with UI
npm run test:ui

# Run tests with code coverage
npm run test:coverage
```

## 📁 Test Structure

```
src/
├── test-setup.ts              # Global settings
├── app/
│   ├── services/
│   │   ├── auth.service.spec.ts    # Authentication service tests
│   │   └── ticket.service.spec.ts  # Ticket service tests
│   └── components/
│       ├── login/
│       │   └── login.component.spec.ts  # Login component tests
│       └── ...
└── coverage/                    # Code coverage reports
```

## 🔧 Test Examples

### **1. Service Test (AuthService)**

```typescript
import { describe, it, expect, beforeEach, vi } from "vitest";
import { TestBed } from "@angular/core/testing";
import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { AuthService } from "./auth.service";

describe("AuthService", () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should login successfully", () => {
    const credentials = { username: "testuser", password: "password" };
    const mockResponse = { token: "test-token", user: {} };

    service.login(credentials).subscribe((response) => {
      expect(response.token).toBe("test-token");
    });

    const req = httpMock.expectOne("http://localhost:8080/api/auth/login");
    expect(req.request.method).toBe("POST");
    req.flush(mockResponse);
  });
});
```

### **2. Component Test (LoginComponent)**

```typescript
import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { LoginComponent } from "./login.component";
import { AuthService } from "../../services/auth.service";

describe("LoginComponent", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: any;

  beforeEach(async () => {
    authServiceSpy = {
      login: vi.fn().mockReturnValue({
        subscribe: vi.fn(),
      }),
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should validate form", () => {
    expect(component.loginForm.invalid).toBe(true);

    component.loginForm.setValue({
      username: "testuser",
      password: "password123",
    });

    expect(component.loginForm.valid).toBe(true);
  });
});
```

### **3. Tests with @testing-library/angular**

```typescript
import { render, screen } from "@testing-library/angular";
import { LoginComponent } from "./login.component";

describe("LoginComponent", () => {
  it("should render login form", async () => {
    await render(LoginComponent);

    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
  });

  it("should show validation errors", async () => {
    await render(LoginComponent);

    const submitButton = screen.getByRole("button", { name: /login/i });
    fireEvent.click(submitButton);

    expect(screen.getByText(/username is required/i)).toBeInTheDocument();
  });
});
```

## 🛠️ Mocking

### **Mocking Services:**

```typescript
const authServiceSpy = {
  login: vi.fn().mockReturnValue({
    subscribe: vi.fn(),
  }),
  logout: vi.fn(),
  isLoggedIn: vi.fn().mockReturnValue(true),
};

TestBed.configureTestingModule({
  providers: [{ provide: AuthService, useValue: authServiceSpy }],
});
```

### **Mocking HTTP:**

```typescript
const mockResponse = { users: [] };
httpMock.expectOne("/api/users").flush(mockResponse);
```

### **Mocking Router:**

```typescript
const routerSpy = {
  navigate: vi.fn(),
};
```

## 📊 Code Coverage

### **Coverage Configuration:**

```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    coverage: {
      provider: "v8",
      reporter: ["text", "json", "html"],
      exclude: ["src/main.ts", "src/test-setup.ts", "**/*.spec.ts"],
      thresholds: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80,
        },
      },
    },
  },
});
```

### **Coverage Report:**

- Open `coverage/index.html` in browser
- Text report in terminal
- Detailed JSON report in `coverage/coverage-final.json`

## 🎯 Best Practices

### **1. Test Structure:**

- **Arrange** - Prepare data and mocks
- **Act** - Call the tested function
- **Assert** - Check the results

### **2. Component Testing:**

- Test component logic
- Test user interactions
- Test HTML rendering
- Test form validation

### **3. Service Testing:**

- Test business logic
- Test API interactions
- Mock external dependencies
- Test edge cases

### **4. Naming Conventions:**

- Files: `*.spec.ts` or `*.test.ts`
- Descriptions: `describe('ComponentName', () => {})`
- Tests: `it('should do something', () => {})`

### **5. Mocking:**

- Mock all external dependencies
- Use `vi.fn()` for functions
- Verify mock calls

## 🚀 Example Commands

```bash
# Run all tests
npm test

# Run tests for specific file
npm test auth.service.spec.ts

# Run tests with coverage
npm run test:coverage

# Run tests in interactive mode
npm run test:ui
```

## 📝 Useful Tips

1. **Test one thing at a time** - Each test should verify one functionality
2. **Use descriptive names** - Tests should be readable and describe what they test
3. **Mock only what's necessary** - Don't overmock
4. **Test edge cases** - Edge cases are important
5. **Keep tests updated** - Tests should evolve with the code

---

**Vitest + Angular is a modern and fast approach to testing Angular applications!** 🚀
