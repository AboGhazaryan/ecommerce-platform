package com.ecommerce.feignClient;


import com.ecommerce.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url ="${product.service.url}")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Integer id);

    @PostMapping("/products/{id}/stock")
    void decreaseStock(@PathVariable("id") Integer id, @RequestParam("quantity") Integer quantity);

}
