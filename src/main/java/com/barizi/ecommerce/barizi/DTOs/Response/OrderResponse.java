package com.barizi.ecommerce.barizi.DTOs.Response;

import com.barizi.ecommerce.barizi.Entities.Order;
import lombok.Data;

@Data
public class OrderResponse {

    private String message;
    private int statusCode;
    private OrderDetails order;
}



