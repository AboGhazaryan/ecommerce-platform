import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService, CartItem } from '../../service/cartService';
import { OrderService, OrderResponse } from '../../service/orderService';

type CheckoutState = 'idle' | 'loading' | 'confirmed';

@Component({
  selector: 'app-checkout',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class Checkout {
  private cartService  = inject(CartService);
  private orderService = inject(OrderService);

  state          = signal<CheckoutState>('idle');
  error          = signal('');
  confirmedOrder = signal<OrderResponse | null>(null);

  private snapshot: CartItem[] = [];

  readonly items = this.cartService.items;
  readonly total = this.cartService.total;

  placeOrder(): void {
    this.snapshot = [...this.cartService.items()];
    this.state.set('loading');
    this.error.set('');

    this.orderService.createOrder({
      items: this.snapshot.map(i => ({ productId: i.productId, quantity: i.quantity })),
    }).subscribe({
      next: order => {
        this.cartService.clearCart();
        this.confirmedOrder.set(order);
        this.state.set('confirmed');
      },
      error: err => {
        const msg = err.error?.message ?? err.error?.error ?? 'Order failed. Please try again.';
        this.error.set(msg);
        this.state.set('idle');
      },
    });
  }

  nameFor(productId: number): string {
    return this.snapshot.find(i => i.productId === productId)?.name ?? `Product #${productId}`;
  }

  imageFor(productId: number): string {
    return this.snapshot.find(i => i.productId === productId)?.imageUrl ?? '';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING:   'Order received',
      CONFIRMED: 'Confirmed',
      SHIPPED:   'Shipped',
      DELIVERED: 'Delivered',
      CANCELLED: 'Cancelled',
    };
    return map[status] ?? status;
  }
}
