package com.ecommerce.mapper;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "userId",   ignore = true)
    @Mapping(target = "status",   ignore = true)
    @Mapping(target = "images",   ignore = true)
    Product toEntity(ProductRequest productRequest);

    @Mapping(target = "imageUrls", ignore = true)
    ProductResponse toResponse(Product product);
}
