import { Component, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../service/cartService';

@Component({
  selector: 'app-cart',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.css'
})
export class Cart {
  cart = inject(CartService);

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }

  decrement(productId: number, current: number): void {
    this.cart.updateQuantity(productId, current - 1);
  }

  increment(productId: number, current: number): void {
    this.cart.updateQuantity(productId, current + 1);
  }

  onQtyInput(productId: number, event: Event): void {
    const val = parseInt((event.target as HTMLInputElement).value, 10);
    if (!isNaN(val)) this.cart.updateQuantity(productId, val);
  }
}
