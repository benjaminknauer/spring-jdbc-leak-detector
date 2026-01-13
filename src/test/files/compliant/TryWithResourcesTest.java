package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests that basic try-with-resources usage is NOT flagged.
 * EXPECTED: 0 issues
 */
class TryWithResourcesTest {
    private JdbcClient jdbcClient;

    void testCase() {
        try (Stream<User> users = jdbcClient.sql("SELECT * FROM users")
            .query(User.class)
            .stream()) {
            users.forEach(user -> System.out.println(user.name));
        }
    }
}
