package com.barizi.ecommerce.barizi.DTOs.Request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private Long customerId;
    private List<OrderItemRequest> orderItems;
}
