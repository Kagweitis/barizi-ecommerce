package com.barizi.ecommerce.barizi;


import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderItemRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderUpdateRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.GetOrdersResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.OrderResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.GetProductsResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.SimpleResponse;
import com.barizi.ecommerce.barizi.Entities.Enums.OrderStatus;
import com.barizi.ecommerce.barizi.Entities.Enums.PaymentStatus;
import com.barizi.ecommerce.barizi.Entities.Order;
import com.barizi.ecommerce.barizi.Entities.OrderItem;
import com.barizi.ecommerce.barizi.Entities.Product;
import com.barizi.ecommerce.barizi.Entities.UserEntity;
import com.barizi.ecommerce.barizi.Repositories.OrderItemsrepository;
import com.barizi.ecommerce.barizi.Repositories.OrderRepository;
import com.barizi.ecommerce.barizi.Repositories.ProductRepository;
import com.barizi.ecommerce.barizi.Repositories.UserRepository;
import com.barizi.ecommerce.barizi.Services.OrderService;
import com.barizi.ecommerce.barizi.Services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemsrepository orderItemsRepository;

    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private ProductService productService;

    @Test
    void testPlaceOrder_Success() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(2);
        orderItemRequest.setPrice(50.0);
        orderRequest.setOrderItems(List.of(orderItemRequest));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setStock(10);
        product.setPrice(50.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ResponseEntity<OrderResponse> response = orderService.placeOrder(orderRequest);

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Order placed successfully", response.getBody().getMessage());
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(20);  // Quantity exceeding stock
        orderItemRequest.setPrice(50.0);
        orderRequest.setOrderItems(List.of(orderItemRequest));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("ProductName");
        product.setStock(10); // Only 10 in stock

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ResponseEntity<OrderResponse> response = orderService.placeOrder(orderRequest);

        // Assert
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Product ProductName has insufficient stock. Available: 10", response.getBody().getMessage());
    }

    @Test
    void testPlaceOrder_ProductNotFound() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(2L); // Invalid product ID
        orderItemRequest.setQuantity(5);
        orderItemRequest.setPrice(50.0);
        orderRequest.setOrderItems(List.of(orderItemRequest));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Mockito.when(productRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<OrderResponse> response = orderService.placeOrder(orderRequest);

        // Assert
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Product with ID 2 not found", response.getBody().getMessage());
    }

    @Test
    void testGetUserOrders_Success() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setOrderItems(List.of(new OrderItem()));
        orders.add(order);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PAID);

        Mockito.when(orderRepository.findAllByUserIdAndDeletedFalse(1L)).thenReturn(orders);

        // Act
        ResponseEntity<GetOrdersResponse> response = orderService.getUserOrders(1L);

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Orders found!", response.getBody().getMessage());
    }

    @Test
    void testDeleteOrder_Success() {
        // Arrange
        Order order = new Order();
        order.setDeleted(false);

        Mockito.when(orderRepository.findByIdAndDeleted(1L)).thenReturn(Optional.of(order));

        // Act
        ResponseEntity<SimpleResponse> response = orderService.deleteOrder(1L);

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Order deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testDeleteOrder_NotFound() {
        // Arrange
        Mockito.when(orderRepository.findByIdAndDeleted(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<SimpleResponse> response = orderService.deleteOrder(1L);

        // Assert
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Order not found", response.getBody().getMessage());
    }

    @Test
    void testUpdateOrder_Success() {
        // Arrange
        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
        orderUpdateRequest.setId(1L);
        orderUpdateRequest.setOrderStatus("SHIPPED");

        Order existingOrder = new Order();
        existingOrder.setOrderStatus(OrderStatus.PENDING);

        // Mock the orderItems list
        List<OrderItem> mockOrderItems = new ArrayList<>();
        existingOrder.setOrderItems(mockOrderItems); // Set the mock list

        Mockito.when(orderRepository.findByIdAndDeleted(1L)).thenReturn(Optional.of(existingOrder));

        // Act
        ResponseEntity<OrderResponse> response = orderService.updateOrder(orderUpdateRequest);

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Order updated successfully", response.getBody().getMessage());
    }


    @Test
    void testUpdateOrder_OrderNotFound() {
        // Arrange
        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
        orderUpdateRequest.setId(1L);


        Mockito.when(orderRepository.findByIdAndDeleted(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<OrderResponse> response = orderService.updateOrder(orderUpdateRequest);

        // Assert
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Order not found", response.getBody().getMessage());
    }

    @Test
    void testSearchPhrase_Success() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(new Product()); // Add at least one product to simulate results
        Page<Product> productPage = new PageImpl<>(products);
//        Page<Product> productPage = new PageImpl<>(new ArrayList<>());
        Mockito.when(productRepository.searchForTerm(Mockito.any(Pageable.class), Mockito.eq("test"))).thenReturn(productPage);

        // Act
        ResponseEntity<GetProductsResponse> response = productService.searchPhrase(1, 10, "test");

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Results found", response.getBody().getMessage());
    }

    @Test
    void testSearchPhrase_NoResults() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(productRepository.searchForTerm(Mockito.any(Pageable.class), Mockito.eq("unknown"))).thenReturn(productPage);

        // Act
        ResponseEntity<GetProductsResponse> response = productService.searchPhrase(1, 10, "unknown");

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("The keyword doesn't match anything we have", response.getBody().getMessage());
    }

}
