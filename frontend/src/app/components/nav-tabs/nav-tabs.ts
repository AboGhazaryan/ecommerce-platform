import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../service/authService';
import { CartService } from '../../service/cartService';

@Component({
  selector: 'app-nav-tabs',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './nav-tabs.html',
  styleUrl: './nav-tabs.css'
})
export class NavTabs {
  auth = inject(AuthService);
  cart = inject(CartService);
}
