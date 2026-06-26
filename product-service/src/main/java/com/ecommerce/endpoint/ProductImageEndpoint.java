package com.ecommerce.endpoint;

import com.ecommerce.dto.ProductImageResponse;
import com.ecommerce.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductImageEndpoint {

    private final ProductImageService productImageService;

    @PostMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponse>> uploadImages(
            @PathVariable("id") Integer id,
            @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(productImageService.uploadImages(id, files));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(productImageService.getImages(id));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable("id") Integer id,
            @PathVariable("imageId") Integer imageId) {
        productImageService.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
