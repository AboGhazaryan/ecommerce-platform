import { Injectable, signal, computed, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { NotificationResponse } from './adminService';

const SEEN_IDS_KEY = 'notif_seen_ids';

@Injectable({ providedIn: 'root' })
export class UserNotificationService implements OnDestroy {
  private readonly notifUrl  = 'http://localhost:8080/notifications';
  private readonly wsUrl     = 'http://localhost:8085/ws';
  private stompClient: Client | null = null;

  notifications = signal<NotificationResponse[]>([]);
  private seenIds = signal<Set<number>>(this.loadSeenIds());

  unreadCount = computed(() => {
    return this.notifications().filter(n => !n.isRead).length;
  });

  newIds = computed(() => {
    const seen = this.seenIds();
    return new Set(
      this.notifications()
        .filter(n => !seen.has(n.id))
        .map(n => n.id)
    );
  });

  constructor(private http: HttpClient) {}

  markAsRead(id: number): void {
    this.http.patch<NotificationResponse>(`${this.notifUrl}/${id}/read`, {})
      .subscribe(updated => {
        this.notifications.update(list => list.map(n => n.id === id ? updated : n));
      });
  }

  markAllAsRead(): void {
    const currentIds = this.notifications().map(n => n.id);

    // Immediately mark all as read locally so the badge drops to 0 at once
    this.notifications.update(list => list.map(n => ({ ...n, isRead: true })));

    this.seenIds.update(set => {
      const next = new Set(set);
      currentIds.forEach(id => next.add(id));
      localStorage.setItem(SEEN_IDS_KEY, JSON.stringify([...next]));
      return next;
    });

    this.http.patch<NotificationResponse[]>(`${this.notifUrl}/read-all`, {})
      .subscribe(updated => this.notifications.set(updated));
  }

  start(userId: number, token: string, isAdmin = false): void {
    this.stop();
    this.fetchHistory(userId, isAdmin);
    this.connectWs(userId, token, isAdmin);
  }

  stop(): void {
    this.stompClient?.deactivate();
    this.stompClient = null;
    this.notifications.set([]);
  }

  ngOnDestroy(): void {
    this.stop();
  }

  private fetchHistory(userId: number, isAdmin: boolean): void {
    const url = isAdmin ? this.notifUrl : `${this.notifUrl}/user/${userId}`;
    this.http.get<NotificationResponse[]>(url)
      .subscribe(data => this.notifications.set(data));
  }

  private connectWs(userId: number, token: string, isAdmin: boolean): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.wsUrl) as WebSocket,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient!.subscribe(`/user/queue/notifications`, msg => {
          const n: NotificationResponse = JSON.parse(msg.body);
          this.notifications.update(list => [n, ...list]);
        });
        if (isAdmin) {
          this.stompClient!.subscribe('/topic/notifications/admin', msg => {
            const n: NotificationResponse = JSON.parse(msg.body);
            this.notifications.update(list =>
              list.some(x => x.id === n.id) ? list : [n, ...list]
            );
          });
        }
      },
      onStompError: frame => {
        console.error('STOMP error', frame.headers['message']);
      },
    });
    this.stompClient.activate();
  }

  private loadSeenIds(): Set<number> {
    try {
      const stored = localStorage.getItem(SEEN_IDS_KEY);
      return stored ? new Set<number>(JSON.parse(stored)) : new Set<number>();
    } catch {
      return new Set<number>();
    }
  }
}
