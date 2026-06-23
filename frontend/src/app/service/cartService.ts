import { Injectable, Signal, computed, signal } from '@angular/core';

export interface CartItem {
  productId: number;
  name:      string;
  price:     number;
  imageUrl:  string;
  quantity:  number;
  stock:     number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly STORAGE_KEY = 'shopmall_cart';

  private _items = signal<CartItem[]>(this.load());

  readonly items: Signal<CartItem[]> = this._items.asReadonly();
  readonly count = computed(() => this._items().reduce((s, i) => s + i.quantity, 0));
  readonly total = computed(() => this._items().reduce((s, i) => s + i.price * i.quantity, 0));

  addToCart(product: { id: number; name: string; price: number; imageUrl: string; stock: number }, qty = 1): boolean {
    let added = false;
    this._items.update(items => {
      const idx = items.findIndex(i => i.productId === product.id);
      if (idx >= 0) {
        const current = items[idx].quantity;
        const newQty = Math.min(current + qty, product.stock);
        if (newQty === current) return items;
        added = true;
        return items.map((item, i) =>
          i === idx ? { ...item, quantity: newQty, stock: product.stock } : item
        );
      }
      added = true;
      return [...items, {
        productId: product.id,
        name:      product.name,
        price:     product.price,
        imageUrl:  product.imageUrl ?? '',
        quantity:  Math.min(qty, product.stock),
        stock:     product.stock,
      }];
    });
    if (added) this.save();
    return added;
  }

  removeFromCart(productId: number): void {
    this._items.update(items => items.filter(i => i.productId !== productId));
    this.save();
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) { this.removeFromCart(productId); return; }
    this._items.update(items =>
      items.map(i => i.productId === productId
        ? { ...i, quantity: Math.min(quantity, i.stock || quantity) }
        : i)
    );
    this.save();
  }

  clearCart(): void {
    this._items.set([]);
    this.save();
  }

  private load(): CartItem[] {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      if (!raw) return [];
      const items = JSON.parse(raw) as CartItem[];
      return items.map(i => ({ ...i, stock: i.stock ?? 9999 }));
    } catch {
      return [];
    }
  }

  private save(): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this._items()));
  }
}
