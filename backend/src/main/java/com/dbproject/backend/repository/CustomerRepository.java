package com.dbproject.backend.repository;

import com.dbproject.backend.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "CALL p_change_email(:customerId, :newEmail)", nativeQuery = true)
    void changeEmail(
            @Param("customerId") Integer customerId,
            @Param("newEmail") String newEmail
    );
}
