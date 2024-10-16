package com.barizi.ecommerce.barizi.Repositories;

import com.barizi.ecommerce.barizi.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(nativeQuery = true, value = "select * from orders where user_id = :id and deleted = false")
    List<Order> findAllByUserIdAndDeletedFalse(long id);

    @Query(nativeQuery = true, value = "select * from orders where id = :id and deleted = false")
    Optional<Order> findByIdAndDeleted(long id);
}
