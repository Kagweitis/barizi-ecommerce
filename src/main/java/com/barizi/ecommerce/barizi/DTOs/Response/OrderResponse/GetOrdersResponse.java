package com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse;

import lombok.Data;

import java.util.List;

@Data
public class GetOrdersResponse {

    private String message;
    private int statusCode;
    private List<OrderDetails> order;
}
