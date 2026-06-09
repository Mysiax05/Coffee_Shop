package com.dbproject.backend.repository;

import com.dbproject.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {

    @Query(value = "SELECT * FROM f_get_customer_payments(:customerId)", nativeQuery = true)
    List<Payment> findByCustomerId(@Param("customerId") Integer customerId);

    @Query(value = "SELECT f_get_customer_expenses(:customerId)", nativeQuery = true)
    BigDecimal findCustomerExpenses(@Param("customerId") Integer customerId);
}
