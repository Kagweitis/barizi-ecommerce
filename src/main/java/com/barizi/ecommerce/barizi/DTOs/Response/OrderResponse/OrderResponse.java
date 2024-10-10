package com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse;

import lombok.Data;

@Data
public class OrderResponse {

    private String message;
    private int statusCode;
    private OrderDetails order;
}



