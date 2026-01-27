import { Component, OnInit, OnDestroy } from "@angular/core";
import { Subject, takeUntil } from "rxjs";
import { AuthService } from "../../services/auth.service";
import { TicketService } from "../../services/ticket.service";
import { User } from "../../models/user.model";
import { Ticket } from "../../models/ticket.model";

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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDashboardData(): void {
    this.isLoading = true;

    // Load general statistics for Total Tickets and New Tickets
    this.ticketService
      .getTicketStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.stats = {
            totalTickets: stats.total || 0,
            newTickets: stats.newTickets || 0,
            inProgressTickets: stats.inProgress || 0,
            resolvedTickets: stats.resolved || 0,
            myTickets: stats.myTickets || 0,
            assignedToMe: stats.assignedToMe || 0,
          };
        },
        error: (error) => {
          console.error("Error loading stats:", error);
        },
      });

    // Load user-specific statistics for my tickets and assigned to me
    this.ticketService
      .getMyTicketStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (myStats) => {
          this.stats.myTickets = myStats.myTickets || 0;
          this.stats.assignedToMe = myStats.assignedToMe || 0;
        },
        error: (error) => {
          console.error("Error loading my stats:", error);
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

  getStatusColor(status: string): string {
    switch (status) {
      case "NEW":
        return "#f44336";
      case "IN_PROGRESS":
        return "#ff9800";
      case "RESOLVED":
        return "#4caf50";
      case "CLOSED":
        return "#9e9e9e";
      default:
        return "#666";
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case "LOW":
        return "#4caf50";
      case "MEDIUM":
        return "#ff9800";
      case "HIGH":
        return "#ff5722";
      case "CRITICAL":
        return "#f44336";
      default:
        return "#666";
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  getGreeting(): string {
    const hour = new Date().getHours();
    if (hour < 12) return "Good morning";
    if (hour < 18) return "Good afternoon";
    return "Good evening";
  }

  getRoleDisplayName(role: string): string {
    switch (role) {
      case "USER":
        return "User";
      case "AGENT":
        return "Agent";
      case "ADMIN":
        return "Administrator";
      default:
        return role;
    }
  }
}
