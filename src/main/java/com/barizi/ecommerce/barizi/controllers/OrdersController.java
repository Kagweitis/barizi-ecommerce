package com.barizi.ecommerce.barizi.controllers;


import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.GetProductsResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse;
import com.barizi.ecommerce.barizi.Entities.Order;
import com.barizi.ecommerce.barizi.Entities.OrderItem;
import com.barizi.ecommerce.barizi.Services.OrderService;
import com.barizi.ecommerce.barizi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<GetProductsResponse> getProducts(){
        return productService.getProducts();
    }

    @PostMapping("/new/order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }
}
