import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface CategoryTile {
  icon: string;
  label: string;
  description: string;
  route: string;
  queryParams: Record<string, string>;
  color: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  categories: CategoryTile[] = [
    { icon: '🍔', label: 'Food & Drinks',  description: 'Fresh meals delivered',     route: '/mall', queryParams: { category: 'FOOD' },        color: '#f97316' },
    { icon: '📱', label: 'Electronics',    description: 'Phones, laptops & more',    route: '/mall', queryParams: { category: 'ELECTRONICS' }, color: '#4f46e5' },
    { icon: '👗', label: 'Fashion',         description: 'Clothing & accessories',    route: '/mall', queryParams: { category: 'CLOTHING' },    color: '#ec4899' },
    { icon: '📚', label: 'Books',           description: 'Textbooks & bestsellers',   route: '/mall', queryParams: { category: 'BOOKS' },       color: '#0ea5e9' },
{ icon: '🛒', label: 'All Products',    description: 'Browse everything',         route: '/mall', queryParams: {},                          color: '#10b981' },
  ];
}
