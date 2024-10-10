package com.barizi.ecommerce.barizi.DTOs.Request;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;
    private int quantity;
    private double price;
}
