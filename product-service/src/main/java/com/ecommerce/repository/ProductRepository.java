package com.ecommerce.repository;

import com.ecommerce.model.ProductCategory;
import com.ecommerce.model.ProductStatus;
import com.ecommerce.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status);

    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);

    List<Product> findByUserId(Integer userId);
}
