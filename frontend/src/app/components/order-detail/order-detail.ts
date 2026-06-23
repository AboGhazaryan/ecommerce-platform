import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, switchMap, tap } from 'rxjs';
import { OrderResponse, OrderService } from '../../service/orderService';

@Component({
  selector: 'app-order-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.css'
})
export class OrderDetail {
  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);

  loading = signal(true);
  error   = signal('');

  readonly steps = ['Pending', 'Confirmed', 'Shipped', 'Delivered'];

  order = toSignal<OrderResponse | null>(
    this.route.paramMap.pipe(
      switchMap(params =>
        this.orderService.getOrderById(Number(params.get('id'))).pipe(
          tap(() => this.loading.set(false)),
          catchError(() => {
            this.error.set('Order not found or you do not have permission to view it.');
            this.loading.set(false);
            return of(null);
          })
        )
      )
    ),
    { initialValue: null }
  );

  statusStep(status: string): number {
    const map: Record<string, number> = {
      PENDING: 0, CONFIRMED: 1, SHIPPED: 2, DELIVERED: 3
    };
    return map[status] ?? -1;
  }
}
