import { Component, inject, signal, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { catchError, of, switchMap, tap } from 'rxjs';
import { ProductService, Product, ProductCategory } from '../../service/productService';
import { CartService } from '../../service/cartService';
import { AuthService } from '../../service/authService';

export interface DisplayProduct extends Product {
  discount:      number;
  originalPrice: number;
  rating:        number;
  reviewCount:   number;
  promoTag:      string;
}

@Component({
  selector: 'app-mall',
  imports: [CurrencyPipe, DecimalPipe, RouterLink],
  templateUrl: './mall.html',
  styleUrl: './mall.css'
})
export class Mall {
  private productService = inject(ProductService);
  private cartService    = inject(CartService);
  authService            = inject(AuthService);
  private route          = inject(ActivatedRoute);

  loading     = signal(true);
  error       = signal('');
  sidebarOpen = signal(false);
  wishlist    = signal(new Set<number>());
  cartToast   = signal('');

  private params = toSignal(this.route.queryParamMap);

  private rawProducts = toSignal(
    this.route.queryParamMap.pipe(
      switchMap(params => {
        this.loading.set(true);
        this.error.set('');
        const category = params.get('category') as ProductCategory | null;
        const search   = params.get('search');

        const source$ = search
          ? this.productService.searchProductsByName(search)
          : category
            ? this.productService.getProductsByCategory(category)
            : this.productService.getAllProducts();

        return source$.pipe(
          tap(() => this.loading.set(false)),
          catchError(() => {
            this.error.set('Could not load products. Please try again.');
            this.loading.set(false);
            return of([] as Product[]);
          })
        );
      })
    ),
    { initialValue: [] as Product[] }
  );

  products = computed(() => this.rawProducts().map(p => this.enrich(p)));

  activeCategory = computed(() => this.params()?.get('category') ?? null);
  activeSearch   = computed(() => this.params()?.get('search')   ?? null);

  pageTitle = computed(() => {
    const s = this.activeSearch();
    const c = this.activeCategory();
    if (s) return `Results for "${s}"`;
    if (c) return c[0].toUpperCase() + c.slice(1).toLowerCase();
    return 'All Products';
  });

  readonly skeletons = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  readonly sidebarCategories: {
    icon: string; label: string; value: ProductCategory | null; badge?: string;
  }[] = [
    { icon: '🏷',  label: 'Discounts',   value: null },
    { icon: '⭐',  label: 'Brands',      value: null },
    { icon: '👗',  label: 'Women',       value: 'CLOTHING' },
    { icon: '👟',  label: 'Shoes',       value: 'CLOTHING' },
    { icon: '🧒',  label: 'Kids',        value: 'CLOTHING',    badge: 'NEW' },
    { icon: '👔',  label: 'Men',         value: 'CLOTHING' },
    { icon: '💄',  label: 'Beauty',      value: 'OTHER' },
    { icon: '💍',  label: 'Accessories', value: 'OTHER' },
    { icon: '📱',  label: 'Electronics', value: 'ELECTRONICS' },
    { icon: '🧸',  label: 'Toys',        value: 'OTHER' },
    { icon: '📚',  label: 'Books',       value: 'BOOKS' },
    { icon: '🍔',  label: 'Food',        value: 'FOOD' },
  ];

  private enrich(p: Product): DisplayProduct {
    const id = p.id;
    const discountPool = [0, 0, 15, 20, 0, 25, 0, 30, 56, 0, 40, 0];
    const discount     = discountPool[id % discountPool.length];
    const originalPrice = discount > 0
      ? Math.round(p.price / (1 - discount / 100) * 100) / 100
      : 0;
    const rating      = +(4.1 + (id * 7 % 9) / 10).toFixed(1);
    const reviewCount = 120 + (id * 1337) % 49800;
    const promoPool   = ['', '', '', 'Good Price', '', 'Bestseller', '', 'Top Pick', '', ''];
    const promoTag    = promoPool[id % promoPool.length];
    return { ...p, discount, originalPrice, rating, reviewCount, promoTag };
  }

  toggleWishlist(id: number, event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    const next = new Set(this.wishlist());
    next.has(id) ? next.delete(id) : next.add(id);
    this.wishlist.set(next);
  }

  addToCart(product: DisplayProduct, event: Event): void {
    event.stopPropagation();
    this.cartService.addToCart({
      id:       product.id,
      name:     product.name,
      price:    product.price,
      imageUrl: product.imageUrls?.[0] ?? '',
      stock:    product.quantity,
    });
    this.cartToast.set(`✓ "${product.name}" added to cart`);
    setTimeout(() => this.cartToast.set(''), 2500);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.style.display = 'none';
    const ph = img.nextElementSibling as HTMLElement | null;
    if (ph) ph.style.display = 'flex';
  }

  canBuy(product: DisplayProduct): boolean {
    if (this.authService.isAdmin()) return false;
    const myId = this.authService.getUserId();
    if (myId !== null && product.userId === myId) return false;
    return true;
  }


  toggleSidebar(): void { this.sidebarOpen.update(v => !v); }
  closeSidebar(): void  { this.sidebarOpen.set(false); }
}
