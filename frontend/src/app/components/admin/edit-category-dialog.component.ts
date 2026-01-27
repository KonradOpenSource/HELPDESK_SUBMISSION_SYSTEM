import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Category, UpdateCategoryRequest } from '../../models/category.model';

@Component({
  selector: 'app-edit-category-dialog',
  templateUrl: './edit-category-dialog.component.html',
  styleUrls: ['./edit-category-dialog.component.scss']
})
export class EditCategoryDialogComponent {
  categoryForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditCategoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public category: Category
  ) {
    this.categoryForm = this.fb.group({
      name: [category.name, [Validators.required, Validators.maxLength(100)]],
      description: [category.description, [Validators.required, Validators.maxLength(500)]]
    });
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      const categoryData: UpdateCategoryRequest = this.categoryForm.value;
      this.dialogRef.close(categoryData);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(field: string): string {
    const control = this.categoryForm.get(field);

    if (control?.hasError('required')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
    }

    if (control?.hasError('maxlength')) {
      const maxLength = control.errors?.['maxlength'].requiredLength;
      return `Maximum ${maxLength} characters allowed`;
    }

    return '';
  }
}
