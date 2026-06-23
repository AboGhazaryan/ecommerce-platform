import { Component, inject, signal, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService, Product } from '../../service/productService';
import { AuthService } from '../../service/authService';

@Component({
  selector: 'app-pending-products',
  imports: [],
  templateUrl: './pending-products.html',
  styleUrl: './pending-products.css'
})
export class PendingProducts implements OnInit {
  private productService = inject(ProductService);
  private auth           = inject(AuthService);
  private router         = inject(Router);

  products = signal<Product[]>([]);
  loading  = signal(true);
  error    = signal('');

  ngOnInit(): void {
    if (!this.auth.isAdmin()) {
      this.router.navigate(['/']);
      return;
    }
    this.loadPending();
  }

  loadPending(): void {
    this.loading.set(true);
    this.productService.getPendingProducts().subscribe({
      next: list => { this.products.set(list); this.loading.set(false); },
      error: ()  => { this.error.set('Failed to load pending products'); this.loading.set(false); }
    });
  }

  approve(id: number): void {
    this.productService.approveProduct(id).subscribe({
      next: () => this.products.update(list => list.filter(p => p.id !== id)),
      error: err => this.error.set(err.error?.message ?? 'Failed to approve')
    });
  }

  reject(id: number): void {
    this.productService.rejectProduct(id).subscribe({
      next: () => this.products.update(list => list.filter(p => p.id !== id)),
      error: err => this.error.set(err.error?.message ?? 'Failed to reject')
    });
  }
}
