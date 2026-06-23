import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../service/authService';

@Component({
  selector: 'app-account',
  imports: [RouterLink],
  templateUrl: './account.html',
  styleUrl: './account.css'
})
export class Account {
  auth = inject(AuthService);

  logout(): void {
    this.auth.logout();
  }
}
