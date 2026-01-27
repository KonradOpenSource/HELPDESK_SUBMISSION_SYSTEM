import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import "@angular/compiler";
import { AuthService } from "./auth.service";
import { User } from "../models/user.model";
import { LoginRequest } from "../models/user.model";

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value;
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key];
    }),
    clear: vi.fn(() => {
      store = {};
    }),
  };
})();

Object.defineProperty(window, "localStorage", {
  value: localStorageMock,
});

// Mock HttpClient
class MockHttpClient {
  private responses: Record<string, any> = {};

  post(url: string, body: any) {
    return {
      pipe: vi.fn((...operators: any[]) => {
        let result = this.responses[url] || { error: "Not found" };

        // Apply operators (simplified for testing)
        operators.forEach((operator) => {
          if (operator.name === "tap") {
            operator.fn(result);
          }
        });

        return {
          subscribe: vi.fn((callback: Function) => {
            callback(result);
            return { unsubscribe: vi.fn() };
          }),
        };
      }),
      subscribe: vi.fn((callback: Function) => {
        callback(this.responses[url] || { error: "Not found" });
        return { unsubscribe: vi.fn() };
      }),
    };
  }

  setResponse(url: string, response: any) {
    this.responses[url] = response;
  }
}

describe("AuthService", () => {
  let service: AuthService;
  let httpClient: MockHttpClient;

  beforeEach(() => {
    httpClient = new MockHttpClient();
    const mockRouter = {
      navigate: vi.fn(),
    };
    service = new AuthService(httpClient as any, mockRouter as any);
    localStorageMock.clear();
  });

  afterEach(() => {
    localStorageMock.clear();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should login successfully", () => {
    const credentials: LoginRequest = {
      username: "testuser",
      password: "password",
    };

    const mockResponse = {
      token: "test-token",
      user: {
        id: 1,
        username: "testuser",
        email: "test@example.com",
        firstName: "Test",
        lastName: "User",
        role: "USER",
      },
    };

    httpClient.setResponse(
      "http://localhost:8080/api/auth/login",
      mockResponse,
    );

    service.login(credentials).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });
  });

  it("should save token to localStorage", () => {
    const token = "test-token";
    service.saveToken(token);
    expect(localStorageMock.setItem).toHaveBeenCalledWith("token", token);
  });

  it("should save user to localStorage", () => {
    const user: User = {
      id: 1,
      username: "testuser",
      email: "test@example.com",
      firstName: "Test",
      lastName: "User",
      role: "USER",
    };
    service.saveUser(user);
    expect(localStorageMock.setItem).toHaveBeenCalledWith(
      "user",
      JSON.stringify(user),
    );
  });

  it("should logout and clear localStorage", () => {
    localStorageMock.setItem("token", "test-token");
    localStorageMock.setItem("user", JSON.stringify({ id: 1 }));
    service.logout();
    expect(localStorageMock.removeItem).toHaveBeenCalledWith("token");
    expect(localStorageMock.removeItem).toHaveBeenCalledWith("user");
  });

  it("should return true when user is logged in", () => {
    localStorageMock.setItem("token", "test-token");
    expect(service.isLoggedIn()).toBe(true);
  });

  it("should return false when user is not logged in", () => {
    expect(service.isLoggedIn()).toBe(false);
  });

  it("should check user role correctly", () => {
    const user: User = {
      id: 1,
      username: "admin",
      email: "admin@example.com",
      firstName: "Admin",
      lastName: "User",
      role: "ADMIN",
    };
    localStorageMock.setItem("user", JSON.stringify(user));
    expect(service.hasRole("ADMIN")).toBe(true);
    expect(service.hasRole("USER")).toBe(false);
  });

  it("should register new user", () => {
    const newUser = {
      username: "newuser",
      password: "password",
      email: "new@example.com",
      firstName: "New",
      lastName: "User",
    };

    const mockResponse = {
      id: 2,
      username: "newuser",
      email: "new@example.com",
      firstName: "New",
      lastName: "User",
      role: "USER",
    };

    httpClient.setResponse(
      "http://localhost:8080/api/auth/register",
      mockResponse,
    );

    service.register(newUser).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });
  });

  it("should get token from localStorage", () => {
    localStorageMock.setItem("token", "test-token");
    expect(service.getToken()).toBe("test-token");
  });

  it("should return null when no token exists", () => {
    expect(service.getToken()).toBeNull();
  });
});
