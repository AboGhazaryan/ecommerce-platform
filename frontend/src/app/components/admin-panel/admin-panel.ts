import { Component, inject, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../service/authService';
import { UserService, UserResponse } from '../../service/userService';
import { ProductService, Product } from '../../service/productService';
import { OrderService, OrderResponse, OrderStatus } from '../../service/orderService';
import { AdminService, NotificationResponse, PaymentResponse } from '../../service/adminService';

type Tab = 'users' | 'orders' | 'products' | 'notifications' | 'payments';

@Component({
  selector: 'app-admin-panel',
  imports: [],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel implements OnInit {
  private auth           = inject(AuthService);
  private userService    = inject(UserService);
  private productService = inject(ProductService);
  private orderService   = inject(OrderService);
  private adminService   = inject(AdminService);
  private router         = inject(Router);
  private route          = inject(ActivatedRoute);

  activeTab = signal<Tab>('users');
  loading   = signal(false);
  error     = signal('');

  users         = signal<UserResponse[]>([]);
  products      = signal<Product[]>([]);
  orders        = signal<OrderResponse[]>([]);
  notifications = signal<NotificationResponse[]>([]);
  payments      = signal<PaymentResponse[]>([]);

  readonly orderStatuses: OrderStatus[] = ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'];

  ngOnInit(): void {
    if (!this.auth.isAdmin()) { this.router.navigate(['/']); return; }
    const tab = this.route.snapshot.queryParamMap.get('tab') as Tab | null;
    const initial: Tab = (tab && ['users','products','orders','notifications','payments'].includes(tab))
      ? tab : 'users';
    this.activeTab.set(initial);
    this.loadTab(initial);
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
    this.loadTab(tab);
  }

  loadTab(tab: Tab): void {
    this.loading.set(true);
    this.error.set('');
    switch (tab) {
      case 'users':
        this.userService.getAllUsers().subscribe({
          next: d => { this.users.set(d); this.loading.set(false); },
          error: () => { this.error.set('Failed to load users'); this.loading.set(false); }
        });
        break;
      case 'products':
        this.loadAllProducts();
        break;
      case 'orders':
        this.orderService.getAllOrders().subscribe({
          next: d => { this.orders.set(d); this.loading.set(false); },
          error: () => { this.error.set('Failed to load orders'); this.loading.set(false); }
        });
        break;
      case 'notifications':
        this.adminService.getAllNotifications().subscribe({
          next: d => { this.notifications.set(d); this.loading.set(false); },
          error: () => { this.error.set('Failed to load notifications'); this.loading.set(false); }
        });
        break;
      case 'payments':
        this.adminService.getAllPayments().subscribe({
          next: d => { this.payments.set(d); this.loading.set(false); },
          error: () => { this.error.set('Failed to load payments'); this.loading.set(false); }
        });
        break;
    }
  }

  // ── Users ──
  blockUser(user: UserResponse): void {
    const call = user.blocked
      ? this.userService.unblockUser(user.id)
      : this.userService.blockUser(user.id);
    call.subscribe({
      next: updated => this.users.update(list => list.map(u => u.id === updated.id ? updated : u)),
      error: () => this.error.set('Failed to update user status')
    });
  }

  deleteUser(id: number): void {
    if (!confirm('Delete this user? This action cannot be undone.')) return;
    this.userService.deleteUser(id).subscribe({
      next: () => this.users.update(list => list.filter(u => u.id !== id)),
      error: () => this.error.set('Failed to delete user')
    });
  }

  // ── Products ──
  approveProduct(id: number): void {
    this.productService.approveProduct(id).subscribe({
      next: updated => this.products.update(list => list.map(p => p.id === id ? updated : p)),
      error: () => this.error.set('Failed to approve product')
    });
  }

  rejectProduct(id: number): void {
    this.productService.rejectProduct(id).subscribe({
      next: updated => this.products.update(list => list.map(p => p.id === id ? updated : p)),
      error: () => this.error.set('Failed to reject product')
    });
  }

  deleteProduct(id: number): void {
    if (!confirm('Delete this product?')) return;
    this.productService.deleteProduct(id).subscribe({
      next: () => this.products.update(list => list.filter(p => p.id !== id)),
      error: () => this.error.set('Failed to delete product')
    });
  }

  loadAllProducts(): void {
    // For admin, also load PENDING and REJECTED products
    // We call getPendingProducts separately and merge
    this.loading.set(true);
    this.productService.getAllProducts().subscribe({
      next: approved => {
        this.productService.getPendingProducts().subscribe({
          next: pending => {
            const ids = new Set(approved.map(p => p.id));
            const merged = [...approved, ...pending.filter(p => !ids.has(p.id))];
            this.products.set(merged);
            this.loading.set(false);
          },
          error: () => { this.products.set(approved); this.loading.set(false); }
        });
      },
      error: () => { this.error.set('Failed to load products'); this.loading.set(false); }
    });
  }

  // ── Orders ──
  updateOrderStatus(id: number, event: Event): void {
    const status = (event.target as HTMLSelectElement).value as OrderStatus;
    this.orderService.updateOrderStatus(id, status).subscribe({
      next: updated => this.orders.update(list => list.map(o => o.id === id ? updated : o)),
      error: () => this.error.set('Failed to update order status')
    });
  }

  deleteOrder(id: number): void {
    if (!confirm('Delete this order?')) return;
    this.orderService.deleteOrder(id).subscribe({
      next: () => this.orders.update(list => list.filter(o => o.id !== id)),
      error: () => this.error.set('Failed to delete order')
    });
  }

  // ── Notifications ──
  deleteNotification(id: number): void {
    this.adminService.deleteNotification(id).subscribe({
      next: () => this.notifications.update(list => list.filter(n => n.id !== id)),
      error: () => this.error.set('Failed to delete notification')
    });
  }

  // ── Helpers ──
  statusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected',
      CONFIRMED: 'badge-confirmed', SHIPPED: 'badge-shipped',
      DELIVERED: 'badge-delivered', CANCELLED: 'badge-cancelled',
      COMPLETED: 'badge-approved', REFUNDED: 'badge-pending', FAILED: 'badge-rejected'
    };
    return map[status] ?? 'badge-pending';
  }

  formatDate(d: string): string {
    return d ? new Date(d).toLocaleString() : '—';
  }
}
