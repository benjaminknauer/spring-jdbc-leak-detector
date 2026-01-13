package com.example.demo;

import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying that compliant code (with try-with-resources) properly
 * returns connections to the pool.
 *
 * <p>These tests use a small connection pool (3 connections) and verify that compliant
 * methods can be called many more times than the pool size without exhausting it.</p>
 *
 * <h2>Note on Leak Tests</h2>
 * <p>Connection leak tests are intentionally excluded from this test class because they
 * are destructive (they exhaust the pool) and break Spring's context management.
 * To demonstrate leaks, use the REST endpoints in {@code DemoController}:</p>
 * <ol>
 *   <li>Start the app: {@code mvn spring-boot:run}</li>
 *   <li>Call leaky endpoints 3+ times: {@code curl http://localhost:8080/users/print}</li>
 *   <li>Observe the 4th call timeout due to pool exhaustion</li>
 * </ol>
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.hikari.maximum-pool-size=3",
    "spring.datasource.hikari.minimum-idle=1",
    "spring.datasource.hikari.connection-timeout=5000",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:schema-h2.sql",
    "spring.sql.init.data-locations=classpath:data-h2.sql"
})
class ConnectionLeakIntegrationTest {

    private static final int POOL_SIZE = 3;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcClient jdbcClient;

    private HikariPoolMXBean poolMXBean;

    @BeforeEach
    void setUp() {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        poolMXBean = hikariDataSource.getHikariPoolMXBean();
    }

    @Test
    @DisplayName("JdbcClient.stream() with try-with-resources does not leak connections")
    void jdbcClientStream_withTryWithResources_noLeak() {
        int iterations = POOL_SIZE * 10; // Many more iterations than pool size

        for (int i = 0; i < iterations; i++) {
            userService.printAllUsersCorrectly();
        }

        // If we got here without timeout, connections were returned to pool
        assertThat(poolMXBean.getActiveConnections())
                .as("Connections should be returned to pool after %d iterations", iterations)
                .isLessThanOrEqualTo(POOL_SIZE);
    }

    @Test
    @DisplayName("JdbcTemplate.queryForStream() with try-with-resources does not leak connections")
    void jdbcTemplateQueryForStream_withTryWithResources_noLeak() {
        int iterations = POOL_SIZE * 10;

        for (int i = 0; i < iterations; i++) {
            orderService.calculateTotalRevenueCorrectly();
        }

        assertThat(poolMXBean.getActiveConnections())
                .as("Connections should be returned to pool")
                .isLessThanOrEqualTo(POOL_SIZE);
    }

    @Test
    @DisplayName("Caller closing returned stream does not leak connections")
    void callerClosingReturnedStream_noLeak() {
        int iterations = POOL_SIZE * 10;

        for (int i = 0; i < iterations; i++) {
            // Caller properly closes the stream
            try (Stream<?> stream = orderService.getOrdersByStatus("completed")) {
                stream.count();
            }
        }

        assertThat(poolMXBean.getActiveConnections())
                .as("Connections should be returned to pool")
                .isLessThanOrEqualTo(POOL_SIZE);
    }

    @Test
    @DisplayName("Manual stream handling with try-with-resources works")
    void manualStreamHandling_withTryWithResources_noLeak() {
        int iterations = POOL_SIZE * 10;

        for (int i = 0; i < iterations; i++) {
            try (var stream = jdbcClient
                    .sql("SELECT * FROM users")
                    .query((rs, rowNum) -> rs.getString("name"))
                    .stream()) {
                long count = stream.count();
                assertThat(count).isGreaterThanOrEqualTo(0);
            }
        }

        assertThat(poolMXBean.getActiveConnections())
                .as("Connections should be returned to pool")
                .isLessThanOrEqualTo(POOL_SIZE);
    }
}
