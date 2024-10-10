package com.barizi.ecommerce.barizi.DTOs.Response;


import com.barizi.ecommerce.barizi.Entities.Product;
import lombok.Data;

import java.util.List;

@Data
public class GetProductsResponse {

    private String message;
    private int statusCode;
    private List<Product> products;
}
