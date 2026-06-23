package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductImageResponse;
import com.ecommerce.exception.*;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.entity.ProductImage;
import com.ecommerce.repository.ProductImageRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private static final int MAX_IMAGES = 10;

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Value("${app.upload.dir:uploads/product-images}")
    private String uploadDir;

    @Value("${app.upload.url-prefix:/images}")
    private String urlPrefix;

    @Override
    @Transactional
    public List<ProductImageResponse> uploadImages(Integer productId, MultipartFile[] files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFountException("Product not found: " + productId));

        int existing = productImageRepository.countByProduct_Id(productId);
        if (existing + files.length > MAX_IMAGES) {
            throw new ToManyImagesException("Limit exceeded. Max images: " + MAX_IMAGES);
        }

        List<ProductImageResponse> responses = new ArrayList<>();
        createdUploadDirectory();

        for (MultipartFile file : files) {
            validateFile(file);
            String fileName = saveFileToDisk(file, productId);

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .fileName(fileName)
                    .build();

            responses.add(toResponse(productImageRepository.save(image), productId));
        }
        return responses;
    }

    @Override
    public List<ProductImageResponse> getImages(Integer productId) {
        return productImageRepository.findByProduct_Id(productId)
                .stream()
                .map(image -> toResponse(image, productId))
                .toList();
    }

    @Override
    @Transactional
    public void deleteImage(Integer productId, Integer imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new InvalidProductImageException("Image does not belong to product " + productId);
        }

        Path filePath = Paths.get(uploadDir).toAbsolutePath().resolve(image.getFileName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file from disk: {}. Error: {}", image.getFileName(), e.getMessage(), e);
            throw new FileStorageException("Failed to delete file");
        }
        productImageRepository.delete(image);
    }

    @Override
    @Transactional
    public void deleteAllImages(Integer productId) {
        List<ProductImage> images = productImageRepository.findByProduct_Id(productId);
        for (ProductImage image : images) {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().resolve(image.getFileName());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Could not delete file: {}. Error: {}", image.getFileName(), e.getMessage());
            }
        }
        productImageRepository.deleteAll(images);
    }

    private ProductImageResponse toResponse(ProductImage image, Integer productId) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(productId)
                .imageUrl(urlPrefix + "/" + image.getFileName())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private void createdUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(uploadDir).toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadDir, e);
            throw new FileStorageException("Could not create upload directory");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ProductImageEmptyException("Product image is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.contains("image/")) {
            throw new InvalidProductImageException("Only image files are allowed.");
        }
    }

    private String saveFileToDisk(MultipartFile file, Integer productId) {

        try {
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf(".")) : ".jpg";
            String fileName = "product-" + productId + "-" + UUID.randomUUID() + ext;
            Path filePath = Paths.get(uploadDir).toAbsolutePath().resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to save file for product {}: {}", productId, e.getMessage(), e);
            throw new FileStorageException("Could not save file to disk");
        }
    }
}


