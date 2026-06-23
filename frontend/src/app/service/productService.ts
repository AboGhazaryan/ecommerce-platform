import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ProductCategory = 'ELECTRONICS' | 'CLOTHING' | 'FOOD' | 'BOOKS' | 'OTHER';
export type ProductStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: ProductCategory;
  imageUrls: string[];
  userId: number;
  status: ProductStatus;
  createdAt: string;
}

export interface ProductRequest {
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: ProductCategory;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8080/products';

  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  getProductsByCategory(category: ProductCategory): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/category/${category}`);
  }

  searchProductsByName(name: string): Observable<Product[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Product[]>(`${this.apiUrl}/search`, { params });
  }

  getMyProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/my-products`);
  }

  getPendingProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/pending`);
  }

  createProduct(product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  approveProduct(id: number): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectProduct(id: number): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}/reject`, {});
  }
}
