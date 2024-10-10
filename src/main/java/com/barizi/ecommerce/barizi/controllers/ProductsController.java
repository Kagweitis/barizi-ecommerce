package com.barizi.ecommerce.barizi.controllers;

import com.barizi.ecommerce.barizi.DTOs.Request.ProductRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.GetProductsResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.SimpleResponse;
import com.barizi.ecommerce.barizi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/products")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class ProductsController {

    private final ProductService productService;


    @PostMapping("/new/product")
    public ResponseEntity<ProductResponse> newProduct(@RequestBody ProductRequest productRequest){
        return productService.newProduct(productRequest);
    }

    @PatchMapping("/update/product")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest productRequest){
        return productService.updateProduct(productRequest);
    }
    @PatchMapping("/delete/product/{id}")
    public ResponseEntity<SimpleResponse> deleteProduct(@PathVariable long id){
        return productService.deleteProduct(id);
    }

    @GetMapping("/products")
    public ResponseEntity<GetProductsResponse> getProducts(){
        return productService.getProducts();
    }

}
