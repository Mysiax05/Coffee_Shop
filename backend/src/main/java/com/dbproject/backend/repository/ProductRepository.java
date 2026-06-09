package com.dbproject.backend.repository;

import com.dbproject.backend.dto.OrderItem;
import com.dbproject.backend.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query(value = "SELECT productid, name, price, stock, attributes::text " +
            "FROM f_filter_products(:minPrice, :maxPrice, :categoryId, CAST(:attributes AS jsonb))",
            nativeQuery = true)
    List<Object[]> filterProducts(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categoryId") Integer categoryId,
            @Param("attributes") String attributes
    );

    @Transactional
    @Modifying
    @Query(value = "CALL p_set_price(:productId, :newPrice)",
            nativeQuery = true)
    void updateProductPrice(@Param("productId") Integer id, @Param("newPrice") BigDecimal newPrice);

    @Transactional
    @Modifying
    @Query(value = "CALL p_update_stock(:productId, :quantity)", nativeQuery = true)
    void addProductStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
