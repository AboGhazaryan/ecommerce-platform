import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';
import { OrderResponse, OrderService } from '../../service/orderService';

@Component({
  selector: 'app-order-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css'
})
export class OrderList {
  private orderService = inject(OrderService);

  loading = signal(true);
  error   = signal('');

  orders = toSignal(
    this.orderService.getMyOrders().pipe(
      tap(() => this.loading.set(false)),
      catchError(() => {
        this.error.set('Could not load orders. Please check your connection and try again.');
        this.loading.set(false);
        return of([] as OrderResponse[]);
      })
    ),
    { initialValue: [] as OrderResponse[] }
  );

  readonly skeletons = [1, 2, 3];

  statusStep(status: string): number {
    const map: Record<string, number> = {
      PENDING: 1, CONFIRMED: 2, SHIPPED: 3, DELIVERED: 4
    };
    return map[status] ?? 0;
  }

  progressPct(status: string): number {
    return (this.statusStep(status) / 4) * 100;
  }
}
