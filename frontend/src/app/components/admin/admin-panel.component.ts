import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../services/category.service';
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../../models/category.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { CreateCategoryDialogComponent } from './create-category-dialog.component';
import { EditCategoryDialogComponent } from './edit-category-dialog.component';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {
  categories: Category[] = [];
  isLoading = false;
  displayedColumns: string[] = ['id', 'name', 'description', 'createdAt', 'actions'];

  constructor(
    private categoryService: CategoryService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.snackBar.open('Failed to load categories', 'Close', {
          duration: 3000,
          panelClass: 'error-snackbar'
        });
        this.isLoading = false;
      }
    });
  }

  openCreateCategoryDialog(): void {
    const dialogRef = this.dialog.open(CreateCategoryDialogComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createCategory(result);
      }
    });
  }

  openEditCategoryDialog(category: Category): void {
    const dialogRef = this.dialog.open(EditCategoryDialogComponent, {
      width: '400px',
      data: category
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateCategory(category.id, result);
      }
    });
  }

  createCategory(request: CreateCategoryRequest): void {
    this.categoryService.createCategory(request as Category).subscribe({
      next: () => {
        this.snackBar.open('Category created successfully!', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
        this.loadCategories();
      },
      error: (error) => {
        console.error('Error creating category:', error);
        this.snackBar.open('Failed to create category', 'Close', {
          duration: 3000,
          panelClass: 'error-snackbar'
        });
      }
    });
  }

  updateCategory(id: number, request: UpdateCategoryRequest): void {
    this.categoryService.updateCategory(id, request as Category).subscribe({
      next: () => {
        this.snackBar.open('Category updated successfully!', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
        this.loadCategories();
      },
      error: (error) => {
        console.error('Error updating category:', error);
        this.snackBar.open('Failed to update category', 'Close', {
          duration: 3000,
          panelClass: 'error-snackbar'
        });
      }
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.snackBar.open('Category deleted successfully!', 'Close', {
            duration: 3000,
            panelClass: 'success-snackbar'
          });
          this.loadCategories();
        },
        error: (error) => {
          console.error('Error deleting category:', error);
          this.snackBar.open('Failed to delete category', 'Close', {
            duration: 3000,
            panelClass: 'error-snackbar'
          });
        }
      });
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
