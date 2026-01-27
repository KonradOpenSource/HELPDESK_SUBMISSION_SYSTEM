import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AuthService } from "../../services/auth.service";
import { CreateUserRequest } from "../../models/user.model";

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

  ngOnInit(): void {}

  passwordMatchValidator(form: FormGroup): { [key: string]: boolean } | null {
    const password = form.get("password")?.value;
    const confirmPassword = form.get("confirmPassword")?.value;

    if (password !== confirmPassword) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isLoading = true;
    const formValue = this.registerForm.value;

    const userData: CreateUserRequest = {
      username: formValue.username,
      email: formValue.email,
      password: formValue.password,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
    };

    this.authService.register(userData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.snackBar.open(
          "Registration successful! Please login.",
          "Dismiss",
          {
            duration: 3000,
            panelClass: "success-snackbar",
          },
        );
        this.router.navigate(["/login"]);
      },
      error: (error) => {
        this.isLoading = false;
        let errorMessage = "Registration failed";
        if (error.error?.message) {
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
    const control = this.registerForm.get(field);

    if (control?.hasError("required")) {
      switch (field) {
        case "username":
          return "Username is required";
        case "email":
          return "Email is required";
        case "password":
          return "Password is required";
        case "confirmPassword":
          return "Confirm password is required";
        case "firstName":
          return "First name is required";
        case "lastName":
          return "Last name is required";
        default:
          return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
      }
    }

    if (control?.hasError("email")) {
      return "Please enter a valid email address";
    }

    if (control?.hasError("minlength")) {
      const minLength = control.errors?.["minlength"].requiredLength;
      return `Minimum ${minLength} characters required`;
    }

    if (control?.hasError("maxlength")) {
      const maxLength = control.errors?.["maxlength"].requiredLength;
      return `Maximum ${maxLength} characters allowed`;
    }

    if (control?.hasError("passwordMismatch")) {
      return "Passwords do not match";
    }

    return "";
  }
}
