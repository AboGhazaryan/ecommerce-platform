package com.ecommerce.service;

import com.ecommerce.dto.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> uploadImages(Integer productId, MultipartFile[] files);
    List<ProductImageResponse> getImages(Integer productId);
    void deleteImage(Integer productId, Integer imageId);
    void deleteAllImages(Integer productId);
}
