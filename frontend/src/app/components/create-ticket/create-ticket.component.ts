import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { TicketService } from "../../services/ticket.service";
import { CategoryService } from "../../services/category.service";
import { CreateTicketRequest } from "../../models/ticket.model";
import { Category } from "../../models/category.model";
import { User } from "../../models/user.model";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: "app-create-ticket",
  templateUrl: "./create-ticket.component.html",
  styleUrls: ["./create-ticket.component.scss"],
})
export class CreateTicketComponent implements OnInit {
  ticketForm: FormGroup;
  categories: Category[] = [];
  users: User[] = [];
  isLoading = false;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private categoryService: CategoryService,
    private router: Router,
    private snackBar: MatSnackBar,
    private http: HttpClient,
  ) {
    this.ticketForm = this.fb.group({
      title: ["", [Validators.required, Validators.maxLength(200)]],
      description: ["", [Validators.required, Validators.minLength(10)]],
      priority: ["MEDIUM", Validators.required],
      categoryId: [null, [Validators.required]],
      assignedToId: [null],
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    this.loadUsers();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        console.error("Error loading categories:", error);
        this.snackBar.open("Failed to load categories", "Dismiss", {
          duration: 3000,
          panelClass: "error-snackbar",
        });
      },
    });
  }

  loadUsers(): void {
    this.http.get<any>("http://localhost:8080/api/auth/users").subscribe({
      next: (response) => {
        const allUsers = response.content || response;
        // Pokaż tylko agentów i adminów do przypisania
        this.users = allUsers.filter(
          (user: any) => user.role === "AGENT" || user.role === "ADMIN",
        );
        console.log("✅ Users loaded (agents and admins only):", this.users);
      },
      error: (error) => {
        console.error("❌ Error loading users:", error);
      },
    });
  }

  onSubmit(): void {
    if (this.ticketForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const formValue = this.ticketForm.value;

    const ticketRequest: CreateTicketRequest = {
      title: formValue.title,
      description: formValue.description,
      priority: formValue.priority,
      categoryId: formValue.categoryId,
      assignedToId: formValue.assignedToId,
    };

    this.ticketService.createTicket(ticketRequest).subscribe({
      next: (ticket) => {
        this.isSubmitting = false;
        this.snackBar.open("Ticket created successfully!", "Dismiss", {
          duration: 3000,
          panelClass: "success-snackbar",
        });
        this.router.navigate(["/tickets", ticket.id]);
      },
      error: (error) => {
        this.isSubmitting = false;
        let errorMessage = "Failed to create ticket";
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

  cancel(): void {
    this.router.navigate(["/tickets"]);
  }

  getErrorMessage(field: string): string {
    const control = this.ticketForm.get(field);

    if (control?.hasError("required")) {
      switch (field) {
        case "title":
          return "Title is required";
        case "description":
          return "Description is required";
        case "priority":
          return "Priority is required";
        case "categoryId":
          return "Category is required";
        default:
          return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
      }
    }

    if (control?.hasError("maxlength")) {
      const maxLength = control.errors?.["maxlength"].requiredLength;
      return `Maximum ${maxLength} characters allowed`;
    }

    if (control?.hasError("minlength")) {
      const minLength = control.errors?.["minlength"].requiredLength;
      return `Minimum ${minLength} characters required`;
    }

    return "";
  }
}
