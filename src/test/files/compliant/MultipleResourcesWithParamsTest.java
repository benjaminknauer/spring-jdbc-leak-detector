package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import test.files.shared.TestModels.Order;
import java.util.stream.Stream;

/**
 * Tests that multiple resources with parameters in try-with-resources is NOT flagged.
 * EXPECTED: 0 issues
 */
class MultipleResourcesWithParamsTest {
    private JdbcClient jdbcClient;

    void testCase() {
        try (Stream<User> users = jdbcClient.sql("SELECT * FROM users WHERE age > ?").param(18).query(User.class).stream();
             Stream<Order> orders = jdbcClient.sql("SELECT * FROM orders WHERE amount > ?").param(100.0).query(Order.class).stream()) {
            long userCount = users.count();
            long orderCount = orders.count();
            System.out.println("Users: " + userCount + ", Orders: " + orderCount);
        }
    }
}
