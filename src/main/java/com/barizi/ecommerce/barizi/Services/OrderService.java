package com.barizi.ecommerce.barizi.Services;


import com.barizi.ecommerce.barizi.DTOs.Request.OrderItemRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderDetails;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderInfo;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemsrepository orderItemsrepository;


    public ResponseEntity<OrderResponse> placeOrder(OrderRequest orderRequest) {

        OrderResponse res = new OrderResponse();

        try {
            Optional<UserEntity> customer = userRepository.findById(orderRequest.getCustomerId());
            if (customer.isPresent()) {
                double totalCost = orderRequest.getOrderItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum();
                List<OrderItem> orderItems = new ArrayList<>();
                List<OrderInfo> orderInfos = new ArrayList<>();

                boolean stockAvailable = true;
                for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                    // Fetch the product
                    Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());

                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();

                        // Check if the product has enough stock
                        if (product.getStock() >= itemRequest.getQuantity()) {
                            // Create new OrderItem
                            OrderItem orderItem = OrderItem.builder()
                                    .product(product)
                                    .quantity(itemRequest.getQuantity())
                                    .build();

                            OrderInfo orderInfo = OrderInfo.builder()
                                    .quantity(itemRequest.getQuantity())
                                    .product(product)
                                    .build();

                            // Calculate total cost for this item
                            totalCost += product.getPrice() * orderItem.getQuantity();

                            // Update product stock
                            product.setStock(product.getStock() - orderItem.getQuantity());
                            productRepository.save(product); // Save updated product

                            orderItems.add(orderItem);
                            orderInfos.add(orderInfo);
                        } else {
                            res.setMessage("Product "+product.getName()+" has insufficient stock. Available: "+product.getStock());
                            res.setStatusCode(400);
                            return ResponseEntity.status(res.getStatusCode()).body(res);
                        }
                    } else {
                        res.setMessage("Product with ID "+itemRequest.getProductId()+" not found");
                        res.setStatusCode(400);
                        return ResponseEntity.status(res.getStatusCode()).body(res);
                    }
                }


                Order newOrder = Order.builder()
                        .customer(customer.get())
//                        .orderItems(orderItems)
                        .totalCost(totalCost)
                        .orderStatus(OrderStatus.PENDING) // Set initial status
                        .paymentStatus(PaymentStatus.PAID)
                        .build();

                orderRepository.save(newOrder);
                // Save the new order to get the order ID
                Order savedOrder = orderRepository.save(newOrder);

                // associate the saved order with the order items b4 saving
                for (OrderItem orderItem : orderItems) {
                    orderItem.setOrder(savedOrder);
                    orderItemsrepository.save(orderItem);
                }
                OrderDetails orderDetails = OrderDetails.builder()
                        .totalCost(totalCost)
                        .orderInfos(orderInfos)
                        .build();
                res.setMessage("Order placed successfully");
                res.setStatusCode(200);
                res.setOrder(orderDetails);
                return ResponseEntity.status(res.getStatusCode()).body(res);

            }
        } catch (Exception e){
            log.error("error creating order "+e);
            res.setMessage("Couldn't place your order. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

}
