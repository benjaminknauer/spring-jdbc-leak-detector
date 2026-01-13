package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of stream assigned outside try-with-resources scope.
 * EXPECTED: 1 issue on the line with .stream()
 */
class VariableOutsideTryTest {
    private JdbcClient jdbcClient;

    void testCase() {
        Stream<User> users;
        users = jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream();
        users.forEach(System.out::println);
    }
}
