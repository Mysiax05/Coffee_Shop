package com.dbproject.backend.repository;

import com.dbproject.backend.entity.PaymentMethod;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    @Transactional
    @Modifying
    @Query(value = "CALL p_deactivate_payment_method(:paymentMethodId)",
            nativeQuery = true)
    void deactivatePaymentMethod(@Param("paymentMethodId") Integer paymentMethodId);
}
