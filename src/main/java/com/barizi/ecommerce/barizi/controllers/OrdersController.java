package com.barizi.ecommerce.barizi.controllers;


import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderUpdateRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.GetOrdersResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.GetProductsResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.OrderResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.SimpleResponse;
import com.barizi.ecommerce.barizi.Services.OrderService;
import com.barizi.ecommerce.barizi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user/orders/{id}")
    public ResponseEntity<GetOrdersResponse> getUserOrders(@PathVariable long id){
        return orderService.getUserOrders(id);
    }

    @PatchMapping("/delete/order/{id}")
    public ResponseEntity<SimpleResponse> deleteOrder(@PathVariable long id){
        return orderService.deleteOrder(id);
    }

    @PatchMapping("/update/order")
    public ResponseEntity<OrderResponse> deleteOrder(@RequestBody OrderUpdateRequest orderUpdateRequest){
        return orderService.updateOrder(orderUpdateRequest);
    }

    @GetMapping("/search/products")
    public ResponseEntity<GetProductsResponse> searchProducts(@RequestParam int page,
                                                              @RequestParam int size,
                                                              @RequestParam String searchPhrase){
        return orderService.searchPhrase(page, size, searchPhrase);
    }

}
