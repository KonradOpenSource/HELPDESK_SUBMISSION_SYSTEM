import { Injectable } from "@angular/core";
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: "root",
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean {
    const expectedRoles = route.data["roles"] as string[];

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(["/login"]);
      return false;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(["/login"]);
      return false;
    }

    const hasRequiredRole = expectedRoles.some(
      (role) => user.role === role || this.authService.hasRole(role),
    );

    if (!hasRequiredRole) {
      this.router.navigate(["/unauthorized"]);
      return false;
    }

    return true;
  }
}
