package com.dbproject.backend.repository;

import com.dbproject.backend.dto.OrderItem;
import com.dbproject.backend.entity.Order;
import com.dbproject.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM f_get_order_products(:orderId)", nativeQuery = true)
    List<OrderItem> findProductsOfOrder(@Param("orderId") Integer orderId);
}
