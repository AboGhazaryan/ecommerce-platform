import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ProductImageService {
  private apiUrl = 'http://localhost:8080/products';

  constructor(private http: HttpClient) {}

  uploadImages(productId: number, files: File[]): Observable<ProductImage[]> {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    return this.http.post<ProductImage[]>(`${this.apiUrl}/${productId}/images`, formData);
  }

  getImages(productId: number): Observable<ProductImage[]> {
    return this.http.get<ProductImage[]>(`${this.apiUrl}/${productId}/images`);
  }

  deleteImage(productId: number, imageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}/images/${imageId}`);
  }
}
