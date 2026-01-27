import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, tap, catchError, throwError } from "rxjs";
import { Router } from "@angular/router";
import {
  User,
  LoginRequest,
  LoginResponse,
  CreateUserRequest,
} from "../models/user.model";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly API_BASE_URL = "http://localhost:8080";
  private readonly API_URL = `${this.API_BASE_URL}/api/auth`;
  private readonly TOKEN_KEY = "token";
  private readonly USER_KEY = "user";
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const user = localStorage.getItem(this.USER_KEY);

    if (token && user) {
      this.currentUserSubject.next(JSON.parse(user));
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap((response) => {
          console.log("✅ Login response received:", response);
          this.saveToken(response.token);
          this.saveUser(response.user);
          this.currentUserSubject.next(response.user);
        }),
        catchError((error) => {
          console.error("❌ Login error:", error);
          return throwError(() => error);
        }),
      );
  }

  register(userData: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/register`, userData);
  }

  saveToken(token: string): void {
    if (!token || token.trim() === "") {
      console.error("❌ Token is empty!");
      return;
    }
    localStorage.setItem(this.TOKEN_KEY, token);
    console.log("✅ Token saved:", token.substring(0, 20) + "...");
  }

  saveUser(user: any): void {
    if (!user) {
      console.error("❌ User is null!");
      return;
    }
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    console.log("✅ User saved:", user.username);
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    console.log(
      "🔑 Token from storage:",
      token ? token.substring(0, 20) + "..." : "null",
    );
    return token;
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    const user = userStr ? JSON.parse(userStr) : null;
    console.log("👤 Current user:", user);
    return user;
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem("authToken");
    localStorage.removeItem("currentUser");
    this.currentUserSubject.next(null);
    this.router.navigate(["/login"]);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    const isLogged = !!token;
    console.log("🔐 Is logged in:", isLogged);
    return isLogged;
  }

  hasRole(role: string): boolean {
    const currentUser = this.getCurrentUser();
    return currentUser ? currentUser.role === role : false;
  }

  isAdmin(): boolean {
    return this.hasRole("ADMIN");
  }

  isAgent(): boolean {
    return this.hasRole("AGENT");
  }

  isUser(): boolean {
    return this.hasRole("USER");
  }
}
