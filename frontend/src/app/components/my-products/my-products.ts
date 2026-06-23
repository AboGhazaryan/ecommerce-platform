import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProductService, Product, ProductRequest, ProductCategory } from '../../service/productService';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-my-products',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './my-products.html',
  styleUrl: './my-products.css'
})
export class MyProducts implements OnInit {
  private productService = inject(ProductService);
  private fb = inject(FormBuilder);

  products = signal<Product[]>([]);
  loading  = signal(true);
  error    = signal('');
  editingId = signal<number | null>(null);
  editError = signal('');
  editLoading = signal(false);

  readonly categories: { value: ProductCategory; label: string }[] = [
    { value: 'ELECTRONICS', label: 'Electronics' },
    { value: 'CLOTHING',    label: 'Clothing' },
    { value: 'FOOD',        label: 'Food' },
    { value: 'BOOKS',       label: 'Books' },
    { value: 'OTHER',       label: 'Other' },
  ];

  editForm = this.fb.group({
    name:        ['', Validators.required],
    description: ['', Validators.required],
    price:       [0, [Validators.required, Validators.min(0.01)]],
    quantity:    [0, [Validators.required, Validators.min(0)]],
    category:    ['' as ProductCategory, Validators.required],
  });

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService.getMyProducts().subscribe({
      next: list => { this.products.set(list); this.loading.set(false); },
      error: ()  => { this.error.set('Failed to load products'); this.loading.set(false); }
    });
  }

  startEdit(product: Product): void {
    this.editingId.set(product.id);
    this.editError.set('');
    this.editForm.setValue({
      name:        product.name,
      description: product.description,
      price:       product.price,
      quantity:    product.quantity,
      category:    product.category,
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.editError.set('');
  }

  saveEdit(id: number): void {
    if (this.editForm.invalid) { this.editForm.markAllAsTouched(); return; }
    this.editLoading.set(true);
    const v = this.editForm.value;
    const req: ProductRequest = {
      name:        v.name!,
      description: v.description!,
      price:       v.price!,
      quantity:    v.quantity!,
      category:    v.category as ProductCategory,
    };
    this.productService.updateProduct(id, req).subscribe({
      next: updated => {
        this.products.update(list => list.map(p => p.id === id ? updated : p));
        this.editingId.set(null);
        this.editLoading.set(false);
      },
      error: err => {
        this.editError.set(err.error?.message ?? 'Failed to update product');
        this.editLoading.set(false);
      }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this product?')) return;
    this.productService.deleteProduct(id).subscribe({
      next: () => this.products.update(list => list.filter(p => p.id !== id)),
      error: err => this.error.set(err.error?.message ?? 'Failed to delete product')
    });
  }

  statusLabel(status: string): string {
    return { PENDING: 'Pending approval', APPROVED: 'Approved', REJECTED: 'Rejected' }[status] ?? status;
  }

  statusClass(status: string): string {
    return { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' }[status] ?? '';
  }
}
