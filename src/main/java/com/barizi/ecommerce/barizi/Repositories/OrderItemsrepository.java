package com.barizi.ecommerce.barizi.Repositories;

import com.barizi.ecommerce.barizi.Entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsrepository extends JpaRepository<OrderItem, Long> {
}
