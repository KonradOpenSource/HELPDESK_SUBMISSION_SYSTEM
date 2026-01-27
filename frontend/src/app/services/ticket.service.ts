import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import {
  Ticket,
  CreateTicketRequest,
  UpdateTicketRequest,
} from "../models/ticket.model";

export interface TicketStats {
  total: number;
  newTickets: number;
  inProgress: number;
  resolved: number;
  closed: number;
  myTickets: number;
  assignedToMe: number;
}

export interface TicketPage {
  content: Ticket[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: "root",
})
export class TicketService {
  private readonly API_URL = `${environment.apiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  getTickets(
    page: number = 0,
    size: number = 10,
    sortBy: string = "createdAt",
    sortDir: string = "desc",
  ): Observable<TicketPage> {
    return this.http.get<TicketPage>(
      `${this.API_URL}?page=${page}&size=${size}&sort=${sortBy},${sortDir}`,
    );
  }

  getTicketById(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.API_URL}/${id}`);
  }

  createTicket(ticket: CreateTicketRequest): Observable<Ticket> {
    return this.http.post<Ticket>(this.API_URL, ticket);
  }

  updateTicket(id: number, ticket: UpdateTicketRequest): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.API_URL}/${id}`, ticket);
  }

  deleteTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  assignTicket(id: number, assignedToId: number): Observable<Ticket> {
    return this.http.put<Ticket>(
      `${this.API_URL}/${id}/assign?assignedToId=${assignedToId}`,
      {},
    );
  }

  getMyTickets(page: number = 0, size: number = 10): Observable<TicketPage> {
    return this.http.get<TicketPage>(
      `${this.API_URL}/my?page=${page}&size=${size}`,
    );
  }

  getAssignedTickets(
    page: number = 0,
    size: number = 10,
  ): Observable<TicketPage> {
    return this.http.get<TicketPage>(
      `${this.API_URL}/assigned?page=${page}&size=${size}`,
    );
  }

  searchTickets(filters: any): Observable<TicketPage> {
    const params = new URLSearchParams();

    // Map frontend filter names to backend parameter names
    if (filters.search && filters.search.trim()) {
      params.set("title", filters.search.trim());
    }
    if (filters.status && filters.status.trim()) {
      params.set("status", filters.status.trim());
    }
    if (filters.priority && filters.priority.trim()) {
      params.set("priority", filters.priority.trim());
    }
    if (filters.categoryId && filters.categoryId.trim()) {
      params.set("categoryId", filters.categoryId.trim());
    }
    if (filters.assignedToId && filters.assignedToId.trim()) {
      params.set("assignedToId", filters.assignedToId.trim());
    }

    return this.http.get<TicketPage>(
      `${this.API_URL}/search?${params.toString()}`,
    );
  }

  searchMyTickets(
    filters: any,
    page: number = 0,
    size: number = 10,
  ): Observable<TicketPage> {
    const params = new URLSearchParams();

    // Map frontend filter names to backend parameter names
    if (filters.search && filters.search.trim()) {
      params.set("title", filters.search.trim());
    }
    if (filters.status && filters.status.trim()) {
      params.set("status", filters.status.trim());
    }
    if (filters.priority && filters.priority.trim()) {
      params.set("priority", filters.priority.trim());
    }
    if (filters.categoryId && filters.categoryId.trim()) {
      params.set("categoryId", filters.categoryId.trim());
    }
    if (filters.assignedToId && filters.assignedToId.trim()) {
      params.set("assignedToId", filters.assignedToId.trim());
    }

    params.set("page", page.toString());
    params.set("size", size.toString());

    return this.http.get<TicketPage>(
      `${this.API_URL}/my/search?${params.toString()}`,
    );
  }

  getTicketStats(): Observable<TicketStats> {
    return this.http.get<TicketStats>(`${this.API_URL}/stats/overview`);
  }

  getMyTicketStats(): Observable<TicketStats> {
    return this.http.get<TicketStats>(`${this.API_URL}/stats/my`);
  }

  getStatusStats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/stats/status`);
  }
}
