package com.ecommerce.feignClient;


import com.ecommerce.dto.SellerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url ="${user.service.url}")
public interface UserClient {

    @GetMapping("/users/{id}/seller-info")
    SellerResponse getSellerInfoById(@PathVariable("id") Integer id);

}
