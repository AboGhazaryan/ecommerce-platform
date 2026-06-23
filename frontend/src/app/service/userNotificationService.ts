import { Injectable, signal, computed, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client } from '@stomp/stompjs';
// @ts-ignore
import SockJS from 'sockjs-client';
import { NotificationResponse } from './adminService';

const SEEN_IDS_KEY = 'notif_seen_ids';

@Injectable({ providedIn: 'root' })
export class UserNotificationService implements OnDestroy {
  private readonly apiUrl      = 'http://localhost:8080/notifications/user';
  private readonly allNotifsUrl = 'http://localhost:8080/notifications';
  private readonly wsUrl       = 'http://localhost:8085/ws';
  private stompClient: Client | null = null;

  notifications = signal<NotificationResponse[]>([]);

  private seenIds = signal<Set<number>>(
    new Set(JSON.parse(localStorage.getItem(SEEN_IDS_KEY) ?? '[]'))
  );

  unreadCount = computed(()=>{
    const seen = this.seenIds();
    return this.notifications().filter(n=> !seen.has(n.id)).length;
  });


  newIds = computed(() => {
    const seen = this.seenIds();
    return new Set(
      this.notifications()
        .filter(n => !seen.has(n.id))
        .map(n => n.id)
    );
  });

  markAllRead(): void {
    const allIds= this.notifications().map(n => n.id);
    localStorage.setItem(SEEN_IDS_KEY, JSON.stringify(allIds));
    this.seenIds.set(new Set(allIds))
  }

  constructor(private http: HttpClient) {}

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
    const url = isAdmin ? this.allNotifsUrl : `${this.apiUrl}/${userId}`;
    this.http.get<NotificationResponse[]>(url)
      .subscribe(data => this.notifications.set(data));
  }

  private connectWs(userId: number, token: string, isAdmin: boolean): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.wsUrl) as WebSocket,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        // subscribe to own notifications
        this.stompClient!.subscribe(`/topic/notifications/${userId}`, msg => {
          const n: NotificationResponse = JSON.parse(msg.body);
          this.notifications.update(list => [n, ...list]);
        });
        // admin also subscribes to the system-wide admin topic
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
}
