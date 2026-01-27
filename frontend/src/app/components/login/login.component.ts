import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AuthService } from "../../services/auth.service";

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

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    const credentials = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.isLoading = false;

        // Check if token was received
        if (!response.token || !response.user) {
          this.snackBar.open("Invalid response from server", "Dismiss", {
            duration: 3000,
            panelClass: "error-snackbar",
          });
          return;
        }

        this.authService.saveToken(response.token);
        this.authService.saveUser(response.user);

        this.snackBar.open("Login successful!", "Dismiss", {
          duration: 3000,
          panelClass: "success-snackbar",
        });

        // Navigate after short delay to display message
        setTimeout(() => {
          this.router.navigate(["/dashboard"]);
        }, 500);
      },
      error: (error) => {
        this.isLoading = false;
        let errorMessage = "Invalid username or password";

        if (error.status === 0) {
          errorMessage = "Cannot connect to server";
        } else if (error.error?.message) {
          errorMessage = error.error.message;
        }

        this.snackBar.open(errorMessage, "Dismiss", {
          duration: 3000,
          panelClass: "error-snackbar",
        });
      },
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  getErrorMessage(field: string): string {
    const control = this.loginForm.get(field);
    if (control?.hasError("required")) {
      switch (field) {
        case "username":
          return "Username is required";
        case "password":
          return "Password is required";
        default:
          return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
      }
    }
    return "";
  }
}
