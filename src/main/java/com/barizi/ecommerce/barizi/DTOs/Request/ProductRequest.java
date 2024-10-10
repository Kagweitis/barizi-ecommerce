package com.barizi.ecommerce.barizi.DTOs.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private long categoryId;
}
