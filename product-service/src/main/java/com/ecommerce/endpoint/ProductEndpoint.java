package com.ecommerce.endpoint;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.model.ProductCategory;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductEndpoint {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-UserService-UserId") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable("id") int id,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer userId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        return ResponseEntity.ok(productService.getProductById(id, userId, role));
    }

    @GetMapping("/my-products")
    public ResponseEntity<List<ProductResponse>> getMyProducts(
            @RequestHeader("X-UserService-UserId") Integer userId) {
        return ResponseEntity.ok(productService.getMyProducts(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProductResponse>> getPendingProducts(
            @RequestHeader("X-UserService-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(productService.getPendingProducts());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByCategory(@PathVariable("category") ProductCategory category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProductByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") int id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-UserService-UserId") Integer userId,
            @RequestHeader("X-UserService-Role") String role) {
        return ResponseEntity.ok(productService.updateProductById(id, request, userId, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(
            @PathVariable("id") int id,
            @RequestHeader("X-UserService-UserId") Integer userId,
            @RequestHeader("X-UserService-Role") String role) {
        productService.deleteProductById(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable("id") Integer id,
            @RequestParam("quantity") Integer quantity) {
        productService.decreaseStock(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ProductResponse> approveProduct(
            @PathVariable("id") Integer id,
            @RequestHeader("X-UserService-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(productService.approveProduct(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ProductResponse> rejectProduct(
            @PathVariable("id") Integer id,
            @RequestHeader("X-UserService-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(productService.rejectProduct(id));
    }
}
