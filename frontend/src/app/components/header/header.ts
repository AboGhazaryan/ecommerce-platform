import { Component, inject, signal, effect, HostListener } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NotificationResponse } from '../../service/adminService';
import { AuthService } from '../../service/authService';
import { CartService } from '../../service/cartService';
import { UserNotificationService } from '../../service/userNotificationService';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  auth    = inject(AuthService);
  cart    = inject(CartService);
  notifs  = inject(UserNotificationService);
  private router = inject(Router);

  searchQuery   = signal('');
  showNotifs    = signal(false);
  openedNewIds  = signal<Set<number>>(new Set());

  constructor() {
    effect(() => {
      if (this.auth.isLoggedIn()) {
        const userId = this.auth.getUserId();
        const token = this.auth.getToken();
        if (userId && token) this.notifs.start(userId, token, this.auth.isAdmin());
      } else {
        this.notifs.stop();
        this.showNotifs.set(false);
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(e: MouseEvent): void {
    if (this.showNotifs() && !(e.target as Element).closest('.notif-wrap')) {
      this.showNotifs.set(false);
    }
  }

  onSearch(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  submitSearch(event: Event) {
    event.preventDefault();
    const q = this.searchQuery().trim();
    if (q) this.router.navigate(['/mall'], { queryParams: { search: q } });
  }

  goToAccount() {
    this.router.navigate([this.auth.isLoggedIn() ? '/account' : '/login']);
  }

  goToCart() { this.router.navigate(['/cart']); }

  openNotifMgmt(n: NotificationResponse): void {
    this.showNotifs.set(false);
    const tabMap: Record<string, string> = {
      PRODUCT_CREATED: 'products',
      PRODUCT_UPDATED: 'products',
      ORDER_CREATED:   'orders',
      PAYMENT_COMPLETED: 'payments',
      PAYMENT_FAILED:    'payments',
    };
    const tab = tabMap[n.type] ?? 'notifications';
    this.router.navigate(['/admin'], { queryParams: { tab } });
  }

  toggleNotifs(e: MouseEvent): void {
    e.stopPropagation();
    const next = !this.showNotifs();
    this.showNotifs.set(next);
    if (next) {
      // capture which IDs are new before marking all read, so highlights stay visible
      this.openedNewIds.set(new Set(this.notifs.newIds()));
      this.notifs.markAllAsRead();
    }
  }

  formatTime(d: string): string {
    if (!d) return '';
    const diffMs = Date.now() - new Date(d).getTime();
    const mins = Math.floor(diffMs / 60_000);
    if (mins < 1) return 'Just now';
    if (mins < 60) return `${mins}m ago`;
    const hours = Math.floor(mins / 60);
    if (hours < 24) return `${hours}h ago`;
    return new Date(d).toLocaleDateString();
  }
}
