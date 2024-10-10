package com.barizi.ecommerce.barizi.Services;


import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderItemRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.OrderRequests.OrderUpdateRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.GetOrdersResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.OrderDetails;
import com.barizi.ecommerce.barizi.DTOs.Response.OrderResponse.OrderInfo;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                        .paymentStatus(newOrder.getPaymentStatus().toString())
                        .orderStatus(newOrder.getOrderStatus().toString())
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

    public ResponseEntity<GetOrdersResponse> getUserOrders(long id) {
        GetOrdersResponse res = new GetOrdersResponse();

        try {
            List<Order> orders = orderRepository.findAllByUserIdAndDeletedFalse(id);
            if (orders.isEmpty()){
                res.setMessage("You haven't placed any orders yet!");
                res.setStatusCode(204);
            }
//            List<OrderInfo> orderInfos = new ArrayList<>();
            List<OrderDetails> orderDetails = new ArrayList<>();
            for (Order order : orders) {
                OrderDetails orderDetail = new OrderDetails();

                // Initialize a new orderInfos list for each order
                List<OrderInfo> orderInfos = new ArrayList<>();

                for (OrderItem orderItem : order.getOrderItems()) {
                    OrderInfo orderInfo = OrderInfo.builder()
                            .product(orderItem.getProduct())
                            .quantity(orderItem.getQuantity())
                            .build();

                    orderInfos.add(orderInfo);  // Add info to this order's list
                }

                // Set the orderInfos and other details for this order
                orderDetail.setOrderInfos(orderInfos);
                orderDetail.setOrderStatus(order.getOrderStatus().toString());
                orderDetail.setPaymentStatus(order.getPaymentStatus().toString());
                orderDetail.setTotalCost(order.getTotalCost());
                orderDetails.add(orderDetail);
            }
            res.setMessage("Orders found!");
            res.setOrder(orderDetails);
            res.setStatusCode(200);
        } catch (Exception e){
            log.error("error creating order "+e);
            res.setMessage("Couldn't get your orders. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    public ResponseEntity<SimpleResponse> deleteOrder(long id){
        SimpleResponse res = new SimpleResponse();

        try {
            Optional<Order> existingOrder = orderRepository.findByIdAndDeleted(id);
            existingOrder.ifPresentOrElse(order -> {
                order.setDeleted(true);
                orderRepository.save(order);
                res.setMessage("Order deleted successfully");
                res.setStatusCode(200);
            }, ()-> {
                res.setMessage("Order not found");
                res.setStatusCode(404);
            });
        } catch (Exception e){
            log.error("error deleting order "+e);
            res.setMessage("Couldn't delete your order. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    public ResponseEntity<OrderResponse> updateOrder(OrderUpdateRequest orderUpdateRequest) {

        OrderResponse res = new OrderResponse();

        try {
            Optional<Order> existingOrder = orderRepository.findByIdAndDeleted(orderUpdateRequest.getId());
            existingOrder.ifPresentOrElse(order -> {
                if (orderUpdateRequest.getOrderStatus() != null){
                    order.setOrderStatus(OrderStatus.valueOf(orderUpdateRequest.getOrderStatus().toUpperCase()));
                }
                if (orderUpdateRequest.getPaymentStatus() != null) {
                    order.setPaymentStatus(PaymentStatus.valueOf(orderUpdateRequest.getPaymentStatus().toUpperCase()));
                }
                orderRepository.save(order);
                OrderDetails orderDetail = new OrderDetails();

                List<OrderInfo> orderInfos = new ArrayList<>();

                for (OrderItem orderItem : order.getOrderItems()) {
                    OrderInfo orderInfo = OrderInfo.builder()
                            .product(orderItem.getProduct())
                            .quantity(orderItem.getQuantity())
                            .build();

                    orderInfos.add(orderInfo);  // Add info to this order's list
                }
                orderDetail.setOrderStatus(orderUpdateRequest.getOrderStatus());
                orderDetail.setPaymentStatus(orderUpdateRequest.getPaymentStatus());
                orderDetail.setTotalCost(order.getTotalCost());
                orderDetail.setOrderInfos(orderInfos);

                res.setMessage("Order updated successfully");
                res.setStatusCode(200);
                res.setOrder(orderDetail);
            }, ()-> {
                res.setMessage("Order not found");
                res.setStatusCode(404);
            });
        } catch (Exception e){
            log.error("error updating order "+e);
            res.setMessage("Couldn't update your order. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    public ResponseEntity<GetProductsResponse> searchPhrase(int page, int size, String searchPhrase) {

        GetProductsResponse res = new GetProductsResponse();
        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<Product> products = productRepository.searchForTerm(pageable, searchPhrase);

            if (products.isEmpty()){
                res.setMessage("The keyword doesn't match anything we have");
                res.setStatusCode(204);
            }
            res.setMessage("Results found");
            res.setStatusCode(200);
            res.setProducts(products.stream().toList());
        } catch (Exception e){
            log.error("error searching keyword "+e);
            res.setMessage("An error occured. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);

    }
}
