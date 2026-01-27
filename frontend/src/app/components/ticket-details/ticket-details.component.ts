import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { TicketService } from "../../services/ticket.service";
import {
  CommentService,
  Comment,
  CreateCommentRequest,
} from "../../services/comment.service";
import { Ticket } from "../../models/ticket.model";

@Component({
  selector: "app-ticket-details",
  templateUrl: "./ticket-details.component.html",
  styleUrls: ["./ticket-details.component.scss"],
})
export class TicketDetailsComponent implements OnInit {
  ticket: Ticket | null = null;
  comments: Comment[] = [];
  isLoading = true;
  newComment = "";
  isSubmittingComment = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketService: TicketService,
    private commentService: CommentService,
  ) {}

  ngOnInit(): void {
    const ticketId = Number(this.route.snapshot.paramMap.get("id"));
    if (ticketId) {
      this.loadTicket(ticketId);
      this.loadComments(ticketId);
    }
  }

  loadTicket(id: number): void {
    this.ticketService.getTicketById(id).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error loading ticket:", error);
        this.isLoading = false;
      },
    });
  }

  loadComments(ticketId: number): void {
    this.commentService.getCommentsByTicketId(ticketId).subscribe({
      next: (comments) => {
        this.comments = comments;
      },
      error: (error) => {
        console.error("Error loading comments:", error);
      },
    });
  }

  addComment(): void {
    if (!this.newComment.trim() || !this.ticket) return;

    this.isSubmittingComment = true;

    const commentRequest: CreateCommentRequest = {
      content: this.newComment,
      ticketId: this.ticket.id,
    };

    this.commentService.createComment(commentRequest).subscribe({
      next: (comment) => {
        this.comments.push(comment);
        this.newComment = "";
        this.isSubmittingComment = false;
      },
      error: (error) => {
        console.error("Error adding comment:", error);
        this.isSubmittingComment = false;
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
    return new Date(dateString).toLocaleString();
  }

  goBack(): void {
    this.router.navigate(["/tickets"]);
  }
}
