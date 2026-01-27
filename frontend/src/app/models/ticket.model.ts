export interface Ticket {
  id: number;
  title: string;
  description: string;
  status: "NEW" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  priority: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  categoryId: number;
  categoryName: string;
  createdById: number;
  createdByName: string;
  assignedToId?: number;
  assignedToName?: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  priority: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  categoryId: number;
  assignedToId?: number;
}

export interface UpdateTicketRequest {
  title?: string;
  description?: string;
  status?: "NEW" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  priority?: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  categoryId?: number;
  assignedToId?: number;
}

export interface Category {
  id: number;
  name: string;
  description: string;
  createdAt: string;
}

export interface Comment {
  id: number;
  content: string;
  ticketId: number;
  userId: number;
  username: string;
  userFirstName: string;
  userLastName: string;
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
}
