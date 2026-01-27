export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: "USER" | "ADMIN" | "AGENT";
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  user: User;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
