import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NotificationResponse {
  id: number;
  userId: number;
  userEmail: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

export interface PaymentResponse {
  id: number;
  orderId: number;
  userId: number;
  userEmail: string;
  amount: number;
  status: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private notifUrl    = 'http://localhost:8080/notifications';
  private paymentUrl  = 'http://localhost:8080/payments';

  constructor(private http: HttpClient) {}

  getAllNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.notifUrl);
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.notifUrl}/${id}`);
  }

  getAllPayments(): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(this.paymentUrl);
  }

  markAsRead(id: number): Observable<NotificationResponse> {
    return this.http.patch<NotificationResponse>(`${this.notifUrl}/${id}/read`, {});
  }

  markAllAsRead(): Observable<NotificationResponse[]> {
    return this.http.patch<NotificationResponse[]>(`${this.notifUrl}/read-all`, {});
  }
}
