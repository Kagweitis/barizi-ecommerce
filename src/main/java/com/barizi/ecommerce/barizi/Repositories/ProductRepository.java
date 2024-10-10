package com.barizi.ecommerce.barizi.Repositories;

import com.barizi.ecommerce.barizi.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query(nativeQuery = true, value = "select * from products where product_name = :name and deleted = false")
    Optional<Product> findByNameAndDeleted(String name);

    @Query(nativeQuery = true, value = "select * from products where id = :id and deleted = false")
    Optional<Product> findByIdDeleted(long id);

    @Query(nativeQuery = true, value = "select * from products where deleted = false")
    List<Product> findAllByDeleted();
}
