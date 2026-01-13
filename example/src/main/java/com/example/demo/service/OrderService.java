package com.example.demo.service;

import com.example.demo.entity.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * OrderService demonstrating NONCOMPLIANT usage of JdbcTemplate.queryForStream().
 *
 * WARNING: This code contains intentional bugs for demonstration purposes!
 * The queryForStream() method returns a Stream that holds an open database connection.
 * Without try-with-resources, the connection is never returned to the pool.
 */
@Service
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Order> ORDER_MAPPER = (rs, rowNum) -> new Order(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getBigDecimal("amount"),
        rs.getString("status"),
        rs.getTimestamp("created_at").toLocalDateTime()
    );

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * NONCOMPLIANT: Stream from queryForStream() is not closed!
     *
     * This method leaks a database connection every time it's called.
     */
    public BigDecimal calculateTotalRevenue() {
        // BAD: queryForStream() returns an unclosed stream
        Stream<Order> orders = jdbcTemplate.queryForStream(
            "SELECT * FROM orders WHERE status = ?",
            ORDER_MAPPER,
            "completed"
        );

        return orders
            .map(Order::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Connection leaked - never returned to pool!
    }

    /**
     * NONCOMPLIANT: Returning an unclosed stream is dangerous!
     *
     * The caller might forget to close the stream, or may not even
     * know that closing is required.
     */
    public Stream<Order> getOrdersByStatus(String status) {
        // BAD: Returning unclosed stream - caller must remember to close!
        return jdbcTemplate.queryForStream(
            "SELECT * FROM orders WHERE status = ?",
            ORDER_MAPPER,
            status
        );
    }

    /**
     * COMPLIANT: Proper usage with try-with-resources.
     */
    public BigDecimal calculateTotalRevenueCorrectly() {
        try (Stream<Order> orders = jdbcTemplate.queryForStream(
                "SELECT * FROM orders WHERE status = ?",
                ORDER_MAPPER,
                "completed")) {
            return orders
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // Connection automatically closed here
    }
}
