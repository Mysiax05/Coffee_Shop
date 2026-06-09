package com.dbproject.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockConcurrencyTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void concurrentPaymentsForLastUnitDoNotOversell() throws Exception {
        //given
        int category = insertCategory("Coffee");
        int product = insertProduct(category, "Last Bag", new BigDecimal("10.00"), 1);
        int customer = insertCustomer("concurrency@test.pl");
        int address = insertAddress(customer);
        int paymentMethod = insertPaymentMethod();
        int order1 = createOrder(customer, address, product, 1);
        int order2 = createOrder(customer, address, product, 1);
        try {
            AtomicInteger successes = new AtomicInteger();
            AtomicReference<String> failureMessage = new AtomicReference<>();
            CountDownLatch ready = new CountDownLatch(2);
            CountDownLatch go = new CountDownLatch(1);

            //when
            Thread first = payInThread(customer, order1, paymentMethod, ready, go, successes, failureMessage);
            Thread second = payInThread(customer, order2, paymentMethod, ready, go, successes, failureMessage);
            first.start();
            second.start();
            ready.await();
            go.countDown();
            first.join();
            second.join();

            //then
            Integer finalStock = jdbc.queryForObject(
                    "SELECT stock FROM products WHERE productid = ?", Integer.class, product);
            assertThat(successes.get()).isEqualTo(1);
            assertThat(failureMessage.get()).contains("Stock cannot be negative");
            assertThat(finalStock).isEqualTo(0);
        } finally {
            cleanUp(order1, order2, address, product, category, customer, paymentMethod);
        }
    }

    private Thread payInThread(int customer, int order, int paymentMethod,
                               CountDownLatch ready, CountDownLatch go,
                               AtomicInteger successes, AtomicReference<String> failureMessage) {
        return new Thread(() -> {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                ready.countDown();
                go.await();
                try (PreparedStatement statement = connection.prepareStatement("CALL p_pay_order(?, ?, ?)")) {
                    statement.setInt(1, customer);
                    statement.setInt(2, order);
                    statement.setInt(3, paymentMethod);
                    statement.execute();
                    connection.commit();
                    successes.incrementAndGet();
                } catch (Exception e) {
                    connection.rollback();
                    failureMessage.set(e.getMessage());
                }
            } catch (Exception e) {
                failureMessage.set(e.getMessage());
            }
        });
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

    private void cleanUp(int order1, int order2, int address, int product, int category, int customer, int paymentMethod) {
        jdbc.update("DELETE FROM payments WHERE orderid IN (?, ?)", order1, order2);
        jdbc.update("DELETE FROM orderdetails WHERE orderid IN (?, ?)", order1, order2);
        jdbc.update("DELETE FROM orders WHERE orderid IN (?, ?)", order1, order2);
        jdbc.update("DELETE FROM addresses WHERE addressid = ?", address);
        jdbc.update("DELETE FROM products WHERE productid = ?", product);
        jdbc.update("DELETE FROM categories WHERE categoryid = ?", category);
        jdbc.update("DELETE FROM customers WHERE customerid = ?", customer);
        jdbc.update("DELETE FROM paymentmethods WHERE paymentmethodid = ?", paymentMethod);
    }
}
