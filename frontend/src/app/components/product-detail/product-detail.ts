import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrencyPipe, LowerCasePipe } from '@angular/common';
import { ProductService, Product } from '../../service/productService';
import { CartService } from '../../service/cartService';
import { AuthService } from '../../service/authService';
import { UserService, UserResponse } from '../../service/userService';

@Component({
  selector: 'app-product-detail',
  imports: [CurrencyPipe, LowerCasePipe],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css'
})
export class ProductDetail implements OnInit {
  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private productService = inject(ProductService);
  private cartService    = inject(CartService);
  private userService    = inject(UserService);
  authService            = inject(AuthService);

  product     = signal<Product | null>(null);
  seller      = signal<UserResponse | null>(null);
  loading     = signal(true);
  error       = signal('');
  activeIndex = signal(0);
  cartToast   = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProductById(id).subscribe({
      next: p => {
        this.product.set(p);
        this.loading.set(false);
        this.userService.getUserById(p.userId).subscribe({
          next: u => this.seller.set(u),
          error: () => {}
        });
      },
      error: () => { this.error.set('Product not found.'); this.loading.set(false); }
    });
  }

  images = computed(() => this.product()?.imageUrls ?? []);

  activeImage = computed(() => this.images()[this.activeIndex()] ?? '');

  setActive(i: number): void { this.activeIndex.set(i); }

  prev(): void {
    const len = this.images().length;
    if (len === 0) return;
    this.activeIndex.update(i => (i - 1 + len) % len);
  }

  next(): void {
    const len = this.images().length;
    if (len === 0) return;
    this.activeIndex.update(i => (i + 1) % len);
  }

  canBuy(): boolean {
    const p = this.product();
    if (!p) return false;
    if (this.authService.isAdmin()) return false;
    const myId = this.authService.getUserId();
    if (myId !== null && p.userId === myId) return false;
    return true;
  }

  addToCart(): void {
    const p = this.product();
    if (!p || !this.canBuy()) return;
    const added = this.cartService.addToCart({
      id:       p.id,
      name:     p.name,
      price:    p.price,
      imageUrl: p.imageUrls?.[0] ?? '',
      stock:    p.quantity,
    });
    this.cartToast.set(added ? 'Added to cart!' : 'Maximum quantity reached!');
    setTimeout(() => this.cartToast.set(''), 2500);
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }

  goBack(): void { this.router.navigate(['/mall']); }
}
