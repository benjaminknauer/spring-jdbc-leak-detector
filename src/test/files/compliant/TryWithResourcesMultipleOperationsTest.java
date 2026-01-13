package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests that try-with-resources with complex stream operations is NOT flagged.
 * EXPECTED: 0 issues
 */
class TryWithResourcesMultipleOperationsTest {
    private JdbcClient jdbcClient;

    void testCase() {
        try (Stream<User> users = jdbcClient.sql("SELECT * FROM users WHERE age > ?")
            .param(18)
            .query(User.class)
            .stream()) {

            long count = users
                .filter(user -> user.name.startsWith("A"))
                .peek(user -> System.out.println("Processing: " + user.name))
                .count();

            System.out.println("Total: " + count);
        }
    }
}
