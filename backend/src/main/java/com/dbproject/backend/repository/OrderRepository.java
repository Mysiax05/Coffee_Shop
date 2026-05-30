package com.dbproject.backend.repository;

import com.dbproject.backend.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Modifying
    @Query(value = "CALL p_create_order(:customerId, :addressId, CAST(:items AS jsonb))",
            nativeQuery = true)
    void createOrder(
            @Param("customerId") Integer customerId,
            @Param("addressId") Integer addressId,
            @Param("items") String items
    );


    @Query(value = "SELECT * FROM f_get_customer_orders(:customerId)", nativeQuery = true)
    List<Order> findOrdersOfCustomer(@Param("customerId") Integer customerId);
}
