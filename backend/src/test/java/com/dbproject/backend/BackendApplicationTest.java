package com.dbproject.backend;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest
class BackendApplicationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(1)).isTrue();
            System.out.println("Connected to: " + connection.getMetaData().getURL());
        }
    }

    @Test
    void testTablesExist() throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            String[] expectedTables = new String[]{
                    "categories",
                    "products",
                    "customers",
                    "addresses",
                    "orders",
                    "orderdetails",
                    "paymentmethods",
                    "payments",
            };

            for (String tableName : expectedTables) {
                try {
                    connection.createStatement().execute("SELECT 1 FROM " + tableName + " LIMIT 1");
                } catch (SQLException e) {
                    fail("Table '" + tableName + "' is missing");
                }
            }
        }
    }
}