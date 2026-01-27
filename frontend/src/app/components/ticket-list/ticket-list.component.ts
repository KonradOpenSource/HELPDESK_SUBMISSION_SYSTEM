import { Component, OnInit, OnDestroy } from "@angular/core";
import { Subject, takeUntil, debounceTime } from "rxjs";
import { TicketService, TicketPage } from "../../services/ticket.service";
import { Ticket } from "../../models/ticket.model";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: "app-ticket-list",
  templateUrl: "./ticket-list.component.html",
  styleUrls: ["./ticket-list.component.scss"],
})
export class TicketListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  tickets: Ticket[] = [];
  displayedColumns: string[] = [
    "id",
    "title",
    "status",
    "priority",
    "category",
    "assignedTo",
    "createdAt",
    "actions",
  ];
  isLoading = true;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  filters = {
    status: "",
    priority: "",
    categoryId: "",
    assignedToId: "",
    search: "",
  };

  constructor(
    private ticketService: TicketService,
    private http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.loadTickets();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadTickets(): void {
    this.isLoading = true;

    const hasFilters = Object.values(this.filters).some(
      (value) => value !== "",
    );

    if (hasFilters) {
      // Use regular getTickets with filters as query params
      const params = new URLSearchParams();

      if (this.filters.search && this.filters.search.trim()) {
        params.set("title", this.filters.search.trim());
      }
      if (this.filters.status && this.filters.status.trim()) {
        params.set("status", this.filters.status.trim());
      }
      if (this.filters.priority && this.filters.priority.trim()) {
        params.set("priority", this.filters.priority.trim());
      }
      if (this.filters.categoryId && this.filters.categoryId.trim()) {
        params.set("categoryId", this.filters.categoryId.trim());
      }
      if (this.filters.assignedToId && this.filters.assignedToId.trim()) {
        params.set("assignedToId", this.filters.assignedToId.trim());
      }

      params.set("page", this.currentPage.toString());
      params.set("size", this.pageSize.toString());

      console.log("Making request with params:", params.toString());

      this.http
        .get<TicketPage>(
          `${this.ticketService["API_URL"]}?${params.toString()}`,
        )
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: TicketPage) => {
            this.tickets = response.content;
            this.totalElements = response.totalElements;
            this.isLoading = false;
          },
          error: (error) => {
            console.error("Error filtering tickets:", error);
            // Fallback to regular tickets
            this.ticketService
              .getTickets(this.currentPage, this.pageSize)
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: (response: TicketPage) => {
                  this.tickets = response.content;
                  this.totalElements = response.totalElements;
                  this.isLoading = false;
                },
                error: (fallbackError) => {
                  console.error("Error loading tickets:", fallbackError);
                  this.isLoading = false;
                },
              });
          },
        });
    } else {
      this.ticketService
        .getTickets(this.currentPage, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: TicketPage) => {
            this.tickets = response.content;
            this.totalElements = response.totalElements;
            this.isLoading = false;
          },
          error: (error) => {
            console.error("Error loading tickets:", error);
            this.isLoading = false;
          },
        });
    }
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTickets();
  }

  applyFilters(): void {
    console.log("Applying filters:", this.filters);
    this.currentPage = 0;
    this.loadTickets();
  }

  clearFilters(): void {
    console.log("Clearing filters...");
    // Reset all filter values to defaults
    this.filters = {
      status: "",
      priority: "",
      categoryId: "",
      assignedToId: "",
      search: "",
    };
    console.log("Filters after reset:", this.filters);

    // Reset pagination
    this.currentPage = 0;

    // Force immediate refresh with clean state
    this.loadTickets();
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
}
