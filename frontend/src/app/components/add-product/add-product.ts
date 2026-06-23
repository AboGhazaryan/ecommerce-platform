import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { switchMap, of } from 'rxjs';
import { ProductService, ProductCategory } from '../../service/productService';
import { ProductImageService } from '../../service/productImageService';

function minValue(min: number) {
  return (control: AbstractControl): ValidationErrors | null => {
    const v = parseFloat(control.value);
    if (control.value === null || control.value === '') return null;
    return isNaN(v) || v < min ? { minValue: { min, actual: v } } : null;
  };
}

@Component({
  selector: 'app-add-product',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './add-product.html',
  styleUrl: './add-product.css'
})
export class AddProduct {
  private fb                  = inject(FormBuilder);
  private productService      = inject(ProductService);
  private productImageService = inject(ProductImageService);

  loading       = signal(false);
  success       = signal(false);
  error         = signal('');
  selectedFiles = signal<File[]>([]);
  previews      = signal<string[]>([]);

  readonly MAX_PHOTOS = 10;

  readonly categories: { value: ProductCategory; label: string; icon: string }[] = [
    { value: 'ELECTRONICS', label: 'Electronics', icon: '📱' },
    { value: 'CLOTHING',    label: 'Clothing',    icon: '👗' },
    { value: 'FOOD',        label: 'Food',        icon: '🍔' },
    { value: 'BOOKS',       label: 'Books',       icon: '📚' },
    { value: 'OTHER',       label: 'Other',       icon: '🛍' },
  ];

  form = this.fb.group({
    name:        ['', [Validators.required]],
    description: ['', [Validators.required, Validators.maxLength(500)]],
    price:       [null as number | null, [Validators.required, minValue(0.01)]],
    quantity:    [null as number | null, [Validators.required, Validators.min(0)]],
    category:    ['' as ProductCategory | '', [Validators.required]],
  });

  get name()        { return this.form.get('name')!; }
  get description() { return this.form.get('description')!; }
  get price()       { return this.form.get('price')!; }
  get quantity()    { return this.form.get('quantity')!; }
  get category()    { return this.form.get('category')!; }

  get descLength(): number { return (this.description.value ?? '').length; }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const incoming = Array.from(input.files);
    const total = this.selectedFiles().length + incoming.length;

    if (total > this.MAX_PHOTOS) {
      this.error.set(`Maximum ${this.MAX_PHOTOS} photos allowed. You already selected ${this.selectedFiles().length}.`);
      input.value = '';
      return;
    }

    this.error.set('');
    const newFiles = [...this.selectedFiles(), ...incoming];
    this.selectedFiles.set(newFiles);

    incoming.forEach(file => {
      const reader = new FileReader();
      reader.onload = e => {
        this.previews.set([...this.previews(), e.target?.result as string]);
      };
      reader.readAsDataURL(file);
    });

    input.value = '';
  }

  removeFile(index: number): void {
    const files = [...this.selectedFiles()];
    files.splice(index, 1);
    this.selectedFiles.set(files);

    const prev = [...this.previews()];
    prev.splice(index, 1);
    this.previews.set(prev);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set('');

    const { name, description, price, quantity, category } = this.form.value;

    this.productService.createProduct({
      name:        name!,
      description: description!,
      price:       price!,
      quantity:    quantity!,
      category:    category as ProductCategory,
    }).pipe(
      switchMap(product => {
        const files = this.selectedFiles();
        if (files.length === 0) return of(null);
        return this.productImageService.uploadImages(product.id, files);
      })
    ).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(true);
        this.form.reset();
        this.selectedFiles.set([]);
        this.previews.set([]);
        setTimeout(() => this.success.set(false), 4000);
      },
      error: err => {
        this.loading.set(false);
        const msg = err.error?.message ?? err.error?.error ?? 'Failed to add product. Please try again.';
        this.error.set(msg);
      }
    });
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }
}
