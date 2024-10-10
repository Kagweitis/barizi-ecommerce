package com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;
    private int quantity;
    private double price;
}
