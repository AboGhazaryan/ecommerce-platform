import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, switchMap, map } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
}

interface UserProfile {
  id: number;
  name: string;
  surname: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY      = 'auth_token';
  private readonly USER_ID_KEY    = 'auth_user_id';
  private readonly USER_NAME_KEY  = 'auth_user_name';
  private readonly USER_SUR_KEY   = 'auth_user_surname';
  private readonly USER_EMAIL_KEY = 'auth_user_email';
  private apiUrl = 'http://localhost:8080/users';

  isLoggedIn   = signal(!!localStorage.getItem(this.TOKEN_KEY));
  userName     = signal(localStorage.getItem('auth_user_name')    ?? '');
  userSurname  = signal(localStorage.getItem('auth_user_surname') ?? '');
  userEmail    = signal(localStorage.getItem('auth_user_email')   ?? '');

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_ID_KEY, String(res.userId));
        this.isLoggedIn.set(true);
      }),
      switchMap(res =>
        this.http.get<UserProfile>(`${this.apiUrl}/${res.userId}`).pipe(
          tap(profile => {
            localStorage.setItem(this.USER_NAME_KEY,  profile.name);
            localStorage.setItem(this.USER_SUR_KEY,   profile.surname);
            localStorage.setItem(this.USER_EMAIL_KEY, profile.email);
            this.userName.set(profile.name);
            this.userSurname.set(profile.surname);
            this.userEmail.set(profile.email);
          }),
          map(() => res)
        )
      )
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.USER_NAME_KEY);
    localStorage.removeItem(this.USER_SUR_KEY);
    localStorage.removeItem(this.USER_EMAIL_KEY);
    this.isLoggedIn.set(false);
    this.userName.set('');
    this.userSurname.set('');
    this.userEmail.set('');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUserId(): number | null {
    const raw = localStorage.getItem(this.USER_ID_KEY);
    return raw ? parseInt(raw, 10) : null;
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role ?? null;
    } catch {
      return null;
    }
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  getFullName(): string {
    return [this.userName(), this.userSurname()].filter(Boolean).join(' ');
  }

  updateProfile(name: string, surname: string, email: string): void {
    localStorage.setItem(this.USER_NAME_KEY,  name);
    localStorage.setItem(this.USER_SUR_KEY,   surname);
    localStorage.setItem(this.USER_EMAIL_KEY, email);
    this.userName.set(name);
    this.userSurname.set(surname);
    this.userEmail.set(email);
  }
}
