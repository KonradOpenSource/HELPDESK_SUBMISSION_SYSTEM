import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { NavbarComponent } from "../shared/navbar/navbar.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

@NgModule({
  declarations: [NavbarComponent],
  imports: [CommonModule, RouterModule, MatIconModule, MatButtonModule],
  exports: [NavbarComponent],
})
export class LayoutModule {}
