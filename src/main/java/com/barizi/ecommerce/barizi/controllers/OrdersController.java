package com.barizi.ecommerce.barizi.controllers;


import com.barizi.ecommerce.barizi.DTOs.Response.GetProductsResponse;
import com.barizi.ecommerce.barizi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<GetProductsResponse> getProducts(){
        return productService.getProducts();
    }

}
