package com.ecommerce.repository;

import com.ecommerce.model.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct_Id(Integer productId);
    int countByProduct_Id(Integer productId);
}
