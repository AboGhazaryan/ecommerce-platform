import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { Product, ProductService } from '../../service/productService';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList {
  private productService = inject(ProductService);

  products = toSignal(
    this.productService.getAllProducts().pipe(catchError(() => of([] as Product[]))),
    { initialValue: [] as Product[] }
  );
}
