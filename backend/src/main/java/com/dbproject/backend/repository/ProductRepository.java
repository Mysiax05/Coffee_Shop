package com.dbproject.backend.repository;

import com.dbproject.backend.dto.OrderItem;
import com.dbproject.backend.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM vw_active_products", nativeQuery = true)
    List<Product> findAllActive();

    @Query(value = "SELECT * FROM f_get_order_products(:orderId)", nativeQuery = true)
    List<OrderItem> findProductsOfOrder(@Param("orderId") Integer orderId);

    @Transactional
    @Modifying
    @Query(value = "CALL p_deactivate_product(:productId)",
            nativeQuery = true)
    void deactivateProduct(@Param("productId") Integer productId);

    @Query(value = "SELECT * FROM f_report_best_sellers(:limit, :categoryId)", nativeQuery = true)
    List<Object[]> findBestSellers(
            @Param("limit") Integer limit,
            @Param("categoryId") Integer categoryId
    );
}
