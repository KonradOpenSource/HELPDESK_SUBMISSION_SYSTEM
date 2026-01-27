import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { HttpClientModule } from "@angular/common/http";
import { ReactiveFormsModule } from "@angular/forms";
import { FormsModule } from "@angular/forms";

import { AppComponent } from "./app.component";
import { LoginComponent } from "./components/login/login.component";
import { RegisterComponent } from "./components/register/register.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { TicketListComponent } from "./components/ticket-list/ticket-list.component";
import { TicketDetailsComponent } from "./components/ticket-details/ticket-details.component";
import { CreateTicketComponent } from "./components/create-ticket/create-ticket.component";
import { LayoutComponent } from "./layout/layout.component";
import { AdminPanelComponent } from "./components/admin/admin-panel.component";
import { CreateCategoryDialogComponent } from "./components/admin/create-category-dialog.component";
import { EditCategoryDialogComponent } from "./components/admin/edit-category-dialog.component";

import { AuthService } from "./services/auth.service";
import { AuthGuard } from "./guards/auth.guard";
import { RoleGuard } from "./guards/role.guard";
import { MaterialModule } from "./material.module";
import { LayoutModule } from "./layout/layout.module";
import { AppRoutingModule } from "./app-routing.module";
import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { JwtInterceptor } from "./interceptors/jwt.interceptor";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    TicketListComponent,
    TicketDetailsComponent,
    CreateTicketComponent,
    LayoutComponent,
    AdminPanelComponent,
    CreateCategoryDialogComponent,
    EditCategoryDialogComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    MaterialModule,
    LayoutModule,
  ],
  providers: [
    AuthService,
    AuthGuard,
    RoleGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
