package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.model.ProductCategory;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest, Integer userId);

    ProductResponse getProductById(Integer id, Integer userId, String role);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByCategory(ProductCategory category);

    List<ProductResponse> searchProductsByName(String name);

    List<ProductResponse> getMyProducts(Integer userId);

    List<ProductResponse> getPendingProducts();

    ProductResponse updateProductById(Integer id, ProductRequest productRequest, Integer userId, String role);

    void deleteProductById(Integer id, Integer userId, String role);

    ProductResponse approveProduct(Integer id);

    ProductResponse rejectProduct(Integer id);

    void decreaseStock(Integer id, Integer quantity);
}
