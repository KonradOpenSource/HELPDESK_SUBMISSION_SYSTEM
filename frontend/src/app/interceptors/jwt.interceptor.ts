import { Injectable } from "@angular/core";
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { AuthService } from "../services/auth.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // Add JWT token to Authorization header
    const token = this.authService.getToken();

    if (token) {
      req = this.addToken(req, token);
    }

    // Handle 401 Unauthorized responses
    return next.handle(req).pipe(
      catchError((error) => {
        if (error.status === 401) {
          // Token expired or invalid, logout user
          this.authService.logout();
        }
        return throwError(error);
      }),
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
