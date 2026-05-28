package com.dbproject.backend.service;

import com.dbproject.backend.entity.Address;
import com.dbproject.backend.entity.Customer;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final EntityManager entityManager;

    public AddressService(EntityManager entityManager){
        this.entityManager=entityManager;
    }

    public void addAddress(Customer customer, Address address){
        entityManager.createNativeQuery("""
            call p_add_address(
                :customerid,
                :label,
                :street,
                :city,
                :postalcode,
                :country
            )
        """)
                .setParameter("customerid", customer.getCustomerId())
                .setParameter("label", address.getLabel())
                .setParameter("street", address.getStreet())
                .setParameter("city", address.getCity())
                .setParameter("postalcode", address.getPostalCode())
                .setParameter("country",address.getCountry())
                .executeUpdate();

    }

}
