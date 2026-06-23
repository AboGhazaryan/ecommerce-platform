package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ProductNotFountException;
import com.ecommerce.exception.UnauthorizedActionException;
import com.ecommerce.kafka.ProductEventProducer;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.ProductCategory;
import com.ecommerce.model.ProductStatus;
import com.ecommerce.model.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductImageService;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductEventProducer productEventProducer;

    @Value("${app.upload.url-prefix:http://localhost:8080/images}")
    private String urlPrefix;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, Integer userId) {
        Product product = productMapper.toEntity(productRequest);
        product.setUserId(userId);
        product.setStatus(ProductStatus.PENDING);
        productRepository.save(product);
        productEventProducer.sendProductEvent(product.getId(), product.getName(), userId, "CREATED");
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id, Integer userId, String role) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        if (!ProductStatus.APPROVED.equals(product.getStatus())) {
            boolean isOwner = userId != null && userId.equals(product.getUserId());
            if (!isAdmin(role) && !isOwner) {
                throw new ProductNotFountException("product not found");
            }
        }
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByStatus(ProductStatus.APPROVED)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategoryAndStatus(category, ProductStatus.APPROVED)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(name, ProductStatus.APPROVED)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(Integer userId) {
        return productRepository.findByUserId(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getPendingProducts() {
        return productRepository.findByStatus(ProductStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProductById(Integer id, ProductRequest request, Integer userId, String role) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        if (!isAdmin(role) && !userId.equals(product.getUserId())) {
            throw new UnauthorizedActionException("You don't have permission to edit this product");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        if (!isAdmin(role)) {
            product.setStatus(ProductStatus.PENDING);
        }
        productRepository.save(product);
        productEventProducer.sendProductEvent(product.getId(), product.getName(), userId, "UPDATED");
        return toResponse(product);
    }

    @Override
    @Transactional
    public void deleteProductById(Integer id, Integer userId, String role) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        if (!isAdmin(role) && !userId.equals(product.getUserId())) {
            throw new UnauthorizedActionException("You don't have permission to delete this product");
        }
        productImageService.deleteAllImages(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse approveProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        product.setStatus(ProductStatus.APPROVED);
        productRepository.save(product);
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse rejectProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        product.setStatus(ProductStatus.REJECTED);
        productRepository.save(product);
        return toResponse(product);
    }

    @Override
    @Transactional
    public void decreaseStock(Integer id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("product not found"));
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock for product");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(img -> urlPrefix + "/" + img.getFileName())
                .toList();
        return productMapper.toResponse(product).toBuilder()
                .imageUrls(imageUrls)
                .build();
    }

    private boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }
}
