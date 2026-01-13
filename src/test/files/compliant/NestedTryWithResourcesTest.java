package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests that nested try-with-resources is NOT flagged.
 * EXPECTED: 0 issues
 */
class NestedTryWithResourcesTest {
    private JdbcClient jdbcClient;

    void testCase() {
        try (Stream<User> activeUsers = jdbcClient.sql("SELECT * FROM users WHERE active = true")
            .query(User.class)
            .stream()) {

            activeUsers.forEach(user -> {
                System.out.println(user.name);
            });
        }
    }
}
