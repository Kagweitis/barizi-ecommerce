package com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetails {

    private double totalCost;
    private List<OrderInfo> orderInfos;
}
