package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import test.files.shared.TestModels.Order;
import java.util.stream.Stream;

/**
 * Tests that multiple resources in single try-with-resources is NOT flagged.
 * EXPECTED: 0 issues
 */
class MultipleResourcesTest {
    private JdbcClient jdbcClient;

    void testCase() {
        try (Stream<User> users = jdbcClient.sql("SELECT * FROM users").query(User.class).stream();
             Stream<Order> orders = jdbcClient.sql("SELECT * FROM orders").query(Order.class).stream()) {
            users.forEach(System.out::println);
            orders.forEach(System.out::println);
        }
    }
}
