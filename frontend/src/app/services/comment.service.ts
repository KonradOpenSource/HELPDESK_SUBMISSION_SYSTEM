import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";

export interface Comment {
  id: number;
  content: string;
  ticketId: number;
  userId: number;
  username: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
  ticketId: number;
}

export interface UpdateCommentRequest {
  content: string;
}

@Injectable({
  providedIn: "root",
})
export class CommentService {
  private readonly API_URL = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient) {}

  getCommentsByTicketId(ticketId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.API_URL}/ticket/${ticketId}`);
  }

  getCommentById(id: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.API_URL}/${id}`);
  }

  createComment(comment: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.API_URL, comment);
  }

  updateComment(
    id: number,
    comment: UpdateCommentRequest,
  ): Observable<Comment> {
    return this.http.put<Comment>(`${this.API_URL}/${id}`, comment);
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
