package com.dbproject.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DatabaseOperationsTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void payingOrderDecrementsStock() {
        //given
        int category = insertCategory("Coffee");
        int product = insertProduct(category, "Beans", new BigDecimal("10.00"), 5);
        int customer = insertCustomer("decrement@test.pl");
        int address = insertAddress(customer);
        int paymentMethod = insertPaymentMethod();
        int order = createOrder(customer, address, product, 2);

        //when
        jdbc.update("CALL p_pay_order(?, ?, ?)", customer, order, paymentMethod);

        //then
        Integer stock = jdbc.queryForObject(
                "SELECT stock FROM products WHERE productid = ?", Integer.class, product);
        assertThat(stock).isEqualTo(3);
    }

    @Test
    void payingOrderRejectsInsufficientStock() {
        //given
        int category = insertCategory("Coffee");
        int product = insertProduct(category, "Beans", new BigDecimal("10.00"), 1);
        int customer = insertCustomer("insufficient@test.pl");
        int address = insertAddress(customer);
        int paymentMethod = insertPaymentMethod();
        int order = createOrder(customer, address, product, 5);

        //when //then
        assertThatThrownBy(() -> jdbc.update("CALL p_pay_order(?, ?, ?)", customer, order, paymentMethod))
                .hasMessageContaining("Stock cannot be negative");
    }

    @Test
    void bestSellersAggregatesPaidOrders() {
        //given
        int category = insertCategory("Coffee");
        int product = insertProduct(category, "Beans", new BigDecimal("10.00"), 100);
        int customer = insertCustomer("bestseller@test.pl");
        int address = insertAddress(customer);
        int paymentMethod = insertPaymentMethod();
        payOrder(customer, address, product, 3, paymentMethod);
        payOrder(customer, address, product, 2, paymentMethod);

        //when
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT total_sold, revenue FROM f_report_best_sellers(10, NULL) WHERE product_id = ?",
                product);

        //then
        assertThat(row.get("total_sold")).isEqualTo(5);
        assertThat((BigDecimal) row.get("revenue")).isEqualByComparingTo("50.00");
    }

    @Test
    void bestSellersIgnoresPendingOrders() {
        //given
        int category = insertCategory("Tea");
        int product = insertProduct(category, "Leaves", new BigDecimal("20.00"), 10);
        int customer = insertCustomer("pending@test.pl");
        int address = insertAddress(customer);
        createOrder(customer, address, product, 4);

        //when
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM f_report_best_sellers(10, NULL) WHERE product_id = ?", product);

        //then
        assertThat(rows).isEmpty();
    }

    @Test
    void cannotOrderWithSomeoneElsesAddress() {
        //given
        int customer = insertCustomer("owner@test.pl");
        int otherCustomer = insertCustomer("stranger@test.pl");
        insertAddress(customer);
        int otherAddress = insertAddress(otherCustomer);
        int category = insertCategory("Coffee");
        int product = insertProduct(category, "Beans", new BigDecimal("10.00"), 10);
        String items = "[{\"productId\":" + product + ",\"quantity\":1}]";

        //when //then
        assertThatThrownBy(() -> jdbc.update("CALL p_create_order(?, ?, ?::jsonb)", customer, otherAddress, items))
                .hasMessageContaining("does not belong");
    }

    private int insertCategory(String name) {
        return jdbc.queryForObject(
                "INSERT INTO categories(categoryname) VALUES (?) RETURNING categoryid",
                Integer.class, name);
    }

    private int insertProduct(int categoryId, String name, BigDecimal price, int stock) {
        return jdbc.queryForObject(
                "INSERT INTO products(categoryid, name, price, stock, isactive) " +
                        "VALUES (?, ?, ?, ?, true) RETURNING productid",
                Integer.class, categoryId, name, price, stock);
    }

    private int insertCustomer(String email) {
        return jdbc.queryForObject(
                "INSERT INTO customers(firstname, lastname, email) VALUES ('Test', 'User', ?) RETURNING customerid",
                Integer.class, email);
    }

    private int insertAddress(int customerId) {
        return jdbc.queryForObject(
                "INSERT INTO addresses(customerid, street, city, postalcode) " +
                        "VALUES (?, 'Street 1', 'City', '00-000') RETURNING addressid",
                Integer.class, customerId);
    }

    private int insertPaymentMethod() {
        return jdbc.queryForObject(
                "INSERT INTO paymentmethods(provider, type) VALUES ('Test', 'Card') RETURNING paymentmethodid",
                Integer.class);
    }

    private int createOrder(int customerId, int addressId, int productId, int quantity) {
        String items = "[{\"productId\":" + productId + ",\"quantity\":" + quantity + "}]";
        jdbc.update("CALL p_create_order(?, ?, ?::jsonb)", customerId, addressId, items);
        return jdbc.queryForObject(
                "SELECT orderid FROM orders WHERE customerid = ? ORDER BY orderid DESC LIMIT 1",
                Integer.class, customerId);
    }

    private void payOrder(int customerId, int addressId, int productId, int quantity, int paymentMethodId) {
        int order = createOrder(customerId, addressId, productId, quantity);
        jdbc.update("CALL p_pay_order(?, ?, ?)", customerId, order, paymentMethodId);
    }
}
