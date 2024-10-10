package com.barizi.ecommerce.barizi.DTOs.Response;

import com.barizi.ecommerce.barizi.Entities.Product;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfo {

    private Product product;
    private int quantity;
}
