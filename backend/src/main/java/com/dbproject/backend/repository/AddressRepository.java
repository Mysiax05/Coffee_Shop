package com.dbproject.backend.repository;

import com.dbproject.backend.entity.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Transactional
    @Modifying
    @Query(value = "CALL p_add_address(:customerId, :label, :street, :city, :postalCode, :country)",
            nativeQuery = true)
    void addAddress(
            @Param("customerId") Integer customerId,
            @Param("label") String label,
            @Param("street") String street,
            @Param("city") String city,
            @Param("postalCode") String postalCode,
            @Param("country") String country
    );

    List<Address> findByCustomer_CustomerId(Integer customerId);
}