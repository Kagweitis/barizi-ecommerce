package com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests;

import lombok.Data;

@Data
public class OrderUpdateRequest {

    private long id;
    private String orderStatus;
    private String paymentStatus;

}
