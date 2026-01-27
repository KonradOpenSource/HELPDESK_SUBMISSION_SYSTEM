import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./components/login/login.component";
import { RegisterComponent } from "./components/register/register.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { TicketListComponent } from "./components/ticket-list/ticket-list.component";
import { TicketDetailsComponent } from "./components/ticket-details/ticket-details.component";
import { CreateTicketComponent } from "./components/create-ticket/create-ticket.component";
import { AdminPanelComponent } from "./components/admin/admin-panel.component";
import { LayoutComponent } from "./layout/layout.component";
import { AuthGuard } from "./guards/auth.guard";
import { RoleGuard } from "./guards/role.guard";

const routes: Routes = [
  { path: "", redirectTo: "/dashboard", pathMatch: "full" },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  {
    path: "",
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: "dashboard", component: DashboardComponent },
      { path: "tickets", component: TicketListComponent },
      { path: "tickets/new", component: CreateTicketComponent },
      { path: "tickets/:id", component: TicketDetailsComponent },
      {
        path: "admin",
        component: AdminPanelComponent,
        canActivate: [RoleGuard],
        data: { roles: ["ADMIN"] },
      },
    ],
  },
  { path: "**", redirectTo: "/dashboard" },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
