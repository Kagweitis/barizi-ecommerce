package com.barizi.ecommerce.barizi.DTOs.Response;

import com.barizi.ecommerce.barizi.Entities.Product;
import lombok.Data;

@Data
public class ProductResponse {

    private String message;
    private int statusCode;
    private Product product;
}
