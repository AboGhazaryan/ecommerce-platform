package com.ecommerce.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerResponse {

    private Integer id;
    private String name;
    private String surname;
    private String email;
}
